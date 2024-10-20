package com.blinkitcloneadmin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blinkitcloneadmin.databinding.ItemViewProductCategoriesBinding
import com.blinkitcloneadmin.models.Category

class CategoriesAdapter(
    private val categoryArrayList: ArrayList<Category>,
    val onCategoryClicked : (Category) -> Unit
) : RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>() {

    class CategoriesViewHolder(
        val bindng: ItemViewProductCategoriesBinding
    ) : RecyclerView.ViewHolder(bindng.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        return CategoriesViewHolder(
            ItemViewProductCategoriesBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return categoryArrayList.size
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val category = categoryArrayList[position]
        holder.bindng.apply {
            ivCategoryImage.setImageResource(category.icon)
            tvCategoryTitle.text = category.category
        }
        holder.itemView.setOnClickListener {
            onCategoryClicked(category)
        }
    }
}