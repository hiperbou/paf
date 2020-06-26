package gameplay


import com.soywiz.klock.milliseconds
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.time.delay
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.format.BMP
import com.soywiz.korio.async.async
import com.soywiz.korio.async.launchAsap
import com.soywiz.korio.async.launchImmediately
import resources.Resources


suspend fun Container.frame() {
    delay(41.milliseconds)
}


private val imageCache = mutableMapOf<Int, BmpSlice>()
fun getImage(graph:Int): BmpSlice {
    return imageCache.getOrPut(graph) {
        return if(graph==0) Process.emptyImage.slice() else
        Resources.pafAtlas["${graph.toString().padStart(3, '0')}.png"].texture
    }
}

private lateinit var currentScene:Scene

abstract class SceneBase:Scene()
{
    init {
        currentScene = this
    }
}


abstract class Process(parent: Container) : Image(emptyImage) {
    companion object {
        val emptyImage = Bitmap32(1,1)
    }

    private var _graph = 0
    var graph:Int
        get() =  _graph
        set(value) {
            _graph = value
            texture = getImage(value)
        }


    init {
        parent.addChild(this)
        currentScene.launchAsap {
            main()
        }
    }

    open suspend fun main() {}

    inline fun loop(block:()->Unit) {
        while(true) {
            block()
        }
    }

    suspend fun <T>async(callback: suspend () -> T) = currentScene.async(callback)

}





fun Scene.loop(block:suspend ()->Unit){
    launchImmediately {
        while(true) {
            block()
        }
    }
}


