import com.soywiz.korge.Korge
import com.soywiz.korge.scene.Module
import com.soywiz.korge.scene.Scene
import com.soywiz.korim.color.Colors
import com.soywiz.korinject.AsyncInjector
import com.soywiz.korma.geom.SizeInt
import scenes.*
import testScenes.AtlasAnchorBugScene
import kotlin.reflect.KClass


suspend fun main() = Korge(Korge.Config(module = PafGameModule))

object PafGameModule : Module() {
	override val title = "Paf!"
	override val size = SizeInt(320,240)
	override val windowSize = SizeInt(800, 600)
	override val targetFps = 60.0

	override val bgcolor = Colors["#2b2b2b"]
	override val mainScene: KClass<out Scene> = AtlasAnchorBugScene::class

	override suspend fun init(injector: AsyncInjector): Unit = injector.run {
		//mapInstance(GameState())
		mapPrototype { LoadingScene(/*get()*/) }
		mapPrototype { TitleScene(/*get()*/) }
		mapPrototype { TransitionScene(/*get()*/) }
		mapPrototype { GameScene(/*get()*/) }
		mapPrototype { TransitionGameScene(/*get()*/) }
		mapPrototype { TransitionTitleScene(/*get()*/) }

		mapPrototype { AtlasAnchorBugScene(/*get()*/) }
		/*mapPrototype { RestartSnakeScene(/*get()*/) }
		mapPrototype { RestartPacmanScene(/*get()*/) }
		mapPrototype { RestartMarioScene(/*get()*/) }
		mapPrototype { TransitionToPacmanScene(/*get()*/) }
		mapPrototype { TransitionToMarioScene(/*get()*/) }
		mapPrototype { SnakeGameScene(/*get()*/) }
		mapPrototype { PacmanGameScene(/*get()*/) }
		mapPrototype { MarioGameScene(/*get()*/) }*/
	}
}
