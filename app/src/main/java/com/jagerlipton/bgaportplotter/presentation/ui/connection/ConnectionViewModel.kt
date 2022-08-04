package com.jagerlipton.bgaportplotter.presentation.ui.connection

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.jagerlipton.bgaportplotter.data.repository.IRepository
import com.jagerlipton.bgaportplotter.data.service.IServiceManager
import com.jagerlipton.bgaportplotter.data.service.IServiceOutput
import com.jagerlipton.bgaportplotter.data.service.model.ConnectionType
import com.jagerlipton.bgaportplotter.presentation.SingleLiveEvent
import com.jagerlipton.bgaportplotter.presentation.ui.connection.model.Device

class ConnectionViewModel(
    private val serviceManager: IServiceManager,
    private val repository: IRepository,
    serviceOutput: IServiceOutput
) : ViewModel() {

    val portStateFlow: LiveData<Boolean> = serviceOutput.portStateFlow.asLiveData()
    val flowCast: LiveData<String> = serviceOutput.broadcastFlow.asLiveData()
    val devList: LiveData<List<Device>> = serviceOutput.deviceListFlow.asLiveData()

    private val _liveCast = SingleLiveEvent<String>()
    val liveCast: SingleLiveEvent<String> = _liveCast

    fun parseFlowCast(flowCast: String) {
        if (flowCast != liveCast.value) _liveCast.value = flowCast
    }

    fun parseDeviceList(
        flowList: List<Device>,
        adapterList: MutableList<Device>,
        adapter: DeviceRecyclerAdapter
    ) {
        if (flowList.isNotEmpty()) {
            adapterList.clear()
            adapterList.addAll(flowList)
            adapter.notifyDataSetChanged()
        } else {
            clearDeviceList(adapterList,adapter)
        }
    }

    fun clearDeviceList(adapterList: MutableList<Device>, adapter: DeviceRecyclerAdapter) {
        adapterList.clear()
        adapter.notifyDataSetChanged()
    }

    fun getSpinnerIndex(): Int {
        return repository.loadBaudrateIndex()
    }

    fun setSpinnerIndex(index: Int) {
        repository.saveBaudrateIndex(index)
    }

    fun connect(type: ConnectionType) {
        serviceManager.startServ(type)
    }

    fun disconnect(type: ConnectionType) {
        serviceManager.stopServ(type)
    }
}