package com.example.basic.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.basic.ImageRVAdapter
import com.example.basic.ImageRecyclerView
import com.example.basic.R
import com.example.basic.databinding.FragmentNotificationsBinding
import com.example.basic.db.FilesEntity
import kotlinx.coroutines.flow.collectLatest

class NotificationsFragment : Fragment(), ImageRVAdapter {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var adapter: ImageRecyclerView

    private lateinit var notificationsViewModel: NotificationsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        notificationsViewModel =
            ViewModelProvider(this)[NotificationsViewModel::class.java]

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.starredRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = ImageRecyclerView(this)
        binding.starredRecyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                notificationsViewModel.allStarredFiles.collectLatest {
                    adapter.setData(it)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(filesEntity: FilesEntity) {

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
        notificationsViewModel.updateImageEntity(newFilesEntity)
    }

    override fun onDeleteClicked(filesEntity: FilesEntity) {
        notificationsViewModel.deleteImageEntity(filesEntity)
    }

    override fun onPdfClicked(filesEntity: FilesEntity) {

    }

    override fun onVideoClicked(filesEntity: FilesEntity) {

    }
}