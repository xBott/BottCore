package me.bottdev

import org.slf4j.LoggerFactory

open class ProgramComponent {

    val logger = LoggerFactory.getLogger(this::class.simpleName)

    private var _status = ComponentStatus.DISABLED

    // Доступ к статусу только для чтения
    val status: ComponentStatus
        get() = _status

}