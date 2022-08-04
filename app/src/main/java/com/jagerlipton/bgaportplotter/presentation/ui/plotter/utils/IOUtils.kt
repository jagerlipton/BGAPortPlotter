package com.jagerlipton.bgaportplotter.presentation.ui.plotter.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineDataSet
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class IOUtils {

    companion object {

        private const val STORAGE_PERMISSION_CODE: Int = 101
        private const val WRITE_EXTERNAL_STORAGE: String =
            Manifest.permission.WRITE_EXTERNAL_STORAGE

        fun checkPermission(activity: Activity) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(WRITE_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }

        fun isFilePermission(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_DENIED
        }

        private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
            val formatter = SimpleDateFormat(format, locale)
            return formatter.format(this)
        }

        private fun getCurrentDateTime(): Date {
            return Calendar.getInstance().time
        }

        private fun generateFileName(): String {
            val date = getCurrentDateTime()
            val dateInString = date.toString("yyyy-MM-dd_HH-mm-ss")
            return "Chart_$dateInString"
        }

        fun saveFile(content: String): String {
            try {
                if (content.isNotEmpty()) {
                    val name = generateFileName()
                    val fullName = "$name.csv"
                    val downloadDir =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val file = File(downloadDir, fullName)
                    if (!file.exists()) file.createNewFile()
                    val fileWriter = FileWriter(file.absoluteFile)
                    val bufferedWriter = BufferedWriter(fileWriter)
                    bufferedWriter.write(content)
                    bufferedWriter.close()
                    return "$name was saved to $downloadDir"
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return "Saving FAILED!"
        }

        fun loadFile(){ //TODO


        }


        fun chartToString(chart: LineChart): String {
            var content: String = ""
            try {
                val setsCount = chart.data.dataSetCount
                if (setsCount > 0) {
                    val valuesCount = chart.data.getDataSetByIndex(0).entryCount
                    for (i in 0 until valuesCount) {
                        for (y in 0 until setsCount) {
                            val currentSet = chart.data.getDataSetByIndex(y) as LineDataSet
                            val values = currentSet.values
                            content = content.plus(values[i].y.toString())
                            content = content.plus(",")
                            if (y == setsCount - 1) content = content.plus('\n')
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return content
        }

        fun saveImage(chart: LineChart): String {
            val name: String = generateFileName()
            return if (chart.saveToGallery(name, "", "", Bitmap.CompressFormat.PNG, 100))
                "$name was saved"
            else "Saving FAILED!"
        }
    }
}