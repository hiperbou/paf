package testScenes

import com.soywiz.korge.atlas.readAtlas
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.anchor
import com.soywiz.korge.view.image
import com.soywiz.korge.view.position
import com.soywiz.korim.bitmap.slice
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs

class AtlasAnchorBugScene() : Scene() {

    override suspend fun Container.sceneInit() {
        val imagePng = resourcesVfs["fpg/020.png"].readBitmap().slice()
        val imageFromAtlas = resourcesVfs["fpg.atlas.json"].readAtlas()["020.png"]

        image(imagePng){
            position(100,100)
            anchor(0.5, 0.5)
        }
        image(imagePng){
            position(100,120)
            anchor(0.5, 0.5)
            scaleX = -1.0
        }
        image(imageFromAtlas){
            position(112,100)
            anchor(0.5, 0.5)
        }
        image(imageFromAtlas){
            position(112,120)
            anchor(0.5, 0.5)
            scaleX = -1.0
        }
    }
}