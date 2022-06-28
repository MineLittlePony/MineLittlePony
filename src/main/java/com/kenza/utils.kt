package com.kenza

import net.minecraft.text.Text

fun Any.random(from: Int, to: Int) = (Math.random() * (to - from) + from).toInt()

fun translatable(text: String, vararg args: Any) = Text.translatable(text, args)
fun literal(text: String) = Text.literal(text)