package com.example.filesystemtest.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.filesystemtest.R
import com.example.filesystemtest.database.item
import com.example.filesystemtest.databinding.FragmentDetailBinding
import com.example.filesystemtest.databinding.FragmentMainBinding
import io.realm.Realm
import io.realm.kotlin.where
import java.io.BufferedInputStream

class DetailFragment : Fragment() {
    private var _binding : FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //選んだカテゴリの画像リストを表示する
        val name = args.name
        val bufferedInputStream = BufferedInputStream(context?.openFileInput(name))
        val image = BitmapFactory.decodeStream(bufferedInputStream)
        binding.imageView.setImageBitmap(image)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}