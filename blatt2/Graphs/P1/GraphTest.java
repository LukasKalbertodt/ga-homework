package P1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import renderGraph.RenderGraph;

public class GraphTest {

	public static void main(String[] args) {

		// testGraphs();
		if (args.length == 0) {
			System.out.println("java GraphTest <filename> [<filename>]...");
			return;
		}
		for (int i = 0; i < args.length; i++) {
			GraphImpl graph = readGraFile(args[i]);
			if (graph != null) {
				String filename = graToPngString(args[i]);
				try {
					RenderGraph.renderGraph(graph, filename);
				} catch (IOException e) {
					System.out.println(filename + " could not be created.");
				}
				System.out.println(filename + " created.");
			} else {
				System.out.println("error in creating a graph");
			}
		}
		System.out.println("Program end.");
	}

	/**
	 * 
	 * @param filename
	 * @return
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
			//printMatrix(matrix);
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

	private static String graToPngString(String grafile) {
		char[] filename = grafile.toCharArray();
		int i = 0;
		filename[filename.length -1 - i++] = 'g';
		filename[filename.length -1 - i++] = 'n';
		filename[filename.length -1 - i++] = 'p';
		//grafile.replace(".gra", ".png");
		StringBuffer name = new StringBuffer();
		name.append(filename);
		return name.toString();
	}

	private static void printMatrix(double[][] matrix) {
		if (matrix != null) {
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[i].length; j++) {
					System.out.print(matrix[i][j] + " ");
				}
				System.out.println();
			}
		} else {
			System.out.println("matrix == null");
		}
	}

	/**
	 * helper function to test the class GraphImpl
	 */
	private static void testGraphs() {
		GraphImpl graph1 = new GraphImpl(true, false);
		GraphImpl graph2 = new GraphImpl(false, true);
		for (int i = 0; i < 4; i++) {
			graph1.addVertex();
			graph2.addVertex();
		}
		try {
			graph1.addEdge(0, 2);
			graph1.addEdge(0, 1);
			graph1.addEdge(2, 1);
			graph1.addEdge(3, 1);

			System.out.println("nodes: " + graph1.getNodeCount());
			graph1.printMatrix();
			System.out.println();

			graph2.addEdge(0, 2, 0.7);
			graph2.addEdge(0, 1, 4.2);
			graph2.addEdge(2, 1, 3.2);
			graph2.addEdge(3, 1, 0.8);
			System.out.println("nodes: " + graph2.getNodeCount());
			graph2.printMatrix();

			try {
				RenderGraph.renderGraph(graph1, "graph1a.png");
				RenderGraph.renderGraph(graph2, "graph2a.png");
			} catch (IOException io) {
				System.out.println("render graph error");
			}
			graph1.removeEdge(3, 1);
			graph1.addVertex();
			graph2.removeVertex();

			System.out.println("nodes: " + graph1.getNodeCount());
			graph1.printMatrix();
			System.out.println();

			try {
				RenderGraph.renderGraph(graph1, "graph1b.png");

				RenderGraph.renderGraph(graph2, "graph2b.png");
			} catch (IOException io) {
				System.out.println("render graph error");
			}

			graph2.addEdge(0, 2);
			graph1.addEdge(1, 3);
			graph1.addEdge(3, 1);
			try {
				RenderGraph.renderGraph(graph1, "graph1c.png");
				RenderGraph.renderGraph(graph2, "graph2c.png");
			} catch (IOException io) {
				System.out.println("render graph error");
			}
		} catch (IllegalArgumentException e) {
			System.out.println("illegal argument error");
			e.printStackTrace();
		}

	}
}
