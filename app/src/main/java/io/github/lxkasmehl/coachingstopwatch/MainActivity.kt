package io.github.lxkasmehl.coachingstopwatch

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.Timer
import java.util.TimerTask
import io.github.lxkasmehl.coachingstopwatch.R


class StopwatchActivity : AppCompatActivity() {
    private lateinit var timeTextView: TextView
    private lateinit var lapListView: ListView
    private lateinit var startButton: Button
    private lateinit var resetButton: Button
    private lateinit var lapButton: Button
    private lateinit var trackLengthSpinner: Spinner
    private lateinit var positionSpinner: Spinner
    private lateinit var raceDistanceSpinner: Spinner
    private lateinit var goaltimeEditText: EditText

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
        resetButton = findViewById(R.id.reset_button)
        lapButton = findViewById(R.id.lap_button)
        trackLengthSpinner = findViewById(R.id.track_length_spinner)
        positionSpinner = findViewById(R.id.position_spinner)
        raceDistanceSpinner = findViewById(R.id.race_distance_spinner)
        val goaltimeLayout = findViewById<ConstraintLayout>(R.id.goaltime_layout)
        goaltimeEditText = goaltimeLayout.findViewById(R.id.goaltime_edittext)

        startButton.setOnClickListener {
            if (startButton.text == getString(R.string.start) || startButton.text == getString(R.string.resume)) {
                startTimer()
            } else {
                pauseTimer()
            }
        }

        resetButton.setOnClickListener {
            stopTimer()
        }

        lapButton.setOnClickListener {
            lapTimer()
        }

        resetButton.visibility = View.GONE
        lapButton.visibility = View.GONE

        val track_length_adapter = ArrayAdapter.createFromResource(
            this,
            R.array.track_length_options,
            android.R.layout.simple_spinner_item
        )
        track_length_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        trackLengthSpinner.adapter = track_length_adapter

        val position_adapter = ArrayAdapter.createFromResource(
            this,
            R.array.position_options,
            android.R.layout.simple_spinner_item
        )
        position_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        positionSpinner.adapter = position_adapter

        val race_distance_adapter = ArrayAdapter.createFromResource(
            this,
            R.array.race_distance_options,
            android.R.layout.simple_spinner_item
        )
        race_distance_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        raceDistanceSpinner.adapter = race_distance_adapter
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
                val timeString = String.format(getString(R.string.lap_time_format), minutes, seconds, milliseconds)
                runOnUiThread {
                    timeTextView.text = timeString
                }
            }
        }, 0, 10)
        isPaused = false
    }

    private fun startTimer() {
        startButton.text = getString(R.string.pause)
        resetButton.visibility = View.VISIBLE
        lapButton.visibility = View.VISIBLE

        if (isPaused) {
            resumeTimer()
        } else if (timeTextView.text == getString(R.string.default_time) || timeTextView.text == "") {
            startTime = System.currentTimeMillis()
            timer = Timer()
            timer!!.schedule(object : TimerTask() {
                override fun run() {
                    val currentTime = System.currentTimeMillis()
                    val timeElapsed = currentTime - startTime
                    val minutes = timeElapsed / 60000
                    val seconds = (timeElapsed % 60000) / 1000
                    val milliseconds = (timeElapsed % 1000)
                    val timeString = String.format(getString(R.string.lap_time_format), minutes, seconds, milliseconds)
                    runOnUiThread {
                        timeTextView.text = timeString
                    }
                }
            }, 0, 10)
        }
    }

    private fun pauseTimer() {
        startButton.text = getString(R.string.resume)
        lapButton.visibility = View.GONE
        resetButton.visibility = View.VISIBLE

        timer?.cancel()
        pausedTime = System.currentTimeMillis() - startTime
        isPaused = true
    }

    private fun stopTimer() {
        resetButton.visibility = View.GONE
        lapButton.visibility = View.GONE
        startButton.text = getString(R.string.start)

        lapTimes.clear()
        startTime = 0
        pausedTime = 0
        timer?.cancel()
        timer = null
        isPaused = false
        lapListView.adapter = null
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                timeTextView.text = getString(R.string.default_time)
            }
        }, 50)
    }


    private fun lapTimer() {
        val currentTime = System.currentTimeMillis()
        val lapTime = currentTime - startTime
        lapTimes.add(0, lapTime)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, lapTimes.map { String.format(getString(R.string.lap_time_format), it / 60000, (it % 60000) / 1000, it % 1000) })
        runOnUiThread {
            lapListView.adapter = adapter
        }
    }
}