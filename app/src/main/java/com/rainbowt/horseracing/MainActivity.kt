package com.rainbowt.horseracing

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var buttonStartRace: Button
    private lateinit var horseTexts: Array<TextView>
    private lateinit var horseProgressBars: Array<ProgressBar>
    private lateinit var handler: Handler
    private lateinit var random: Random
    // 標記賽馬比賽是否已經開始
    private var isRaceStarted: Boolean = false
    // 賽馬顏色
    private val horseColors = arrayOf(Color.RED, Color.rgb(255, 165, 0), Color.YELLOW, Color.GREEN, Color.BLUE, Color.rgb(128, 0, 128))
    // 記錄已使用的顏色
    private val usedColors = mutableSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonStartRace = findViewById(R.id.button_start_race)
        horseTexts = arrayOf(text_view_horse1, text_view_horse2, text_view_horse3, text_view_horse4, text_view_horse5, text_view_horse6)
        horseProgressBars = arrayOf(progress_bar_horse1, progress_bar_horse2, progress_bar_horse3, progress_bar_horse4, progress_bar_horse5, progress_bar_horse6)

        random = Random()
        isRaceStarted = false

        handler = Handler(Handler.Callback {
            if (it.what == 0) {
                val horseIndex = it.arg1
                val distance = it.arg2

                // 更新賽馬進度條
                horseProgressBars[horseIndex].progress = distance

                // 若賽馬到達終點，則設定賽馬比賽狀態為未開始並啟用按鈕
                if (distance >= 100) {
                    isRaceStarted = false
                    buttonStartRace.isEnabled = true
                }
            }
            true
        })

        buttonStartRace.setOnClickListener {
            if (!isRaceStarted) {
                isRaceStarted = true
                buttonStartRace.isEnabled = false

                initHorseColor()

                startRace()
            }
        }
    }

    private fun initHorseColor() {
        horseProgressBars.forEach { progressBar ->
            var color = horseColors[random.nextInt(horseColors.size)]
            while (usedColors.contains(color)) {
                color = horseColors[random.nextInt(horseColors.size)]
            }
            usedColors.add(color)
            progressBar.progressTintList = ColorStateList.valueOf(color)
        }
    }

    private fun startRace() {
        // 開始一個新的執行緒來模擬賽馬比賽
        Thread {
            // 用於儲存每匹賽馬的距離
            val distances = IntArray(6)

            // 當賽馬比賽正在進行中時
            while (isRaceStarted) {
                // 遍歷每匹賽馬
                for (i in 0 until 6) {
                    // 隨機增加賽馬距離
                    distances[i] += random.nextInt(5) + 1

                    // 建立一個消息用於更新UI
                    val message = handler.obtainMessage(0)
                    message.arg1 = i                // 設定賽馬索引
                    message.arg2 = distances[i]     // 設定賽馬距離
                    handler.sendMessage(message)    // 發送消息給 Handler 進行處理

                    try {
                        Thread.sleep(200)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }.start()
    }
}
