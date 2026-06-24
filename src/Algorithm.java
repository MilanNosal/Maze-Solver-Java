import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;


public class Algorithm {

	private int searchtime = 100;

	public int getSearchTime() {
		return searchtime;
	}
	public void setSearchTime(int searchtime) {
		this.searchtime = searchtime;
	}

	private void sleep() {
		try {
			Thread.sleep(searchtime);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public void dfs(Node start, Node end, int graphWidth, int graphHeight) {
		Logger.info("DFS started from (" + start.getX() + ", " + start.getY() + ") to ("
				+ end.getX() + ", " + end.getY() + ")");
		Node[][] prev = new Node[graphWidth][graphHeight];
		Stack<Node> nodes = new Stack<>();
		nodes.push(start);
		int visited = 0;
		boolean found = false;
		Node curNode = null;

		while (!nodes.empty()) {
			Logger.info("DFS new loop");
			curNode = nodes.pop();

			if (curNode.isEnd()) {
				Logger.info("DFS reached target after visiting " + visited + " nodes");
				curNode.setColor(Color.MAGENTA);
				found = true;
				break;
			}

			if (curNode.isSearched()) {
				Logger.debug("DFS skipping already-visited node (" + curNode.getX() + ", " + curNode.getY() + ")");
				continue;
			}

			Logger.node("DFS visiting", curNode);
			visited++;
			curNode.setColor(Color.ORANGE);
			sleep();
			curNode.setColor(Color.BLUE);

			for (Node adjacent : curNode.getNeighbours()) {
				if (!adjacent.isSearched() && adjacent != start && prev[adjacent.getX()][adjacent.getY()] == null) {
					prev[adjacent.getX()][adjacent.getY()] = curNode;
				}
				if (!adjacent.isSearched()) {
					Logger.node("DFS pushing", adjacent);
					nodes.push(adjacent);
				}
			}

			Logger.nextDfs(nodes);
			Logger.info("DFS end loop");
		}

		if (!found) {
			Logger.warn("DFS exhausted the stack without reaching the target");
		}
		shortpath(prev, end);
	}

	public void bfs(Node start, Node end, int graphWidth, int graphHeight) {
		Logger.info("BFS started from (" + start.getX() + ", " + start.getY() + ") to ("
				+ end.getX() + ", " + end.getY() + ")");
		Queue<Node> queue = new LinkedList<>();
		Node[][] prev = new Node[graphWidth][graphHeight];
		int visited = 0;
		boolean found = false;

		queue.add(start);
		while (!queue.isEmpty()) {
			Logger.info("BFS new loop");

			Node curNode = queue.poll();
			if (curNode.isEnd()) {
				Logger.info("BFS reached target after visiting " + visited + " nodes");
				curNode.setColor(Color.MAGENTA);
				found = true;
				break;
			}

			if (!curNode.isSearched()) {
				Logger.node("BFS visiting", curNode);
				visited++;
				curNode.setColor(Color.ORANGE);
				sleep();
				curNode.setColor(Color.BLUE);
				for (Node adjacent : curNode.getNeighbours()) {
					// only enqueue/record a node the first time we reach it
					if (!adjacent.isSearched() && adjacent != start
							&& prev[adjacent.getX()][adjacent.getY()] == null) {
						prev[adjacent.getX()][adjacent.getY()] = curNode;
						Logger.node("BFS enqueuing", adjacent);
						queue.add(adjacent);
					}
				}

				Logger.nextBfs(queue);
			}
		}

		if (!found) {
			Logger.warn("BFS exhausted the queue without reaching the target");
		}
		shortpath(prev, end);
	}

	private void shortpath(Node[][] prev, Node end) {
		if (prev[end.getX()][end.getY()] == null) {
			Logger.warn("No path to the target; nothing to reconstruct");
			return;
		}
		Logger.info("Reconstructing shortest path to target");
		Node pathConstructor = end;
		int length = 0;
		while (pathConstructor != null) {
			pathConstructor = prev[pathConstructor.getX()][pathConstructor.getY()];

			if (pathConstructor != null) {
				Logger.node("Path", pathConstructor);
				pathConstructor.setColor(Color.ORANGE);
				length++;
			}
			sleep();
		}
		Logger.info("Path reconstruction complete (" + length + " steps)");
	}

	public void Astar(Node start, Node targetNode, int graphWidth, int graphHeight) {
		Logger.info("A* started from (" + start.getX() + ", " + start.getY() + ") to ("
				+ targetNode.getX() + ", " + targetNode.getY() + ")");
		List<Node> openList = new ArrayList<Node>();
		Node[][] prev = new Node[graphWidth][graphHeight];

		// gScore = cheapest known cost from start to a given node
		double[][] gScore = new double[graphWidth][graphHeight];
		for (double[] row : gScore) {
			java.util.Arrays.fill(row, Double.POSITIVE_INFINITY);
		}
		gScore[start.getX()][start.getY()] = 0;
		openList.add(start);
		int visited = 0;
		boolean found = false;

		while (!openList.isEmpty()) {

			Node curNode = getLeastF(openList, gScore, targetNode);
			openList.remove(curNode);

			if (curNode.isEnd()) {
				Logger.info("A* reached target after visiting " + visited + " nodes");
				curNode.setColor(Color.MAGENTA);
				found = true;
				break;
			}

			Logger.debug("A* expanding node (" + curNode.getX() + ", " + curNode.getY() + ") f="
					+ (gScore[curNode.getX()][curNode.getY()] + Node.distance(curNode, targetNode)));
			visited++;
			curNode.setColor(Color.ORANGE);
			sleep();
			curNode.setColor(Color.BLUE);

			double curG = gScore[curNode.getX()][curNode.getY()];
			for (Node adjacent : curNode.getNeighbours()) {
				double tentativeG = curG + Node.distance(curNode, adjacent);
				if (tentativeG < gScore[adjacent.getX()][adjacent.getY()]) {
					prev[adjacent.getX()][adjacent.getY()] = curNode;
					gScore[adjacent.getX()][adjacent.getY()] = tentativeG;
					Logger.debug("A* relaxing neighbour (" + adjacent.getX() + ", " + adjacent.getY()
							+ ") g=" + tentativeG);
					if (!openList.contains(adjacent)) {
						openList.add(adjacent);
					}
				}
			}

			Logger.nextAstar(getLeastF(openList, gScore, targetNode));
		}

		if (!found) {
			Logger.warn("A* exhausted the open list without reaching the target");
		}
		shortpath(prev, targetNode);
	}

	// node in the open list with the lowest f = g + h (h = straight-line distance to target)
	private Node getLeastF(List<Node> nodes, double[][] gScore, Node end) {
		if (nodes.isEmpty()) {
			return null;
		}
		Node best = nodes.get(0);
		double bestF = gScore[best.getX()][best.getY()] + Node.distance(best, end);
		for (int i = 1; i < nodes.size(); i++) {
			Node n = nodes.get(i);
			double f = gScore[n.getX()][n.getY()] + Node.distance(n, end);
			if (f < bestF) {
				best = n;
				bestF = f;
			}
		}
		return best;
	}

}
