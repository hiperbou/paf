package gameplay

import entity.Arrow
import entity.Ball


class GameState() {
    var paused = false
    var pauseBalls = false
    var restarting = false


    var escenario = 0

    var vidas = 4
    var puntos = 0
    var porcentaje = 0


    val arrowCollisions = Collisions<Arrow>()
    val ballCollision = Collisions<Ball>()
    //val playerCollision = Collisions<Player>()

    fun resetCollisions(){
        arrowCollisions.reset()
        ballCollision.reset()
    }
}

var currentGameState = GameState()

var record = 0