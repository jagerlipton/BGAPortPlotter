package com.jagerlipton.bgaportplotter.data.repository

import com.jagerlipton.bgaportplotter.data.storage.IStorage

class Repository(private val storage: IStorage): IRepository {

    override fun loadBaudrateIndex(): Int {
        return storage.loadBaudrateIndex()
    }

    override fun saveBaudrateIndex(index: Int) {
        storage.saveBaudrateIndex(index);
    }
}