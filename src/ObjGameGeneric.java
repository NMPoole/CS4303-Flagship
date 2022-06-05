import processing.core.PVector;

import static processing.core.PApplet.*;

class ObjGameGeneric {


    // ########################################################################
    // Generic Game Object Attributes:
    // ########################################################################

    public GameState gameState; // Instance of game state, required as though a global variable.

    private PVector position; // All game objects have a position.
    private PVector velocity; // All game objects have a velocity.
    private PVector accel; // Al, game objects have an acceleration.

    private float maxSpeed; // Max speed of this object.

    private int diameter; // All game objects have an associated size (i.e., diameter).
    private float orientation; // Orientation of this object relative to up.
    private float rotation; // Current rotation of this object applied per frame.

    private int colour; // All game objects have a colour (for displaying simple geometry).

    // Object De-Spawning.
    private boolean willDeSpawn; // Boolean determining whether this object will de-spawn after so many seconds off the screen.
    private int deSpawnTimer; // Timer counting how long this object has been off the screen to determine whether to de-spawn.


    // ########################################################################
    // Generic Game Object Constructors:
    // ########################################################################

    ObjGameGeneric(float posXInit, float posYInit, float maxSpeed, int radius, int colour, boolean willDeSpawn, GameState gameState) {

        this.gameState = gameState;

        // Initially Stationary Object Constructor:

        this.position = new PVector(posXInit, posYInit); // Position initialisation.
        this.velocity = new PVector(0, 0); // No velocity for this object.
        this.accel = new PVector(0, 0); // No acceleration for this object.

        this.maxSpeed = maxSpeed; // Set maximum speed of this object.

        this.diameter = radius; // Size of object.
        this.orientation = 0; // Default pointing up.
        this.rotation = 0; // Default not steering.

        this.colour = colour; // Colour of object.

        this.willDeSpawn = willDeSpawn; // Object de-spawning.

    }


    // ########################################################################
    // Generic Game Object Update Methods:
    // ########################################################################

    boolean update() {

        // Apply velocity (with limit):
        this.velocity.add(this.accel);
        //this.velocity.limit(this.maxSpeed);

        // Update position based on velocity.
        this.position.add(this.velocity);

        // Acceleration is spent.
        this.accel.mult(0);

        if (willDeSpawn) return this.updateDeSpawn();
        else return false; // Object cannot de-spawn, so return false.

    }

    public void updatePan(float camXPan, float camYPan) {
        this.setPosX(this.getPosX() - camXPan);
        this.setPosY(this.getPosY() - camYPan);
    }

    public boolean updateDeSpawn() {

        // Update the de-spawn timer if this object is out of the map.
        if (!this.isInMap()) this.deSpawnTimer += 1;
        else this.deSpawnTimer = 0;

        return (this.deSpawnTimer >= GameConfig.DESPAWN_TIMER); // Checking if object should be de-spawned.

    }

    public void applyForce(PVector force) {
        this.accel.add(force);
    }


    // ########################################################################
    // Generic Game Object Collision Methods:
    // ########################################################################

    public boolean collide(ObjGameGeneric other) {

        // Calculate Pythagorean distance between object positions.
        float distance = PVector.dist(this.getPos(), other.getPos());

        // Calculate minimum distance between objects to be touching.
        // Size is the diameter, so ensure to convert to radius.
        float minDist = ((float) other.getDiameter() / 2) + ((float) this.getDiameter() / 2);

        // If the distance is <= the minimum distance for touching, then they are intersecting.
        return (distance <= minDist);

    }

