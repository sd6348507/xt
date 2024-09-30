package com.roemsoft.equipment.di

import com.roemsoft.common.di.NetworkModule
import com.roemsoft.equipment.api.ApiService
import com.roemsoft.equipment.repository.AppRepository

object RepositoryModule {

    private val api: ApiService by lazy { provideApiService() }

    val repository: AppRepository by lazy { provideAppRepository() }

    private fun provideApiService(): ApiService {
        return NetworkModule.instance.retrofit.create(ApiService::class.java)
    }

    private fun provideAppRepository(): AppRepository {
        return AppRepository(api)
    }
}