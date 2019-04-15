package io.apuno.fieldTomatoes

class Game {

    val WEEKS_IN_SEASON = 16
    val DELIMINATOR = ","
    val DEBUG = true

    val season = Season()

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

            // check for bugs
            sprayCheck(week)

            if (DEBUG)
                println(week)

            // check for bad weather
            if (haveBadWeather(week) || badOptions(week) || season.pestCount() > 2) {
                println("\nYou've lost! Try again :(\n")
                return
            }

            season.weeks.add(week)

        }

        println("You've won!!")

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
        for (value in values) {
            val goodOption = Option.values().find { it.id == value.toInt() }
            if (goodOption != null)
                options.add(goodOption)
        }

        return options
    }

    private fun prompt(week: Week) {

        println("""

            Week ${week.weekNumber}
            ---------------

            Weather for this week:
                ${ week.dailyWeather.joinToString(DELIMINATOR) { w -> " ${w.displayName}" }.trimStart() }
️
            Options:
                ${ Option.values().joinToString(DELIMINATOR) { o -> " ${o.displayName} [${o.id}]" } }

            Please select seven options for this week ([1-6] comma separated):
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

    private fun badOptions(week: Week): Boolean {

        // trim too early
        if (week.options.contains(Option.TRIM) && week.weekNumber < 3)
            return true

        // over fertilize
        if (week.options.filter { o -> o == Option.FERTILIZE }.size > 2)
            return true

        // too wet
        if ((week.options.filter { it == Option.WATER }.size + week.dailyWeather.filter { it == Weather.RAIN }.size) > 3)
            return false

        // not enough weeding
        if (!week.options.contains(Option.WEED))
            return true

        return false
    }

    private fun sprayCheck(week: Week) {

        // add pest if we didn't spray
        if (!week.options.contains(Option.SPRAY)) {
            println("didn't spray")
            week.pests.add(Pest.values()[(0..2).random()])
        }
        else {
            println("did spray")
            week.pests.removeAt(week.pests.lastIndex)
        }

    }

    private fun getRandomWeather(): Weather {
        val values = ArrayList<Weather>()
        values.add(Weather.SUN)
        values.add(Weather.SUN)
        values.add(Weather.SUN)
        values.add(Weather.RAIN)
        values.add(Weather.OVERCAST)

        return values[(0..values.size-1).random()]
    }

    class Week(val weekNumber: Int) {
        val dailyWeather = ArrayList<Weather>()
        val options = ArrayList<Option>()
        val pests = ArrayList<Pest>()

        override fun toString(): String {
            return "\nWeek(weekNumber=$weekNumber, dailyWeather=$dailyWeather, options=$options, pests=$pests\n"
        }
    }

    class Season {
        val weeks = ArrayList<Week>()

        fun pestCount(): Int {
            var count = 0

            for (week in weeks) {
                count += week.pests.size
            }

            return count
        }

        override fun toString(): String {
            return "\nSeason(weeks=$weeks)\n"
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

    enum class Pest(val id: Int, val displayName: String) {
        SQUASH_BUG(1, "Squash Bug"),
        SPIDER_MITE(2, "Spider Mite"),
        APHID(3, "Aphid")
    }
}

