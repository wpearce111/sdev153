package com.wpearce.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {
	final Drop game;

	// Utilities
	private float screenWidth;
	private float screenHeight;

	// Declaring asset
	Texture dropImage;
	Texture bucketImage;
	Sound dropSound;
	Music rainMusic;
	OrthographicCamera camera;
	Rectangle bucket;
	Array<Rectangle> raindrops;
	long lastDropTime;
	int dropsGathered;

	// Gameplay variables
	private int raindropSpeed = 100;
	private int bucketSpeed = 400;

	public GameScreen(final Drop game){
		this.game = game;

		// Load assets
		dropImage = new Texture(Gdx.files.internal("DropImage.png"));
		bucketImage = new Texture(Gdx.files.internal("BucketImage.png"));
		dropSound = Gdx.audio.newSound(Gdx.files.internal("DropSound.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("RainSound.mp3"));
		rainMusic.setLooping(true);

		// Create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 960, 540);
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();

		// Create a rectangle to logically represent the bucket
		bucket = new Rectangle();
		bucket.width = 64;
		bucket.height = 64;
		bucket.x = (screenWidth/2) - (bucket.width/2);
		bucket.y = bucket.height/2;

		// Create raindrops array and spawn the first raindrop
		raindrops = new Array<Rectangle>();
		spawnRaindrop();
	}

	private void spawnRaindrop(){
		Rectangle raindrop = new Rectangle();
		raindrop.width = 64;
		raindrop.height = 64;
		raindrop.x = MathUtils.random(0, screenWidth-bucket.width);
		raindrop.y = screenHeight;

		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}

	@Override
	public void render (float delta) {
		// Clear the last frame and set background color
		ScreenUtils.clear(0.02353f, 0.05098f, 0.07451f, 1);

		// Update the cameras matrix to ensure proper position and other properties
		camera.update();

		// Set units to the units of the current camera viewport
		game.batch.setProjectionMatrix(camera.combined);

		// Render the game
		game.batch.begin();

		game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 0, screenHeight);
		game.batch.draw(bucketImage, bucket.x, bucket.y, bucket.width, bucket.height);
		for (Rectangle raindrop : raindrops) {
			game.batch.draw(dropImage, raindrop.x, raindrop.y, raindrop.width, raindrop.height);
		}

		game.batch.end();

		// User Controls
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			bucket.x -= bucketSpeed * Gdx.graphics.getDeltaTime();
		}
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			bucket.x += bucketSpeed * Gdx.graphics.getDeltaTime();
		}

		// Limit Bucket movement to the screen
		if (bucket.x < 0) {
			bucket.x = 0;
		}
		if (bucket.x > screenWidth - bucket.width) {
			bucket.x = screenWidth - bucket.width;
		}

		// Spawn new raindrops every second
		if (TimeUtils.nanoTime() - lastDropTime > 1000000000) {
			spawnRaindrop();
		}

		// Iterate through raindrops to move and destroy them as needed
		Iterator<Rectangle> iterator = raindrops.iterator();
		while(iterator.hasNext()){
			Rectangle raindrop = iterator.next();

			raindrop.y -= raindropSpeed * Gdx.graphics.getDeltaTime();
			if (raindrop.y + raindrop.height < 0) {
				// Under bottom of screen
				iterator.remove();
			}
			if (raindrop.overlaps(bucket)) {
				dropsGathered++;
				iterator.remove();
				dropSound.play();
			}
		}
	}

	@Override
	public void dispose () {
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		rainMusic.play();
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume(){
	}
}
