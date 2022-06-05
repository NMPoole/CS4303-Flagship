import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

import static processing.core.PApplet.*;

public class Character extends ObjGameGeneric {


    // ########################################################################
    // Character Attributes:
    // ########################################################################

    private final GameConfig.CharType charType; // Type of this character; either land or water.

    private int baseHealth; // Base (i.e., max) health of this character.
    private float currHealth; // Current health of this character (between 0 and base health).

    private int arriveRadius; // Arrival radius for this character - when within such distance to target slow to stop.
    private int awareRadius; // Radius of awareness of this character for enacting behaviour.


    // ########################################################################
    // Character Constructors:
    // ########################################################################

    public Character(GameConfig.CharType characterType, float posXInit, float posYInit, float maxSpeed, int diameter,
                     int colour, int health, boolean willDeSpawn, GameState gamestate) {

        // Character constructor.
        super(posXInit, posYInit, maxSpeed, diameter, colour, willDeSpawn, gamestate);

        // Type of this character - land or water.
        this.charType = characterType;

        // Health Instantiation.
        this.baseHealth = health;
        this.currHealth = health;

        // Awareness Radii.
        this.arriveRadius = this.getDiameter() * GameConfig.CHAR_ARRIVE_MULT;
        this.awareRadius = this.getDiameter() * GameConfig.CHAR_AWARE_MULT;

    }


    // ########################################################################
    // Character Update Methods:
    // ########################################################################

    boolean update() {

        // Characters need to avoid land so override update function instead of call to super:

        this.getVel().add(this.getAccel()); // Alter velocity by acceleration.
        this.avoidTerrain(); // Enact terrain type avoidance.
        this.getPos().add(this.getVel()); // Alter position by velocity.
        this.getAccel().mult(0); // Acceleration is used up.
        this.confineMovement(); // Also, confine the character's movement.

        boolean removeDeSpawn = false; // Object said to not be able to de-spawn by default.
        if (this.isWillDeSpawn()) removeDeSpawn = super.updateDeSpawn(); // Update based on objects de-spawn settings.

        boolean removeDead = !(this.isAlive()); // Indicate to remove this character on the basis that it is dead.

        return (removeDeSpawn || removeDead); // Whether this object should be removed (i.e., de-spawning or death).

    }


    // ########################################################################
    // Character Render Methods:
    // ########################################################################

    public void display() {

        this.displayHealth(); // All characters should display their health.

        if (Game.showMechanics) {
            this.displayAwareness(); // Show awareness when specified to do so.
            this.displayInfo(); // Display info about this character.
        }

    }

    public void displayInfo() {

        if (!this.isOnScreen()) return; // Objects off the screen should not show details - we will not see them.

        // Display the speed, co-ordinates, and map grid position of the ship.
        int[] mapGridPos = gameState.getMap().getMapGridCoordAtPos((int) this.getPosX(), (int) this.getPosY());
        Game.sketch.fill(GameConfig.TITLE_TEXT_COLOR);
        Game.sketch.textSize((float) GameConfig.TITLE_TEXT_SIZE / 2);

        Game.sketch.textAlign(CENTER);
        Game.sketch.text("Speed: " + this.getVel().mag(), this.getPosX(), this.getPosY() - 50);
        Game.sketch.text("PosX: " + this.getPosX() + ", PosY: " + this.getPosY(), this.getPosX(), this.getPosY() - 40);
        Game.sketch.text("MapGridCol: " + mapGridPos[0] + ", MapGridRow: " + mapGridPos[1], this.getPosX(), this.getPosY() - 30);

        GameConfig.Terrain terrainAtMouse = gameState.getMap().getTerrainAtPos((int) this.getPosX(), (int) this.getPosY());
        Game.sketch.text(String.valueOf(terrainAtMouse), this.getPosX(), this.getPosY() - 20);

    }


    // ########################################################################
    // Character Movement/Steering Methods:
    // ########################################################################

    PVector flock(ArrayList<? extends ObjGameGeneric> targets) {

        // Flock - Apply alignment, cohesion, and separation for group behaviour.

        // Apply each of the three flocking behaviours.
        PVector separateForce = this.separate(targets);
        PVector alignForce = this.align(targets);
        PVector cohesionForce = this.cohesion(targets);

        // Weighting of these behaviours.
        separateForce.mult(GameConfig.CHAR_FLOCK_SEP_WEIGHT);
        alignForce.mult(GameConfig.CHAR_FLOCK_ALI_WEIGHT);
        cohesionForce.mult(GameConfig.CHAR_FLOCK_COH_WEIGHT);

        // Create vector summing the forces given by the three behaviours.
        PVector resultForce = new PVector(0, 0);
        resultForce.add(separateForce);
        resultForce.add(alignForce);
        resultForce.add(cohesionForce);

        return resultForce;

    }

