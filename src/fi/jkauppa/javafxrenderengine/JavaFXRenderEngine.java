package fi.jkauppa.javafxrenderengine;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class JavaFXRenderEngine extends Application implements Runnable, EventHandler<KeyEvent> {
	private Stage primaryStage = null;
	private Group root = new Group();
	private Scene scene = new Scene(this.root, 1920, 1080);
	private PerspectiveCamera camera = new PerspectiveCamera(true);
	private long lastpulse = System.currentTimeMillis();
	private long newpulse = this.lastpulse;
	private long deltatime = this.newpulse-this.lastpulse;
	private long lasttick = System.currentTimeMillis();
	private long newtick = this.lasttick;
	private long deltatick = this.newtick-this.lasttick;
	private FrameTick frametick = new FrameTick();
	private Text pulsetext = new Text("0ms");
	private Text ticktext = new Text("0ms");
	
    @Override public void init() {
    }
    
    @Override public void start(Stage primaryStagei) throws Exception {
    	this.primaryStage = primaryStagei;
    	this.primaryStage.setTitle("JavaFXRenderEngine v0.0.6");
    	this.primaryStage.addEventHandler(KeyEvent.ANY, this);
        this.primaryStage.setScene(this.scene);
        this.scene.setCamera(camera);
        this.scene.addPreLayoutPulseListener(this);
        frametick.start();
        this.primaryStage.show();

        Sphere sphere = new Sphere();
        sphere.setTranslateZ(10.0d);
        sphere.setTranslateX(-2.0d);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.RED);
        sphere.setMaterial(material);
        Font font = Font.font("Times new roman", 18);
        pulsetext.setFont(font);
        pulsetext.setTranslateZ(100);
        pulsetext.setTranslateX(-40);
        pulsetext.setTranslateY(-5);
        ticktext.setFont(font);
        ticktext.setTranslateZ(100);
        ticktext.setTranslateX(-40);
        ticktext.setTranslateY(15);
        this.root.getChildren().add(sphere);
        this.root.getChildren().add(pulsetext);
        this.root.getChildren().add(ticktext);
    }

    @Override public void stop() throws Exception {
    }
    
    public static void main(String[] args) {
        launch(args);
    }

	@Override
	public void handle(KeyEvent event) {
		if (event.getEventType().equals(KeyEvent.KEY_PRESSED)) {
			if (event.getCode().equals(KeyCode.ENTER)) {
				if (event.isAltDown()) {
					this.primaryStage.setFullScreen(!this.primaryStage.isFullScreen());
				}
			}
		}
	}

	@Override public void run() {
		this.lastpulse = this.newpulse;
		this.newpulse = System.currentTimeMillis();
		this.deltatime = this.newpulse-this.lastpulse;
		System.out.println("pulse run(): this.deltatime= "+this.deltatime+"ms");
		this.pulsetext.setText("pulse:"+this.deltatime+"ms");
		Platform.requestNextPulse();
	}
	
	private class FrameTick extends AnimationTimer {
		@Override public void handle(long now) {
			JavaFXRenderEngine.this.lasttick = JavaFXRenderEngine.this.newtick;
			JavaFXRenderEngine.this.newtick = System.currentTimeMillis();
			JavaFXRenderEngine.this.deltatick = JavaFXRenderEngine.this.newtick-JavaFXRenderEngine.this.lasttick;
			System.out.println("tick run(): this.deltatick= "+JavaFXRenderEngine.this.deltatick+"ms");
			JavaFXRenderEngine.this.ticktext.setText("tick:"+JavaFXRenderEngine.this.deltatick+"ms");
		}
	}
}
