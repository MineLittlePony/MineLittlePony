package com.kenza

fun Any.random(from: Int, to: Int) = (Math.random() * (to - from) + from).toInt()
