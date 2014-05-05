package com.example.snakeladder.model;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.IPathModifierListener;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.ease.EaseSineInOut;

import android.app.Activity;
import android.util.SparseArray;

import com.example.snakeladder.CallBack;
import com.example.snakeladder.Utility;

public class Player {
	public final int MOVE_LEFT = 1;
	public final int MOVE_RIGHT = 2;

	public Activity activity;

	public int id;
	public int position;
	public float x;
	public float y;
	public boolean isTurn;
	public Sprite sprite;

	private SparseArray<Ladder> ladderList;

	public Player(Activity activity, int id, SparseArray<Ladder> ladder) {
		this.activity = activity;
		this.id = id;
		this.position = 1;
		this.ladderList = ladder;
	}

	public void setXY(float posX, float posY) {
		x = posX;
		y = posY;
	}

	private int getMoveDirection(int row) {
		if (row % 2 == 0)
			return MOVE_LEFT;
		else
			return MOVE_RIGHT;
	}

	private float getMoveXPoint(float pX, int row, int originalPosition, int movePosition) {
		if (isFinish()) {
			row = 5;
			movePosition = 50;
		}

		float moveX = 69 * (movePosition - originalPosition);
		int direction = getMoveDirection(row);
		if (direction == MOVE_LEFT) {
			return pX - moveX;
		} else {
			return pX + moveX;
		}
	}

	private Ladder isLadder(int position) {
		return ladderList.get(position);
	}

	private boolean isFinish() {
		if (position >= 50)
			return true;
		return false;
	}

	public void Move(final int moveNext, final Text playerText, final CallBack listener) {
		int originalPosition = position;
		position += moveNext;

		int originalRow = (int) Math.ceil(originalPosition / 10.0);
		int moveRow = (int) Math.ceil(position / 10.0);
		Debug.e("Dice : " + moveNext + ", form " + originalPosition + " to " + position);
		Debug.e("Row form " + originalRow + " to " + moveRow);

		Ladder ladder = isLadder(position);

		Path path;
		float lastX, lastY;
		int pathSize = 2;
		if (moveRow == originalRow || isFinish()) {
			// not change row
			if (ladder != null) {
				pathSize++;
			}

			lastX = getMoveXPoint(x, moveRow, originalPosition, position);
			lastY = y;
			path = new Path(pathSize).to(x, y).to(lastX, lastY);
		} else {
			pathSize = (position % 10 == 1 ? 3 : 4);
			if (ladder != null) {
				pathSize++;
			}

			// move first row
			int maxCell = originalRow * 10;
			lastX = getMoveXPoint(x, originalRow, originalPosition, maxCell);
			lastY = y;
			path = new Path(pathSize).to(x, y).to(lastX, lastY);

			// move top
			lastY = y - 69;
			path.to(lastX, lastY);

			if (pathSize == 4) {
				// move second row
				lastX = getMoveXPoint(lastX, moveRow, maxCell + 1, position);
				path.to(lastX, lastY);
			}
		}

		if (ladder != null) {
			path.to(ladder.mX + 17, ladder.mY - (30 * id) - 2);
			position = ladder.positionTo;
		}

		sprite.registerEntityModifier(new SequenceEntityModifier(new PathModifier(3, path, null, new IPathModifierListener() {
			@Override
			public void onPathStarted(final PathModifier pPathModifier, final IEntity pEntity) {
				if (isFinish()) {
					position = 50;
				}

				playerText.setText("กำลังเดินไปที่ช่อง " + position);
			}

			@Override
			public void onPathWaypointStarted(PathModifier pPathModifier, IEntity pEntity, int pWaypointIndex) {
			}

			@Override
			public void onPathWaypointFinished(PathModifier pPathModifier, IEntity pEntity, int pWaypointIndex) {
			}

			@Override
			public void onPathFinished(final PathModifier pPathModifier, final IEntity pEntity) {
				x = pEntity.getX();
				y = pEntity.getY();
				if (isFinish()) {
					Utility.showDialog(activity, "WINNER", (id == 1 ? "Player 1 ชนะ" : "Computer ชนะ"), new CallBack() {

						@Override
						public void onCallBack() {
							activity.finish();
						}
					});
					playerText.setText(id == 1 ? "Player 1 ชนะ" : "Computer ชนะ");
				} else {
					playerText.setText(id == 1 ? "Computer กำลังทอยลูกเต๋า" : "Player 1 กรุณาทอยลูกเต๋า");
					listener.onCallBack();
				}
			}
		}, EaseSineInOut.getInstance())));
	}
}