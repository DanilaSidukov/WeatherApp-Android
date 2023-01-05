package com.sidukov.weatherapp.data

public data class NTuple5<out T1, out T2, out T3, out T4, out T5>(
    val first: T1,
    val second: T2,
    val third: T3,
    val fourth: T4,
    val fifth: T5,
) : java.io.Serializable {

    public override fun toString(): String = "($first, $second, $third, $fourth, $fifth)"

}

public fun <T> NTuple5<T, T, T, T, T>.toList(): List<T> = listOf(first, second, third, fourth, fifth)
