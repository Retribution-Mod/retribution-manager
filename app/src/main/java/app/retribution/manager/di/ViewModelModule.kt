package app.retribution.manager.di

import app.retribution.manager.ui.viewmodel.home.HomeViewModel
import app.retribution.manager.ui.viewmodel.installer.InstallerViewModel
import app.retribution.manager.ui.viewmodel.installer.LogViewerViewModel
import app.retribution.manager.ui.viewmodel.libraries.LibrariesViewModel
import app.retribution.manager.ui.viewmodel.settings.AdvancedSettingsViewModel
import app.retribution.manager.utils.DiscordVersion
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val viewModelModule = module {
    factory { (discordVersion: DiscordVersion, customModUrl: String?) ->
        InstallerViewModel(get(), get(), discordVersion, customModUrl)
    }
    factoryOf(::AdvancedSettingsViewModel)
    factoryOf(::HomeViewModel)
    factoryOf(::LogViewerViewModel)
    factoryOf(::LibrariesViewModel)
}