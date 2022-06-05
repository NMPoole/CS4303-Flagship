import processing.core.PVector;

import java.util.ArrayList;

import static processing.core.PConstants.HALF_PI;

public class CharacterShark extends Character {


    // ########################################################################
    // Shark Attributes:
    // ########################################################################

    private final float damage; // Damage this cannonball will deal.


    // ########################################################################
    // Shark Constructors:
    // ########################################################################

    CharacterShark(float startX, float startY, float maxSpeed, int diam, int color, int health, boolean willDeSpawn,
                   float damage, GameState gameState) {

        // Initialise with Character class constructor.
        super(GameConfig.CharType.WATER, startX, startY, maxSpeed, diam, color, health, willDeSpawn, gameState);

        // Shark Attribute Initialisation:
        this.damage = damage;

    }


    // ########################################################################
    // Shark Update Methods:
    // ########################################################################

    public boolean update() {

        // Guard Condition: If this character is not in the map, then just head straight for the player to be relevant.
        //                  Only follow the decision tree behaviour if in the map. Hopefully, becomes relevant before de-spawn.
        if (!this.isInMap()) {

            PVector steer = super.pursue(gameState.getPlayer());
            super.applyForce(steer);

            boolean remove = super.update();
            return remove;

        }

        // Decision Tree:
        //      If aware of player, then seek and lunge the player.
        //      If unaware of player, then wander.
        //      Regardless, act as a flock with other sharks.

        Character player = super.awareOfPlayer();
        PVector steer;
        if (player != null) steer = super.seek(player.getPos(), GameConfig.SEEK_TYPE.LUNGE); // Seek and lunge the player.
        else steer = super.wander(GameConfig.SHARK_WANDER_RAND_FACT); // The shark is not aware of the player, so wander.
        steer.mult(GameConfig.SHARK_FORCE_MULT); // Scale applied forces for sharks.
        super.applyForce(steer); // Apply steering force based on the behaviour enacted by the decision tree.

        // Also, ensure a flocking behaviour with other sharks is maintained.
        ArrayList<? extends  ObjGameGeneric> nearbyObjects = gameState.getCollisionLattice().nearbyObjects(this);
        nearbyObjects.removeIf(currGameObject -> (!(currGameObject instanceof CharacterShark))); // Remove non-shark objects nearby.
        PVector flock = this.flock(nearbyObjects); // Calculate flocking force.
        flock.mult(GameConfig.SHARK_FORCE_MULT); // Scale applied forces for sharks.
        super.applyForce(flock);

        this.applyDragWaterForce(); // Apply water drag force to the shark speed.

        boolean remove = super.update(); // Update this enemy shark's movement as an instance of a Character.

        this.setOri(this.getVel().heading()); // Update orientation of the shark to direction of motion.
        this.collisionResolution(); // Apply collision resolution for this shark.
        return remove; // Whether this object should be removed (i.e., de-spawning or death).

    }

    private void collisionResolution() {

        // Only worry about collisions with the player, so collision resolution is very simple.
        if (this.collide(gameState.getPlayer())) {
            Game.gameSound.soundSharkAttack(true);
            gameState.getPlayer().subHealth(this.damage);
        }

    }

    private void applyDragWaterForce() {

        PVector drag = this.getVel().copy();
        drag.normalize();

        float speed = this.getVel().mag();
        drag.mult(GameConfig.WATER_DRAG_COEFF * speed);

        this.applyForce(drag);

    }


    // ########################################################################
    // Shark Render/Draw Methods:
    // ########################################################################

    public void display() {

        this.displaySharkGeom(); // Display the shark.

        super.display(); // Display attributes all characters should display (e.g., health).

    }

    private void displaySharkGeom() {

        // Calculate and apply rotation of model to point at aim.

        Game.sketch.translate(this.getPosX(), this.getPosY()); // Translate co-ordinate origin to position.
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



}
