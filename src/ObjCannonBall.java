import processing.core.PVector;

import java.util.ArrayList;

public class ObjCannonBall extends ObjGameGeneric {


    // ########################################################################
    // Cannon Ball Attributes:
    // ########################################################################

    private final int damage; // Damage this cannonball will deal.
    private final Character firedBy; // Who fired this cannonball.


    // ########################################################################
    // Cannon Ball Constructor:
    // ########################################################################

    public ObjCannonBall(float posXInit, float posYInit, float maxSpeed, int diameter, int colour, int damage,
                         Character firedBy, boolean willDeSpawn, GameState gamestate) {

        // Game Object Constructor; cannons will move with a ship or are stationary as part of a fort.
        super(posXInit, posYInit, maxSpeed, diameter, colour, willDeSpawn, gamestate);

        // Cannon Ball Attribute Instantiation:
        this.damage = damage;
        this.firedBy = firedBy;

    }


    // ########################################################################
    // Cannon Ball Update Methods:
    // ########################################################################

    public boolean update() {

        this.applyDrag(); // Cannonballs are effected by drag.

        boolean removeDeSpawn = super.update(); // Update this cannonball as an instance of ObjGameGeneric.

        // If current speed is below a threshold, then remove this cannonball.
        boolean removeTooSlow = (this.getVel().mag() < GameConfig.CANNONBALL_MIN_SPEED);

        boolean removeHit = this.collisionResolution(); // Cannonball hit detection.

        return (removeTooSlow || removeHit || removeDeSpawn);

    }

    private void applyDrag() {

        PVector drag = this.getVel().copy();
        drag.normalize();

        float speed = this.getVel().mag();

        drag.mult(GameConfig.WIND_DRAG_COEFF * speed);

        this.applyForce(drag);

    }

    private boolean collisionResolution() {

        // Get set of nearby objects to this cannonball using the bin-lattice spatial sub-division.
        ArrayList<? extends ObjGameGeneric> nearbyObjects = gameState.getCollisionLattice().nearbyObjects(this);

        boolean removeThisCannonBall = false; // Only need to remove if there is a collision.

        for (ObjGameGeneric currNearbyObj : nearbyObjects) { // For all nearby objects...
            if (currNearbyObj instanceof Character) { // ...If the nearby object is a character...

                if (currNearbyObj == firedBy) continue; // Do not need to consider collisions with the character that fired the cannonball.

                // For the fort mini-boss, don't allow friendly fire!
                if (firedBy instanceof CharacterFortBoss && currNearbyObj instanceof CharacterFort) continue;
                if (firedBy instanceof CharacterFort && currNearbyObj instanceof CharacterFortBoss) continue;

                if (this.collide(currNearbyObj)) { // ...And this cannonball is colliding with the character...

                    ((Character) currNearbyObj).subHealth(this.getDamage()); // ...Then damage the character.
                    removeThisCannonBall = true; // Collided so remove cannonball.

                    Game.gameSound.soundImpactThud(true); // Play impact indicator sound as feedback.

                }

            }
        }

        return removeThisCannonBall;

    }


    // ########################################################################
    // Cannon Ball Render/Draw Methods:
    // ########################################################################

    public void display() {

        // Cannonball is displayed as a simple round ellipse.
        Game.sketch.fill(this.getCol());
        Game.sketch.stroke(0);
        Game.sketch.ellipse(this.getPosX(), this.getPosY(), this.getDiameter(), this.getDiameter());

    }

    // ########################################################################
    // Cannon Ball Getters/Setters Methods:
    // ########################################################################

    public int getDamage() {
        return damage;
    }


}
