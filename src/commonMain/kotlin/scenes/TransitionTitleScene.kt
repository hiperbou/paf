package scenes

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container

class TransitionTitleScene() : Scene() {

    override suspend fun Container.sceneInit() {
    }


    override suspend fun sceneAfterInit() {
        super.sceneAfterInit()
        sceneContainer.changeTo<TitleScene>()
    }

}