package com.example.basic.ui.files

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.basic.EqualSpacingItemDecoration
import com.example.basic.R
import com.example.basic.Utils
import com.example.basic.databinding.FragmentFilesBinding
import com.example.basic.db.FilesEntity
import com.example.basic.ui.adapter.AdapterItemClickListener
import com.example.basic.ui.adapter.FilesAdapter
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.flow.collectLatest
import java.text.DecimalFormat

class FilesFragment : Fragment(), AdapterItemClickListener {

    private var _binding: FragmentFilesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    private lateinit var viewModel: FilesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[FilesViewModel::class.java]
        _binding = FragmentFilesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = FilesAdapter(this@FilesFragment)
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing) // Define the spacing dimension in resources
        binding.filesRecyclerView.addItemDecoration(EqualSpacingItemDecoration(spacingInPixels))
        binding.filesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.filesRecyclerView.adapter = adapter

        binding.getFiles.setOnClickListener {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
                selectImage()
            } else {
                dexterRequestPermission()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allFiles.collectLatest {
                    adapter.submitList(it)
                }
            }
        }
    }

    private fun dexterRequestPermission() {
        Dexter.withContext(activity)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_DOCUMENTS
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    selectImage()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
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

    private val requestFile = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val data = result.data
            data?.let {
                if (data.clipData != null) {
                    //If multiple images chosen
                    val count = data.clipData!!.itemCount
                    val imageList: ArrayList<FilesEntity> = ArrayList()
                    for (i in 0 until count) {
                        val imageUri = data.clipData!!.getItemAt(i).uri.toString()
                        requireContext().contentResolver.takePersistableUriPermission(data.clipData!!.getItemAt(i).uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        imageList.add(getFileNameAndSize(imageUri))
                    }
                    viewModel.insertImageList(imageList)

                } else {
                    //If single image chosen
                    val imageUri = data.data.toString()
                    data.data?.let { it1 ->
                        requireContext().contentResolver.takePersistableUriPermission(
                            it1, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    viewModel.insertSingleImage(getFileNameAndSize(imageUri))
                }
            }
        }
    }

    private fun getFileType(contentResolver: ContentResolver, uri: Uri) : String {
        val mimeType = contentResolver.getType(uri)
        if (mimeType?.startsWith("image/") == true) {
            return Utils.IMAGE_FILE
        }
        if (mimeType?.startsWith("video/") == true) {
            return Utils.VIDEO_FILE
        }
        if (mimeType?.startsWith("application/pdf") == true) {
            return Utils.DOCUMENT_FILE
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

    override fun onStarClicked(filesEntity: FilesEntity, holder: FilesAdapter.FilesViewHolder) {
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
        if (newFilesEntity.imageStarred == 0) {
            holder.binding.starBtn.setImageResource(R.drawable.baseline_star_border_24)
        } else {
            holder.binding.starBtn.setImageResource(R.drawable.baseline_star_24)
        }
        viewModel.updateImageEntity(newFilesEntity)
    }

    override fun onDeleteClicked(filesEntity: FilesEntity) {
        viewModel.deleteImageEntity(filesEntity)
    }

    override fun onDocumentClicked(filesEntity: FilesEntity) {
        val action = FilesFragmentDirections.actionNavigationDetailsToFileDetailsFragment(filesEntity.id)
        findNavController().navigate(action)
    }

    override fun onVideoClicked(filesEntity: FilesEntity) {
        val action = FilesFragmentDirections.actionNavigationDetailsToFileDetailsFragment(filesEntity.id)
        findNavController().navigate(action)
    }

    override fun onImageClicked(filesEntity: FilesEntity) {
        val action = FilesFragmentDirections.actionNavigationDetailsToFileDetailsFragment(filesEntity.id)
        findNavController().navigate(action)
    }

}