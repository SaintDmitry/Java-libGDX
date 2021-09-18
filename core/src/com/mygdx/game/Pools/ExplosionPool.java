package com.mygdx.game.Pools;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.game.base.Sprite;
import com.mygdx.game.base.SpritesPool;
import com.mygdx.game.sprite.Explosion;

public class ExplosionPool extends SpritesPool {

    private TextureAtlas atlas;

    public ExplosionPool(TextureAtlas atlas) {
        this.atlas = atlas;
    }

    @Override
    protected Explosion newObject() {
        return new Explosion(atlas);
    }
}
