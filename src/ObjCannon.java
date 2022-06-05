import processing.core.PVector;
import java.util.ArrayList;
import static processing.core.PApplet.*;

public class ObjCannon extends ObjGameGeneric {


    // ########################################################################
    // Cannon Attributes:
    // ########################################################################

    private Character cannonCharacter; // Character object this cannon is designated to.

    private int numVolleys; // Number of available shots this cannon has.
    private int numBallsPerVolley; // Number of cannonballs launched in a single volley/shot.
    private float range; // Range of cannon, which affects the instantaneous velocity of cannonballs fired.
    private int damagePerBall; // The damage that each cannonball fired will deal.

    private int coolDownTime; // Cool-down time required to wait before cannon can be activated again.
    private int currCoolDown; // The currently applied cool-down timer remaining to be waited before firing.


    // ########################################################################
    // Cannon Constructors:
    // ########################################################################

    public ObjCannon(float posXInit, float posYInit, int diameter, int colour, float orientation,
                     int numVolleys, int numBallsPerVolley, float range, int coolDownTime, int damagePerBall,
                     boolean willDeSpawn, Character character, GameState gamestate) {

        // Game Object Constructor; cannons will move with a ship or are stationary as part of a fort.
        super(posXInit, posYInit, 0, diameter, colour, willDeSpawn, gamestate);
        this.setOri(orientation); // Initial orientation of the cannon.

        // Cannon Attribute Instantiation:
        this.cannonCharacter = character;

        this.numVolleys = numVolleys;
        this.numBallsPerVolley = numBallsPerVolley;
        this.range = range;
        this.damagePerBall = damagePerBall;

        this.coolDownTime = coolDownTime;
        this.currCoolDown = 0; // Cannon is available to fire immediately when created.

    }


    // ########################################################################
    // Cannon Update Methods:
    // ########################################################################

    public boolean update() {

        if (this.isOnCoolDown()) this.decrementCoolDown(); // Reduce cool-down timer, if applied.

        boolean remove = super.update(); // Cannons should be stationary without forces applied BUT update for de-spawning.
        return remove; // Whether this object should be removed (i.e., de-spawning).

    }

    public boolean fireVolley(PVector target, PVector forceToAdd) {

        if (!this.isVolleyAvailable() || this.isOnCoolDown()) return false;

        // Calculate numBallsPerVolley projectile vectors with instantaneous force proportional to cannon range.
        ArrayList<PVector> cannonBallTrajectories = this.calcTrajectories(target);

        for (PVector currTrajectory : cannonBallTrajectories) {

            // Create a cannonball firing from this cannon's position. Apply a force to cannonball as currTrajectory.
            ObjCannonBall currCannonBall = new ObjCannonBall(this.getPosX(), this.getPosY(),
                    GameConfig.CANNONBALL_DEF_SPEED * this.range,
                    (int) (this.getDiameter() * GameConfig.CANNONBALL_SIZE_MULT),
                    GameConfig.CANNONBALL_COL, this.damagePerBall, this.cannonCharacter, this.isWillDeSpawn(), gameState);

            currTrajectory.mult(this.getRange()); // Multiply trajectories in accordance with the cannon range.
            if (forceToAdd != null) currTrajectory.add(forceToAdd); // Add force if given; e.g., if cannon is moving!
            currCannonBall.applyForce(currTrajectory); // Apply force to the cannonball.

            gameState.addCannonBall(currCannonBall); // Add cannonball to the game.

        }

        this.applyCoolDown(); // Apply cool-down for this cannon now that a volley has been fired.
        this.subNumVolleys(1); // Decrement the number of volleys available for fire.

        Game.gameSound.soundCannonFire(true); // Play sound of the cannon firing.

        return true;

    }

    public ArrayList<PVector> calcTrajectories(PVector target) {

        ArrayList<PVector> trajectories = new ArrayList<>();

        // First trajectory is simply trajectory straight towards the target.
        PVector straightTrajectory = new PVector(target.x - this.getPosX(), target.y - this.getPosY());
        straightTrajectory.normalize();
        trajectories.add(straightTrajectory);

        // Then, for every other ball in the volley, trajectory is same +- some angle of arc.
        for (int i = 1; i <= this.getNumBallsPerVolley() - 1; i++) {

            float theta = GameConfig.CANNON_BALL_SPREAD * ceil((float) i / 2);
            if (i % 2 != 0) theta = -theta; // Applying θ and -θ rotations evenly on either side of straight trajectory.

            // New co-ordinates when rotating the straight trajectory by θ is given by:
            PVector currTrajectory = new PVector();
            // x' = x cos(θ) − y sin(θ).
            currTrajectory.x = (straightTrajectory.x * cos(theta)) - (straightTrajectory.y * sin(theta));
            // y' = x sin(θ) + y cos(θ).
            currTrajectory.y = (straightTrajectory.x * sin(theta)) + (straightTrajectory.y * cos(theta));

            currTrajectory.normalize();
            trajectories.add(currTrajectory);

        }

        // Return normalised set of trajectories fanning toward target as determined by number of cannonballs per volley.
        return trajectories;

    }

