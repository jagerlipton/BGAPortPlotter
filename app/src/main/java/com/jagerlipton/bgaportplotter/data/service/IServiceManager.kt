package com.jagerlipton.bgaportplotter.data.service

import com.jagerlipton.bgaportplotter.data.service.model.ConnectionType

interface IServiceManager {
    fun startServ(connectionType: ConnectionType)
    fun stopServ(connectionType: ConnectionType)
}