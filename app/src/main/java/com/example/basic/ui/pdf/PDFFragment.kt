package com.example.basic.ui.pdf

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.basic.databinding.FragmentPDFBinding
import kotlinx.coroutines.flow.collectLatest
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class PDFFragment : Fragment() {

    private var _binding: FragmentPDFBinding? = null
    private val binding get() = _binding!!
    private val arguments by navArgs<PDFFragmentArgs>()

    private lateinit var viewModel: PDFViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[PDFViewModel::class.java]
        _binding = FragmentPDFBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getSpecificFileDetails(arguments.pdfID)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.imageDetails.collectLatest { it ->
                    if (it.imageUri.isNotEmpty()) {
                        binding.pdfView.fromUri(it.imageUri.toUri()).load()
//                        var filePath: String? = null
//                        val _uri: Uri = it.imageUri.toUri()
//                        Log.d("", "URI = $_uri")
//                        if ("content" == _uri.scheme) {
//                            val cursor: Cursor = requireContext().contentResolver.query(
//                                _uri,
//                                arrayOf<String>(MediaStore.Images.ImageColumns.DATA),
//                                null,
//                                null,
//                                null
//                            )
//                            cursor.moveToFirst()
//                            filePath = cursor.getString(0)
//                            cursor.close()
//                        } else {
//                            filePath = _uri.path
//                        }
//                        binding.pdfView.fromUri(it.imageUri.toUri()).load()
//                        context?.let {
//                        val file = File("${it.getExternalFilesDir(null)}/Documents/newNehal's CV.pdf")
//
//                            val uri = FileProvider.getUriForFile(
//                                it,
//                                "${it.packageName}.fileprovider",
//                                file
//                            )
//                            binding.pdfView.fromUri(uri).load()
//                        }

//                        if (file.exists()) {
//                            binding.pdfView.fromFile(file).load()
//                        }

                    }
                }
            }
        }
    }

    private fun getAbsolutePathFromUri(context: Context, uri: Uri): String? {
        var absolutePath: String? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10 and above, use ContentResolver#openFileDescriptor and ParcelFileDescriptor#getFileDescriptor to get the absolute file path
            val contentResolver = context.contentResolver
            val fileDescriptor = contentResolver.openFileDescriptor(uri, "r")?.fileDescriptor
            val inputStream = FileInputStream(fileDescriptor)
            val file = File(context.cacheDir, "temp.pdf")
            FileOutputStream(file).use { outputStream ->
                val buffer = ByteArray(1024)
                var read: Int = inputStream.read(buffer)
                while (read != -1) {
                    outputStream.write(buffer, 0, read)
                    read = inputStream.read(buffer)
                }
                outputStream.flush()
            }
            absolutePath = file.absolutePath
        } else {
            // For Android 9 and below, use ContentResolver#query to get the absolute file path
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(uri, projection, null, null, null)
            cursor?.let {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    absolutePath = it.getString(columnIndex)
                }
                it.close()
            }
        }

        return absolutePath
    }

    private fun setPDf(uri: String) {

// Get a content resolver instance
        val contentResolver = context?.contentResolver

// Get the file name and extension from the URI
        val fileName = "my_file.pdf"
        val fileExtension = ".pdf"

// Create an output file in your app's private storage
        val outputDir = context?.filesDir
        val outputFile = File(outputDir, fileName + fileExtension)

// Open an input stream from the source URI
        contentResolver?.openInputStream(Uri.parse(uri))?.use { inputStream ->
            // Open an output stream to the output file
            FileOutputStream(outputFile).use { outputStream ->
                // Copy the input stream to the output stream
                inputStream.copyTo(outputStream)
            }
        }

// Load the PDF file from the output file path
        binding.pdfView.fromFile(outputFile).load()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}