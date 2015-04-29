package renderGraph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * This Class offers two methods for rendering a graph either as a
 * run-of-the-mill graph or as a bipartite graph. Either way, the graph is to be
 * passed as an instance of a class implementing the
 * <code>RenderableGraph</code>-Interface. This absolves the user of the duty to
 * prepare a graph representation in the form of multidimensional arrays or
 * other such grievances.
 * 
 * @author Philip Muench / AG Kombinatorische Optimierung
 * 
 * 
 * 
 */
public class RenderGraph {

	private RenderGraph() {
		super();
	}

	/**
	 * Render the graph with all nodes arranged in a circle
	 * 
	 * @param graph
	 *            instance of a user-supplied graph representation class
	 * 
	 * @param filename
	 *            the path and filename of the png output file. Should end in
	 *            .png, or opening it in MS Paint et al. will not immediately be
	 *            possible.
	 * @throws IOException
	 *             if an IO Error occurs
	 * @throws RuntimeException
	 *             if there is a problem with the <code>RenderableGraph</code>.
	 */
	public static void renderGraph(RenderableGraph graph, String filename)
			throws IOException {

		ImageIO.write(renderGraph(graph), "PNG", new File(filename));

	}

	/**
	 * Render the graph as a bipartite graph, two rows of nodes side-by-side.
	 * 
	 * @param graph
	 *            instance of a user-supplied graph representation class
	 * @param cardinality1
	 *            cardinality of one of the sets of nodes. This set will contain
	 *            the nodes with indices<code>0</code> through
	 *            <code>cardinality1 - 1</code>, the other the nodes with
	 *            indices <code>cardinality1</code> through
	 *            <code>graph.getNodeCount()</code>.
	 * @param filename
	 *            the path and filename of the png output file. Should end in
	 *            .png, or opening it in MS Paint et al. will not immediately be
	 *            possible.
	 * @throws IOException
	 *             if an IO Error occurs
	 * @throws RuntimeException
	 *             if there is a problem with the <code>RenderableGraph</code>.
	 */
	public static void renderBipartite(RenderableGraph graph, int cardinality1,
			String filename) throws IOException {

		ImageIO.write(renderBipartite(graph, cardinality1), "PNG", new File(
				filename));

	}

	private static void renderNodesAndLabels(Vec2D[] nodes, Vec2D[] labels,
			Graphics g) {
		g.setColor(Color.BLACK);
		for (int i = 0; i < nodes.length; i++) {
			Vec2D pos = nodes[i];
			g.fillOval((int) pos.getX() - 5, (int) pos.getY() - 5, 11, 11);
			Vec2D textPos = labels[i];
			g.drawString(Integer.toString(i), (int) textPos.getX() - 3,
					(int) textPos.getY() + 5);
		}
	}

