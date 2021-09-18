package com.mygdx.game.base;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Pools.BulletPool;
import com.mygdx.game.Pools.ExplosionPool;
import com.mygdx.game.math.Rect;
import com.mygdx.game.sprite.Bullet;
import com.mygdx.game.sprite.Explosion;
import com.mygdx.game.utils.Sounds;

public abstract class Ship extends Sprite{

    protected TextureRegion bulletRegion;

    private Sounds sounds = new Sounds();

    protected Rect worldBounds;
    protected BulletPool bulletPool;
    protected ExplosionPool explosionPool;

    protected Vector2 v0;
    protected Vector2 speed;
    protected Vector2 bulletSpeed;

    protected float reloadInterval;
    protected float reloadTimer;
    protected float bulletHeight;

    protected int damage;
    protected int hp;

    private final float damageAnimateInterval = 0.2f;
    private float damageAnimateTimer = damageAnimateInterval;

    public Ship() {
    }

    public Ship(TextureRegion region, int rows, int cols, int frames) {
        super(region, rows, cols, frames);
    }

    @Override
    public void resize(Rect worldBounds) {
        this.worldBounds = worldBounds;
    }


    @Override
    public void dispose() {

    }

    @Override
    public void update(float delta) {
        damageAnimateTimer += delta;
        if (damageAnimateTimer >= damageAnimateInterval) {
            frame = 0;
        }
    }

    private void shoot(String shooter) {

        Bullet bullet = bulletPool.obtain();
        if (shooter.equals("player")) {
            sounds.playPlayerShoot();
        } else {
            sounds.playEnemyShoot();
        }
        bullet.set(this, bulletRegion, pos, bulletSpeed, bulletHeight, worldBounds, damage);
    }

    public void autoShoot(float delta, String shooter) {
        reloadTimer += delta;
        if (reloadTimer >= reloadInterval) {
            reloadTimer = 0f;
            shoot(shooter);
        }
    }

    public void damage(int damage) {
        frame = 1;
        damageAnimateTimer = 0f;
        hp -= damage;
        if (hp <= 0) {
            destroy();
            boom();
        }
    }

    public int getHp() {
        return hp;
    }

    public float getBulletHeight() {
        return bulletHeight;
    }

    public void boom() {
        Explosion explosion = (Explosion) explosionPool.obtain();
        explosion.set(getHeight(), pos);
        sounds.playBoom(getBulletHeight());
    }
}
