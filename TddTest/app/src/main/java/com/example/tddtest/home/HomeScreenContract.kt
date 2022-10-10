package com.example.tddtest.home

import com.example.tddtest.ViewEvent
import com.example.tddtest.ViewSideEffect
import com.example.tddtest.ViewState
import com.example.tddtest.home.model.PostDto

class HomeScreenContract {

    sealed class Event : ViewEvent

    data class State(
        val isLoading :Boolean,
        val postsListData : List<PostDto>,
        val error : String?
    ) : ViewState

    sealed class Effect : ViewSideEffect{
        object OnError : Effect()
    }
}