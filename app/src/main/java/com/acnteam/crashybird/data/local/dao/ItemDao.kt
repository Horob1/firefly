package com.acnteam.crashybird.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.acnteam.crashybird.data.local.entity.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    // Get flow
    @Query("SELECT * FROM items")
    fun getAllItems(): Flow<List<ItemEntity>>

    //Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ItemEntity>)

    //Delete
    @Query("DELETE FROM items")
    suspend fun deleteAllItems()

    //Unlock
    @Query(
        """
        UPDATE items
        SET isUnlocked = :isUnlocked
        WHERE name = :name
    """
    )
    suspend fun unlockItem(name: String, isUnlocked: Boolean)


}