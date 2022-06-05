import processing.core.PVector;

import static java.lang.Math.abs;
import static processing.core.PApplet.map;
import static processing.core.PConstants.*;

public class CharacterShip extends Character {


    // ########################################################################
    // Ship Attributes:
    // ########################################################################

    // Mast Attributes:
    private float sailNormAngle;
    private final int sailSize;

    // Cannon Attributes:
    // Thematically, the port and starboard side cannons, respectively.
    private ObjCannon leftCannon;
    private ObjCannon rightCannon;


    // ########################################################################
    // Ship Constructors:
    // ########################################################################

    CharacterShip(float startX, float startY, float maxSpeed, int playerDiam, int playerColor, int health, boolean willDeSpawn,
                  int cannonNumVolleys, int cannonNumBallsPerVolley, float cannonRange, int cannonCoolDown,
                  int damagePerBall, GameState gameState) {

        // Initialise with Character class constructor.
        super(GameConfig.CharType.MARITIME, startX, startY, maxSpeed, playerDiam, playerColor, health, willDeSpawn, gameState);

        // Sail Attributes:
        this.sailNormAngle = this.getOri();
        this.sailSize = playerDiam * 3;

        // Cannon Attributes:
        this.initShipCannon(true, willDeSpawn, cannonNumVolleys, cannonNumBallsPerVolley, cannonRange, cannonCoolDown, damagePerBall);
        this.initShipCannon(false, willDeSpawn, cannonNumVolleys, cannonNumBallsPerVolley, cannonRange, cannonCoolDown, damagePerBall);

    }

    private void initShipCannon(boolean isLeft, boolean willDeSpawn, int cannonNumVolleys, int cannonNumBallsPerVolley,
                                float cannonRange, int cannonCoolDown, int damagePerBall) {

        float ori = (isLeft) ? this.getOri() - (PI / 4) : this.getOri() + (PI / 4);
        int diam = (int) (this.getDiameter() * GameConfig.CANNON_SHIP_SCALE_MULT);

        float startX = this.getPosX(); // + (PVector.fromAngle(ori).x * diam); // Offset the cannons from the middle.
        float startY = this.getPosY(); // + (PVector.fromAngle(ori).y * diam); // Offset cannons from middle.

        if (isLeft) {
            this.leftCannon = new ObjCannon(startX, startY, diam, GameConfig.CANNON_COL, ori,
                    cannonNumVolleys, cannonNumBallsPerVolley, cannonRange, cannonCoolDown, damagePerBall, willDeSpawn,
                    this, this.gameState);
        } else {
            this.rightCannon = new ObjCannon(startX, startY, diam, GameConfig.CANNON_COL, ori,
                    cannonNumVolleys, cannonNumBallsPerVolley, cannonRange, cannonCoolDown, damagePerBall, willDeSpawn,
                    this, this.gameState);
        }

    }


    // ########################################################################
    // Ship Update Methods:
    // ########################################################################

    boolean update() {

        this.applyWindForce(); // Apply affect of wind on the ship.
        this.applyDragWaterForce(); // Apply effect of drag in the water on the ship.

        boolean remove = super.update(); // Update this ships' movement as an instance of a Character.

        this.updateCannonsPos(); // Update the position of the cannons on this ship to move with the ship.
        this.setOri(this.getVel().heading()); // Update orientation of the ship to direction of motion.

        return remove; // Whether this object should be removed (i.e., de-spawning).

    }


    // ########################################################################
    // Ship Render/Display Methods:
    // ########################################################################

    public void display() {

        // Display the ship.
        this.displayShipGeom();
        this.displayShipCannons();
        this.displayShipMast();

        // Display attributes all characters should display (e.g., health).
        super.display();

    }

