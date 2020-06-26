package entity

import com.soywiz.korge.view.Image
import com.soywiz.korim.bitmap.BmpSlice
import gameplay.currentGameState

class Arrow(
        texture: BmpSlice, anchorX: Double = 0.0, anchorY: Double = 0.0
) : Image(texture, anchorX, anchorY)
{
    var alive = true
    init{
        currentGameState.arrowCollisions.add(this)
    }

    fun destroy() {
        alive = false
        removeFromParent()
        currentGameState.arrowCollisions.remove(this)
    }
}