import java.awt.AlphaComposite;
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
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.Skills;
import org.rsbot.script.wrappers.RSArea;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
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

	public RSTile[] pathToStairs = { new RSTile(3214, 3219),
			new RSTile(3206, 3228) };
	public final int[] AXE_IDS = { 1351, 1349, 1353, 1361, 1355, 1357, 1359,
			4031, 6739, 13470, 14108 };
	public final int[] PICKAXES = { 1265, 1267, 1269, 1271, 1273, 1275, 15259,
			303 };// 303 IS SMALL FISHING NET

	public final int[] NEST_IDS = { 5070, 5071, 5072, 5073, 5074, 5075, 5076,
			7413, 11966 };
	public int[] trees = { 1276, 1278, 38788, 38782, 38787, 38783, 38786,
			38784, 38760 };
	public int[] oaks = { 1281, 38731, 38732 };
	public int[] willows = { 5551, 5552, 5553 };

	public long startTime;
	public int currentLevel, startWcLvl, startFishLvl, startRangeLvl,
			startAttLvl, startStrLvl, startMagicLvl, startDefLvl, currentExp,
			startExp;
	public int logsCut, oaksCut, willowsCut, bans, loads;
	public Point mousePoint;
	public boolean showProggy;
	public RSTile lumbyTile = new RSTile(3223, 3233);

	public enum skillState {
		WC, FISH, FIGHT, RELOCATE, BANK;
		// RELOCATE = HEADING FROM LUMBY TO LOCATION
		// BANK = Banks your useless shit out of tut island.
	}

	public enum mainState {
		CHOP, FISH, FIGHT, DROP, WAIT, RELOC;// TODO Create a "getMainState()"
												// method, this is going to be
												// quite lengthly :)
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

	public mainState getMainState() {
		switch (getSkill()) {
		case WC:
			if (inventory.isFull()) {
				return mainState.DROP;
			}
			if (calc.distanceTo(new RSTile(2992, 3205)) < 32) {
				if (getMyPlayer().isIdle()) {
					return mainState.CHOP;
				} else {
					return mainState.WAIT;
				}
			} else {
				return mainState.RELOC;
			}
		case FISH:
			if (inventory.isFull()) {
				return mainState.DROP;
			}
			if (calc.distanceTo(new RSTile(2988, 3178)) < 15) {
				if (getMyPlayer().isIdle()) {
					return mainState.FISH;
				} else {
					return mainState.WAIT;
				}
			} else {
				return mainState.RELOC;
			}
		case FIGHT:
			if (inventory.isFull()) {
				return mainState.DROP;
			}
			if (calc.distanceTo(new RSTile(3000, 3205)) < 15) {
				if (getMyPlayer().isIdle()) {
					return mainState.FIGHT;
				} else {
					return mainState.WAIT;
				}
			} else {
				return mainState.RELOC;
			}
		}

		return mainState.WAIT;
	}

	public skillState getSkill() {
		/*
		 * if (calc.distanceTo(lumbyTile) < 20) { if (inventory.getCount() > 7)
		 * { return skillState.BANK; } else { return skillState.RELOCATE; } }
		 * else {
		 */
		if (calc.distanceTo(new RSTile(3000, 3205)) > 60) {
			if (calc.distanceTo(lumbyTile) > 19) {
				// magic.castSpell(magic.SPELL_HOME_TELEPORT);
			}
			return skillState.RELOCATE;

		}

		if (skills.getRealLevel(Skills.WOODCUTTING) < 30) {
			return skillState.WC;
		}
		if (skills.getRealLevel(Skills.FISHING) < 30) {
			return skillState.FISH;
		}
		return skillState.FIGHT;

	}

	public fightState getFightStyle() {
		if (skills.getRealLevel(Skills.ATTACK) < 30) {
			return fightState.ATTACK;
		}
		if (skills.getRealLevel(Skills.DEFENSE) < 30) {
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

		startWcLvl = skills.getRealLevel(Skills.WOODCUTTING);
		startFishLvl = skills.getRealLevel(Skills.FISHING);
		startDefLvl = skills.getRealLevel(Skills.DEFENSE);
		startStrLvl = skills.getRealLevel(Skills.STRENGTH);
		startAttLvl = skills.getRealLevel(Skills.ATTACK);
		startRangeLvl = skills.getRealLevel(Skills.RANGE);
		startMagicLvl = skills.getRealLevel(Skills.MAGIC);

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
			currentLevel = skills.getCurrentLevel(Skills.WOODCUTTING);
			s = "Woodcutting";
			break;
		case FISH:
			currentLevel = skills.getCurrentLevel(Skills.FISHING);
			s = "Fishing";
			break;
		case FIGHT:
			switch (getFightStyle()) {
			case ATTACK:
				currentLevel = skills.getCurrentLevel(Skills.ATTACK);
				s = "Attack";
				break;
			case STRENGTH:
				currentLevel = skills.getCurrentLevel(Skills.STRENGTH);
				s = "Strength";
				break;
			case DEFENSE:
				currentLevel = skills.getCurrentLevel(Skills.DEFENSE);
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
					"WC LVL: " + skills.getCurrentLevel(Skills.WOODCUTTING)
							+ "/30", 563, 259);
			g.drawString("FISH LVL: " + skills.getCurrentLevel(Skills.FISHING)
					+ "/30", 563, 275);
			g.drawString("Fighting -", 563, 291);

			g.drawString("ATT LVL: " + skills.getCurrentLevel(Skills.ATTACK)
					+ "/30", 573, 307);
			g.drawString("STR LVL: " + skills.getCurrentLevel(Skills.STRENGTH)
					+ "/30", 573, 323);
			g.drawString("DEF LVL: " + skills.getCurrentLevel(Skills.DEFENSE)
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
			interfaces.getComponent(Game.INTERFACE_WELCOME_SCREEN_PLAY)
					.doClick();
			log("Clicked on login... Now waiting...");
			sleep(random(5000, 7000));
			return true;
		} else if (interfaces.getComponent(Game.INTERFACE_WELCOME_SCREEN)
				.isValid()) {
			log("On Welcome Screen, logging in...");
			sleep(random(2000, 3500));
			interfaces.getComponent(Game.INTERFACE_WELCOME_SCREEN_PLAY)
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

	public int chop() {
		int lvl = skills.getCurrentLevel(Skills.WOODCUTTING);
		log("Chopping...");
		RSObject tree = null;
		if (lvl < 15) {
			tree = objects.getNearest(trees);

		} else {
			tree = objects.getNearest(oaks);
			if (tree == null || !tree.isOnScreen()) {
				tree = objects.getNearest(trees);
			}
		}
		if (tree != null) {
			if (!tree.isOnScreen()) {
				camera.turnTo(tree);
				if (!tree.isOnScreen()) {
					walking.walkTileMM(tree.getLocation(), 1, 1);
					tree.doAction("Chop");
				} else {
					tree.doAction("Chop");
				}

			} else {
				tree.doAction("Chop");
			}
		}
		return random(200, 400);
	}

	public int fish() {
		// int lvl = skills.getCurrentLevel(Skills.FISHING);

		RSNPC spot = null;
		spot = npcs.getNearest(325);// Fish spot NPC
		if (spot != null) {
			spot.doAction("Net");
		}
		return random(200, 400);
	}

	public int fight() {
		return random(200, 400);
	}

	public boolean walkPathSafe(RSTile[] path) { // Originally made this for
													// banking, but your walking
													// was like weird.
		if (calc.distanceTo(path[path.length - 1]) > 4) {
			RSTile n = getNext(path);
			if (n != null) {
				walking.walkTileMM(n.randomize(0, 0));
				if (random(1, 6) != 2) {
					mouse.moveRandomly(20);
				}
				if (walking.getEnergy() < 20) {
					walking.rest(90);
				}
				if (!walking.isRunEnabled()) {
					walking.setRun(true);
				}
				while (getMyPlayer().isMoving()) {
					sleep(150);
				}
				return false;
			}
		} else {
			return true;
		}
		return false;
	}

	@Override
	public int loop() {// Chopping works, it gets you from 1-15, then starts
						// searching for oaks. Progress Report works too
		if (!game.isLoggedIn()) {
			sleep(1400);
			logIn();
			return random(100, 200);
		}
		mousePoint = mouse.getLocation();
		mouse.setSpeed(random(5, 8));
		if (getSkill().equals(skillState.BANK)) {// Never is BANK, i commented
													// it out.
			if (!bank()) {
				stopScript(false);
			}
		}
		log("Get Skill = " + getSkill().toString());
		log("Get Main = " + getMainState().toString());
		if (getSkill().equals(skillState.RELOCATE)) {
			while (calc.distanceTo(new RSTile(2996, 3201)) > 10) {
				walkPathSafe(path);
			}
			return random(200, 400);
		}

		switch (getMainState()) {
		case DROP:
			drop();
			return random(200, 400);
		case CHOP:
			return chop();
		case FISH:
			return fish();
		case FIGHT:
			return fight();
		case WAIT:
			if (random(0, 20) > 17)
				mouse.move(random(10, 450), random(10, 450));
			return random(500, 1000);
		case RELOC:
			switch (getSkill()) {
			case WC:
				while (calc.distanceTo(new RSTile(2996, 3201)) > 10) {
					walkPath(path);
				}
				return random(200, 400);
			case FISH:
				while (calc.distanceTo(new RSTile(2996, 3201)) > 10) {
					walkPath(path);
				}
				return random(200, 400);
			case FIGHT:
				while (calc.distanceTo(new RSTile(2996, 3201)) > 10) {
					walkPath(path);
				}
				return random(200, 400);
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

	public boolean bank() { // Not used.. not really needed either.. didnt
							// really work either.
		while (inventory.getCount() > 7) {
			if (walkPathSafe(pathToStairs)) {
				RSTile stairTile = new RSTile(3204, 3229);
				while (getMyPlayer().isMoving()) {
					sleep(150);
				}
				sleep(random(500, 1000));
				if (calc.tileOnScreen(stairTile)) {
					Point p = calc.tileToScreen(stairTile);
					mouse.click(p, 5, 5, true);
					RSNPC duke = npcs.getNearest(741);
					int fs = 0;
					while (duke == null) {
						sleep(random(30, 80));
						duke = npcs.getNearest(741);
						fs++;
						if (fs > 75)
							break;
					}
					sleep(random(500, 1000));
					Point pp = calc.tileToScreen(stairTile);
					mouse.click(pp, 5, 5, false);
					menu.doAction("Up");
					RSNPC banker = npcs.getNearest(494);
					fs = 0;
					while (banker == null) {
						sleep(random(30, 80));
						banker = npcs.getNearest(494);
						fs++;
						if (banker != null)
							break;
						if (fs > 75)
							break;
					}
					sleep(random(500, 1000));
					// walking.walkTileMM(new
					// RSTile(banker.getLocation().getX(),
					// banker.getLocation().getY() - 2), 0, 0);

					RSObject bz = objects.getNearest(36786);
					if (bz != null) {
						if (bz.isOnScreen()) {
							bz.doAction("uick");
							while (getMyPlayer().isMoving()) {
								sleep(150);
							}
						} else {
							walking.walkTileMM(bz.getLocation(), 0, 0);
							while (getMyPlayer().isMoving()) {
								sleep(150);
							}
							sleep(random(500, 1000));
							RSObject bsz = objects.getNearest(36786);
							if (bsz != null) {
								bsz.doAction("uick");
								while (getMyPlayer().isMoving()) {
									sleep(150);
								}
							}
						}
					}
					if (!bank.isOpen()) {
						return false;
					} else {
						while (inventory.getCountExcept(false,
								merge(PICKAXES, AXE_IDS)) > 0)
							bank.depositAllExcept(merge(PICKAXES, AXE_IDS));
					}
				}
			}

		}
		return false;
	}

}