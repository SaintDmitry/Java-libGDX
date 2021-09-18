package com.mygdx.game.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.Pools.BulletPool;
import com.mygdx.game.Pools.EnemyPool;
import com.mygdx.game.Pools.ExplosionPool;
import com.mygdx.game.base.BaseScreen;
import com.mygdx.game.base.Font;
import com.mygdx.game.math.Rect;
import com.mygdx.game.sprite.Background;
import com.mygdx.game.sprite.Bullet;
import com.mygdx.game.sprite.Enemy;
import com.mygdx.game.sprite.GameOver;
import com.mygdx.game.sprite.NewGame;
import com.mygdx.game.sprite.PlayerShip;
import com.mygdx.game.sprite.Star;
import com.mygdx.game.utils.EnemyGenerator;
import com.mygdx.game.utils.Sounds;

import java.util.List;

public class GameScreen extends BaseScreen {

    private enum State {PLAYING, PAUSE, GAME_OVER}

    private static final int STAR_COUNT = 64;
    private static final float INDENT = 0.01f;
    private static final String FRAGS = "Frags: ";
    private static final String HP = "HP: ";
    private static final String LEVEL = "Level: ";

    private Game game;
    private TextureAtlas atlas;
    private Texture bgimg;
    private Background background;
    private Sounds sounds = new Sounds();

    private EnemyPool enemyPool;
    private EnemyGenerator enemyGenerator;
    private ExplosionPool explosionPool;
    private Vector2 enemyStartPos = new Vector2();

    private Star[] starArray;
    private PlayerShip playerShip;
    private BulletPool bulletPool;

    private State state;
    private State stateBuff;

    private GameOver gameOver;
    private NewGame newGame;

    private Font font;
    private StringBuilder sbFrags;
    private StringBuilder sbHp;
    private StringBuilder sbLevel;

    private int frags;

    public GameScreen(Game game) {
        this.game = game;
    }


    @Override
    public void show() {
        playIntro();
        super.show();
        sounds.playSoundTrack();
        atlas = new TextureAtlas("textures/mainAtlas.tpack");
        bgimg = new Texture("background.jpg");
        background = new Background(new TextureRegion(bgimg));
        starArray = new Star[STAR_COUNT];
        for (int i = 0; i < STAR_COUNT; i++) {
            starArray[i] = new Star(atlas);
        }
        bulletPool = new BulletPool();
        explosionPool = new ExplosionPool(atlas);
        playerShip = new PlayerShip(atlas, bulletPool, explosionPool);
        enemyPool = new EnemyPool(bulletPool, explosionPool, worldBounds, playerShip);
        enemyGenerator = new EnemyGenerator(enemyPool, atlas, worldBounds);
        state = State.PLAYING;
        stateBuff = State.PLAYING;
        gameOver = new GameOver(atlas);
        newGame = new NewGame(atlas, this);
        font =  new Font("font/font.fnt", "font/font.png");
        font.setSize(0.03f);
        sbFrags = new StringBuilder();
        sbHp = new StringBuilder();
        sbLevel = new StringBuilder();
    }

    private void playIntro() {
        sounds.playLoading();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        update(delta);
        checkCollisions();
        freeAllDestroyedActiveSprites();
        draw();
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        background.resize(worldBounds);
        for (Star star : starArray) {
            star.resize(worldBounds);
        }
        playerShip.resize(worldBounds);
        gameOver.resize(worldBounds);
        newGame.resize(worldBounds);
    }

    @Override
    public void pause() {
        pauseOn();
    }

    @Override
    public void resume() {
        pauseOff();
    }

    @Override
    public void dispose() {
        super.dispose();
        bgimg.dispose();
        atlas.dispose();
        explosionPool.dispose();
        bulletPool.dispose();
        enemyPool.dispose();
        playerShip.dispose();
        gameOver.dispose();
        newGame.dispose();
        sounds.dispose();
        font.dispose();
    }

    private void update(float delta) {
        if (state != State.PAUSE) {
            for (Star star : starArray) {
                star.update(delta);
            }
        }
        explosionPool.updateActiveSprites(delta);
        if (state == State.PLAYING) {
            bulletPool.updateActiveSprites(delta);
            enemyPool.updateActiveSprites(delta);
            playerShip.update(delta);
            enemyGenerator.generate(delta, frags);
        }
        if (state == State.GAME_OVER) {
            gameOver.update(delta);
            newGame.update(delta);
            sounds.stopSoundTrack();
        }
    }

