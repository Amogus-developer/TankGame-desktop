package com.mygdx.game

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
	private var textures = emptyArray<TextureRegion>()

	lateinit var player: Entity
	private val enemies = ArrayList<Entity>()
	private val walls = ArrayList<Entity>()

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
				when(blankEntity.type) {                    // Супер функция хитбоксов :)  v
					"player"->   player = Entity(textures[Entity.PLAYER], world.createBody(blankEntity), blankEntity.width)
					"enemy" -> enemies.add(Entity(textures[Entity.ENEMY], world.createBody(blankEntity), blankEntity.width, 1f))
					else -> walls.add(Entity(textures[blankEntity.index], world.createBody(blankEntity), blankEntity.width))
				}                                                   // ^ Номер текстуры в set.json (слева на право)
			}
		}
	}

	override fun create() {

		world = World(Vector2(0f, 0f), true)

		spriteBatch = SpriteBatch()

		camera = OrthographicCamera(50f, 25f)
		camera.position.set(Vector2(0f, 0f), 0f)

		setTexture = Texture("src/main/resources/icons/set.png")
		textures = Array(5) {
			val row = it / 2
			val col = it.mod(2)
			TextureRegion(setTexture, 320 * col, 320 * row, 320, 320 )
		}

		Gdx.input.inputProcessor = forceController
		Gdx.gl.glClearColor(0.18f, 0.56f, 0.4f, 225f)

		loadMap("src/main/resources/icons/map.json", "src/main/resources/icons/set.json")

	}

	private fun update(){
		world.step(1/60f, 4, 4)
		camera.position.set(player.getPosition(), 0F)
		camera.update()
		spriteBatch.projectionMatrix = camera.combined

		player.applyForceToCenter(forceController.getForce())
		enemies.forEach { it.moveAt(player.getPosition()) }
		speedLimiter(player)
		friction(player)
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