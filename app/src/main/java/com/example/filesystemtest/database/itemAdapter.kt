package com.example.filesystemtest.database

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.filesystemtest.R

class itemAdapter(
    private val context: Context,
    private val images : List<Bitmap> //引数として受け取ったリスト
):RecyclerView.Adapter<itemAdapter.ViewHolder>(){

    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image) //表示したい部品
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): itemAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_layout,parent,false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: itemAdapter.ViewHolder, position: Int) {
        var imageResource = images[position] //リストを展開
        holder.image.setImageBitmap(imageResource)
    }

    override fun getItemCount(): Int = images.size

}
