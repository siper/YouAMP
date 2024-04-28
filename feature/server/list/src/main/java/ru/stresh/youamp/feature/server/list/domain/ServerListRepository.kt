package ru.stresh.youamp.feature.server.list.domain

import kotlinx.coroutines.flow.Flow

internal interface ServerListRepository {

    fun getServerList(): Flow<List<Server>>

    suspend fun setActiveServer(serverId: Long)
}