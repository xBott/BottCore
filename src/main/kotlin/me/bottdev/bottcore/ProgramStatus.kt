package me.bottdev.bottcore

enum class ProgramStatus(val message: String) {
    ENABLED("Successfully enabled all components"),
    DISABLED("Successfully disabled all components"),
    ENABLING_ERROR("An error was occurred while enabling components"),
    DISABLING_ERROR("An error was occurred while disabling components")
}