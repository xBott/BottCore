package me.bottdev.bottcore.components

import me.bottdev.bottcore.ProgramComponent

abstract class Service : ProgramComponent() {

    protected abstract fun onStart()

    protected abstract fun onStop()

}