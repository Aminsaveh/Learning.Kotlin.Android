package com.example.tddtest

import com.example.tddtest.home.model.PostDto
import java.util.*


private val random = Random()

fun String.appendRandom() = "$this-${random.nextInt(100)}"

fun randomInt() = random.nextInt()


fun testPostDtoData(
    id: Int = randomInt(),
    title: String = "Title-".appendRandom(),
    body: String = "Body-".appendRandom()
) = PostDto(
    id = id,
    title = title,
    body = body
)