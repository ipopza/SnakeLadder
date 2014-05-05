package com.example.snakeladder.model;

public class Ladder {
	public static final int LADDER = 1;
	public static final int SNAKE = 2;

	public int positionFrom;
	public int positionTo;
	public int type;

	public float pX;
	public float pY;

	public float mX;
	public float mY;

	public Ladder(int positionFrom, int positionTo, int type, float pX, float pY, float mX, float mY) {
		this.positionFrom = positionFrom;
		this.positionTo = positionTo;
		this.type = type;
		this.pX = pX;
		this.pY = pY;
		this.mX = mX;
		this.mY = mY;
	}

}
