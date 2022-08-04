package com.jagerlipton.bgaportplotter.data.service

import com.jagerlipton.bgaportplotter.data.service.model.PlotterMessage
import com.jagerlipton.bgaportplotter.presentation.ui.connection.model.Device
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ServiceOutput : IServiceOutput {

    private val _lastMessageFlow = MutableStateFlow(PlotterMessage(-1, listOf()))
    override val lastMessageFlow: StateFlow<PlotterMessage> = _lastMessageFlow

    private val _portStateFlow = MutableStateFlow(false)
    override val portStateFlow: StateFlow<Boolean> = _portStateFlow

    private val _broadcastFlow = MutableStateFlow("")
    override val broadcastFlow: StateFlow<String> = _broadcastFlow

    private val _deviceListFlow = MutableStateFlow<List<Device>>(listOf())
    override val deviceListFlow: StateFlow<List<Device>> = _deviceListFlow

    private val plotterData: MutableList<PlotterMessage> = mutableListOf()

    override fun addMessage(inputMessage: PlotterMessage) {
        val lastIndex = plotterData.size
        val newMessage = PlotterMessage(lastIndex, inputMessage.list)
        plotterData.add(newMessage)
        _lastMessageFlow.value = newMessage
    }

    override fun getPlotterList(): List<PlotterMessage> {
        return plotterData
    }

    override fun clearPlotterList() {
        plotterData.clear()
        val newMessage = PlotterMessage(0, listOf())
        _lastMessageFlow.value = newMessage
    }

    override fun setDeviceList(devList: List<Device>) {
        _deviceListFlow.value = devList
    }

    override fun setBroadcast(broadcast: String) {
        _broadcastFlow.value = broadcast
    }

    override fun setPortState(portState: Boolean) {
        _portStateFlow.value = portState
    }

}