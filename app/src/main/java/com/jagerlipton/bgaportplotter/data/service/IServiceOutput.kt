package com.jagerlipton.bgaportplotter.data.service

import com.jagerlipton.bgaportplotter.data.service.model.PlotterMessage
import com.jagerlipton.bgaportplotter.presentation.ui.connection.model.Device
import kotlinx.coroutines.flow.StateFlow

interface IServiceOutput {
    val lastMessageFlow: StateFlow<PlotterMessage>
    val portStateFlow: StateFlow<Boolean>
    val broadcastFlow: StateFlow<String>
    val deviceListFlow: StateFlow<List<Device>>

    fun addMessage(inputMessage: PlotterMessage)
    fun getPlotterList(): List<PlotterMessage>
    fun clearPlotterList()
    fun setDeviceList(devList: List<Device>)
    fun setBroadcast(broadcast: String)
    fun setPortState(portState: Boolean)
}