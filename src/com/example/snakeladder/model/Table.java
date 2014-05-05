package com.example.snakeladder.model;

import java.util.ArrayList;

public class Table {
	/*
	 * Property
	 */
	public int start = 1;
	public int finish = 50;

	public ArrayList<TableCell> cell;

	/*
	 * Method
	 */
	public Table(int startX, int startY, int cellSize) {
		int xIndex = 0, yIndex = 0;

		cell = new ArrayList<TableCell>();
		TableCell c;
		for (int i = 1; i <= finish; i++) {
			c = new TableCell();
			c.x = startX + (cellSize * xIndex);
			c.y = startY - (cellSize * yIndex);
			cell.add(c);

			if (i % 10 == 0) {
				xIndex = 0;
				yIndex++;
			} else {
				xIndex++;
			}
		}
	}
}
