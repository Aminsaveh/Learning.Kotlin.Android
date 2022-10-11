package com.example.tddtest.home

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tddtest.KoinTestApp
import com.example.tddtest.MainActivity
import com.example.tddtest.home.api.HomeRepository
import com.example.tddtest.home.model.PostDto
import com.example.tddtest.testPostDtoData
import com.example.tddtest.ui.theme.TddTestTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(
    instrumentedPackages = ["androidx.loader.content"],
    application = KoinTestApp::class,
    sdk = [28]
)
class HomeScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val homeRepository : HomeRepository = mock(HomeRepository::class.java)

    private val mockedModule = module {
        single { homeRepository }
        viewModel { HomeViewModel(get()) }
    }

    private val app : KoinTestApp = ApplicationProvider.getApplicationContext()

    @Before
    fun setUp(){
        app.addModule(mockedModule)
        app.loadModules()
    }

    @After
    fun tearDown(){
        app.unloadModules()
    }


    @Test
   fun `validate posts data`() {
        runTest(UnconfinedTestDispatcher()) {
            //Assemble
            val post1 = testPostDtoData(title = "Post 1", body = "Post Body 1")
            val post2 = testPostDtoData(title = "Post 2", body = "Post Body 2")

            val subject = Channel<List<PostDto>>()

            Mockito.`when`(homeRepository.getPostsData()).thenReturn(subject.consumeAsFlow())

            //Act
            composeTestRule.setContent {
                TddTestTheme {
                    Surface(color = MaterialTheme.colors.background) {
                        HomeScreen()
                    }
                }
            }

            launch(UnconfinedTestDispatcher()) {
                subject.send(listOf(post1,post2))
            }
            //Assert
            composeTestRule.onNodeWithText("Post 1").assertIsDisplayed()
            composeTestRule.onNodeWithText("Post Body 1").assertIsDisplayed()

            composeTestRule.onNodeWithText("Post 2").assertIsDisplayed()
            composeTestRule.onNodeWithText("Post Body 2").assertIsDisplayed()

        }
    }
}