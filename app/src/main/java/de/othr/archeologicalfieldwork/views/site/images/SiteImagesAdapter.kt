package de.othr.archeologicalfieldwork.views.site.images

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.othr.archeologicalfieldwork.R
import de.othr.archeologicalfieldwork.helper.readImageFromPath
import kotlinx.android.synthetic.main.card_image.view.*

class SiteImagesAdapter constructor(
        private var images: List<String>,
        private val listener: SiteImagesListener
    ) : RecyclerView.Adapter<SiteImagesAdapter.ImagesMainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesMainHolder {
        return ImagesMainHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.card_image,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ImagesMainHolder, position: Int) {
        val site = images[holder.adapterPosition]
        holder.bind(site, listener)
    }

    override fun getItemCount(): Int = images.size

    class ImagesMainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(imgSrc: String, listener: SiteImagesListener) {
            if (imgSrc.startsWith("content://")) {
                itemView.siteDetailImage.setImageBitmap(readImageFromPath(itemView.context, imgSrc))
            } else {
                Glide.with(itemView).load(imgSrc).into(itemView.siteDetailImage);
            }

            itemView.setOnClickListener { listener.onImageClick(imgSrc) }
        }
    }
}