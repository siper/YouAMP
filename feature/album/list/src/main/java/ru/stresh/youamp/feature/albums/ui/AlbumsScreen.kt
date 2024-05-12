package ru.stresh.youamp.feature.albums.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import ru.stresh.youamp.core.ui.Artwork
import ru.stresh.youamp.core.ui.OnBottomReached
import ru.stresh.youamp.core.ui.YouAmpPlayerTheme


@Composable
fun AlbumsScreen(
    viewModelStoreOwner: ViewModelStoreOwner,
    onAlbumClick: (id: String) -> Unit
) {
    val viewModel: AlbumsViewModel = koinViewModel(viewModelStoreOwner = viewModelStoreOwner)

    val state by viewModel.state.collectAsStateWithLifecycle()

    AlbumsScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onBottomReached = viewModel::loadMore,
        onAlbumClick = onAlbumClick
    )
}

@Composable
private fun AlbumsScreen(
    state: StateUi,
    onRefresh: () -> Unit,
    onBottomReached: () -> Unit,
    onAlbumClick: (id: String) -> Unit
) {
    val pullRefreshState = rememberPullToRefreshState()

    if (pullRefreshState.isRefreshing) {
        onRefresh()
    }

    if (pullRefreshState.isRefreshing && !state.isRefreshing) {
        pullRefreshState.endRefresh()
    }

    val listState = rememberLazyGridState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(pullRefreshState.nestedScrollConnection)
    ) {
        when (state) {
            is StateUi.Content -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = listState,
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                )
                {
                    items(
                        key = { it.id },
                        items = state.items
                    ) { album ->
                        AlbumItem(
                            album = album,
                            onAlbumClick = onAlbumClick
                        )
                    }
                }
                PullToRefreshContainer(
                    modifier = Modifier.align(Alignment.TopCenter),
                    state = pullRefreshState,
                )
            }

            is StateUi.Progress -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }


        listState.OnBottomReached {
            onBottomReached()
        }
    }
}

@Composable
private fun AlbumItem(
    album: AlbumUi,
    onAlbumClick: (id: String) -> Unit
) {
    ElevatedCard(
        shape = MaterialTheme.shapes.large,
        onClick = {
            onAlbumClick(album.id)
        }
    ) {
        Artwork(
            artworkUrl = album.artworkUrl,
            placeholder = Icons.Rounded.Album,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )
        Text(
            text = album.title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 12.dp, end = 12.dp),
            textAlign = TextAlign.Left,
            minLines = 1,
            maxLines = 1,
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = album.artist,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp, start = 12.dp, end = 12.dp, bottom = 8.dp),
            textAlign = TextAlign.Left,
            minLines = 1,
            maxLines = 1,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
@Preview
private fun AlbumsScreenPreview() {
    YouAmpPlayerTheme {
        val items = listOf(
            AlbumUi(
                id = "1",
                title = "Test",
                artist = "Test artist",
                artworkUrl = null
            ),
            AlbumUi(
                id = "2",
                title = "Test 2",
                artist = "Test artist 2 ",
                artworkUrl = null
            ),
            AlbumUi(
                id = "3",
                title = "Test 3",
                artist = "Test artist 3",
                artworkUrl = null
            )
        )
        val state = StateUi.Content(isRefreshing = true, items)
        AlbumsScreen(
            state = state,
            onRefresh = { },
            onBottomReached = { },
            onAlbumClick = { }
        )
    }
}