package com.example.filesystemtest.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filesystemtest.database.item
import com.example.filesystemtest.database.itemAdapter
import com.example.filesystemtest.databinding.FragmentItemBinding
import io.realm.Realm
import io.realm.kotlin.where


class ItemFragment : Fragment() {
    private var _binding : FragmentItemBinding? = null
    private val binding get() = _binding!!
    private lateinit var realm: Realm
    private val args: ItemFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance() //realmのオープン処理
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //選んだカテゴリの画像リストを表示する
        val category = args.category
        binding.RecyclerView.layoutManager = LinearLayoutManager(context)
        val items = realm.where<item>().equalTo("category",category).findAll()
        val adapter = itemAdapter(items)
        binding.RecyclerView.adapter = adapter
        //画像を選ぶ
        binding.RecyclerView.setOnClickListener {
            val item = (view.findViewById<TextView>(android.R.id.text2)).text.toString()
            val action = ItemFragmentDirections.actionItemFragmentToImageFragment(item)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close() //realmのクローズ処理
    }
}