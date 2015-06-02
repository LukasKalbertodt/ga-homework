package blatt7;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import renderGraph.RenderGraph;
import P1.GraphImpl;

public class BellmannFord_Floyd {
	/**
	 * internal representation of an edge, most important is the overwritten
	 * equals-method
	 */
	private static class Edge {
		int pred, node; // node numbers
		double weight; // weight of this edge

		/**
		 * C-tor
		 * 
		 * @param pred
		 *            predecessor of node
		 * @param node
		 *            vertexnumber
		 * @param weight
		 *            double value >= 0.0, no checking of value
		 */
		public Edge(int pred, int node, double weight) {
			this.pred = pred;
			this.node = node;
			this.weight = weight;
		}

		/**
		 * for the heap-remove and -add-method as updateHeap
		 */
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Edge)) {
				return false;
			}
			Edge tmp = (Edge) obj;
			if (this.node == tmp.node) {
				return true;
			}
			return false;
		}

		public String toString() {
			String tmp = "({" + pred + ", " + node + "}, " + weight + ")";
			return tmp;
		}
	}

	static double[][] dist_bellmann;
	static int[][] pred_bellmann;

	static double[][] dist_floyd;
	static int[][] pred_floyd;

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
		directed = true;
		weighted = true;

		// check if filename gives information whether graph is directed or
		// undirected
		if (filename.contains("undir")) {
			directed = false;
		} else if (filename.contains("dir")) {
			directed = true;
		} else { // if no information is given, than graph is assumed to be not
					// directed
			directed = true;
		}

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

	/**
	 * prints both paths from the start node to every other possible node
	 * 
	 * @param start
	 *            start node
	 */
	private static void printPath(int start) {
		int pred_b[] = pred_bellmann[start];
		int pred_f[] = pred_floyd[start];
		double d_b[] = dist_bellmann[start];
		double d_f[] = dist_floyd[start];

		for (int i = 0; i < pred_b.length; i++) {
			String path_b = "";
			String path_f = "";
			double costs_b = 0;
			double costs_f = 0;

			if (i != start) {
				int j_b = i;
				int j_f = i;
				costs_b = d_b[j_b];
				costs_f = d_f[j_f];

				path_b = " -> " + j_b;
				while (pred_b[j_b] != start) {
					path_b = " -> " + pred_b[j_b] + path_b;
					j_b = pred_b[j_b];
				}

				path_f = " -> " + j_f;
				while (pred_f[j_f] != start) {
					path_f = " -> " + pred_f[j_f] + path_f;
					j_f = pred_f[j_f];
				}
			}
			path_b = start + path_b;
			path_f = start + path_f;
			println(String.format("Bellmann: c = %5.1f: %s", costs_b, path_b));
			println(String.format("Floyd   : c = %5.1f: %s", costs_f, path_f));
		}
	}

	/**
	 * prints the matrix either of distances or the predecessors
	 * 
	 * @param d
	 *            distances
	 * @param pred
	 *            predecessors
	 * @param size
	 *            size of the matrix
	 * @param matrix
	 *            1 for printing distances, 2 for printing predecessors
	 */
	private static void printMatrix(double[][] d, int[][] pred, int size,
			int matrix) {
		print("   ");
		for (int i = 0; i < size; i++) {
			print(String.format("%4d", i));
		}
		print("\n---");
		for (int i = 0; i < size; i++) {
			print("----");
		}
		println("   ");
		for (int i = 0; i < size; i++) {
			print(String.format("%2d|", i));
			for (int j = 0; j < size; j++) {

				switch (matrix) {
				case 1:
					if (d[i][j] == Double.POSITIVE_INFINITY) {
						print(String.format("%5s", "x"));
					} else {
						print(String.format("%5.0f", d[i][j]));
					}
					break;
				case 2:
					print(String.format("%4d", pred[i][j]));
				}
			}
			println("");
		}
		println("");
	}

	/**
	 * Creates a directed weighted graph where all vertices are connected to the
	 * others with one directed edge
	 * 
	 * @param vnumber
	 *            number of vertices for the graph
	 */
	private static void checkCompleteGraphInstances(int vnumber) {
		print(String.format("%4d     ", vnumber));
		Random rand = new Random();
		// create a bigger graph
		GraphImpl g = new GraphImpl(true, true);
		for (int i = 0; i < vnumber; i++) {
			g.addVertex();
		}
		for (int i = 0; i < vnumber; i++) {
			for (int j = i + 1; j < vnumber; j++) {
				double cost = (rand.nextDouble()) * 10;
				int x, y;
				x = y = 0;
				while (x == y) {
					x = rand.nextInt(vnumber);
					y = rand.nextInt(vnumber);
				}
				g.addEdge(x, y, cost);
			}
		}

		long start, end;
		start = System.currentTimeMillis();
		// Floyd-Algorithm
		Floyd(g);
		end = System.currentTimeMillis();
		print(String.format("%4f sec     ", (end - start) / 1000.0));
		start = System.currentTimeMillis();
		// Bellmann-Ford-Algorithm
		BellmannFord(g);
		end = System.currentTimeMillis();
		println(String.format("%4f sec", (end - start) / 1000.0));

	}

	/**
	 * Reads the gra-file, creates a graph. If the graph is created correctly,
	 * both algorithms are started, their performance measured and printed on
	 * console. Predecessor matrices from the algorithms can vary if the path is
	 * not definitive because of the sum of distances.
	 * 
	 * @param grafile
	 *            name of the .gra-file
	 */
	private static void startAlgorithms(String grafile) {
		println("reading " + grafile + ".");

		// read the file
		GraphImpl graph = readGraFile(grafile);
		if (graph == null) {
			println("error in creating the graph.");
			return;
		}

		long start, end;

		// start the algorithms
		println("start of Floyd-algorithm");
		start = System.currentTimeMillis();
		Floyd(graph);
		end = System.currentTimeMillis();
		println(String.format("Time: %f sec", (end - start) / 1000.0));
		printMatrix(dist_floyd, pred_floyd, graph.getNodeCount(), 2);

		println("start of Bellmann-Ford-algorithm");
		start = System.currentTimeMillis();
		BellmannFord(graph);
		end = System.currentTimeMillis();
		println(String.format("Time: %f sec", (end - start) / 1000.0));
		printMatrix(dist_bellmann, pred_bellmann, graph.getNodeCount(), 2);

	}

	/**
	 * Implementation of the Floyd-algorithm
	 * 
	 * @param graph
	 *            instance of GraphImpl
	 */
	private static void Floyd(GraphImpl graph) {
		if (graph.getNodeCount() == 0) {
			return;
		}

		int nodecount = graph.getNodeCount();
		double[][] dist = new double[nodecount][nodecount]; // distances
		int[][] pred = new int[nodecount][nodecount]; // predecessors

		// initiate the matrix of distances and predecessors:
		// d^0[i,i]=0; d^0[i,j]=c_ij for all i != j
		for (int i = 0; i < nodecount; i++) {
			for (int j = 0; j < nodecount; j++) {
				if (i == j) {
					dist[i][j] = 0;
				} else {
					dist[i][j] = graph.getWeight(i, j);
				}
				pred[i][j] = i;
			}
		}
		// in every round a path with maximum k intermediate vertices is built
		for (int k = 0; k < nodecount; k++) {
			for (int i = 0; i < nodecount; i++) {
				for (int j = 0; j < nodecount; j++) {
					if (dist[i][k] + dist[k][j] < dist[i][j]) {
						dist[i][j] = dist[i][k] + dist[k][j];
						pred[i][j] = pred[k][j];
					}
				}
			}
		}
		dist_floyd = dist;
		pred_floyd = pred;
	}

	/**
	 * Implementation of the Bellmann-Ford-algorithm
	 * 
	 * @param graph
	 *            instance of GraphImpl
	 */
	private static void BellmannFord(GraphImpl graph) {

		if (graph.getNodeCount() == 0) {
			return;
		}
		int nodecount = graph.getNodeCount();
		double[][] dist = new double[nodecount][nodecount]; // distances
		int[][] pred = new int[nodecount][nodecount]; // predecessors
		PriorityQueue<Edge> edges = new PriorityQueue<Edge>(nodecount,
				new Comparator<Edge>() {
					@Override
					public int compare(Edge e1, Edge e2) {
						if (e1.weight < e2.weight)
							return -1;
						if (e1.weight > e2.weight)
							return 1;
						return 0;
					}
				});
		// k=1, start node is i
		for (int i = 0; i < nodecount; i++) {
			for (int j = 0; j < nodecount; j++) {
				if (i == j) {
					dist[i][j] = 0;
				} else {
					dist[i][j] = graph.getWeight(i, j);
				}
				pred[i][j] = i;
			}
		}
		// in every round maximum k edges can be used
		for (int k = 2; k < nodecount - 1; k++) {
			for (int i = 0; i < nodecount; i++) {
				for (int j = 0; j < nodecount; j++) {
					// get predecessors of j and put them to the heap
					List<Integer> n_i = graph.getPredecessors(j);
					for (int l = 0; l < n_i.size(); l++) {
						int h = n_i.get(l);
						edges.add(new Edge(h, j, dist[i][h]
								+ graph.getWeight(h, j)));
					}
					// get the edge with minimum weight
					Edge min = edges.poll();
					edges.clear();
					if (min != null) {
						if (dist[i][j] >= min.weight) {
							dist[i][j] = min.weight;
							pred[i][j] = pred[min.pred][j];
						}
					}
				}
			}
		}
		dist_bellmann = dist;
		pred_bellmann = pred;

	}

	public static void main(String[] args) {
		if (args.length != 1) {
			println("Usage: java -jar BellmannFord_Floyd.jar <filename>");
			return;
		}
		startAlgorithms(args[0]);
		// println("  v |      Floyd     |  Bellmann-Ford");
		// println("----|----------------|--------------");
		// checkCompleteGraphInstances(10);
		// checkCompleteGraphInstances(50);
		// checkCompleteGraphInstances(100);
		// checkCompleteGraphInstances(150);
		// not recommended:
		// checkCompleteGraphInstances(250);
	}

}
