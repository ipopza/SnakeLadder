package com.example.snakeladder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.SparseArray;

import com.example.snakeladder.model.Ladder;
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
	private TextureRegion mPlayer1TextTextureRegion;
	private TextureRegion mPlayer2TextTextureRegion;
	private TextureRegion diceTextureRegion;
	private ButtonSprite diceButton;

	private Font mFont;
	private Text playerText;

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
	public Engine onCreateEngine(EngineOptions pEngineOptions) {
		Engine engine = new LimitedFPSEngine(pEngineOptions, 60);
		return engine;
	}

	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mGrassBackground = new RepeatingSpriteBackground(CAMERA_WIDTH, CAMERA_HEIGHT, this.getTextureManager(),
				AssetBitmapTextureAtlasSource.create(this.getAssets(), "gfx/background.png"), this.getVertexBufferObjectManager());

		ITexture p1Texture = loadTexture("gfx/p1.png");
		ITexture p2Texture = loadTexture("gfx/p2.png");
		ITexture p1TextTexture = loadTexture("gfx/p1_text.png");
		ITexture p2TextTexture = loadTexture("gfx/p2_text.png");
		ITexture diceTexture = loadTexture("gfx/dice.png");

		p1Texture.load();
		p2Texture.load();
		p1TextTexture.load();
		p2TextTexture.load();
		diceTexture.load();

		this.mPlayer1TextureRegion = TextureRegionFactory.extractFromTexture(p1Texture);
		this.mPlayer2TextureRegion = TextureRegionFactory.extractFromTexture(p2Texture);
		this.mPlayer1TextTextureRegion = TextureRegionFactory.extractFromTexture(p1TextTexture);
		this.mPlayer2TextTextureRegion = TextureRegionFactory.extractFromTexture(p2TextTexture);
		this.diceTextureRegion = TextureRegionFactory.extractFromTexture(diceTexture);

		this.mFont = FontFactory.create(getFontManager(), getTextureManager(), 512, 120, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 24,
				Color.RED.hashCode());
		this.mFont.load();

		SparseArray<Ladder> ladders = getLadderList();
		Activity activity = MainActivity.this;
		this.p1 = new Player(activity, 1, ladders);
		p1.isTurn = true;

		this.p2 = new Player(activity, 2, ladders);
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

		diceButton = new ButtonSprite(START_X + 150, START_Y + 20, diceTextureRegion, this.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionUp() && this.isEnabled()) {
					Move("Player 1 ");
					return true;
				}
				return false;
			}
		};
		scene.attachChild(diceButton);

		playerText = new Text(START_X + 150 + 85, START_Y + 40, this.mFont, "Player 1 กรุณาทอยลูกเต๋า", new TextOptions(HorizontalAlign.LEFT),
				getVertexBufferObjectManager());
		scene.attachChild(playerText);

		Sprite p2Text = new Sprite(START_X, START_Y + 25, 120, 30, this.mPlayer2TextTextureRegion, this.getVertexBufferObjectManager());
		scene.attachChild(p2Text);
		Sprite p1Text = new Sprite(START_X, START_Y + 60, 120, 30, this.mPlayer1TextTextureRegion, this.getVertexBufferObjectManager());
		scene.attachChild(p1Text);

		scene.registerTouchArea(diceButton);

		return scene;
	}

	/*
	 * Helper
	 */
	private SparseArray<Ladder> getLadderList() {
		SparseArray<Ladder> list = new SparseArray<Ladder>();

		// Ladder
		Ladder l = new Ladder(2, 23, Ladder.LADDER, 0, 0, 150, 230);
		list.put(2, l);

		l = new Ladder(28, 47, Ladder.LADDER, 0, 0, 430, 90);
		list.put(28, l);

		l = new Ladder(37, 44, Ladder.LADDER, 0, 0, 220, 90);
		list.put(37, l);

		// Snake
		l = new Ladder(30, 8, Ladder.SNAKE, 0, 0, 500, 365);
		list.put(30, l);

		l = new Ladder(36, 14, Ladder.SNAKE, 0, 0, 430, 300);
		list.put(36, l);

		return list;
	}

	private void Move(String player) {
		diceButton.setEnabled(false);
		final int dice = doDice();
		Utility.showDialog(MainActivity.this, "DICE", player + " ทอยลูกเต๋าได้ " + dice + " แต้ม", new CallBack() {

			@Override
			public void onCallBack() {
				if (p1.isTurn) {
					p1.Move(dice, playerText, playerMoveCallBack);
					p1.isTurn = false;
					p2.isTurn = true;
				} else if (p2.isTurn) {
					p2.Move(dice, playerText, playerMoveCallBack);
					p2.isTurn = false;
					p1.isTurn = true;
				}
			}
		});
	}

	CallBack playerMoveCallBack = new CallBack() {

		@Override
		public void onCallBack() {
			if (p2.isTurn) {
				Move("Computer ");
			}else{
				diceButton.setEnabled(true);
			}
		}
	};

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