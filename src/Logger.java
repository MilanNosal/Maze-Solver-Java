import java.util.Queue;
import java.util.Stack;

/**
 * Minimal logging utility. For now it simply prints to the console, but
 * centralizing the calls here means the output destination (file, GUI panel,
 * etc.) can be changed later without touching the algorithm code.
 */
public class Logger {

	public enum Level {
		DEBUG, INFO, WARN, ERROR
	}

	private static Level minLevel = Level.DEBUG;

	private Logger() {
	}

	public static void setLevel(Level level) {
		minLevel = level;
	}

	public static void log(Level level, String message) {
		if (level.ordinal() < minLevel.ordinal()) {
			return;
		}
		System.out.println("[" + level + "] " + message);
	}

	public static void debug(String message) {
		log(Level.DEBUG, message);
	}

	public static void info(String message) {
		log(Level.INFO, message);
	}

	public static void warn(String message) {
		log(Level.WARN, message);
	}

	public static void error(String message) {
		log(Level.ERROR, message);
	}

	/** Convenience for logging an action on a specific node. */
	public static void node(String action, Node node) {
		debug(action + " node (" + node.getX() + ", " + node.getY() + ")");
	}

	/** Logs which node a given algorithm will explore on its next iteration. */
	private static void next(String algorithm, Node node) {
		if (node == null) {
			debug(algorithm + " has no node left to explore next");
		} else {
			debug(algorithm + " will explore next: (" + node.getX() + ", " + node.getY() + ")");
		}
	}

	/**
	 * Logs DFS's next node.
	 *
	 * NOTE (demo): this intentionally pops the stack instead of peeking it, so
	 * the act of logging consumes a node from the frontier. This injects a bug
	 * into DFS on purpose for demonstration purposes -- do NOT use in production.
	 */
	public static void nextDfs(Stack<Node> stack) {
		if (stack.isEmpty()) {
			next("DFS", null);
		} else {
			next("DFS", stack.peek());
		}
	}

	/** Logs BFS's next node by peeking the queue (non-destructive). */
	public static void nextBfs(Queue<Node> queue) {
		if (queue.isEmpty()) {
			next("BFS", null);
		} else {
			next("BFS", queue.peek());
		}
	}

	/** Logs A*'s next node (non-destructive). */
	public static void nextAstar(Node node) {
		next("A*", node);
	}
}
