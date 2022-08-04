package com.jagerlipton.bgaportplotter.data.service.model

class Command (val command: Commands) {

    enum class Commands {
        COMMAND_GET_PROFILE, COMMAND_SAVE_PROFILE, SHORT_PROFILE, JSON_PROFILE
    }
//TODO запрос профиля для построения графика

//    fun getCommand(): Commands? {
//        return command
//    }
//
//    fun setCommand(command: Commands?) {
//        this.command = command
//    }
//
//    fun getList(): List<ArduinoProfileListDomain>? {
//        return list
//    }
//
//    fun setList(list: List<ArduinoProfileListDomain>?) {
//        this.list = list
//    }

}