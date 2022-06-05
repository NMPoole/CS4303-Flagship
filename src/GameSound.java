import ddf.minim.*;

class GameSound {


    // ########################################################################
    // Sound Attributes:
    // ########################################################################

    private final AudioSample soundGameMusic;
    private final float soundGameMusic_Volume = -30;
    private boolean soundGameMusic_isPlaying = false;

    private final AudioSample soundGameOver;
    private final float soundGameOver_Volume = -20;
    private boolean soundGameOver_isPlaying = false;

    private final AudioSample soundShipAmbient;
    private final float soundShipAmbient_Volume = -30;
    private boolean soundShipAmbient_isPlaying = false;

    private final AudioSample soundMoneyCollect;
    private final float soundMoneyCollect_Volume = -20;

    private final AudioSample soundCannonFire;
    private final float soundCannonFire_Volume = -25;

    private final AudioSample soundImpactThud;
    private final float soundImpactThud_Volume = -20;

    private final AudioSample soundCharDeath;
    private final float soundCharDeath_Volume = -20;

    private final AudioPlayer soundSharkAttack;
    private final float soundSharkAttack_Volume = -25;
    private boolean soundSharkAttack_isPlaying = false;

    private final AudioPlayer soundSirenSong;
    private final float soundSirenSong_Volume = -25;
    private boolean soundSirenSong_isPlaying = false;

    private final AudioSample soundWindGust;
    private final float soundWindGust_Volume = -20;


    // ########################################################################
    // Sound Constructor:
    // ########################################################################

    GameSound(Minim minim) {

        // Load relevant sound files and set associated volumes:

        this.soundCannonFire = minim.loadSample(GameConfig.CANNON_FIRE_LOC);
        this.soundCannonFire.setGain(this.soundCannonFire_Volume);

        this.soundGameMusic = minim.loadSample(GameConfig.GAME_MUSIC_LOC);
        this.soundGameMusic.setGain(this.soundGameMusic_Volume);

        this.soundGameOver = minim.loadSample(GameConfig.GAME_OVER_LOC);
        this.soundGameOver.setGain(this.soundGameOver_Volume);

        this.soundMoneyCollect = minim.loadSample(GameConfig.MONEY_COLLECT_LOC);
        this.soundMoneyCollect.setGain(this.soundMoneyCollect_Volume);

        this.soundShipAmbient = minim.loadSample(GameConfig.SHIP_AMBIENT_LOC);
        this.soundShipAmbient.setGain(this.soundShipAmbient_Volume);

        this.soundImpactThud = minim.loadSample(GameConfig.IMPACT_THUD_LOC);
        this.soundImpactThud.setGain(this.soundImpactThud_Volume);

        this.soundCharDeath = minim.loadSample(GameConfig.CHAR_DEATH_LOC);
        this.soundCharDeath.setGain(this.soundCharDeath_Volume);

        this.soundWindGust = minim.loadSample(GameConfig.WIND_GUST_LOC);
        this.soundWindGust.setGain(this.soundWindGust_Volume);

        this.soundSharkAttack = minim.loadFile(GameConfig.SHARK_ATTACK_LOC);
        this.soundSharkAttack.setGain(this.soundSharkAttack_Volume);

        this.soundSirenSong = minim.loadFile(GameConfig.SIREN_SONG_LOC);
        this.soundSirenSong.setGain(this.soundSirenSong_Volume);

    }


    // ########################################################################
    // Sound Methods:
    // ########################################################################

    public void soundCannonFire(boolean play) {

        if (play) this.soundCannonFire.trigger();
        else this.soundCannonFire.stop();

    }

    public void soundGameMusic(boolean play) {

        if (play && !this.soundGameMusic_isPlaying) {

            this.soundGameMusic.trigger();
            this.soundGameMusic_isPlaying = true;

        } else if (!play) {

            this.soundGameMusic.stop();
            this.soundGameMusic_isPlaying = false;

        }

    }

    public void soundGameOver(boolean play) {

        if (play && !this.soundGameOver_isPlaying) {

            this.soundGameOver.trigger();
            this.soundGameOver_isPlaying = true;

        } else if (!play) {

            this.soundGameOver.stop();
            this.soundGameOver_isPlaying = false;

        }

    }

    public void soundMoneyCollect(boolean play) {

        if (play) this.soundMoneyCollect.trigger();
        else this.soundMoneyCollect.stop();

    }

    public void soundShipAmbient(boolean play) {

        if (play && !this.soundShipAmbient_isPlaying) {

            this.soundShipAmbient.trigger();
            this.soundShipAmbient_isPlaying = true;

        } else if (!play) {

            this.soundShipAmbient.stop();
            this.soundShipAmbient_isPlaying = false;

        }

    }

    public void soundImpactThud(boolean play) {

        if (play) this.soundImpactThud.trigger();
        else this.soundImpactThud.stop();

    }

    public void soundCharDeath(boolean play) {

        if (play) this.soundCharDeath.trigger();
        else this.soundCharDeath.stop();

    }

    public void soundSharkAttack(boolean play) {

        if (play && !this.soundSharkAttack_isPlaying) this.soundSharkAttack.play();

        if (this.soundSharkAttack.position() >= this.soundSharkAttack.length()) {

            this.soundSharkAttack.rewind();
            this.soundSharkAttack.pause();
            this.soundSharkAttack_isPlaying = false;

        }

    }

    public void soundSirenSong(boolean play) {

        if (play && !this.soundSirenSong_isPlaying) this.soundSirenSong.play();

        if (this.soundSirenSong.position() >= this.soundSirenSong.length()) {

            this.soundSirenSong.rewind();
            this.soundSirenSong.pause();
            this.soundSirenSong_isPlaying = false;

        }

    }

    public void soundWindGust(boolean play) {

        if (play) this.soundWindGust.trigger();
        else this.soundWindGust.stop();

    }


}
