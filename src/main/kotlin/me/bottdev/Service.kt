package me.bottdev

abstract class Service : ProgramComponent() {

    protected abstract fun onStart()

    protected abstract fun onStop()

}