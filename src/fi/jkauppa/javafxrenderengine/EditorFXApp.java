package fi.jkauppa.javafxrenderengine;

import fi.jkauppa.javafxrenderengine.JavaFXRenderEngine.AppFXHandler;
import javafx.event.Event;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Paint;

public class EditorFXApp extends AppFXHandler {
	private Group root = null;
	private Scene scene = null;
	public EditorFXApp(Group root) {
		this.root = root;
		this.scene = root.getScene();
	}
	@Override public void update() {
		this.renderwidth = (int)this.scene.getWidth();
		this.renderheight = (int)this.scene.getHeight();
		this.scene.setCursor(Cursor.DEFAULT);
		this.scene.setFill(Paint.valueOf("LIGHTGRAY"));
		root.getChildren().clear();
	}
	@Override public void tick() {}
	@Override public void pulse() {}
	@Override public void handle(Event event) {}
}
