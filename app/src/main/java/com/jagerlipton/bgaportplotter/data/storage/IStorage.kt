package com.jagerlipton.bgaportplotter.data.storage

interface IStorage {
    fun loadBaudrateIndex(): Int
    fun saveBaudrateIndex(index: Int)
}