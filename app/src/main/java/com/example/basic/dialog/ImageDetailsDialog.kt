package com.example.basic.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.example.basic.R
import com.example.basic.databinding.FragmentHomeBinding
import com.example.basic.databinding.FragmentImageDetailsBinding
import com.example.basic.databinding.FragmentImageDetailsDialogBinding
import com.example.basic.ui.imageDetails.ImageDetailsFragmentArgs

class ImageDetailsDialog : DialogFragment() {


    private val arguments by navArgs<ImageDetailsDialogArgs>()

    private var _binding: FragmentImageDetailsDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageDetailsDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageSize.text = getString(R.string.size, arguments.imageSize)
        binding.imageFileName.text = getString(R.string.file_name, arguments.imageFileName)

    }

}