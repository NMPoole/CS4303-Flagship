import java.util.ArrayList;
import java.util.Iterator;

public class CharacterFortBoss extends  CharacterFort {


    // ########################################################################
    // Fort Boss Attributes:
    // ########################################################################

    private final ArrayList<CharacterFort> cornerForts;


    // ########################################################################
    // Fort Boss Constructors:
    // ########################################################################

    CharacterFortBoss(float startX, float startY, int diam, int color, int health, boolean willDeSpawn,
                  int numCannons, int cannonNumVolleys, int cannonNumBallsPerVolley, float cannonRange, int cannonCoolDown,
                  int damagePerBall, GameState gameState) {

        // Main fort is this.
        super(startX, startY, 0, diam, color, health, willDeSpawn, numCannons, cannonNumVolleys,
                cannonNumBallsPerVolley, cannonRange, cannonCoolDown, damagePerBall, gameState);

        // Update cannons sizes so they aren't too big.
        for(ObjCannon currCannon : this.getCannons()) {
            currCannon.setDiameter((int) (currCannon.getDiameter() * 0.5));
        }

        this.cornerForts = new ArrayList<>();
        for (int i = 0; i < 4; i++) { // Smaller forts sitting at each corner.

            float currFortX = (i % 2 == 0) ? startX - (float) (diam / 2 - 10) : startX + (float) (diam / 2 - 10);
            float currFortY = (i / 2 == 0) ? startY - (float) (diam / 2 - 10)  : startY + (float) (diam / 2 - 10);
            int currDiam = diam / 4;
            int currHealth = health / 10;
            float currRange = cannonRange / 2;

            this.cornerForts.add(
                    new CharacterFort(
                            currFortX, currFortY, 0, currDiam, color, currHealth, willDeSpawn,
                            numCannons, cannonNumVolleys, cannonNumBallsPerVolley, currRange, cannonCoolDown,
                            damagePerBall, gameState));

        }

    }


    // ########################################################################
    // Fort Boss Update Methods:
    // ########################################################################

    boolean update() {

        // Update Main Fort:
        boolean removeMain = super.update();

        // Update Corner Forts: Remove if they die.
        Iterator<CharacterFort> cornerFortsIter = this.cornerForts.iterator();
        while (cornerFortsIter.hasNext()) {
            CharacterFort currCornerFort = cornerFortsIter.next();
            boolean removeCurrFort = currCornerFort.update();
            if (removeCurrFort) cornerFortsIter.remove();
        }

        return removeMain;

    }


    // ########################################################################
    // Fort Boss Render/Draw Methods:
    // ########################################################################

    public void display() {

        // Display main fort:
        super.display();

        // Display each of the side forts:
        for (CharacterFort currCornerFort : this.cornerForts) {
            currCornerFort.display();
        }

    }


    // ########################################################################
    // Fort Boss Utility Methods:
    // ########################################################################

    public ArrayList<CharacterFort> getCornerForts() {
        return cornerForts;
    }


}
