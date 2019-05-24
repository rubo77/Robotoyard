package roboyard.eclabs;

import android.graphics.Color;

/**
 * Created by Pierre on 21/01/2015.
 */
public class GameOptionsGameScreen extends GameScreen {


    public GameOptionsGameScreen(GameManager gameManager){
        super(gameManager);
    }

    @Override
    public void create() {
        int buttonSize=440;
        float ratioW = ((float)gameManager.getScreenWidth()) /((float)1080);
        float ratioH = ((float)gameManager.getScreenHeight()) /((float)1920);
        int relativeButtonWidth=(int)(ratioW*(float)buttonSize);
        int ws2 = (int)(((float)this.gameManager.getScreenWidth()-relativeButtonWidth)/2);

        // screen 4: start random game
        this.instances.add(new GameButtonGotoRandomGame(ws2, (int) (ratioH * 200), relativeButtonWidth, (int)(ratioH * buttonSize), R.drawable.bt_start_up_random, R.drawable.bt_start_down_random, 4));
        // screen 5: start level game
        this.instances.add(new GameButtonGoto(ws2, (int)(ratioH * 750), relativeButtonWidth, (int)(ratioH * buttonSize), R.drawable.bt_start_up, R.drawable.bt_start_down, 5));
        // screen 9: save games
        this.instances.add(new GameButtonGoto(ws2, (int)(ratioH * 1300), relativeButtonWidth, (int)(ratioH * buttonSize), R.drawable.bt_start_up_saved, R.drawable.bt_start_down_saved, 9));
    }

    @Override
    public void load(RenderManager renderManager) {
        super.load(renderManager);
    }

    @Override
    public void draw(RenderManager renderManager) {
//        renderManager.setColor(Color.GREEN);
//        renderManager.setColor(Color.parseColor("#FFAE07"));
        renderManager.setColor(Color.parseColor("#E3C898"));
        renderManager.paintScreen();
        super.draw(renderManager);
    }

    @Override
    public void update(GameManager gameManager) {
        super.update(gameManager);
        if(gameManager.getInputManager().backOccurred()){
            gameManager.setGameScreen(0);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
