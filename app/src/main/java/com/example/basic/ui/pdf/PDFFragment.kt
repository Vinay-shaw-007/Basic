package com.example.basic.ui.pdf

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.basic.databinding.FragmentPDFBinding
import kotlinx.coroutines.flow.collectLatest
import java.io.File
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
        viewModel.getSpecificImageDetails(arguments.pdfID)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.imageDetails.collectLatest {
                    if (it.imageUri.isNotEmpty()) {
//                        setPDf(it.imageUri)
                    }
                }
            }
        }
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