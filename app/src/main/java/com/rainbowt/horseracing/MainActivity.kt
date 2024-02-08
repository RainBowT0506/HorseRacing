package com.rainbowt.horseracing

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var buttonStartRace: Button
    private lateinit var horseTexts: Array<TextView>
    private lateinit var horseProgressBars: Array<ProgressBar>

    //  用於更新賽馬 UI
    private lateinit var horseHandler: Handler
    private lateinit var random: Random

    // 標記賽馬比賽是否已經開始
    private var isRaceStarted: Boolean = false

    // 賽馬顏色
    private val horseColors = arrayOf(
        Color.RED,
        Color.rgb(255, 165, 0),
        Color.YELLOW,
        Color.GREEN,
        Color.BLUE,
        Color.rgb(128, 0, 128)
    )

    // 記錄已使用的顏色
    private val usedColors = mutableSetOf<Int>()

    // 賽馬主持人消息
    private lateinit var hostMessageTextView: TextView

    //  賽馬主持人的 Handler
    private lateinit var hostHandler: Handler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonStartRace = findViewById(R.id.button_start_race)
        horseTexts = arrayOf(
            text_view_horse1,
            text_view_horse2,
            text_view_horse3,
            text_view_horse4,
            text_view_horse5,
            text_view_horse6
        )
        horseProgressBars = arrayOf(
            progress_bar_horse1,
            progress_bar_horse2,
            progress_bar_horse3,
            progress_bar_horse4,
            progress_bar_horse5,
            progress_bar_horse6
        )

        hostMessageTextView = findViewById(R.id.text_view_host_message)

        random = Random()
        isRaceStarted = false

        initHorseColor()

        hostHandler = Handler(Handler.Callback { // 新增賽馬主持人的Handler
            if (it.what == 0) {
                val message = it.obj as String
                hostMessageTextView.text = message
            }
            true
        })

        horseHandler = Handler(Handler.Callback {
            if (it.what == 0) {
                val horseIndex = it.arg1
                val distance = it.arg2

                // 更新賽馬進度條
                horseProgressBars[horseIndex].progress = distance

                // 若賽馬到達終點，則設定賽馬比賽狀態為未開始並啟用按鈕
                if (distance >= 100) {
                    isRaceStarted = false
                    buttonStartRace.isEnabled = true
                    val championMsg =
                        horseTexts[horseIndex].text.toString() + raceChampionMsg.random()
                    hostHandler.sendMessage(hostHandler.obtainMessage(0, championMsg))
                } else {
                    val msg = when (distance) {
                        in 0..50 -> raceObservationMsg.random()
                        in 51..60 -> raceCommentaryMsg.random()
                        in 61..70 -> raceIntensityMsg.random()
                        in 71..80 -> raceProcessMsg.random()
                        in 81..90 -> raceClimaxMsg.random()
                        in 91..99 -> raceResultMsg.random()

                        else -> "等等!! 這場比賽，好像有些爭議。"
                    }
                    hostHandler.sendMessage(hostHandler.obtainMessage(0, msg))
                }
            }
            true
        })



        buttonStartRace.setOnClickListener {
            try {
                if (!isRaceStarted) {
                    isRaceStarted = true
                    buttonStartRace.isEnabled = false

                    startHost()

                    startRace()
                }
            } catch (e: Exception) {
                Log.e("StartRace : ", e.message.toString())
            }
        }
    }

    private fun startHost() {
        try {
            hostHandler.sendMessage(hostHandler.obtainMessage(0, preRaceMsg.random()))
            Thread.sleep(1000)
            hostHandler.sendMessage(hostHandler.obtainMessage(0, raceStartMsg.random()))
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
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
                    val message = horseHandler.obtainMessage(0)
                    message.arg1 = i                // 設定賽馬索引
                    message.arg2 = distances[i]     // 設定賽馬距離
                    horseHandler.sendMessage(message)    // 發送消息給 Handler 進行處理
                    try {
                        Thread.sleep(200)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }.start()
    }


    companion object {
        //  賽前介紹和期待
        val preRaceMsg = listOf(
            "歡迎來到今天的賽馬場！",
            "這是一場精彩的賽事，讓我們一起期待。",
            "馬匹們已經在等待出發，準備好了嗎？",
            "讓我們看看今天有哪些優秀的賽馬參賽。"
        )

        //  比賽開始
        val raceStartMsg = listOf(
            "馬匹們進入起點，即將開始比賽。",
            "這場比賽將會非常激烈，每匹馬都渴望勝利。"
        )

        // 比賽進行觀察
        val raceObservationMsg = listOf(
            "賽道上的馬匹們正在展示他們的速度和力量。",
            "我們看到一些領先的馬匹開始脫穎而出。",
            "現在是時候密切關注賽道上的情況了。",
            "觀眾們在賽道上看到了馬匹們展現出的驚人速度和力量。",
            "我們可以清楚地看到一些領先的馬匹已經開始拉開與其他馬匹的距離。",
            "賽道上的情況變得越來越精彩，每匹馬都在爭取更好的位置。",
            "馬匹們的表現讓人印象深刻，他們展示了出色的技術和韌性。"
        )

        // 比賽評論和分析
        val raceCommentaryMsg = listOf(
            "這是一場絕佳的比賽，不是嗎？",
            "我們看到一些馬匹已經開始加速。",
            "選手們正在盡力爭取領先位置。",
            "這場比賽的激烈程度超乎想象，是不是？",
            "一些馬匹已經開始展現出驚人的加速能力，給其他選手帶來了壓力。",
            "選手們在追求領先位置時展現出了極大的毅力和決心。",
            "這場比賽的局勢變化迅速，每一步都可能改變比賽的結果。"
        )


        //  比賽激烈程度和動態變化
        val raceIntensityMsg = listOf(
            "這場比賽非常激烈，不斷有新的局面出現。",
            "馬匹們展現出了他們的耐力和毅力。",
            "現在是一場真正的角逐，勝負即將分曉。"
        )

        //  比賽進展和情況
        val raceProcessMsg = listOf(
            "那匹馬目前處於領先位置。",
            "這場比賽令人屏息以待。",
            "每匹馬都在拼命追趕領先的那匹馬。"
        )

        //  比賽高潮和結束
        val raceClimaxMsg = listOf(
            "我們看到了一些非常精彩的超越。",
            "這是一場真正的馬力爭鋒，每匹馬都在全力以赴。",
            "選手們正在接近終點線。"
        )

        //  結果公佈和表揚
        val raceResultMsg = listOf(
            "現在是時候發揮最後的力量了。",
            "這是一個關鍵時刻，決定性的瞬間即將到來。",
            "領先的馬匹仍然保持著優勢。"
        )

        //  冠軍誕生和榮耀
        val raceChampionMsg = listOf(
            "那匹馬已經交出了一份出色的表現。",
            "現在是為所有參賽馬匹鼓掌的時候了。",
            "那匹馬贏得了今天的冠軍！"
        )
    }
}
