import me.bottdev.bottcore.components.Service
import me.bottdev.bottcore.program

class CoreService : Service() {

    private var counter = 0;

    override fun onStart() {
       // println("Core service started!")
    }

    override fun onStop() {
        //println("Core service stopped!")
    }

    fun count() {
        counter++
        println(counter)
    }

}

class AnotherService(val coreService: CoreService) : Service() {

    override fun onStart() {
        //("Another service started!")
        //coreService.count()

    }

    override fun onStop() {
        //println("Another service stopped!")
    }

}

val programManager = program {
    component { CoreService() }
    component { AnotherService(get()) }
}

fun main() {

    programManager.startAllComponents()

    while (true) {

        var amount = 1
        val input = readlnOrNull()?.trim()?.let {
            if (it.contains("-")) {
                val parts = it.split("-")
                amount = parts.last().toIntOrNull() ?: 1
                return@let parts.first()
            }
            it
        }

        if (input.isNullOrEmpty()) {
            println("Empty input, try again.")
            continue
        }

        when (input) {
            "count" -> {
                val coreService = programManager.get<AnotherService>().coreService
                repeat(amount) {
                    coreService.count()
                }
            }
            "exit" -> {
                println("Exiting program.")
                programManager.stopAllComponents()
                break
            }
            else -> println("Unknown command")
        }
    }

}