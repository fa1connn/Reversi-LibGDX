package io.github.fa1connn.reversi;

import com.badlogic.gdx.Game;

public class reversiGame extends Game {
    @Override
    public void create() {
        //We switch to our own screen
        setScreen(new MenuScreen(this));
    }
}
