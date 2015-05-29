
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import renderGraph.RenderGraph;
import P1.Graph;
import P1.GraphImpl;

/**
 * 
 * @author Elena Resch
 * @author Lukas Kalbertodt
 * @author Mirko Wagner
 * 
 */

public class Dijkstra {

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
				System.out.println("1. wrong data in file " + filename);
				return null;
			} catch (RuntimeException re) {
				System.out.println("2. wrong data in file " + filename);
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
	 *            line to be printed
	 */
	private static void print(String str) {
		System.out.println(str);
	}

	
	/**
	 * helper function to print all possible path
	 * 
	 * @param pred
	 *            integer array of predecessors
	 * @param d
	 *            double array with the sum of distances for each vertex from
	 *            the start
	 * @param start
	 *            vertex where dijkstra algorithm started
	 */
	private static void printPaths(int[] pred, double[] d, int start) {

		for (int i = 0; i < pred.length; i++) {
			String path = "";
			double costs = 0;
			if (i != start) {
				int j = i;
				costs = d[j];
				if (costs == Double.MAX_VALUE) {
					print("c = infty: no path from " + start + " to " + i);
				} else {
					path = " -> " + j;
					while (pred[j] != start) {
						path = " -> " + pred[j] + path;
						j = pred[j];
					}
				}
			}
			path = start + path;
			print(String.format("c = %5.1f: %s", costs, path));
		}
	}

	/**
	 * Function reads the file, creates a graph, and writes the .png-files
	 * 
	 * @param args
	 *            filename of the .gra-file to be read
	 */
	private static void createDijkstraFromFile(String args) {
		print("reading " + args + ".");
		GraphImpl graph = readGraFile(args);
		if (graph != null) {
			String filename = graToPngString(args);
			try {
				RenderGraph.renderGraph(graph, filename);
				print(filename + " created.");
				print("----------------------------------");
				print("dijktra: ");
				GraphImpl dijkstra = dijkstra(graph, 0);
				if (dijkstra != null) {
					RenderGraph.renderGraph(dijkstra, "shortest_path_"
							+ filename);
					print("shortest_path_" + filename + " created.");
				}
			} catch (IOException e) {
				print("file could not be created");
			}
		} else {
			print("error in creating the graph.");
		}
		print("Program exit.....");
	}

	/**
	 * implementation of dijkstra algorithm for the single-source shortest path
	 * problem
	 * 
	 * @param g
	 *            directed and weighted graph
	 * @param start
	 *            vertex to be started from
	 * @return "reduced" graph to be rendered
	 */
	private static GraphImpl dijkstra(GraphImpl g, int start) {
		if (g == null) {
			print("g is null");
			return null;
		}
		if (!(g instanceof Graph)) {
			print("g is not instance of Graph");
			return null;
		}
		if (g.getNodeCount() == 0) {
			print("g has no nodes");
			return new GraphImpl(true, true);
		}
		if (start >= g.getNodeCount()) {
			print("start node not in g");
			return null;
		}
	
		int vertexcnt = g.getNodeCount();
	
		Set<Integer> s = new HashSet<Integer>(); // set of vertices
		double[] d = new double[vertexcnt]; // distances
		int[] pred = new int[vertexcnt]; // predecessors
		List<Edge> edges = new ArrayList<Edge>(); // list of edges to create
													// graph
		// heap with specified comparator for weights
		PriorityQueue<Edge> heap = new PriorityQueue<>(vertexcnt,
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
	
		// init distances to infinity
		for (int i = 0; i < vertexcnt; i++) {
			d[i] = Double.MAX_VALUE;
		}
	
		// start of dijkstra
		s.add(start);
		d[start] = 0;
		List<Integer> neighbors = g.getSuccessors(start);
		// add all neighbors to the heap
		for (int i = 0; i < neighbors.size(); i++) {
			int n = neighbors.get(i);
			d[n] = g.getWeight(start, n);
			pred[n] = start;
			heap.add(new Edge(0, n, d[n]));
		}
	
		// while-loop
		while (s.size() != vertexcnt) {
			// get a node that has the minimal distance to the currently built
			// tree
			Edge minimum = heap.poll();
			if (minimum == null) {
				print("graph not connected.");
				return null;
			}
			int i = minimum.node;
			s.add(i);
			d[i] = minimum.weight;
			edges.add(new Edge(pred[i], i, g.getWeight(pred[i], i)));
	
			// update the successors from i
			List<Integer> n = g.getSuccessors(i);
			for (int k = 0; k < n.size(); k++) {
				int j = n.get(k);
				if (!s.contains(j)) {
					double c_ij = g.getWeight(i, j);
					if (d[j] > (d[i] + c_ij)) {
						d[j] = d[i] + c_ij;
						pred[j] = i;
						Edge edge = new Edge(i, j, d[j]);
						// both methods for updateHeap
						heap.remove(edge);
						heap.add(edge);
					}
				}
			}
		}
	
		// create the graph
		GraphImpl dijkstra = new GraphImpl(true, true);
		for (int i = 0; i < vertexcnt; i++) {
			dijkstra.addVertex();
		}
		for (int i = 0; i < edges.size(); i++) {
			Edge tmp = edges.get(i);
			dijkstra.addEdge(tmp.pred, tmp.node,
					g.getWeight(tmp.pred, tmp.node));
		}
	
		// print all paths
		printPaths(pred, d, start);
		return dijkstra;
	}

	/**
	 * main-function to start the whole process of dijkstra
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			print("java -jar Dijkstra.jar <filename>");
			return;
		}
		createDijkstraFromFile(args[0]);
	}

}