    PVector align(ArrayList<? extends ObjGameGeneric> targets) {

        // Alignment (or Copy) - Steer in the same direction as neighbours.

        float desiredDist = this.getDiameter() * GameConfig.CHAR_FLOCK_MAX_MULT;

        PVector sum = new PVector(0, 0);
        int count = 0;

        for (ObjGameGeneric currTarget : targets) {

            float d = PVector.dist(this.getPos(), currTarget.getPos());

            if (d > 0 && d < desiredDist) {

                sum.add(currTarget.getVel());
                count += 1; // For an average, keep track of number of boids within the distance.

            }

        }

        // If there is at least one target not close enough, then apply alignment.
        if (count > 0) {

            sum.div(count);
            sum.normalize();
            sum.mult(this.getMaxSpeed());

            PVector steer = PVector.sub(sum, this.getVel());
            return steer;

        }

        // Otherwise, no alignment needs to be applied. Return zero vector.
        return new PVector(0, 0);

    }

    PVector cohesion(ArrayList<? extends ObjGameGeneric> targets) {

        // Cohesion (or Center) - Steer towards center of neighbours (stay with the group).

        float desiredDist = this.getDiameter() * GameConfig.CHAR_FLOCK_MAX_MULT;

        PVector sum = new PVector(0, 0);
        int count = 0;

        for (ObjGameGeneric currTarget : targets) {

            float d = PVector.dist(this.getPos(), currTarget.getPos());

            if (d > 0 && d < desiredDist) {

                sum.add(currTarget.getPos());
                count += 1; // For an average, keep track of number of boids within the distance.

            }

        }

        // If there is at least one target within the distance, then apply cohesion.
        if (count > 0) {

            sum.div(count);
            return this.seek(sum, GameConfig.SEEK_TYPE.NORM); // Target sought is average location of neighbours.

        }

        // Otherwise, no alignment needs to be applied. Return zero vector.
        return new PVector(0, 0);

    }

    PVector separate(ArrayList<? extends ObjGameGeneric> targets) {

        // Separation (or Avoidance) - Steer to avoid colliding with nearby neighbours.

        // Desired separation is based on the size of this character.
        float desiredSeparation = this.getDiameter() * GameConfig.CHAR_FLOCK_MIN_MULT;

        PVector sum = new PVector();
        int count = 0;

        for (ObjGameGeneric currTarget : targets) { // Keep track of how many of the targets are too close.

            float d = PVector.dist(this.getPos(), currTarget.getPos());

            if (d > 0 && d < desiredSeparation) {

                PVector diff = PVector.sub(this.getPos(), currTarget.getPos());
                diff.normalize();
                diff.div(d); // The closer the target to separate from, the greater the separation force, and vice versa.

                sum.add(diff);
                count += 1;

            }

        }

        // If there is at least one target too close, then apply separation.
        if (count > 0) {

            sum.div(count);
            sum.normalize();
            sum.mult(this.getMaxSpeed());

            PVector steer = PVector.sub(sum, this.getVel());
            return steer;

        }

        // Otherwise, no separation needs to be applied. Return zero vector.
        return new PVector(0, 0);

    }

    PVector seek(PVector target, GameConfig.SEEK_TYPE seekType) {

        PVector force = PVector.sub(target, this.getPos());
        float desiredSpeed = this.getMaxSpeed();

        float d = force.mag();
        if (seekType == GameConfig.SEEK_TYPE.ARRIVE) {
            if (d < this.arriveRadius) desiredSpeed = PApplet.map(d, 0, arriveRadius, 0, this.getMaxSpeed());
        } else if (seekType == GameConfig.SEEK_TYPE.LUNGE) {
            if (d < this.arriveRadius) desiredSpeed = PApplet.map(d, 0, arriveRadius,
                    this.getMaxSpeed() * GameConfig.CHAR_LUNGE_MULT, this.getVel().mag());
        }

        force.setMag(desiredSpeed);
        force.sub(this.getVel());

        return force;

    }

    PVector flee(PVector target) {
        return this.seek(target, GameConfig.SEEK_TYPE.NORM).mult(-1);
    }

    PVector arrive(PVector target) {
        return this.seek(target, GameConfig.SEEK_TYPE.ARRIVE);
    }

    PVector pursue(ObjGameGeneric target) {

        // Get current position of the target.
        PVector targetPosCopy = target.getPos().copy();

        // Get fixed-distance future position of target, which assumes velocity but accounts for when stationary.
        PVector prediction = PVector.fromAngle(target.getOri());
        prediction.mult(GameConfig.CHAR_PRED_MULT);
        targetPosCopy.add(prediction);

        // DEPRECATED - Above prediction gives better behaviour.
        // Get future position of target assuming current velocity.
        //PVector prediction = target.getVel().copy();
        //prediction.mult(GameConfig.CHAR_PRED_MULT);
        //targetPosCopy.add(prediction);

        // Seek the predicted position.
        return this.seek(targetPosCopy, GameConfig.SEEK_TYPE.NORM);

    }

