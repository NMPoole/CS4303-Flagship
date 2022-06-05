import processing.core.PConstants;
import processing.core.PVector;

public class CharacterShipPlayer extends CharacterShip {


    // ########################################################################
    // Player Attributes:
    // ########################################################################

    private PVector pointToSeek; // Point to seek, which is where the player clicked to apply movement.
    private int gold; // Amount of gold the player has collected.
    private int totalDeaths; // Total deaths of the player.


    // ########################################################################
    // Player Constructors:
    // ########################################################################

    CharacterShipPlayer(float startX, float startY, float maxSpeed, int playerDiam, int playerColor, int health,
                        int cannonNumVolleys, int cannonNumBallsPerVolley, float cannonRange, int cannonCoolDown,
                        int damagePerBall, GameState gameState) {

        // Initialise with CharacterShip class constructor.
        super(startX, startY, maxSpeed, playerDiam, playerColor, health, false,
                cannonNumVolleys, cannonNumBallsPerVolley, cannonRange, cannonCoolDown, damagePerBall,
                gameState);

        // Player Attributes:
        this.pointToSeek = null;
        this.gold = GameConfig.PLYR_START_GOLD;
        this.totalDeaths = 0;

    }


    // ########################################################################
    // Player Update Methods:
    // ########################################################################

    public boolean update() {

        if (pointToSeek != null) {

            // Remove point to seek when sufficiently close to the target.
            if (PVector.dist(pointToSeek, this.getPos()) < this.getDiameter()) {

                this.pointToSeek = null; // Point has been reached, so remove point to seek.

            } else { // Otherwise, enact an arrival force which helps get to the point to seek.

                PVector steer = super.arrive(this.pointToSeek);
                steer = this.applyWindForceScaling(steer); // Scale the applied behaviour in accordance with sail and wind alignment.
                super.applyForce(steer);

            }

        }

        // Update aim of the player's cannons when aiming.
        if (Game.cannonFireEngaged) super.updateCannonsAim(gameState.getAim());
        // Update direction of the player's sail.
        if (Game.sailAlterEngaged) super.updateSailDirToTarget(gameState.getAim());

        boolean remove = super.update(); // Update the player's movement as an instance of a CharacterShip.
        return remove; // Player cannot be removed!

    }

    @Override
    public void avoidTerrain() {
        // Do not avoid terrain as the player!
    }


    // ########################################################################
    // Player Render/Display Methods:
    // ########################################################################

    public void display() {

        if (this.pointToSeek != null) this.displayPointToSeek(); // Display the point that the boat is seeking.

        super.display(); // Display player ship.

        this.displayPlayerCannonInfo(); // Display information about the player's ship cannons.

        if (Game.cannonFireEngaged) this.displayPlayerCannonAim(); // Display player's aim when awaiting cannon fire.

    }

    void displayHealth() {

        // Re-implement the display health method for the player so that the health bar is green.

        int baseHealthBarLength = this.getBaseHealth() / GameConfig.CHAR_HEALTH_PER_PIX;
        int currHealthBarLength = (int) this.getCurrHealth() / GameConfig.CHAR_HEALTH_PER_PIX;

        int healthBarStartX = (int) this.getPosX() - (baseHealthBarLength / 2);
        int healthBarStartY = (int) (this.getPosY() + this.getDiameter() + 5);

        // Health bar is a background bar representing the total length with a foreground bar representing curr health.
        Game.sketch.fill(GameConfig.CHAR_HEALTH_COL_BACK);
        Game.sketch.stroke(GameConfig.CHAR_HEALTH_COL_BACK);
        Game.sketch.rect(healthBarStartX, healthBarStartY, baseHealthBarLength, 5);
        Game.sketch.fill(GameConfig.PLYR_ICON_COL);
        Game.sketch.rect(healthBarStartX, healthBarStartY, currHealthBarLength, 5);

    }

