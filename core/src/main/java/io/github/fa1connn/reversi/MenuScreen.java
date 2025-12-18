package io.github.fa1connn.reversi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout; // YENİ: Yazı hizalama aracı
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MenuScreen extends ScreenAdapter {
    private final reversiGame game;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    private Viewport viewport;
    private GlyphLayout layout; // Yazı ölçümleri için

    // Sanal ekran boyutumuz (Tasarımı buna göre yapıyoruz, sistem ölçekliyor)
    private static final float WORLD_WIDTH = 800f;
    private static final float WORLD_HEIGHT = 800f;

    public MenuScreen(reversiGame game) {
        this.game = game;
        this.shapeRenderer = new ShapeRenderer();
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.font.getData().setScale(3); // Yazılar büyük olsun

        this.layout = new GlyphLayout();
        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
        viewport.apply();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.92f, 0.85f, 0.72f, 1);

        // --- GİRİŞ KONTROLÜ ---
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.input.getY();
            com.badlogic.gdx.math.Vector3 touchPoint = new com.badlogic.gdx.math.Vector3(touchX, touchY, 0);
            viewport.unproject(touchPoint);

            // OYNA BUTONU (Ortada)
            // Merkez: 400,350 -> Genişlik 240, Yükseklik 100
            if (touchPoint.x > 280 && touchPoint.x < 520 && touchPoint.y > 300 && touchPoint.y < 400) {
                game.setScreen(new GameScreen(game));
            }

            // ÇIKIŞ BUTONU (Sol Üst)
            if (touchPoint.x > 20 && touchPoint.x < 170 && touchPoint.y > 720 && touchPoint.y < 780) {
                Gdx.app.exit();
            }
        }

        // --- ŞEKİL ÇİZİMLERİ ---
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Oyna Butonu (Ekranın Tam Ortasında)
        shapeRenderer.setColor(Color.FOREST);
        // x: (800 - 240)/2 = 280, y: 300
        shapeRenderer.rect(280, 300, 240, 100);

        // Çıkış Butonu (Sol Üst)
        shapeRenderer.setColor(Color.FIREBRICK);
        shapeRenderer.rect(20, 720, 150, 60);

        shapeRenderer.end();

        // --- YAZI ÇİZİMLERİ ---
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        // 1. BAŞLIK: REVERSI
        font.setColor(Color.BLACK);
        font.getData().setScale(4); // Başlık çok büyük
        layout.setText(font, "REVERSI");
        // Ekrana tam ortala: (EkranGenişliği - YazıGenişliği) / 2
        font.draw(batch, layout, (WORLD_WIDTH - layout.width) / 2, 650);

        // 2. OYNA YAZISI
        font.setColor(Color.WHITE);
        font.getData().setScale(3);
        layout.setText(font, "OYNA");
        // Butonun tam ortasına koy: ButonX + (ButonGen - YazıGen)/2
        font.draw(batch, layout, 280 + (240 - layout.width) / 2, 300 + (100 + layout.height) / 2);

        // 3. ÇIKIŞ YAZISI
        font.getData().setScale(2);
        layout.setText(font, "CIKIS");
        font.draw(batch, layout, 20 + (150 - layout.width) / 2, 720 + (60 + layout.height) / 2);

        batch.end();
    }

    // ... (resize ve dispose metodların aynı kalabilir) ...
    @Override
    public void resize(int width, int height) { viewport.update(width, height, true); } // True: Ortala
    @Override
    public void dispose() { shapeRenderer.dispose(); batch.dispose(); font.dispose(); }
}
