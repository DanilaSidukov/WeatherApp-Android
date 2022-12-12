package com.sidukov.weatherapp

import com.sidukov.weatherapp.data.remote.api.APIClient
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
enum class DescriptionCondition(val value: Int) {
    Cold(R.string.cold),
    Sunny(R.string.sunny),
    Rainy(R.string.rainy),
    Error(R.string.error),
    Snowfall(R.string.snowfall),
    CloudCover(R.string.cloud_cover)
}

class ExampleUnitTest {
    @Test
    fun openLink() {

        val a = DescriptionCondition.Cold
        println(a.value)

    }
}