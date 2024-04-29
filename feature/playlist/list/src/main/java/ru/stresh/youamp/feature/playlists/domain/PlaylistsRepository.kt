package ru.stresh.youamp.feature.playlists.domain

import kotlinx.coroutines.flow.Flow

internal interface PlaylistsRepository {
    suspend fun getPlaylists(): Flow<List<Playlist>>
}