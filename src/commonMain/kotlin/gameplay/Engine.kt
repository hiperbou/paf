package gameplay


import korlibs.image.bitmap.*
import korlibs.image.format.readBitmapSlice
import korlibs.io.async.*
import korlibs.io.file.VfsFile
import korlibs.io.resources.Resourceable
import korlibs.korge.render.RenderContext
import korlibs.korge.scene.Scene
import korlibs.korge.view.*
import korlibs.korge.view.Image
import korlibs.korge.view.property.ViewProperty
import korlibs.korge.view.property.ViewPropertyFileRef
import korlibs.math.geom.Anchor
import korlibs.math.geom.vector.VectorPath
import korlibs.time.Frequency
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import resources.Resources
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private val imageCache = mutableMapOf<Int, BmpSlice>()
fun getImage(graph:Int): BmpSlice {
    return imageCache.getOrPut(graph) {
        return if(graph==0) Process.emptyImage.slice() else
        Resources.pafAtlas["${graph.toString().padStart(3, '0')}.png"]//.texture
    }
}

private lateinit var currentScene:Scene

abstract class SceneBase:Scene()
{
    init {
        currentScene = this
    }
}

//abstract class Process(parent: Container) : OpenImage(emptyImage) {
abstract class Process(parent: Container) : Container(), Anchorable {
    companion object {
        val emptyImage = Bitmap32(1,1)
    }

    override var anchor: Anchor = Anchor.TOP_LEFT
    var bitmap: BmpSlice? = null
    var smoothing: Boolean = false

    private var _graph = 0
    var graph:Int
        get() =  _graph
        set(value) {
            _graph = value
            bitmap = getImage(value)
        }

    override fun renderInternal(ctx: RenderContext) {
        ctx.useBatcher { batch ->
            val bitmap = this@Process.bitmap
            if (bitmap != null) {
                val px = bitmap.width * anchor.sx
                val py = bitmap.height * anchor.sy
                batch.drawQuad(ctx.getTex(bitmap), -px, -py, filtering = smoothing, blendMode = renderBlendMode, m = globalMatrix)
            }
        }
        super.renderInternal(ctx)
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

    private val frameReady = Signal<Unit>()
    private var frameListenerInitialized = false

    suspend fun Container.frame() = suspendCoroutine<Unit> { cont ->
        if(!frameListenerInitialized) {
            frameListenerInitialized = true
            addFixedUpdater(Frequency(24.0)) {
                frameReady.invoke()
            }
        }
        launchImmediately {
            frameReady.waitOneBase()
            cont.resume(Unit)
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


