package java.awt;

import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.ImageIcon;

import net.sce.bot.tabs.Bot;

public class Canvas extends Component implements Accessible {
	private static final String base = "canvas";
	private static int nameCounter = 0;
	
	private BufferedImage bot;
	private BufferedImage game;

	private static final long serialVersionUID = -2284879212465893870L;

	public Graphics getGraphics() {
		// need to wait till component is added to do this,
		// so we can't just make the images in the constructor
		checkImages();
		
		Graphics render = bot.getGraphics();
		render.drawImage(game, 0, 0, null);
		
		// paint bot graphics here
		((Bot) getParent().getParent()).doClientPainting(render);
		
		// Draw an icon
		Image im = new ImageIcon("icon.png").getImage();
		render.drawImage(im, getWidth() - 32, getHeight() - 32, null);
		
		Graphics g = super.getGraphics();
		g.drawImage(bot, 0, 0, null);
		return game.getGraphics();
	}
	
	private void checkImages() {
		Dimension dim = getSize();
		if(bot == null || game == null 
				|| bot.getWidth() != dim.width || bot.getHeight() != dim.height) {
			bot = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB);
			game = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB);
		}
	}

	public Canvas() { 
		
	}
	
	public String toString() { return "SCE Canvas"; }
	
	public Canvas(GraphicsConfiguration config) {
		graphicsConfig = config;
	}

	String constructComponentName() {
		synchronized (getClass()) {
			return base + nameCounter++;
		}
	}

	public void addNotify() {
		synchronized (getTreeLock()) {
			if (peer == null)
				peer = getToolkit().createCanvas(this);
			super.addNotify();
		}
	}

	public void paint(Graphics g) {
		g.clearRect(0, 0, width, height);
	}

	public void update(Graphics g) {
		g.clearRect(0, 0, width, height);
		paint(g);
	}

	boolean postsOldMouseEvents() {
		return true;
	}

	public void createBufferStrategy(int numBuffers) {
		super.createBufferStrategy(numBuffers);
	}

	public void createBufferStrategy(int numBuffers, BufferCapabilities caps)
			throws AWTException {
		super.createBufferStrategy(numBuffers, caps);
	}

	public BufferStrategy getBufferStrategy() {
		return super.getBufferStrategy();
	}

	public AccessibleContext getAccessibleContext() {
		if (accessibleContext == null) {
			accessibleContext = new AccessibleAWTCanvas();
		}
		return accessibleContext;
	}

	protected class AccessibleAWTCanvas extends AccessibleAWTComponent {
		private static final long serialVersionUID = -6325592262103146699L;

		public AccessibleRole getAccessibleRole() {
			return AccessibleRole.CANVAS;
		}
	}
}
