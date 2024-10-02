package io.github.lxkasmehl.coachingstopwatch

import android.os.Bundle
import android.view.View
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import java.util.Locale
import java.util.Timer
import java.util.TimerTask


class StopwatchActivity : AppCompatActivity() {
    private lateinit var timeTextView: TextView
    private lateinit var lapTable: TableLayout
    private lateinit var startButton: Button
    private lateinit var resetButton: Button
    private lateinit var lapButton: Button
    private lateinit var trackLengthLayout: ConstraintLayout
    private lateinit var positionLayout: ConstraintLayout
    private lateinit var raceDistanceLayout: ConstraintLayout
    private lateinit var goaltimeLayout: ConstraintLayout
    private lateinit var trackLengthSpinner: Spinner
    private lateinit var positionSpinner: Spinner
    private lateinit var raceDistanceSpinner: Spinner
    private lateinit var goaltimeEditText: EditText

    private var timer: Timer? = null
    private var startTime: Long = 0
    private var lapTimes: MutableList<Long> = mutableListOf()
    private var isPaused = false
    private var pausedTime: Long = 0
    private var averageLapTimeMilliseconds = 0
    private var timeToFirstLap = 0
    private var distanceToFirstLap = 0
    private var trackLength = 0
    private var positionToFinish = 0
    private var raceStartPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stopwatch)

        timeTextView = findViewById(R.id.time_textview)
        lapTable = findViewById(R.id.lap_table)
        startButton = findViewById(R.id.start_button)
        resetButton = findViewById(R.id.reset_button)
        lapButton = findViewById(R.id.lap_button)
        trackLengthSpinner = findViewById(R.id.track_length_spinner)
        positionSpinner = findViewById(R.id.position_spinner)
        raceDistanceSpinner = findViewById(R.id.race_distance_spinner)
        trackLengthLayout = findViewById(R.id.track_length_layout)
        positionLayout = findViewById(R.id.position_layout)
        raceDistanceLayout = findViewById(R.id.race_distance_layout)
        goaltimeLayout = findViewById(R.id.goaltime_layout)
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

        setupSpinners()
    }

    private fun setupSpinners() {
        val trackLengthAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.track_length_options,
            android.R.layout.simple_spinner_item
        )
        trackLengthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        trackLengthSpinner.adapter = trackLengthAdapter

        val positionAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.position_options,
            android.R.layout.simple_spinner_item
        )
        positionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        positionSpinner.adapter = positionAdapter

        val raceDistanceAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.race_distance_options,
            android.R.layout.simple_spinner_item
        )
        raceDistanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        raceDistanceSpinner.adapter = raceDistanceAdapter
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

        val goalTime = goaltimeEditText.text.toString()
        val goalTimeTotalMilliseconds: Int

        if (goalTime.isEmpty()){
            goalTimeTotalMilliseconds = 0
        } else if (goalTime.matches(Regex("^(\\d{2}):(\\d{2}):(\\d{3})$"))) {
            // MM:ss:mmm format
            val goalTimeMinutes = goalTime.substring(0, 2).toInt()
            val goalTimeSeconds = goalTime.substring(3, 5).toInt()
            val goalTimeMilliseconds = goalTime.substring(6, 8).toInt()
            goalTimeTotalMilliseconds = (goalTimeMinutes * 60 + goalTimeSeconds) * 1000 + goalTimeMilliseconds
        } else if (goalTime.matches(Regex("^(\\d+):(\\d{2})$"))) {
            // M:ss format
            val goalTimeMinutes = goalTime.substring(0, goalTime.indexOf(":")).toInt()
            val goalTimeSeconds = goalTime.substring(goalTime.indexOf(":") + 1).toInt()
            goalTimeTotalMilliseconds = (goalTimeMinutes * 60 + goalTimeSeconds) * 1000
        } else if (goalTime.matches(Regex("^(\\d+)$"))) {
            // Single number format (minutes)
            val goalTimeMinutes = goalTime.toInt()
            goalTimeTotalMilliseconds = goalTimeMinutes * 60 * 1000
        } else {
            Toast.makeText(this, "Invalid goal time format. Please use MM:ss:mmm, M:ss, or a single number", Toast.LENGTH_SHORT).show()
            return
        }

        val trackLengthString = trackLengthSpinner.selectedItem.toString()
        trackLength = trackLengthString.replace("m", "").toInt()

        val raceDistanceString = raceDistanceSpinner.selectedItem.toString()
        val raceDistance = raceDistanceString.replace("m", "").toInt()

        averageLapTimeMilliseconds = (goalTimeTotalMilliseconds * trackLength) / raceDistance
        
        raceStartPosition = raceDistance % trackLength

        val positionString = positionSpinner.selectedItem.toString()
        positionToFinish = 0
        if (positionString != "Finish") {
            positionToFinish = positionString.replace("m Start", "").toInt()
        }

        distanceToFirstLap = if (raceStartPosition == positionToFinish) {
            trackLength
        } else if (raceStartPosition > positionToFinish){
            raceStartPosition - positionToFinish
        } else {
            raceStartPosition + (trackLength - positionToFinish)
        }
        timeToFirstLap = (goalTimeTotalMilliseconds / raceDistance) * distanceToFirstLap

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
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                timeTextView.text = getString(R.string.default_time)
            }
        }, 50)

        val rowCount = lapTable.childCount
        for (i in rowCount - 1 downTo 1) {
            lapTable.removeViewAt(i)
        }
    }

    private fun lapTimer() {
        val currentTime = System.currentTimeMillis()
        val lapTime = currentTime - startTime
        lapTimes.add(0, lapTime)

        var lapDiffTime = lapTime
        if (lapTimes.size > 1) {
            lapDiffTime = lapTime - lapTimes[1]
        }

        val isFirstLap = lapTimes.size == 1

        val distanceCovered = if (isFirstLap) distanceToFirstLap else (lapTimes.size-1) * trackLength + distanceToFirstLap

        runOnUiThread {
            val row = TableRow(this)
            row.layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
            row.setPadding(5, 10, 5, 10)

            val distanceTextView = TextView(this)
            distanceTextView.text = getString(R.string.distance_covered, distanceCovered)
            distanceTextView.textSize = 16f
            distanceTextView.gravity = Gravity.CENTER_HORIZONTAL
            row.addView(distanceTextView)

            val lapTimeTextView = TextView(this)
            lapTimeTextView.text = String.format(
                getString(R.string.lap_time_format),
                lapDiffTime / 60000,
                lapDiffTime % 60000 / 1000,
                lapDiffTime % 1000
            )
            lapTimeTextView.textSize = 16f
            lapTimeTextView.gravity = Gravity.CENTER_HORIZONTAL
            row.addView(lapTimeTextView)

            val lapTimeDiffTextView = TextView(this)
            if (averageLapTimeMilliseconds == 0) {
                lapTimeDiffTextView.text = ""
            } else {
                val lapDiffBase = if (isFirstLap) timeToFirstLap else averageLapTimeMilliseconds
                val lapAvgDiff = lapDiffTime - lapDiffBase
                val diffSeconds = lapAvgDiff / 1000
                val diffMilliseconds = lapAvgDiff % 1000
                val diffString = if (lapAvgDiff < 0) {
                    "-${-diffSeconds}.${String.format(Locale.getDefault(), "%03d", -diffMilliseconds)}"
                } else {
                    "+${diffSeconds}.${String.format(Locale.getDefault(), "%03d", diffMilliseconds)}"
                }
                lapTimeDiffTextView.text = diffString
                lapTimeDiffTextView.setTextColor(ContextCompat.getColor(this, if (lapAvgDiff < 0) R.color.green else R.color.red))
                lapTimeDiffTextView.textSize = 16f
                lapTimeDiffTextView.gravity = Gravity.CENTER_HORIZONTAL
            }
            row.addView(lapTimeDiffTextView)

            val totalTimeTextView =
                TextView(this)
            totalTimeTextView.text = String.format(
                getString(R.string.lap_time_format),
                lapTime / 60000,
                lapTime % 60000 / 1000,
                lapTime % 1000
            )
            totalTimeTextView.textSize = 16f
            totalTimeTextView.gravity = Gravity.CENTER_HORIZONTAL
            row.addView(totalTimeTextView)

            val totalTimeDiffTextView = TextView(this)
            if (averageLapTimeMilliseconds == 0) {
                totalTimeDiffTextView.text = ""
            } else {
                val totalTimeDiffBase = timeToFirstLap + (lapTimes.size - 1) * averageLapTimeMilliseconds
                val totalTimeDiff = lapTime - totalTimeDiffBase
                val totalTimeDiffSeconds = totalTimeDiff / 1000
                val totalTimeDiffMilliseconds = totalTimeDiff % 1000
                val totalTimeDiffString = if (totalTimeDiff < 0) {
                    "-${-totalTimeDiffSeconds}.${String.format(Locale.getDefault(), "%03d", -totalTimeDiffMilliseconds)}"
                } else {
                    "+${totalTimeDiffSeconds}.${String.format(Locale.getDefault(), "%03d", totalTimeDiffMilliseconds)}"
                }
                totalTimeDiffTextView.text = totalTimeDiffString
                totalTimeDiffTextView.setTextColor(ContextCompat.getColor(this, if (totalTimeDiff < 0) R.color.green else R.color.red))
                totalTimeDiffTextView.textSize = 16f
                totalTimeDiffTextView.gravity = Gravity.CENTER_HORIZONTAL
            }
            row.addView(totalTimeDiffTextView)

            lapTable.addView(row, 1)
        }
    }
}