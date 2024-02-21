package fi.jkauppa.javafxrenderengine;

import fi.jkauppa.javafxrenderengine.JavaFXRenderEngine.AppFXHandler;
import javafx.event.Event;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Paint;

public class GameFXApp extends AppFXHandler {
	@Override public void update(Group root) {
		Scene scene = root.getScene();
		scene.setFill(Paint.valueOf("RED"));
		root.getChildren().clear();
	}
	@Override public void handle(Event event) {}
}