	private static void renderEdges(RenderableGraph graph, Graphics g,
			Vec2D[] nodes) throws RuntimeException {
		Color[] c = new Color[] { Color.BLUE, Color.RED, Color.MAGENTA,
				Color.CYAN, Color.GREEN, Color.ORANGE };
		int colInd = 0;

		for (int i = 0; i < graph.getNodeCount(); i++) {

			Vec2D pos = nodes[i];

			for (int j = graph.isDirected() ? 0 : i + 1; j < graph
					.getNodeCount(); j++) {
				double weight = Double.POSITIVE_INFINITY;
				try {
					weight = graph.getWeight(i, j);
				} catch (IllegalArgumentException e) {
					throw new RuntimeException(
							"There is something wrong with the graph!"
									+ " The getNodeCount() result count"
									+ " seems to be higher than the"
									+ " actual node count!");
				}
				if (i != j && weight != Double.POSITIVE_INFINITY) {
					g.setColor(c[colInd++ % 6]);
					Vec2D target = nodes[j];
					g.drawLine((int) pos.getX(), (int) pos.getY(),
							(int) target.getX(), (int) target.getY());
					Vec2D direction = nodes[j].getSum(nodes[i].getScaled(-1.0));
					if (graph.isWeighted()) {
						Vec2D midPoint = direction.getScaled(0.7);
						double mpAngle = midPoint.angle();
						Vec2D mpOffset = Vec2D.getUnityX()
								.setAngle(mpAngle + 90.0 / Vec2D.rad2deg)
								.scaleBy(7.0);
						Vec2D labelPos = nodes[i].getSum(midPoint).getSum(
								mpOffset);
						g.drawString(Double.toString(weight),
								(int) labelPos.getX() - 3,
								(int) labelPos.getY() + 3);
					}
					if (graph.isDirected()) {
						double length = direction.length();
						direction.scaleBy((length - 5.0) / length);
						Vec2D arrowPoint = nodes[i].getSum(direction);
						double arrowAngle = direction.angle();
						double arrowAngle1 = arrowAngle + 165.0 / Vec2D.rad2deg;
						double arrowAngle2 = arrowAngle - 165.0 / Vec2D.rad2deg;
						Vec2D arrowVert1 = Vec2D.getUnityX().scaleBy(15.0)
								.setAngle(arrowAngle1).getSum(arrowPoint);
						Vec2D arrowVert2 = Vec2D.getUnityX().scaleBy(15.0)
								.setAngle(arrowAngle2).getSum(arrowPoint);

						int[] xpoints = new int[] { (int) arrowPoint.getX(),
								(int) arrowVert1.getX(),
								(int) arrowVert2.getX() };
						int[] ypoints = new int[] { (int) arrowPoint.getY(),
								(int) arrowVert1.getY(),
								(int) arrowVert2.getY() };
						g.fillPolygon(xpoints, ypoints, 3);
					}
				}

			}
		}

	}

	private static BufferedImage renderGraph(RenderableGraph graph) {
		double radius = (graph.getNodeCount() * 200.0) / (2 * Math.PI);
		int width = (int) (2.0 * radius) + 50;

		BufferedImage result = new BufferedImage(width, width,
				BufferedImage.TYPE_INT_RGB);
		Graphics g = result.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, width);
		Vec2D[] nodes = new Vec2D[graph.getNodeCount()];
		Vec2D[] fromCenter = new Vec2D[graph.getNodeCount()];
		Vec2D base = new Vec2D(radius, 0.0);
		Vec2D center = new Vec2D(width / 2, width / 2);
		double angle1 = (360.0 / graph.getNodeCount()) / Vec2D.rad2deg;
		double angle2 = angle1;
		for (int i = 0; i < graph.getNodeCount(); i++) {
			fromCenter[i] = base.clone().scaleBy((radius + 12.0) / radius)
					.addVec(center);
			nodes[i] = base.getSum(center);
			base.setAngle(angle2);
			angle2 += angle1;
		}

		renderEdges(graph, g, nodes);
		renderNodesAndLabels(nodes, fromCenter, g);
		return result;
	}

	private static BufferedImage renderBipartite(RenderableGraph graph,
			int cardinality1) {
		int dist = 100;
		int cardinality2 = graph.getNodeCount() - cardinality1;
		int width = Math.max(cardinality1, cardinality2) * dist + 100;
		BufferedImage result = new BufferedImage(width, width,
				BufferedImage.TYPE_INT_RGB);
		Graphics g = result.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, width);
		int height1 = (width - (cardinality1 - 1) * dist) / 2;
		int height2 = (width - (cardinality2 - 1) * dist) / 2;
		int width2 = width - 20;
		Vec2D[] nodes = new Vec2D[graph.getNodeCount()];
		Vec2D[] labelPos = new Vec2D[graph.getNodeCount()];
		for (int i = 0; i < cardinality1; i++) {
			nodes[i] = new Vec2D(20.0, (double) (height1 + i * dist));
			labelPos[i] = nodes[i].getSum(Vec2D.getUnityX().scaleBy(-10.0));
		}
		for (int i = cardinality1; i < graph.getNodeCount(); i++) {
			nodes[i] = new Vec2D(width2, (double) (height2 + (i - cardinality1)
					* dist));
			labelPos[i] = nodes[i].getSum(Vec2D.getUnityX().scaleBy(10.0));
		}
		renderEdges(graph, g, nodes);
		renderNodesAndLabels(nodes, labelPos, g);
		return result;
	}
}
