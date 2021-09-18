package com.mygdx.game.sprite;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Pools.BulletPool;
import com.mygdx.game.Pools.ExplosionPool;
import com.mygdx.game.base.Ship;
import com.mygdx.game.math.Rect;

public class PlayerShip extends Ship {

    private static final int INVALID_POINTER = -1;
    private static final int HP = 100;

    private boolean pressedLeft = false;
    private boolean pressedRight = false;

    private int leftPointer = INVALID_POINTER;
    private int rightPointer = INVALID_POINTER;

    public PlayerShip(TextureAtlas atlas, BulletPool bulletPool, ExplosionPool explosionPool) {
        super(atlas.findRegion("main_ship"), 1, 2, 2);
        bulletRegion = atlas.findRegion("bulletMainShip");
        this.bulletPool = bulletPool;
        this.explosionPool = explosionPool;
        reloadInterval = 0.2f;
        v0 = new Vector2(0.5f, 0);
        speed = new Vector2();
        bulletSpeed = new Vector2(0f, 0.5f);
        bulletHeight = 0.02f;
        damage = 1;
        hp = HP;
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        setHeightProportion(0.15f);
        setBottom(worldBounds.getBottom() + 0.03f);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        pos.mulAdd(speed, delta);
        autoShoot(delta, "player");
        checkBounds();
    }

    public void checkBounds() {
        //Упираемся в стенки
        if (getRight() < worldBounds.getLeft() + getWidth()) setLeft(worldBounds.getLeft());
        if (getLeft() > worldBounds.getRight() - getWidth()) setRight(worldBounds.getRight());
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer, int button) {
        if (touch.x < worldBounds.pos.x) {
            if (leftPointer != INVALID_POINTER) {
                return false;
            }
            leftPointer = pointer;
            moveLeft();
        } else {
            if (rightPointer != INVALID_POINTER) {
                return false;
            }
            rightPointer = pointer;
            moveRight();
        }
        return false;
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer, int button) {
        if (pointer == leftPointer) {
            leftPointer = INVALID_POINTER;
            if (rightPointer != INVALID_POINTER) {
                moveRight();
            } else {
                stop();
            }
        }else if (pointer == rightPointer) {
            rightPointer = INVALID_POINTER;
            if (leftPointer != INVALID_POINTER) {
                moveLeft();
            } else {
                stop();
            }
        }
        return false;
    }

    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.LEFT:
            case Input.Keys.A:
                moveLeft();
                pressedLeft = true;
                break;

            case Input.Keys.RIGHT:
            case Input.Keys.D:
                moveRight();
                pressedRight = true;
                break;
        }
        return false;
    }

    public boolean keyUp(int keycode) {
//        keyDowned = false;
//        if(keycode == 21 || keycode == 22) {
//            keyDirection.set(pos.x, 0);
//        }
        switch (keycode) {
            case Input.Keys.LEFT:
            case Input.Keys.A:
                if (!pressedRight) {
                    stop();
                }
                pressedLeft = false;
                break;
            case Input.Keys.RIGHT:
            case Input.Keys.D:
                if (!pressedLeft) {
                    stop();
                }
                pressedRight = false;
                stop();
                break;
        }
        return false;
    }

    public boolean isBulletCollision(Rect bullet) {
        return !(
                bullet.getRight() < getLeft() ||
                bullet.getLeft() > getRight() ||
                bullet.getBottom() > pos.y ||
                bullet.getTop() < getBottom()
        );
    }

    private void moveRight() {
        speed.set(v0);
    }

    private void moveLeft() {
        speed.set(v0).rotate(180);
    }

    private void stop() {
        speed.setZero();
    }

    public void startNewGame() {
        stop();
        pressedRight = false;
        pressedLeft = false;
        leftPointer = INVALID_POINTER;
        rightPointer = INVALID_POINTER;
        hp = HP;
        pos.x = worldBounds.pos.x;
        flushDestroy();
    }
}
