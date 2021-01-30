package de.othr.archeologicalfieldwork.views.sitelist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.othr.archeologicalfieldwork.R
import de.othr.archeologicalfieldwork.helper.readImageFromPath
import de.othr.archeologicalfieldwork.model.Site
import kotlinx.android.synthetic.main.card_image.view.*
import kotlinx.android.synthetic.main.card_site.view.*

class SiteAdapter constructor(
        private var sites: List<Site>,
        private val listener: SiteListener
    ) : RecyclerView.Adapter<SiteAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.card_site,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val site = sites[holder.adapterPosition]
        holder.bind(site, listener)
    }

    override fun getItemCount(): Int = sites.size

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(site: Site, listener: SiteListener) {
            itemView.siteName.text = site.name
            itemView.siteDescription.text = site.description

            if (site.images.isNotEmpty()) {
                if (site.images.first().startsWith("content://")) {
                    itemView.siteDetailImage.setImageBitmap(readImageFromPath(itemView.context, site.images.first()))
                } else {
                    Glide.with(itemView).load(site.images.first()).into(itemView.siteImageIcon);
                }
            }

            itemView.setOnClickListener { listener.onSiteClick(site) }
        }
    }
}