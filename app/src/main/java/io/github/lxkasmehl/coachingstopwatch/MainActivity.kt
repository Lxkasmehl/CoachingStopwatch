package io.github.lxkasmehl.coachingstopwatch

import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Timer
import java.util.TimerTask
import android.widget.ArrayAdapter

class StopwatchActivity : AppCompatActivity() {

    private lateinit var timeTextView: TextView
    private lateinit var lapListView: ListView
    private lateinit var startButton: Button
    private lateinit var pauseButton: Button
    private lateinit var lapButton: Button

    private var timer: Timer? = null
    private var startTime: Long = 0
    private var lapTimes: MutableList<Long> = mutableListOf()
    private var isPaused = false
    private var pausedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stopwatch)

        timeTextView = findViewById(R.id.time_textview)
        lapListView = findViewById(R.id.lap_listview)
        startButton = findViewById(R.id.start_button)
        pauseButton = findViewById(R.id.pause_button)
        lapButton = findViewById(R.id.lap_button)

        startButton.setOnClickListener {
            startTimer()
        }

        pauseButton.setOnClickListener {
            if (isPaused) {
                stopTimer()
            } else {
                pauseTimer()
            }
        }

        lapButton.setOnClickListener {
            lapTimer()
        }
    }

    private fun resumeTimer() {
        startTime = System.currentTimeMillis() - pausedTime
        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                val currentTime = System.currentTimeMillis()
                val timeElapsed = currentTime - startTime
                val minutes = timeElapsed / 60000
                val seconds = (timeElapsed % 60000) / 1000
                val milliseconds = (timeElapsed % 1000)
                val timeString = String.format("%02d:%02d:%03d", minutes, seconds, milliseconds)
                runOnUiThread {
                    timeTextView.text = timeString
                }
            }
        }, 0, 10)
        startButton.text = "Start"
        pauseButton.text = "Pause"
        isPaused = false
    }

    private fun startTimer() {
        if (isPaused) {
            resumeTimer()
        } else if (timeTextView.text == "00:00:000" || timeTextView.text == "") {
            startTime = System.currentTimeMillis()
            timer = Timer()
            timer!!.schedule(object : TimerTask() {
                override fun run() {
                    val currentTime = System.currentTimeMillis()
                    val timeElapsed = currentTime - startTime
                    val minutes = timeElapsed / 60000
                    val seconds = (timeElapsed % 60000) / 1000
                    val milliseconds = (timeElapsed % 1000)
                    val timeString = String.format("%02d:%02d:%03d", minutes, seconds, milliseconds)
                    runOnUiThread {
                        timeTextView.text = timeString
                    }
                }
            }, 0, 10)
        }
    }

    private fun pauseTimer() {
        timer?.cancel()
        pausedTime = System.currentTimeMillis() - startTime
        isPaused = true
        pauseButton.text = "Stop"
        startButton.text = "Resume"
    }

    private fun stopTimer() {
        lapTimes.clear()
        startTime = 0
        timer?.cancel()
        isPaused = false
        pauseButton.text = "Pause"
        startButton.text = "Start"
        timeTextView.text = "00:00:000"
        lapListView.adapter = null
    }

    private fun lapTimer() {
        val currentTime = System.currentTimeMillis()
        val lapTime = currentTime - startTime
        lapTimes.add(lapTime)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, lapTimes.map { String.format("%02d:%02d:%03d", it / 60000, (it % 60000) / 1000, it % 1000) })
        runOnUiThread {
            lapListView.adapter = adapter
        }
    }
}