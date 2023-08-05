package com.example.mvppractice.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mvppractice.R
import com.example.mvppractice.view.models.TopHeadlinesUiModel

class TopHeadlinesAdapter() :
    ListAdapter<TopHeadlinesUiModel, TopHeadlinesAdapter.TopHeadlinesViewHolder>(
        ItemDiffUtil()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopHeadlinesViewHolder {
        val root =
            LayoutInflater.from(parent.context).inflate(R.layout.item_top_headlines, parent, false)
        return TopHeadlinesViewHolder(root)
    }

    override fun onBindViewHolder(holder: TopHeadlinesViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class TopHeadlinesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText = itemView.findViewById<TextView>(R.id.tv_title)
        private val descriptionText = itemView.findViewById<TextView>(R.id.tv_description)
        private val newsImage = itemView.findViewById<ImageView>(R.id.iv_headline_image)

        fun bind(item: TopHeadlinesUiModel) {
            titleText.text = item.title
            descriptionText.text = item.description
            Glide.with(itemView)
                .load(item.imageUrl)
                .centerCrop()
                .into(newsImage)
        }
    }

    class ItemDiffUtil : DiffUtil.ItemCallback<TopHeadlinesUiModel>() {
        override fun areItemsTheSame(
            oldItem: TopHeadlinesUiModel,
            newItem: TopHeadlinesUiModel
        ): Boolean = oldItem.title == newItem.title

        override fun areContentsTheSame(
            oldItem: TopHeadlinesUiModel,
            newItem: TopHeadlinesUiModel
        ): Boolean = oldItem == newItem

    }
}