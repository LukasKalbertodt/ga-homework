package renderGraph;


/**
 * This is the interface that is to be implemented by a class representing a
 * graph to be rendered by RenderGraph. By allowing RenderGraph to gather all
 * the needed information for rendering a graph using these methods, the user
 * does not need to prepare that data for calling one of the rendering methods
 * of RenderGraph himself. No need to create huge multidimensional arrays of
 * edge weights and other such tools of torture! :)
 * 
 * @author Philip Muench /AG Kombinatorische Optimierung
 * 
 * 
 * 
 */
public interface RenderableGraph {

	/**
	 * Does the renderer need to add weight labels to the edges?
	 * 
	 * @return true if the graph is weighted
	 */
	public boolean isWeighted();

	/**
	 * Does the renderer need to draw arrow heads?
	 * 
	 * @return true if the graph is directed
	 */
	public boolean isDirected();

	/**
	 * How many nodes need to be rendered?
	 * 
	 * @return the number of nodes in this graph.
	 */
	public int getNodeCount();

	/**
	 * Determine the existence and weight of an edge in the graph. A nonexistant
	 * edge is to be reported by returning Double.POSITIVE_INFINITY. Otherwise,
	 * the return value is irrelevant for nonweighted graphs, and interpreted as
	 * the weight for weighted graphs. Any value other than the above denotes an
	 * existing edge. RenderGraph does not support multigraphs, therefor
	 * getWeight(x,y) == getWeight(y,x) should hold true for undirected graphs.
	 * For directed graphs, if getWeight(x,y) != Double.POSITIVE_INFINITY, then
	 * getWeight(y,x) should return Double.POSITIVE_INFINITY to denote a
	 * nonexistant edge for the same reason.
	 * 
	 * @param x
	 *            index of the first node incident to the edge in question.
	 * @param y
	 *            index of the second node incident to the edge in question.
	 * @return weight of the edge in question or Double.POSITIVE_INFINITY if it
	 *         does not exist
	 * @throws IllegalArgumentException
	 *             if an illegal node index is passed
	 */
	public double getWeight(int x, int y) throws IllegalArgumentException;

}
