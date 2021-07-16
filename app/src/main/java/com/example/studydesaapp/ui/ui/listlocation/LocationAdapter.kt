package com.example.studydesaapp.ui.ui.listlocation

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.studydesaapp.databinding.ItemLocationBinding
import com.example.studydesaapp.ui.entity.LocationEntity
import com.example.studydesaapp.ui.ui.detail.DetailLocationActivity

class LocationAdapter : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>(){

    companion object {
        const val EXTRA_LOCATION = "EXTRA_LOCATION"
    }

    private var listLocation = ArrayList<LocationEntity>()

    fun setLocation(location: List<LocationEntity>?){
        if(location == null) return
        this.listLocation.clear()
        this.listLocation.addAll(location)
    }

    class LocationViewHolder(private val binding: ItemLocationBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(location: LocationEntity) {
            with(binding){
                tvItemTitle.text = location.name
                tvFacultyName.text = location.faculty
                Glide.with(itemView.context)
                    .load(location.photo)
                    .into(imgPoster)
                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailLocationActivity::class.java)
                    intent.putExtra(EXTRA_LOCATION, location)
                    itemView.context.startActivity(intent)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val itemLocationBinding = ItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocationViewHolder(itemLocationBinding)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = listLocation[position]
        holder.bind(location)
    }

    override fun getItemCount(): Int {
        return listLocation.size
    }
}