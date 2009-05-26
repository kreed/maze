import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Area;
import java.awt.image.BufferStrategy;

public class MainWindow extends Frame implements KeyListener {
	static MainWindow w;
	static int lolwut = 0;

	public static void main(String[] args)
	{
		w = new MainWindow();
	}

	Maze a;
	Maze b;

	Image imageA;
	Image imageB;
	Image buffer;
	
	Ellipse2D.Double los = new Ellipse2D.Double();
	int losSize = 50;
	int losOffset = (losSize - Maze.blockSize) / 2;

	public MainWindow()
	{
		super(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());

		addKeyListener(this);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});

		setSize(900, 750);
		setVisible(true);

		setIgnoreRepaint(true);
		createBufferStrategy(2);

		BufferStrategy s = getBufferStrategy();
		Graphics g = s.getDrawGraphics();

		g.setFont(g.getFont().deriveFont(32.0f));
		g.drawString("Generating Maze...", 20, 70);

		g.dispose();

		s.show();

		imageA = createImage(Maze.blockSize * Maze.width + 1, Maze.blockSize * Maze.height + 1);
		imageB = createImage(Maze.blockSize * Maze.width + 1, Maze.blockSize * Maze.height + 1);

		a = new Maze(1, imageA);
		MazeSolver.makeSolvable(a);
	
		b = new Maze(2, imageB);
		MazeSolver.makeSolvable(b);

		a.setOther(b);
		b.setOther(a);

		a.draw();
		b.draw();

		draw();
	}

	public void draw()
	{
		BufferStrategy s = getBufferStrategy();
		Graphics g = s.getDrawGraphics();

		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());

		g.translate(5, 30);

		if (lolwut < 0) {
			Maze m;
			if (lolwut == -1) {
				m = b;
				g.translate(0, Maze.blockSize * (Maze.height + 1));
			} else {
				m = a;
			}

			g.setColor(Color.red);
			g.fillRect((m.getCurrentColumn() - 2) * Maze.blockSize, (m.getCurrentRow() - 2) * Maze.blockSize, Maze.blockSize * 5, Maze.blockSize * 5);
			g.setColor(Color.white);
			g.setFont(g.getFont().deriveFont(24.0f));
			g.drawString("lolwut", (m.getCurrentColumn() - 2) * Maze.blockSize + 3, (m.getCurrentRow() + 1) * Maze.blockSize + 3);

			g.dispose();
			s.show();
		}

		if (lolwut != 1) {
			los.setFrame(a.getCurrentColumn() * Maze.blockSize - losOffset, a.getCurrentRow() * Maze.blockSize - losOffset, losSize, losSize);
			g.setClip(los);
			if (lolwut == 2) {
				Area a = new Area(los);
				los.setFrame(b.getCurrentColumn() * Maze.blockSize - losOffset, b.getCurrentRow() * Maze.blockSize - losOffset, losSize, losSize);
				a.add(new Area(los));
				g.setClip(a);
			}
		}

		g.drawImage(imageA, 0, 0, null);
		
		g.translate(0, Maze.blockSize * (Maze.height + 1));

		if (lolwut == 2) {
			g.setClip(null);
		} else {
			los.setFrame(b.getCurrentColumn() * Maze.blockSize - losOffset, b.getCurrentRow() * Maze.blockSize - losOffset, losSize, losSize);
			g.setClip(los);
			if (lolwut == 1) {
				Area c = new Area(los);
				los.setFrame(a.getCurrentColumn() * Maze.blockSize - losOffset, a.getCurrentRow() * Maze.blockSize - losOffset, losSize, losSize);
				c.add(new Area(los));
				g.setClip(c);
			}
		}
	
		g.drawImage(imageB, 0, 0, null);

		g.translate(Maze.blockSize * (Maze.width - 1), Maze.blockSize * -2);
		g.setClip(null);

		g.setColor(Color.gray);
		g.fillRect(0, 0, Maze.blockSize * 6, Maze.blockSize * 3);
		g.setColor(Color.black);
		g.drawRect(0, 0, Maze.blockSize * 6, Maze.blockSize * 3);

		g.setFont(g.getFont().deriveFont(32.0f));
		g.setColor(Color.white);
		g.drawString("END?", 3, 35);

		g.dispose();

		s.show();
	}
	
	public void keyTyped(KeyEvent e)
	{
	}

	public void keyReleased(KeyEvent e)
	{
	}

	public void setLos(int diam)
	{
		if (diam < Maze.blockSize)
			return;
		if (diam / 3 > Maze.blockSize * Maze.width && diam / 3 > Maze.blockSize * Maze.height)
			return;
		
		losSize = diam;
		losOffset = (diam - Maze.blockSize) / 2;

		draw();
	}

	public void keyPressed(KeyEvent e)
	{
		if (lolwut > 0)
			return;
		if (lolwut < 0)
			System.exit(0);

		switch (e.getKeyCode()) {
		case KeyEvent.VK_W:
			a.move(0);
			break;
		case KeyEvent.VK_A:
			a.move(1);
			break;
		case KeyEvent.VK_D:
			a.move(2);
			break;
		case KeyEvent.VK_S:
			a.move(3);
			break;
		case KeyEvent.VK_UP:
			b.move(0);
			break;
		case KeyEvent.VK_LEFT:
			b.move(1);
			break;
		case KeyEvent.VK_RIGHT:
			b.move(2);
			break;
		case KeyEvent.VK_DOWN:
			b.move(3);
			break;
		case KeyEvent.VK_EQUALS:
			setLos(losSize * 3 / 2);
			break;
		case KeyEvent.VK_MINUS:
			setLos(losSize / 3 * 2);
			break;
		case KeyEvent.VK_V:
			MazeSolver.showTrail(a);
			break;
		case KeyEvent.VK_B:
			MazeSolver.showTrail(b);
			break;
		case KeyEvent.VK_ESCAPE:
			System.exit(0);
		}

		draw();
	}
}
