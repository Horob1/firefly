package com.acnteam.crashybird.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.acnteam.crashybird.data.local.entity.CharacterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters")
    fun getAllCharacters(): Flow<List<CharacterEntity>>


    @Query("SELECT * FROM characters WHERE name = :name LIMIT 1")
    suspend fun getCharacter(name: String): CharacterEntity?

    @Query("UPDATE characters SET isUnlocked = :isUnlocked WHERE name = :name")
    suspend fun updateCharacter(name: String, isUnlocked: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<CharacterEntity>)

    @Query("DELETE FROM characters")
    suspend fun deleteAllCharacters()
}