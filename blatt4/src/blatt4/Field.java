package blatt4;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * representation of a 3x3-field filled with numbers 1,..., 8. User has the
 * possibility to get access to the neighbors and to check if the field is the
 * result field
 * 
 * @author Elena Resch
 * @author Lukas Kalbertodt
 * @author Mirko Wagner
 * 
 */
public class Field {

	static final int RIGHT = 1; // right neighbor
	static final int LEFT = 2; // left neighbor
	static final int UP = 3; // upper neighbor
	static final int DOWN = 4; // down neighbor
	static final int SIZE = 3; // fixed size of the arrays
	static final int EMPTY = -1; // number equivalent for the empty field

	/**
	 * class variables
	 */
	private int[][] field; // 3x3 int-array
	private List<int[][]> neighbors;
	private int neighborspos;
	private int x; //x-pos of the empty field
	private int y; //y-pos of the emtpy field

	/**
	 * C-tor generates a random 3x3 field with numbers 1,...,8
	 */
	public Field() {
		this.field = generateField();
		setEmptyFieldPos();
		this.neighbors = generateNeighbors();
		this.neighborspos = 0;
	}

	/**
	 * C-tor gets an already generated 3x3 field
	 * 
	 * @param field
	 *            3x3 2d-int-array with numbers 1,..., 8
	 */
	public Field(int[][] field) {
		if (!checkField(field)) {
			throw new IllegalArgumentException(
					"field with wrong size and/or wrong arguments");
		 }
		this.field = field;
		setEmptyFieldPos();
		this.neighbors = generateNeighbors();
		this.neighborspos = 0;
	}

