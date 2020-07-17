package jp.trans_it.game.minesweeper.model;

public class Cell {
	private boolean havingBomb;
	private boolean flagged;
	private boolean opened;
	private int neighborBombs;
	private int x;
	private int y;

	public Cell(int x, int y) {
		initialize(x, y);
	}

	public void initialize(int x, int y) {
		this.havingBomb = false;
		this.flagged = false;
		this.opened = false;

		this.neighborBombs = -1;

		this.x = x;
		this.y = y;
	}

	public boolean isHavingBomb() {
		return havingBomb;
	}

	public void setHavingBomb(boolean havingBomb) {
		this.havingBomb = havingBomb;
	}

	public boolean isFlagged() {
		return flagged;
	}

	public void setFlagged(boolean flagged) {
		this.flagged = flagged;
	}

	public boolean isOpened() {
		return opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	public int getNeighborBombs() {
		return neighborBombs;
	}

	public void setNeighborBombs(int neighborBombs) {
		this.neighborBombs = neighborBombs;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public String getString() {
		String string = null;

		if(this.opened) {
			if(this.havingBomb) {
				string = "×";
			}
			else if(this.neighborBombs > 0) {
				string = Const.zenkakuNumbers[this.neighborBombs];
			}
			else {
				string = "□";
			}
		}
		else {
			if(this.flagged ) {
				string = "∇";
			}
			else {
				string = "＃";
			}
		}

		return string;
	}
}
