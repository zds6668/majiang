package com.example.majiang

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // 主界面控件
    private lateinit var etPlayer1Name: EditText
    private lateinit var etPlayer2Name: EditText
    private lateinit var etPlayer3Name: EditText
    private lateinit var etPlayer4Name: EditText
    private lateinit var spinnerBottomScore: Spinner
    private lateinit var btnZimo: Button
    private lateinit var btnDianpao: Button
    private lateinit var tvTotalScores: TextView
    private lateinit var lvRounds: ListView
    private lateinit var currentRoundText: TextView

    // 数据：累计得分、局记录列表
    private val totalScores = IntArray(4) { 0 }
    private val currentRoundScores = IntArray(4) { 0 }
    private val roundRecords = mutableListOf<String>()
    private lateinit var roundsAdapter: ArrayAdapter<String>

    // 当前局数
    private var currentRound = 1

    // 已经胡牌的玩家（自摸和点炮的玩家共享）
    private val huPlayers = mutableSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化控件
        etPlayer1Name = findViewById(R.id.etPlayer1Name)
        etPlayer2Name = findViewById(R.id.etPlayer2Name)
        etPlayer3Name = findViewById(R.id.etPlayer3Name)
        etPlayer4Name = findViewById(R.id.etPlayer4Name)
        spinnerBottomScore = findViewById(R.id.spinnerBottomScore)
        btnZimo = findViewById(R.id.btnZimo)
        btnDianpao = findViewById(R.id.btnDianpao)
        tvTotalScores = findViewById(R.id.tvTotalScores)

        currentRoundText = findViewById(R.id.currentRound)

        // 设置默认玩家姓名
        etPlayer1Name.setText("玩家1")
        etPlayer2Name.setText("玩家2")
        etPlayer3Name.setText("玩家3")
        etPlayer4Name.setText("玩家4")

        // 底分下拉列表
        val bottomScores = listOf("1", "5", "10")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bottomScores)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBottomScore.adapter = spinnerAdapter

        // 局记录 ListView 适配器
        roundsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, roundRecords)

        // 按钮点击事件
        btnZimo.setOnClickListener { showZimoDialog() }
        btnDianpao.setOnClickListener { showDianpaoDialog() }

        // 显示初始累计得分
        updateTotalScoresDisplay()

        // 更新当前局数显示
        updateCurrentRoundDisplay()
    }

    // 更新累计得分显示
    private fun updateTotalScoresDisplay() {
        val players = getPlayerNames()
        tvTotalScores.text =
            "累计得分：\n${players[0]}: ${totalScores[0]}\n${players[1]}: ${totalScores[1]}\n${players[2]}: ${totalScores[2]}\n${players[3]}: ${totalScores[3]}"
    }

    // 获取当前玩家姓名
    private fun getPlayerNames(): List<String> {
        return listOf(
            etPlayer1Name.text.toString().ifEmpty { "玩家1" },
            etPlayer2Name.text.toString().ifEmpty { "玩家2" },
            etPlayer3Name.text.toString().ifEmpty { "玩家3" },
            etPlayer4Name.text.toString().ifEmpty { "玩家4" }
        )
    }

    // 更新当前局数显示
    private fun updateCurrentRoundDisplay() {
        currentRoundText.text = "当前局数：$currentRound"
    }

    // ---------------- 自摸计分 ----------------
