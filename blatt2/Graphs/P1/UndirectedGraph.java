package P1;

import java.util.List;

import renderGraph.RenderableGraph;

public class UndirectedGraph implements Graph, RenderableGraph {

	private boolean m_weighted;
	private boolean m_directed;
	private int m_vertexCount;
	
	public UndirectedGraph(boolean weighted) {
		this.m_weighted = weighted;
		this.m_directed = false;
		this.m_vertexCount = 0;
		
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
		// TODO Auto-generated method stub

	}

	@Override
	public void addEdge(int start, int end, double weight) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addVertex() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Integer> getNeighbors(int v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getPredecessors(int v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getSuccessors(int v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getVertexCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getWeightOfEdge(int start, int end) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasEdge(int start, int end) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeEdge(int start, int end) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeVertex() {
		// TODO Auto-generated method stub

	}

}