    private void freeAllDestroyedActiveSprites() {
        explosionPool.freeAllDestroyedActiveSprites();
        bulletPool.freeAllDestroyedActiveSprites();
        enemyPool.freeAllDestroyedActiveSprites();
    }

    private void draw() {
        ScreenUtils.clear(1, 0, 0, 1);
        batch.begin();
        background.draw(batch);
        for (Star star : starArray) {
            star.draw(batch);
        }
        explosionPool.drawActiveSprites(batch);
        if (state == State.PLAYING || (state == State.PAUSE && stateBuff != State.GAME_OVER)) {
            bulletPool.drawActiveSprites(batch);
            enemyPool.drawActiveSprites(batch);
            playerShip.draw(batch);
        }
        if (state == State.GAME_OVER) {
            gameOver.draw(batch);
            newGame.draw(batch);
        }
        printInfo();
        batch.end();
    }

    private void checkCollisions() {
        if (state != State.PLAYING) {
            return;
        }
        checkPlayerDestroyed();
        List<Enemy> enemyList = enemyPool.getActiveObjects();
        for (Enemy enemy : enemyList) {
            if (!enemy.isDestroyed()) {
                float minDist = enemy.getHalfWidth() + playerShip.getHalfWidth();
                if (enemy.pos.dst(playerShip.pos) < minDist) {
                    enemy.damage(enemy.getHp());
                    playerShip.damage(enemy.getHp());
                    checkPlayerDestroyed();
                }
            }
        }
        List<Bullet> bulletList = bulletPool.getActiveObjects();
        for (Bullet bullet : bulletList) {
            if (!bullet.isDestroyed()) {
                if (bullet.getOwner() != playerShip) {
                    if (playerShip.isBulletCollision(bullet)) {
                        playerShip.damage(bullet.getDamage());
                        checkPlayerDestroyed();
                        bullet.destroy();
                    }
                } else {
                    for (Enemy enemy : enemyList) {
                        if (!enemy.isDestroyed()) {
                            if (enemy.isBulletCollision(bullet)) {
                                enemy.damage(bullet.getDamage());
                                bullet.destroy();
                                if (enemy.isDestroyed()) {
                                    frags++;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer, int button) {
        if (state == State.PLAYING) {
            playerShip.touchDown(touch, pointer, button);
        }
        if (state == State.GAME_OVER) {
            newGame.touchDown(touch, pointer, button);
        }
        return false;
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer, int button) {
        if (state == State.PLAYING) {
            playerShip.touchUp(touch, pointer, button);
        }
        if (state == State.GAME_OVER) {
            newGame.touchUp(touch, pointer, button);
        }
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        //Пауза
        if (keycode == Input.Keys.P) {
            if (state == State.PAUSE) {
                pauseOff();
            } else {
                pauseOn();
            }
        }//Выход в меню
        if (keycode == 111) {
            game.setScreen(new MenuScreen(game));
        } else if (state == State.PLAYING) {
            //Передача нажатия для движения
            playerShip.keyDown(keycode);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (state == State.PLAYING) {
            playerShip.keyUp(keycode);
        }
        return false;
    }

    private void pauseOn() {
        stateBuff = state;
        state = State.PAUSE;
        sounds.pauseSoundTrack();
    }

    private void pauseOff() {
        state = stateBuff;
        sounds.playSoundTrack();
    }

    private void printInfo() {
        sbFrags.setLength(0);
        sbHp.setLength(0);
        sbLevel.setLength(0);
        font.draw(batch, sbFrags.append(FRAGS).append(frags), worldBounds.getLeft(), worldBounds.getTop() - INDENT);
        font.draw(batch, sbHp.append(HP).append(playerShip.getHp()), worldBounds.getPosX(), worldBounds.getTop() - INDENT, Align.center);
        font.draw(batch, sbLevel.append(LEVEL).append(enemyGenerator.getLevel()), worldBounds.getRight(), worldBounds.getTop() - INDENT, Align.right);
    }

    public void startNewGame() {
        playIntro();
        state = State.PLAYING;
        bulletPool.freeAllActiveObjects();
        enemyPool.freeAllActiveObjects();
        explosionPool.freeAllActiveObjects();
        playerShip.startNewGame();
        sounds.playSoundTrack();
        frags = 0;
    }

    private void checkPlayerDestroyed() {
        if (playerShip.isDestroyed()) {
            state = State.GAME_OVER;
        }
    }
}