    private void displayShipGeom() {

        // Calculate and apply rotation of model to point at aim.

        Game.sketch.translate(this.getPosX(), this.getPosY()); // Translate co-ordinate origin to player position.
        Game.sketch.rotate(this.getOri() + HALF_PI); // Rotate model to point in aiming direction.

        Game.sketch.fill(this.getCol());
        Game.sketch.stroke(0);

        // Draw model.
        Game.sketch.triangle((float) -this.getDiameter() / 2, (float) this.getDiameter() / 2,
                            (float) this.getDiameter() / 2, (float) this.getDiameter() / 2,
                            0, -(2 * (float) (this.getDiameter() / 2)));

        // Reset the origin.
        Game.sketch.rotate(-(this.getOri() + HALF_PI));
        Game.sketch.translate(-this.getPosX(), -this.getPosY());

    }

    void displayShipMast() {

        Game.sketch.translate(this.getPosX(), this.getPosY()); // Translate co-ordinate origin to player position.
        Game.sketch.rotate(this.sailNormAngle); // Rotate model to point in aiming direction.

        this.displaySail();

        // Reset the origin.
        Game.sketch.rotate(-this.sailNormAngle);
        Game.sketch.translate(-this.getPosX(), -this.getPosY());

    }

    void displaySail() {

        Game.sketch.fill(GameConfig.SHIP_MAST_COL);
        Game.sketch.stroke(GameConfig.SHIP_MAST_COL);

        Game.sketch.rotate(HALF_PI);

        float mastStartX = (float) (-this.sailSize / 2);
        float mastStartY = 0;
        float mastGirth = (float) (this.getDiameter() / 8);

        // Main mast platform display.
        Game.sketch.rect(mastStartX, mastStartY, this.sailSize, mastGirth);

        Game.sketch.fill(GameConfig.SHIP_SAIL_COL);
        Game.sketch.stroke(GameConfig.SHIP_SAIL_COL);

        // Sail display.
        float windAlignment = this.getWindAlignment();
        int sailRectWidth = (int) (windAlignment * this.getDiameter() * GameConfig.SHIP_SAIL_SIZE_MULT);
        Game.sketch.rect(mastStartX, mastStartY - sailRectWidth, this.sailSize, sailRectWidth);

        Game.sketch.rotate(-HALF_PI);

    }

    private void displayShipCannons() {

        // Show cannons.
        this.leftCannon.display();
        this.rightCannon.display();

    }


    // ########################################################################
    // Ship - Weather Forces:
    // ########################################################################

    private void applyWindForce() {

        // Wind Alignment - 0 or between 0.5 and 1 -> 0 is unaligned (mast backward) -> 0.5 to 1 depending on forward alignment with wind.
        float windAlignment = this.getWindAlignment();

        // Ship should be affected by the wind.
        PVector wind = PVector.fromAngle(gameState.getWindDirAngle());
        wind.mult(gameState.getWindStrength());
        wind.mult(GameConfig.WIND_SCALE_MULT);
        wind.mult(windAlignment);

        this.applyForce(wind);

    }

    private void applyDragWaterForce() {

        PVector drag = this.getVel().copy();
        drag.normalize();

        float speed = this.getVel().mag();
        drag.mult(GameConfig.WATER_DRAG_COEFF * speed);

        this.applyForce(drag);

    }

    public PVector applyWindForceScaling(PVector steer) {

        steer.normalize();
        steer.mult(this.getMaxSpeed() * GameConfig.SHIP_MOV_FORCE_MULT);
        if (this.getVel().mag() < this.getMaxSpeed() / 2) steer.mult(this.getWindAlignment());

        return steer;

    }


    // ########################################################################
    // Ship - Sail:
    // ########################################################################

    public void setSailNormAngle(float sailNormAngle) {

        // Keep between -PI and PI for ease.
        if (sailNormAngle > PI) sailNormAngle -= (2 * PI);
        else if (sailNormAngle < -PI) sailNormAngle += (2 * PI);

        this.sailNormAngle = sailNormAngle;

    }

    public float getSailNormAngle() {
        return sailNormAngle;
    }

    public float getWindAlignment() {

        float angleAlignment = abs(this.sailNormAngle - gameState.getWindDirAngle());
        float windAlignment = map(angleAlignment, 0, 2*PI, -1, 1);
        if (windAlignment >= -0.5 && windAlignment <= 0.5) windAlignment = 0;

        return abs(windAlignment);

    }

