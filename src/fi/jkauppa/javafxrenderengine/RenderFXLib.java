package fi.jkauppa.javafxrenderengine;

import fi.jkauppa.javarenderengine.ModelLib.Direction;
import fi.jkauppa.javarenderengine.ModelLib.Entity;
import fi.jkauppa.javarenderengine.ModelLib.Triangle;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;

public class RenderFXLib {
	public static void constructFXScene(Group root, Entity[] entitylist, boolean unlit) {
		for (int k=0;k<entitylist.length;k++) {
			constructFXTriangle(root, entitylist[k].trianglelist, unlit);
		}
	}
	
	public static void constructFXTriangle(Group root, Triangle[] vtri, boolean unlit) {
		for (int i=0;i<vtri.length;i++) {
			Triangle[] tri = {vtri[i]};
			Direction[] trinorm = {tri[0].norm};
			float[] tripoints = {(float)tri[0].pos1.x, (float)tri[0].pos1.y, (float)tri[0].pos1.z, (float)tri[0].pos2.x, (float)tri[0].pos2.y, (float)tri[0].pos2.z, (float)tri[0].pos3.x, (float)tri[0].pos3.y, (float)tri[0].pos3.z};
			float[] tricoords = {(float)tri[0].pos1.tex.u,1.0f-(float)tri[0].pos1.tex.v,(float)tri[0].pos2.tex.u,1.0f-(float)tri[0].pos2.tex.v,(float)tri[0].pos3.tex.u,1.0f-(float)tri[0].pos3.tex.v};
			float[] trinorms = {(float)trinorm[0].dx, (float)trinorm[0].dy, (float)trinorm[0].dz};
			int[] trifacenorm = {0, 0, 0, 1, 0, 1, 2, 0, 2};
			TriangleMesh trimesh = new TriangleMesh(VertexFormat.POINT_NORMAL_TEXCOORD);
			trimesh.getPoints().addAll(tripoints);
			trimesh.getTexCoords().addAll(tricoords);
			trimesh.getNormals().addAll(trinorms);
			trimesh.getFaces().addAll(trifacenorm);
			MeshView trimeshview = new MeshView();
			trimeshview.setMesh(trimesh);
			trimeshview.setCullFace(CullFace.NONE);
			PhongMaterial trimat = new PhongMaterial();
			if (tri[0].mat!=null) {
				float[] tricolorcomp = tri[0].mat.facecolor.getRGBComponents(new float[4]);
				Color tricolor = new Color(tricolorcomp[0], tricolorcomp[1], tricolorcomp[2], tricolorcomp[3]);
				trimat.setDiffuseColor(tricolor);
				if ((tri[0].mat.emissivecolor!=null)||(tri[0].mat.emissivefileimage!=null)) {
					float multiplier = 10.0f;
					WritableImage emissivemap = null;
					if (tri[0].mat.emissivefileimage!=null) {
						emissivemap = SwingFXUtils.toFXImage(tri[0].mat.emissivefileimage, null);
					} else {
						float[] emissivecolorcomp = tri[0].mat.emissivecolor.getRGBComponents(new float[4]);
						float[] boostedcolor = {multiplier*emissivecolorcomp[0], multiplier*emissivecolorcomp[1], multiplier*emissivecolorcomp[2], multiplier*emissivecolorcomp[3]};
						if (boostedcolor[0]>1.0f) {boostedcolor[0]=1.0f;}
						if (boostedcolor[1]>1.0f) {boostedcolor[1]=1.0f;}
						if (boostedcolor[2]>1.0f) {boostedcolor[2]=1.0f;}
						if (boostedcolor[3]>1.0f) {boostedcolor[3]=1.0f;}
						Color emissivecolor = new Color(boostedcolor[0],boostedcolor[1],boostedcolor[2],boostedcolor[3]);
						emissivemap = new WritableImage(1, 1);
						emissivemap.getPixelWriter().setColor(0, 0, emissivecolor);
					}
					trimat.setSelfIlluminationMap(emissivemap);
				}
				if (tri[0].mat.fileimage!=null) {
					WritableImage tridifimg = SwingFXUtils.toFXImage(tri[0].mat.fileimage, null);
					trimat.setDiffuseMap(tridifimg);
				}
			}
			trimeshview.setMaterial(trimat);
			root.getChildren().add(trimeshview);
		}
	}
}
