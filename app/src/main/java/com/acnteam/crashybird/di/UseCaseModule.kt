package com.acnteam.crashybird.di

import com.acnteam.crashybird.domain.usecase.AddCoinUseCase
import com.acnteam.crashybird.domain.usecase.CompleteOnBoardingUseCase
import com.acnteam.crashybird.domain.usecase.GetBackgroundMusicSettingsUseCase
import com.acnteam.crashybird.domain.usecase.GetBestScoreUseCase
import com.acnteam.crashybird.domain.usecase.GetCharacterListUseCase
import com.acnteam.crashybird.domain.usecase.GetCoinUseCase
import com.acnteam.crashybird.domain.usecase.GetCurrentCharacterUseCase
import com.acnteam.crashybird.domain.usecase.GetCurrentLevelUseCase
import com.acnteam.crashybird.domain.usecase.GetFirstTimeInHomeState
import com.acnteam.crashybird.domain.usecase.GetLevelStateListUseCase
import com.acnteam.crashybird.domain.usecase.GetOnBoardingStateUseCase
import com.acnteam.crashybird.domain.usecase.GetRanksUseCase
import com.acnteam.crashybird.domain.usecase.GetSoundEffectSettingsUseCase
import com.acnteam.crashybird.domain.usecase.InitDataUseCase
import com.acnteam.crashybird.domain.usecase.SaveFirstTimeInHomeState
import com.acnteam.crashybird.domain.usecase.SetBackgroundMusicSettingsUseCase
import com.acnteam.crashybird.domain.usecase.SetCurrentCharacterUseCase
import com.acnteam.crashybird.domain.usecase.SetCurrentLevelUseCase
import com.acnteam.crashybird.domain.usecase.SetSoundEffectSettingsUseCase
import com.acnteam.crashybird.domain.usecase.SyncDownUseCase
import com.acnteam.crashybird.domain.usecase.SyncToServerUseCase
import com.acnteam.crashybird.domain.usecase.UnlockCharacterUseCase
import com.acnteam.crashybird.domain.usecase.UpdateLevelUseCase
import com.acnteam.crashybird.domain.usecase.UpdatePlaySectionUseCase
import com.acnteam.crashybird.domain.usecase.WithDrawCoinUseCase
import com.acnteam.crashybird.presentation.screen.play.component.SoundEffectPlayer
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

    single {
        GetCharacterListUseCase(get())
    }

    single {
        GetCurrentCharacterUseCase(get())
    }

    single {
        SetCurrentCharacterUseCase(get())
    }

    single {
        UnlockCharacterUseCase(get())
    }

    single {
        WithDrawCoinUseCase(get())
    }

    single {
        SyncToServerUseCase(get(), get(), get(), get())
    }

    single {
        SyncDownUseCase(get(), get(), get(), get())
    }

    single {
        GetRanksUseCase(get())
    }

    single {
        GetFirstTimeInHomeState(get())
    }

    single {
        SaveFirstTimeInHomeState(get())
    }
    single { SoundEffectPlayer(get()) }

    single {
        AddCoinUseCase(get())
    }

    single {
        UpdateLevelUseCase(get())
    }
    single {
        GetBestScoreUseCase(get())
    }
    single {
        UpdatePlaySectionUseCase(get())
    }
}