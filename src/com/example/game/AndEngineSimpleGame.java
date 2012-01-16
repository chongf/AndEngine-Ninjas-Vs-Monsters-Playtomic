package com.example.game;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import org.anddev.andengine.audio.music.Music;
import org.anddev.andengine.audio.music.MusicFactory;
import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.audio.sound.SoundFactory;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.MoveModifier;
import org.anddev.andengine.entity.modifier.MoveXModifier;
import org.anddev.andengine.entity.scene.CameraScene;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import com.playtomic.android.api.Playtomic;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Display;
import android.view.KeyEvent;
import android.widget.Toast;

public class AndEngineSimpleGame extends BaseGameActivity implements
		IOnSceneTouchListener {

	private Camera mCamera;

	// This one is for the font
	private BitmapTextureAtlas mFontTexture;
	private Font mFont;
	private ChangeableText score;

	// this one is for all other textures
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TextureRegion mPlayerTextureRegion;
	private TextureRegion mProjectileTextureRegion;
	private TextureRegion mTargetTextureRegion;
	private TextureRegion mPausedTextureRegion;
	private TextureRegion mWinTextureRegion;
	private TextureRegion mFailTextureRegion;

	// the main scene for the game
	private Scene mMainScene;
	private Sprite player;

	// win/fail sprites
	private Sprite winSprite;
	private Sprite failSprite;

	private LinkedList<Sprite> projectileLL;
	private LinkedList<Sprite> targetLL;
	private LinkedList<Sprite> projectilesToBeAdded;
	private LinkedList<Sprite> TargetsToBeAdded;
	private Sound shootingSound;
	private Music backgroundMusic;
	private boolean runningFlag = false;
	private boolean pauseFlag = false;
	private CameraScene mPauseScene;
	private CameraScene mResultScene;
	private int hitCount;
	private final int maxScore = 5;
	
	@Override
	public Engine onLoadEngine() {

		// getting the device's screen size
		final Display display = getWindowManager().getDefaultDisplay();
		int cameraWidth = display.getWidth();
		int cameraHeight = display.getHeight();

		// setting up the camera [AndEngine's camera , not the one you take
		// pictures with]
		mCamera = new Camera(0, 0, cameraWidth, cameraHeight);

		
		// Engine with varius options
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE,
				new RatioResolutionPolicy(cameraWidth, cameraHeight), mCamera)
				.setNeedsSound(true).setNeedsMusic(true));
		
		
	}

	@Override
	public void onLoadResources() {
		// prepare a container for the image
		mBitmapTextureAtlas = new BitmapTextureAtlas(512, 512,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		// prepare a container for the font
		mFontTexture = new BitmapTextureAtlas(256, 256,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		// setting assets path for easy access
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		// loading the image inside the container
		mPlayerTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mBitmapTextureAtlas, this, "Player.png",
						0, 0);
		mProjectileTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mBitmapTextureAtlas, this,
						"Projectile.png", 64, 0);
		mTargetTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mBitmapTextureAtlas, this, "Target.png",
						128, 0);
		mPausedTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mBitmapTextureAtlas, this, "paused.png",
						0, 64);
		mWinTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mBitmapTextureAtlas, this, "win.png", 0,
						128);
		mFailTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mBitmapTextureAtlas, this, "fail.png", 0,
						256);

		// preparing the font
		mFont = new Font(mFontTexture, Typeface.create(Typeface.DEFAULT,
				Typeface.BOLD), 40, true, Color.BLACK);

		// loading textures in the engine
		mEngine.getTextureManager().loadTexture(mBitmapTextureAtlas);
		mEngine.getTextureManager().loadTexture(mFontTexture);
		mEngine.getFontManager().loadFont(mFont);

		SoundFactory.setAssetBasePath("mfx/");
		try {
			shootingSound = SoundFactory.createSoundFromAsset(mEngine
					.getSoundManager(), this, "pew_pew_lei.wav");
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		MusicFactory.setAssetBasePath("mfx/");

		try {
			backgroundMusic = MusicFactory.createMusicFromAsset(mEngine
					.getMusicManager(), this, "background_music.wav");
			backgroundMusic.setLooping(true);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public Scene onLoadScene() {
		mEngine.registerUpdateHandler(new FPSLogger());

		// creating a new scene for the pause menu
		mPauseScene = new CameraScene(mCamera);
		/* Make the label centered on the camera. */
		final int x = (int) (mCamera.getWidth() / 2 - mPausedTextureRegion
				.getWidth() / 2);
		final int y = (int) (mCamera.getHeight() / 2 - mPausedTextureRegion
				.getHeight() / 2);
		final Sprite pausedSprite = new Sprite(x, y, mPausedTextureRegion);
		mPauseScene.attachChild(pausedSprite);
		// makes the scene transparent
		mPauseScene.setBackgroundEnabled(false);

		// the results scene, for win/fail
		mResultScene = new CameraScene(mCamera);
		winSprite = new Sprite(x, y, mWinTextureRegion);
		failSprite = new Sprite(x, y, mFailTextureRegion);
		mResultScene.attachChild(winSprite);
		mResultScene.attachChild(failSprite);
		// makes the scene transparent
		mResultScene.setBackgroundEnabled(false);

		winSprite.setVisible(false);
		failSprite.setVisible(false);

		// set background color
		mMainScene = new Scene();
		mMainScene
				.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));
		mMainScene.setOnSceneTouchListener(this);

		// set coordinates for the player
		final int PlayerX = this.mPlayerTextureRegion.getWidth() / 2;
		final int PlayerY = (int) ((mCamera.getHeight() - mPlayerTextureRegion
				.getHeight()) / 2);

		// set the player on the scene
		player = new Sprite(PlayerX, PlayerY, mPlayerTextureRegion);
		player.setScale(2);

		// initializing variables
		projectileLL = new LinkedList<Sprite>();
		targetLL = new LinkedList<Sprite>();
		projectilesToBeAdded = new LinkedList<Sprite>();
		TargetsToBeAdded = new LinkedList<Sprite>();

		// settings score to the value of the max score to make sure it appears
		// correctly on the screen
		score = new ChangeableText(0, 0, mFont, String.valueOf(maxScore));
		// repositioning the score later so we can use the score.getWidth()
		score.setPosition(mCamera.getWidth() - score.getWidth() - 5, 5);

		createSpriteSpawnTimeHandler();
		mMainScene.registerUpdateHandler(detect);

		// starting background music
		backgroundMusic.play();
		// runningFlag = true;
		
		restart();
				
		return mMainScene;
	}

	@Override
	public void onLoadComplete() {

	}

	// TimerHandler for collision detection and cleaning up
	IUpdateHandler detect = new IUpdateHandler() {
		@Override
		public void reset() {
		}

		@Override
		public void onUpdate(float pSecondsElapsed) {

			Iterator<Sprite> targets = targetLL.iterator();
			Sprite _target;
			boolean hit = false;

			// iterating over the targets
			while (targets.hasNext()) {
				_target = targets.next();

				// if target passed the left edge of the screen, then remove it
				// and call a fail
				if (_target.getX() <= -_target.getWidth()) {
					removeSprite(_target, targets);
					fail();
					break;
				}
				Iterator<Sprite> projectiles = projectileLL.iterator();
				Sprite _projectile;
				// iterating over all the projectiles (bullets)
				while (projectiles.hasNext()) {
					_projectile = projectiles.next();

					// in case the projectile left the screen
					if (_projectile.getX() >= mCamera.getWidth()
							|| _projectile.getY() >= mCamera.getHeight()
									+ _projectile.getHeight()
							|| _projectile.getY() <= -_projectile.getHeight()) {
						removeSprite(_projectile, projectiles);
						continue;
					}

					// if the targets collides with a projectile, remove the
					// projectile and set the hit flag to true
					if (_target.collidesWith(_projectile)) {
						removeSprite(_projectile, projectiles);
						hit = true;
						break;
					}
				}

				// if a projectile hit the target, remove the target, increment
				// the hit count, and update the score
				if (hit) {
					removeSprite(_target, targets);
					hit = false;
					hitCount++;
					score.setText(String.valueOf(hitCount));
				}
			}

			// if max score , then we are done
			if (hitCount >= maxScore) {
				win();
			}

			// a work around to avoid ConcurrentAccessException
			projectileLL.addAll(projectilesToBeAdded);
			projectilesToBeAdded.clear();

			targetLL.addAll(TargetsToBeAdded);
			TargetsToBeAdded.clear();
		}
	};

	/* safely detach the sprite from the scene and remove it from the iterator */
	public void removeSprite(final Sprite _sprite, Iterator<Sprite> it) {
		runOnUpdateThread(new Runnable() {

			@Override
			public void run() {
				mMainScene.detachChild(_sprite);
			}
		});
		it.remove();
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

		// if the user tapped the screen
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
			final float touchX = pSceneTouchEvent.getX();
			final float touchY = pSceneTouchEvent.getY();
			shootProjectile(touchX, touchY);
			return true;
		}
		return false;
	}

	/* shoots a projectile from the player's position along the touched area */
	private void shootProjectile(final float pX, final float pY) {

		int offX = (int) (pX - player.getX());
		int offY = (int) (pY - player.getY());
		if (offX <= 0)
			return;

		final Sprite projectile;
		// position the projectile on the player
		projectile = new Sprite(player.getX(), player.getY(),
				mProjectileTextureRegion.deepCopy());
		mMainScene.attachChild(projectile, 1);

		int realX = (int) (mCamera.getWidth() + projectile.getWidth() / 2.0f);
		float ratio = (float) offY / (float) offX;
		int realY = (int) ((realX * ratio) + projectile.getY());

		int offRealX = (int) (realX - projectile.getX());
		int offRealY = (int) (realY - projectile.getY());
		float length = (float) Math.sqrt((offRealX * offRealX)
				+ (offRealY * offRealY));
		float velocity = 480.0f / 1.0f; // 480 pixels / 1 sec
		float realMoveDuration = length / velocity;

		// defining a move modifier from the projectile's position to the
		// calculated one
		MoveModifier mod = new MoveModifier(realMoveDuration,
				projectile.getX(), realX, projectile.getY(), realY);
		projectile.registerEntityModifier(mod.deepCopy());

		projectilesToBeAdded.add(projectile);
		// plays a shooting sound
		shootingSound.play();
	}

	// adds a target at a random location and let it move along the x-axis
	public void addTarget() {
		Random rand = new Random();

		int x = (int) mCamera.getWidth() + mTargetTextureRegion.getWidth();
		int minY = mTargetTextureRegion.getHeight();
		int maxY = (int) (mCamera.getHeight() - mTargetTextureRegion
				.getHeight());
		int rangeY = maxY - minY;
		int y = rand.nextInt(rangeY) + minY;

		Sprite target = new Sprite(x, y, mTargetTextureRegion.deepCopy());
		mMainScene.attachChild(target);

		int minDuration = 2;
		int maxDuration = 4;
		int rangeDuration = maxDuration - minDuration;
		int actualDuration = rand.nextInt(rangeDuration) + minDuration;

		MoveXModifier mod = new MoveXModifier(actualDuration, target.getX(),
				-target.getWidth());
		target.registerEntityModifier(mod.deepCopy());

		TargetsToBeAdded.add(target);

	}

	// a Time Handler for spawning targets, triggers every 1 second
	private void createSpriteSpawnTimeHandler() {
		TimerHandler spriteTimerHandler;
		float mEffectSpawnDelay = 1f;

		spriteTimerHandler = new TimerHandler(mEffectSpawnDelay, true,
				new ITimerCallback() {

					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {

						addTarget();
					}
				});

		getEngine().registerUpdateHandler(spriteTimerHandler);
	}

	/* to restart the game and clear the whole screen */
	public void restart() {

		runOnUpdateThread(new Runnable() {

			@Override
			// to safely detach and re-attach the sprites
			public void run() {
				mMainScene.detachChildren();
				mMainScene.attachChild(player, 0);
				mMainScene.attachChild(score);
			}
		});

		// resetting everything
		hitCount = 0;
		score.setText(String.valueOf(hitCount));
		projectileLL.clear();
		projectilesToBeAdded.clear();
		TargetsToBeAdded.clear();
		targetLL.clear();
		
		// if there are multiple levels, you should further abstract this to include level name
		Playtomic.Log().levelCounterMetric("Restart",  "Level1", false); 

		// for testing - we want to see data immediately. out-comment if you want events sent in batches
		Playtomic.Log().forceSend();

	}

	@Override
	// pauses the music and the game when the game goes to the background
	protected void onPause() {
		if (runningFlag) {
			pauseMusic();
			if (mEngine.isRunning()) {
				pauseGame();
				pauseFlag = true;
			}
		}
		super.onPause();
		
		Playtomic.Log().customMetric("Paused",  "MainMenu", false); 

		// for testing - we want to see data immediately. out-comment if you want events sent in batches
		Playtomic.Log().forceSend();

	}

	
	@Override
	public void onResumeGame() {
		super.onResumeGame();
		// shows this Toast when coming back to the game
		if (runningFlag) {
			if (pauseFlag) {
				pauseFlag = false;
				Toast.makeText(this, "Menu button to resume",
						Toast.LENGTH_SHORT).show();
			} else {
				// in case the user clicks the home button while the game on the
				// resultScene
				resumeMusic();
				mEngine.stop();
			}
		} else {
			runningFlag = true;
		}
		
		Playtomic.Log().customMetric("Resume",  "MainMenu", false); 

		// for testing - we want to see data immediately. out-comment if you want events sent in batches
		Playtomic.Log().forceSend();

	}
	
	

	public void pauseMusic() {
		if (runningFlag)
			if (backgroundMusic.isPlaying()){
				backgroundMusic.pause();
				Playtomic.Log().customMetric("PauseMusic",  "MainMenu", false); 				

				// for testing - we want to see data immediately. out-comment if you want events sent in batches
				Playtomic.Log().forceSend();

			}
	}

	public void resumeMusic() {
		if (runningFlag)
			if (!backgroundMusic.isPlaying()){
				backgroundMusic.resume();
				Playtomic.Log().customMetric("ResumeMusic",  "MainMenu", false); 

				// for testing - we want to see data immediately. out-comment if you want events sent in batches
				Playtomic.Log().forceSend();

			}
	}

	public void fail() {
		if (mEngine.isRunning()) {
			winSprite.setVisible(false);
			failSprite.setVisible(true);
			mMainScene.setChildScene(mResultScene, false, true, true);
			mEngine.stop();
			
			// if you have multiple levels, further abstract this to allow level names
			Playtomic.Log().levelCounterMetric("Failed",  "Level1", false); 

			// for testing - we want to see data immediately. out-comment if you want events sent in batches
			Playtomic.Log().forceSend();

		}
	}

	public void win() {
		if (mEngine.isRunning()) {
			failSprite.setVisible(false);
			winSprite.setVisible(true);
			mMainScene.setChildScene(mResultScene, false, true, true);
			mEngine.stop();
			
			// if you have multiple levels, further abstract this to allow level names
			Playtomic.Log().levelCounterMetric("Win",  "Level1", false); 
			
			// for testing - we want to see data immediately. out-comment if you want events sent in batches
			Playtomic.Log().forceSend();

		}
	}

	public void pauseGame() {
		if (runningFlag) {
			mMainScene.setChildScene(mPauseScene, false, true, true);
			mEngine.stop();
		}
	}
	
	public void unPauseGame(){
		mMainScene.clearChildScene();
	}

	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		// if menu button is pressed
		if (pKeyCode == KeyEvent.KEYCODE_MENU
				&& pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			if (mEngine.isRunning() && backgroundMusic.isPlaying()) {
				pauseMusic();
				pauseFlag = true;
				pauseGame();
				Toast.makeText(this, "Menu button to resume",
						Toast.LENGTH_SHORT).show();
			} else {
				if (!backgroundMusic.isPlaying()) {
					unPauseGame();
					pauseFlag = false;
					resumeMusic();
					mEngine.start();
				}
				return true;
			}
			// if back key was pressed
		} else if (pKeyCode == KeyEvent.KEYCODE_BACK
				&& pEvent.getAction() == KeyEvent.ACTION_DOWN) {

			if (!mEngine.isRunning() && backgroundMusic.isPlaying()) {
				mMainScene.clearChildScene();
				mEngine.start();
				restart();
				return true;
			}
			return super.onKeyDown(pKeyCode, pEvent);
		}
		return super.onKeyDown(pKeyCode, pEvent);
	}
}

// for time handler
// http://www.andengine.org/forums/tutorials/using-timer-s-sprite-spawn-example-t463.html