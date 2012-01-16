package com.example.game;

import java.util.LinkedList;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.MoveXModifier;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.RectangularShape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

public class CollisionTest extends BaseGameActivity {
	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;

	private Camera mCamera;
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TextureRegion mPlayerTextureRegion;
	private TextureRegion mTargetTextureRegion;
	private Scene scene;
	private Sprite player, target;

	@Override
	public void onLoadComplete() {
		// TODO Auto-generated method stub

	}

	@Override
	public Engine onLoadEngine() {
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		final EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(
						CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
		return new Engine(engineOptions);
	}

	@Override
	public void onLoadResources() {
		// prepare a container for the image
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(128, 128,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		// loading the image inside the container
		this.mPlayerTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mBitmapTextureAtlas, this, "gfx/Player.png",
						0, 0);

		this.mTargetTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mBitmapTextureAtlas, this, "gfx/Target.png",
						64, 0);

		this.mEngine.getTextureManager().loadTexture(mBitmapTextureAtlas);

	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		// set background color
		scene = new Scene();
		scene.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));

		// set coordinates for the player
		final int PlayerX = this.mPlayerTextureRegion.getWidth() / 2;
		final int TargetX = CAMERA_WIDTH - this.mTargetTextureRegion.getWidth();
		final int PlayerY = (CAMERA_HEIGHT - mPlayerTextureRegion.getHeight()) / 2;

		// set the player on the scene
		player = new Sprite(PlayerX, PlayerY, mPlayerTextureRegion);
		MoveXModifier right = new MoveXModifier(5f, 0, CAMERA_WIDTH - 15);
		MoveXModifier left = new MoveXModifier(5f, CAMERA_WIDTH - 15, 0);
		player.registerEntityModifier(right);
		scene.attachChild(player, 0);

		target = new Sprite(TargetX, PlayerY, mTargetTextureRegion);
		target.registerEntityModifier(left);
		scene.attachChild(target, 0);

		scene.registerUpdateHandler(new IUpdateHandler() {

			@Override
			public void reset() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onUpdate(float pSecondsElapsed) {

				if (player.collidesWith(target)) {
					player.setScale(3);
					target.setScale(1);
				} else {
					player.setScale(1);
					target.setScale(3);
				}

			}
		});
		return scene;
	}

}
