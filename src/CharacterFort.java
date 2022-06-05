import processing.core.PVector;

import java.util.ArrayList;

public class CharacterFort extends Character {


    // ########################################################################
    // Fort Attributes:
    // ########################################################################

    ArrayList<ObjCannon> cannons;


    // ########################################################################
    // Fort Constructors:
    // ########################################################################

    CharacterFort(float startX, float startY, float maxSpeed, int diam, int color, int health, boolean willDeSpawn,
                  int numCannons, int cannonNumVolleys, int cannonNumBallsPerVolley, float cannonRange, int cannonCoolDown,
                  int damagePerBall, GameState gameState) {

        // Initialise with Character class constructor.
        super(GameConfig.CharType.LAND, startX, startY, maxSpeed, diam, color, health, willDeSpawn, gameState);

        // Cannon Attributes:
        this.initCannons(numCannons, willDeSpawn, cannonNumVolleys, cannonNumBallsPerVolley, cannonRange, cannonCoolDown, damagePerBall);
        this.initCannons(numCannons, willDeSpawn, cannonNumVolleys, cannonNumBallsPerVolley, cannonRange, cannonCoolDown, damagePerBall);

    }

    private void initCannons(int numCannons, boolean willDeSpawn, int cannonNumVolleys, int cannonNumBallsPerVolley, float cannonRange,
                             int cannonCoolDown, int damagePerBall) {

        this.cannons = new ArrayList<>();

        // All cannons in the fort have the same cannon diameter.
        int cannonDiam = (int) (this.getDiameter() * GameConfig.CANNON_FORT_SCALE_MULT);

        // Create a cannon object for however many cannons are specified to attach to the fort.
        for (int i = 0; i < numCannons; i++) {

            PVector cannonPos = this.getCannonPos(i);
            float ori = PVector.sub(cannonPos, this.getPos()).heading();

            this.cannons.add(
                    new ObjCannon(cannonPos.x, cannonPos.y, cannonDiam, GameConfig.CANNON_COL, ori,
                            cannonNumVolleys, cannonNumBallsPerVolley, cannonRange, cannonCoolDown, damagePerBall,
                            willDeSpawn, this, this.gameState));

        }

    }


    // ########################################################################
    // Fort Update Methods:
    // ########################################################################

    boolean update() {

        // Decision Tree:
        //      If aware of player and player in range, then shoot the player.
        //      Otherwise, do nothing - stationary.

        Character player = super.awareOfPlayer();

        if (player != null) { // The fort is aware of the player.

            for (ObjCannon currCannon : this.cannons) {

                PVector target = player.getPos();

                // Point the cannon at the player.
                PVector cannonToTargetVector = target.copy().sub(currCannon.getPos());
                currCannon.setOri(cannonToTargetVector.heading());

                if (currCannon.isInRange(target)) currCannon.fireVolley(target, null); // Fire at the player, no force to add as stationary.

            }

        }

        this.updateCannons(); // Update cannons state.

        boolean remove = super.update(); // Update this fort as an instance of a Character.
        return remove; // Whether this object should be removed (i.e., de-spawning).

    }

    private void updateCannons() {

        // Update the state of the onboard cannons.
        int i = 0;
        for (ObjCannon currCannon : this.cannons) {

            currCannon.update(); // Update internal cannon state.

            // Recalculate cannon position based on changed to fort position.
            PVector cannonPos = this.getCannonPos(i);
            currCannon.setPos(cannonPos);

            i += 1;

        }

    }

    private PVector getCannonPos(int cannonIndex) {

        PVector cannonPos;
        int posOffset = this.getDiameter() / 2;

        // Hard Code - Could have made this dynamic for the min and max amount of cannons, but none important.
        if (cannonIndex == 0) {
            cannonPos = new PVector(this.getPosX(), this.getPosY() + posOffset);
        } else if (cannonIndex == 1) {
            cannonPos = new PVector(this.getPosX(), this.getPosY() - posOffset);
        } else if (cannonIndex == 2) {
            cannonPos = new PVector(this.getPosX() + posOffset, this.getPosY());
        } else {
            cannonPos = new PVector(this.getPosX() - posOffset, this.getPosY());
        }

        return cannonPos;

    }


    // ########################################################################
    // Fort Render/Draw Methods:
    // ########################################################################

    public void display() {

        this.displayFortGeom(); // Display the fort.
        for (ObjCannon currCannon : this.cannons) currCannon.display(); // Display the fort's cannons.

        super.display(); // Display attributes all characters should display (e.g., health).

    }

    private void displayFortGeom() {

        // Display the fort geometry; square of sand with a wall.
        Game.sketch.fill(this.getCol());
        Game.sketch.stroke(GameConfig.FORT_WALL_COL);
        Game.sketch.strokeWeight(GameConfig.FORT_WALL_WEIGHT);

        Game.sketch.rect(this.getPosX() - (float) (this.getDiameter() / 2),
                        this.getPosY() - (float)(this.getDiameter() / 2),
                            this.getDiameter(), this.getDiameter());

        Game.sketch.strokeWeight(1); // Reset stroke weight.

    }


    // ########################################################################
    // Fort Getters/Setters:
    // ########################################################################

    public ArrayList<ObjCannon> getCannons() {
        return cannons;
    }


}
