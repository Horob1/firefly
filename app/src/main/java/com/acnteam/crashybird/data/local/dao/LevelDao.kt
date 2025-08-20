package com.acnteam.crashybird.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.acnteam.crashybird.data.local.entity.LevelEntity

@Dao
interface LevelDao {
    @Query("SELECT * FROM levels ORDER BY levelNumber ASC")
    suspend fun getAllLevels(): List<LevelEntity>

    @Query("SELECT * FROM levels WHERE levelNumber = :levelNumber LIMIT 1")
    suspend fun getLevel(levelNumber: Int): LevelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLevels(levels: List<LevelEntity>)

    @Update
    suspend fun updateLevel(level: LevelEntity)
}