package ru.stersh.youamp.feature.player.mini

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.stersh.youamp.feature.player.mini.ui.MiniPlayerViewModel

val playerMiniModule = module {
    viewModel { MiniPlayerViewModel(get(), get(), get(), get()) }
}