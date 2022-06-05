import processing.core.PApplet;
import processing.core.PVector;
import ddf.minim.*;

public class Game extends PApplet {


    // ################################################################################################################
    // PROCESSING LIBRARY OOP SET-UP:
    // ################################################################################################################

    public static PApplet sketch; // This sketch instance made globally available for accessing Processing functions.

    public static void main(String[] args) {
        PApplet.main("Game");
    }


    // ################################################################################################################
    // GAME ATTRIBUTES:
    // ################################################################################################################

    private GameState gameState; // State of the game (handler).

    private enum GameStage {TITLE, GAME_PLAY, GAME_PAUSED, GAME_OVER} // Possible stages of the game.
    private GameStage gameStage; // Current stage of the game.

    public static Minim minim; // Minim required for sound.
    public static GameSound gameSound; // Sounds for the game.

    private Shop shop; // Shop instance used for managing upgrades within the game within the pause menu.

    public static boolean showMechanics; // Whether the implementation should show the underlying game mechanics.
    public static boolean devPaused; // Whether the pause menu should be shown with a developmental style of view.


    // ################################################################################################################
    // GAME SET-UP:
    // ################################################################################################################

    public void settings() {

        // Set-up sketch instance:
        sketch = this;
        size(GameConfig.CANVAS_WIDTH, GameConfig.CANVAS_HEIGHT);

        // Get sound object for the game data.sounds to be played.
        minim = new Minim(this);
        gameSound = new GameSound(minim);

        gameSound.soundGameMusic(true); // Start game music at title screen.

    }

    public void setup() {

        // Initialise Game Attributes:
        this.gameStage = GameStage.TITLE; // Game begins at the title screen.
        showMechanics = false; // Game doesn't show underlying mechanics by default.

        // Initialise Canvas:
        frameRate(GameConfig.FPS); // Ideal frames-per-second - default is 60fps.
        sketch.noiseSeed(GameConfig.MAP_SEED); // Seed used by Perlin noise for procedural map generation.
        sketch.randomSeed(GameConfig.RAND_SEED); // Seed used for random generation.

        // Prevents immediate panning if the player does not use their cursor in the title screen.
        mouseX = GameConfig.CANVAS_WIDTH / 2;
        mouseY = GameConfig.CANVAS_HEIGHT / 2;

    }


    // ################################################################################################################
    // GAME DRAW:
    // ################################################################################################################

    public void draw() {

        // Draw the game depending on the current stage of the game.
        switch (gameStage) {
            case TITLE -> displayTitleScreen();
            case GAME_PLAY -> displayGamePlay();
            case GAME_PAUSED -> displayGamePaused();
            case GAME_OVER -> displayGameOver();
        }

    }

    private void displayTitleScreen() {

        // Temporary generation and display of the map as a cool background.
        Map tempMap = new Map(new GameState());
        tempMap.display();

        // Simple title screen with game title, game controls, and start instructions.
        textAlign(CENTER);

        // Game Title:
        textSize(GameConfig.TITLE_TEXT_SIZE * 2);
        fill(255);
        text("FLAGSHIP", (float) GameConfig.CANVAS_WIDTH / 2, (float) GameConfig.CANVAS_HEIGHT / 7);

        // Game Controls:
        textSize(GameConfig.TITLE_TEXT_SIZE);
        fill(GameConfig.TITLE_TEXT_COLOR);
        text("GAME CONTROLS:\n" +
                    "Mouse Hover - Explores Map (Hovering Towards Screen Edges Causes Map Panning)\n" +
                    "Mouse Left Click - Specify Player Ship Target Location (i.e., Click-To-Move)\n" +
                    "'Tab' - Toggles Shop/Pausing (Mouse Left-Click Upgrade Buttons To Purchase)\n" +
                    "Hold '1' - Engages Sail Adjustment (Mouse Position Controls Sail Direction)\n" +
                    "Hold '2' - Engages Cannon Aiming (Mouse Position Aims, Left-Click Fires)\n" +
                    "DEV CONTROLS:\n" +
                    "'W'|'A'|'S'|D' - Enacts Up|Left|Down|Right Map Panning Respectively\n" +
                    "'M' - Skip To Game Over Screen\n" +
                    "'N' - Toggles View Of Underlying Game Mechanics\n" +
                    "'B' - Toggles Pausing Without Shop Menu\n",
                (float) GameConfig.CANVAS_WIDTH / 2, (float) 2 * GameConfig.CANVAS_HEIGHT / 7);

        // Game Begin Instructions:
        textSize(GameConfig.TITLE_TEXT_SIZE * 1.5f);
        fill(255);
        text("PRESS ANY KEY TO BEGIN GAME",
                (float) GameConfig.CANVAS_WIDTH / 2, (float) 6 * GameConfig.CANVAS_HEIGHT / 7);

    }

    private void displayGamePlay() {

        gameState.update(); // Update all game objects.
        gameState.display(); // Display all game objects.

        if (gameState.isGameEnd()) {
            gameStage = GameStage.GAME_OVER; // At game over, switch to game over screen.
            gameSound.soundShipAmbient(false);
            gameSound.soundGameOver(true);
        }

    }

    private void displayGamePaused() {

        // Add text (top left) to indicate that the game is paused to the player.
        textAlign(CENTER);
        stroke(GameConfig.TITLE_TEXT_COLOR);
        fill(GameConfig.TITLE_TEXT_COLOR);
        textSize(GameConfig.TITLE_TEXT_SIZE);
        text("PAUSED", (float) GameConfig.CANVAS_WIDTH / 10, (float) GameConfig.CANVAS_HEIGHT / 10);

        if (!devPaused) this.shop.display(); // Display shop for player to be able to purchase upgrades.

    }

