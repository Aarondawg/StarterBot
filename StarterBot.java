import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSArea;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Camaro`", "Bentzilla" }, name = "StarterBot", keywords = "Woodcutting", description = "AIO AIO?", version = 1.337)
public class StarterBot extends Script implements MouseListener,
		MouseMotionListener, PaintListener {

	public final RSTile[] path = { new RSTile(3222, 3218),
			new RSTile(3226, 3218), new RSTile(3231, 3219),
			new RSTile(3235, 3221), new RSTile(3235, 3225),
			new RSTile(3232, 3228), new RSTile(3228, 3231),
			new RSTile(3226, 3234), new RSTile(3220, 3236),
			new RSTile(3214, 3237), new RSTile(3209, 3237),
			new RSTile(3203, 3237), new RSTile(3196, 3236),
			new RSTile(3190, 3235), new RSTile(3185, 3235),
			new RSTile(3180, 3235), new RSTile(3173, 3234),
			new RSTile(3166, 3235), new RSTile(3162, 3233),
			new RSTile(3155, 3233), new RSTile(3148, 3234),
			new RSTile(3142, 3231), new RSTile(3136, 3228),
			new RSTile(3130, 3227), new RSTile(3124, 3225),
			new RSTile(3117, 3224), new RSTile(3112, 3224),
			new RSTile(3106, 3226), new RSTile(3099, 3228),
			new RSTile(3093, 3232), new RSTile(3087, 3236),
			new RSTile(3082, 3239), new RSTile(3076, 3242),
			new RSTile(3072, 3248), new RSTile(3070, 3254),
			new RSTile(3070, 3260), new RSTile(3072, 3266),
			new RSTile(3073, 3272), new RSTile(3072, 3276),
			new RSTile(3067, 3276), new RSTile(3065, 3273),
			new RSTile(3062, 3268), new RSTile(3061, 3266),
			new RSTile(3058, 3264), new RSTile(3054, 3264),
			new RSTile(3049, 3264), new RSTile(3043, 3263),
			new RSTile(3037, 3262), new RSTile(3031, 3265),
			new RSTile(3025, 3265), new RSTile(3019, 3264),
			new RSTile(3013, 3264), new RSTile(3008, 3263),
			new RSTile(3004, 3259), new RSTile(3004, 3255),
			new RSTile(3004, 3249), new RSTile(3002, 3243),
			new RSTile(3002, 3239), new RSTile(3001, 3233),
			new RSTile(3000, 3229), new RSTile(2999, 3224),
			new RSTile(2998, 3218), new RSTile(2998, 3214),
			new RSTile(2997, 3207), new RSTile(2996, 3201) };

	public final int[] AXE_IDS = { 1351, 1349, 1353, 1361, 1355, 1357, 1359,
			4031, 6739, 13470, 14108 };
	public final int[] PICKAXES = { 1265, 1267, 1269, 1271, 1273, 1275, 15259 };

	public final int[] NEST_IDS = { 5070, 5071, 5072, 5073, 5074, 5075, 5076,
			7413, 11966 };
	public int[] trees = { 1276, 1278 };
	public int[] oaks = { 1281 };
	public int[] willows = { 5551, 5552, 5553 };

	public long startTime;
	public int currentLevel, startWcLvl, startFishLvl, startRangeLvl,
			startAttLvl, startStrLvl, startMagicLvl, startDefLvl, currentExp,
			startExp;
	public int logsCut, oaksCut, willowsCut, bans, loads;
	public Point mousePoint;
	public boolean showProggy;

	public enum skillState {
		WC, FISH, FIGHT, RELOCATE;
	}

	public enum mainState {
		WORK, DROP, WAIT;//TODO Create a "getMainState()" method, this is going to be quite lengthly :)
	}

	public enum fightState {
		ATTACK, STRENGTH, DEFENSE, MAGIC, RANGE;
	}

	public static int[] merge(int[] arg1, int[] arg2) {
		int[] result = new int[arg1.length + arg2.length];
		System.arraycopy(arg1, 0, result, 0, arg1.length);
		System.arraycopy(arg2, 0, result, arg1.length, arg2.length);
		return result;
	}

	private boolean inArea(RSArea area) {
		if (area == null)
			return false;

		if (area.contains(getMyPlayer().getLocation()))
			return true;

		return false;
	}

	public skillState getSkill() {

		if (skills.getRealLevel(skills.WOODCUTTING) < 30) {
			return skillState.WC;
		}
		if (skills.getRealLevel(skills.FISHING) < 30) {
			return skillState.FISH;
		}
		return skillState.FIGHT;

	}

	public fightState getFightStyle() {
		if (skills.getRealLevel(skills.ATTACK) < 30) {
			return fightState.ATTACK;
		}
		if (skills.getRealLevel(skills.DEFENSE) < 30) {
			return fightState.DEFENSE;
		}
		return fightState.STRENGTH;

	}

	public void drop() {
		inventory.dropAllExcept(merge(AXE_IDS, PICKAXES));
	}

	public long TimeFromMark(long T) {
		return (System.currentTimeMillis() - T);
	}

	public boolean waitToMove(int Timeout) {
		long a = System.currentTimeMillis();
		while (!getMyPlayer().isMoving()) {
			if (TimeFromMark(a) > Timeout)
				return false;
			sleep(50);
		}
		return true;
	}

	@Override
	public boolean onStart() {

		startTime = System.currentTimeMillis();

		startWcLvl = skills.getRealLevel(skills.WOODCUTTING);
		startFishLvl = skills.getRealLevel(skills.FISHING);
		startDefLvl = skills.getRealLevel(skills.DEFENSE);
		startStrLvl = skills.getRealLevel(skills.STRENGTH);
		startAttLvl = skills.getRealLevel(skills.ATTACK);
		startRangeLvl = skills.getRealLevel(skills.RANGE);
		startMagicLvl = skills.getRealLevel(skills.MAGIC);

		log("Starting script");
		return true;
	}

	private void drawMouseLines(Graphics render) {
		if (!mouse.isPresent())
			return;

		render.setColor(Color.BLACK);
		render.drawLine(0, (int) mouse.getLocation().getY(), game.getWidth(),
				(int) mouse.getLocation().getY());
		render.drawLine((int) mouse.getLocation().getX(), 0, (int) mouse
				.getLocation().getX(), game.getHeight());

	}

	@Override
	public void onRepaint(Graphics g1) {

		Graphics2D g = (Graphics2D) g1;
		drawMouseLines(g);
		String s = "Determining...";
		switch (getSkill()) {
		case WC:
			currentLevel = skills.getCurrentLevel(skills.WOODCUTTING);
			s = "Woodcutting";
			break;
		case FISH:
			currentLevel = skills.getCurrentLevel(skills.FISHING);
			s = "Fishing";
			break;
		case FIGHT:
			switch (getFightStyle()) {
			case ATTACK:
				currentLevel = skills.getCurrentLevel(skills.ATTACK);
				s = "Attack";
				break;
			case STRENGTH:
				currentLevel = skills.getCurrentLevel(skills.STRENGTH);
				s = "Strength";
				break;
			case DEFENSE:
				currentLevel = skills.getCurrentLevel(skills.DEFENSE);
				s = "Defense";
				break;
			}
			break;

		}
		if (game.isLoggedIn()) {

			long millis = System.currentTimeMillis() - startTime;
			long hours = millis / (1000 * 60 * 60);
			millis -= hours * 1000 * 60 * 60;
			long minutes = millis / (1000 * 60);
			millis -= minutes * 1000 * 60;
			long seconds = millis / 1000;

			int runTime = (int) (System.currentTimeMillis() - startTime);

			if (!showProggy) {
				Composite c = AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, .4f);
				g.setComposite(c);
			}
			g.setColor(Color.black);
			g.fillRoundRect(556, 212, 171, 244, 16, 16);
			g.setColor(Color.black);
			g.drawRoundRect(556, 212, 171, 244, 16, 16);
			g.setColor(Color.white);
			g.drawString("Time Running: " + hours + " : " + minutes + " : "
					+ seconds, 563, 227);

			g.drawString("Training: " + s, 563, 243);// 16
			g.drawString(
					"WC LVL: " + skills.getCurrentLevel(skills.WOODCUTTING)
							+ "/30", 563, 259);
			g.drawString("FISH LVL: " + skills.getCurrentLevel(skills.FISHING)
					+ "/30", 563, 275);
			g.drawString("Fighting -", 563, 291);

			g.drawString("ATT LVL: " + skills.getCurrentLevel(skills.ATTACK)
					+ "/30", 573, 307);
			g.drawString("STR LVL: " + skills.getCurrentLevel(skills.STRENGTH)
					+ "/30", 573, 323);
			g.drawString("DEF LVL: " + skills.getCurrentLevel(skills.DEFENSE)
					+ "/30", 573, 338);

		}

	}

	@Override
	public void onFinish() {

	}

	public boolean logIn() {
		if (game.isWelcomeScreen()) {
			log("On Welcome Screen, logging in...");
			sleep(random(2000, 3500));
			interfaces.getComponent(game.INTERFACE_WELCOME_SCREEN_PLAY)
					.doClick();
			log("Clicked on login... Now waiting...");
			sleep(random(5000, 7000));
			return true;
		} else if (interfaces.getComponent(game.INTERFACE_WELCOME_SCREEN)
				.isValid()) {
			log("On Welcome Screen, logging in...");
			sleep(random(2000, 3500));
			interfaces.getComponent(game.INTERFACE_WELCOME_SCREEN_PLAY)
					.doClick();
			log("Clicked on login... Now waiting...");
			sleep(random(5000, 7000));
			return true;
		} else {
			log("Not on welcome screen... Can't login from here...");
		}
		return false;
	}

	private boolean walkPath(RSTile[] path) {
		if (calc.distanceTo(path[path.length - 1]) > 4) {
			RSTile n = getNext(path);
			if (n != null) {
				walking.walkTileMM(n.randomize(2, 2));
				if (random(1, 6) != 2) {
					mouse.moveRandomly(20);
				}
				if (walking.getEnergy() < 20) {
					walking.rest(90);
				}
				if (!walking.isRunEnabled()) {
					walking.setRun(true);
				}
			}
		}
		return false;
	}

	private RSTile getNext(RSTile[] path) {
		boolean found = false;
		for (int a = 0; a < path.length && !found; a++) {
			if (calc.tileOnMap(path[path.length - 1 - a])) {
				found = true;
				return path[path.length - 1 - a];
			}
		}
		return null;
	}

	public int wcloop() {
		return random(200, 400);
	}

	public int fishloop() {
		return random(200, 400);
	}

	public int fightloop() {
		return random(200, 400);
	}

	@Override
	public int loop() {
		if (!game.isLoggedIn()) {
			sleep(1400);
			logIn();
			return random(100, 200);
		}
		mousePoint = mouse.getLocation();
		mouse.setSpeed(random(5, 8));

		switch (getSkill()) {
		case WC:
			return wcloop();
		case FISH:
			return fishloop();
		case FIGHT:
			return fightloop();

		case RELOCATE:
			if (calc.distanceTo(new RSTile(2996, 3201)) > 50) {
				walkPath(path);
			}
		}

		return random(250, 400);
	}

	// Leave all this mouse stuff, its for the progress report.
	@Override
	public void mouseDragged(MouseEvent arg0) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if (x > 548) {
			if (x < 737) {
				if (y > 204) {
					if (y < 464) {
						showProggy = true;
					}
				}
			}
		}

		if (x < 549) {
			showProggy = false;
		}
		if (x > 736) {
			showProggy = false;
		}
		if (y < 205) {
			showProggy = false;
		}
		if (y > 463) {
			showProggy = false;
		}

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		//

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {

	}

	@Override
	public void mousePressed(MouseEvent arg0) {

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

	}

}
