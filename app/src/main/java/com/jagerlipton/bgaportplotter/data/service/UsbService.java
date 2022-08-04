package com.jagerlipton.bgaportplotter.data.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.jagerlipton.bgaportplotter.data.service.model.ConnectionType;
import com.jagerlipton.bgaportplotter.data.service.model.PlotterMessage;
import com.jagerlipton.bgaportplotter.presentation.ui.connection.model.Device;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsbService extends Service {
    public static final String ACTION_USB_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    public static final String ACTION_USB_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED";
    private static final String ACTION_USB_PERMISSION = "com.jagerlipton.bgaportplotter.USB_PERMISSION";
    public static final int MESSAGE_FROM_SERIAL_PORT = 0;
    public static final int MESSAGE_FROM_SERVICE_DEVICELIST = 1;
    public static final int MESSAGE_FROM_SERVICE_BROADCAST = 2;
    public static final int MESSAGE_FROM_SERVICE_PORT_STATE = 3;

    private static final String DELIMITER = "\n";
    private int baudrate = 9600; // default
    private final IBinder binder = new UsbBinder();
    private UsbManager usbManager;
    private UsbDevice device;
    private UsbDeviceConnection connection;
    private UsbSerialDevice serialPort;
    private boolean serialPortConnected = false;

    private final Device usbDevice = new Device(ConnectionType.USB, "Unknown", "", "");
    private final List<Device> devList = new ArrayList<>();

    private Messenger messageHandler;

    public void sendToServiceManager(int state, Object obj) {
        if (messageHandler != null) {
            Message message = Message.obtain();
            message.arg1 = state;
            message.obj = obj;
            try {
                messageHandler.send(message);

            } catch (
                    RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void setMessageHandler(Messenger messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void setBaudrate(int baudrate) {
        this.baudrate = baudrate;
    }

    public class UsbBinder extends Binder {
        public UsbService getService() {
            return UsbService.this;
        }
    }

    @Override
    public void onCreate() {
        serialPortConnected = false;
        setFilter();
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        findSerialPortDevice();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serialPort.close();
        unregisterReceiver(usbReceiver);
        sendToServiceManager(MESSAGE_FROM_SERVICE_PORT_STATE, false);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    private void setFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(ACTION_USB_DETACHED);
        filter.addAction(ACTION_USB_ATTACHED);
        registerReceiver(usbReceiver, filter);
    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if (arg1.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = arg1.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    sendToServiceManager(MESSAGE_FROM_SERVICE_BROADCAST, "USB Ready");
                    connection = usbManager.openDevice(device);
                    new ConnectionThread().start();
                } else {
                    sendToServiceManager(MESSAGE_FROM_SERVICE_BROADCAST, "USB Permission not granted");
                }
            } else if (arg1.getAction().equals(ACTION_USB_ATTACHED)) {
                if (!serialPortConnected)
                    findSerialPortDevice();
            } else if (arg1.getAction().equals(ACTION_USB_DETACHED)) {
                sendToServiceManager(MESSAGE_FROM_SERVICE_BROADCAST, "USB Disconnected");
                if (serialPortConnected) {
                    serialPort.close();
                }
                serialPortConnected = false;
                devList.clear();
                sendToServiceManager(MESSAGE_FROM_SERVICE_DEVICELIST, devList);
                sendToServiceManager(MESSAGE_FROM_SERVICE_PORT_STATE, false);
            }
        }
    };

    private void findSerialPortDevice() {
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
            }
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                int deviceVID = device.getVendorId();
                int devicePID = device.getProductId();
                usbDevice.setSubHeader("VID:" + deviceVID + "/PID:" + devicePID);
                if (UsbSerialDevice.isSupported(device)) {
                    usbDevice.setComment("Supported: true");
                    requestUserPermission();
                    devList.clear();
                    devList.add(usbDevice);
                    sendToServiceManager(MESSAGE_FROM_SERVICE_DEVICELIST, devList);
                    break;
                } else {
                    connection = null;
                    device = null;
                    usbDevice.setComment("Supported: false");
                    devList.clear();
                    devList.add(usbDevice);
                    sendToServiceManager(MESSAGE_FROM_SERVICE_DEVICELIST, devList);
                }
            }
            if (device == null) {
                sendToServiceManager(MESSAGE_FROM_SERVICE_BROADCAST, "No USB");
            }
        } else {
            sendToServiceManager(MESSAGE_FROM_SERVICE_BROADCAST, "No USB");
        }
    }

    private void requestUserPermission() {
        Intent intent = new Intent(ACTION_USB_PERMISSION);
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        usbManager.requestPermission(device, mPendingIntent);
    }

    private class ConnectionThread extends Thread {
        @Override
        public void run() {
            serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
            if (serialPort != null) {
                if (serialPort.open()) {
                    sendToServiceManager(MESSAGE_FROM_SERVICE_PORT_STATE, true);
                    serialPort.setBaudRate(baudrate);
                    serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                    serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                    serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                    serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                    serialPort.read(mCallback);
                }
            } else {
                sendToServiceManager(MESSAGE_FROM_SERVICE_BROADCAST, "USB not supported");
            }
        }
    }

    private void write(byte[] data) {
        if (serialPort != null) {
            serialPort.write(data);
        }
    }

    public void writeCommandToPort(String outputString) {
        write(outputString.getBytes());
    }

    private UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] arg0) {
            if (arg0.length != 0) {
                String data = new String(arg0, StandardCharsets.UTF_8);
                String[] messages = data.split(DELIMITER);
                for (String message : messages) {
                    parsing(message);
                }
            }
        }
    };

    private String delReturnChar(String input) {
        String tempString = input;
        if (tempString.charAt(tempString.length() - 1) != ';')
            tempString = tempString.substring(0, tempString.length() - 1);
        return tempString;
    }

    private Boolean isValidPlotterMessage(String input) {
        if (!input.isEmpty())
            return input.charAt(0) == '$' && input.charAt(input.length() - 1) == ';';
        return false;
    }

    private void parsing(String input) {
        try {
            String tempString = delReturnChar(input);
            if (isValidPlotterMessage(tempString)) {
                String messageBody = tempString.substring(1, tempString.length() - 1);
                String[] numbers = messageBody.split(" ");
                List<Float> newList = new ArrayList<>();
                for (String number : numbers) {
                    newList.add(Float.parseFloat(number));
                }
                PlotterMessage newMessage = new PlotterMessage(-1, newList);
                sendToServiceManager(MESSAGE_FROM_SERIAL_PORT, newMessage);
            }
        } catch (NumberFormatException e) {
            //
        }
    }
}
