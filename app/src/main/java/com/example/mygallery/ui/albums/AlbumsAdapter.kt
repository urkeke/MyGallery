package com.example.mygallery.ui.albums

import android.graphics.*
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.mygallery.R
import com.example.mygallery.data.model.unsplash.UCollection
import com.example.mygallery.databinding.ItemAlbumBinding

class AlbumsAdapter(
    private val albums: List<UCollection>,
    private val listener: AlbumClickListener
) : RecyclerView.Adapter<AlbumsAdapter.AlbumViewHolder>() {

    override fun getItemCount(): Int = albums.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder =
        AlbumViewHolder(
            ItemAlbumBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(albums[position], position)
    }


    inner class AlbumViewHolder(private val binding: ItemAlbumBinding) :
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

        fun bind(album: UCollection, position: Int) = with(itemView) {
            binding.itemAlbumName.text = album.title
            Glide.with(context)
                .load(album.coverPhoto.urls.thumb)
                .placeholder(progressDrawable)
                .into(binding.itemAlbumThumbnail)

            setOnClickListener {
                listener.albumClicked(album)
            }

            if (position == itemCount - 1)
                listener.lastAlbumReached()
        }
    }

    interface AlbumClickListener {
        fun albumClicked(album: UCollection)
        fun lastAlbumReached()
    }

}