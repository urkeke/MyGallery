package com.example.mygallery.ui.photos

import android.graphics.*
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.mygallery.R
import com.example.mygallery.data.model.unsplash.UPhoto
import com.example.mygallery.databinding.ItemPhotoBinding

class PhotosAdapter(
    private val photos: List<UPhoto>,
    private val listener: PhotoClickListener
) : RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder>() {

    override fun getItemCount(): Int = photos.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder =
        PhotoViewHolder(
            ItemPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photos[position], position)
    }


    inner class PhotoViewHolder(private val binding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val progressDrawable by lazy {
            CircularProgressDrawable(binding.root.context).apply {
                strokeWidth = 5f
                centerRadius = 30f
                colorFilter = PorterDuffColorFilter(
                    ContextCompat.getColor(binding.root.context, R.color.red),
                    PorterDuff.Mode.SRC_OVER
                )
                start()
            }
        }

        fun bind(photo: UPhoto, position: Int) = with(itemView) {
            Glide.with(context)
                .load(photo.urls.thumb)
                .placeholder(progressDrawable)
                .into(binding.itemPhotoIV)

            setOnClickListener {
                listener.photoClicked(photo)
            }

            if (position == itemCount -1){
                listener.lastPhotoReached()
            }
        }
    }

    interface PhotoClickListener {
        fun photoClicked(photo: UPhoto)
        fun lastPhotoReached()
    }

}