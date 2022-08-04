package com.jagerlipton.bgaportplotter.presentation.ui.plotter

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.LineChart
import com.jagerlipton.bgaportplotter.R
import com.jagerlipton.bgaportplotter.data.service.model.PlotterMessage
import com.jagerlipton.bgaportplotter.databinding.FragmentPlotterBinding
import com.jagerlipton.bgaportplotter.presentation.Colors
import com.jagerlipton.bgaportplotter.presentation.Toaster
import com.jagerlipton.bgaportplotter.presentation.ui.plotter.utils.ChartUtils
import com.jagerlipton.bgaportplotter.presentation.ui.plotter.utils.IOUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlotterFragment : Fragment() {

    private var _binding: FragmentPlotterBinding? = null
    private val binding get() = _binding!!
    private val plotterViewModel by viewModel<PlotterViewModel>()
    private lateinit var portStateFlowObserver: Observer<Boolean>
    private lateinit var lastMessageFlowObserver: Observer<PlotterMessage>
    private val lastMessageList: MutableList<Float> = mutableListOf()
    private lateinit var lastMessageAdapter: LastMessageRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlotterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        lastMessageAdapter = LastMessageRecyclerAdapter(lastMessageList)

        with(binding.lastMessageRecycler) {
            layoutManager = LinearLayoutManager(activity)
            adapter = lastMessageAdapter
        }
        with(binding.lineChart) {
            isAutoScaleMinMaxEnabled = true
            description.isEnabled = false
            setBackgroundColor(Colors.getColorFromRes(R.color.chart_bg, requireContext()))
            setNoDataTextColor(Colors.getColorFromRes(R.color.actionbar, requireContext()))
        }

        portStateFlowObserver =
            Observer { portState: Boolean -> if (portState) onConnect() else onDisconnect() }

        lastMessageFlowObserver = Observer { message: PlotterMessage ->
            ChartUtils.addMessageToChart(
                message,
                binding.lineChart,
                lastMessageList,
                lastMessageAdapter
            )
        }

        binding.saveButton.setOnClickListener() {
            saveButtonClick()
        }

        binding.clearButton.setOnClickListener() {
            clearButtonClick()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        plotterViewModel.portStateFlow.observe(this, portStateFlowObserver)
        plotterViewModel.lastMessageFlow.observe(this, lastMessageFlowObserver)
        replaceChartData(binding.lineChart, lastMessageList, lastMessageAdapter)
    }

    override fun onPause() {
        super.onPause()
        plotterViewModel.portStateFlow.removeObserver(portStateFlowObserver)
        plotterViewModel.lastMessageFlow.removeObserver(lastMessageFlowObserver)
    }

    private fun onConnect() {
        with(binding) {
            statusTextView.setText(R.string.label_connected)
        }
    }

    private fun onDisconnect() {
        with(binding) {
            statusTextView.setText(R.string.label_disconnected)
        }
    }

    private fun clearButtonClick() {
        clearChartData(binding.lineChart, lastMessageList, lastMessageAdapter)
    }

    private fun saveButtonClick() {
        if (IOUtils.isFilePermission(requireContext())) showPopup(binding.saveButton)
        else IOUtils.checkPermission(requireActivity())
    }

    private fun showPopup(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.inflate(R.menu.save_menu)
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.savePNG -> {
                    savePNG(binding.lineChart)
                }
                R.id.saveCSV -> {
                    saveCSV(binding.lineChart)
                }
            }
            true
        }
        popup.show()
    }

    private fun saveCSV(chart: LineChart) {
        if (chart.data != null) {
            val result = IOUtils.saveFile(IOUtils.chartToString(chart))
            Toaster.show(result, chart.context)
        }
    }

    private fun savePNG(chart: LineChart) {
        if (chart.data != null) {
            val result = IOUtils.saveImage(chart)
            Toaster.show(result, chart.context)
        }
    }

    private fun replaceChartData(
        chart: LineChart,
        list: MutableList<Float>,
        adapter: LastMessageRecyclerAdapter
    ) {
        ChartUtils.replaceAllMessagesInChart(
            plotterViewModel.getPlotterList(),
            chart,
            list,
            adapter
        )
    }

    private fun clearChartData(
        chart: LineChart,
        list: MutableList<Float>,
        adapter: LastMessageRecyclerAdapter
    ) {
        plotterViewModel.clearPlotterList()
        chart.data = null
        list.clear()
        adapter.notifyDataSetChanged()
    }

}
