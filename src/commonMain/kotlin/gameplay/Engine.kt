package gameplay


import com.soywiz.klock.milliseconds
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.time.delay
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.Image
import com.soywiz.korim.bitmap.Bitmap32
import com.soywiz.korim.bitmap.BmpSlice
import com.soywiz.korim.bitmap.slice
import com.soywiz.korio.async.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
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

    fun launch(callback: suspend () -> Unit) = currentScene.launch(callback)
    fun launchImmediately(callback: suspend () -> Unit) = currentScene.launchImmediately(callback)
    fun launchAsap(callback: suspend () -> Unit) = currentScene.launchAsap(callback)

    fun <T>async(callback: suspend () -> T) = currentScene.async(callback)
    fun <T>asyncImmediately(callback: suspend () -> T) = currentScene.asyncImmediately(callback)
    fun <T>asyncAsap(callback: suspend () -> T) = currentScene.asyncAsap(callback)


}





fun Scene.loop(block:suspend ()->Unit){
    launchImmediately {
        while(true) {
            block()
        }
    }
}


