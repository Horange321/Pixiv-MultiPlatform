package top.kagg886.pmf.ui.route.welcome

import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.core.model.ScreenModel
import com.russhwolf.settings.Settings
import com.russhwolf.settings.boolean
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import top.kagg886.pmf.backend.SystemConfig
import top.kagg886.pmf.ui.util.container

class WelcomeModel(
    settings: Settings = SystemConfig.getConfig()
) : ContainerHost<WelcomeViewState, WelcomeSideEffect>, ViewModel(), ScreenModel {

    private var isInited by settings.boolean("welcome_init", false)

    override val container: Container<WelcomeViewState, WelcomeSideEffect> = container(WelcomeViewState.Loading) {
        if (isInited) {
            postSideEffect(WelcomeSideEffect.NavigateToMain)
            return@container
        }
    }

    @OptIn(OrbitExperimental::class)
    fun nextStep() = intent {
        runOn<WelcomeViewState.ConfigureSetting> {
            isInited = true
            postSideEffect(WelcomeSideEffect.NavigateToMain)
        }

        runOn<WelcomeViewState.Welcome> {
            reduce {
                WelcomeViewState.ConfigureSetting
            }
        }
    }
}

sealed class WelcomeViewState {
    data object Loading : WelcomeViewState()
    data object Welcome : WelcomeViewState()
    data object ConfigureSetting: WelcomeViewState()
}

sealed class WelcomeSideEffect {
    data object NavigateToMain : WelcomeSideEffect()
}