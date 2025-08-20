package com.acnteam.crashybird.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.acnteam.crashybird.data.local.AppDatabase
import com.acnteam.crashybird.data.local.entity.LevelEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(get(), AppDatabase::class.java, "game_db")
            .addCallback(
                object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = get<AppDatabase>().levelDao
                            val existing = dao.getAllLevels()
                            if (existing.isEmpty()) {
                                val levels = (1..5).map { i ->
                                    LevelEntity(
                                        levelNumber = i,
                                        isUnlocked = i == 1,
                                    )
                                }
                                dao.insertLevels(levels)
                            }
                        }
                    }
                }
            )
            .build()
    }
}