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
	private DrawFXApp drawapp = new DrawFXApp(root);
	private CADFXApp cadapp = new CADFXApp(root);
	private ModelFXApp modelapp = new ModelFXApp(root);
	private EditorFXApp editorapp = new EditorFXApp(root);
	private GameFXApp gameapp = new GameFXApp(root);
	private AppFXHandler activeapp = null;
	private BufferedImage logoimage = UtilLib.loadImage("res/icons/logo.png", true);
	private WritableImage logowimage = SwingFXUtils.toFXImage(logoimage, null);
	
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
    	this.primaryStage.setTitle("JavaFXRenderEngine v0.1.8");
    	this.primaryStage.getIcons().add(logowimage);
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
    	public Group root = null;
    	public Scene scene = null;
    	public Group entities = new Group();
    	public int renderwidth = 0;
    	public int renderheight = 0;
    	public long lastanimtick = System.currentTimeMillis();
    	public long nowanimtick = this.lastanimtick;
		public double diffanimtick = (double)(this.nowanimtick - this.lastanimtick);
		public double diffanimticksec = this.diffanimtick/1000.0f;
    	public long nowpulsetime = System.currentTimeMillis();
    	public long lastpulsetime = this.nowpulsetime;
		public double diffpulsetime = (double)(this.nowpulsetime - this.lastpulsetime);
		public double diffpulsetimesec = this.diffpulsetime/1000.0f;
    	public Clipboard cb = Clipboard.getSystemClipboard();
    	public AppFXHandler() {}
    	public void animtick() {
			this.lastanimtick = this.nowanimtick;
			this.nowanimtick = System.currentTimeMillis();
			this.diffanimtick = (double)(this.nowanimtick - this.lastanimtick);
			this.diffanimticksec = this.diffanimtick/1000.0f;
    	}
    	public void pulsetick() {
    		this.lastpulsetime = this.nowpulsetime;
    		this.nowpulsetime = System.currentTimeMillis();
    		this.diffpulsetime = (double)(this.nowpulsetime - this.lastpulsetime);
    		this.diffpulsetimesec = this.diffpulsetime/1000.0f;
    	}
    	public abstract void update();
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
			activeapp.animtick();
			activeapp.update();
		}
	}

	@Override public void run() {
		activeapp.pulsetick();
		activeapp.pulse();
		Platform.requestNextPulse();
	}
}
