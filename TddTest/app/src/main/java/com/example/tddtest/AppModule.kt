package com.example.tddtest

import com.example.tddtest.home.HomeViewModel
import com.example.tddtest.home.api.HomeDataService
import com.example.tddtest.home.api.HomeDataSource
import com.example.tddtest.home.api.HomeRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class AppModule : KoinComponent {
    private val REQUEST_TIMEOUT = 5L

    private var okHttpClient: OkHttpClient? = null

    private val moshi: Moshi by lazy { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }

    private lateinit var homePostsRetrofit: Retrofit

    fun init() {
        homePostsRetrofit = buildRetrofit(
            okHttpClient = getOkHttpClient(),
            moshi = moshi
        )

        val homeScreenModules = module {
            single { createHomePostsApiService(HomeDataService::class.java) }
            single { HomeDataSource(get()) }
            single { HomeRepository(get()) }
            viewModel { HomeViewModel(get()) }
        }

        val moduleList = listOf(
            homeScreenModules,
        )
        loadKoinModules(moduleList)
    }

    fun <S> createHomePostsApiService(service: Class<S>) = homePostsRetrofit.create(service)

    private fun getOkHttpClient(): OkHttpClient {

        return if (okHttpClient == null) {
            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .build()
            this.okHttpClient = okHttpClient
            okHttpClient
        } else {
            okHttpClient!!
        }
    }

    private fun buildRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://gorest.co.in/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
    }
}