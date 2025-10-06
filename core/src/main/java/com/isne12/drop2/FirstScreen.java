package com.isne12.drop2;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class FirstScreen implements Screen {
    final Main game;

    Texture backgroundTexture;
    Animation<TextureRegion> heroAnimation;
    float stateTime;
    Animation<TextureRegion> badAnimation;
    enum WeaponType{
        fan1,fan2
    }
    WeaponType enemyWeapon;
    Texture fan1Texture;
    Texture fan2Texture;
    Sprite weaponSprite; //Spite to show weapon
    Array<Sprite> enemyWeaponProjectiles;
    float enemyWeaponTimer = 0f;

    Sound dropSound;
    Music music;

    Sprite mySprite;
    Sprite enemySprite;

    Vector2 touchPos;
    float myDropTimer;

    Rectangle myRectangle;
    Rectangle enemyRectangle;

    Rectangle dropRectangle;
    Rectangle myDropRectangle;

    int myHP = 10;
    int myDMG = 1;
    int enemyHP = 20;
    int enemyDMG = 2;
    float weaponChangeTimer = 0f;
    public FirstScreen(final Main game) {
        this.game = game;

        // load the images for the background, bucket and droplet
        backgroundTexture = new Texture("background.png");
        //myTexture = new Texture("Hero.gif");
        TextureRegion[] heroFrames=new TextureRegion[5];
        heroFrames[0]=new TextureRegion(new Texture("HeroFight1.png"));
        heroFrames[1]=new TextureRegion(new Texture("HeroFight2.png"));
        heroFrames[2]=new TextureRegion(new Texture("HeroFight3.png"));
        heroFrames[3]=new TextureRegion(new Texture("HeroFight4.png"));
        heroFrames[4]=new TextureRegion(new Texture("HeroFight5.png"));
        heroAnimation=new Animation<TextureRegion>(0.7f,heroFrames);
        heroAnimation.setPlayMode(Animation.PlayMode.LOOP);
        stateTime=0f; //start when 0 sec
        //enemyTexture = new Texture("Bad.gif");
        TextureRegion[] badFrames=new TextureRegion[5];
        badFrames[0]=new TextureRegion(new Texture("BadPlay1.png"));
        badFrames[1]=new TextureRegion(new Texture("BadPlay2.png"));
        badFrames[2]=new TextureRegion(new Texture("BadPlay3.png"));
        badFrames[3]=new TextureRegion(new Texture("BadPlay4.png"));
        badFrames[4]=new TextureRegion(new Texture("BadPlay5.png"));
        badAnimation=new Animation<>(0.7f,badFrames);
        badAnimation.setPlayMode(Animation.PlayMode.LOOP);
        stateTime=0f;
        //dropTexture = new Texture("drop.png");
        fan1Texture=new Texture("badW1.png");
        fan2Texture=new Texture("badW2.png");
        //random weapon
        enemyWeapon=MathUtils.randomBoolean()?WeaponType.fan1:WeaponType.fan2;
        if(enemyWeapon==WeaponType.fan1){
            weaponSprite=new Sprite(fan1Texture);
        }else{
            weaponSprite=new Sprite(fan2Texture);
        }
        weaponSprite.setSize(1f,1f);
        enemyWeaponProjectiles = new Array<>();
        // load the drop sound effect and background music
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("satu99.mp3"));
        music.setLooping(true);
        music.setVolume(0.5F);
        TextureRegion firstFrame=heroAnimation.getKeyFrame(0);
        mySprite=new Sprite(firstFrame);
        //mySprite = new Sprite(myTexture);
        mySprite.setSize(1, 1);
        mySprite.setPosition(0,0);

        //enemySprite = new Sprite(enemyTexture);
        TextureRegion startFrame=badAnimation.getKeyFrame((0));
        enemySprite=new Sprite(startFrame);
        enemySprite.setSize(1, 1);
        float worldHeight = game.viewport.getWorldHeight();
        float enemyWidth = enemySprite.getWidth();
        enemySprite.setPosition(0, worldHeight-enemyWidth);

        touchPos = new Vector2();

        myRectangle = new Rectangle();
        enemyRectangle = new Rectangle();
        dropRectangle = new Rectangle();
        myDropRectangle = new Rectangle();
    }
    private void createEnemyWeaponProjectile() {
        Sprite proj = new Sprite(weaponSprite);   // clone รูปอาวุธปัจจุบัน
        proj.setSize(0.5f, 0.5f);                  // กำหนดขนาดอาวุธที่ยิง
        proj.setPosition(
            enemySprite.getX() + enemySprite.getWidth() / 2f - proj.getWidth() / 2f,
            enemySprite.getY()
        );
        enemyWeaponProjectiles.add(proj);
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

        //input mySprite shooting

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
        weaponSprite.setPosition(
            enemySprite.getX() + enemySprite.getWidth() / 2f - weaponSprite.getWidth() / 2f,
            enemySprite.getY() + enemySprite.getHeight() / 2f
        );
        weaponChangeTimer += delta;
        if (weaponChangeTimer >0.1f) {
            weaponChangeTimer = 0;
            enemyWeapon = MathUtils.randomBoolean() ? WeaponType.fan1 : WeaponType.fan2;
            if (enemyWeapon == WeaponType.fan1) {
                weaponSprite.setTexture(fan1Texture);
            } else {
                weaponSprite.setTexture(fan2Texture);
            }
        }
        enemyWeaponTimer += delta;
        if (enemyWeaponTimer > 1f) {
            enemyWeaponTimer = 0f;
            createEnemyWeaponProjectile();
        }
        for (int i = enemyWeaponProjectiles.size - 1; i >= 0; i--) {
            Sprite proj = enemyWeaponProjectiles.get(i);
            proj.translateY(-2f * delta);  // projectile เลื่อนลง (ศัตรูยิงลง)
            Rectangle projRect = new Rectangle(proj.getX(), proj.getY(), proj.getWidth(), proj.getHeight());

            if (proj.getY() < -proj.getHeight()) {
                enemyWeaponProjectiles.removeIndex(i); // ออกนอกจอลบ projectile
            } else if (myRectangle.overlaps(projRect)) {
                myHP -= enemyDMG;                         // player โดนโจมตี
                enemyWeaponProjectiles.removeIndex(i);
                dropSound.play();
            }
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
        //mySprite.draw(game.batch);
        stateTime+=Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame=heroAnimation.getKeyFrame(stateTime,true);
        //draw currentFrame instead mySpite
        game.batch.draw(currentFrame,mySprite.getX(),mySprite.getY(),mySprite.getWidth(),mySprite.getHeight());
        //enemySprite.draw(game.batch);
        stateTime+=Gdx.graphics.getDeltaTime();
        TextureRegion nowFrame=badAnimation.getKeyFrame(stateTime,true);
        game.batch.draw(nowFrame,enemySprite.getX(),enemySprite.getY(),enemySprite.getWidth(),enemySprite.getHeight());
        weaponSprite.draw(game.batch);

        game.font.draw(game.batch, "myHP: " + myHP +  "\nenemyHP: " + enemyHP, 0, worldHeight);
        for (Sprite proj : enemyWeaponProjectiles) {
            proj.draw(game.batch);
        }
        game.batch.end();

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
        //dropSound.dispose();
        music.dispose();
        fan1Texture.dispose();
        fan2Texture.dispose();
        //dropTexture.dispose();
        //myTexture.dispose();
        //enemyTexture.dispose();
    }
}
