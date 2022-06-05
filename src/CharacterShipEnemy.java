import processing.core.PVector;

public class CharacterShipEnemy extends CharacterShip {


    // ########################################################################
    // Ship Enemy Attributes:
    // ########################################################################

    // No Additional Attributes Required.


    // ########################################################################
    // Ship Enemy Constructors:
    // ########################################################################

    CharacterShipEnemy(float startX, float startY, float maxSpeed, int diam, int color, int health, boolean willDespawn,
                        int cannonNumVolleys, int cannonNumBallsPerVolley, float cannonRange, int cannonCoolDown,
                        int damagePerBall, GameState gameState) {

        // Initialise with CharacterShip class constructor.
        super(startX, startY, maxSpeed, diam, color, health, willDespawn,
                cannonNumVolleys, cannonNumBallsPerVolley, cannonRange, cannonCoolDown, damagePerBall,
                gameState);

    }


    // ########################################################################
    // Ship Enemy Update Methods:
    // ########################################################################

    public boolean update() {

        // Guard Condition: If this character is not in the map, then just head straight for the player to be relevant.
        //                  Only follow the decision tree behaviour if in the map. Hopefully, becomes relevant before de-spawn.
        if (!this.isInMap()) {
            PVector steer = super.pursue(gameState.getPlayer());
            steer = this.applyWindForceScaling(steer); // Scale the applied behaviour in accordance with sail and wind alignment.
            super.applyForce(steer);
            boolean remove = super.update();
            return remove;
        }

        // Decision Tree:
        //      If aware of player and player in range, then shoot the player.
        //      If aware of player and player not in range, then pursue the player.
        //      Otherwise, wander.

        Character player = super.awareOfPlayer();

        PVector steer = new PVector(0, 0);
        if (player != null) { // The ship is aware of the player.

            if (super.isTargetInRange(gameState.getPlayer().getPos())) { // If target in range, then aim cannons at player and fire.

                super.updateCannonsAim(gameState.getPlayer().getPos()); // Have enemy ship's cannons point at player target.
                super.fireCannons(gameState.getPlayer().getPos()); // Fire at the player.

            } else { // Otherwise, pursue the player.

                steer = super.pursue(player);

            }

        } else { // The ship is not aware of the player and the player is not in range.

            steer = super.wander(GameConfig.SHIP_WANDER_RAND_FACT);

        }

        steer = this.applyWindForceScaling(steer); // Scale the applied behaviour in accordance with sail and wind alignment.
        super.applyForce(steer); // Apply steering force based on the behaviour enacted by the decision tree.

        // Update sail alignment - ship should not be able to adjust the sail immediately to a desired direction.
        // Always aim to point the sail with the wind.
        int dirToTurn = this.dirToTurnOfTwoAngles(this.getSailNormAngle(), gameState.getWindDirAngle());
        super.setSailNormAngle(this.getSailNormAngle() + (GameConfig.SHIP_SAIL_TURN_SPEED * dirToTurn));

        boolean remove = super.update(); // Update this enemy ship's movement as an instance of a CharacterShip.
        return remove; // Whether this object should be removed (i.e., de-spawning).

    }


}
