package com.example.tddtest

import android.app.Application
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module

class KoinTestApp : Application() {
    private val moduleList = mutableListOf<Module>()

    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidLogger()
            androidContext(this@KoinTestApp)
        }
    }

    fun addModule(module: Module){
        moduleList.add(module)
    }

    internal fun loadModules(){
        loadKoinModules(moduleList)
    }

    internal fun unloadModules(){
        GlobalContext.unloadKoinModules(moduleList)
        stopKoin()
    }
}