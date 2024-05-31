package ru.stresh.youamp.main.ui

import kotlinx.serialization.Serializable


@Serializable
object Main

@Serializable
data class AlbumInfo(val albumId: String)

@Serializable
object Player

@Serializable
data class ArtistInfo(val artistId: String)

@Serializable
object PlayQueue

@Serializable
object Search

@Serializable
object AddServer

@Serializable
object ServerList

@Serializable
data class ServerEditor(val serverId: Long)