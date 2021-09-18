package com.mygdx.game.utils;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Pools.EnemyPool;
import com.mygdx.game.math.Rect;
import com.mygdx.game.math.Rnd;
import com.mygdx.game.sprite.Enemy;

public class EnemyGenerator {

    private static final float ENEMY_SMALL_HEIGHT = 0.1f;
    private static final float ENEMY_SMALL_BULLET_HEIGHT = 0.02f;
    private static final float ENEMY_SMALL_BULLET_SPEED = -0.3f;
    private static final int ENEMY_SMALL_DAMAGE = 1;
    private static final float ENEMY_SMALL_RELOAD_INTERVAL = 3f;
    private static final int ENEMY_SMALL_HP = 1;

    private static final float ENEMY_MEDIUM_HEIGHT = 0.12f;
    private static final float ENEMY_MEDIUM_BULLET_HEIGHT = 0.03f;
    private static final float ENEMY_MEDIUM_BULLET_SPEED = -0.25f;
    private static final int ENEMY_MEDIUM_DAMAGE = 5;
    private static final float ENEMY_MEDIUM_RELOAD_INTERVAL = 4f;
    private static final int ENEMY_MEDIUM_HP = 5;

    private static final float ENEMY_BIG_HEIGHT = 0.2f;
    private static final float ENEMY_BIG_BULLET_HEIGHT = 0.06f;
    private static final float ENEMY_BIG_BULLET_SPEED = -0.3f;
    private static final int ENEMY_BIG_DAMAGE = 10;
    private static final float ENEMY_BIG_RELOAD_INTERVAL = 2f;
    private static final int ENEMY_BIG_HP = 10;

    private float generateInterval = 3f;
    private float generateTimer;
    private Vector2 enemySmallSpeed = new Vector2(0, -0.2f);
    private Vector2 enemyMediumSpeed = new Vector2(0, -0.03f);
    private Vector2 enemyBigSpeed = new Vector2(0, -0.005f);

    private TextureRegion[] enemySmallRegions;
    private TextureRegion[] enemyMediumRegions;
    private TextureRegion[] enemyBigRegions;
    private TextureRegion bulletRegion;
    private EnemyPool enemyPool;
    private Rect worldBounds;

    private int level;

    public EnemyGenerator(EnemyPool enemyPool, TextureAtlas atlas, Rect worldBounds) {
        this.enemyPool = enemyPool;
        this.worldBounds = worldBounds;
        TextureRegion region0 = atlas.findRegion("enemy0");
        enemySmallRegions = Regions.split(region0, 1, 2, 2);
        TextureRegion region1 = atlas.findRegion("enemy1");
        enemyMediumRegions = Regions.split(region1, 1, 2, 2);
        TextureRegion region2 = atlas.findRegion("enemy2");
        enemyBigRegions = Regions.split(region2, 1, 2, 2);
        bulletRegion = atlas.findRegion("bulletEnemy");
    }

    public void generate(float delta, int frags) {
        level = frags / 10 + 1;
        generateTimer += delta;
        if (generateTimer >= generateInterval) {
            generateTimer = 0f;
            Enemy enemy = enemyPool.obtain();
            float type = (float) Math.random();
            if (type < 0.7f) {
                enemy.set(enemySmallRegions,
                        enemySmallSpeed,
                        bulletRegion,
                        ENEMY_SMALL_BULLET_HEIGHT,
                        ENEMY_SMALL_BULLET_SPEED,
                        ENEMY_SMALL_DAMAGE * level,
                        ENEMY_SMALL_RELOAD_INTERVAL,
                        ENEMY_SMALL_HEIGHT,
                        ENEMY_SMALL_HP
                );
            } else if (type < 0.9f) {
                enemy.set(enemyMediumRegions,
                        enemyMediumSpeed,
                        bulletRegion,
                        ENEMY_MEDIUM_BULLET_HEIGHT,
                        ENEMY_MEDIUM_BULLET_SPEED,
                        ENEMY_MEDIUM_DAMAGE * level,
                        ENEMY_MEDIUM_RELOAD_INTERVAL,
                        ENEMY_MEDIUM_HEIGHT,
                        ENEMY_MEDIUM_HP
                );
            } else {
                enemy.set(enemyBigRegions,
                        enemyBigSpeed,
                        bulletRegion,
                        ENEMY_BIG_BULLET_HEIGHT,
                        ENEMY_BIG_BULLET_SPEED,
                        ENEMY_BIG_DAMAGE * level,
                        ENEMY_BIG_RELOAD_INTERVAL,
                        ENEMY_BIG_HEIGHT,
                        ENEMY_BIG_HP
                );
            }
            enemy.pos.x = Rnd.nextFloat(worldBounds.getLeft() + adjustment(enemy),
                                        worldBounds.getRight() - adjustment(enemy));
            enemy.setBottom(worldBounds.getTop());
        }
    }

    private float adjustment(Enemy enemy) {
        if (enemy.getWidth() < 0.1f) {
            return 0.05f;
        }
        return enemy.getHalfWidth();
    }

    public int getLevel() {
        return level;
    }
}
