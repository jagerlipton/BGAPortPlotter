package com.jagerlipton.bgaportplotter.data.storage

import android.content.Context
import android.content.SharedPreferences

class Storage(context: Context) :IStorage{
    private val APP_PREFERENCES = "preferences"
    private val APP_PREFERENCES_BAUDRATEINDEX = "baudrateindex"
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

   override fun loadBaudrateIndex(): Int {
        return sharedPreferences.getInt(APP_PREFERENCES_BAUDRATEINDEX, 0)
    }

    override fun saveBaudrateIndex(index: Int) {
        sharedPreferences.edit().putInt(APP_PREFERENCES_BAUDRATEINDEX, index).apply()
    }

}