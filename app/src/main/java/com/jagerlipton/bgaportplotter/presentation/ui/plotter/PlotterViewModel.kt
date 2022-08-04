package com.jagerlipton.bgaportplotter.presentation.ui.plotter

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.jagerlipton.bgaportplotter.data.service.IServiceOutput
import com.jagerlipton.bgaportplotter.data.service.model.PlotterMessage

class PlotterViewModel(private val serviceOutput: IServiceOutput) : ViewModel() {
    val portStateFlow: LiveData<Boolean> = serviceOutput.portStateFlow.asLiveData()
    val lastMessageFlow: LiveData<PlotterMessage> = serviceOutput.lastMessageFlow.asLiveData()

    fun getPlotterList(): List<PlotterMessage> {
       return serviceOutput.getPlotterList()
    }

    fun clearPlotterList(){
        serviceOutput.clearPlotterList()
    }
}