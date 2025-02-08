package com.example.majiang

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.majiang.database.AppDatabase
import com.example.majiang.database.GameSessionEntity
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private lateinit var historyListView: ListView
    private lateinit var gameSessions: List<GameSessionEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        historyListView = findViewById(R.id.history_list)

        // 使用协程从数据库加载历史记录
        lifecycleScope.launch {
            gameSessions = AppDatabase.getDatabase(applicationContext).gameSessionDao().getAllSessions()

            val adapter = ArrayAdapter(this@HistoryActivity, android.R.layout.simple_list_item_1, gameSessions.map {
                "第${it.round}局 - 得分: ${it.playerScores}"
            })
            historyListView.adapter = adapter
        }
    }
}
