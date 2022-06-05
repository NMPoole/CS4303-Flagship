import processing.core.PVector;

public class CharacterShipBoss extends CharacterShip {


    // ########################################################################
    // Ship Enemy Boss Attributes:
    // ########################################################################

    // No Additional Attributes Required.


    // ########################################################################
    // Ship Enemy Boss Constructors:
    // ########################################################################

    CharacterShipBoss(float startX, float startY, float maxSpeed, int diam, int color, int health,
                       int cannonNumVolleys, int cannonNumBallsPerVolley, float cannonRange, int cannonCoolDown,
                       int damagePerBall, GameState gameState) {

        // Initialise with CharacterShip class constructor.
        super(startX, startY, maxSpeed, diam, color, health, false,
                cannonNumVolleys, cannonNumBallsPerVolley, cannonRange, cannonCoolDown, damagePerBall,
                gameState);

    }


    // ########################################################################
    // Ship Enemy Boss Update Methods:
    // ########################################################################

    public boolean update() {

        // Decision Tree:
        //      If the player is in range, shoot at the player.
        //      Otherwise, pursue the player.

        if (super.isTargetInRange(gameState.getPlayer().getPos())) { // If target in range, then aim cannons at player and fire.

            super.updateCannonsAim(gameState.getPlayer().getPos()); // Have enemy ship's cannons point at player target.
            super.fireCannons(gameState.getPlayer().getPos()); // Fire at the player.

        } else { // Otherwise, pursue the player.

            PVector steer = super.pursue(gameState.getPlayer());
            steer = this.applyWindForceScaling(steer); // Scale the applied behaviour in accordance with sail and wind alignment.
            super.applyForce(steer); // Apply steering force based on the behaviour enacted by the decision tree.

        }

        // Update sail alignment - ship should not be able to adjust the sail immediately to a desired direction.
        int dirToTurn = this.dirToTurnOfTwoAngles(this.getSailNormAngle(), gameState.getWindDirAngle());
        super.setSailNormAngle(this.getSailNormAngle() + (GameConfig.SHIP_SAIL_TURN_SPEED * dirToTurn)); // Always point sail with the wind.

        boolean remove = super.update(); // Update this enemy ship's movement as an instance of a CharacterShip.
        return remove; // The final boss cannot be de-spawned.

    }


}
