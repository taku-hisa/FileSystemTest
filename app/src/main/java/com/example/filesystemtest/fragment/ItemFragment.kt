package com.example.filesystemtest.fragment

import android.R
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.filesystemtest.database.item
import com.example.filesystemtest.database.itemAdapter
import com.example.filesystemtest.databinding.FragmentItemBinding
import io.realm.Realm
import io.realm.kotlin.where
import java.io.*


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
        _binding = FragmentItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //選んだカテゴリの画像リストを表示する
        val category = args.category
        val items = realm.where<item>().equalTo("category", category).findAll()
        //画像の読込
        val imageList = mutableListOf<Bitmap>()
        for(i in items){
            val bufferedInputStream = BufferedInputStream(context?.openFileInput(i.name))
            val itemImage = BitmapFactory.decodeStream(bufferedInputStream)
            imageList.add(itemImage)
        }
        binding.RecyclerView.apply {
            layoutManager =
                when {
                    resources.configuration.orientation
                            == Configuration.ORIENTATION_PORTRAIT
                    ->GridLayoutManager(requireContext(),2)
                    else
                    ->GridLayoutManager(requireContext(),4)
                }
            adapter = itemAdapter(context,imageList)
        }

        //リサイクラービューの選択
        /*binding.RecyclerView.setOnClickListener {
            val item = (view.findViewById<TextView>(android.R.id.text2)).text.toString()
            val action = ItemFragmentDirections.actionItemFragmentToImageFragment(item)
            findNavController().navigate(action)
        }*/

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