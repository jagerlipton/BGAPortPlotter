package com.jagerlipton.bgaportplotter.presentation.ui.connection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jagerlipton.bgaportplotter.R
import com.jagerlipton.bgaportplotter.data.service.model.ConnectionType
import com.jagerlipton.bgaportplotter.databinding.FragmentConnectionBinding
import com.jagerlipton.bgaportplotter.presentation.Toaster
import com.jagerlipton.bgaportplotter.presentation.ui.connection.model.Device
import org.koin.androidx.viewmodel.ext.android.viewModel


class ConnectionFragment() : Fragment() {

    private var _binding: FragmentConnectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var portStateFlowObserver: Observer<Boolean>
    private lateinit var flowCastObserver: Observer<String>
    private lateinit var deviceListObserver: Observer<List<Device>>
    private lateinit var liveCastObserver: Observer<String>

    private val connectionViewModel by viewModel<ConnectionViewModel>()
    private val deviceList: MutableList<Device> = mutableListOf()
    private lateinit var deviceAdapter: DeviceRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConnectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        deviceAdapter = DeviceRecyclerAdapter(deviceList)

        with(binding.deviceRecycler) {
            layoutManager = LinearLayoutManager(activity)
            adapter = deviceAdapter
        }

        portStateFlowObserver =
            Observer { portState: Boolean -> if (portState) onConnect() else onDisconnect() }

        flowCastObserver = Observer {
              text: String -> connectionViewModel.parseFlowCast(text)
         }

        deviceListObserver = Observer { flowList: List<Device> ->
            connectionViewModel.parseDeviceList(
                flowList,
                deviceList,
                deviceAdapter
            )
        }

       liveCastObserver = Observer { text: String -> Toaster.show(text, requireContext()) }
//        binding.navigateToOptionsButton.setOnClickListener {
//            findNavController().navigate(R.id.action_navigation_connection_to_navigation_options)
//        }
        binding.navigateToPlotterButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_connection_to_navigation_plotter)
        }

        binding.speedSpinner.setSelection(connectionViewModel.getSpinnerIndex())

        binding.speedSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                connectionViewModel.setSpinnerIndex(position)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        connectionViewModel.portStateFlow.observe(this, portStateFlowObserver)
        connectionViewModel.flowCast.observe(this, flowCastObserver)
        connectionViewModel.liveCast.observe(this, liveCastObserver)
        connectionViewModel.devList.observe(this, deviceListObserver)
        connectionViewModel.connect(ConnectionType.USB)
    }

    override fun onPause() {
        super.onPause()
        connectionViewModel.portStateFlow.removeObserver(portStateFlowObserver)
        connectionViewModel.flowCast.removeObserver(flowCastObserver)
        connectionViewModel.liveCast.removeObserver(liveCastObserver)
        connectionViewModel.devList.removeObserver(deviceListObserver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onConnect() {
        with(binding) {
            statusTextView.setText(R.string.label_connected)
            speedSpinner.isEnabled = false
            bgImageView.setImageResource(R.drawable.usb_online)
            navigateToPlotterButton.isEnabled = true
        }
    }

    private fun onDisconnect() {
        with(binding) {
            statusTextView.setText(R.string.label_disconnected)
            speedSpinner.isEnabled = true
            bgImageView.setImageResource(R.drawable.usb_offline)
            navigateToPlotterButton.isEnabled = false
            connectionViewModel.clearDeviceList( deviceList, deviceAdapter)
        }
    }
}