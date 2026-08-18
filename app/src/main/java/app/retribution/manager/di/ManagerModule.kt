package app.retribution.manager.di

import app.retribution.manager.domain.manager.DownloadManager
import app.retribution.manager.domain.manager.InstallManager
import app.retribution.manager.domain.manager.PreferenceManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val managerModule = module {
    singleOf(::DownloadManager)
    singleOf(::PreferenceManager)
    singleOf(::InstallManager)
}