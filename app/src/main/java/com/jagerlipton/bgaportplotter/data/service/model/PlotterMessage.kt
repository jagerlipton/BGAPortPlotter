package com.jagerlipton.bgaportplotter.data.service.model

class PlotterMessage(private val index: Int, val list: List<Float>) {

    override fun toString(): String {
        return "PlotterMessage(index=$index, list=$list)"
    }

}