    PVector evade(ObjGameGeneric target) {

        PVector pursuit = this.pursue(target);
        pursuit.mult(-1);
        return pursuit;

    }

    PVector wander(float wanderRand) {

        // Randomly update orientation a little.
        float updatedOri = this.getOri() + Game.sketch.random(0, wanderRand) - Game.sketch.random(0, wanderRand);

        // Keep in bounds.
        if (updatedOri > PI) updatedOri -= 2 * PI;
        else if (updatedOri < -PI) updatedOri += 2 * PI;

        // Calculate vector in the randomly chosen direction.
        PVector vectorRandDir = PVector.fromAngle(updatedOri);
        vectorRandDir.normalize();
        vectorRandDir.mult(this.getMaxSpeed());

        return vectorRandDir;

    }


    // ########################################################################
    // Character Movement Confining Methods:
    // ########################################################################

    private void confineMovement() {

        if (!this.isInMap()) return; // Objects out the map get a free-pass at following the rules, so they become relevant and do not get stuck.

        // Confine such that cannot hit land or water - depending on character type!
        // If land character, need to restrict from entering water, and vice versa.
        boolean restrictFromLand = !this.charType.equals(GameConfig.CharType.LAND);

        if (abs(this.getVelX()) > abs(this.getVelY())) { // Crude resolution of player being able to move diagonally.

            float blockX;

            if (this.getVelX() < 0) { // Collision left of character with wall.
                blockX = gameState.getMap().collidesXLeft(this, restrictFromLand);
                if (blockX != -1) {
                    this.setPosX(blockX);
                    this.setVel(this.getVel().mult(-1)); // Will work for most characters - player and flagship excluded.
                }
            } else if (this.getVelX() > 0) { // Collision right of character with wall.
                blockX = gameState.getMap().collidesXRight(this, restrictFromLand);
                if (blockX != -1) {
                    this.setPosX(blockX);
                    this.setVel(this.getVel().mult(-1)); // Will work for most characters - player and flagship excluded.
                }
            }

        } else {

            float blockY;

            if (this.getVelY() < 0) { // Collision up of character with wall.
                blockY = gameState.getMap().collidesYUp(this, restrictFromLand);
                if (blockY != -1) {
                    this.setPosY(blockY);
                    this.setVel(this.getVel().mult(-1)); // Will work for most characters - player and flagship excluded.
                }
            } else if (this.getVelY() > 0) { // Collision down of character with wall.
                blockY = gameState.getMap().collidesYDown(this, restrictFromLand);
                if (blockY != -1) {
                    this.setPosY(blockY);
                    this.setVel(this.getVel().mult(-1)); // Will work for most characters - player and flagship excluded.
                }
            }

        }

    }

    public void avoidTerrain() {

        if (!this.isInMap()) return; // Objects out the map get a free-pass at following the rules, so they become relevant and do not get stuck.
        if (this.getVel().mag() <= 0.001) return; // No need to enact turning to avoid terrain on stationary abjects.

        // Determine type of terrain this character is to avoid.
        GameConfig.Terrain terrainToAvoid;
        if (this.charType == GameConfig.CharType.LAND) terrainToAvoid = GameConfig.Terrain.SHALLOW_WATER;
        else terrainToAvoid = GameConfig.Terrain.SAND;

        // Cast a ray out in front of the character to determine if heading towards land.
        PVector straightRay = this.getVel().copy();
        int numHitsStraight = this.samplesAlongRayTerrainHit(terrainToAvoid, straightRay,
                GameConfig.CHAR_AVOID_TER_NUM_SAMPLES / 4); // NOTE: Less samples - simply need to discern if turning.

        // If there is terrain to avoid along the ray, then decide which direction to move in (left or right).
        if (numHitsStraight > 0) {

            float theta = GameConfig.CHAR_AVOID_TER_RAY_ANG;

            // Cast a ray theta degrees to the left and right of the ship and determine extent of terrain to dodge in either direction.

            PVector rightRay = new PVector();
            rightRay.x = (straightRay.x * cos(theta)) - (straightRay.y * sin(theta));
            rightRay.y = (straightRay.x * sin(theta)) + (straightRay.y * cos(theta));
            int numHitsR = this.samplesAlongRayTerrainHit(terrainToAvoid, rightRay, GameConfig.CHAR_AVOID_TER_NUM_SAMPLES);

            PVector leftRay = this.getAccel().copy();
            leftRay.x = (straightRay.x * cos(-theta)) - (straightRay.y * sin(-theta));
            leftRay.y = (straightRay.x * sin(-theta)) + (straightRay.y * cos(-theta));
            int numHitsL = this.samplesAlongRayTerrainHit(terrainToAvoid, leftRay, GameConfig.CHAR_AVOID_TER_NUM_SAMPLES);

            // If there is more terrain to dodge on the left than the right, then turn towards the right, and vice versa.
            if (numHitsL >= numHitsR) this.getVel().rotate(GameConfig.CHAR_AVOID_TER_TURN_ANG);
            else this.getVel().rotate(-GameConfig.CHAR_AVOID_TER_TURN_ANG);

        }

    }

