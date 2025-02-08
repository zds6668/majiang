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

    // 自摸玩家顺序（每局最多4个自摸玩家）
    private val zimoOrder = mutableListOf<Int>()

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
        lvRounds = findViewById(R.id.lvRounds)
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
        lvRounds.adapter = roundsAdapter

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
// ---------------- 自摸计分 ----------------
    private fun showZimoDialog() {
        // 检查是否已有3个玩家自摸
        if (zimoOrder.size >= 3) {
            Toast.makeText(this, "已经有3个玩家自摸，不能再自摸", Toast.LENGTH_SHORT).show()
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

        // 使用主界面当前的玩家姓名填充 spinner
        val players = getPlayerNames()
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, players)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerWinner.adapter = spinnerAdapter

        // 番数选择：默认未选中（-1 表示未选择），点击后改变背景颜色以示选中
        var selectedFan = -1
        val fanButtons = listOf(btnFan1, btnFan2, btnFan3, btnFan4)
        fun clearFanSelection() {
            for (btn in fanButtons) {
                btn.setBackgroundResource(android.R.drawable.btn_default)
            }
        }

        // 为每个按钮设置点击事件
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
            // 获取底分
            val bottomScore = spinnerBottomScore.selectedItem.toString().toInt()
            // 自摸计算：胡牌玩家加分 = 底分 × 番数 × 3；其他玩家各扣 底分 × 番数
            val winnerIndex = spinnerWinner.selectedItemPosition
            val winScore = bottomScore * selectedFan * 3
            val loseScore = bottomScore * selectedFan

            // 更新累计得分
            for (i in totalScores.indices) {
                totalScores[i] += if (i == winnerIndex) winScore else -loseScore
            }

            // 更新当前局得分
            currentRoundScores[winnerIndex] += winScore
            for (i in currentRoundScores.indices) {
                if (i != winnerIndex) currentRoundScores[i] -= loseScore
            }

            // 添加局记录
            val record = "自摸: ${players[winnerIndex]} 自摸 $selectedFan 番，获得 +$winScore，其他各扣 -$loseScore"
            roundRecords.add(record)
            roundsAdapter.notifyDataSetChanged() // 刷新记录显示
            updateTotalScoresDisplay()

            dialog.dismiss()
        }
        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }


    // ---------------- 点炮计分 ----------------
    private fun showDianpaoDialog() {
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

        // 使用当前玩家姓名填充 spinner
        val players = getPlayerNames()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, players)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerWinner.adapter = adapter
        spinnerDianpao.adapter = adapter

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
            // 获取胡牌和点炮玩家索引
            val winnerIndex = spinnerWinner.selectedItemPosition
            val dianpaoIndex = spinnerDianpao.selectedItemPosition
            if (winnerIndex == dianpaoIndex) {
                Toast.makeText(this, "胡牌玩家和点炮玩家不能相同", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // 获取底分
            val bottomScore = spinnerBottomScore.selectedItem.toString().toInt()
            // 点炮计算：胡牌玩家加分 = 底分 × 番数；点炮玩家扣分 = 底分 × 番数
            val scoreChange = bottomScore * selectedFan

            totalScores[winnerIndex] += scoreChange
            totalScores[dianpaoIndex] -= scoreChange

            // 更新当前局得分
            currentRoundScores[winnerIndex] += scoreChange
            currentRoundScores[dianpaoIndex] -= scoreChange

            // 更新局记录
            val record = "点炮: ${players[winnerIndex]} 胡牌，${players[dianpaoIndex]} 点炮，$selectedFan 番，${players[winnerIndex]} +$scoreChange, ${players[dianpaoIndex]} -$scoreChange"
            roundRecords.add(record)
            roundsAdapter.notifyDataSetChanged() // 刷新记录显示
            updateTotalScoresDisplay()

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

        // 清除当前局的得分记录
        currentRoundScores.fill(0) // 清空当前局得分

        // 清除局记录
        roundsAdapter.notifyDataSetChanged()

        // 清除自摸玩家顺序
        zimoOrder.clear()

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
