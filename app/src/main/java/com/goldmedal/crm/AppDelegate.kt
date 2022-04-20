package com.goldmedal.crm


import android.app.Application
import android.content.Context
import com.goldmedal.crm.data.db.AppDatabase
import com.goldmedal.crm.data.network.MyApi
import com.goldmedal.crm.data.network.NetworkConnectionInterceptor
import com.goldmedal.crm.data.preferences.PreferenceProvider
import com.goldmedal.crm.data.repositories.*

import com.goldmedal.crm.ui.auth.LoginViewModelFactory
import com.goldmedal.crm.ui.dashboard.Manager.ManagerViewModelFactory
import com.goldmedal.crm.ui.dashboard.home.HomeViewModelFactory
import com.goldmedal.crm.ui.dashboard.notification.NotificationViewModelFactory

import com.goldmedal.crm.ui.parts.PartsViewModelFactory
import com.goldmedal.crm.ui.ticket.TicketViewModelFactory
import com.goldmedal.hrapp.ui.dialogs.TicketUnacceptanceViewModelFactory

import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton


/**
 * Created by goldmedal on 22/08/18.
 */

class AppDelegate : Application(), KodeinAware {

    init {
        instance = this
    }

    companion object {
        private var instance: AppDelegate? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }


    override val kodein = Kodein.lazy {
        import(androidXModule(this@AppDelegate))

        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { MyApi(instance()) }

        bind() from singleton { AppDatabase(instance()) }
        bind() from singleton { PreferenceProvider(instance()) }
        bind() from singleton { UserRepository(instance(), instance(),instance()) }
        bind() from singleton { HomeRepository(instance(), instance()) }
        bind() from singleton { NotificationRepository(instance(), instance()) }
        bind() from singleton { TicketRepository(instance(), instance()) }
        bind() from singleton { ManagerRepository(instance(), instance()) }

        bind() from provider { LoginViewModelFactory(instance()) }
        bind() from provider { HomeViewModelFactory(instance()) }
        bind() from provider { NotificationViewModelFactory(instance()) }
        bind() from provider { TicketViewModelFactory(instance()) }
        bind() from provider { TicketUnacceptanceViewModelFactory(instance()) }
        bind() from provider { PartsViewModelFactory(instance()) }
        bind() from provider { ManagerViewModelFactory(instance()) }

    }

}
