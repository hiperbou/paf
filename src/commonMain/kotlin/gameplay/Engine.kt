package gameplay


import korlibs.image.bitmap.*
import korlibs.image.color.*
import korlibs.image.format.readBitmapSlice
import korlibs.io.async.*
import korlibs.io.file.VfsFile
import korlibs.io.resources.Resourceable
import korlibs.korge.render.*
import korlibs.korge.scene.Scene
import korlibs.korge.view.*
import korlibs.korge.view.Image
import korlibs.korge.view.property.ViewProperty
import korlibs.korge.view.property.ViewPropertyFileRef
import korlibs.math.geom.*
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

class ImageData {
    val vertices = TexturedVertexArray(4, TexturedVertexArray.QUAD_INDICES)
    var baseBitmap: BitmapCoords = Bitmaps.white
    var anchor: Anchor = Anchor.TOP_LEFT
    val sLeft: Float get() = -anchorDispX
    val sTop: Float get() = -anchorDispY
    val sRight: Float get() = sLeft + bwidth
    val sBottom: Float get() = sTop + bheight
    val bwidth: Float get() = baseBitmap.width.toFloat()
    val bheight: Float get() = baseBitmap.height.toFloat()
    val frameOffsetX: Float get() = baseBitmap.frameOffsetX.toFloat()
    val frameOffsetY: Float get() = baseBitmap.frameOffsetY.toFloat()
    val frameWidth: Float get() = baseBitmap.frameWidth.toFloat()
    val frameHeight: Float get() = baseBitmap.frameHeight.toFloat()
    val anchorDispXNoOffset: Float get() = (anchor.sx * frameWidth)
    val anchorDispYNoOffset: Float get() = (anchor.sy * frameHeight)
    val anchorDispX: Float get() = (anchorDispXNoOffset - frameOffsetX)
    val anchorDispY: Float get() = (anchorDispYNoOffset - frameOffsetY)
    var smoothing: Boolean = true
    var renderBlendMode: BlendMode = BlendMode.NORMAL

    fun drawVertices(ctx: RenderContext) {
        ctx.useBatcher { batch ->
            //batch.texture1212
            //batch.setTemporalUniforms(_programUniforms) {
            batch.drawVertices(
                vertices, ctx.getTex(baseBitmap).base, smoothing, renderBlendMode,
            )
            //}
        }
    }

    fun computeVertices(globalMatrix: Matrix, renderColorMul: RGBA) {
        vertices.quad(0, sLeft, sTop, bwidth, bheight, globalMatrix, baseBitmap, renderColorMul)
    }

}

//abstract class Process(parent: Container) : OpenImage(emptyImage) {
abstract class Process(parent: Container) : Container(), Anchorable {
    companion object {
        val emptyImage = Bitmap32(1,1)
    }

    private val imageData = ImageData()
    override var anchor: Anchor by imageData::anchor
    var bitmap: BitmapCoords by imageData::baseBitmap
    var smoothing: Boolean by imageData::smoothing

    private var _graph = 0
    var graph: Int
        get() =  _graph
        set(value) {
            _graph = value
            bitmap = getImage(value)
        }

    override fun renderInternal(ctx: RenderContext) {
        imageData.computeVertices(globalMatrix, renderColorMul)
        imageData.drawVertices(ctx)
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


