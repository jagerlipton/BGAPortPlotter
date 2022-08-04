package com.jagerlipton.bgaportplotter.presentation.ui.plotter.utils

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.jagerlipton.bgaportplotter.R
import com.jagerlipton.bgaportplotter.data.service.model.PlotterMessage
import com.jagerlipton.bgaportplotter.presentation.Colors
import com.jagerlipton.bgaportplotter.presentation.ui.plotter.LastMessageRecyclerAdapter


class ChartUtils {

    companion object {

        private fun addRowsToSetList(
            message: PlotterMessage,
            chart: LineChart,
            messageList: MutableList<Float>
        ) {
            var setsCount = 0
            if (chart.data != null) setsCount = chart.data.dataSetCount
            if (setsCount < message.list.size) {
                val countNewSets: Int = message.list.size - setsCount
                for (i in 1..countNewSets) {
                    addSet(chart)
                }
            }
            addRowsToLastMessageList(message.list.size, messageList)
        }

        private fun addRowsToLastMessageList(
            plotterSetCount: Int,
            messageList: MutableList<Float>
        ) {
            if (messageList.size < plotterSetCount) {
                val countRows: Int = plotterSetCount - messageList.size
                for (i in 1..countRows) {
                    messageList.add(0f)
                }
            }
        }

        fun addMessageToChart(
            message: PlotterMessage,
            chart: LineChart,
            messageList: MutableList<Float>,
            adapter: LastMessageRecyclerAdapter
        ) {
            addRowsToSetList(message, chart, messageList)
            for (i in 0 until message.list.size) {
                val currentSet = chart.data.getDataSetByIndex(i) as LineDataSet
                currentSet.addEntry(Entry(currentSet.entryCount.toFloat(), message.list[i]))
                currentSet.notifyDataSetChanged()
                chart.data.notifyDataChanged()
                chart.notifyDataSetChanged()
                messageList[i] = message.list[i]
                adapter.notifyItemChanged(i)
            }

            chart.invalidate()
        }

        fun replaceAllMessagesInChart(
            messages: List<PlotterMessage>,
            chart: LineChart,
            messageList: MutableList<Float>,
            adapter: LastMessageRecyclerAdapter
        ) {
            if (chart.data != null) {
                chart.data.dataSets.clear()
                messages.forEach() {
                    addMessageToChart(it, chart, messageList, adapter)
                }
            }
        }

        private fun addSet(chart: LineChart) {
            var setNumber: Int = 0
            if (chart.data != null) setNumber = chart.data.dataSetCount
            val newSet = LineDataSet(null, "№ " + (setNumber + 1).toString())
            when (setNumber) {
                0 -> {
                    newSet.color = Colors.getColorFromRes(R.color.line1, chart.context)
//                        newSet.setDrawFilled(true)
//                        val fillGradient = ContextCompat.getDrawable(chart.context, R.drawable.line1_gradient)
//                        newSet.fillDrawable = fillGradient
                }
                1 -> {
                    newSet.color = Colors.getColorFromRes(R.color.line2, chart.context)
                }
                2 -> {
                    newSet.color = Colors.getColorFromRes(R.color.line3, chart.context)

                }
                3 -> {
                    newSet.color = Colors.getColorFromRes(R.color.line4, chart.context)
                }
                else -> newSet.color = Colors.randomColor()
            }
            newSet.lineWidth = 2.5f
            newSet.setDrawCircles(false)
            newSet.highLightColor = newSet.color
            newSet.axisDependency = YAxis.AxisDependency.LEFT
            newSet.valueTextSize = 10f

            var dataSets: MutableList<ILineDataSet> = mutableListOf()
            if (chart.data != null) dataSets = chart.data.dataSets
            dataSets.add(newSet)
            val data = LineData(dataSets)
            chart.data = data
        }
    }
}