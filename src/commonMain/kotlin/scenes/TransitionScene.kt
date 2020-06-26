package scenes

import com.soywiz.klock.seconds
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.scene.sleep
import com.soywiz.korge.view.Container

class TransitionScene() : Scene() {

    override suspend fun Container.sceneInit() {
        println("TransitionScene.sceneInit")
        sleep(1.seconds)
    }


    override suspend fun sceneAfterInit() {
        super.sceneAfterInit()
        println("TransitionScene.3")
        sleep(1.seconds)
        println("TransitionScene.2")
        sleep(1.seconds)
        println("TransitionScene.1")
        sleep(1.seconds)
        sceneContainer.changeTo<LoadingScene>()
    }

}