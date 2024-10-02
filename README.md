A simple stopwatch app designed for coaches to track athlete performance during track and field events.

# Features
* Start, pause, and reset the stopwatch
* Record lap times and calculate total time
* Display distance covered and lap time differences
* Set goal times and track progress
* Support for various track lengths and race distances
* Select a timekeeping position to account for where the timekeeper is standing on the track

# Usage
* Select the track length and race distance from the dropdown menus.
* Select the timekeeping position, which indicates where the timekeeper is standing on the track to take lap times. This can be at the finish line or at a different point on the track, such as the 100m, 200m, or 300m start
* Set the goal time in the format MM:ss:mmm.
* Press the "Start" button to begin the stopwatch.
* Press the "Lap" button to record a lap time.
* Press the "Pause" button to pause the stopwatch.
* Press the "Reset" button to reset the stopwatch.

# Timekeeping Position
The timekeeping position is used to calculate the distance covered by the athlete. By selecting the timekeeping position, the app can accurately calculate the distance covered by the athlete, even if the timekeeper is not standing at the finish line.

# Technical Details
* Built using Android Studio and Java.
* Uses a ConstraintLayout for the user interface.
* Utilizes a Timer and TimerTask to handle the stopwatch functionality.
* Stores data in a MutableList to track lap times and distances.

# Contributing
Contributions are welcome! If you'd like to add a feature or fix a bug, please submit a pull request.

# License
This project is licensed under the MIT License. See the LICENSE file for details.