    public int dirToTurnOfTwoAngles(float a, float b) {

        float delta = (2 * PI) - a;
        b = b + delta;
        b = b % (2 * PI);

        if (b < PI) return 1; // Clockwise.
        else return -1; // Anti-Clockwise.

    }

    public void updateSailDirToTarget(PVector target) {

        PVector sailToTargetVector = target.copy().sub(this.getPos());
        this.setSailNormAngle(sailToTargetVector.heading());

    }


    // ########################################################################
    // Ship - Cannons:
    // ########################################################################

    public boolean fireCannons(PVector target) {

        // Determine which side of cannons to fire on given the target.
        boolean fireRight = this.isTargetRightOfShip(this.getOri(), target);

        // Fire cannon on determined side.
        // NOTE: Add velocity of ship as component of the cannonballs initial instantaneous force.
        //       Scaling this velocity by the length of the cannon makes the ball look like its travels the cannon length.
        //       This also has the benefit of increasing shot inaccuracy as speed!
        if (fireRight) return this.rightCannon.fireVolley(target, this.getVel().copy().mult(GameConfig.CANNON_LENGTH_MULT));
        else return this.leftCannon.fireVolley(target, this.getVel().copy().mult(GameConfig.CANNON_LENGTH_MULT));

    }

    private void updateCannonsPos() {

        // Update the state of the onboard cannons.
        this.leftCannon.update();
        this.rightCannon.update();

        // Also need to update the cannon positions to reflect the movement. Just add ship velocity to cannon positions.
        this.leftCannon.setPos(this.getPos().add(this.getVel()));
        this.rightCannon.setPos(this.getPos().add(this.getVel()));

    }

    public void updateCannonsAim(PVector target) {

        // Calculate which cannon is relevant and update its orientation to point at the target.
        boolean aimRight = this.isTargetRightOfShip(this.getOri(), target);

        if (aimRight) {
            PVector cannonToTargetVector = target.copy().sub(this.getRightCannon().getPos());
            this.getRightCannon().setOri(cannonToTargetVector.heading());
        } else {
            PVector cannonToTargetVector = target.copy().sub(this.getLeftCannon().getPos());
            this.getLeftCannon().setOri(cannonToTargetVector.heading());
        }

    }

    public boolean isTargetInRange(PVector target) {

        // Calculate which cannon is relevant.
        boolean aimRight = this.isTargetRightOfShip(this.getOri(), target);

        // Calculate whether the target is in range of the cannon on the side of the target.
        if (aimRight) return this.getRightCannon().isInRange(target);
        else return this.getLeftCannon().isInRange(target);

    }

    boolean isTargetRightOfShip(float shipOri, PVector target){

        PVector shipMiddle = this.getPos();

        PVector shipBow = this.getPos().copy();
        shipBow.add(PVector.fromAngle(shipOri).mult(this.getDiameter()));

        return (shipBow.x - shipMiddle.x) * (target.y - shipMiddle.y) -
                (shipBow.y - shipMiddle.y) * (target.x - shipMiddle.x) > 0;

    }

    public ObjCannon getLeftCannon() {
        return leftCannon;
    }

    public ObjCannon getRightCannon() {
        return rightCannon;
    }

    public int getTotalNumVolleys() {
        return (this.getLeftCannon().getNumVolleys() + this.getRightCannon().getNumVolleys());
    }

    public int getNumBallsPerVolley() {
        return this.getLeftCannon().getNumBallsPerVolley(); // Symmetric between both cannons.
    }

    public int getDamagePerBall() {
        return this.getLeftCannon().getDamagePerBall(); // Symmetric between both cannons.
    }

    public float getCannonRange() {
        return this.getLeftCannon().getRange(); // Symmetric between both cannons.
    }

    public int getCannonCoolDown() {
        return this.getLeftCannon().getCoolDownTime();
    }


}
