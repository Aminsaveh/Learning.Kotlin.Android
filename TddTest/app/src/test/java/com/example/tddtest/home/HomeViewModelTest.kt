package com.example.tddtest.home

import com.example.tddtest.TestCoroutineRule
import com.example.tddtest.home.api.HomeRepository
import com.example.tddtest.home.model.PostDto
import com.example.tddtest.testPostDtoData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.HttpException
import retrofit2.Response
import java.net.ConnectException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()


    @Mock
    private lateinit var homeRepository: HomeRepository
    private lateinit var testObject: HomeViewModel


    @get:Rule
    var testCoroutineRule = TestCoroutineRule()

    @Test
    fun `on home view model init validate loading state default value as true`() {

            //Act
            testObject = HomeViewModel(homeRepository = homeRepository, dispatcher = testDispatcher)

            //Assert
            Assert.assertEquals(false, testObject.viewState.value.isLoading)
    }

    @Test
    fun `on home view model init validate post data state default value as empty posts list`() {

        //Act
        testObject = HomeViewModel(homeRepository)
        //Assert
        Assert.assertEquals(listOf<PostDto>(), testObject.viewState.value.postsListData)
    }


    @Test
    fun `on home view model init get posts returns success with posts list data`() {
        runTest(UnconfinedTestDispatcher()) {
            //Assemble
            val post1 = testPostDtoData(id = 11, title = "Post 11", body = "Post 11 Body")
            val post2 = testPostDtoData(id = 12, title = "Post 12", body = "Post 12 Body")

            Mockito.`when`(homeRepository.getPostsData()).thenReturn(flow {
                emit(listOf(post1, post2))
            })

            //Act
            testObject = HomeViewModel(homeRepository = homeRepository, dispatcher = testDispatcher)
            //Assert
            Assert.assertEquals(listOf(post1, post2), testObject.viewState.value.postsListData)
            Assert.assertEquals(false, testObject.viewState.value.isLoading)
        }
    }

    @Test
    fun `on home view model init get posts returns error with socket connection exception`() {
        runTest(UnconfinedTestDispatcher()) {
            //Assemble
            Mockito.`when`(homeRepository.getPostsData())
                .thenAnswer { throw ConnectException("Network Error") }

            //Act
            testObject = HomeViewModel(homeRepository = homeRepository, dispatcher = testDispatcher)
            //Assert
            Assert.assertEquals("Network Error", testObject.viewState.value.error)
        }
    }

    @Test
    fun `on home view model init get posts returns error with socket http exception`() {
        runTest(UnconfinedTestDispatcher()) {

            Mockito.`when`(homeRepository.getPostsData()).thenThrow(
                HttpException(
                    Response.error<Any>(
                        500,
                        "Error".toResponseBody("text/plain".toMediaTypeOrNull())
                    )
                )
            )

            //Act
            val testObject = HomeViewModel(homeRepository = homeRepository, dispatcher = testDispatcher)
            testObject.fetchPostsData()
            //Assert
            Assert.assertEquals("HTTP 500 Response.error()", testObject.viewState.value.error)
        }
    }


    @Test
    fun `on receiving error retry get posts returns success with posts list data`() {
        runTest(UnconfinedTestDispatcher()) {
            //Assemble
            val post1 = testPostDtoData()
            val post2 = testPostDtoData(id = 1, title = "Post 1", body = "Post 1 Body")

            Mockito.`when`(homeRepository.getPostsData())
                .thenAnswer { throw ConnectException("Network Error") }.thenReturn(flow {
                    emit(listOf(post1, post2))
                })

            //First Act
            testObject = HomeViewModel(homeRepository = homeRepository, dispatcher = testDispatcher)
            //Error Assertion
            Assert.assertEquals("Network Error", testObject.viewState.value.error)

            //Second Act
            testObject.fetchPostsData()

            //Assert Data
            Assert.assertEquals(listOf(post1, post2), testObject.viewState.value.postsListData)
        }
    }
}