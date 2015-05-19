package blatt5;

import renderGraph.RenderGraph;
import renderGraph.RenderableGraph;
import P1.Graph;
import P1.GraphImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * 
 * @author Elena Resch
 * @author Lukas Kalbertodt
 * @author Mirko Wagner
 * 
 */
public class Prim {

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
		directed = false;
		weighted = true;

		// check if filename gives information whether graph is directed or
		// undirected
		if (filename.contains("undir")) {
			directed = false;
		} else if (filename.contains("dir")) {
			directed = true;
		} else { // if no information is given, than graph is assumed to be not
					// directed
			directed = false;
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
		// grafile.replace(".gra", ".png");
		StringBuffer name = new StringBuffer();
		name.append(filename);
		return name.toString();
	}

	/**
	 * Method returns a minimum spanning tree of an undirected, weighted graph
	 * and uses a heap
	 * 
	 * @param g
	 *            instance of Graph and RenderableGraph
	 * @return minimum spanning tree of the given graph, null if given graph is
	 *         null or is not an instance of Graph or if it is directed or not
	 *         weighted
	 */
	public static RenderableGraph minimumSpanningTree(RenderableGraph g) {
		if (g == null)
			return null;
		if (!(g instanceof Graph))
			return null;
		if (g.getNodeCount() == 0)
			return new GraphImpl(false, true); // return an empty graph
		if (g.isDirected() || !g.isWeighted())
			return null; // MST defined only for weighted undirected graphs

		int vertexcount = g.getNodeCount();

		// prims algorithm
		double cost = 0.0; // current cost
		List<Edge> edges = new ArrayList<Edge>(); // list of edges
		Set<Integer> s = new HashSet<Integer>(); // Set of vertices
		double[] d = new double[vertexcount]; // distances
		int[] pred = new int[vertexcount]; // predecessors

		// heap with specified comparator for weights
		PriorityQueue<Edge> heap = new PriorityQueue<>(vertexcount,
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

		// all distances are positive infinity
		for (int i = 0; i < vertexcount; i++) {
			d[i] = Double.MAX_VALUE;
		}
		s.add(0);

		// add all neighbors of 0 into the heap
		List<Integer> neighbors = ((Graph) g).getNeighbors(0);
		for (int i = 0; i < neighbors.size(); i++) {
			int j = neighbors.get(i);
			pred[j] = 0;
			d[j] = g.getWeight(0, j);
			heap.add(new Edge(0, j, d[j]));
		}

		// while-loop
		while (edges.size() != vertexcount - 1) {
			// get the minimum
			Edge minimum = heap.poll();
			if (minimum == null) {
				System.out.println("graph not connected.");
				return null;
			}
			int i = minimum.node;
			s.add(i);
			cost += minimum.weight;
			edges.add(new Edge(pred[i], i, g.getWeight(pred[i], i)));

			// check the neighbors of i
			List<Integer> n = ((Graph) g).getNeighbors(i);
			for (int k = 0; k < n.size(); k++) {
				int j = n.get(k);
				if (!s.contains(j)) {
					if (g.getWeight(i, j) < d[j]) {
						d[j] = g.getWeight(i, j);
						pred[j] = i;
						Edge edge = new Edge(i, j, d[j]);

						// both methods for updateHeap
						heap.remove(edge);
						heap.add(edge);
					}
				}
			}
		}

		GraphImpl mst = new GraphImpl(false, true);
		for (int i = 0; i < vertexcount; i++) {
			mst.addVertex();
		}
		for (int i = 0; i < edges.size(); i++) {
			Edge tmp = edges.get(i);
			mst.addEdge(tmp.pred, tmp.node, tmp.weight);
		}
		System.out.println("edges:" + edges);
		System.out.println("cost: " + cost);
		return mst;
	}

	public static RenderableGraph minimumSpanningTree2(RenderableGraph g) {
		if (g == null)
			return null;
		if (!(g instanceof Graph))
			return null;
		if (g.getNodeCount() == 0)
			return new GraphImpl(false, true); // return an empty graph
		if (g.isDirected() || !g.isWeighted())
			return null; // MST defined only for weighted undirected graphs

		int vertexcount = g.getNodeCount();
		Comparator<Edge> comp = new Comparator<Edge>() { // comparator for the
			// sorting-operation
			@Override
			public int compare(Edge e1, Edge e2) {
				if (e1.weight < e2.weight)
					return -1;
				if (e1.weight > e2.weight)
					return 1;
				return 0;
			}
		};

		// prims algorithm without a heap
		double cost = 0.0; // current cost
		List<Edge> edges = new ArrayList<Edge>(); // list of edges
		List<Integer> s = new ArrayList<Integer>(); // Set of vertices
		List<Integer> s_across = new ArrayList<Integer>();
		List<Edge> sortededges = new ArrayList<Edge>();

		// add vertices minus 0 to s_across
		for (int i = 1; i < vertexcount; i++) {
			s_across.add(i);
		}
		s.add(0);

		// while-loop
		while (s.size() != vertexcount) {
			// choose the cheapest edge {i,j} with i in S and j in S'
			Edge min = getCheapestEdge(s, s_across, (Graph) g, comp);
			if (min != null) {
				edges.add(min);
				s.add(min.node);
				s_across.remove(new Integer(min.node));
				cost += min.weight;
			} else {
				System.out.println("graph not connected");
				return null;
			}
		}

		// create the mst-graph
		GraphImpl mst = new GraphImpl(false, true);
		for (int i = 0; i < vertexcount; i++) {
			mst.addVertex();
		}
		for (int i = 0; i < edges.size(); i++) {
			Edge tmp = edges.get(i);
			mst.addEdge(tmp.pred, tmp.node, tmp.weight);
		}
		System.out.println("edges:" + edges);
		System.out.println("cost: " + cost);
		return mst;
	}

	private static Edge getCheapestEdge(List<Integer> s,
			List<Integer> s_across, Graph g, Comparator<Edge> comp) {
		List<Edge> edges = new ArrayList<Edge>();
		for (int i = 0; i < s.size(); i++) {
			for (int j = 0; j < s_across.size(); j++) {
				int pred = s.get(i);
				int node = s_across.get(j);
				if (g.hasEdge(pred, node)) {
					edges.add(new Edge(pred, node, g
							.getWeightOfEdge(pred, node)));
				}
			}
		}
		if (edges.isEmpty()) {
			return null;
		}
		 Collections.sort(edges, comp);
		 return edges.get(0);
	}

	/**
	 * main-function neads a *.gra-filename as a parameter
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("java -jar Prim.jar <filename>");
			return;
		}
		long end, start;
		GraphImpl graph = readGraFile(args[0]);
		if (graph != null) {
			String filename = graToPngString(args[0]);
			try {
				RenderGraph.renderGraph(graph, filename);
				System.out.println(filename + " created.");
				System.out.println("--------------------------------------");
				System.out.println("mst with a heap: ");
				start = System.currentTimeMillis();
				GraphImpl mst = (GraphImpl) minimumSpanningTree(graph);
				end = System.currentTimeMillis();
				System.out.println("time: " + (end - start) / 1000.0 + " sec");
				if (mst != null) {
					RenderGraph.renderGraph(mst, "mst_" + filename);
					System.out.println("mst_" + filename + " created.");
				}
				System.out.println("--------------------------------------");
				System.out.println("mst with a list: ");
				start = System.currentTimeMillis();
				GraphImpl mst2 = (GraphImpl) minimumSpanningTree2(graph);
				end = System.currentTimeMillis();
				System.out.println("time: " + (end - start) / 1000.0 + " sec");
				if (mst2 != null) {
					RenderGraph.renderGraph(mst2, "mst2_" + filename);
					System.out.println("mst2_" + filename + " created.");
				}
			} catch (IOException e) {
				System.out.println(filename + " could not be created.");
			}

		} else {
			System.out.println("error in creating a graph");
		}
	}
}
