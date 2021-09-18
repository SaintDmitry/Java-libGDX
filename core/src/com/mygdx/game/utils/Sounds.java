package com.mygdx.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class Sounds {

    private Music soundTrack;
    private Sound loading;
    private Sound playerShoot;
    private Sound enemyShoot;
    private Sound boom;
    private float boomPower;

    public Sounds() {
    }

    public Sounds(float boomPower) {
        this.boomPower = boomPower;
    }

    public void playSoundTrack() {
        soundTrack = Gdx.audio.newMusic(Gdx.files.internal("audio/SoundTrack.mp3"));
        soundTrack.setLooping(true);
        soundTrack.setVolume(0.1f);
        soundTrack.play();
    }

    public void stopSoundTrack() {
        soundTrack.stop();
    }

    public void pauseSoundTrack() {
        soundTrack.pause();
    }


    public void playLoading() {
        loading = Gdx.audio.newSound(Gdx.files.internal("audio/loading.mp3"));
        loading.play(0.5f);
    }

    public void playBoom(float bulletHeight) {
        loading = Gdx.audio.newSound(Gdx.files.internal("audio/boom.mp3"));
        loading.play(bulletHeight * 50f);
    }

    public void playPlayerShoot() {
        loading = Gdx.audio.newSound(Gdx.files.internal("audio/playerShoot.mp3"));
        loading.play(0.05f);
    }

    public void playEnemyShoot() {
        loading = Gdx.audio.newSound(Gdx.files.internal("audio/enemyShoot.mp3"));
        loading.play(0.3f);
    }

    public void dispose() {
        soundTrack.dispose();
    }
}
