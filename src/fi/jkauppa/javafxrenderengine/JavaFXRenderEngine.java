package fi.jkauppa.javafxrenderengine;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.stage.Stage;

public class JavaFXRenderEngine extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Group root = new Group();
        primaryStage.setTitle("JavaFXRenderEngine v0.0.3");
        Scene scene = new Scene(root, 400, 300);
        scene.setFill(Color.LIGHTGRAY);
        primaryStage.setScene(scene);
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
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
