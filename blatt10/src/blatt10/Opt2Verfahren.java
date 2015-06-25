package blatt10;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import P1.GraphImpl;

public class Opt2Verfahren {
	private static final int MIN = 0;
	private static final int AVG = 1;
	private static final int MAX = 2;

	/**
	 * checks if the given filename is a .gra-file and reads its content while
	 * checking for correct .gra-file format
	 * 
	 * @param filename
	 *            *.gra-file
	 * @return graph if file could be read, null on error
	 */
	private static GraphImpl readGraFile(String filename) {
		// check if filename is a .gra-file
		if (!filename.endsWith(".gra")) {
			System.out.println(filename
					+ " has wrong suffix.  only '*.gra'-files allowed");
			return null;
		}
		boolean directed, weighted;
		String line;
		int vertexNumber;
		double[][] matrix = null;
		GraphImpl graph;
		directed = false;
		weighted = true;

		try (BufferedReader rd = new BufferedReader(new FileReader(new File(
				filename)))) {

			// first line(s) is(are) a comment
			while ((line = rd.readLine()) != null && line.startsWith("#")) {
			}

			// line is null
			if (line == null) { // wrong format, file emtpy
				rd.close();
				System.out.println("file has wrong format");
				return null;
			}

			line.trim();

			// if regex for the count of vertices line n <number> does not
			// matches the standard
			if (!line.matches("n [1-9][0-9]*")) {
				rd.close();
				System.out.println("file " + filename + " has wrong format.");
				return null;
			}

			String[] splitted = line.split(" ");
			// convert the string number to int
			try {
				vertexNumber = Integer.parseInt(splitted[1]);
			} catch (NumberFormatException nfe) { // is not possible to be, as a
													// regex assures that the
													// right format is used
				rd.close();
				System.out.println("file " + filename + " has wrong format.");
				return null;
			}
			// ignore the comment lines
			while ((line = rd.readLine()) != null && line.startsWith("#")) {
			}

			// now must come the adjacency matrix
			// regex: "\\d+|\\s+[ : ][\\d ]+"
			matrix = new double[vertexNumber][];
			for (int i = 0; i < vertexNumber; i++) {
				line.trim();
				if ((!line.matches(".+ : [[[0-9]+ ]+|[[0-9]+\\.[0-9]+ ]+]+"))) {

					System.out.println("file " + filename
							+ " has wrong format.");
					rd.close();
					return null;
				}
				// if everything ok, split the line on ":"
				splitted = line.split(" : ", 2);

				// get the double[] of weights.
				matrix[i] = convertLine(splitted[1], vertexNumber);
				if (matrix[i] == null) {
					rd.close();
					return null;
				}
				line = rd.readLine();
			}

		} catch (FileNotFoundException e) {
			System.out.println(filename + " not found.");
			return null;
		} catch (IOException e) {
			System.out.println("error while opening/reading" + filename + ".");
		}

		if (matrix != null) {
			// printMatrix(matrix);
			// matrix read, instantiate the graph
			try {
				graph = new GraphImpl(directed, weighted, matrix);

			} catch (IllegalArgumentException iae) {
				System.out.println("wrong format in file " + filename);
				System.out.println(iae.getMessage());
				return null;
			} catch (RuntimeException re) {
				System.out.println("wrong data in file " + filename);
				return null;
			}
			return graph;

		}
		return null;
	}

	/**
	 * helper function to convert the numbers
	 * 
	 * @param line
	 * @param number
	 * @return
	 */
	private static double[] convertLine(String line, int number) {

		double[] values;
		line.trim();
		String[] splitted = line.split(" ");
		if (splitted.length != number) {
			System.out
					.println("number of vertices is not equal to matrix size");
			return null;
		}
		values = new double[number];
		for (int i = 0; i < number; i++) {
			try {
				values[i] = Double.parseDouble(splitted[i]);
				if (values[i] < 0) {
					System.out.println("weight negative");
				}
			} catch (NumberFormatException nfe) {
				System.out.println("file has wrong format");
				return null;
			}
		}
		return values;
	}

	/**
	 * replaces the .gra suffix with .png
	 * 
	 * @param grafile
	 *            string = "*.gra"
	 * @return *.png filename
	 */
	private static String graToPngString(String grafile) {
		char[] filename = grafile.toCharArray();
		int i = 0;
		filename[filename.length - 1 - i++] = 'g';
		filename[filename.length - 1 - i++] = 'n';
		filename[filename.length - 1 - i++] = 'p';
		StringBuffer name = new StringBuffer();
		name.append(filename);
		return name.toString();
	}

	/**
	 * Helper function to not write the long expression(laziness)
	 * 
	 * @param str
	 *            string to be printed without an endline
	 */
	private static void print(String str) {
		System.out.print(str);
	}

