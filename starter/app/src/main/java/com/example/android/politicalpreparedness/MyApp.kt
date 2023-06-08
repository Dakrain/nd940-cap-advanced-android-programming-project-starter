package com.example.android.politicalpreparedness

import android.app.Application
import com.example.android.politicalpreparedness.data.database.ElectionDatabase
import com.example.android.politicalpreparedness.data.network.CivicsApi
import com.example.android.politicalpreparedness.domain.repository.ElectionLocalRepository
import com.example.android.politicalpreparedness.domain.repository.ElectionRemoteRepository
import com.example.android.politicalpreparedness.election.ElectionsViewModel
import com.example.android.politicalpreparedness.election.VoterInfoViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber


class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        /**
         * use Koin library as a service locator
         */
        val myModule = module {
            viewModel {
                ElectionsViewModel(this@MyApp, get(), get())
            }
            viewModel {
                VoterInfoViewModel(
                    this@MyApp,
                    get(),
                    get()
                )
            }
            single { ElectionLocalRepository(get()) }
            single { ElectionRemoteRepository(get()) }
            single { ElectionDatabase.createElectionDAO(this@MyApp) }
            single { CivicsApi.retrofitService }
        }

        startKoin {
            androidContext(this@MyApp)
            modules(listOf(myModule))
        }
    }
}