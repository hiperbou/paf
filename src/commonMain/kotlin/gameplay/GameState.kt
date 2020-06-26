package gameplay


import scenes.GameScene


class GameState() {
    var paused = false
    var pauseBalls = false
    var restarting = false


    var escenario = 0

    var vidas = 4
    var puntos = 0
    var porcentaje = 0


    val arrowCollisions = Collisions<GameScene.disparo>()
    val ballCollision = Collisions<GameScene.bola>()
    //val playerCollision = Collisions<Player>()

    fun resetCollisions(){
        arrowCollisions.reset()
        ballCollision.reset()
    }
}

var currentGameState = GameState()

var record = 0