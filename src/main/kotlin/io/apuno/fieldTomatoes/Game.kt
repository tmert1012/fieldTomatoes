package io.apuno.fieldTomatoes

class Game {

    val WEEKS_IN_SEASON = 16
    val DELIMINATOR = ","
    val DEBUG = false

    fun run() {

        displayIntro()

        // each week
        for (weekNumber in 1..WEEKS_IN_SEASON) {
            val week = Week(weekNumber)

            // set this week's weather
            for (day in 1..7)
                week.dailyWeather.add(getRandomWeather())

            // get user input
            prompt(week)

            // save options
            week.options.addAll( parseOptions(readLine()!!) )

            if (DEBUG)
                println(week)

            // check for bad weather
            if (haveBadWeather(week)) {
                println("\nYou've lost! Try again :(\n")
                return
            }
        }

    }

    private fun displayIntro() {
        println("""

            Field Tomatoes!
            ---------------

            Goal: To make it through the season without losing your crop!

            Game play: Make a work schedule for each week, based on the weather forecast. Try and figure out the weather and work combo to make it to the end!

            Have fun, good luck!


        """.trimIndent())
    }

    private fun parseOptions(input: String): ArrayList<Option> {
        val values = ArrayList<String>()
        val options = ArrayList<Option>()

        // no input, bail
        if (input.isNullOrEmpty())
            return options

        // split on delim
        if (input.contains(DELIMINATOR))
            values.addAll(input.split(DELIMINATOR))
        else
            values.add(input)

        // validate and popuplate options
        for (value in values)
            options.add( Option.values().first { it.id == value.toInt() } )

        return options
    }

    private fun prompt(week: Week) {

        println("""

            Week ${week.weekNumber}
            ---------------

            Weather for this week:
                ${ week.dailyWeather.joinToString(DELIMINATOR) { w -> " ${w.displayName}" }.trimStart() }

            Options:
                ${ Option.values().joinToString(DELIMINATOR) { o -> " ${o.displayName} [${o.id}]" } }

            Please select seven options for this week ([1-7] comma separated):
        """.trimIndent())

    }

    private fun haveBadWeather(week: Week): Boolean {
        var lastRainOn = -1
        var lastOvercastOn = -1

        // check for rain and overcast
        for ((dayOfWeek, weather) in week.dailyWeather.withIndex()) {
            when (weather) {
                Weather.RAIN -> lastRainOn = dayOfWeek
                Weather.OVERCAST -> lastOvercastOn = dayOfWeek
                else -> {}
            }

            // overcast followed by a day of rain
            if (lastRainOn > -1 && lastOvercastOn > -1 && lastOvercastOn > lastRainOn && (lastOvercastOn-lastRainOn) == 1)
                return true

        }

        return false
    }

    private fun getRandomWeather(): Weather {
        return Weather.values()[(0..2).random()]
    }

    class Week(val weekNumber: Int) {
        val dailyWeather = ArrayList<Weather>()
        val options = ArrayList<Option>()

        override fun toString(): String {
            return "\nWeek(weekNumber=$weekNumber, dailyWeather=$dailyWeather, options=$options)\n"
        }
    }

    enum class Weather(val id: Int, val displayName: String) {
        SUN(1, "Sun"),
        RAIN(2, "Rain"),
        OVERCAST(3, "Overcast");
    }

    enum class Option(val id: Int, val displayName: String) {
        TRIM(1, "Trim"),
        WATER(2, "Water"),
        WEED(3, "Weed"),
        FERTILIZE(4, "Fertilize"),
        TRELLIS(5, "Trellis"),
        SPRAY(6, "Spray");
    }
}

