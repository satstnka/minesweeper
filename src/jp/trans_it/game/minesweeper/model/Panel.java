package jp.trans_it.game.minesweeper.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Panel {
	private Cell[][] cells;
	private int width;
	private int height;
	private int numberOfBombs;


	public Panel(int width, int height, int bombCount) {
		initialize(width, height, bombCount);
	}

	public void initialize(int width, int height, int bombCount) {
		this.width = width;
		this.height = height;
		this.numberOfBombs = bombCount;

		this.cells = new Cell[width][height];
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				Cell cell = new Cell(i, j);
				this.cells[i][j] = cell;
			}
		}

		this.setBombs();
	}

	private void setBombs() {
		List<Cell> list = new ArrayList<Cell>();
		for(int x = 0; x < this.width; x++) {
			for(int y = 0; y < this.height; y++) {
				Cell cell = this.cells[x][y];
				list.add(cell);
			}
		}
		Collections.shuffle(list);

		for(int i = 0; i < this.numberOfBombs; i++) {
			Cell cell = list.get(i);
			cell.setHavingBomb(true);
		}

		for(int x = 0; x < this.width; x++) {
			for(int y = 0; y < this.height; y++ ) {
				Cell cell = this.cells[x][y];
				int count = countNeighborBombs(cell);
				cell.setNeighborBombs(count);
			}
		}
	}

	private int countNeighborBombs(Cell cell) {
		int count = 0;
		for(int x = cell.getX() - 1; x <= cell.getX() + 1; x++) {
			for(int y = cell.getY() - 1; y <= cell.getY() + 1; y++) {
				if(x >= 0 && x < this.width && y >= 0 && y < this.height) {
					if(cells[x][y].isHavingBomb()) {
						count++;
					}
				}

			}
		}
		return count;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getNumberOfBombs() {
		return numberOfBombs;
	}

	public void setNumberOfBombs(int numberOfBombs) {
		this.numberOfBombs = numberOfBombs;
	}

	public Cell getCell(int x, int y) {
		Cell cell = null;
		if(x >= 0 && x < this.width && y >= 0 && y < this.height) {
			cell = this.cells[x][y];
		}
		return cell;
	}

	public void toggleFlag(int x, int y) {
		Cell cell = this.getCell(x, y);
		if(!cell.isOpened()) {
			boolean flagged = cell.isFlagged();
			cell.setFlagged(!flagged);
		}
	}

	public void open(int x, int y) {
		Cell cell = this.getCell(x,  y);
		if(!cell.isOpened()) {
			cell.setFlagged(false);
			cell.setOpened(true);
			if(!cell.isHavingBomb() && cell.getNeighborBombs() == 0) {
				for(int neighborX = x - 1; neighborX <= x + 1; neighborX++) {
					for(int neighborY = y - 1; neighborY <= y + 1; neighborY++) {
						Cell neighbor = this.getCell(neighborX,  neighborY);
						if(neighbor != null && neighbor != cell && !neighbor.isOpened()) {
							this.open(neighborX, neighborY);
						}
					}
				}
			}
		}
	}

	public boolean judgesGameOver() {
		boolean gameOver = false;
		for(int x = 0; x < this.width; x++) {
			for(int y = 0; y < this.height; y++) {
				Cell cell = this.getCell(x,  y);
				if(cell.isOpened() && cell.isHavingBomb()) {
					gameOver = true;
				}
			}
		}
		return gameOver;
	}

	public int countClosed() {
		int count = 0;
		for(int x = 0; x < this.width; x++) {
			for(int y = 0; y < this.height; y++) {
				Cell cell = this.getCell(x,  y);
				if(!cell.isOpened()) {
					count++;
				}
			}
		}
		return count;
	}

	public boolean judgesCompleted() {
		boolean completed = (this.numberOfBombs == countClosed());
		for(int x = 0; x < this.width; x++) {
			for(int y = 0; y < this.height; y++) {
				Cell cell = this.getCell(x,  y);
				if(!cell.isOpened() && !cell.isFlagged()) {
					completed = false;
				}
			}
		}
		return completed;
	}

	public int countFlagged() {
		int count = 0;
		for(int x = 0; x < this.width; x++) {
			for(int y = 0; y < this.height; y++) {
				Cell cell = this.getCell(x,  y);
				if(cell.isFlagged()) {
					count++;
				}
			}
		}
		return count;
	}

	private static void displayNumber(int number) {
		if(number < 10) {
			System.out.print(Const.zenkakuNumbers[number]);
		}
		else {
			System.out.print(Integer.toString(number));
		}
	}

	public void display() {
		System.out.print("　 ");
		for(int x = 0;  x < this.width; x++) {
			displayNumber(x);
		}
		System.out.println("");
		System.out.println("");

		for(int y = 0; y < this.height; y++) {
			displayNumber(y);
			System.out.print(" ");
			for(int x = 0; x < this.width; x++) {
				Cell cell = this.getCell(x,  y);
				System.out.print(cell.getString());
			}
			System.out.println("");
		}
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		Panel panel = new Panel(10, 10, 10);

		boolean gameOver = false;
		boolean completed = false;

		while(!gameOver && !completed) {
			panel.display();
			int remain = panel.getNumberOfBombs() - panel.countFlagged();
			System.out.println("    のこり: " + remain);

			System.out.println("");
			System.out.println("[O] 開く  [F] フラグ  [Q]終了");
			String operation = scanner.next();
			operation = operation.toUpperCase();

			if(operation.equals("O") || operation.equals("F")) {
				System.out.println("よこ");
				int x = scanner.nextInt();
				System.out.println("たて");
				int y = scanner.nextInt();

				if(operation.equals("O")) {
					panel.open(x,  y);
				}
				else if(operation.equals("F")) {
					panel.toggleFlag(x,  y);
				}

				gameOver = panel.judgesGameOver();
				completed = panel.judgesCompleted();
			}
			else if(operation.equals("Q")) {
				gameOver = true;
			}
		}

		panel.display();
		if(completed) {
			System.out.println("おめでとうございます!!!");
		}
		else if(gameOver){
			System.out.println("残念。 (>_<)");
		}

		scanner.close();
	}
}
