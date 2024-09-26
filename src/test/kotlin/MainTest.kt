import me.bottdev.*

class CoreService : Service() {

    override fun onStart() {
       // println("Core service started!")
    }

    override fun onStop() {
        //println("Core service stopped!")
    }

    fun count() {
        (0..5).forEach { println(it) }
    }

}

class AnotherService(private val coreService: CoreService) : Service() {

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

    val coreService = programManager.get<CoreService>()
    val anotherService = programManager.get<AnotherService>()

    programManager.startAllComponents()

    programManager.stopAllComponents()
}