package com.jagerlipton.bgaportplotter.presentation.ui.connection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jagerlipton.bgaportplotter.databinding.DeviceItemBinding
import com.jagerlipton.bgaportplotter.presentation.ui.connection.model.Device

class DeviceRecyclerAdapter(private val rows: List<Device>) :
    RecyclerView.Adapter<DeviceRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: DeviceItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DeviceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = rows.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(rows[position]) {
                binding.headerTextView.text = this.header
                binding.subHeaderTextView.text = this.subHeader
                binding.commentTextView.text = this.comment
            }
        }
    }

}