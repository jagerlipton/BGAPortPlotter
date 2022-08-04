package com.jagerlipton.bgaportplotter.presentation.ui.plotter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jagerlipton.bgaportplotter.databinding.LastMessageItemBinding
import com.jagerlipton.bgaportplotter.presentation.Colors


class LastMessageRecyclerAdapter(private val rows: List<Float>) :
    RecyclerView.Adapter<LastMessageRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: LastMessageItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LastMessageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = rows.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(rows[position]) {
                val pos: Int = position + 1
                binding.counterTextView.text = pos.toString()
                binding.lastMessageTextView.text = this.toString()
                binding.counterTextView.setBackgroundColor(
                    Colors.getColorWFromRes(
                        position,
                        binding.lastMessageTextView.context
                    )
                )
            }
        }
    }
}























