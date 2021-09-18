package com.mygdx.game.sprite;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.game.base.ScaledTouchUpButton;
import com.mygdx.game.math.Rect;
import com.mygdx.game.screen.GameScreen;

public class NewGame extends ScaledTouchUpButton {

    private GameScreen gameScreen;

    public NewGame(TextureAtlas atlas, GameScreen gameScreen) {
        super(atlas.findRegion("button_new_game"));
        this.gameScreen = gameScreen;
    }

    @Override
    public void resize(Rect worldBounds) {
        setHeightProportion(0.1f);
        setBottom(worldBounds.getPosY() - getHeight());
        setLeft(worldBounds.getPosX() - getHalfWidth());
    }

    @Override
    public void action() {
        gameScreen.startNewGame();
    }
}