// ---------------- 获取剩余玩家 ----------------
// 获取剩余未胡牌的玩家，保持原始的玩家下标与姓名匹配
    private fun getRemainingPlayers(): List<Pair<String, Int>> {
        val players = getPlayerNames() // 获取最新的玩家姓名
        return players
            .mapIndexed { index, playerName -> playerName to index }
            .filter { (name, index) -> !huPlayers.contains(index) } // 只保留未胡牌的玩家
    }

    // ---------------- 自摸计分 ----------------
    private fun showZimoDialog() {
        if (huPlayers.size >= 3) {
            Toast.makeText(this, "已经有3个玩家胡牌，自动开始下一局", Toast.LENGTH_SHORT).show()
            startNextRound(btnZimo) // 自动开始下一局
            return
        }
        val builder = AlertDialog.Builder(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_zimo, null)
        builder.setView(dialogView)

        val spinnerWinner = dialogView.findViewById<Spinner>(R.id.spinnerWinner)
        val btnFan1 = dialogView.findViewById<Button>(R.id.btnFan1)
        val btnFan2 = dialogView.findViewById<Button>(R.id.btnFan2)
        val btnFan3 = dialogView.findViewById<Button>(R.id.btnFan3)
        val btnFan4 = dialogView.findViewById<Button>(R.id.btnFan4)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirm)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        val remainingPlayers = getRemainingPlayers()
        val playerNames = remainingPlayers.map { it.first }
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, playerNames)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerWinner.adapter = spinnerAdapter

        var selectedFan = -1
        val fanButtons = listOf(btnFan1, btnFan2, btnFan3, btnFan4)

        fun clearFanSelection() {
            for (btn in fanButtons) {
                btn.setBackgroundResource(android.R.drawable.btn_default)
            }
        }

        btnFan1.setOnClickListener {
            clearFanSelection()
            btnFan1.setBackgroundResource(android.R.color.holo_blue_light)
            selectedFan = 1
        }
        btnFan2.setOnClickListener {
            clearFanSelection()
            btnFan2.setBackgroundResource(android.R.color.holo_blue_light)
            selectedFan = 2
        }
        btnFan3.setOnClickListener {
            clearFanSelection()
            btnFan3.setBackgroundResource(android.R.color.holo_blue_light)
            selectedFan = 3
        }
        btnFan4.setOnClickListener {
            clearFanSelection()
            btnFan4.setBackgroundResource(android.R.color.holo_blue_light)
            selectedFan = 4
        }

        val dialog = builder.create()

        btnConfirm.setOnClickListener {
            if (selectedFan == -1) {
                Toast.makeText(this, "请选择番数", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bottomScore = spinnerBottomScore.selectedItem.toString().toInt()
            val winnerName = spinnerWinner.selectedItem.toString()
            val winnerIndex = remainingPlayers.first { it.first == winnerName }.second

            if (huPlayers.contains(winnerIndex)) {
                Toast.makeText(this, "该玩家已经胡牌，无法自摸", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            huPlayers.add(winnerIndex)
            val winScore = bottomScore * selectedFan * (4 - huPlayers.size)
            val loseScore = bottomScore * selectedFan

            for (i in totalScores.indices) {
                if (i == winnerIndex) totalScores[i] += winScore
                else if (huPlayers.contains(i)) continue
                else totalScores[i] -= loseScore
            }

            currentRoundScores[winnerIndex] += winScore
            for (i in currentRoundScores.indices) {
                if (i != winnerIndex && !huPlayers.contains(i)) currentRoundScores[i] -= loseScore
            }

            // 获取扣分的玩家名称
            val losingPlayers = getRemainingPlayers().filter { it.second != winnerIndex && !huPlayers.contains(it.second) }
            val losingPlayerNames = losingPlayers.joinToString(", ") { it.first }

            val record = "自摸: $winnerName 自摸 $selectedFan 番，获得 +$winScore，其他各扣 -$loseScore (${losingPlayerNames})"
            roundRecords.add(record)
            roundsAdapter.notifyDataSetChanged()
            updateTotalScoresDisplay()

            // 检查是否有3个胡牌玩家
            if (huPlayers.size >= 3) {
                Toast.makeText(this, "已胡牌3人自动下一局", Toast.LENGTH_SHORT).show()
                startNextRound(btnZimo) // 自动开始下一局
            }

            dialog.dismiss()
        }

        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }




    // ---------------- 点炮计分 ----------------
// ---------------- 点炮计分 ----------------
    private fun showDianpaoDialog() {
        if (huPlayers.size >= 3) {
            Toast.makeText(this, "已经有3个玩家胡牌，自动开始下一局", Toast.LENGTH_SHORT).show()
            startNextRound(btnZimo) // 自动开始下一局
            return
        }
        val builder = AlertDialog.Builder(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_dianpao, null)
        builder.setView(dialogView)

        val spinnerWinner = dialogView.findViewById<Spinner>(R.id.spinnerWinner)
        val spinnerDianpao = dialogView.findViewById<Spinner>(R.id.spinnerDianpao)
        val btnFan1 = dialogView.findViewById<Button>(R.id.btnFan1)
        val btnFan2 = dialogView.findViewById<Button>(R.id.btnFan2)
        val btnFan3 = dialogView.findViewById<Button>(R.id.btnFan3)
        val btnFan4 = dialogView.findViewById<Button>(R.id.btnFan4)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirm)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        // 获取未胡牌玩家列表及其原始下标
        val remainingPlayers = getRemainingPlayers()

        // 提取玩家姓名，并创建适配器
        val playerNames = remainingPlayers.map { it.first }
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, playerNames)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerWinner.adapter = spinnerAdapter
        spinnerDianpao.adapter = spinnerAdapter

        // 番数选择：默认未选中
        var selectedFan = -1
        val fanButtons = listOf(btnFan1, btnFan2, btnFan3, btnFan4)
        fun clearFanSelection() {
            for (btn in fanButtons) {
                btn.setBackgroundResource(android.R.drawable.btn_default)
            }
        }
        btnFan1.setOnClickListener {
            clearFanSelection()
            btnFan1.setBackgroundResource(android.R.color.holo_blue_light)
            selectedFan = 1
        }
        btnFan2.setOnClickListener {
            clearFanSelection()
            btnFan2.setBackgroundResource(android.R.color.holo_blue_light)
            selectedFan = 2
        }
        btnFan3.setOnClickListener {
            clearFanSelection()
            btnFan3.setBackgroundResource(android.R.color.holo_blue_light)
            selectedFan = 3
        }
        btnFan4.setOnClickListener {
            clearFanSelection()
            btnFan4.setBackgroundResource(android.R.color.holo_blue_light)
            selectedFan = 4
        }

        val dialog = builder.create()

        btnConfirm.setOnClickListener {
            if (selectedFan == -1) {
                Toast.makeText(this, "请选择番数", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 获取胡牌和点炮玩家的姓名
            val winnerName = spinnerWinner.selectedItem.toString()
            val dianpaoName = spinnerDianpao.selectedItem.toString()

            // 通过姓名找到原始下标
            val winnerIndex = remainingPlayers.first { it.first == winnerName }.second
            val dianpaoIndex = remainingPlayers.first { it.first == dianpaoName }.second

            // 检查该玩家是否已胡牌
            if (huPlayers.contains(winnerIndex)) {
                Toast.makeText(this, "该玩家已经胡牌，无法点炮", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 检查胡牌玩家和点炮玩家不能相同
            if (winnerIndex == dianpaoIndex) {
                Toast.makeText(this, "胡牌玩家和点炮玩家不能相同", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 记录胡牌玩家
            huPlayers.add(winnerIndex)

            // 获取底分
            val bottomScore = spinnerBottomScore.selectedItem.toString().toInt()
            // 点炮计算：胡牌玩家加分 = 底分 × 番数；点炮玩家扣分 = 底分 × 番数
            val scoreChange = bottomScore * selectedFan

            // 更新累计得分
            totalScores[winnerIndex] += scoreChange
            totalScores[dianpaoIndex] -= scoreChange

            // 更新当前局得分
            currentRoundScores[winnerIndex] += scoreChange
            currentRoundScores[dianpaoIndex] -= scoreChange

            // 添加局记录
            val record = "点炮: $winnerName 胡牌，$dianpaoName 点炮，$selectedFan 番，$winnerName +$scoreChange, $dianpaoName -$scoreChange"
            roundRecords.add(record)
            roundsAdapter.notifyDataSetChanged() // 刷新记录显示
            updateTotalScoresDisplay()
            // 检查是否有3个胡牌玩家
            if (huPlayers.size >= 3) {
                Toast.makeText(this, "已胡牌3人自动下一局", Toast.LENGTH_SHORT).show()
                startNextRound(btnZimo) // 自动开始下一局
            }
            dialog.dismiss()
        }
        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }




    // ---------------- 开始下一局 ----------------
    fun startNextRound(view: android.view.View) {
        // 增加局数
        currentRound++
        updateCurrentRoundDisplay()

        // 记录当前局的得分
        val players = getPlayerNames()
        val record = "局 ${currentRound - 1} 得分：${players[0]}: ${currentRoundScores[0]}, ${players[1]}: ${currentRoundScores[1]}, ${players[2]}: ${currentRoundScores[2]}, ${players[3]}: ${currentRoundScores[3]}"
        roundRecords.add(record)
        roundRecords.add("")

        // 清除当前局的得分记录
        currentRoundScores.fill(0) // 清空当前局得分

        // 清除局记录
        roundsAdapter.notifyDataSetChanged()

        // 清除胡牌玩家记录
        huPlayers.clear()

        // 更新总得分显示
        updateTotalScoresDisplay()
    }

    // 处理点击"查看历史记录"按钮
    fun onViewHistory(view: android.view.View) {
        // 显示历史记录对话框
        val historyDialog = AlertDialog.Builder(this)
            .setTitle("历史记录")
            .setMessage(roundRecords.joinToString("\n"))
            .setPositiveButton("关闭") { dialog, _ -> dialog.dismiss() }
            .create()

        historyDialog.show()
    }
}
