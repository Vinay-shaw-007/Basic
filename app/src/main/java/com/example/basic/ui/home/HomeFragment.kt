package com.example.basic.ui.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.basic.R
import com.example.basic.databinding.FragmentHomeBinding
import java.io.File


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var imageUri: Uri

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageUri = createImageUri()
        binding.getImages.setOnClickListener {
            requestPermission()
        }
    }

    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()){
        binding.imageView.setImageURI(null)
        binding.imageView.setImageURI(imageUri)
    }

    private fun createImageUri(): Uri {
        val image = File(requireActivity().filesDir, "camera_photos")
        return FileProvider.getUriForFile(
            requireActivity(),
            "com.example.basic.fileProvider",
            image
        )
    }

    private fun requestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.permission_granted),
                    Toast.LENGTH_LONG
                ).show()
                contract.launch(imageUri)
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.CAMERA
            ) -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.permission_required),
                    Toast.LENGTH_LONG
                ).show()
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )

            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
                contract.launch(imageUri)
            } else {
                Log.i("Permission: ", "Denied")
            }
        }


//    private fun selectImageFromGallery() {
//        if (ContextCompat.checkSelfPermission(
//                requireActivity(),
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                requireActivity(),
//                arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE),
//                101
//            )
//        } else {
//            selectImage()
//        }
//        selectImage()
//    }

//    private fun selectImage() {
//        Intent().apply {
//            type = "image/*"
//            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//            requestFile.launch(this)
//        }
//    }

//    private val requestFile = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) {
//        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
//            val data = it.data
//            Log.d("Data", data.toString())
//        }
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}