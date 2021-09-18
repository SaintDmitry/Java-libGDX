package com.mygdx.game.Pools;

import com.mygdx.game.base.SpritesPool;
import com.mygdx.game.sprite.Bullet;

public class BulletPool extends SpritesPool<Bullet> {
    @Override
    protected Bullet newObject() {
        return new Bullet();
    }
}
