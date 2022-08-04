package com.jagerlipton.bgaportplotter.data.storage.utils

class Baudrate {
   companion object {
       private val rates = arrayOf(9600, 19200, 38400, 57600, 115200, 230400, 460800, 921600)

       fun getBaudrateByIndex(index: Int): Int {
           return rates[index]
       }
   }
}