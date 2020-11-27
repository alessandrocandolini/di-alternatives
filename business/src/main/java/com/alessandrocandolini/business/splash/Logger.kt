package com.alessandrocandolini.business.splash

interface Logger {
    fun warn(tag : String, message : String)
    fun error(tag : String, message : String, t : Throwable?)
}