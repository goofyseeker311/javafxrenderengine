package fi.jkauppa.javafxrenderengine;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.stage.Stage;

public class JavaFXRenderEngine extends Application implements EventHandler<KeyEvent> {
	private Stage primaryStage = null;
	
    @Override public void init() {
    }
    
    @Override public void start(Stage primaryStagei) throws Exception {
    	this.primaryStage = primaryStagei;
    	this.primaryStage.setTitle("JavaFXRenderEngine v0.0.4");
    	this.primaryStage.setFullScreen(true);
    	this.primaryStage.addEventHandler(KeyEvent.ANY, this);
        
        Group root = new Group();
        Scene scene = new Scene(root, 400, 300);
        scene.setFill(Color.LIGHTGRAY);
        this.primaryStage.setScene(scene);
        TriangleMesh triangle = new TriangleMesh();
        float[] tripoints = {-1.0f,0.0f,5.0f,0.0f,1.0f,5.0f,0.0f,-1.0f,5.0f};
        float[] triuvs = {0.0f,0.0f,1.0f,0.0f,1.0f,1.0f};
        int[] trifaceind = {0, 0, 1, 1, 2, 2};
        triangle.getPoints().addAll(tripoints);
        triangle.getTexCoords().addAll(triuvs);
        triangle.getFaces().addAll(trifaceind);
        MeshView trianglemeshview =  new MeshView();
        trianglemeshview.setMesh(triangle);
        PerspectiveCamera camera = new PerspectiveCamera(true);
        scene.setCamera(camera);
        Sphere sphere = new Sphere();
        PhongMaterial spheremat = new PhongMaterial();
        spheremat.setDiffuseColor(Color.RED);
        PhongMaterial trianglemat = new PhongMaterial();
        trianglemat.setDiffuseColor(Color.BLUE);
        sphere.setMaterial(spheremat);
        trianglemeshview.setMaterial(trianglemat);
        root.getChildren().add(sphere);
        root.getChildren().add(trianglemeshview);
        sphere.setTranslateZ(10);
        sphere.setTranslateY(2);
        
        this.primaryStage.show();
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
}
