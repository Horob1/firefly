package com.acnteam.crashybird.data.repository

import com.acnteam.crashybird.data.remote.dto.RankDto
import com.acnteam.crashybird.domain.model.Level
import com.acnteam.crashybird.domain.model.RankState
import com.acnteam.crashybird.domain.repository.RankRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class RankRepositoryImpl : RankRepository {
    companion object {
        private const val COLLECTION_NAME = "ranks"
        private const val LEVEL = "level"
        private const val SCORE = "score"
    }

    val auth = FirebaseAuth.getInstance()
    val store = Firebase.firestore

    override suspend fun getBestScore(level: Level): Int {
        return try {
            val snapshot = store.collection(COLLECTION_NAME)
                .whereEqualTo(LEVEL, level.levelNumber)
                .orderBy(SCORE, Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()

            if (snapshot.isEmpty) {
                0
            } else {
                val rankDto = snapshot.documents[0].toObject(RankDto::class.java)
                rankDto?.score ?: 0
            }
        } catch (e: Exception) {
            0
        }
    }


    override suspend fun upDateGameScore(
        score: Int,
        level: Level,
    ): Result<Unit> {
        return try {
            store.collection(COLLECTION_NAME)
                .document("${auth.currentUser!!.uid}_${level.levelNumber}")
                .set(
                    RankDto(
                        level = level.levelNumber,
                        username = auth.currentUser!!.displayName!!,
                        score = score,
                    )
                )
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchRank(): List<RankState> {
        val ranks = mutableListOf<RankState>()
        for (level in Level.getAll()) {
            try {
                val snapshot = store.collection(COLLECTION_NAME)
                    .whereEqualTo(LEVEL, level.levelNumber)
                    .orderBy(SCORE, Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .await()

                val default = RankState(
                    level,
                    "NO USER",
                    0
                )

                if (snapshot.isEmpty) {
                    ranks.add(
                        default
                    )
                    continue
                }
                val rankDto = snapshot.documents[0].toObject(RankDto::class.java)

                if (rankDto != null) {
                    ranks.add(
                        default
                    )
                }

                ranks.add(
                    RankState(
                        level,
                        rankDto!!.username,
                        rankDto.score
                    )
                )
            } catch (e: Exception) {
                ranks.add(
                    RankState(
                        level,
                        "NO USER",
                        0
                    )
                )
            }
        }
        return ranks
    }
}