package fi.jkauppa.javafxrenderengine;

import fi.jkauppa.javafxrenderengine.JavaFXRenderEngine.AppFXHandler;
import javafx.event.Event;
import javafx.scene.Group;

public class TemplateFXApp extends AppFXHandler {
	public TemplateFXApp(Group root) {
		this.root = root;
		this.scene = root.getScene();
	}
	@Override public void update() {}
	@Override public void pulse() {}
	@Override public void handle(Event event) {}
}
