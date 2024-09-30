package me.bottdev.bottcore

import me.bottdev.bottcore.components.Service
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.slf4j.LoggerFactory
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

class ProgramManager {

    private var _status = ProgramStatus.DISABLED

    val status: ProgramStatus
        get() = _status

    private val logger = LoggerFactory.getLogger("Program Manager")!!

    val components = mutableListOf<ProgramComponent>()

    val module = module {}

    var application: KoinApplication = koinApplication {}

    inline fun <reified T> component(crossinline creator: () -> T) {
        module.single(createdAtStart = true) {
            creator().also { components.add(it as ProgramComponent) }
        }
        application.koin.loadModules(listOf(module), createEagerInstances = true)
    }

    private fun Service.enableService(): Boolean {

        println("")

        if (status == ComponentStatus.ENABLED ||
            status == ComponentStatus.ENABLING ||
            status == ComponentStatus.ENABLING_ERROR
        ) {
            logger.warn("Already enabled!")
            return true
        }

        logger.info("Enabling service:")
        (this as ProgramComponent).setStatus(ComponentStatus.ENABLING)
        (this as ProgramComponent).setStatus(
            try {

                this::class.declaredFunctions.find { it.name == "onStart" }?.apply {
                    isAccessible = true
                    call(this@enableService)
                }

                logger.info("Service was successfully enabled!")

                ComponentStatus.ENABLED
                return true

            } catch (ex: Exception) {
                logger.error("An error was occurred while enabling!", ex)
                ComponentStatus.ENABLING_ERROR
            }
        )
        return false

    }

    private fun Service.disableService(): Boolean {

        println("")

        if (status == ComponentStatus.DISABLED ||
            status == ComponentStatus.DISABLING ||
            status == ComponentStatus.DISABLING_ERROR
            ) {
            logger.warn("Already disabled!")
            return true
        }

        logger.info("Disabling service:")
        (this as ProgramComponent).setStatus(ComponentStatus.DISABLING)
        (this as ProgramComponent).setStatus(
            try {

                this::class.declaredFunctions.find { it.name == "onStop" }?.apply {
                    isAccessible = true
                    call(this@disableService)
                }

                logger.info("Service was successfully disabled!")

                ComponentStatus.DISABLED
                return true

            } catch (ex: Exception) {
                logger.error("An error was occurred while disabling!", ex)
                ComponentStatus.DISABLING_ERROR
            }
        )
        return false

    }

    private fun ProgramComponent.setStatus(newStatus: ComponentStatus) {
        val property = ProgramComponent::class.declaredMemberProperties.find { it.name == "_status" } ?: return
        property.isAccessible = true
        val mutableProperty = property as? kotlin.reflect.KMutableProperty<*> ?: return
        mutableProperty.setter.call(this, newStatus)
    }

    fun startAllComponents() {
        logger.info("Enabling components:")

        var success = true
        components.filterIsInstance<Service>().forEach {
            val result = it.enableService()
            success = result && success
        }

        _status = if (success) {
            ProgramStatus.ENABLED
        } else {
            ProgramStatus.ENABLING_ERROR
        }

        sendStatusMessage()
    }

    fun stopAllComponents() {
        logger.info("Disabling components:")

        var success = true
        components.filterIsInstance<Service>().forEach {
            val result = it.disableService()
            success = result && success
        }

        _status = if (success) {
            ProgramStatus.DISABLED
        } else {
            ProgramStatus.DISABLING_ERROR
        }

        sendStatusMessage()
    }

    private fun sendStatusMessage() {
        println("")
        logger.info(status.message)
    }

    inline fun <reified T : ProgramComponent> get(): T = application.koin.get()

}

fun program(block: ProgramManager.() -> Unit): ProgramManager {
    val programManager = ProgramManager()
    programManager.block()
    return programManager
}