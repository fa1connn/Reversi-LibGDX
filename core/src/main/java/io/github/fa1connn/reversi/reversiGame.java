package io.github.fa1connn.reversi;

import com.badlogic.gdx.Game;

public class reversiGame extends Game {

    @Override
    public void create() {
        // Oyun başlar başlamaz kendi ekranımıza geçiyoruz
        setScreen(new MenuScreen(this));
    }

    // render, dispose gibi metodları burada tekrar yazmana gerek yok.
    // Game sınıfı bunları otomatik olarak aktif olan Screen'e (GameScreen) iletir.
}
