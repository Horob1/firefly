package com.acnteam.crashybird.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.acnteam.crashybird.data.local.dao.CharacterDao
import com.acnteam.crashybird.data.local.dao.ItemDao
import com.acnteam.crashybird.data.local.dao.LevelDao
import com.acnteam.crashybird.data.local.entity.CharacterEntity
import com.acnteam.crashybird.data.local.entity.ItemEntity
import com.acnteam.crashybird.data.local.entity.LevelEntity

@Database(
    entities = [
        LevelEntity::class,
        CharacterEntity::class,
        ItemEntity::class
    ],
    version = 1
)

abstract class AppDatabase : RoomDatabase() {
    abstract val levelDao: LevelDao
    abstract val characterDao: CharacterDao

    abstract val itemDao: ItemDao
}