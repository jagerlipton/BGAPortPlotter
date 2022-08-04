package com.jagerlipton.bgaportplotter.presentation.ui.connection.model

import com.jagerlipton.bgaportplotter.data.service.model.ConnectionType

class Device(val type: ConnectionType, var header: String, var subHeader: String, var comment:String) {
}