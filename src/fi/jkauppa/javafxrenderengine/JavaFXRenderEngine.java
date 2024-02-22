package fi.jkauppa.javafxrenderengine;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.UIManager;

import fi.jkauppa.javarenderengine.UtilLib;
import fi.jkauppa.javarenderengine.UtilLib.ImageFileFilters.PNGFileFilter;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;

public class JavaFXRenderEngine extends Application implements Runnable,EventHandler<Event> {
	private int defaultimagecanvaswidth = 1920;
	private int defaultimagecanvasheight = 1080;
	private Stage primaryStage = null;
	private Group root = new Group();
	public Scene scene = new Scene(root, defaultimagecanvaswidth, defaultimagecanvasheight, true, SceneAntialiasing.BALANCED);
	private FrameTick frametick = new FrameTick();
	private DrawFXApp drawapp = new DrawFXApp();
	private CADFXApp cadapp = new CADFXApp();
	private ModelFXApp modelapp = new ModelFXApp();
	private EditorFXApp editorapp = new EditorFXApp();
	private GameFXApp gameapp = new GameFXApp();
	private AppFXHandler activeapp = null;
	
    public static void main(String[] args) {
		System.setProperty("sun.java2d.opengl", "true");
		String userdir = System.getProperty("user.dir");
		System.out.println("JavaRenderEngine: main: userdir="+userdir);
		try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());} catch (Exception ex) {}
    	launch(args);
    }
    @Override public void init() {}
    @Override public void stop() throws Exception {}
    
    @Override public void start(Stage primaryStagei) throws Exception {
    	this.primaryStage = primaryStagei;
    	this.primaryStage.setTitle("JavaFXRenderEngine v0.1.5");
    	this.primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
    	this.primaryStage.setFullScreenExitHint("");
    	this.scene.addEventHandler(KeyEvent.ANY, this);
    	this.scene.addEventHandler(MouseEvent.ANY, this);
    	this.scene.addEventHandler(ScrollEvent.ANY, this);
    	this.scene.addEventHandler(DragEvent.ANY, this);
        this.scene.addPreLayoutPulseListener(this);
        this.primaryStage.setScene(this.scene);
        this.setActiveApp(this.drawapp);
        this.primaryStage.show();
        frametick.start();
        this.primaryStage.requestFocus();
    }

    public static abstract class AppFXHandler implements EventHandler<Event> {
    	public long lasttick = System.currentTimeMillis();
    	public long newtick = this.lasttick;
    	public Clipboard cb = Clipboard.getSystemClipboard();
    	public void tick() {
			this.lasttick = this.newtick;
			this.newtick = System.currentTimeMillis();
    	}
    	public abstract void update(Group root);
    	public abstract void pulse();
    }

	private void setActiveApp(AppFXHandler activeappi) {
		this.activeapp = activeappi;
	}
    
	@Override public void handle(Event event) {
		if (event.getEventType().equals(KeyEvent.KEY_PRESSED)) {
			KeyEvent keyevent = (KeyEvent)event;
			if ((keyevent.getCode().equals(KeyCode.ENTER))&&(keyevent.isAltDown())&&(!keyevent.isControlDown())&&(!keyevent.isShiftDown())&&(!keyevent.isMetaDown())) {
				this.primaryStage.setFullScreen(!this.primaryStage.isFullScreen());
				keyevent.consume();
			} else if (keyevent.getCode().equals(KeyCode.F5)) {
				this.setActiveApp(this.drawapp);
			} else if (keyevent.getCode().equals(KeyCode.F6)) {
				this.setActiveApp(this.cadapp);
			} else if (keyevent.getCode().equals(KeyCode.F7)) {
				this.setActiveApp(this.modelapp);
			} else if (keyevent.getCode().equals(KeyCode.F8)) {
				this.setActiveApp(this.editorapp);
			} else if (keyevent.getCode().equals(KeyCode.F9)) {
				this.setActiveApp(this.gameapp);
			} else if (keyevent.getCode().equals(KeyCode.F10)) {
				//TODO <tbd>
			} else if (keyevent.getCode().equals(KeyCode.F11)) {
				//TODO <tbd>
			} else if (keyevent.getCode().equals(KeyCode.F12)) {
				WritableImage writablescreenshot = this.scene.snapshot(null);
				BufferedImage screenshot = SwingFXUtils.fromFXImage(writablescreenshot, null);
				File screenshotfile = new File("screenshot1.png");
				int screenshotnum = 1;
				while (screenshotfile.exists()) {
					screenshotnum += 1;
					screenshotfile = new File("screenshot"+screenshotnum+".png");
				}
				UtilLib.saveImageFormat(screenshotfile.getPath(), screenshot, new PNGFileFilter());
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

	@Override public void run() {
		activeapp.pulse();
		Platform.requestNextPulse();
	}
}
