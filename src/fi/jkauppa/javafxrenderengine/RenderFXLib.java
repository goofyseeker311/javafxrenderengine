package fi.jkauppa.javafxrenderengine;

import fi.jkauppa.javarenderengine.RenderLib;
import fi.jkauppa.javarenderengine.ModelLib.Coordinate;
import fi.jkauppa.javarenderengine.ModelLib.Direction;
import fi.jkauppa.javarenderengine.ModelLib.Entity;
import fi.jkauppa.javarenderengine.ModelLib.Triangle;
import javafx.scene.Group;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;

public class RenderFXLib {
	public static class RenderMeshView extends MeshView {
		public Triangle swent;
	}
	
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
			RenderMeshView trimeshview = new RenderMeshView();
			tri[0].hwent = trimeshview;
			trimeshview.swent = tri[0];
			trimeshview.setMesh(trimesh);
			trimeshview.setCullFace(CullFace.NONE);
			PhongMaterial trimat = new PhongMaterial();
			WritableImage diffusemap = null;
			if (tri[0].mat.fileimage!=null) {
				diffusemap = new WritableImage(tri[0].mat.fileimage.getWidth(), tri[0].mat.fileimage.getHeight());
			}
			if ((diffusemap==null)&&(tri[0].mat.emissivefileimage!=null)) {
				diffusemap = new WritableImage(tri[0].mat.emissivefileimage.getWidth(), tri[0].mat.emissivefileimage.getHeight());
			}
			if (diffusemap==null) {
				java.awt.Color trianglecolor = RenderLib.trianglePixelShader(tri[0], null, null, null, unlit);
				float[] trianglecolorcomp = trianglecolor.getRGBComponents(new float[4]);
				Color shadertrianglecolor = new Color(trianglecolorcomp[0], trianglecolorcomp[1], trianglecolorcomp[2], trianglecolorcomp[3]);
				trimat.setDiffuseColor(shadertrianglecolor);
			} else {
				PixelWriter diffusemapwriter = diffusemap.getPixelWriter();
				for (int y=0;y<diffusemap.getHeight();y++) {
					for (int x=0;x<diffusemap.getWidth();x++) {
						double ucoord = ((double)x)/((double)(diffusemap.getWidth()-1));
						double vcoord = ((double)y)/((double)(diffusemap.getHeight()-1));
						Coordinate texuv = new Coordinate(ucoord, vcoord);
						java.awt.Color trianglecolor = RenderLib.trianglePixelShader(tri[0], null, texuv, null, unlit);
						if (trianglecolor!=null) {
							float[] trianglecolorcomp = trianglecolor.getRGBComponents(new float[4]);
							Color shadertrianglecolor = new Color(trianglecolorcomp[0], trianglecolorcomp[1], trianglecolorcomp[2], trianglecolorcomp[3]);
							diffusemapwriter.setColor(x, y, shadertrianglecolor);
						} else {
							diffusemapwriter.setColor(x, y, Color.TRANSPARENT);
						}
					}
				}
				trimat.setDiffuseMap(diffusemap);
			}
			trimeshview.setMaterial(trimat);
			root.getChildren().add(trimeshview);
		}
	}
}
