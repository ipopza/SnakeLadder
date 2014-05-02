package com.example.snakeladder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;

import com.example.snakeladder.model.Player;

public class MainActivity extends SimpleBaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;
	private static final int ICON_SIZE = 30;
	private static final int START_X = 10;
	private static final int START_Y = 365;

	// ===========================================================
	// Objects
	// ===========================================================
	private Player p1;
	private Player p2;

	// ===========================================================
	// Fields
	// ===========================================================
	private RepeatingSpriteBackground mGrassBackground;

	private TextureRegion mPlayer1TextureRegion;
	private TextureRegion mPlayer2TextureRegion;
	private TextureRegion diceTextureRegion;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mGrassBackground = new RepeatingSpriteBackground(CAMERA_WIDTH, CAMERA_HEIGHT, this.getTextureManager(), AssetBitmapTextureAtlasSource.create(this.getAssets(), "gfx/background.png"),
				this.getVertexBufferObjectManager());

		ITexture p1Texture = loadTexture("gfx/p1.png");
		ITexture p2Texture = loadTexture("gfx/p2.png");
		ITexture diceTexture = loadTexture("gfx/dice.png");

		p1Texture.load();
		p2Texture.load();
		diceTexture.load();

		this.mPlayer1TextureRegion = TextureRegionFactory.extractFromTexture(p1Texture);
		this.mPlayer2TextureRegion = TextureRegionFactory.extractFromTexture(p2Texture);
		this.diceTextureRegion = TextureRegionFactory.extractFromTexture(diceTexture);

		this.p1 = new Player();
		p1.isTurn = true;

		this.p2 = new Player();
		p2.isTurn = false;
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene();
		scene.setBackground(this.mGrassBackground);

		/* Create the face and add it to the scene. */
		int posX = START_X + 17;
		int posY = START_Y - ICON_SIZE;
		p1.sprite = new Sprite(posX, posY, ICON_SIZE, ICON_SIZE, this.mPlayer1TextureRegion, this.getVertexBufferObjectManager());
		p1.setXY(posX, posY);
		scene.attachChild(p1.sprite);

		posY = START_Y - (ICON_SIZE * 2) - 2;
		p2.sprite = new Sprite(posX, posY, ICON_SIZE, ICON_SIZE, this.mPlayer2TextureRegion, this.getVertexBufferObjectManager());
		p2.setXY(posX, posY);
		scene.attachChild(p2.sprite);

		ButtonSprite diceButton = new ButtonSprite(START_X, START_Y + 20, diceTextureRegion, this.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionUp() && this.isEnabled()) {
					Debug.e("DICE!!!");
					if (p1.isTurn) {
						p1.Move(doDice(), this, ICON_SIZE);
						p1.isTurn = false;
						p2.isTurn = true;
					} else if (p2.isTurn) {
						p2.Move(doDice(), this, ICON_SIZE);
						p2.isTurn = false;
						p1.isTurn = true;
					}

					return true;
				}
				return false;
			}
		};
		scene.attachChild(diceButton);
		scene.registerTouchArea(diceButton);

		return scene;
	}

	/*
	 * Helper
	 */
	private int doDice() {
		Random r = new Random();
		return r.nextInt(6) + 1;
	}

	private ITexture loadTexture(final String path) {
		try {
			ITexture texture = new BitmapTexture(getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return getAssets().open(path);
				}
			});
			return texture;
		} catch (IOException e) {
			Debug.e(e);
		}
		return null;
	}
}