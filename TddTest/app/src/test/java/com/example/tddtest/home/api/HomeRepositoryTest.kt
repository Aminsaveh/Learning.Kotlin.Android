package com.example.tddtest.home.api

import com.example.tddtest.TestCoroutineRule
import com.example.tddtest.home.model.PostDto
import com.example.tddtest.test
import com.example.tddtest.testPostDtoData
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class HomeRepositoryTest {

    @Mock
    private lateinit var homeDataSource : HomeDataSource

    @get:Rule
    var testCoroutineRule = TestCoroutineRule()


    @Test
    fun `get posts data returns posts list data`(){
        runTest(UnconfinedTestDispatcher()) {
            //Assemble
            val subject = Channel<List<PostDto>>()
            val post1 = testPostDtoData()
            val post2 = testPostDtoData()

            Mockito.`when`(homeDataSource.getPostsData()).thenReturn(subject.consumeAsFlow())

            val testObject = HomeRepository(homeDataSource = homeDataSource)

            //Act
            val testObserver = testObject.getPostsData().test(this)

            testObserver.assertNoValues()

            subject.send(listOf(post1, post2))


            //Assert
            testObserver.assertValues(listOf(post1,post2))

            testObserver.finish()
        }
    }
}



