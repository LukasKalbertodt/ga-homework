package blatt4;

import java.util.Stack;

/**
 * class generates a random 3x3 field, which is filled with numbers 1,..., 8.
 * Tries to find a solution for the generated problem.
 * 
 * @author Elena Resch
 * @author Lukas Kalbertodt
 * @author Mirko Wagner
 * 
 */
public class main {

	/**
	 * just for printing out the way to the solution
	 */
	static Stack<Field> stack;

	public static void main(String[] args) {
		
		Field field = new Field();

		System.out.println("start:\n" + field);
		System.out
				.println(".......................................................");

		boolean sol = beschraenkteTiefensuche(field, 40);
		System.out.println("solution: " + sol);
//		if (!stack.isEmpty()) {
//			System.out.print(stack);
//		}
	}

	/**
	 * makes a depth first search with the given start field, with specified
	 * maximum tree depth. If no solutions are found within the depth, method
	 * return false, as no solutions to be found
	 * 
	 * @param startfield
	 *            Field with randomly generated number configuration
	 * @param searchDepth
	 *            maximum search depth in the tree
	 * @return true if a solution is found, false if no solution is found within
	 *         the given depth
	 */
	private static boolean beschraenkteTiefensuche(Field startfield,
			int searchDepth) {
		if (startfield.isEndfield()) { // startfield == endfield
			return true;
		}
		stack = new Stack<Field>();
		stack.push(startfield);

		do {
			Field actual, next;
			// get the top element without removing it
			actual = stack.peek();
			// get the list of neighbors of actual
			next = actual.nextNeighbor();
			if (next == null) {
				// System.out.println("pop:\n" + stack.pop());
				stack.pop();
			} else {
				if (next.isEndfield()) {
					// System.out.println("push:\n" + next);
					stack.push(next);
					return true;
				}
				if (!stack.contains(next) && stack.size() < searchDepth) {
					// System.out.println("push:\n" + next);
					stack.push(next);
				}
			}
		} while (!stack.empty());
		// nothing found
		return false;
	}

}
