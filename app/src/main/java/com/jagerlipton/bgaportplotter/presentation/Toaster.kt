package com.jagerlipton.bgaportplotter.presentation

import android.content.Context
import android.widget.Toast

class Toaster (){
    companion object{
        fun show(text: String, context: Context) {
            if (text.isNotEmpty()) Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }
}