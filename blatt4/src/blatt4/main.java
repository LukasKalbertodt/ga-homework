package blatt4;

import java.util.Random;

public class main {

	public static int RIGHT = 1;
	public static int LEFT = -1;
	public static int UP = 3;
	public static int DOWN = -3;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[][] field = generateField();

		printField(field);

	}

	private static void beschraenkteTiefensuche(int[][] startfield) {

	
	}

	private static int[][] generateField() {
		int[] tmp = new int[9];
		// create an empty field
		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = -1;
		}
		Random rand = new Random();
		// fill it randomly with 8 numbers
		for (int i = 0; i < tmp.length - 1; i++) {
			int num = rand.nextInt(tmp.length);
			if (tmp[num] == -1) {
				tmp[num] = i + 1;
			} else {
				do {
					num = rand.nextInt(tmp.length);
				} while (tmp[num] != -1);
				tmp[num] = i + 1;
			}
		}
		// copy the input in a 2d-array
		int[][] field = new int[3][3];
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				field[i][j] = tmp[j + i * 3];
			}
		}

		return field;
	}

	private static void printField(int[][] field) {
		System.out.print(" -------\n");
		for (int i = 0; i < field.length; i++) {
			System.out.print("| ");
			for (int j = 0; j < field[i].length; j++) {
				if (field[i][j] == -1) {
					System.out.print("  ");
				} else {
					System.out.print(field[i][j] + " ");
				}
			}
			System.out.print("|\n");
		}
		System.out.println(" -------");
	}

}
