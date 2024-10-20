package com.blinkitcloneadmin.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.blinkitcloneadmin.databinding.ItemViewImageSelectionBinding

class AdapterSelectedImage(
    val imageUri: ArrayList<Uri>
) : RecyclerView.Adapter<AdapterSelectedImage.SelectedImageViewHolder>() {

    class SelectedImageViewHolder(
        val binding: ItemViewImageSelectionBinding
    ) : ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedImageViewHolder {
        return SelectedImageViewHolder(ItemViewImageSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return imageUri.size
    }

    override fun onBindViewHolder(holder: SelectedImageViewHolder, position: Int) {
        val image = imageUri[position]
        holder.binding.apply {
            ivIMage.setImageURI(image)
        }

        holder.binding.closeButton.setOnClickListener {
            if (position < imageUri.size) {
                imageUri.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }
}