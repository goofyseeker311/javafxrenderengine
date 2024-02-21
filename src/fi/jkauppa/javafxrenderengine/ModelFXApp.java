package fi.jkauppa.javafxrenderengine;

import java.util.Random;

import fi.jkauppa.javafxrenderengine.JavaFXRenderEngine.AppFXHandler;
import javafx.event.Event;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

public class ModelFXApp extends AppFXHandler {
	private PerspectiveCamera camera = new PerspectiveCamera(true);
	private Random random = new Random();
	
	@Override public void update(Group root) {
		Scene scene = root.getScene();
		scene.setCamera(camera);
		scene.setFill(Paint.valueOf("BLACK"));
		root.getChildren().clear();
		Sphere sphere = new Sphere();
        sphere.setTranslateZ(10.0d);
        sphere.setTranslateX(-2.0d);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.RED);
        sphere.setMaterial(material);
		int randomx = this.random.nextInt(0, 5);
		sphere.setTranslateX(-20+Math.floorMod(this.newtick+randomx, 40L));
        root.getChildren().add(sphere);
	}

	@Override public void handle(Event event) {}

}
