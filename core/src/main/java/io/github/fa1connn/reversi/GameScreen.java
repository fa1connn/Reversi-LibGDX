package io.github.fa1connn.reversi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
    private final reversiGame game;
    private Board board;

    //Game status
    private CellState currentPlayer = CellState.BLACK;
    private boolean isGameOver = false;
    private String winnerText = "";

    //Drawing tools
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;

    //Constants
    private static final float WORLD_WIDTH = 800f;
    private static final float WORLD_HEIGHT = 800f;

    //Board settings
    private static final float BOARD_SIZE = 640f;
    private static final float CELL_SIZE = BOARD_SIZE / 8;
    private static final float BOARD_X = 80f;
    private static final float BOARD_Y = 40f;

    //Button coordinates
    private static final float EXIT_BTN_WIDTH = 80f;
    private static final float EXIT_BTN_HEIGHT = 35f;
    private static final float EXIT_BTN_X = 10f;
    private static final float EXIT_BTN_Y = WORLD_HEIGHT - EXIT_BTN_HEIGHT - 10f;

    //End-of-game buttons
    private static final float BTN_WIDTH = 260f;
    private static final float BTN_HEIGHT = 70f;
    private static final float PLAY_AGAIN_Y = 400f;
    private static final float MENU_BTN_Y = 310f;

    public GameScreen(reversiGame game) {
        this.game = game;
        this.board = new Board();

        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        //Font settings
        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        layout = new GlyphLayout();
    }

    @Override
    public void render(float delta) {
        handleInput(); //Touch Control

        ScreenUtils.clear(0.62f, 0.65f, 0.92f, 1); //Background color

        //Transparency
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        //Board
        shapeRenderer.setColor(new Color(0.35f, 0.20f, 0.10f, 1));
        shapeRenderer.rect(BOARD_X, BOARD_Y, BOARD_SIZE, BOARD_SIZE);

        //Exit button
        shapeRenderer.setColor(Color.FIREBRICK);
        shapeRenderer.rect(EXIT_BTN_X, EXIT_BTN_Y, EXIT_BTN_WIDTH, EXIT_BTN_HEIGHT);

        //Cells and tokens
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                float x = BOARD_X + (col * CELL_SIZE);
                float y = BOARD_Y + (row * CELL_SIZE);

                //Grid
                shapeRenderer.setColor(Color.BLACK);
                shapeRenderer.rectLine(x, y, x + CELL_SIZE, y, 2);
                shapeRenderer.rectLine(x, y, x, y + CELL_SIZE, 2);
                shapeRenderer.rectLine(x + CELL_SIZE, y, x + CELL_SIZE, y + CELL_SIZE, 2);
                shapeRenderer.rectLine(x, y + CELL_SIZE, x + CELL_SIZE, y + CELL_SIZE, 2);

                //Tokens
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

        //Turn
        if (!isGameOver) {
            if (currentPlayer == CellState.BLACK) shapeRenderer.setColor(Color.BLACK);
            else shapeRenderer.setColor(Color.WHITE);
            //Token shape
            shapeRenderer.circle(270, 765, 13);
        }

        //End-of-Game screen
        if (isGameOver) {
            //Semi-transparent
            shapeRenderer.setColor(0, 0, 0, 0.85f);
            shapeRenderer.rect(0, 0, WORLD_WIDTH, WORLD_HEIGHT);

            //Play again
            shapeRenderer.setColor(Color.FOREST);
            float btnX = (WORLD_WIDTH - BTN_WIDTH) / 2;
            shapeRenderer.rect(btnX, PLAY_AGAIN_Y, BTN_WIDTH, BTN_HEIGHT);

            //Menu
            shapeRenderer.setColor(Color.ROYAL);
            shapeRenderer.rect(btnX, MENU_BTN_Y, BTN_WIDTH, BTN_HEIGHT);
        }

        shapeRenderer.end();

        //Texts
        batch.begin();

        //Exit
        font.setColor(Color.WHITE);
        font.getData().setScale(1.2f);
        layout.setText(font, "EXIT");
        //Center the text
        font.draw(batch, layout, EXIT_BTN_X + (EXIT_BTN_WIDTH - layout.width) / 2, EXIT_BTN_Y + (EXIT_BTN_HEIGHT + layout.height) / 2);

        if (!isGameOver) {
            //Score and turn
            font.getData().setScale(1.5f);

            //Turn
            font.setColor(Color.BLACK);
            String turnText = (currentPlayer == CellState.BLACK) ? "Turn: Black" : "Turn: White";
            font.draw(batch, turnText, 140, 775);

            //Score
            font.setColor(Color.BLACK);
            font.draw(batch, "Black: " + countStones(CellState.BLACK), 500, 785);
            font.setColor(Color.DARK_GRAY);
            font.draw(batch, "White: " + countStones(CellState.WHITE), 500, 755);
        }
        else {

            //Winner
            font.getData().setScale(3.0f);
            font.setColor(Color.GOLD);
            layout.setText(font, winnerText);
            font.draw(batch, layout, (WORLD_WIDTH - layout.width) / 2, 600);

            //Play Again
            font.getData().setScale(2.0f);
            font.setColor(Color.WHITE);
            layout.setText(font, "PLAY AGAIN");
            float btnCenterX = WORLD_WIDTH / 2;
            font.draw(batch, layout, btnCenterX - layout.width / 2, PLAY_AGAIN_Y + (BTN_HEIGHT + layout.height) / 2);

            //Menu
            layout.setText(font, "MENU");
            font.draw(batch, layout, btnCenterX - layout.width / 2, MENU_BTN_Y + (BTN_HEIGHT + layout.height) / 2);
        }
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            //Get coordinate which was taken by touch
            float touchX = Gdx.input.getX();
            float touchY = Gdx.input.getY();
            com.badlogic.gdx.math.Vector3 touchPoint = new com.badlogic.gdx.math.Vector3(touchX, touchY, 0);
            viewport.unproject(touchPoint);

            //Game Over
            if (isGameOver) {
                float btnX = (WORLD_WIDTH - BTN_WIDTH) / 2;

                //Play Again
                if (touchPoint.x >= btnX && touchPoint.x <= btnX + BTN_WIDTH &&
                    touchPoint.y >= PLAY_AGAIN_Y && touchPoint.y <= PLAY_AGAIN_Y + BTN_HEIGHT) {
                    resetGame();
                }

                //Menu
                if (touchPoint.x >= btnX && touchPoint.x <= btnX + BTN_WIDTH &&
                    touchPoint.y >= MENU_BTN_Y && touchPoint.y <= MENU_BTN_Y + BTN_HEIGHT) {
                    game.setScreen(new MenuScreen(game));
                }
                return;
            }

            if (touchPoint.x >= EXIT_BTN_X && touchPoint.x <= EXIT_BTN_X + EXIT_BTN_WIDTH &&
                touchPoint.y >= EXIT_BTN_Y && touchPoint.y <= EXIT_BTN_Y + EXIT_BTN_HEIGHT) {
                Gdx.app.exit();
            }

            //Putting tokens
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

    //Reset
    private void resetGame() {
        board.reset();
        currentPlayer = CellState.BLACK;
        isGameOver = false;
        winnerText = "";
    }

    private void processMove(int row, int col) {
        if (GameRules.isValidMove(board, currentPlayer, row, col)) {
            GameRules.makeMove(board, currentPlayer, row, col);
            currentPlayer = currentPlayer.getOpponent();

            //Does one of them have a move
            if (!GameRules.hasValidMove(board, currentPlayer)) {
                currentPlayer = currentPlayer.getOpponent();
                //The currentPlayer passes, and it's the opponent's turn
                //Does the other one have a move
                if (!GameRules.hasValidMove(board, currentPlayer)) {
                    gameOver();
                }
            }
            //Is board full
            if (isBoardFull()) {
                gameOver();
            }
        }
    }

    private void gameOver() {
        isGameOver = true;
        int blackScore = countStones(CellState.BLACK);
        int whiteScore = countStones(CellState.WHITE);

        if (blackScore > whiteScore) winnerText = "WINNER: BLACK!";
        else if (whiteScore > blackScore) winnerText = "WINNER: WHITE!";
        else winnerText = "DRAW!";
    }

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
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
    }
}
