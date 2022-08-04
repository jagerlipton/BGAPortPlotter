package com.jagerlipton.bgaportplotter.data.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Messenger
import com.jagerlipton.bgaportplotter.data.service.model.ConnectionType
import com.jagerlipton.bgaportplotter.data.service.model.PlotterMessage
import com.jagerlipton.bgaportplotter.data.storage.IStorage
import com.jagerlipton.bgaportplotter.data.storage.utils.Baudrate
import com.jagerlipton.bgaportplotter.presentation.ui.connection.model.Device


class ServiceManager(
    val context: Context,
    val storage: IStorage,
    val serviceOutput: IServiceOutput
) : IServiceManager {
    private var usbService: UsbService? = null
    private var isBounded = false

    override fun startServ(connectionType: ConnectionType) {
        when (connectionType) {
            ConnectionType.USB -> startUSBService(UsbService::class.java, usbConnection)
        }
    }

    override fun stopServ(connectionType: ConnectionType) {
        when (connectionType) {
            ConnectionType.USB -> {
                if (isBounded) {
                    context.unbindService(usbConnection);
                    isBounded = false;
                }
            }
        }
    }

    var mainHandler: Handler = Handler(Looper.getMainLooper()) { message ->

        when (message.arg1) {
            UsbService.MESSAGE_FROM_SERIAL_PORT -> {
                val newPlotterMessage: PlotterMessage = message.obj as PlotterMessage
                serviceOutput.addMessage(newPlotterMessage)
            }
            UsbService.MESSAGE_FROM_SERVICE_DEVICELIST -> {
                val devList: List<Device> = message.obj as List<Device>
                serviceOutput.setDeviceList(devList)
            }
            UsbService.MESSAGE_FROM_SERVICE_BROADCAST -> {
                val broadcast: String = message.obj as String
                serviceOutput.setBroadcast(broadcast)
            }
            UsbService.MESSAGE_FROM_SERVICE_PORT_STATE -> {
                val portState: Boolean = message.obj as Boolean
                serviceOutput.setPortState(portState)
            }
        }
        true
    }

    private val usbConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(arg0: ComponentName, arg1: IBinder) {
            usbService = (arg1 as UsbService.UsbBinder).service
            isBounded = true
            with(usbService) {
                this?.setMessageHandler(Messenger(mainHandler))
                this?.setBaudrate(Baudrate.getBaudrateByIndex(storage.loadBaudrateIndex()))
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            usbService = null
            isBounded = false
        }
    }

    private fun startUSBService(service: Class<*>, serviceConnection: ServiceConnection) {
        if (!isBounded) {
            val intentService = Intent(context, service)
            context.startService(intentService)
        }
        val bindingIntent = Intent(context, service)
        context.bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }
}