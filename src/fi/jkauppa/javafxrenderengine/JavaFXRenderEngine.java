package fi.jkauppa.javafxrenderengine;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class JavaFXRenderEngine extends Application implements EventHandler<Event> {
	private final int defaultimagecanvaswidth = 1920;
	private final int defaultimagecanvasheight= 1080;
	private Stage primaryStage = null;
	private Group root = new Group();
	private Scene scene = new Scene(root, defaultimagecanvaswidth, defaultimagecanvasheight, true, SceneAntialiasing.BALANCED);
	private FrameTick frametick = new FrameTick();
	private DrawFXApp drawapp = new DrawFXApp();
	private CADFXApp cadapp = new CADFXApp();
	private ModelFXApp modelapp = new ModelFXApp();
	private EditorFXApp editorapp = new EditorFXApp();
	private GameFXApp gameapp = new GameFXApp();
	private AppFXHandler activeapp = null;
	
    public static void main(String[] args) {launch(args);}
    @Override public void init() {}
    @Override public void stop() throws Exception {}
    
    @Override public void start(Stage primaryStagei) throws Exception {
    	this.primaryStage = primaryStagei;
    	this.primaryStage.setTitle("JavaFXRenderEngine v0.1.0");
    	this.primaryStage.addEventHandler(KeyEvent.ANY, this);
        this.primaryStage.setScene(this.scene);
        this.setActiveApp(this.drawapp);
        this.primaryStage.show();
        frametick.start();
    }

    public static abstract class AppFXHandler implements EventHandler<Event> {
    	public long lasttick = System.currentTimeMillis();
    	public long newtick = this.lasttick;
    	public void tick() {
			this.lasttick = this.newtick;
			this.newtick = System.currentTimeMillis();
    	}
    	public abstract void update(Group root);
    }

	private void setActiveApp(AppFXHandler activeappi) {
		this.activeapp = activeappi;
	}
    
	@Override public void handle(Event event) {
		if (event.getEventType().equals(KeyEvent.KEY_PRESSED)) {
			KeyEvent keyevent = (KeyEvent)event;
			if ((keyevent.getCode().equals(KeyCode.ENTER))&&(keyevent.isAltDown())) {
				this.primaryStage.setFullScreen(!this.primaryStage.isFullScreen());
				keyevent.consume();
			} else if (keyevent.getCode().equals(KeyCode.F5)) {
				this.setActiveApp(this.drawapp);
				keyevent.consume();
			} else if (keyevent.getCode().equals(KeyCode.F6)) {
				this.setActiveApp(this.cadapp);
				keyevent.consume();
			} else if (keyevent.getCode().equals(KeyCode.F7)) {
				this.setActiveApp(this.modelapp);
				keyevent.consume();
			} else if (keyevent.getCode().equals(KeyCode.F8)) {
				this.setActiveApp(this.editorapp);
				keyevent.consume();
			} else if (keyevent.getCode().equals(KeyCode.F9)) {
				this.setActiveApp(this.gameapp);
				keyevent.consume();
			} else if (keyevent.getCode().equals(KeyCode.F10)) {
				//TODO <tbd>
				keyevent.consume();
			} else if (keyevent.getCode().equals(KeyCode.F11)) {
				//TODO <tbd>
				keyevent.consume();
			} else if (keyevent.getCode().equals(KeyCode.F12)) {
				WritableImage writablescreenshot = this.scene.snapshot(null);
				BufferedImage screenshot = SwingFXUtils.fromFXImage(writablescreenshot, null);
				File screenshotfile = new File("screenshot1.png");
				int screenshotnum = 1;
				while (screenshotfile.exists()) {
					screenshotnum += 1;
					screenshotfile = new File("screenshot"+screenshotnum+".png");
				}
				try {
					ImageIO.write(screenshot, "PNG", screenshotfile);
				} catch (Exception ex) {ex.printStackTrace();}
				keyevent.consume();
			} else {
				activeapp.handle(event);
			}
		} else {
			activeapp.handle(event);
		}
	}
	
	private class FrameTick extends AnimationTimer {
		@Override public void handle(long now) {
			activeapp.tick();
			activeapp.update(root);
		}
	}
}
