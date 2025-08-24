package com.acnteam.crashybird.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.acnteam.crashybird.data.local.entity.LevelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LevelDao {
    @Query("SELECT * FROM levels ORDER BY levelNumber ASC")
    fun getAllLevels(): Flow<List<LevelEntity>>

    @Query("SELECT * FROM levels WHERE levelNumber = :levelNumber LIMIT 1")
    fun getLevelFlow(levelNumber: Int): Flow<LevelEntity?>

    @Query("SELECT * FROM levels WHERE levelNumber = :levelNumber LIMIT 1")
    suspend fun getLevel(levelNumber: Int): LevelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLevels(levels: List<LevelEntity>)

    @Query(
        """
        UPDATE levels
        SET isUnlocked = :isUnlocked
        WHERE levelNumber = :levelNumber
    """
    )
    suspend fun unlockLevel(levelNumber: Int, isUnlocked: Boolean)

    @Query(
        """
        UPDATE levels
        SET highScore = :highScore
        WHERE levelNumber = :levelNumber
    """
    )

    suspend fun updateHighScore(levelNumber: Int, highScore: Int)

    @Query("DELETE FROM levels")
    suspend fun deleteAllLevels()
}