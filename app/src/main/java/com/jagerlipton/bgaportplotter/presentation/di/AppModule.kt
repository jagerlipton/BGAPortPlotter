package com.jagerlipton.bgaportplotter.presentation.di

import com.jagerlipton.bgaportplotter.data.repository.IRepository
import com.jagerlipton.bgaportplotter.data.repository.Repository
import com.jagerlipton.bgaportplotter.data.service.IServiceManager
import com.jagerlipton.bgaportplotter.data.service.IServiceOutput
import com.jagerlipton.bgaportplotter.data.service.ServiceManager
import com.jagerlipton.bgaportplotter.data.service.ServiceOutput
import com.jagerlipton.bgaportplotter.data.storage.IStorage
import com.jagerlipton.bgaportplotter.data.storage.Storage
import com.jagerlipton.bgaportplotter.presentation.ui.connection.ConnectionViewModel
import com.jagerlipton.bgaportplotter.presentation.ui.options.OptionsViewModel
import com.jagerlipton.bgaportplotter.presentation.ui.plotter.PlotterViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<IStorage> {
        Storage(context = get())
    }

    single<IRepository> {
        Repository(storage = get())
    }

     single<IServiceManager> {
        ServiceManager(context = get(), storage = get(), serviceOutput = get())
    }

    single<IServiceOutput> {
        ServiceOutput()
    }

    viewModel {
        ConnectionViewModel(
        serviceManager = get (),
        repository = get (),
        serviceOutput = get ()
        )
    }

    viewModel {
        PlotterViewModel(
            serviceOutput = get ()
        )
    }
}