package com.wpearce.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Drop extends Game{

    private GameScreen gameScreen;

    public SpriteBatch batch;
    public BitmapFont font;

    public void create(){
        batch = new SpriteBatch();
        font = new BitmapFont(); // No arguments uses LibGDX's default Arial font
        gameScreen = new GameScreen(this);
        setScreen(new MainMenuScreen(this));
    }

    public void render(){
        super.render(); // Important! Super to this class is game so this renders, well... the game!
    }

    public void dispose(){
        batch.dispose();
        font.dispose();
        gameScreen.dispose();
    }
}
