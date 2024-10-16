package com.example.recetapp

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recetapp.databinding.PopularRvItemBinding

class PopularAdapter(var dataLists: ArrayList<Recipe>, var context: Context) : RecyclerView.Adapter<PopularAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: PopularRvItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PopularRvItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataLists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(dataLists.get(position).img).into(holder.binding.popularImg)
        holder.binding.popularTxt.text = dataLists.get(position).tittle
        var time= dataLists.get(position).ing.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        holder.binding.popularTime.text = time[0]

    }
}