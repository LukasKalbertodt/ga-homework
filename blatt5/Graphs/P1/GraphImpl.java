package P1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import renderGraph.RenderableGraph;

/**
 * Representation of a directed and undirected graph. If graph is not weighted,
 * all weights are set to 0.0. Vertex names begin from 0. Internally the graph
 * is structured as a directed weighted graph through an adjacency list which
 * gives the opportunity to the graph to grow dynamically.
 * 
 * @author Elena Resch
 * @author Lukas Kalbertodt
 * @author Mirco Wagner
 * 
 */
public class GraphImpl implements Graph, RenderableGraph {

	private boolean m_weighted;
	private boolean m_directed;
	private int m_vertexCount;
	private List<Map<Integer, Double>> m_adjacencylist; // internal
														// representation of the
														// graph

	/**
	 * C-tor creates an empty graph, needs informations if the graph should be
	 * directed and if it is weighted
	 * 
	 * @param weighted
	 *            true if graph should be weighted
	 * @param directed
	 *            true if graph should be directed
	 */
	public GraphImpl(boolean directed, boolean weighted) {
		this.m_weighted = weighted;
		this.m_directed = directed;
		this.m_vertexCount = 0;
		this.m_adjacencylist = new ArrayList<Map<Integer, Double>>();
	}

	/**
	 * C-tor makes a graph from given values
	 * 
	 * @param matrix
	 *            quadratic double array
	 * @param directed
	 *            boolean value if the graph should be directed
	 * @param weighted
	 *            boolean value if the graph should be weighted
	 * @throws RuntimeException
	 *             if matrix is not quadratic
	 * @throws IllegalArgumentException
	 *             if there is a negative value in the matrix, if there is a
	 *             self loop in the matrix, if undirected graph wished but
	 *             matrix for a directed graph is given, if matrix is null
	 */
	public GraphImpl(boolean directed, boolean weighted, double[][] matrix) {
		this(directed, weighted);
		if (matrix == null) {
			throw new IllegalArgumentException("matrix is null");
		}
		this.graphFromMatrix(matrix);
	}

	/**
	 * @return true if graph is weighted, else false
	 */
	@Override
	public boolean isWeighted() {
		return this.m_weighted;
	}

	/**
	 * @return true if this graph is directed, else false
	 */
	@Override
	public boolean isDirected() {
		return this.m_directed;
	}

	/**
	 * returns the number of vertices in this graph
	 * 
	 * @return number of vertices
	 */
	@Override
	public int getNodeCount() {
		return this.m_vertexCount;
	}

	/**
	 * is forwarded to getWeightOfEdge(start, end)
	 * 
	 * @param x
	 *            start vertex of the edge
	 * @param y
	 *            end vertex of the edge
	 * @return c(start, end) in a weighted graph, 0.0 in a not weighted graph
	 *         Double.POSITIVE_INFINITY if the edge is not graph
	 * @throws IllegalArgumentException
	 *             if wrong node number(s) is(are) given
	 */
	@Override
	public double getWeight(int x, int y) throws IllegalArgumentException {
		return this.getWeightOfEdge(x, y);
	}

	/**
	 * is forwarded to addEdge(start, end, weight) with weight = 0.0
	 * 
	 * @param start
	 *            start vertex in the graph
	 * @param end
	 *            end vertex in the graph
	 * @throws IllegalArgumentException
	 *             if node(s) is(are) not in this graph, if start equal end
	 *             (selfloop),if weight is negative.
	 */
	@Override
	public void addEdge(int start, int end) {
		this.addEdge(start, end, 1.0);

	}

	/**
	 * Adds an edge to the graph.
	 * 
	 * @param start
	 *            start vertex in the graph
	 * @param end
	 *            end vertex in the graph
	 * @param weight
	 *            of the edge, must be positive or 0.0
	 * @throws IllegalArgumentException
	 *             if node(s) is(are) not in this graph, if start equal end
	 *             (selfloop),if weight is negative.
	 */
	@Override
	public void addEdge(int start, int end, double weight) {
		if (start < 0 || start >= this.m_vertexCount || end < 0
				|| end >= this.m_vertexCount) {
			throw new IllegalArgumentException("node(s) not in graph");
		}
		if (start == end) { // no selfloops in this kind of graph allowed
			throw new IllegalArgumentException("no selfloops allowed");
		}
		if (weight < 0.0) {
			throw new IllegalArgumentException("weight is < 0.0");
		}
		(this.m_adjacencylist.get(start)).put(end, weight);
		if (!this.m_directed) {
			(this.m_adjacencylist.get(end)).put(start, weight);
		}
	}

	/**
	 * Adds a consecutively numbered vertex to the graph with an empty
	 * neighborhood
	 */
	@Override
	public void addVertex() {
		Map<Integer, Double> newVertex = new HashMap<Integer, Double>();
		this.m_adjacencylist.add(newVertex);
		this.m_vertexCount++;
	}

