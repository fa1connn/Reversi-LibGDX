package io.github.fa1connn.reversi.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.fa1connn.reversi.reversiGame;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new reversiGame(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("reversi");

        // Vsync, ekran yırtılmalarını önler
        configuration.useVsync(true);

        // FPS limitini monitörün yenileme hızına (Hz) eşitler
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);

        // --- DEĞİŞİKLİK BURADA ---
        // Eski kod (Pencere modu): configuration.setWindowedMode(640, 480);

        // Yeni kod (Tam Ekran):
        // Bilgisayarın mevcut ekran çözünürlüğünü alır ve oyunu tam ekran başlatır.
        configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        // -------------------------

        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

        // Uyumluluk ayarları (Bunlara dokunmuyoruz, olduğu gibi kalıyor)
        configuration.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.ANGLE_GLES20, 0, 0);

        return configuration;
    }
}
