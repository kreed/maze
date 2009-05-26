import java.util.Random;

class Coord {
	private boolean[] blockers = new boolean[4];
	private Coord[] neighbors = new Coord[4];
	private Coord next = null;

	private boolean visited = false;

	static private int[] directions = new int[4];
	static private Random random = new Random();

	public boolean isIsolated()
	{
		return !(blockers[0] || blockers[1] || blockers[2] || blockers[3]);
	}

	public boolean isVisited()
	{
		return visited;
	}

	public void visit()
	{
		visited = true;
	}

	public void unvisit()
	{
		visited = false;
	}

	public int ensureRemoval(int mode)
	{
		if (mode == 0 && !isIsolated())
			return -1;

		int length = 0;

		for (int i = 3; i != -1; --i)
			if (neighbors[i] != null && !blockers[i] && (mode == 0 || (mode == 1 && !neighbors[i].visited) || (mode == 2 && neighbors[i].visited)))
				directions[length++] = i;

		if (length == 0)
			return -1;

		int dir = directions[random.nextInt(length)];

		blockers[dir] = neighbors[dir].blockers[3-dir] = true;
		return dir;
	}

	public boolean surrounded()
	{
		return (neighbors[0] == null || neighbors[0].visited)
			&& (neighbors[1] == null || neighbors[1].visited)
			&& (neighbors[2] == null || neighbors[2].visited)
			&& (neighbors[3] == null || neighbors[3].visited);
	}

	public int getDir(Coord other)
	{
		if (neighbors[0] == other)
			return 0;
		if (neighbors[1] == other)
			return 1;
		if (neighbors[2] == other)
			return 2;
		if (neighbors[3] == other)
			return 3;
		return -1;
	}

	public boolean blocked(int dir)
	{
		return !blockers[dir];
	}

	public Coord getRight()
	{
		return neighbors[2];
	}

	public Coord getDown()
	{
		return neighbors[3];
	}

	public Coord getNext()
	{
		return next == null ? neighbors[2] : next;
	}

	public void setNext(Coord v)
	{
		next = v;
	}

	public Coord get(final int dir)
	{
		return neighbors[dir];
	}

	public void setUp(Coord v)
	{
		neighbors[0] = v;
	}

	public void setLeft(Coord v)
	{
		neighbors[1] = v;
	}

	public void setRight(Coord v)
	{
		neighbors[2] = v;
	}

	public void setDown(Coord v)
	{
		neighbors[3] = v;
	}
}
