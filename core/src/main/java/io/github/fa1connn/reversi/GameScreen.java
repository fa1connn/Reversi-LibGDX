package io.github.fa1connn.reversi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20; // Yarı saydamlık için gerekli
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen extends ScreenAdapter {
    private CellState currentPlayer = CellState.BLACK;
    private boolean isGameOver = false; // Oyun bitti mi kontrolü
    private String winnerText = "";     // Kazananı yazmak için

    // --- BOYUT AYARLARI ---
    private static final float WORLD_WIDTH = 800f;
    private static final float WORLD_HEIGHT = 800f;

    // Tahta Boyutu
    private static final float BOARD_SIZE = 640f;
    private static final float CELL_SIZE = BOARD_SIZE / 8;

    // Tahta Konumu
    private static final float BOARD_X = 80f;
    private static final float BOARD_Y = 40f;

    private final reversiGame game;
    private Board board;

    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;

    public GameScreen(reversiGame game) {
        this.game = game;
        board = new Board();

        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        // --- FONT KALİTESİ ---
        font = new BitmapFont();
        // Fontun kenarlarını yumuşat (Linear Filter)
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.getData().setScale(1.5f); // Çok büyütürsek bozulur, 1.5 ideal

        layout = new GlyphLayout();
    }

    @Override
    public void render(float delta) {
        // Girişleri kontrol et
        handleInput();

        ScreenUtils.clear(0.92f, 0.85f, 0.72f, 1);

        // Yarı saydamlık (transparanlık) için gerekli ayar
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        // --- 1. ŞEKİL ÇİZİMLERİ (TAHTA VE OYUN) ---
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // A) Oyun Tahtası Zemin
        shapeRenderer.setColor(new Color(0.35f, 0.20f, 0.10f, 1));
        shapeRenderer.rect(BOARD_X, BOARD_Y, BOARD_SIZE, BOARD_SIZE);

        // B) Çıkış Butonu (Sol En Üst Köşe)
        // X: 10, Y: 760 (800'e çok yakın)
        shapeRenderer.setColor(Color.FIREBRICK);
        shapeRenderer.rect(10, 760, 80, 35);

        // C) Izgara ve Taşlar
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                float x = BOARD_X + (col * CELL_SIZE);
                float y = BOARD_Y + (row * CELL_SIZE);

                // Izgaralar
                shapeRenderer.setColor(Color.BLACK);
                shapeRenderer.rectLine(x, y, x + CELL_SIZE, y, 2);
                shapeRenderer.rectLine(x, y, x, y + CELL_SIZE, 2);
                shapeRenderer.rectLine(x + CELL_SIZE, y, x + CELL_SIZE, y + CELL_SIZE, 2);
                shapeRenderer.rectLine(x, y + CELL_SIZE, x + CELL_SIZE, y + CELL_SIZE, 2);

                // Taşlar
                CellState cell = board.getCell(row, col);
                if (cell != CellState.EMPTY) {
                    float cx = x + CELL_SIZE / 2;
                    float cy = y + CELL_SIZE / 2;
                    float radius = CELL_SIZE * 0.4f;

                    if (cell == CellState.BLACK) shapeRenderer.setColor(Color.BLACK);
                    else shapeRenderer.setColor(Color.WHITE);

                    shapeRenderer.circle(cx, cy, radius);
                }
            }
        }

        // D) Sıra Göstergesi Pulu (Yazıya değmemesi için konumlandı)
        if (!isGameOver) {
            if (currentPlayer == CellState.BLACK) shapeRenderer.setColor(Color.BLACK);
            else shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.circle(230, 735, 15);
        }

        // --- OYUN BİTTİ EKRANI (OVERLAY) ---
        if (isGameOver) {
            // Yarı saydam siyah arka plan
            shapeRenderer.setColor(0, 0, 0, 0.85f);
            shapeRenderer.rect(0, 0, WORLD_WIDTH, WORLD_HEIGHT);

            // Buton Kutuları
            // Yeniden Oyna Butonu (Yeşilimsi)
            shapeRenderer.setColor(Color.FOREST);
            shapeRenderer.rect(250, 400, 300, 80);

            // Menü Butonu (Mavimsi)
            shapeRenderer.setColor(Color.ROYAL);
            shapeRenderer.rect(250, 280, 300, 80);
        }

        shapeRenderer.end();

        // --- 2. YAZI ÇİZİMLERİ ---
        batch.begin();

        // Çıkış Yazısı (Sol Üst)
        font.setColor(Color.WHITE);
        font.getData().setScale(1.2f); // Çıkış yazısı kibar olsun
        layout.setText(font, "CIKIS");
        // Butonun ortasına: 10 + (80 - width)/2
        font.draw(batch, layout, 10 + (80 - layout.width) / 2, 760 + (35 + layout.height) / 2);

        // Oyun Arayüzü Yazıları (Eğer oyun bitmediyse göster)
        if (!isGameOver) {
            font.getData().setScale(1.8f);

            // Sıra Bilgisi
            font.setColor(Color.BLACK);
            String siraMetni = (currentPlayer == CellState.BLACK) ? "SIRA: SIYAH" : "SIRA: BEYAZ";
            font.draw(batch, siraMetni, 130, 785); // Biraz daha yukarı aldık

            // Skorlar
            String skorSiyah = "SIYAH: " + countStones(CellState.BLACK);
            String skorBeyaz = "BEYAZ: " + countStones(CellState.WHITE);

            font.setColor(Color.BLACK);
            font.draw(batch, skorSiyah, 500, 790);

            font.setColor(Color.DARK_GRAY);
            font.draw(batch, skorBeyaz, 500, 760);
        }
        else {
            // --- OYUN SONU YAZILARI ---
            font.getData().setScale(3f);
            font.setColor(Color.GOLD);
            layout.setText(font, winnerText);
            // Ekranın ortasına yaz
            font.draw(batch, layout, (WORLD_WIDTH - layout.width) / 2, 600);

            font.getData().setScale(2f);
            font.setColor(Color.WHITE);

            // Yeniden Oyna Yazısı
            layout.setText(font, "YENIDEN OYNA");
            font.draw(batch, layout, 250 + (300 - layout.width) / 2, 400 + (80 + layout.height) / 2);

            // Menü Yazısı
            layout.setText(font, "MENU");
            font.draw(batch, layout, 250 + (300 - layout.width) / 2, 280 + (80 + layout.height) / 2);
        }

        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.input.getY();
            com.badlogic.gdx.math.Vector3 touchPoint = new com.badlogic.gdx.math.Vector3(touchX, touchY, 0);
            viewport.unproject(touchPoint);

            // --- 1. OYUN BİTTİYSE BUTONLARI DİNLE ---
            if (isGameOver) {
                // Yeniden Oyna Butonu (250, 400) - (550, 480)
                if (touchPoint.x >= 250 && touchPoint.x <= 550 && touchPoint.y >= 400 && touchPoint.y <= 480) {
                    resetGame(); // Oyunu sıfırla
                }

                // Menü Butonu (250, 280) - (550, 360)
                if (touchPoint.x >= 250 && touchPoint.x <= 550 && touchPoint.y >= 280 && touchPoint.y <= 360) {
                    game.setScreen(new MenuScreen(game)); // Menüye dön
                }
                return; // Oyun bittiyse aşağıdaki tahta tıklamalarını yapma
            }

            // --- 2. ÇIKIŞ BUTONU (Sol Üst) ---
            // X: 10-90, Y: 760-795
            if (touchPoint.x >= 10 && touchPoint.x <= 90 && touchPoint.y >= 760 && touchPoint.y <= 795) {
                Gdx.app.exit();
            }

            // --- 3. TAHTA HAMLESİ ---
            if (touchPoint.x >= BOARD_X && touchPoint.x < BOARD_X + BOARD_SIZE &&
                touchPoint.y >= BOARD_Y && touchPoint.y < BOARD_Y + BOARD_SIZE) {

                float localX = touchPoint.x - BOARD_X;
                float localY = touchPoint.y - BOARD_Y;
                int col = (int) (localX / CELL_SIZE);
                int row = (int) (localY / CELL_SIZE);

                if (board.isInsideBoard(row, col)) {
                    processMove(row, col);
                }
            }
        }
    }

    private void processMove(int row, int col) {
        if (GameRules.isValidMove(board, currentPlayer, row, col)) {
            GameRules.makeMove(board, currentPlayer, row, col);

            // Sırayı rakibe ver
            currentPlayer = currentPlayer.getOpponent();

            // Rakip oynayabiliyor mu?
            if (!GameRules.hasValidMove(board, currentPlayer)) {
                // Hayırsa, sıra tekrar bana geçer (Pas)
                currentPlayer = currentPlayer.getOpponent();

                // Ben de oynayamıyorsam -> Oyun Biter
                if (!GameRules.hasValidMove(board, currentPlayer)) {
                    gameOver();
                }
            }

            // Eğer tahta dolduysa da oyun biter
            if (isBoardFull()) {
                gameOver();
            }
        }
    }

    private void gameOver() {
        isGameOver = true;
        int blackScore = countStones(CellState.BLACK);
        int whiteScore = countStones(CellState.WHITE);

        if (blackScore > whiteScore) winnerText = "KAZANAN: SIYAH!";
        else if (whiteScore > blackScore) winnerText = "KAZANAN: BEYAZ!";
        else winnerText = "BERABERE!";
    }

    private void resetGame() {
        board.reset(); // Tahtayı temizle
        currentPlayer = CellState.BLACK; // Siyah başlar
        isGameOver = false; // Oyun bitmedi durumuna getir
    }

    // Tahtada hiç boş yer kaldı mı kontrolü
    private boolean isBoardFull() {
        for(int r=0; r<8; r++) {
            for(int c=0; c<8; c++) {
                if(board.getCell(r, c) == CellState.EMPTY) return false;
            }
        }
        return true;
    }

    private int countStones(CellState type) {
        int count = 0;
        for(int r=0; r<8; r++) {
            for(int c=0; c<8; c++) {
                if(board.getCell(r, c) == type) count++;
            }
        }
        return count;
    }

    @Override
    public void resize(int width, int height) { viewport.update(width, height); }

    @Override
    public void dispose() { shapeRenderer.dispose(); batch.dispose(); font.dispose(); }
}
