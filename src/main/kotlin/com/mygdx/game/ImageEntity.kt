package com.mygdx.game

import alexey.client.utils.misc.draw
import alexey.server.utils.level.Entity
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body

open class ImageEntity(protected val textureRegion: TextureRegion,
                       protected val entity: Entity, ): GameEntity {
    lateinit var physicEntity: PhysicEntity

    override fun render(spriteBatch: SpriteBatch) {
        spriteBatch.draw(textureRegion, entity)
    }

    override fun getPosition(): Vector2 = Vector2(entity.x, entity.y)

    override fun getCenter(): Vector2 = Vector2(physicEntity.body.position.x + entity.width / 2f, physicEntity.body.position.y + entity.height / 2f)

    override fun getBodylinearVelocity(): Vector2 = physicEntity.body.linearVelocity

    override fun applyForceToCenter(force: Vector2) {

    }

    override fun moveAt(position: Vector2) {

    }
}