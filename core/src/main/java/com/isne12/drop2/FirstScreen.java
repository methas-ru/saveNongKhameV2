package com.isne12.drop2;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class FirstScreen implements Screen {
    final Main game;

    Texture backgroundTexture;
    Texture myTexture;
    Texture enemyTexture;
    Texture dropTexture;

    Sound dropSound;
    Music music;

    Sprite mySprite;
    Sprite enemySprite;

    Vector2 touchPos;

    Array<Sprite> dropSprites;
    float dropTimer;

    Rectangle myRectangle;
    Rectangle enemyRectangle;

    Rectangle dropRectangle;
    Rectangle EnemyRectangle;

    int myHP = 10;
    int myDMG = 1;
    int enemyHP = 20;
    int enemyDMG = 2;

    public FirstScreen(final Main game) {
        this.game = game;

        // load the images for the background, bucket and droplet
        backgroundTexture = new Texture("background.png");
        myTexture = new Texture("bucket.png");
        enemyTexture = new Texture("bucket.png");
        dropTexture = new Texture("drop.png");

        // load the drop sound effect and background music
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        music.setLooping(true);
        music.setVolume(0.5F);

        mySprite = new Sprite(myTexture);
        mySprite.setSize(1, 1);
        mySprite.setPosition(0,0);

        enemySprite = new Sprite(myTexture);
        enemySprite.setSize(1, 1);
        float worldHeight = game.viewport.getWorldHeight();
        float enemyWidth = enemySprite.getWidth();
        enemySprite.setPosition(0, worldHeight-enemyWidth);

        touchPos = new Vector2();

        myRectangle = new Rectangle();
        enemyRectangle = new Rectangle();
        dropRectangle = new Rectangle();

        dropSprites = new Array<>();
    }

    @Override
    public void show() {
        // start the playback of the background music
        // when the screen is shown
        music.play();
    }

    @Override
    public void render(float delta) {
        input();
        logic();
        draw();
    }

    private void input() {
        float mySpeed = 4f;
        float enemySpeed = 2f;
        float delta = Gdx.graphics.getDeltaTime();

        //input mySprite movement

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            mySprite.translateX(mySpeed * delta);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            mySprite.translateX(-mySpeed * delta);
        }

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            game.viewport.unproject(touchPos);
            mySprite.setCenterX(touchPos.x);
        }

        //enemy movement
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            enemySprite.translateX(enemySpeed * delta);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            enemySprite.translateX(-enemySpeed * delta);
        }

    }

    private void logic() {
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        float myWidth = mySprite.getWidth();
        float myHeight = mySprite.getHeight();

        float enemyWidth = enemySprite.getWidth();
        float enemyHeight = enemySprite.getHeight();

        float delta = Gdx.graphics.getDeltaTime();

        mySprite.setX(MathUtils.clamp(mySprite.getX(), 0, worldWidth - myWidth));
        myRectangle.set(mySprite.getX(), mySprite.getY(), myWidth, myHeight);

        enemySprite.setX(MathUtils.clamp(enemySprite.getX(), 0, worldWidth - myWidth));
        enemyRectangle.set(enemySprite.getX(), enemySprite.getY(), enemyWidth, enemyHeight);

        for (int i = dropSprites.size - 1; i >= 0; i--) {
            Sprite dropSprite = dropSprites.get(i);
            float dropWidth = dropSprite.getWidth();
            float dropHeight = dropSprite.getHeight();

            dropSprite.translateY(-2f * delta);
            dropRectangle.set(dropSprite.getX(), dropSprite.getY(), dropWidth, dropHeight);

            if (dropSprite.getY() < -dropHeight) dropSprites.removeIndex(i);
            else if (myRectangle.overlaps(dropRectangle)) {
                myHP -= enemyDMG;
                dropSprites.removeIndex(i);
                dropSound.play();
            }
        }

        dropTimer += delta;
        if (dropTimer > 1f) {
            dropTimer = 0;
            createDroplet();
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.batch.begin();

        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        game.batch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
        mySprite.draw(game.batch);
        enemySprite.draw(game.batch);


        game.font.draw(game.batch, "myHP: " + myHP +  "\nenemyHP: " + enemyHP, 0, worldHeight);

        for (Sprite dropSprite : dropSprites) {
            dropSprite.draw(game.batch);
        }

        game.batch.end();
    }

    private void createDroplet() {
        float dropWidth = 1;
        float dropHeight = 1;
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        Sprite dropSprite = new Sprite(dropTexture);
        dropSprite.setSize(dropWidth, dropHeight);
        dropSprite.setX(enemySprite.getX());
        dropSprite.setY(enemySprite.getY());
        dropSprites.add(dropSprite);
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        dropSound.dispose();
        music.dispose();
        dropTexture.dispose();
        myTexture.dispose();
        enemyTexture.dispose();
    }
}