	/**
	 * returns the whole neighborhood of this vertex
	 * 
	 * @param v
	 *            vertex in the graph
	 * @return list of Integer which are neighbors of the given vertex, if v has
	 *         no neighbors an empty list is returned
	 * 
	 * @throws IllegalArgumentException
	 *             if v is not in graph
	 */
	@Override
	public List<Integer> getNeighbors(int v) throws IllegalArgumentException {
		List<Integer> successors = this.getSuccessors(v);
		if (this.m_directed) {
			List<Integer> predecessors = this.getPredecessors(v);
			successors.addAll(predecessors);
			return successors;
		} else {
			return successors;
		}
	}

	/**
	 * if the graph is undirected all predecessors are the whole neighborhood,
	 * else check for all vertices in the adjacency list if there is v their
	 * neighborhood, and add these vertices to the list of predecessors
	 * 
	 * @param v
	 *            vertex in the graph
	 * @return list of Integers which are predecessors of the given vertex, if v
	 *         has no predecessors an empty list is returned
	 * @throws IllegalArgumentException
	 *             if v is not in the graph
	 */
	@Override
	public List<Integer> getPredecessors(int v) {
		if (v < 0 || v >= this.m_vertexCount) {
			throw new IllegalArgumentException("vertex not in graph");
		}
		if (this.m_directed) { // predecessors != successors
			List<Integer> predecessors = new ArrayList<Integer>();
			// run throw the list and check if the key is in the hashmap, if so
			// add the actual index to the list of predecessors
			for (int i = 0; i < this.m_adjacencylist.size(); i++) {
				List<Integer> successors = this.getSuccessors(i);
				if (successors.contains(new Integer(v))) {
					predecessors.add(i);
				}
			}
			return predecessors;
		} else {
			// predecessors = successors, so just get successors
			return this.getSuccessors(v);
		}
	}

	/**
	 * if this graph is directed then successors are generally not equal to
	 * predecessors, but in an undirected graph successors are equal to
	 * predecessors, so they are all neighbors.
	 * 
	 * @param v
	 *            vertex in the graph
	 * @return list of Integers which are successors of the given vertex, if v
	 *         has no successors an empty list is returned
	 * @throws IllegalArgumentException
	 *             if v not in the graph
	 */
	@Override
	public List<Integer> getSuccessors(int v) {
		if (v < 0 || v >= this.m_vertexCount) {
			throw new IllegalArgumentException("vertex not in graph");
		}

		// get the corresponding set of this vertex
		Map<Integer, Double> successors = this.m_adjacencylist.get(v);
		Set<Integer> set = successors.keySet();

		// return the set converted to a list
		return new ArrayList<Integer>(set);
	}

	/**
	 * returns the number of vertices in this graph
	 */
	@Override
	public int getVertexCount() {
		return this.m_vertexCount;
	}

	/**
	 * returns the weight of the given edge
	 * 
	 * @param start
	 *            start vertex of the edge
	 * @param end
	 *            end vertex of the edge
	 * @return c(start, end) in a weighted graph, 0.0 in a not weighted graph
	 *         Double.POSITIVE_INFINITY if the edge is not graph
	 * @throws IllegalArgumentException
	 *             if wrong node number(s) is(are) given
	 */
	@Override
	public double getWeightOfEdge(int start, int end)
			throws IllegalArgumentException {
		if (!this.hasEdge(start, end)) { // if edge is not in graph
			if (start == end) {
				return 0.0;
			}
			return Double.POSITIVE_INFINITY;
		}
		// edge is in this graph
		Map<Integer, Double> successors = this.m_adjacencylist.get(start);
		// get the corresponding value
		double value = successors.get(end);
		return value;
	}

	/**
	 * checks if the edge is in graph
	 * 
	 * @return true if edge in graph else false
	 * @throws IllegalArgumentException
	 *             if given node(s) is(are) not in graph
	 */
	@Override
	public boolean hasEdge(int start, int end) {
		if (start < 0 || start >= this.m_vertexCount || end < 0
				|| end >= this.m_vertexCount) {
			throw new IllegalArgumentException("node(s) not in graph");
		}
		if (start == end) {
			return false;
		}
		List<Integer> successors = this.getSuccessors(start);
		if (successors.contains(new Integer(end))) {
			return true;
		}
		return false;
	}

	/**
	 * removes the edge from the graph
	 * 
	 * @param start
	 *            number of the start vertex
	 * @param end
	 *            number of the start vertex
	 * @throws IllegalArgumentException
	 *             if node(s) is(are) not in graph
	 */
	@Override
	public void removeEdge(int start, int end) throws IllegalArgumentException {
		if (!this.hasEdge(start, end)) {
			return;
		}
		Map<Integer, Double> successors = this.m_adjacencylist.get(start);
		successors.remove(end);
		this.m_adjacencylist.set(start, successors);
		
		if (!this.m_directed) {
			successors = this.m_adjacencylist.get(end);
			successors.remove(start);
			this.m_adjacencylist.set(end, successors);
		}
	}