	/**
	 * used if C-tor with given field should be public
	 * 
	 * @param field
	 * @return
	 */
	private boolean checkField(int[][] field) {
		if (field.length != SIZE) {
			return false;
		}
		boolean[] tmp = new boolean[SIZE * SIZE];

		for (int i = 0; i < SIZE; i++) {
			if (field[i].length != SIZE) {
				return false;
			}
			for (int j = 0; j < SIZE; j++) {
				if (field[i][j] == EMPTY) {
					tmp[8] = true;
				} else {
					tmp[field[i][j] - 1] = true;
				}
			}
		}
		for (int i = 0; i < tmp.length; i++) {
			if (!tmp[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the next neighbor of this field.
	 * 
	 * @return null if no neighbors are left, else next neighbor of the intern
	 *         list
	 */
	public Field nextNeighbor() {
		if (neighbors.isEmpty()) {
			return null;
		}
		if (neighborspos == neighbors.size()) {
			return null;
		}
		return new Field(neighbors.get(neighborspos++));
	}

	/**
	 * creates the neighbors of this field and adds them to the intern list
	 * 
	 * @return list of neighbors, might be emtpy
	 */
	private List<int[][]> generateNeighbors() {
		// get the position of the empty field
		int[][] n;
		List<int[][]> neighbors = new ArrayList<int[][]>();

		if ((n = getNeighbor(RIGHT)) != null) {
			neighbors.add(n);
		}
		if ((n = getNeighbor(LEFT)) != null) {
			neighbors.add(n);
		}
		if ((n = getNeighbor(UP)) != null) {
			neighbors.add(n);
		}
		if ((n = getNeighbor(DOWN)) != null) {
			neighbors.add(n);
		}
		return neighbors;
	}

	/**
	 * creates the neighbor with the given movement direction
	 * 
	 * @param direction
	 *            LEFT, RIGHT, UP, DOWN
	 * @return 2d-array or null, if no neighbor with this movement possible,
	 */
	private int[][] getNeighbor(int direction) {
		switch (direction) {
		case RIGHT:
			if (this.y + 1 < SIZE) {
				int[][] n = new int[SIZE][SIZE];
				for (int i = 0; i < SIZE; i++) {
					for (int j = 0; j < SIZE; j++) {
						n[i][j] = field[i][j];
					}
				}
				n[this.x][this.y] = field[this.x][this.y + 1];
				n[this.x][this.y + 1] = EMPTY;
				return n;
			} else {
				return null;
			}
		case LEFT:
			if (this.y - 1 >= 0) {
				int[][] n = new int[SIZE][SIZE];
				for (int i = 0; i < SIZE; i++) {
					for (int j = 0; j < SIZE; j++) {
						n[i][j] = field[i][j];
					}
				}
				n[this.x][this.y] = field[this.x][this.y - 1];
				n[this.x][this.y - 1] = EMPTY;
				return n;
			} else {
				return null;
			}
		case UP:
			if (this.x - 1 >= 0) {
				int[][] n = new int[SIZE][SIZE];
				for (int i = 0; i < SIZE; i++) {
					for (int j = 0; j < SIZE; j++) {
						n[i][j] = field[i][j];
					}
				}
				n[this.x][this.y] = field[this.x - 1][this.y];
				n[this.x - 1][this.y] = EMPTY;
				return n;
			} else {
				return null;
			}
		case DOWN:
			if (this.x + 1 < SIZE) {
				int[][] n = new int[SIZE][SIZE];
				for (int i = 0; i < SIZE; i++) {
					for (int j = 0; j < SIZE; j++) {
						n[i][j] = field[i][j];
					}
				}
				n[this.x][this.y] = field[this.x + 1][this.y];
				n[this.x + 1][this.y] = EMPTY;
				return n;
			} else {
				return null;
			}
		default:
			return null;
		}

	}

	/**
	 * Generates a 3x3 integer array with randomly filled numbers 1,..., 8
	 * 
	 * @return 2d-array
	 */
	private int[][] generateField() {
		int[] tmp = new int[SIZE * SIZE];
		// create an empty field
		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = EMPTY;
		}
		Random rand = new Random();
		// fill it randomly with 8 numbers
		for (int i = 0; i < tmp.length - 1; i++) {
			int num = rand.nextInt(tmp.length);
			if (tmp[num] == EMPTY) {
				tmp[num] = i + 1;
			} else {
				do {
					num = rand.nextInt(tmp.length);
				} while (tmp[num] != EMPTY);
				tmp[num] = i + 1;
			}
		}
		// copy the input in a 2d-array
		int[][] field = new int[SIZE][SIZE];
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				field[i][j] = tmp[j + i * 3];
			}
		}

		return field;
	}

	/**
	 * Checks if this field is the result field:
	 *   ------- 
	 *  | 1 2 3 | 
	 *  | 4 5 6 | 
	 *  | 7 8   | 
	 *   -------
	 * 
	 * @return true, if this field has the result configuration of numbers, else
	 *         false
	 */
	public boolean isEndfield() {
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if (i == (SIZE - 1) && j == (SIZE - 1)) {
					return true;
				} else {
					if (field[i][j] != (j + 1 + i * 3)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * searches and returns the position of the emtpy field
	 * @return x,y- pos
	 */
	private void setEmptyFieldPos() {
		int i, j = 0;
		
		// look for the position of the empty field
		for (i = 0; i < SIZE; i++) {
			for (j = 0; j < SIZE; j++) {
				if (field[i][j] == EMPTY) {
					this.x = i;
					this.y = j;
					return;
				}
			}
		}
	}

	@Override
	public String toString() {
		String tmp = " -------\n";
		for (int i = 0; i < SIZE; i++) {
			tmp += "| ";
			for (int j = 0; j < SIZE; j++) {
				if (field[i][j] == -1) {
					tmp += ("  ");
				} else {
					tmp += (field[i][j] + " ");
				}
			}
			tmp += ("|\n");
		}
		tmp += " -------\n";
		return tmp;
	}

	
	/**
	 * for the stack.contains(field)-method
	 */
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof Field) {
			if (obj == this) {
				return true;
			}
			for (int i = 0; i < SIZE; i++) {
				for (int j = 0; j < SIZE; j++) {
					if (((Field) obj).field[i][j] != this.field[i][j]) {
						return false;
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}
}