    private int samplesAlongRayTerrainHit(GameConfig.Terrain terrainToAvoid, PVector ray, int numSamples) {

        int terrainHitCnt = 0;

        // For the given ray, take samples along the cast ray to determine if any of the terrain to dodge is along the ray.
        for (int i = 0; i < numSamples; i++) {

            int rayOffset = i * ((int) (this.getAwareRadius() * GameConfig.CHAR_AVOID_TER_AWARE_MULT) / numSamples);

            // Create position to check terrain of using the ray and sample number.
            PVector toAddToPos = ray.copy().setMag(rayOffset);
            PVector posToCheck = this.getPos().copy().add(toAddToPos);
            GameConfig.Terrain terrainAtPos = gameState.getMap().getTerrainAtPos((int) posToCheck.x, (int) posToCheck.y);

            // Count terrain to dodge if that terrain exists at the sample point in the map.
            if (terrainAtPos != null && terrainAtPos == terrainToAvoid) terrainHitCnt += 1;

        }

        // Return count of how many terrain tiles there are to dodge along the cast ray for the given number of samples.
        return terrainHitCnt;

    }


    // ########################################################################
    // Character Awareness Methods:
    // ########################################################################

    Character awareOfPlayer() {

        // Get player of the game.
        CharacterShipPlayer player = gameState.getPlayer();

        // Calculate distance to player, and required distance to be aware of this player.
        float distToPlayer = PVector.dist(this.getPos(), player.getPos());
        float minDist = (float) (player.getDiameter() / 2) + this.awareRadius;

        // If aware of the player, return the player object, else return null.
        if (distToPlayer < minDist) return player;
        else return null;

    }


    // ########################################################################
    // Character - Type:
    // ########################################################################

    public GameConfig.CharType getCharType() {
        return charType;
    }


    // ########################################################################
    // Character - Health:
    // ########################################################################

    public void upgradeHealth(int healthToAdd) {
        this.baseHealth += healthToAdd;
        this.currHealth += healthToAdd;
    }

    public void replenishHealth() {
        this.currHealth = this.baseHealth;
    }

    public void addHealth(int healthToAdd) {
        this.currHealth = min(this.currHealth + healthToAdd, this.baseHealth);
    }

    public void subHealth(float healthToTake) {
        this.currHealth = max(this.currHealth - healthToTake, 0);
    }

    void displayHealth() {

        int baseHealthBarLength = this.baseHealth / GameConfig.CHAR_HEALTH_PER_PIX;
        int currHealthBarLength = (int) this.currHealth / GameConfig.CHAR_HEALTH_PER_PIX;

        int healthBarStartX = (int) this.getPosX() - (baseHealthBarLength / 2);
        int healthBarStartY = (int) (this.getPosY() + this.getDiameter() + 5);

        // Health bar is a background bar representing the total length with a foreground bar representing curr health.
        Game.sketch.fill(GameConfig.CHAR_HEALTH_COL_BACK);
        Game.sketch.stroke(GameConfig.CHAR_HEALTH_COL_BACK);
        Game.sketch.rect(healthBarStartX, healthBarStartY, baseHealthBarLength, 5);
        Game.sketch.fill(GameConfig.CHAR_HEALTH_COL_FRONT);
        Game.sketch.rect(healthBarStartX, healthBarStartY, currHealthBarLength, 5);

    }

    public boolean isAlive() {
        return (this.currHealth > 0);
    }

    public float getCurrHealth() {
        return this.currHealth;
    }

    public int getBaseHealth() {
        return baseHealth;
    }

    // ########################################################################
    // Character - Awareness:
    // ########################################################################

    private void displayAwareness() {

        Game.sketch.noFill();
        Game.sketch.stroke(this.getCol());

        Game.sketch.ellipse(this.getPosX(), this.getPosY(), this.getAwareRadius() * 2, this.getAwareRadius() * 2);

    }

    public void setAwareRadius(int awareRadius) {
        this.awareRadius = awareRadius;
    }

    public int getAwareRadius() {
        return awareRadius;
    }



}
