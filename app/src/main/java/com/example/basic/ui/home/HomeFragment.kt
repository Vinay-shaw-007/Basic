package com.example.basic.ui.home

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.basic.ImageRVAdapter
import com.example.basic.ImageRecyclerView
import com.example.basic.R
import com.example.basic.Utils.DOCUMENT_FILE
import com.example.basic.Utils.IMAGE_FILE
import com.example.basic.Utils.VIDEO_FILE
import com.example.basic.databinding.FragmentHomeBinding
import com.example.basic.db.FilesEntity
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.coroutines.flow.collectLatest
import java.text.DecimalFormat


class HomeFragment : Fragment(), ImageRVAdapter {

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

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = ImageRecyclerView(this)
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
            data?.let {
                if (data.clipData != null) {
                    //If multiple images chosen
                    val count = data.clipData!!.itemCount
                    val imageList: ArrayList<FilesEntity> = ArrayList()
                    for (i in 0 until count) {
                        val imageUri = data.clipData!!.getItemAt(i).uri.toString()

                        imageList.add(getFileNameAndSize(imageUri))
                    }
                    homeViewModel.insertImageList(imageList)

                } else {
                    //If single image chosen
                    val imageUri = data.data.toString()
                    homeViewModel.insertSingleImage(getFileNameAndSize(imageUri))
                }
            }
        }
    }

    private fun getFileType(contentResolver: ContentResolver, uri: Uri) : String {
        val mimeType = contentResolver.getType(uri)
        if (mimeType?.startsWith("image/") == true) {
            return IMAGE_FILE
        }
        if (mimeType?.startsWith("video/") == true) {
            return VIDEO_FILE
        }
        if (mimeType?.startsWith("application/pdf") == true) {
            return DOCUMENT_FILE
        }
        return ""
    }

    private fun getFileNameAndSize(imageUri: String): FilesEntity {
        val cursor =
            context?.contentResolver?.query(imageUri.toUri(), null, null, null, null)
        cursor?.use {
            if (cursor.moveToFirst()) {
                //Get the file name
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val fileName = cursor.getString(nameIndex)

                //Get the file type
                val fileType = context?.contentResolver?.let { it1 -> getFileType(it1,imageUri.toUri()) }

                //Get the size of the file
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                val size = cursor.getLong(sizeIndex)
                val sizeInMb = size.toDouble() / (1024.0 * 1024.0)
                val decimalFormat = DecimalFormat("#.###")
                val sizeInMbFormatted = decimalFormat.format(sizeInMb)
                Log.d("Video Details", fileName.toString())
                Log.d("Video Details", sizeInMbFormatted.toString())
                Log.d("Video Details", fileType!!)
                return FilesEntity(imageUri, fileName, sizeInMbFormatted, 0, fileType)
            }
        }
        cursor?.close()
        return FilesEntity(imageUri, "", "", 0,"")
    }

    private fun selectImage() {
        Intent().apply {
            action = Intent.ACTION_OPEN_DOCUMENT
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*", "application/pdf"))
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            flags = Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            requestFile.launch(this)
        }
    }


    private fun dexterRequestPermission() {
        Dexter.withContext(activity)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    selectImage()
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

    override fun onItemClicked(filesEntity: FilesEntity) {
        val action = HomeFragmentDirections.actionNavigationHomeToImageDetailsFragment(
            filesEntity.id,
            filesEntity.imageSize,
            filesEntity.imageFileName
        )
        findNavController().navigate(action)
    }

    override fun onStarClicked(filesEntity: FilesEntity, holder: RecyclerView.ViewHolder) {
        val newFilesEntity: FilesEntity = if (filesEntity.imageStarred == 0) {
            FilesEntity(
                filesEntity.imageUri,
                filesEntity.imageFileName,
                filesEntity.imageSize,
                1,
                filesEntity.fileType,
                filesEntity.id
            )
        } else {
            FilesEntity(
                filesEntity.imageUri,
                filesEntity.imageFileName,
                filesEntity.imageSize,
                0,
                filesEntity.fileType,
                filesEntity.id
            )
        }
        when (holder) {
            is ImageRecyclerView.ImageViewHolder -> {
                if (newFilesEntity.imageStarred == 0) {
                    holder.starBtn.setImageResource(R.drawable.baseline_star_border_24)
                } else {
                    holder.starBtn.setImageResource(R.drawable.baseline_star_24)
                }
            }
            is ImageRecyclerView.VideoViewHolder -> {
                if (newFilesEntity.imageStarred == 0) {
                    holder.starBtn.setImageResource(R.drawable.baseline_star_border_24)
                } else {
                    holder.starBtn.setImageResource(R.drawable.baseline_star_24)
                }
            }
            is ImageRecyclerView.DocumentViewHolder -> {
                if (newFilesEntity.imageStarred == 0) {
                    holder.starBtn.setImageResource(R.drawable.baseline_star_border_24)
                } else {
                    holder.starBtn.setImageResource(R.drawable.baseline_star_24)
                }
            }
        }
        homeViewModel.updateImageEntity(newFilesEntity)
    }

    override fun onDeleteClicked(filesEntity: FilesEntity) {
        homeViewModel.deleteImageEntity(filesEntity)
    }

    override fun onPdfClicked(filesEntity: FilesEntity) {
//        val uri = filesEntity.imageUri.toUri()
//        val contentResolver = context?.contentResolver
//        val inputStream = contentResolver?.openInputStream(uri)
//        val file = File.createTempFile("pdf", ".pdf", context?.cacheDir)
//        val outputStream = FileOutputStream(file)
//        inputStream?.copyTo(outputStream)
//
//        val intent = Intent(Intent.ACTION_VIEW)
//        val uri1 = Uri.fromFile(file)
//        intent.setDataAndType(uri1, "application/pdf")
//        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_CLEAR_TOP
//
//        try {
//            startActivity(intent)
//        } catch (e: ActivityNotFoundException) {
//            // Handle the error
//            Log.d("OpenPDF", e.message.toString())
//        }
        findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToPDFFragment(filesEntity.id))
    }


    override fun onVideoClicked(filesEntity: FilesEntity) {
        val action = HomeFragmentDirections.actionNavigationHomeToVideoPlayerActivity(filesEntity.imageUri)
        findNavController().navigate(action)
    }


}