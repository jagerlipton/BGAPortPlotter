package com.jagerlipton.bgaportplotter.data.repository

interface IRepository {
    fun loadBaudrateIndex(): Int
    fun saveBaudrateIndex(index: Int)
}