package com.example.basic.ui.imageDetails

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.basic.R
import com.example.basic.databinding.FragmentHomeBinding
import com.example.basic.databinding.FragmentImageDetailsBinding
import kotlinx.coroutines.flow.collectLatest

class ImageDetailsFragment : Fragment() {

    private lateinit var viewModel: ImageDetailsViewModel

    private var _binding: FragmentImageDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    private val arguments by navArgs<ImageDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageDetailsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[ImageDetailsViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getSpecificImageDetails(arguments.imageID)
        val size = arguments.imageSize
        val fileName = arguments.imageFileName
        activity?.findViewById<ImageView>(R.id.filter_main)?.setOnClickListener {
            val action = ImageDetailsFragmentDirections.actionImageDetailsFragmentToImageDetailsDialog(size, fileName)
            findNavController().navigate(action)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.imageDetails.collectLatest {
                    if (it.imageUri.isNotEmpty()) {
                        Glide.with(this@ImageDetailsFragment).load(it.imageUri.toUri()).into(binding.imageDetails)
                        Log.d("ImageDetails", it.toString())
                    }
                }
            }
        }
    }
}