package com.example.majiang.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_sessions")
data class GameSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val round: Int,
    val playerScores: String,  // 序列化后的玩家得分
    val actions: String        // 序列化后的操作记录
)
