package fi.jkauppa.javafxrenderengine;

import fi.jkauppa.javafxrenderengine.JavaFXRenderEngine.AppFXHandler;
import javafx.event.Event;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Paint;

public class EditorFXApp extends AppFXHandler {
	public EditorFXApp() {}
	@Override public void update(Group root) {
		Scene scene = root.getScene();
		scene.setCursor(Cursor.DEFAULT);
		scene.setFill(Paint.valueOf("LIGHTGRAY"));
		root.getChildren().clear();
	}
	@Override public void pulse() {}
	@Override public void handle(Event event) {}
}
