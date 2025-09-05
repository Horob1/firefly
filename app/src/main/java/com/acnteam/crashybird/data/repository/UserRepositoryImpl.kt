package com.acnteam.crashybird.data.repository

import android.util.Log
import com.acnteam.crashybird.data.remote.dto.CharacterDto
import com.acnteam.crashybird.data.remote.dto.ItemDto
import com.acnteam.crashybird.data.remote.dto.LevelDto
import com.acnteam.crashybird.data.remote.dto.UserDto
import com.acnteam.crashybird.domain.model.Character
import com.acnteam.crashybird.domain.model.CharacterState
import com.acnteam.crashybird.domain.model.ItemState
import com.acnteam.crashybird.domain.model.Items
import com.acnteam.crashybird.domain.model.Level
import com.acnteam.crashybird.domain.model.LevelState
import com.acnteam.crashybird.domain.repository.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await


class UserRepositoryImpl : UserRepository {
    companion object {
        private const val COLLECTION_NAME = "users"

    }

    val auth = FirebaseAuth.getInstance()
    val store = Firebase.firestore

    override suspend fun syncLocalToServer(
        coin: Int,
        levels: List<LevelState>,
        chars: List<CharacterState>,
        items: List<ItemState>,
    ): Result<Unit> {
        return try {
            val levelList = levels.map {
                LevelDto(
                    levelNumber = it.level.levelNumber,
                    isUnlocked = it.isUnlocked,
                    highScore = it.highestScore,
                )
            }
            val characterList = chars.map {
                CharacterDto(
                    name = it.character.name,
                    isUnlocked = it.isUnlocked,
                )
            }
            val itemList = items.map {
                ItemDto(
                    name = it.item.name,
                    isUnlocked = it.isUnlocked,
                )
            }



            store.collection(COLLECTION_NAME)
                .document(auth.currentUser!!.uid)
                .set(
                    UserDto(
                        coin = coin,
                        levels = levelList,
                        characters = characterList,
                        items = itemList
                    )
                )
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun getUserDataFromServer(): Pair<Pair<Int, List<LevelState>>, Pair<List<CharacterState>, List<ItemState>>> {
        val default = Pair(
            Pair(
                0,
                Level.getAll()
                    .map { LevelState(it, isUnlocked = it.levelNumber == 1, highestScore = 0) }
            ),
            Pair(
                Character.getAll()
                    .map { CharacterState(it, isUnlocked = it.name == Character.Jack.name) },
                Items.getAll()
                    .map { ItemState(it, isUnlocked = it.name == Items.ChildFirefly.name) }
            )

        )
        return try {
            val snapshot = store.collection(COLLECTION_NAME)
                .document(auth.currentUser!!.uid)
                .get()
                .await()

            if (!snapshot.exists()) {
                return default
            }

            val userDto = snapshot.toObject(UserDto::class.java)

            if (userDto == null || userDto.levels.isEmpty() || userDto.characters.isEmpty() || userDto.items.isEmpty()) {
                return default
            }

            Log.d("UserRepositoryImpl", "getUserDataFromServer: $userDto")

            Pair(
                Pair(
                    userDto.coin,
                    userDto.levels.map {
                        LevelState(
                            Level.getByLevelNumber(it.levelNumber),
                            isUnlocked = it.isUnlocked,
                            highestScore = it.highScore
                        )
                    }
                ),
                Pair(
                    userDto.characters.map {
                        CharacterState(
                            Character.getByName(it.name),
                            isUnlocked = it.isUnlocked
                        )
                    },
                    userDto.items.map {
                        ItemState(
                            Items.getByName(it.name),
                            isUnlocked = it.isUnlocked
                        )
                    }
                )
            )
        } catch (e: Exception) {
            Log.e("UserRepositoryImpl", "getUserDataFromServer: ${e.message}")
            e.printStackTrace()
            // fallback nếu lỗi
            default
        }
    }
}