    // NUM VOLLEYS:

    public void setNumVolleys(int numVolleysToSet) {
        this.numVolleys = numVolleysToSet;
    }

    public void addNumVolleys(int numVolleysToAdd) {
        this.numVolleys += numVolleysToAdd;
    }

    public void subNumVolleys(int numVolleysToTake) {
        this.numVolleys -= numVolleysToTake;
    }

    // NUM BALLS PER VOLLEY:

    public void setNumBallsPerVolley(int numBallsPerVolley) {
        this.numBallsPerVolley = numBallsPerVolley;
    }

    public void addNumBallsPerVolley(int ballsPerVolleyToAdd) {
        this.numBallsPerVolley += ballsPerVolleyToAdd;
    }

    public void subNumBallsPerVolley(int ballsPerVolleyToTake) {
        this.numBallsPerVolley -= ballsPerVolleyToTake;
    }

    // CANNON RANGE:

    public void setRange(float range) {
        this.range = range;
    }

    public void addCannonRange(float rangeToAdd) {
        this.range += rangeToAdd;
    }

    public void subCannonRange(float rangeToTake) {
        this.range -= rangeToTake;
    }

    // CANNONBALL DAMAGE:

    public void setDamagePerBall(int damagePerBall) {
        this.damagePerBall = damagePerBall;
    }

    public void addDamagePerBall(int damageToAdd) {
        this.damagePerBall += damageToAdd;
    }

    public void subDamagePerBall(int damageToTake) {
        this.damagePerBall -= damageToTake;
    }

    // COOL-DOWN TIMER:

    public void setCoolDownTime(int coolDownTime) {
        this.coolDownTime = coolDownTime;
    }

    private void applyCoolDown() {
        this.currCoolDown = this.coolDownTime;
    }

    private void decrementCoolDown() {
        this.currCoolDown -= 1;
    }


    // ########################################################################
    // Cannon Render/Draw Methods:
    // ########################################################################

    public void display() {

        this.displayCannonModels();

        if (Game.showMechanics) this.displayRangeRadius();

    }

    private void displayCannonModels() {

        Game.sketch.fill(GameConfig.CANNON_COL);
        Game.sketch.stroke(0);

        // Colour of cannon determined by cannon status.
        if (this.isOnCoolDown()) {
            Game.sketch.fill(GameConfig.CANNON_COOL_DOWN_COL);
            Game.sketch.stroke(GameConfig.CANNON_COOL_DOWN_COL);
        } else if (!this.isVolleyAvailable()) {
            Game.sketch.fill(GameConfig.CANNON_EMPTY_COL);
            Game.sketch.stroke(GameConfig.CANNON_EMPTY_COL);
        }

        int cannonLength = (int) (this.getDiameter() * GameConfig.CANNON_LENGTH_MULT);

        // Display cannon in the direction of its orientation.
        Game.sketch.translate(this.getPosX(), this.getPosY());
        Game.sketch.rotate(this.getOri());
        Game.sketch.rect(-((float) this.getDiameter() / 2), -((float) this.getDiameter() / 2), cannonLength, this.getDiameter());
        Game.sketch.rotate(-this.getOri());
        Game.sketch.translate(-this.getPosX(), -this.getPosY());

    }

    private void displayRangeRadius() {

        Game.sketch.noFill();
        Game.sketch.stroke(this.getCol());

        Game.sketch.ellipse(this.getPosX(), this.getPosY(), this.getRangeRadius() * 2, this.getRangeRadius() * 2);

    }


    // ########################################################################
    // Cannon Getter/Setter Methods:
    // ########################################################################

    public boolean isVolleyAvailable() {
        // != instead of > so that numVolleys = -1 basically means infinite ammo.
        // Need to be careful about not allowing cannon fire when numVolleys = 0, otherwise, will not work as intended.
        return (this.getNumVolleys() != 0);
    }

    public int getNumVolleys() {
        return this.numVolleys;
    }

    public int getNumBallsPerVolley() {
        return numBallsPerVolley;
    }

    public boolean isInRange(PVector target) {
        return (PVector.dist(target, this.getPos()) <= this.getRangeRadius());
    }

    public float getRange() {
        return range;
    }

    public int getRangeRadius() {
        return (int) (this.range * GameConfig.CANNON_RANGE_RADIUS_MULT);
    }

    public int getDamagePerBall() {
        return damagePerBall;
    }

    private boolean isOnCoolDown() {
        return (this.currCoolDown > 0);
    }

    public int getCoolDownTime() {
        return this.coolDownTime;
    }


}
