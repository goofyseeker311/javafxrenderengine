package fi.jkauppa.javafxrenderengine;

import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;

public class JavaFXRenderEngine extends Application implements EventHandler<KeyEvent> {
	private Stage primaryStage = null;
	private Group root = new Group();
	private Scene scene = new Scene(this.root, 1920, 1080);
	private PerspectiveCamera camera = new PerspectiveCamera(true);
	private long lasttick = System.currentTimeMillis();
	private long newtick = this.lasttick;
	private FrameTick frametick = new FrameTick();
	private Sphere sphere = new Sphere();
	private Random random = new Random();
	
    public static void main(String[] args) {launch(args);}
    @Override public void init() {}
    
    @Override public void start(Stage primaryStagei) throws Exception {
    	this.primaryStage = primaryStagei;
    	this.primaryStage.setTitle("JavaFXRenderEngine v0.0.7");
    	this.primaryStage.addEventHandler(KeyEvent.ANY, this);
        this.primaryStage.setScene(this.scene);
        this.scene.setCamera(camera);
        this.primaryStage.show();
        frametick.start();

        sphere.setTranslateZ(10.0d);
        sphere.setTranslateX(-2.0d);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.RED);
        sphere.setMaterial(material);
        this.root.getChildren().add(sphere);
    }

    @Override public void stop() throws Exception {}

	@Override public void handle(KeyEvent event) {
		if (event.getEventType().equals(KeyEvent.KEY_PRESSED)) {
			if (event.getCode().equals(KeyCode.ENTER)) {
				if (event.isAltDown()) {
					this.primaryStage.setFullScreen(!this.primaryStage.isFullScreen());
				}
			}
		}
	}
	
	private class FrameTick extends AnimationTimer {
		@Override public void handle(long now) {
			lasttick = newtick;
			newtick = System.currentTimeMillis();
			int randomx = random.nextInt(0, 5);
			sphere.setTranslateX(-20+Math.floorMod(newtick+randomx, 40L));
		}
	}
}
