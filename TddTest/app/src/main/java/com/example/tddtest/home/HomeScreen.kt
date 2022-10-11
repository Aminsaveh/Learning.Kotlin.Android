package com.example.tddtest.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tddtest.R
import com.example.tddtest.home.model.PostDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.viewModel

@Composable
fun HomeScreen() {
    val homeViewModel: HomeViewModel by viewModel()

    val state: HomeScreenContract.State = homeViewModel.viewState.value

    val effectFlow: Flow<HomeScreenContract.Effect> = homeViewModel.effect.consumeAsFlow()

    val scaffoldState: ScaffoldState = rememberScaffoldState()

    val LAUNCH_LISTEN_FOR_EFFECTS = "launch-listen-to-effects"

    val coroutineScope = rememberCoroutineScope()

    if (state.error != null) {
        LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
            effectFlow.collect { effect ->
                when (effect) {
                    is HomeScreenContract.Effect.OnError -> {
                        coroutineScope.launch {
                            val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                                message = state.error,
                                actionLabel = "Retry",
                                duration = SnackbarDuration.Indefinite,
                            )
                            when (snackbarResult) {
                                SnackbarResult.ActionPerformed -> homeViewModel.fetchPostsData()
                                else -> {}
                            }
                        }
                    }
                }
            }
        }
    }
    Scaffold(
        scaffoldState = scaffoldState
    ) {
        Column() {
            TopAppBar(title = { stringResource(id = R.string.app_name) })
            PostsList(postsList = state.postsListData)
            if (state.isLoading)
                LoadingBar()
        }
    }

}

@Composable
private fun PostsList(postsList: List<PostDto>) {
    LazyColumn {
        items(postsList) { item ->
            PostsRow(item)
        }
    }
}

@Composable
private fun PostsRow(item: PostDto) {
    Row(modifier = Modifier.animateContentSize()) {
        PostItemRowDetails(item)
    }
}

@Composable
private fun PostItemRowDetails(item: PostDto) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(7.dp),
        elevation = 6.dp
    ) {
        Column {
            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
                modifier = Modifier.padding(5.dp)
            )
            Text(
                text = item.body,
                fontSize = 14.sp,
                fontWeight = FontWeight.W600,
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}

@Composable
fun LoadingBar() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}
