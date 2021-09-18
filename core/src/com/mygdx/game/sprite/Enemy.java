package com.mygdx.game.sprite;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Pools.BulletPool;
import com.mygdx.game.Pools.ExplosionPool;
import com.mygdx.game.base.Ship;
import com.mygdx.game.math.Rect;

public class Enemy extends Ship {

private enum State {DESCENT, FIGHT}
private State state;
private PlayerShip playerShip;

private Vector2 descentSpeed = new Vector2(0, -0.15f);

    public Enemy(BulletPool bulletPool, ExplosionPool explosionPool, Rect worldBounds, PlayerShip playerShip) {
        this.bulletPool = bulletPool;
        this.explosionPool = explosionPool;
        this.worldBounds = worldBounds;
        this.playerShip = playerShip;
        v0 = new Vector2();
        speed = new Vector2();
        bulletSpeed = new Vector2();
        state = State.DESCENT;
    }

    public void set(
            TextureRegion[] regions,
            Vector2 v0,
            TextureRegion bulletRegion,
            float bulletHeight,
            float bulletSpeed,
            int damage,
            float reloadInterval,
            float height,
            int hp
    ) {
        this.regions = regions;
        this.v0.set(v0);
        this.bulletRegion = bulletRegion;
        this.bulletHeight = bulletHeight;
        this.bulletSpeed.set(0, bulletSpeed);
        this.damage = damage;
        this.reloadInterval = reloadInterval;
        setHeightProportion(height);
        this.hp = hp;
        reloadTimer = reloadInterval;
        speed.set(descentSpeed);
        state = State.DESCENT;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        pos.mulAdd(speed, delta);
        switch (state) {
            case DESCENT:
                if (getTop() <= worldBounds.getTop()) {
                    speed.set(v0);
                    state = State.FIGHT;
                }
                break;
            case FIGHT:
                autoShoot(delta, "enemy");
                break;
        }
        if (getBottom() < worldBounds.getBottom()) {
            playerShip.damage(damage);
            destroy();
            boom();
        }
    }

    public boolean isBulletCollision(Rect bullet) {
        return !(
                bullet.getRight() < getLeft() ||
                bullet.getLeft() > getRight() ||
                bullet.getBottom() > getTop() ||
                bullet.getTop() < pos.y
                );
    }
}