	/**
	 * Helper function to not write the long expression(laziness)
	 * 
	 * @param str
	 *            string to be printed with an endline
	 */
	private static void println(String str) {
		System.out.println(str);
	}

	private static void printSol(int[] sol) {
		print("(");
		for (int i = 0; i < sol.length - 1; i++) {
			print(sol[i] + ", ");
		}
		print(sol[sol.length - 1] + ")");
	}

	/**
	 * Creates a random solution for the given graph
	 * 
	 * @param graph
	 * @return int array with a sequence of node numbers
	 */
	private static int[] createRandomSolution(GraphImpl graph) {
		int vertexnumber = graph.getNodeCount();
		Random rand = new Random();
		int[] sol = new int[vertexnumber];

		for (int i = 0; i < vertexnumber; i++) {
			sol[i] = -1;
		}
		for (int i = 0; i < vertexnumber; i++) {
			int num = rand.nextInt(vertexnumber);
			while (sol[num] != -1) {
				num = rand.nextInt(vertexnumber);
			}
			sol[num] = i;
		}
		return sol;
	}

	/**
	 * Computes the costs of the given cycle
	 * 
	 * @param graph
	 * @param sol
	 *            int array with the sequence of node numbers
	 * @return cost of the cycle
	 */
	private static double getSolutionCost(GraphImpl graph, int[] sol) {
		double cost = 0;
		for (int i = 0; i < sol.length - 1; i++) {
			cost += graph.getWeight(sol[i], sol[i + 1]);
		}
		cost += graph.getWeight(sol[sol.length - 1], sol[0]);
		return cost;
	}

	/**
	 * Return a neighbor in the 2-opt-neighborhood with smaller tour cost, or null
	 * @param graph GraphImpl to get the edge weights
	 * @param cost int array for min-max-avg costs
	 * @param sol int array with a sequence of node numbers to find the neighbor
	 * @return neighbor with smaller tour cost than the given tour, else null
	 */
	private static int[] getNeighbor(GraphImpl graph, double[] cost, int[] sol) {
		int nodecount = graph.getNodeCount();
		int[] result = null;
		double avgcost = 0;
		int iter = 0;
		for (int v = 0; v < nodecount; v++) {
			for (int u = v + 2; u < nodecount; u++) {
				int i = v;
				int k = u;
				int l = (u + 1) % nodecount;
				if (l != i) {
					int[] tmpsol = new int[sol.length];
					// copy the new vertex order
					int tmp = 0;
					while (tmp <= i) { // 0 to i
						tmpsol[tmp] = sol[tmp];
						tmp++;
					}
					while (k >= i + 1) {
						tmpsol[tmp++] = sol[k--];
					}
					while (tmp < nodecount) {
						tmpsol[tmp++] = sol[l++];
					}
					// get the cost of this cycle
					double newcost = getSolutionCost(graph, tmpsol);
					
					//set the values for min-,max-,avg-costs
					if (cost[MIN] > newcost) {
						cost[MIN] = newcost;
						result = tmpsol;
					} else if (newcost > cost[MAX]) {
						cost[MAX] = newcost;
					}
					avgcost += newcost;
					iter++;
				}
			}
		}
		cost[AVG] += (avgcost / (double) iter);
		return result;
	}

	/**
	 * 
	 * @param graph
	 * @param startsol
	 * @param cost
	 * @return
	 */
	private static int[] twoOPTAlgorithm(GraphImpl graph, int[] startsol,
			double[] cost) {
		double startcost = getSolutionCost(graph, startsol);
		cost[MIN] = cost[MAX] = startcost;
		cost[AVG] = 0.0;
		int[] sol = startsol;
		int iter = 1;
		while (true) {
			int[] neighbor = getNeighbor(graph, cost, sol);
			if (neighbor == null) {
				break;
			}
			sol = neighbor;
			iter++;
		}
		cost[AVG] /= (double) iter; //computes the average tour cost
		return sol;
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			println("Usage: java -jar 2OptAlg.jar <filename>");
			return;
		}
	
		GraphImpl graph = readGraFile(args[0]);
		if (graph == null) {
			println("graph could not be created. Program exit.");
			return;
		}
		for (int i = 0; i < 100; i++) {
			double[] cost = new double[3];
			int[] startsol = createRandomSolution(graph);
			int[] sol = twoOPTAlgorithm(graph, startsol, cost);
			print((i+1) + ": start: ");
			printSol(startsol);
			print(" --> result: ");
			printSol(sol);
			println(String.format("\nmin_tour = %.0f, max_tour = %.0f, avg_tour = %f\n", cost[MIN], cost[MAX], cost[AVG]));
		}
	}

}
