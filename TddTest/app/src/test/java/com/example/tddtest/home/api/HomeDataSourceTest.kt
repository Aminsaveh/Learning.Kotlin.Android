package com.example.tddtest.home.api

import com.example.tddtest.TestCoroutineRule
import com.example.tddtest.TestObserver
import com.example.tddtest.test
import com.example.tddtest.testPostDtoData
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class HomeDataSourceTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var homeDataService : HomeDataService

    @Test
    fun `get posts data returns posts list data`(){
        runTest(UnconfinedTestDispatcher()) {
            //Assemble
            val post1 = testPostDtoData()
            val post2 = testPostDtoData()
            Mockito.`when`(homeDataService.getPostsData()).thenReturn(listOf(post1,post2))
            val testObject = HomeDataSource(homeDataService = homeDataService, dispatcher = testDispatcher)

            //Act
            val testObserver = testObject.getPostsData().test(scope = this)


            //Assert
            testObserver.assertValues(listOf(post1,post2))

            testObserver.finish()

        }
    }
}