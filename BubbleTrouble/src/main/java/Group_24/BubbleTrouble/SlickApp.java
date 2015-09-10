package Group_24.BubbleTrouble;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.util.ResourceLoader;

import java.awt.Font;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application running the game.
 *
 */
@SuppressWarnings("serial")
public class SlickApp extends BasicGame
{
    private Image background;
    private Timers timers;
    private Rectangle timerBar;
    private TrueTypeFont font;

	public SlickApp(String gamename) {
        super(gamename);
    }

    public void init(GameContainer gc) throws SlickException {
        Controller.startNewGame(gc);
        background = new Image("Sprites/background.jpg");
        timers = Controller.getTimers();
        timerBar = new Rectangle(200, gc.getHeight() - 113 , gc.getWidth() - 400, 25);
        timers.restartTimer();

        // load font from a .ttf file
        try {
            InputStream inputStream	= ResourceLoader.getResourceAsStream("Sprites/Hetilica.ttf");

            Font awtFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtFont = awtFont.deriveFont(24f); // set font size
            font = new TrueTypeFont(awtFont, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(GameContainer gc, int delta) throws SlickException {
        if(!timers.getCountdownRunning()) {
            Controller.update();
            for (Player player : Model.getPlayers()) {
                player.move(gc, delta);
            }
        }
    }

    public void render(GameContainer gc, Graphics g) throws SlickException {
        g.setFont(font);
        background.draw(0, 0);
        // Draw countdown timer
        if(timers.getCountdownRunning()) {
            g.drawString("Game starts in " + (timers.getCountdownTimeLeft() / 1000) + " seconds",
                    gc.getWidth() / 2 - 200, gc.getHeight() / 2 - 100);
        }

        // Draw timer progress bar
        g.setColor(Color.red);
        g.fillRect(timerBar.getX(), timerBar.getY(),
                (int)(timerBar.getWidth() * timers.getLevelTimeLeft() / timers.getLevelMaxDuration()),
                timerBar.getHeight());
        g.setColor(Color.lightGray );
        g.draw(timerBar);

        for(Player player: Model.getPlayers()) {
            player.draw();
            g.setColor(Color.red);
            g.drawString("[Score " + player.getScore() + ", Lives " + player.getLives() + "]", 30, 30);
        }
        for(Bubble bubble: Model.getBubbles()) {
            g.setAntiAlias(true);
            g.setColor(Color.black);
            g.fill(bubble);
            g.draw(bubble);
        }
//        for(Rectangle wall: Model.getCurrentRoom().getWalls()) {
//            g.setColor(Color.green);
//            g.fill(wall);
//            g.draw(wall);
//        }
//        for(Rectangle floor: Model.getCurrentRoom().getFloors()) {
//            g.setColor(Color.blue);
//            g.fill(floor);
//            g.draw(floor);
//        }
    }

    public static void main(String[] args) {
        try
        {
            AppGameContainer appgc;
            appgc = new AppGameContainer(new SlickApp("Bubble Trouble"));
            appgc.setDisplayMode(1123, 921, false);
            appgc.setShowFPS(false);
            appgc.setVSync(true);
            appgc.setTargetFrameRate(60);
            appgc.setAlwaysRender(true);
            appgc.start();
        }
        catch (SlickException ex)
        {
            Logger.getLogger(SlickApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}