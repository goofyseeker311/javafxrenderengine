package fi.jkauppa.javafxrenderengine;


import fi.jkauppa.javarenderengine.ModelLib.Entity;
import fi.jkauppa.javarenderengine.ModelLib.Triangle;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;

public class RenderFXLib {
	public static void constructFXScene(Group root, Entity[] entitylist) {
		for (int k=0;k<entitylist.length;k++) {
			constructFXTriangle(root, entitylist[k].trianglelist);
		}
	}
	
	public static void constructFXTriangle(Group root, Triangle[] vtri) {
		for (int i=0;i<vtri.length;i++) {
			Triangle tri = vtri[i];
			float[] tripoints = {(float)tri.pos1.x, (float)tri.pos1.y, (float)tri.pos1.z, (float)tri.pos2.x, (float)tri.pos2.y, (float)tri.pos2.z, (float)tri.pos3.x, (float)tri.pos3.y, (float)tri.pos3.z};
			float[] tricoords = {(float)tri.pos1.tex.u,1.0f-(float)tri.pos1.tex.v,(float)tri.pos2.tex.u,1.0f-(float)tri.pos2.tex.v,(float)tri.pos3.tex.u,1.0f-(float)tri.pos3.tex.v};
			float[] trinorms = {(float)tri.norm.dx, (float)tri.norm.dy, (float)tri.norm.dz};
			int[] triface = {0, 0, 0, 1, 0, 1, 2, 0, 2};
			TriangleMesh trimesh = new TriangleMesh(VertexFormat.POINT_NORMAL_TEXCOORD);
			trimesh.getPoints().addAll(tripoints);
			trimesh.getTexCoords().addAll(tricoords);
			trimesh.getNormals().addAll(trinorms);
			trimesh.getFaces().addAll(triface);
			MeshView trimeshview = new MeshView();
			trimeshview.setMesh(trimesh);
			PhongMaterial trimat = new PhongMaterial();
			if (tri.mat!=null) {
				float[] tricolorcomp = tri.mat.facecolor.getRGBComponents(new float[4]);
				Color tricolor = new Color(tricolorcomp[0], tricolorcomp[1], tricolorcomp[2], tricolorcomp[3]);
				trimat.setDiffuseColor(tricolor);
				if (tri.mat.fileimage!=null) {
					WritableImage tridifimg = SwingFXUtils.toFXImage(tri.mat.fileimage, null);
					trimat.setDiffuseMap(tridifimg);
				}
			}
			trimeshview.setMaterial(trimat);
			root.getChildren().add(trimeshview);
		}
	}
}
