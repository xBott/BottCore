package me.bottdev

import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

class ProgramManager {

    val components = mutableListOf<ProgramComponent>()

    val module = module {}

    var application: KoinApplication = koinApplication {

    }

    inline fun <reified T> component(crossinline creator: () -> T) {
        module.single(createdAtStart = true) {
            creator().also { components.add(it as ProgramComponent) }
        }
        application.koin.loadModules(listOf(module), createEagerInstances = true)
    }

    private fun Service.enableService() {

        println("")

        if (status != ComponentStatus.DISABLED) {
            logger.warn("Already enabled!")
            return
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

            } catch (ex: Exception) {
                logger.error("An error was occurred while enabling!", ex)
                ComponentStatus.ENABLING_ERROR
            }
        )

    }

    private fun Service.disableService() {

        println("")

        if (status != ComponentStatus.ENABLED) {
            logger.warn("Already disabled!")
            return
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
            } catch (ex: Exception) {
                logger.error("An error was occurred while disabling!", ex)
                ComponentStatus.DISABLING_ERROR
            }
        )
    }

    private fun ProgramComponent.setStatus(newStatus: ComponentStatus) {
        val property = ProgramComponent::class.declaredMemberProperties.find { it.name == "_status" } ?: return
        property.isAccessible = true
        val mutableProperty = property as? kotlin.reflect.KMutableProperty<*> ?: return
        mutableProperty.setter.call(this, newStatus)
    }

    fun startAllComponents() {
        components.filterIsInstance<Service>().forEach {
            it.enableService()
        }
    }

    fun stopAllComponents() {
        components.filterIsInstance<Service>().forEach {
            it.disableService()
        }
    }

    inline fun <reified T : ProgramComponent> get(): T = application.koin.get()

}

fun program(block: ProgramManager.() -> Unit): ProgramManager {
    val programManager = ProgramManager()
    programManager.block()
    return programManager
}