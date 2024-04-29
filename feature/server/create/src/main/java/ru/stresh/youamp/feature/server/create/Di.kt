package ru.stresh.youamp.feature.server.create

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.stresh.youamp.feature.server.create.data.ServerRepositoryImpl
import ru.stresh.youamp.feature.server.create.domain.ServerRepository
import ru.stresh.youamp.feature.server.create.ui.ServerCreateViewModel

val serverCreateModule = module {
    factory<ServerRepository> { ServerRepositoryImpl(get()) }
    viewModel { (serverId: Long?) ->
        ServerCreateViewModel(serverId, get())
    }
}