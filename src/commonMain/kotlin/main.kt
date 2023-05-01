import korlibs.image.color.Colors
import korlibs.korge.Korge
import korlibs.korge.scene.sceneContainer
import korlibs.math.geom.Size
import scenes.GameScene
import scenes.LoadingScene
import scenes.TitleScene


suspend fun main() = Korge(
	title = "Paf!",
	windowSize = Size(800, 600),
	virtualSize = Size(320, 240),
	targetFps = 60.0,
	backgroundColor = Colors["#2b2b2b"]
) {
	injector
		.mapPrototype { LoadingScene(/*get()*/) }
		.mapPrototype { TitleScene(/*get()*/) }
		.mapPrototype { GameScene(/*get()*/) }

	sceneContainer().changeTo<LoadingScene>()
	//sceneContainer().changeTo<GameScene>()
}
