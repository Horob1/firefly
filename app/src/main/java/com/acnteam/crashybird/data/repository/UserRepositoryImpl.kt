package com.acnteam.crashybird.data.repository

import android.util.Log
import com.acnteam.crashybird.data.remote.dto.CharacterDto
import com.acnteam.crashybird.data.remote.dto.LevelDto
import com.acnteam.crashybird.data.remote.dto.UserDto
import com.acnteam.crashybird.domain.model.Character
import com.acnteam.crashybird.domain.model.CharacterState
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



            store.collection(COLLECTION_NAME)
                .document(auth.currentUser!!.uid)
                .set(
                    UserDto(
                        coin = coin,
                        levels = levelList,
                        characters = characterList,
                    )
                )
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun getUserDataFromServer(): Triple<Int, List<LevelState>, List<CharacterState>> {
        val default = Triple(
            0,
            Level.getAll()
                .map { LevelState(it, isUnlocked = it.levelNumber == 1, highestScore = 0) },
            Character.getAll()
                .map { CharacterState(it, isUnlocked = Character.Jack.name == it.name) }
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

            if (userDto == null || userDto.levels.isEmpty() || userDto.characters.isEmpty()) {
                return default
            }

            Log.d("UserRepositoryImpl", "getUserDataFromServer: $userDto")

            Triple(
                userDto.coin,
                userDto.levels.map {
                    LevelState(
                        Level.getByLevelNumber(it.levelNumber),
                        isUnlocked = it.isUnlocked,
                        highestScore = it.highScore
                    )
                },
                userDto.characters.map {
                    CharacterState(
                        Character.getByName(it.name),
                        isUnlocked = it.isUnlocked
                    )
                }
            )
        } catch (e: Exception) {
            Log.e("UserRepositoryImpl", "getUserDataFromServer: ${e.message}")
            e.printStackTrace()
            // fallback nếu lỗi
            default
        }
    }
}