    private void displayGameOver() {

        // Temporary generation and display of the map as a cool background.
        Map tempMap = new Map(new GameState());
        tempMap.display();

        // Simple game over screen with title, total deaths, and return instructions.
        textAlign(CENTER);

        textSize(GameConfig.TITLE_TEXT_SIZE * 2);
        fill(255);
        text("GAME OVER", (float) GameConfig.CANVAS_WIDTH / 2, (float) GameConfig.CANVAS_HEIGHT / 3);

        textSize(GameConfig.TITLE_TEXT_SIZE);
        text("YOU DEFEATED THE FINAL BOSS!", (float) GameConfig.CANVAS_WIDTH / 2, (float) (GameConfig.CANVAS_HEIGHT / 3) + 25);

        fill(GameConfig.TITLE_TEXT_COLOR);
        text("TOTAL DEATHS: " + gameState.getPlayer().getTotalDeaths(), (float) GameConfig.CANVAS_WIDTH / 2, (float) GameConfig.CANVAS_HEIGHT / 2);

        fill(255);
        text("PRESS ANY KEY TO RETURN TO THE TITLE SCREEN",
                (float) GameConfig.CANVAS_WIDTH / 2, (float) 2 * GameConfig.CANVAS_HEIGHT / 3);

    }


    // ################################################################################################################
    // GAME CONTROLS:
    // ################################################################################################################

    public static boolean sailAlterEngaged = false;
    public static boolean cannonFireEngaged = false;


    // ###############
    // Keyboard Press:
    // ###############

    @Override
    public void keyPressed() {

        // Game Pausing At Any Point:
        if (keyCode == TAB) {

            // Toggle between paused and un-paused.
            if (this.gameStage == GameStage.GAME_PLAY) this.gameStage = GameStage.GAME_PAUSED;
            else if (this.gameStage == GameStage.GAME_PAUSED) this.gameStage = GameStage.GAME_PLAY;

            devPaused = false; // Use of TAB is always used for the shop.

            return;

        } else if (key == 'b' || key == 'B') {

            // Toggle between paused and un-paused.
            if (this.gameStage == GameStage.GAME_PLAY) {
                this.gameStage = GameStage.GAME_PAUSED;
                devPaused = true;
            } else if (this.gameStage == GameStage.GAME_PAUSED) {
                this.gameStage = GameStage.GAME_PLAY;
                devPaused = false;
            }

            return;

        }

        // Only accept key input when un-paused:
        if (this.gameStage != GameStage.GAME_PAUSED) {

            // Handle input event according to stage of the game.
            switch (gameStage) {
                case TITLE -> titleKeyPressed(key); // Start the game.
                case GAME_PLAY -> gameKeyPressed(key); // Handle input during the game.
                case GAME_OVER -> gameOverKeyPressed(key); // End the game.
            }

        }

    }

    private void titleKeyPressed(char key) {

        gameState = new GameState(); // Create new game state.
        shop = new Shop(gameState); // Create instance of the shop.
        gameStage = GameStage.GAME_PLAY; // Start game.

        gameSound.soundGameMusic(false); // End game music.
        gameSound.soundShipAmbient(true); // Start ship ambient sounds.

    }

    private void gameKeyPressed(char key) {

        switch (key) {

            // Dev/Debug Camera Controls:
            case 'w', 'W' -> gameState.panUp(10);
            case 's', 'S' -> gameState.panDown(10);
            case 'a', 'A' -> gameState.panLeft(10);
            case 'd', 'D' -> gameState.panRight(10);

            // Player Ship Controls:
            case '1' -> sailAlterEngaged = true;
            case '2' -> cannonFireEngaged = true;

            // Shortcuts for testing/verifying game elements more quickly.
            case 'm', 'M' -> {
                gameStage = GameStage.GAME_OVER;
                gameSound.soundShipAmbient(false);
                gameSound.soundGameOver(true); // End game music.
            }
            case 'n', 'N' -> showMechanics = !showMechanics;

        }

    }

    private void gameOverKeyPressed(char key) {

        gameStage = GameStage.TITLE; // Go back to title screen.
        gameSound.soundGameOver(false);
        gameSound.soundGameMusic(true); // Start game music at title screen.

    }

    // ##################
    // Keyboard Released:
    // ##################

    @Override
    public void keyReleased() {

        // Only accept input when un-paused:
        if (this.gameStage != GameStage.GAME_PAUSED) {

            // Handle input event according to stage of the game.
            switch (gameStage) {
                case GAME_PLAY -> gameKeyReleased(key); // Handle input during the game.
            }

        }

    }

    public void gameKeyReleased(char key) {

        switch (key) {

            case '1' -> Game.sailAlterEngaged = false; // Sail Alignment Controls:
            case '2' -> cannonFireEngaged = false; // Cannon Firing Controls:

        }

    }

    // ##################
    // Mouse Pressed:
    // ##################

    @Override
    public void mousePressed() {

        // Handle input event according to stage of the game.
        switch (gameStage) {
            case GAME_PLAY -> gamePlayMousePressed(); // Handle input during the game.
            case GAME_PAUSED -> gamePausedMousePressed(); // End the game.
        }

    }

    private void gamePlayMousePressed() {

        // If cannon firing key is active, player clicks will fire the player's cannons.
        if (cannonFireEngaged) gameState.getPlayer().fireCannons(gameState.getAim());

        // When no mouse-press related keys are activated, player clicks move the player.
        else gameState.getPlayer().setPointToSeek(new PVector(gameState.getAim().x, gameState.getAim().y));

    }

    private void gamePausedMousePressed() {

        shop.purchase(new PVector(mouseX, mouseY)); // Shop interaction with mouse press.

    }


}
