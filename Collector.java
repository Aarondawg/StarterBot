import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.methods.Magic;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.script.wrappers.RSNPC;

@ScriptManifest(authors = "Test", name = "test collect", version = 1.0, description = "Test :D")
public class Collector extends Script {

	public final RSTile homeLoc = new RSTile(3222, 3220);
	public boolean collectedAxes, collectedNet;
	
    private boolean walkPath(RSTile[] path) {
        if (calc.distanceTo(path[path.length - 1]) > 4) {
            RSTile n = getNext(path);
            if(n!=null){
                walking.walkTileMM(n.randomize(2, 2));
                if(random(1,6) != 2){
                    mouse.moveRandomly(20);
                }
                if (walking.getEnergy() < 20){
                	walking.rest(90);
                }
                if (!walking.isRunEnabled()){
                	walking.setRun(true);
                }
                sleep(random(1000, 3000));
            }
        }
        return false;
    }

    private RSTile getNext(RSTile[] path) {
        boolean found = false;
        for (int a = 0; a < path.length&&!found; a++) {
            if(calc.tileOnMap(path[path.length-1-a])){
                found = true;
                return path[path.length-1-a];
            }
        }
        return null;
    }

    private boolean collectNet(){
    	
		final RSTile[] path = {
				new RSTile(3221, 3218), new RSTile(3226, 3218),
				new RSTile(3230, 3219), new RSTile(3233, 3220),
				new RSTile(3234, 3225), new RSTile(3231, 3228),
				new RSTile(3228, 3232), new RSTile(3225, 3235),
				new RSTile(3224, 3238), new RSTile(3222, 3242),
				new RSTile(3220, 3246), new RSTile(3215, 3247),
				new RSTile(3211, 3247), new RSTile(3207, 3247),
				new RSTile(3201, 3248), new RSTile(3196, 3247),
				new RSTile(3193, 3253) 
		};
    	
    	boolean homePort = false, atHank = false;
    	
    	if (!homePort){
			magic.castSpell(Magic.SPELL_HOME_TELEPORT);
			while (calc.distanceTo(homeLoc) > 5) {
				sleep(random(10, 20));
			}
			homePort = true;
		}
			
		if (walkPath(path)){
			atHank = true;
			log("At Hank");
		} else if(calc.distanceTo(path[path.length - 1]) < 5){
			atHank = true;
			log("At Hank 2");
		}
		
		if (atHank){
			if(tradeHank()){
				return true;
			}
		}
		
		return false;
    }

	private boolean tradeHank(){
		RSNPC hank;
		hank = npcs.getNearest(8864);
		if (hank != null){
			hank.doAction("Trade");
			while (!getMyPlayer().isIdle()){
				sleep(random(500, 650));
			}
			sleep(2000);
			if (interfaces.get(620).isValid()){
				if (interfaces.get(620).getComponent(26).getComponent(2).getComponentStackSize() == 1){
					interfaces.get(620).getComponent(26).getComponent(0).doAction("Take 1");
	    			sleep(random(900, 1050));
				}
				interfaces.get(620).getComponent(15).doClick();
				return true;
			}
		}
		return false;
	}

    private boolean collectAxes(){
    	
    	final RSTile[] path = {
    			new RSTile(3222, 3217), new RSTile(3227, 3218),
    			new RSTile(3232, 3218), new RSTile(3233, 3215),
    			new RSTile(3235, 3211), new RSTile(3235, 3207),
    			new RSTile(3235, 3204), new RSTile(3231, 3203)
    	};
    	
    	boolean homePort = false, atBob = false;
    	
    	if (!homePort){
			magic.castSpell(Magic.SPELL_HOME_TELEPORT);
			while (calc.distanceTo(homeLoc) > 5) {
				sleep(random(10, 20));
			}
			homePort = true;
		}
			
		if (walkPath(path)){
			atBob = true;
			log("At Bob");
		} else if(calc.distanceTo(path[path.length - 1]) < 5){
			atBob = true;
			log("At Bob 2");
		}
		
		if (atBob){
			if(tradeBob()){
				return true;
			}
		}
		
		return false;
    }
    
    private boolean tradeBob(){
    	RSNPC bob;
    	bob = npcs.getNearest(519);
    	if (bob != null){
    		bob.doAction("Trade");
    		while (!getMyPlayer().isIdle()){
    			sleep(random(500, 650));
    		}
    		sleep(2000);
    		if (interfaces.get(620).isValid()){
    			if (interfaces.get(620).getComponent(26).getComponent(2).getComponentStackSize() == 1){
    				interfaces.get(620).getComponent(26).getComponent(0).doAction("Take 1");
        			sleep(random(900, 1050));
    			}
    			if (interfaces.get(620).getComponent(26).getComponent(6).getComponentStackSize() == 1){
    				interfaces.get(620).getComponent(26).getComponent(4).doAction("Take 1");
        			sleep(random(900, 1050));
    			}
    			interfaces.get(620).getComponent(15).doClick();
    			return true;
    		}
    	}
    	return false;
    }
    
	public boolean onStart() {
		log("Welcome to Test.");
		return true;

	}

	public void onFinish() {
		log("Goodbye.");
	}

	public int loop() {
		
		if (camera.getPitch() != 3072){
			camera.setPitch(3072);
		}
		
		if (!collectedAxes){
			if (collectAxes()){
				collectedAxes = true;
				log("Collected Axes :D");
			}
		}
		
		if (!collectedNet){
			if (collectNet()){
				collectedNet = true;
				log("Collected Net :D");
			}
		}
		
		return(random(650, 950));
	}


}
