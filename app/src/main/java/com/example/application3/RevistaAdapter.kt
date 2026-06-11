package com.example.application3

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RevistaAdapter(private var revistas: List<Revista>) :
    RecyclerView.Adapter<RevistaAdapter.RevistaViewHolder>() {

    class RevistaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPortada: ImageView = view.findViewById(R.id.ivPortada)
        val tvTitulo: TextView = view.findViewById(R.id.tvTitulo)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvPdfUrl: TextView = view.findViewById(R.id.tvPdfUrl)
        val llPdf: View = view.findViewById(R.id.llPdf) // Get the container
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RevistaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_revista, parent, false)
        return RevistaViewHolder(view)
    }

    override fun onBindViewHolder(holder: RevistaViewHolder, position: Int) {
        val revista = revistas[position]
        holder.tvTitulo.text = "Revista Mensual UTEQ"
        holder.tvFecha.text = "Año: ${revista.anio} Mes ${revista.mes}"
        holder.tvPdfUrl.text = revista.pdf

        // Add click listener to the PDF section
        holder.llPdf.setOnClickListener {
            revista.pdf?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                holder.itemView.context.startActivity(intent)
            }
        }

        val fullImageUrl = "https://uteq.edu.ec/assets/images/newspapers/${revista.urlportada}"
        
        Glide.with(holder.itemView.context)
            .load(fullImageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(holder.ivPortada)
    }

    override fun getItemCount() = revistas.size

    fun updateData(newRevistas: List<Revista>) {
        revistas = newRevistas
        notifyDataSetChanged()
    }
}