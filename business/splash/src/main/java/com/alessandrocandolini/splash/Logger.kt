package com.alessandrocandolini.splash

interface Logger {
    fun warn(tag : String, message : String)
    fun error(tag : String, message : String, t : Throwable?)
}