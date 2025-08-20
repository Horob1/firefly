package com.acnteam.crashybird.di

import com.acnteam.crashybird.domain.usecase.CompleteOnBoardingUseCase
import com.acnteam.crashybird.domain.usecase.GetBackgroundMusicSettingsUseCase
import com.acnteam.crashybird.domain.usecase.GetCoinUseCase
import com.acnteam.crashybird.domain.usecase.GetCurrentLevelUseCase
import com.acnteam.crashybird.domain.usecase.GetLevelStateListUseCase
import com.acnteam.crashybird.domain.usecase.GetOnBoardingStateUseCase
import com.acnteam.crashybird.domain.usecase.GetSoundEffectSettingsUseCase
import com.acnteam.crashybird.domain.usecase.InitDataUseCase
import com.acnteam.crashybird.domain.usecase.SetBackgroundMusicSettingsUseCase
import com.acnteam.crashybird.domain.usecase.SetCurrentLevelUseCase
import com.acnteam.crashybird.domain.usecase.SetSoundEffectSettingsUseCase
import org.koin.dsl.module

val useCaseModule = module {

    single {
        GetOnBoardingStateUseCase(get())
    }

    single {
        CompleteOnBoardingUseCase(get())
    }

    single {
        GetBackgroundMusicSettingsUseCase(get())
    }

    single {
        GetSoundEffectSettingsUseCase(get())
    }

    single {
        SetBackgroundMusicSettingsUseCase(get())
    }

    single {
        SetSoundEffectSettingsUseCase(get())
    }

    single {
        GetLevelStateListUseCase(get())
    }

    single {
        InitDataUseCase(
            get(), get()
        )
    }

    single {
        GetCoinUseCase(get())
    }

    single {
        GetCurrentLevelUseCase(get())
    }

    single {
        SetCurrentLevelUseCase(get())
    }
}