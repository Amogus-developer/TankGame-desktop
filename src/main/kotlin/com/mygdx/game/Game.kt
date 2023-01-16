package com.mygdx.game

import alexey.client.utils.misc.toTextureRegions
import alexey.server.utils.level.createBody
import alexey.server.utils.tiled.deserializers.ImmutableIntListDeserializer
import alexey.server.utils.tiled.map.TiledMap
import alexey.server.utils.tiled.tileset.Tileset
import alexey.server.utils.tiled.toLevel
import alexey.tools.common.collections.ImmutableIntList
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule

class Game: Game() {

	private lateinit var world: World
	private lateinit var camera: OrthographicCamera
	private lateinit var spriteBatch: SpriteBatch

	private lateinit var setTexture: Texture
	private var textures = emptyList<TextureRegion>()

	private lateinit var player: GameEntity
	private val enemies = ArrayList<GameEntity>()
	private val walls = ArrayList<GameEntity>()

	private val forceController = ForceController()

	private val objectMapper = ObjectMapper().apply {
		val module = SimpleModule()
		module.addDeserializer(ImmutableIntList::class.java, ImmutableIntListDeserializer())
		registerModule(module)
	}

	private fun loadMap(mapPath: String, tileSetPath: String){
		val tileSet = Gdx.files.internal(tileSetPath).read().use {
			objectMapper.readValue(it, Tileset::class.java)
		}

		val map = Gdx.files.internal(mapPath).read().use {
			objectMapper.readValue(it, TiledMap::class.java)
		}.toLevel(listOf(tileSet), 1F / 160F)


		map.layers.forEach {
			it.entities.forEach { blankEntity ->
				when(blankEntity.type) {// Супер функция хитбоксов :)  v
					"player"->   player = PhysicEntity(world.createBody(blankEntity), textures[PhysicEntity.PLAYER], blankEntity)
					"enemy" -> enemies.add(PhysicEntity(world.createBody(blankEntity), textures[PhysicEntity.ENEMY], blankEntity))
					else -> walls.add(if (blankEntity.shapes.isEmpty())
						ImageEntity(textures[blankEntity.index], blankEntity) else
						PhysicEntity(world.createBody(blankEntity), textures[blankEntity.index], blankEntity))
				}                                                   //                     ^ Номер текстуры в set.json (слева на право)
			}
		}
	}

	override fun create() {

		world = World(Vector2(0f, 0f), true)

		spriteBatch = SpriteBatch()

		camera = OrthographicCamera(50f, 25f)
		camera.position.set(Vector2(), 0f)

		setTexture = Texture("src/main/resources/icons/set.png")
		textures = setTexture.toTextureRegions(320, 320)

		Gdx.input.inputProcessor = forceController
		Gdx.gl.glClearColor(0.18f, 0.56f, 0.4f, 225f)
		loadMap("src/main/resources/icons/map.json", "src/main/resources/icons/set.json")

	}

	private fun update(){
		world.step(1/60f, 4, 4)
		camera.position.set(player.getCenter(), 0f)
		camera.update()
		spriteBatch.projectionMatrix = camera.combined

		player.applyForceToCenter(forceController.getForce())
		enemies.forEach { it.moveAt(player.getPosition()) }
	}
	override fun render() {
		update()
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

		spriteBatch.begin()

		walls.forEach { it.render(spriteBatch) }
		enemies.forEach { it.render(spriteBatch) }
		player.render(spriteBatch)

		spriteBatch.end()
	}
	override fun resize(width: Int, height: Int) {
		val camWidth: Float = thingsWidth.toFloat() * amountOfThings.toFloat()
		val camHeight = camWidth * (height.toFloat() / width.toFloat())
		camera.viewportWidth = camWidth
		camera.viewportHeight = camHeight
		camera.update()
	}
	override fun dispose() {
		world.dispose()
		spriteBatch.dispose()
		setTexture.dispose()
	}
	companion object {
		private const val thingsWidth = 6
		private const val amountOfThings = 6
	}
}