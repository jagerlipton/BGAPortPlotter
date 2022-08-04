package com.jagerlipton.bgaportplotter.presentation

import android.content.Context
import android.graphics.Color
import android.os.Build
import com.jagerlipton.bgaportplotter.R
import kotlin.random.Random

class Colors {
    companion object {

         fun randomColor(): Int {
            val rnd = Random.Default
            return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        }

        fun getColorFromRes(indexRes:Int, context: Context):Int {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                context.resources.getColor(indexRes, context.theme)
             else context.resources.getColor(indexRes)
        }

        fun getColorWFromRes(index: Int, context: Context): Int {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return when (index) {
                    0 -> context.resources.getColor(R.color.line1, context.theme)
                    1 -> context.resources.getColor(R.color.line2, context.theme)
                    2 -> context.resources.getColor(R.color.line3, context.theme)
                    3 -> context.resources.getColor(R.color.line4, context.theme)
                    else -> context.resources.getColor(R.color.white, context.theme)
                }
            } else {
                return when (index) {
                    0 -> context.resources.getColor(R.color.line1)
                    1 -> context.resources.getColor(R.color.line2)
                    2 -> context.resources.getColor(R.color.line3)
                    3 -> context.resources.getColor(R.color.line4)
                    else -> context.resources.getColor(R.color.white)
                }
            }
        }
    }
}