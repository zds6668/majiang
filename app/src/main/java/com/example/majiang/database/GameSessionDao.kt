package com.example.majiang.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameSessionDao {

    @Insert
    suspend fun insert(session: GameSessionEntity)

    @Query("SELECT * FROM game_sessions")
    suspend fun getAllSessions(): List<GameSessionEntity>
}
