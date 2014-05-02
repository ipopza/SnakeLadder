package com.example.snakeladder.model;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.debug.Debug;

public class Player {
	public int id;
	public int position;
	public int x;
	public int y;
	public boolean isTurn;
	public Sprite sprite;

	public Player() {
		position = 1;
	}

	public void setXY(int posX, int posY) {
		x = posX;
		y = posY;
	}

	public void Move(int moveNext, final ButtonSprite button, int iconSize) {
		position += moveNext;

		Debug.e("POS : " + position);
		
		int mX = x + (iconSize * position);
		int mY = y;// + (iconSize * position);
		MoveModifier move = new MoveModifier(5, x, mX, y, mY) {
			@Override
			protected void onModifierStarted(IEntity pItem) {
				super.onModifierStarted(pItem);
				button.setEnabled(false);
			}

			protected void onModifierFinished(IEntity pItem) {
				button.setEnabled(true);
			}
		};
		sprite.registerEntityModifier(move);
	}
}