    public void bounce(ObjGameGeneric other) {

        // Calculate difference in x and y co-ordinates.
        float dx = other.getPosX() - this.getPosX();
        float dy = other.getPosY() - this.getPosY();

        // Calculate Pythagorean distance between object positions.
        // Don't use dist(); need the dx and dy values.
        float distance = sqrt(dx * dx + dy * dy);

        // Calculate minimum distance between objects to be touching.
        // Size is the diameter, so ensure to convert to radius.
        float minDist = ((float) other.getDiameter() / 2) + ((float) this.getDiameter() / 2);

        // If the distance is <= the minimum distance for touching, then they are intersecting.
        if (distance <= minDist) {

            // If objects intersecting, then calculate their 'bounce' velocities:

            float angle = atan2(dy, dx); // Angle between the two objects in radians.

            float targetX = this.getPosX() + (cos(angle) * minDist);
            float targetY = this.getPosY() + (sin(angle) * minDist);

            float spring = GameConfig.SPRING_COEFF; // 'Springiness' of bounce.
            float ax = (targetX - other.getPosX()) * spring;
            float ay = (targetY - other.getPosY()) * spring;

            // Adjust object velocities to bounce off each other.
            this.velocity.x -= ax;
            this.velocity.y -= ay;
            other.velocity.x += ax;
            other.velocity.y += ay;

        }

    }


    // ########################################################################
    // Generic Game Object Utility:
    // ########################################################################

    public boolean isOnScreen() {

        return !(this.getPosX() < 0 || this.getPosX() > GameConfig.CANVAS_WIDTH ||
                 this.getPosY() < 0 || this.getPosY() > GameConfig.CANVAS_HEIGHT);

    }

    public boolean isInMap() {

        int[] mapGridCoord = gameState.getMap().getMapGridCoordAtPos((int) this.getPosX(), (int) this.getPosY());

        return  !(mapGridCoord[0] < 0 || mapGridCoord[0] >= gameState.getMap().getMapGridWidth() ||
                  mapGridCoord[1] < 0 || mapGridCoord[1] >= gameState.getMap().getMapGridHeight());

    }


    // ########################################################################
    // Generic Game Object Getters:
    // ########################################################################

    // POSITION:

    public PVector getPos() {
        return this.position;
    }

    public float getPosX() {
        return this.position.x;
    }

    public float getPosY() {
        return this.position.y;
    }

    // VELOCITY:

    public PVector getVel() {
        return this.velocity;
    }

    public float getVelX() {
        return this.velocity.x;
    }

    public float getVelY() {
        return this.velocity.y;
    }

    // ACCELERATION:

    public PVector getAccel() {
        return accel;
    }

    public float getAccelX() {
        return this.accel.x;
    }

    public float getAccelY() {
        return this.accel.y;
    }


    // MAX SPEED:

    public float getMaxSpeed() {
        return maxSpeed;
    }

    // RADIUS:

    public int getDiameter() {
        return this.diameter;
    }

    // ORIENTATION:

    public float getOri() {
        return orientation;
    }

    // ROTATION:

    public float getRot() {
        return rotation;
    }

    // COLOUR:

    public int getCol() {
        return this.colour;
    }

    // DE-SPAWN:

    public boolean isWillDeSpawn() {
        return willDeSpawn;
    }


    // ########################################################################
    // Generic Game Object Setters:
    // ########################################################################

    // POSITION:

    public void setPos(PVector position) {
        this.position = position;
    }

    public void setPosX(float x) {
        this.position.x = x;
    }

    public void setPosY(float y) {
        this.position.y = y;
    }

    // VELOCITY:

    public void setVel(PVector velocity) {
        this.velocity = velocity;
    }

    public void setVelX(float x) {
        this.velocity.x = x;
    }

    public void setVelY(float y) {
        this.velocity.y = y;
    }

    // ACCELERATION:

    public void setAccel(PVector accel) {
        this.accel = accel;
    }

    public void setAccelX(float x) {
        this.accel.x = x;
    }

    public void setAccelY(float y) {
        this.accel.y = y;
    }

    // MAX SPEED:

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    // RADIUS:

    public void setDiameter(int diameter) {
        this.diameter = diameter;
    }

    // ORIENTATION:

    public void setOri(float orientation) {
        this.orientation = orientation;
    }

    // ROTATION:

    public void setRot(float rotation) {
        this.rotation = rotation;
    }

    // COLOUR:

    public void setCol(int colour) {
        this.colour = colour;
    }

    // DE-SPAWN:

    public void setWillDeSpawn(boolean willDeSpawn) {
        this.willDeSpawn = willDeSpawn;
    }


}
