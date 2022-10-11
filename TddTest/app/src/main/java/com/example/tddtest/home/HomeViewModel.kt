package com.example.tddtest.home

import androidx.lifecycle.viewModelScope
import com.example.tddtest.BaseViewModel
import com.example.tddtest.home.api.HomeRepository
import com.example.tddtest.home.model.PostDto
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull


class HomeViewModel(private val homeRepository: HomeRepository, private val dispatcher: CoroutineDispatcher = Dispatchers.IO) :
    BaseViewModel<HomeScreenContract.Event, HomeScreenContract.State, HomeScreenContract.Effect>() {

    override fun setInitialState(): HomeScreenContract.State {
        return HomeScreenContract.State(
            isLoading = true,
            postsListData = emptyList(),
            error = null
        )
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("hii" + throwable.message)
        viewModelScope.launch(dispatcher) {
            setState {
                copy(
                    error = throwable.message,
                    isLoading = false
                )
            }
        }
        setEffect { HomeScreenContract.Effect.OnError}
    }



    init {
        viewModelScope.launch(dispatcher + exceptionHandler){
            fetchPostsData()
        }

    }

    fun fetchPostsData() {
        viewModelScope.launch(dispatcher + exceptionHandler) {
            homeRepository.getPostsData().collect{
              postsData ->
                println("hii" + postsData)
                updatePostsDataToView(postsData)
            }
        }
    }

    private fun updatePostsDataToView(postsData: List<PostDto>) {
        viewModelScope.launch(dispatcher) {
            setState {
                copy(postsListData = postsData, isLoading = false)
            }
        }
    }

}
