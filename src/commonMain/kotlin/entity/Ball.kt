package entity

import com.soywiz.korge.view.Image
import com.soywiz.korim.bitmap.BmpSlice
import gameplay.currentGameState

class Ball(
        texture: BmpSlice, anchorX: Double = 0.0, anchorY: Double = 0.0
) : Image(texture, anchorX, anchorY)
{
    var alive = true
    init {
        currentGameState.ballCollision.add(this)
    }

    fun destroy() {
        alive = false
        removeFromParent()
        currentGameState.ballCollision.remove(this)
    }
}
