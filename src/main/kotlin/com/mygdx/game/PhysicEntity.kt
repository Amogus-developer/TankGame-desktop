package com.mygdx.game

import alexey.client.utils.misc.draw
import alexey.server.utils.level.Entity
import alexey.tools.common.misc.getFloat
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body

class PhysicEntity(val body: Body,
                   textureRegion: TextureRegion,
                   entity: Entity): ImageEntity(textureRegion, entity) {

    private val speed = entity.properties.getFloat("speed", 0.4F)

    override fun render(spriteBatch: SpriteBatch) {
        spriteBatch.draw(textureRegion, body, entity)
    }

    override fun getPosition(): Vector2 = body.position
    override fun getCenter(): Vector2 = body.worldCenter
    override fun getBodylinearVelocity(): Vector2 = body.linearVelocity


    override fun applyForceToCenter(force: Vector2) {
        body.applyForceToCenter(force, true)
    }

    override fun moveAt(position: Vector2) {
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