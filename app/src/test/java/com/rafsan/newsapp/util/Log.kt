@file:JvmName("Log")

package com.rafsan.newsapp.util

fun d(tag: String, msg: String): Int {
    println("DEBUG: $tag: $msg")
    return 0
}

fun i(tag: String, msg: String): Int {
    println("INFO: $tag: $msg")
    return 0
}

fun w(tag: String, msg: String): Int {
    println("WARN: $tag: $msg")
    return 0
}

fun e(tag: String, msg: String): Int {
    println("ERROR: $tag: $msg")
    return 0
}