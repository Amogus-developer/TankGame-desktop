package com.mygdx.game

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.Shape

class Entity(private val textureRegion: TextureRegion,
             val body: Body,
             private val doubleRadius: Float = 1F,
             private val speed: Float = 0.6F) {

    private val radius: Float = doubleRadius / 2F


    fun render(spriteBatch: SpriteBatch) {
        val x = body.position.x
        val y = body.position.y
        spriteBatch.draw(textureRegion, x, y,
            x + radius, y + radius,
            doubleRadius, doubleRadius,
            1F, 1F,
            0F)
    }

    fun getPosition(): Vector2 = body.position

    fun applyForceToCenter(force: Vector2) {
        body.applyForceToCenter(force, true)
    }

    fun moveAt(position: Vector2) {
        val v = Vector2(position.x - body.position.x, position.y - body.position.y)
        v.nor()
        v.scl(speed)
        applyForceToCenter(v)
    }



    companion object {
        const val PLAYER = 3
        const val ENEMY = 4
    }
}