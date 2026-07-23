package com.acnteam.crashybird.domain.usecase

import com.acnteam.crashybird.data.local.entity.ItemEntity
import com.acnteam.crashybird.domain.local.CharacterLocalDataSource
import com.acnteam.crashybird.domain.local.ItemLocalDataSource
import com.acnteam.crashybird.domain.local.LevelLocalDataSource
import com.acnteam.crashybird.domain.model.Character
import com.acnteam.crashybird.domain.repository.GameRepository
import com.acnteam.crashybird.domain.repository.UserRepository

class SyncDownUseCase(
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository,
    private val levelLocalDataSource: LevelLocalDataSource,
    private val characterLocalDataSource: CharacterLocalDataSource,
    private val itemLocalDataSource: ItemLocalDataSource,
) {
    suspend operator fun invoke() {
        val data = userRepository.getUserDataFromServer()
        gameRepository.setCoin(data.first.first)

        // Đảm bảo tính nhất quán: Nếu Level N được mở khóa, tất cả Level < N cũng phải được mở khóa
        val remoteLevels = data.first.second
        val maxUnlockedLevel = remoteLevels.filter { it.isUnlocked }.maxOfOrNull { it.level.levelNumber } ?: 1
        
        val consistentLevels = remoteLevels.map { state ->
            // Luôn mở khóa level 1 và các level trước level cao nhất đã mở
            if (state.level.levelNumber <= maxUnlockedLevel || state.level.levelNumber == 1) {
                state.copy(isUnlocked = true)
            } else {
                state
            }
        }

        levelLocalDataSource.setLevelList(consistentLevels)

        // Đảm bảo nhân vật mặc định Jack luôn được mở khóa khi sync về
        val remoteChars = data.second.first
        val consistentChars = remoteChars.map { state ->
            if (state.character.name == Character.Jack.name) {
                state.copy(isUnlocked = true)
            } else {
                state
            }
        }
        characterLocalDataSource.setCharacterList(consistentChars)
        itemLocalDataSource.saveItems(data.second.second.map {
            ItemEntity(
                name = it.item.name,
                isUnlocked = it.isUnlocked
            )
        }
        )

    }
}