package com.jagerlipton.bgaportplotter.presentation.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jagerlipton.bgaportplotter.presentation.ui.connection.ConnectionFragment
import com.jagerlipton.bgaportplotter.presentation.ui.plotter.PlotterFragment

class ViewPagerFragmentStateAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ConnectionFragment()
            else -> PlotterFragment()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}