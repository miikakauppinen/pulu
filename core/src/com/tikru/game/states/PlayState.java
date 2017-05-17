package com.tikru.game.states;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.tikru.game.Pulu;
import com.tikru.game.sprites.Bird;
import com.tikru.game.sprites.Tube;
import com.badlogic.gdx.math.Vector2;


public class PlayState extends State {

    private static final int TUBE_SPACING = 125;
    private static final int TUBE_COUNT = 4;
    private static final int GROUND_Y_OFFSET = -50;

    private Bird pulu;
    private Texture bg;
    private Texture ground;
    private Vector2 groundPos1, groundPos2;

    private Array<Tube> tubes;

    //Luodaan pelikenttä, piirretään siihen tekstruurit ja luodaan tuubeille lista ja for käymään listaa läpi
    public PlayState(GameStateMngr gsm) {
        super(gsm);
        pulu = new Bird(50, 300);
        cam.setToOrtho(false, Pulu.WIDTH / 2, Pulu.HEIGHT / 2);
        bg = new Texture("bg.png");
        ground = new Texture("ground.png");
        groundPos1 = new Vector2((cam.position.x - cam.viewportWidth / 2), GROUND_Y_OFFSET);
        groundPos2 = new Vector2((cam.position.x - cam.viewportWidth / 2) +  ground.getWidth(), GROUND_Y_OFFSET);
        tubes = new Array<Tube>();

        for(int i = 1; i <= TUBE_COUNT; i++){
            tubes.add(new Tube(i * (TUBE_SPACING + Tube.TUBE_WIDTH)));
        }
    }

    //Käsitellään käyttäjältä saatua palautetta, tässä tapauksessa pulun hyppimistä hiiren klikkaisten mukaan
    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched()){
            pulu.jump();
        }

    }

    //Päivitetään pelikenttä, pulu liikkuu koko ajan eteenpäin joten kenttääkin on piirrettävä sitä mukaa
    @Override
    public void update(float dt) {
        handleInput();
        updateGround();
        pulu.update(dt);
        cam.position.x = pulu.getPosition().x + 80;

        for(int i = 0; i < tubes.size; i++){
            Tube tube = tubes.get(i);
            if(cam.position.x - (cam.viewportWidth / 2) > tube.getPosTopTube().x + tube.getTopTube().getWidth()){
                tube.reposition(tube.getPosTopTube().x + ((Tube.TUBE_WIDTH + TUBE_SPACING) * TUBE_COUNT));
            }

            if(tube.collides(pulu.getBounds()))
                gsm.set(new PlayState(gsm));
        }

        if(pulu.getPosition().y <= ground.getHeight() + GROUND_Y_OFFSET)
            gsm.set(new PlayState(gsm));
        cam.update();

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(bg, cam.position.x - (cam.viewportWidth / 2), 0);
        sb.draw(pulu.getTexture(), pulu.getPosition().x, pulu.getPosition().y);
        for(Tube tube : tubes) {
            sb.draw(tube.getTopTube(), tube.getPosTopTube().x, tube.getPosTopTube().y);
            sb.draw(tube.getBottomTube(), tube.getPosBottomTube().x, tube.getPosBottomTube().y);
        }

        sb.draw(ground, groundPos1.x, groundPos1.y);
        sb.draw(ground, groundPos2.x, groundPos2.y);
        sb.end();

    }

    //Hävitetään roskat
    @Override
    public void dispose() {
        bg.dispose();
        pulu.dispose();
        ground.dispose();
        for(Tube tube : tubes){
            tube.dispose();
        }
        System.out.println("Play state disposed");
    }

    private void updateGround(){
        if(cam.position.x - (cam.viewportWidth / 2) > groundPos1.x + ground.getWidth())
            groundPos1.add(ground.getWidth() * 2, 0);
        if(cam.position.x - (cam.viewportWidth / 2) > groundPos2.x + ground.getWidth())
            groundPos2.add(ground.getWidth() * 2, 0);
    }
}
