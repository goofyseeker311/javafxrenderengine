package fi.jkauppa.javafxrenderengine;

import fi.jkauppa.javafxrenderengine.JavaFXRenderEngine.AppFXHandler;
import javafx.event.Event;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.ParallelCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Paint;

public class GameFXApp extends AppFXHandler {
	private ParallelCamera camera = new ParallelCamera();
	private Scene scene = null;
	private int renderwidth = 0;
	private int renderheight = 0;
	public GameFXApp() {}
	@Override public void update(Group root) {
		this.scene = root.getScene();
		this.renderwidth = (int)this.scene.getWidth();
		this.renderheight = (int)this.scene.getHeight();
		this.scene.setCursor(Cursor.DEFAULT);
		this.scene.setCamera(camera);
		this.scene.setFill(Paint.valueOf("RED"));
		root.getChildren().clear();
		System.out.println("this.renderwidth="+this.renderwidth+" this.renderheight="+this.renderheight);
	}
	@Override public void pulse() {}
	@Override public void handle(Event event) {}
}
