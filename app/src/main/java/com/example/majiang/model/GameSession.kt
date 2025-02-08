package com.example.majiang.model

data class GameSession(
    val round: Int,  // 记录是哪一局
    val playerScores: Map<String, Int>, // 玩家得分
    val actions: List<String> // 操作记录，如：自摸、点炮
)
