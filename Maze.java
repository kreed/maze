import java.awt.Graphics;
import java.util.Stack;
import java.awt.Color;
import java.awt.Image;

class Maze extends Thread {
	final static int width = 50;
	final static int height = 23;
	final static int blockSize = 15;

	private int r;
	private int c;
	private Color playerColor;
	private int playerNum;

	private Coord start;
	private Coord end;
	private Coord corner;
	private Coord curPos;

	private Image image;
	
	private Maze otherMaze;

	public Maze(int n, Image img)
	{
		playerNum = n;
		image = img;

		Coord[] lastRow = null;
		Coord[] thisRow;
		Coord last = null;
		Coord now;

		for (int r = 0; r != height; ++r) {
			thisRow = new Coord[width];
			for (int c = 0; c != width; ++c) {
				now = new Coord();
				if (c != 0) {
					last.setRight(now);
					now.setLeft(last);
				}
				if (r != 0) {
					now.setUp(lastRow[c]);
					lastRow[c].setDown(now);
				}
				thisRow[c] = now;
				last = now;
			}
			if (r == 0) {
				corner = thisRow[0];
				end = thisRow[width - 1];
			} else
				lastRow[width-1].setNext(thisRow[0]);
			lastRow = thisRow;
		}

		if (playerNum == 1) {
			end = lastRow[width - 1];
			start = corner;
			playerColor = Color.blue;
		} else {
			start = lastRow[c];
			playerColor = Color.red;
		}

		r = height - getEndRow() - 1;
		c = 0;
		curPos = start;

		for (Coord c = corner; c != null; c = c.getNext())
			c.ensureRemoval(0);
	}

	public void setOther(Maze v)
	{
		otherMaze = v;
	}

	public void draw()
	{
		Graphics g = image.getGraphics();

		g.setColor(Color.white);
		g.fillRect(0, 0, image.getWidth(null), image.getHeight(null));

		int x;
		int y;

		g.setColor(Color.black);

		y = 0;
		for (Coord r = corner; r != null; r = r.getDown(), y += blockSize) {
			x = 0;
			for (Coord c = r; c != null; c = c.getRight(), x += blockSize) {
				if (c == curPos)
					drawSquare(this.r, this.c, playerColor);
				if (c.blocked(0))
					g.drawLine(x, y, x + blockSize, y);
				if (c.blocked(1))
					g.drawLine(x, y, x, y + blockSize);
				if (c.blocked(2))
					g.drawLine(x + blockSize, y, x + blockSize, y + blockSize);
				if (c.blocked(3))
					g.drawLine(x, y + blockSize, x + blockSize, y + blockSize);
			}
		}
	}

	private void reRow(int dir)
	{
		switch (dir) {
		case 0:
			--r;
			break;
		case 1:
			--c;
			break;
		case 2:
			++c;
			break;
		case 3:
			++r;
			break;
		}
	}
	
	public void run()
	{
		MainWindow.lolwut = playerNum;

		r = otherMaze.getEndRow();
		c = otherMaze.getEndCol();

		Coord pos = otherMaze.end;
		Coord old;

		for (; r != otherMaze.r && c != otherMaze.c; otherMaze.clearVisits()) {
			Stack<Coord> s = MazeSolver.go(otherMaze, pos, otherMaze.curPos);
			if (s == null)
				continue;
			s.remove(0);
			while (!s.isEmpty()) {
				old = pos;
				pos = s.firstElement();
				s.remove(0);

				otherMaze.drawSquare(r, c, Color.white);
				reRow(old.getDir(pos));
				otherMaze.drawSquare(r, c, playerColor);

				MainWindow.w.draw();

				try {
					Thread.sleep(25);
				} catch(Exception e) {
				}
			}
		}

		MainWindow.lolwut = -playerNum;
		MainWindow.w.draw();
	}
	
	public void drawSquare(int row, int col, Color cl)
	{
		Graphics g = image.getGraphics();
		g.setColor(cl);
		g.fillRect(col * blockSize + 3, row * blockSize + 3, blockSize - 5, blockSize - 5);
	}

	public void move(int dir)
	{
		if (curPos.blocked(dir))
			return;

		drawSquare(r, c, Color.white);

		curPos = curPos.get(dir);

		if (curPos == end) {
			if (MainWindow.lolwut == 0)
				start();
			return;
		}

		reRow(dir);
		drawSquare(r, c, playerColor);
	}

	public void clearVisits()
	{
		for (Coord c = corner; c != null; c = c.getNext())
			c.unvisit();
	}

	public Coord findUnvisited(Coord start)
	{
		for (Coord c = start == null ? corner : start.getNext(); c != null; c = c.getNext())
			if (!c.isVisited())
				return c;
		return null;
	}

	public Coord getEnd()
	{
		return end;
	}
	
	public int getEndRow()
	{
		return playerNum == 1 ? height - 1 : 0;
	}

	public int getEndCol()
	{
		return width - 1;
	}

	public Coord getStart()
	{
		return start;
	}
	
	public Coord getCurPos()
	{
		return curPos;
	}

	public int getCurrentRow()
	{
		return r;
	}
	
	public int getCurrentColumn()
	{
		return c;
	}
}