package com.example.basic.ui.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.basic.ImageRecyclerView
import com.example.basic.R
import com.example.basic.databinding.FragmentHomeBinding
import com.example.basic.db.ImageEntity
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.coroutines.flow.collectLatest
import java.io.File


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var adapter: ImageRecyclerView

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.getImages.setOnClickListener {

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
                selectImage()
            } else {
                dexterRequestPermission()
            }
        }

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        adapter = ImageRecyclerView()
        binding.recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.allImages.collectLatest {
                    Log.d("Database List", it.toString())
                    adapter.setData(it)
                    adapter.notifyDataSetChanged()
                }
            }
        }

    }

    private val requestFile = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
            val data = it.data
            Log.d("Get_selected_data", data.toString())
            data?.let {
                if (data.clipData != null) {
                    //If multiple images chosen
                    val count = data.clipData!!.itemCount
                    val imageList: ArrayList<ImageEntity> = ArrayList()
                    for (i in 0 until count) {
                        val imageUri = data.clipData!!.getItemAt(i).uri.toString()
                        imageList.add(ImageEntity(imageUri))
                    }
                    homeViewModel.insertImageList(imageList)

                } else {
                    //If single image chosen
                    val imageUri = data.data.toString()
                    homeViewModel.insertSingleImage(ImageEntity(imageUri))
                }
            }
        }
    }

    private fun selectImage() {
        Intent().apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            action = Intent.ACTION_GET_CONTENT
            requestFile.launch(this)
        }
    }


    private fun dexterRequestPermission() {
        Dexter.withContext(activity)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.permission_granted),
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.permission_required),
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }

            })
            .check()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}