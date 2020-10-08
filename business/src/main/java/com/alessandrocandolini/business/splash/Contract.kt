package com.alessandrocandolini.business.splash

interface View

interface Presenter {

    fun bind(v : View)
    fun onViewCreated()
    fun unbind()

}