	/**
	 * removes the vertex with the last number, and its incident edges. If graph
	 * has no vertices nothing happens
	 */
	@Override
	public void removeVertex() {
		if (!this.m_adjacencylist.isEmpty()) {
			// remove all edges incident to the last vertex
			// remove all predecessors
			List<Integer> pred = this.getPredecessors(this.m_vertexCount);
			for (int i = 0; i < pred.size(); i++) {
				this.removeEdge(pred.get(i), this.m_vertexCount);
			}
			//remove all successors and the vertex
			this.m_adjacencylist.remove(--this.m_vertexCount); // an pos
		}
	}

	/**
	 * returns graph information as a 2D-double-matrix
	 * 
	 * @return 2D-double-matrix with weights in it, if graph is not weighted the
	 *         edges in the graph are represented through 1.0 value
	 */
	public double[][] getMatrix() {
		double matrix[][] = new double[this.m_vertexCount][this.m_vertexCount];
		// fill the matrix with weights
		for (int i = 0; i < this.m_vertexCount; i++) {
			Map<Integer, Double> neighbors = this.m_adjacencylist.get(i);
			for (int j = 0; j < this.m_vertexCount; j++) {
				if (i == j) {
					matrix[i][j] = 0.0;
				} else if (neighbors.containsKey(j)) {
					if (this.m_weighted) {
						matrix[i][j] = neighbors.get(j);
					} else {
						matrix[i][j] = 1.0;
					}
				} else {
					matrix[i][j] = Double.POSITIVE_INFINITY;
				}
			}
		}
		return matrix;
	}

	/**
	 * makes a graph from given values, default values are undirected and
	 * weighted.
	 * 
	 * @param matrix
	 *            quadratic double array
	 * @throws RuntimeException
	 *             if matrix is not quadratic
	 * @throws IllegalArgumentException
	 *             if there is a negative value in the matrix, if there is a
	 *             self loop in the matrix if undirected graph wished but matrix
	 *             for a directed graph is given
	 */
	private void graphFromMatrix(double[][] matrix) {

		for (int i = 0; i < matrix.length; i++) {
			this.addVertex();
		}
		for (int i = 0; i < matrix.length; i++) {
			// create new vertex
			for (int j = 0; j < matrix[i].length; j++) {
				// check if matrix is quadratic
				if (matrix.length != matrix[i].length) {
					throw new RuntimeException("matrix is not quadratic");
				}
				// check values in the matrix
				if (matrix[i][j] < 0.0) {
					throw new IllegalArgumentException(
							"negative value in the matrix");
				}
				if (i == j && matrix[i][j] != 0.0) {
					throw new IllegalArgumentException(
							"selfloops in graph are not allowed.");
				}
				// check values if an edge exists
				if (matrix[i][j] > 0.0) {
					this.addEdge(i, j, matrix[i][j]);
				}
			}
		}
		// check if a matrix for an undirected graph is given
		if (!this.m_directed) {
			for (int i = 0; i < matrix.length; i++) {
				for (int j = i; j < matrix.length; j++) {
					if (matrix[i][j] != matrix[j][i]) {
						throw new IllegalArgumentException(
								"given matrix is directed");
					}
				}
			}
		}
	}

	/**
	 * prints the 2D-double-matrix
	 */
	public void printMatrix() {
		double matrix[][] = this.getMatrix();

		System.out.print("_| ");
		for (int i = 0; i < matrix.length; i++) {
			System.out.print("_" + i + "__");
		}
		System.out.println();
		for (int i = 0; i < matrix.length; i++) {
			System.out.print(i + "| ");
			for (int j = 0; j < matrix[i].length; j++) {
				if (matrix[i][j] == Double.POSITIVE_INFINITY) {
					System.out.print(" x  ");
				} else {
					if (this.m_weighted) {
						System.out.print(matrix[i][j] + " ");
					} else {
						System.out.println("1.0 ");
					}
				}
			}
			System.out.println();
		}
	}

	/**
	 * prints the adjacency list with (key, value)-pairs if graph is weighted,
	 * else only key
	 */
	public void printList() {
		for (int i = 0; i < this.m_adjacencylist.size(); i++) {
			Map<Integer, Double> entries = this.m_adjacencylist.get(i);
			System.out.print("list(" + i + ") = {");
			for (Map.Entry<Integer, Double> entry : entries.entrySet()) {
				if (this.m_weighted) {
					System.out.print("(" + entry.getKey() + ", "
							+ entry.getValue() + ")");
				} else {
					System.out.print(entry.getKey() + " ");
				}
			}
			System.out.println("}");
		}
	}
}
