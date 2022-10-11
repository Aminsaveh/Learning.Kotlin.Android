package com.example.tddtest.home.api

import com.example.tddtest.home.model.PostDto
import kotlinx.coroutines.flow.Flow

class HomeRepository(private val homeDataSource: HomeDataSource) {
    suspend fun getPostsData(): Flow<List<PostDto>> {
        return homeDataSource.getPostsData()
    }
}
