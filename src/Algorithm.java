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
		Node[][] prev = new Node[graphWidth][graphHeight];
		Stack<Node> nodes = new Stack<>();
		nodes.push(start);

		while (!nodes.empty()) {
			Node curNode = nodes.pop();

			if (curNode.isEnd()) {
				curNode.setColor(Color.MAGENTA);
				break;
			}

			if (curNode.isSearched()) {
				continue;
			}

			curNode.setColor(Color.ORANGE);
			sleep();
			curNode.setColor(Color.BLUE);

			for (Node adjacent : curNode.getNeighbours()) {
				if (!adjacent.isSearched() && adjacent != start && prev[adjacent.getX()][adjacent.getY()] == null) {
					prev[adjacent.getX()][adjacent.getY()] = curNode;
				}
				if (!adjacent.isSearched()) {
					nodes.push(adjacent);
				}
			}
		}

		shortpath(prev, end);
	}

	public void bfs(Node start, Node end, int graphWidth, int graphHeight) {
		Queue<Node> queue = new LinkedList<>();
		Node[][] prev = new Node[graphWidth][graphHeight];

		queue.add(start);
		while (!queue.isEmpty()) {

			Node curNode = queue.poll();
			if (curNode.isEnd()) {
				curNode.setColor(Color.MAGENTA);
				break;
			}

			if (!curNode.isSearched()) {
				curNode.setColor(Color.ORANGE);
				sleep();
				curNode.setColor(Color.BLUE);
				for (Node adjacent : curNode.getNeighbours()) {
					// only enqueue/record a node the first time we reach it
					if (!adjacent.isSearched() && adjacent != start
							&& prev[adjacent.getX()][adjacent.getY()] == null) {
						prev[adjacent.getX()][adjacent.getY()] = curNode;
						queue.add(adjacent);
					}
				}
			}
		}

		shortpath(prev, end);
	}

	private void shortpath(Node[][] prev, Node end) {
		Node pathConstructor = end;
		while (pathConstructor != null) {
			pathConstructor = prev[pathConstructor.getX()][pathConstructor.getY()];

			if (pathConstructor != null) {
				pathConstructor.setColor(Color.ORANGE);
			}
			sleep();
		}
	}

	public void Astar(Node start, Node targetNode, int graphWidth, int graphHeight) {
		List<Node> openList = new ArrayList<Node>();
		Node[][] prev = new Node[graphWidth][graphHeight];

		// gScore = cheapest known cost from start to a given node
		double[][] gScore = new double[graphWidth][graphHeight];
		for (double[] row : gScore) {
			java.util.Arrays.fill(row, Double.POSITIVE_INFINITY);
		}
		gScore[start.getX()][start.getY()] = 0;
		openList.add(start);

		while (!openList.isEmpty()) {

			Node curNode = getLeastF(openList, gScore, targetNode);
			openList.remove(curNode);

			if (curNode.isEnd()) {
				curNode.setColor(Color.MAGENTA);
				break;
			}

			curNode.setColor(Color.ORANGE);
			sleep();
			curNode.setColor(Color.BLUE);

			double curG = gScore[curNode.getX()][curNode.getY()];
			for (Node adjacent : curNode.getNeighbours()) {
				double tentativeG = curG + Node.distance(curNode, adjacent);
				if (tentativeG < gScore[adjacent.getX()][adjacent.getY()]) {
					prev[adjacent.getX()][adjacent.getY()] = curNode;
					gScore[adjacent.getX()][adjacent.getY()] = tentativeG;
					if (!openList.contains(adjacent)) {
						openList.add(adjacent);
					}
				}
			}
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
