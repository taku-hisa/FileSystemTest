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
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
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
        val imageList = mutableListOf<Bitmap>() //bitmap型リストのインスタンスを作成します
        var bufferedInputStream : BufferedInputStream;
        for(i in items){
            bufferedInputStream = BufferedInputStream(context?.openFileInput(i.name)) //"honyarara.jpg"という名前からbufferdInputStreamを取得します
            val itemImage = BitmapFactory.decodeStream(bufferedInputStream) //bitmap型で取得します
            bufferedInputStream.close()
            imageList.add(itemImage) //bitmap型のリストへ追加します
        }
        //アルゴリズムの修正が必要
        binding.RecyclerView.apply {
            layoutManager =
                when {
                    resources.configuration.orientation
                            == Configuration.ORIENTATION_PORTRAIT
                    -> GridLayoutManager(requireContext(), 2)
                    else
                    -> GridLayoutManager(requireContext(), 4)
                }
            adapter = itemAdapter(context, imageList).apply{ //adapterへlist<bitmap>を送ります
                //画面遷移
                setOnItemClickListener { position:Int ->
                    val action = items[position]?.let { ItemFragmentDirections.actionItemFragmentToDetailFragment( it.name) }
                    if (action != null) { findNavController().navigate(action) }
                }
            }
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