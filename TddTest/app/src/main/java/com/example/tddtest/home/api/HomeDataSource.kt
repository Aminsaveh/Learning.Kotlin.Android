package com.example.tddtest.home.api

import com.example.tddtest.home.model.PostDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class HomeDataSource(
    private val homeDataService: HomeDataService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    fun getPostsData(): Flow<List<PostDto>> {
        return flow {
            emit(homeDataService.getPostsData())
        }.flowOn(dispatcher)
    }
}