package com.mygdx.game

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2

interface GameEntity {
    fun render(spriteBatch: SpriteBatch)
    fun getPosition(): Vector2
    fun getCenter(): Vector2
    fun getBodylinearVelocity(): Vector2
    fun applyForceToCenter(force: Vector2)
    fun moveAt(position: Vector2)
}