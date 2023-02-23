package com.example.basic.ui.filedetails

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.basic.R
import com.example.basic.Utils.DOCUMENT_FILE
import com.example.basic.Utils.IMAGE_FILE
import com.example.basic.Utils.VIDEO_FILE
import com.example.basic.databinding.FragmentFileDetailsBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.collectLatest

class FileDetailsFragment : Fragment() {
    private var _binding: FragmentFileDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: FileDetailsViewModel

//    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var player: ExoPlayer

    private val arguments by navArgs<FileDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[FileDetailsViewModel::class.java]

        player = ExoPlayer.Builder(requireContext()).build()

        _binding = FragmentFileDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        if (bottomNavigationView != null) {
            bottomNavigationView.visibility = View.GONE
        }
        viewModel.getSpecificFileDetails(arguments.fileID)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fileDetails.collectLatest {
                    activity?.findViewById<ImageView>(R.id.filter_main)?.setOnClickListener { _ ->
                        val action =
                            FileDetailsFragmentDirections.actionFileDetailsFragmentToImageDetailsDialog(
                                it.imageSize,
                                it.imageFileName
                            )
                        findNavController().navigate(action)
                    }
                    if (it.fileType == IMAGE_FILE) {
                        binding.imageFile.visibility = View.VISIBLE
                        binding.videoFile.visibility = View.GONE
                        binding.documentFile.visibility = View.GONE
                        Glide.with(this@FileDetailsFragment).load(it.imageUri.toUri())
                            .into(binding.imageFile)
                        return@collectLatest
                    }
                    if (it.fileType == VIDEO_FILE) {
//                        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        activity?.window?.statusBarColor = Color.argb(128, 0, 0, 0)
                        (activity as AppCompatActivity).findViewById<androidx.coordinatorlayout.widget.CoordinatorLayout>(
                            R.id.appBarMain
                        ).visibility = View.GONE
                        binding.imageFile.visibility = View.GONE
                        binding.videoFile.visibility = View.VISIBLE
                        binding.documentFile.visibility = View.GONE
                        initializePlayer(it.imageUri)
                        return@collectLatest
                    }
                    if (it.fileType == DOCUMENT_FILE) {
                        binding.imageFile.visibility = View.GONE
                        binding.videoFile.visibility = View.GONE
                        binding.documentFile.visibility = View.VISIBLE
                        binding.documentFile.fromUri(it.imageUri.toUri()).load()
                        return@collectLatest
                    }
                }
            }
        }
    }

    private fun initializePlayer(uri: String) {
        binding.videoFile.player = player
        val mediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        // Show the bottom navigation bar
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)

        bottomNavigationView.visibility = View.VISIBLE

        (activity as AppCompatActivity).findViewById<androidx.coordinatorlayout.widget.CoordinatorLayout>(
            R.id.appBarMain
        ).visibility = View.VISIBLE


        player.release()
    }

}