    void displayShipMast() {

        // Over-written since mast changing indicators should be shown when the player is updating their mast angle.

        if (Game.sailAlterEngaged) {

            PVector shipToAimVector = new PVector(gameState.getAim().x - gameState.getPlayer().getPosX(),
                                                gameState.getAim().y - gameState.getPlayer().getPosY());
            float shipToAimHeading = shipToAimVector.heading();

            Game.sketch.translate(this.getPosX(), this.getPosY()); // Translate co-ordinate origin to player position.
            Game.sketch.rotate(shipToAimHeading); // Rotate model to point in aiming direction.

            this.displaySail();

            // Reset the origin.
            Game.sketch.rotate(-shipToAimHeading);
            Game.sketch.translate(-this.getPosX(), -this.getPosY());

            Game.sketch.fill(GameConfig.SHIP_MAST_COL);
            Game.sketch.stroke(GameConfig.SHIP_MAST_COL);
            Game.sketch.line(this.getPosX(), this.getPosY(), gameState.getAim().x, gameState.getAim().y);

        } else {

            super.displayShipMast(); // Otherwise, show ship sail the same as all other ships.

        }

    }

    private void displayPointToSeek() {

        // Display simple circle of the player's icon colour to show the point where the ship is seeking.
        Game.sketch.fill(GameConfig.PLYR_ICON_COL);
        Game.sketch.stroke(0);

        Game.sketch.ellipse(this.pointToSeek.x, this.pointToSeek.y, (float) this.getDiameter() / 2, (float) this.getDiameter() / 2);

    }

    private void displayPlayerCannonInfo() {

        // Simple text-based showing of cannon info under the health bar.
        Game.sketch.textSize((float) GameConfig.TITLE_TEXT_SIZE / 2.5f);
        Game.sketch.fill(GameConfig.TITLE_TEXT_COLOR);
        Game.sketch.textAlign(PConstants.CENTER);

        int cannonInfoStartX = (int) this.getPosX();
        int cannonInfoStartY = (int) (this.getPosY() + this.getDiameter() + 25);

        String cannonInfoStr = "L: " + this.getLeftCannon().getNumVolleys() +
                " (" + this.getLeftCannon().getNumBallsPerVolley() + ")" +
                " | R: " + this.getRightCannon().getNumVolleys() +
                " (" + this.getRightCannon().getNumBallsPerVolley() + ")";

        Game.sketch.text(cannonInfoStr, cannonInfoStartX, cannonInfoStartY);

    }

    private void displayPlayerCannonAim() {

        Game.sketch.fill(GameConfig.CANNON_COL);
        Game.sketch.stroke(GameConfig.CANNON_COL);

        // Determine which side of cannons to fire on given the target.
        boolean fireRight = this.isTargetRightOfShip(this.getOri(), gameState.getAim());

        if (fireRight) {
            Game.sketch.line(this.getRightCannon().getPosX(), this.getRightCannon().getPosY(), gameState.getAim().x, gameState.getAim().y);
        } else {
            Game.sketch.line(this.getLeftCannon().getPosX(), this.getLeftCannon().getPosY(), gameState.getAim().x, gameState.getAim().y);
        }

    }


    // ########################################################################
    // Player - Point To Seek:
    // ########################################################################

    public void setPointToSeek(PVector pointToSeek) {
        this.pointToSeek = pointToSeek;
    }

    public void halt() {

        // Halt this player character.
        this.setVel(new PVector(0, 0));
        this.setAccel(new PVector(0, 0));
        this.setPointToSeek(null);

    }

    public void panPointToSeek(float x, float y) {

        if (pointToSeek != null) {
            this.pointToSeek.x -= x;
            this.pointToSeek.y -= y;
        }

    }

    public PVector getPointToSeek() {
        return pointToSeek;
    }


    // ########################################################################
    // Player - Gold:
    // ########################################################################

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void addGold(int goldToAdd) {
        this.gold += goldToAdd;
    }

    public void subGold(int goldToTake) {
        this.gold -= goldToTake;
    }

    public int getGold() {
        return gold;
    }


    // ########################################################################
    // Player - Deaths:
    // ########################################################################

    public void incrementTotalDeaths() {
        this.totalDeaths += 1;
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }


}
