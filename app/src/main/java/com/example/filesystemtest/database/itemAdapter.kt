package com.example.filesystemtest.database

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter


class itemAdapter(data: OrderedRealmCollection<item>):
    RealmRecyclerViewAdapter<item, itemAdapter.ViewHolder>(data,true) {

    init{
        setHasStableIds(true)
    }

    class ViewHolder(cell: View): RecyclerView.ViewHolder(cell) {
        val date: TextView = cell.findViewById(android.R.id.text1)
        val title:TextView = cell.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): itemAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(android.R.layout.simple_expandable_list_item_2,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: itemAdapter.ViewHolder, position: Int) {
        val item: item? = getItem(position)
        holder.date.text = ""
        holder.title.text = item?.name
    }
    override fun getItemId(position: Int): Long {
        return getItem(position)?.id ?: 0
    }
}
