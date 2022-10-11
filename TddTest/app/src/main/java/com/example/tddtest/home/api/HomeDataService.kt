package com.example.tddtest.home.api

import com.example.tddtest.home.model.PostDto
import retrofit2.http.GET

interface HomeDataService {

    @GET("/public/v2/posts")
    suspend fun getPostsData() : List<PostDto>

}
