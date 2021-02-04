package com.example.filesystemtest.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import com.example.filesystemtest.R
import com.example.filesystemtest.databinding.FragmentImageBinding
import com.example.filesystemtest.databinding.FragmentMainBinding
import java.io.BufferedInputStream

class ImageFragment : Fragment() {
    private var _binding : FragmentImageBinding? = null
    private val binding get() = _binding!!
    private val args: ImageFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImageBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val image = args.image
        binding.textView.text = image
    }

    override fun onDestroy(){
        super.onDestroy()
        _binding = null
    }
}