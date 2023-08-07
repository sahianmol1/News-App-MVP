package com.example.mvppractice.view.selectcountry

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.mvppractice.R
import com.example.mvppractice.view.models.CountriesUIModel

class CountriesAdapter(
    val onCountrySelected: (String) -> Unit
): ListAdapter<CountriesUIModel, CountriesAdapter.CountriesViewHolder>(CountryDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountriesViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.item_country, parent, false)
        return CountriesViewHolder(root)
    }

    override fun onBindViewHolder(holder: CountriesViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class CountriesViewHolder(itemView: View): ViewHolder(itemView) {
        val tvCountry = itemView.findViewById<TextView>(R.id.tv_country)
        fun bind(item: CountriesUIModel) {
            tvCountry.text = item.countryName

            tvCountry.setOnClickListener {
                onCountrySelected(item.countryCode)
            }
        }
    }

    class CountryDiffUtil: DiffUtil.ItemCallback<CountriesUIModel>() {
        override fun areItemsTheSame(
            oldItem: CountriesUIModel,
            newItem: CountriesUIModel
        ): Boolean = oldItem.countryCode == newItem.countryCode

        override fun areContentsTheSame(
            oldItem: CountriesUIModel,
            newItem: CountriesUIModel
        ): Boolean = oldItem == newItem

    }
}