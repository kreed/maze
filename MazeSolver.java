import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

class MazeSolver {
	private static List<Integer> directions = Arrays.asList(0, 1, 2, 3);

	private static void add(HashMap<Integer, List<Coord>> m, Stack<Coord> s, Coord v, final int size)
	{
		v.visit();
		s.push(v);

		List<Coord> l = m.get(size);

		if (l == null) {
			l = new LinkedList<Coord>();
			m.put(size, l);
		}

		l.add(v);
	}

	private static void blazeTrail(Maze maze)
	{
		Stack<Coord> visitStack = new Stack<Coord>();
		HashMap<Integer, List<Coord>> longests = new HashMap<Integer, List<Coord>>();
		int size = -1;
		Coord pos = maze.getStart();
		Coord end = maze.getEnd();
		Coord next;

	outer:
		for (;;) {
			visitStack.clear();
			visitStack.push(pos);

			for (;; Collections.shuffle(directions)) {
				next = getMove(pos);
				if (next == end) {
					return;
				} else if (next == null) {
					visitStack.pop();
					if (visitStack.isEmpty())
						break;
					++size;
					pos = visitStack.peek();
				} else {
					pos = next;
					--size;
					add(longests, visitStack, pos, size);
				}
			}

			for (Map.Entry<Integer, List<Coord>> e : longests.entrySet())
				for (Coord c : e.getValue())
					if (!c.surrounded()) {
						pos = c.get(c.ensureRemoval(1));
						if (pos == end)
							return;
						size = e.getKey();
						continue outer;
					}
		}
	}

	private static void freeIsolates(Maze maze)
	{
		Coord uv;

		for (;;) {
			uv = null;
			do {
				uv = maze.findUnvisited(uv);
				if (uv == null)
					return;
			} while (uv.ensureRemoval(2) == -1);
			go(maze, uv, null);
		}
	}

	public static Stack<Coord> go(Maze maze, Coord start, Coord end)
	{
		Stack<Coord> visitStack = new Stack<Coord>();

		Coord pos = start;
		pos.visit();
		visitStack.push(pos);
		Coord next;

		for (;;) {
			next = getMove(pos);
			if (next == end) {
				return visitStack;
			} else if (next == null) {
				visitStack.pop();
				if (visitStack.isEmpty())
					return null;
				pos = visitStack.peek();
			} else {
				pos = next;
				visitStack.push(pos);
				pos.visit();
			}
		}
	}

	public static void makeSolvable(Maze m)
	{
		blazeTrail(m);
		freeIsolates(m);
		m.clearVisits();
	}

	private static Coord getMove(Coord pos)
	{
		Coord next;
		for (int d : directions)
			if (!pos.blocked(d)) {
				next = pos.get(d);
				if (!next.isVisited())
					return next;
			}
		return null;
	}

	public static void showTrail(Maze m)
	{
		int xr = m.getCurrentRow();
		int xc = m.getCurrentColumn();	

		Coord pos = m.getCurPos();
		Coord old;

		Stack<Coord> s = go(m, pos, m.getEnd());
		s.remove(0);

		while (!s.isEmpty()) {
			old = pos;
			pos = s.firstElement();
			s.remove(0);

			switch (old.getDir(pos)) {
			case 0:
				--xr;
				break;
			case 1:
				--xc;
				break;
			case 2:
				++xc;
				break;
			case 3:
				++xr;
				break;
			}				

			m.drawSquare(xr, xc, Color.gray);
		}

		MainWindow.w.draw();
	}
}