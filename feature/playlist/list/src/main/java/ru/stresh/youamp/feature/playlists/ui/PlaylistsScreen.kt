package ru.stresh.youamp.feature.playlists.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import org.koin.androidx.compose.koinViewModel
import ru.stresh.youamp.core.ui.OnBottomReached
import ru.stresh.youamp.core.ui.YouAmpPlayerTheme


@Composable
fun PlaylistsScreen(onPlaylistClick: (id: String) -> Unit) {
    val viewModel: PlaylistsViewModel = koinViewModel()

    val state by viewModel.state.collectAsStateWithLifecycle()

    PlaylistsScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onBottomReached = viewModel::loadMore,
        onPlaylistClick = onPlaylistClick
    )
}

@Composable
private fun PlaylistsScreen(
    state: PlaylistsViewModel.StateUi,
    onRefresh: () -> Unit,
    onBottomReached: () -> Unit,
    onPlaylistClick: (id: String) -> Unit
) {
    val isRefreshing by rememberSaveable { mutableStateOf(false) }
    val pullRefreshState = rememberPullToRefreshState(
        enabled = { isRefreshing }
    )

    if (pullRefreshState.isRefreshing) {
        onRefresh()
    }

    if (pullRefreshState.isRefreshing && !isRefreshing) {
        pullRefreshState.endRefresh()
    }

    val listState = rememberLazyGridState()
    Scaffold(
        modifier = Modifier.nestedScroll(pullRefreshState.nestedScrollConnection)
    ) { padding ->
        Box(
            modifier = Modifier
                .nestedScroll(pullRefreshState.nestedScrollConnection)
                .padding(padding)
        ) {
            when (state) {
                is PlaylistsViewModel.StateUi.Content -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    )
                    {
                        items(
                            items = state.items
                        ) { playlist ->
                            PlaylistItem(
                                playlist = playlist,
                                onPlaylistClick = onPlaylistClick
                            )
                        }
                    }
                    PullToRefreshContainer(
                        modifier = Modifier.align(Alignment.TopCenter),
                        state = pullRefreshState,
                    )
                }

                is PlaylistsViewModel.StateUi.Progress -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

        listState.OnBottomReached {
            onBottomReached()
        }
    }
}

@Composable
private fun PlaylistItem(
    playlist: PlaylistUi,
    onPlaylistClick: (id: String) -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable {
                onPlaylistClick(playlist.id)
            },
    ) {
        SubcomposeAsyncImage(
            model = playlist.artworkUrl,
            contentDescription = "Album image",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            loading = {
                Image(
                    imageVector = Icons.Rounded.MusicNote,
                    contentDescription = "placeholder"
                )
            }
        )
        Text(
            text = playlist.name,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            textAlign = TextAlign.Left,
            minLines = 1,
            maxLines = 1,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
@Preview
private fun PlaylistsScreenPreview() {
    YouAmpPlayerTheme {
        val items = listOf(
            PlaylistUi(
                id = "1",
                name = "Test",
                artworkUrl = null
            ),
            PlaylistUi(
                id = "2",
                name = "Test 2",
                artworkUrl = null
            ),
            PlaylistUi(
                id = "3",
                name = "Test 3",
                artworkUrl = null
            )
        )
        val state = PlaylistsViewModel.StateUi.Content(isRefreshing = true, items)
        PlaylistsScreen(
            state = state,
            onRefresh = { },
            onBottomReached = { },
            onPlaylistClick = { }
        )
    }
}