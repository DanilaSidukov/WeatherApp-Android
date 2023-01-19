package com.sidukov.weatherapp

import org.junit.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
//enum class DescriptionCondition(val value: Int) {
//    Cold(R.string.cold),
//    Sunny(R.string.sunny),
//    Rainy(R.string.rainy),
//    Error(R.string.error),
//    Snowfall(R.string.snowfall),
//    CloudCover(R.string.cloud_cover)
//}
//
class ExampleUnitDescriptionDigest {
    @Test
    fun openLink() {

//        var dayCounter = LocalDate.now().dayOfWeek.value
//        println(dayCounter)

        val date = Calendar.getInstance().add(Calendar.DAY_OF_MONTH, 2)
        val c = Calendar.getInstance()
        c.add(Calendar.WEEK_OF_MONTH, 2)
        val d = c.time.toInstant()

        LocalDateTime.now().dayOfMonth+14
        val add = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-${LocalDateTime.now().dayOfMonth+14}"))

        val day = LocalDateTime.now().dayOfWeek.toString()

        println("TIME IS - $day")


//        val a = DescriptionCondition.Cold
//        println(a.value)

    }
}


