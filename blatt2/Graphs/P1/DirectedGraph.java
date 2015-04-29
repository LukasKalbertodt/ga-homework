package P1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import renderGraph.RenderableGraph;

/**
 * Representation of a directed graph. Vertex names begin from 0
 * 
 * @author Elena Resch
 * @author Lukas Kalbertodt
 * @author Mirco Wagner
 * 
 */
public class DirectedGraph implements Graph, RenderableGraph {
	private boolean m_weighted;
	private boolean m_directed;
	private int m_vertexCount;
	private List<Map<Integer, Double>> m_adjacencylist;

	/**
	 * C-tor
	 * 
	 * @param weighted
	 */
	public DirectedGraph(boolean weighted) {
		this.m_weighted = weighted;
		this.m_directed = true;
		this.m_vertexCount = 0;
		this.m_adjacencylist = new ArrayList<Map<Integer, Double>>();
	}

	@Override
	public boolean isWeighted() {
		return this.m_weighted;
	}

	@Override
	public boolean isDirected() {
		return this.m_directed;
	}

	@Override
	public int getNodeCount() {
		return this.m_vertexCount;
	}

	@Override
	public double getWeight(int x, int y) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addEdge(int start, int end) {
		this.addEdge(start, end, 0.0);

	}

	@Override
	public void addEdge(int start, int end, double weight) {
		if (start < 0 || start >= this.m_vertexCount || end < 0
				|| end >= this.m_vertexCount) {
			throw new IllegalArgumentException("node(s) not in graph");
		}
		if (weight < 0.0) {
			throw new IllegalArgumentException("weight is < 0.0");
		}
		(this.m_adjacencylist.get(start)).put(end, weight);
	}

	@Override
	public void addVertex() {
		Map<Integer, Double> newVertex = new HashMap<Integer, Double>();
		this.m_adjacencylist.add(newVertex);
		this.m_vertexCount++;
	}

	@Override
	public List<Integer> getNeighbors(int v) {
		List<Integer> successors = this.getSuccessors(v);
		List<Integer> predecessors = this.getPredecessors(v);
		successors.addAll(predecessors);
		return successors;
	}

	@Override
	public List<Integer> getPredecessors(int v) {
		if (v < 0 || v >= this.m_vertexCount) {
			throw new IllegalArgumentException("vertex not in graph");
		}

		List<Integer> predecessors = new ArrayList<Integer>();

		// run throw the list and check if the key is in the hashmap, if so add
		// the actual index to the list of predecessors
		for (int i = 0; i < this.m_adjacencylist.size(); i++) {
			List<Integer> successors = this.getSuccessors(i);
			if (successors.contains(new Integer(v))) {
				predecessors.add(i);
			}
		}
		return predecessors;
	}

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

	@Override
	public int getVertexCount() {
		return this.m_vertexCount;
	}

	@Override
	public double getWeightOfEdge(int start, int end) {
		if(!this.hasEdge(start, end)) { //if edge is not in graph
			return Double.POSITIVE_INFINITY;
		}
		// edge is in this graph
		Map<Integer, Double> successors = this.m_adjacencylist.get(start);
		//get the corresponding value
		double value = successors.get(end);
		
		return value;
	}

	@Override
	public boolean hasEdge(int start, int end) {
		// if there is no vertex with this name so there is certainly no edge in
		// this graph
		if (start < 0 || start >= this.m_vertexCount) {
			return false;
		}
		List<Integer> successors = this.getSuccessors(start);
		if(successors.contains(new Integer(end))) {
			return true;
		}
		return false;
	}

	@Override
	public void removeEdge(int start, int end) {
		if(!this.hasEdge(start, end)) {
			return;
		}
		Map<Integer,Double> successors = this.m_adjacencylist.get(start);
		successors.remove(end);
		this.m_adjacencylist.set(start, successors);
	}

	@Override
	public void removeVertex() {
		m_adjacencylist.remove(--this.m_vertexCount);
	}

}
