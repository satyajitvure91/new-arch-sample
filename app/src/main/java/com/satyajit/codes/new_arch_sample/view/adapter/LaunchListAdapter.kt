package com.satyajit.codes.new_arch_sample.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.satyajit.codes.new_arch_sample.LaunchListQuery
import com.satyajit.codes.new_arch_sample.R
import com.satyajit.codes.new_arch_sample.databinding.LaunchItemBinding

class LaunchListAdapter() :
    ListAdapter<LaunchListQuery.Launch, ViewHolder>(LaunchDiffUtil()) {

    var onItemClicked: ((LaunchListQuery.Launch) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: LaunchItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.launch_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.launch = getItem(position)
        val launch = getItem(position)
        holder.binding.root.setOnClickListener {
            onItemClicked?.invoke(launch)
        }
    }

}

class ViewHolder(val binding: LaunchItemBinding) : RecyclerView.ViewHolder(binding.root)

class LaunchDiffUtil : DiffUtil.ItemCallback<LaunchListQuery.Launch>() {
    override fun areItemsTheSame(
        oldItem: LaunchListQuery.Launch,
        newItem: LaunchListQuery.Launch
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: LaunchListQuery.Launch,
        newItem: LaunchListQuery.Launch
    ): Boolean {
        return oldItem == newItem
    }

}

@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, url: String?) {
    imageView.load(url) { crossfade(true) }
}