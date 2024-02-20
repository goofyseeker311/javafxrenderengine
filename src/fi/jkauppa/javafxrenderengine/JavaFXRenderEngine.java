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
	private Sphere sphere = null;
	private double translatex = 0.0d, translatey = 0.0d;
	
    @Override public void init() {
    }
    
    @Override public void start(Stage primaryStagei) throws Exception {
    	this.primaryStage = primaryStagei;
    	this.primaryStage.setTitle("JavaFXRenderEngine v0.0.5");
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
        this.sphere = new Sphere();
        PhongMaterial spheremat = new PhongMaterial();
        spheremat.setDiffuseColor(Color.RED);
        PhongMaterial trianglemat = new PhongMaterial();
        trianglemat.setDiffuseColor(Color.BLUE);
        this.sphere.setMaterial(spheremat);
        trianglemeshview.setMaterial(trianglemat);
        root.getChildren().add(this.sphere);
        root.getChildren().add(trianglemeshview);
        this.sphere.setTranslateZ(10);
        this.sphere.setTranslateY(this.translatey);
        this.sphere.setTranslateX(this.translatex);
        
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
			if (event.getCode().equals(KeyCode.RIGHT)) {
				this.translatex += 0.1d;
		        this.sphere.setTranslateX(this.translatex);
			}
			if (event.getCode().equals(KeyCode.LEFT)) {
				this.translatex -= 0.1d;
		        this.sphere.setTranslateX(this.translatex);
			}
			if (event.getCode().equals(KeyCode.UP)) {
				this.translatey -= 0.1d;
		        this.sphere.setTranslateY(this.translatey);
			}
			if (event.getCode().equals(KeyCode.DOWN)) {
				this.translatey += 0.1d;
		        this.sphere.setTranslateY(this.translatey);
			}
		}
	}
}
