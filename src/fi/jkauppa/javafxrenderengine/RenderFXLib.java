package fi.jkauppa.javafxrenderengine;

import java.awt.Rectangle;

import fi.jkauppa.javarenderengine.MathLib;
import fi.jkauppa.javarenderengine.RenderLib;
import fi.jkauppa.javarenderengine.ModelLib.Coordinate;
import fi.jkauppa.javarenderengine.ModelLib.Cubemap;
import fi.jkauppa.javarenderengine.ModelLib.Direction;
import fi.jkauppa.javarenderengine.ModelLib.Entity;
import fi.jkauppa.javarenderengine.ModelLib.Matrix;
import fi.jkauppa.javarenderengine.ModelLib.Plane;
import fi.jkauppa.javarenderengine.ModelLib.Position;
import fi.jkauppa.javarenderengine.ModelLib.RenderView;
import fi.jkauppa.javarenderengine.ModelLib.Triangle;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;
import javafx.scene.transform.Affine;

public class RenderFXLib {
	public static class RenderMeshView extends MeshView {
		public Entity swent;
		public Triangle swtri;
	}
	
	public static void constructFXScene(Group root, Entity[] entitylist, boolean unlit) {
		for (int k=0;k<entitylist.length;k++) {
			Entity[] ent = {entitylist[k]};
			for (int j=0;j<ent[0].trianglelist.length;j++) {
				Triangle[] tri = {ent[0].trianglelist[j]};
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
				tri[0].hwent = null;
				tri[0].hwtri = trimeshview;
				trimeshview.swent = ent[0];
				trimeshview.swtri = tri[0];
				trimeshview.setMesh(trimesh);
				trimeshview.setCullFace(CullFace.NONE);
				PhongMaterial trimat = new PhongMaterial();
				WritableImage diffusemap = null;
				if (tri[0].mat.fileimage!=null) {
					diffusemap = new WritableImage(tri[0].mat.fileimage.getWidth(), tri[0].mat.fileimage.getHeight());
				}
				if ((diffusemap==null)&&(tri[0].mat.ambientfileimage!=null)) {
					diffusemap = new WritableImage(tri[0].mat.ambientfileimage.getWidth(), tri[0].mat.ambientfileimage.getHeight());
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

	public static Affine matrixAffine(Matrix vmat) {
		return Affine.affine(vmat.a11, vmat.a12, vmat.a13, 0, vmat.a21, vmat.a22, vmat.a23, 0, vmat.a31, vmat.a32, vmat.a33, 0);		
	}
	
	public static RenderView renderProjectedView(Position campos, Group root, int renderwidth, double hfov, int renderheight, double vfov, Matrix viewrot, int bounces, Plane nclipplane, Triangle nodrawtriangle, Rectangle drawrange, int mouselocationx, int mouselocationy) {
		RenderView renderview = new RenderView();
		renderview.pos = campos.copy();
		renderview.rot = viewrot.copy();
		renderview.renderwidth = renderwidth;
		renderview.renderheight = renderheight;
		renderview.hfov = hfov;
		renderview.vfov = MathLib.calculateVfov(renderview.renderwidth, renderview.renderheight, renderview.hfov);
		renderview.mouselocationx = mouselocationx;
		renderview.mouselocationy = mouselocationy;
		renderview.dirs = MathLib.projectedCameraDirections(renderview.rot);
		Scene scene = new Scene(root, renderwidth, renderheight, true, SceneAntialiasing.BALANCED);
		PerspectiveCamera camera = new PerspectiveCamera(true);
		camera.setFarClip(1000000.0f);
		camera.setVerticalFieldOfView(false);
		camera.setFieldOfView(renderview.hfov);
		camera.getTransforms().clear();
		camera.setTranslateX(renderview.pos.x);
		camera.setTranslateY(renderview.pos.y);
		camera.setTranslateZ(renderview.pos.z);
		Affine transform = RenderFXLib.matrixAffine(renderview.rot);
		camera.getTransforms().add(transform);
		scene.setFill(Paint.valueOf("BLACK"));
		scene.setCamera(camera);
		WritableImage renderimage = scene.snapshot(null);
		renderview.renderimageobject = renderimage;
		scene.setRoot(new Group());
		return renderview;
	}

	public static RenderView renderCubemapView(Position campos, Group root, int renderwidth, int renderheight, int rendersize, Matrix viewrot, int bounces, Plane nclipplane, Triangle nodrawtriangle, Rectangle drawrange, int mouselocationx, int mouselocationy) {
		RenderView renderview = new RenderView();
		renderview.pos = campos.copy();
		renderview.rot = viewrot.copy();
		renderview.renderwidth = renderwidth;
		renderview.renderheight = renderheight;
		renderview.rendersize = rendersize;
		renderview.hfov = 90.0f;
		renderview.vfov = 90.0f;
		renderview.mouselocationx = mouselocationx;
		renderview.mouselocationy = mouselocationy;
		int renderposx1start = 0;
		int renderposx2start = rendersize;
		int renderposx3start = 2*rendersize;
		int renderposy1start = 0;
		int renderposy2start = rendersize;
		Matrix topmatrix = MathLib.rotationMatrix(-180.0f, 0.0f, 0.0f);
		Matrix bottommatrix = MathLib.rotationMatrix(0.0f, 0.0f, 0.0f);
		Matrix forwardmatrix = MathLib.rotationMatrix(-90.0f, 0.0f, 0.0f);
		Matrix rightmatrix = MathLib.matrixMultiply(MathLib.rotationMatrix(0.0f, 0.0f, 90.0f), forwardmatrix);
		Matrix backwardmatrix = MathLib.matrixMultiply(MathLib.rotationMatrix(0.0f, 0.0f, 180.0f), forwardmatrix);
		Matrix leftmatrix = MathLib.matrixMultiply(MathLib.rotationMatrix(0.0f, 0.0f, 270.0f), forwardmatrix);
		topmatrix = MathLib.matrixMultiply(renderview.rot, topmatrix);
		bottommatrix = MathLib.matrixMultiply(renderview.rot, bottommatrix);
		forwardmatrix = MathLib.matrixMultiply(renderview.rot, forwardmatrix);
		rightmatrix = MathLib.matrixMultiply(renderview.rot, rightmatrix);
		backwardmatrix = MathLib.matrixMultiply(renderview.rot, backwardmatrix);
		leftmatrix = MathLib.matrixMultiply(renderview.rot, leftmatrix);
		renderview.cubemap = new Cubemap();
		renderview.cubemap.topview = renderProjectedView(renderview.pos, root, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, topmatrix, bounces, nclipplane, nodrawtriangle, drawrange, mouselocationx, mouselocationy);
		renderview.cubemap.bottomview = renderProjectedView(renderview.pos, root, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, bottommatrix, bounces, nclipplane, nodrawtriangle, drawrange, mouselocationx, mouselocationy); 
		renderview.cubemap.forwardview = renderProjectedView(renderview.pos, root, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, forwardmatrix, bounces, nclipplane, nodrawtriangle, drawrange, mouselocationx, mouselocationy);
		renderview.cubemap.rightview = renderProjectedView(renderview.pos, root, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, rightmatrix, bounces, nclipplane, nodrawtriangle, drawrange, mouselocationx, mouselocationy);
		renderview.cubemap.backwardview = renderProjectedView(renderview.pos, root, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, backwardmatrix, bounces, nclipplane, nodrawtriangle, drawrange, mouselocationx, mouselocationy);
		renderview.cubemap.leftview = renderProjectedView(renderview.pos, root, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, leftmatrix, bounces, nclipplane, nodrawtriangle, drawrange, mouselocationx, mouselocationy);
		Canvas renderimage = new Canvas(renderwidth, renderheight);
		GraphicsContext rigfx = renderimage.getGraphicsContext2D();
		rigfx.setFill(new Color(0.0f,0.0f,0.0f,0.0f));
		rigfx.fillRect(0, 0, renderwidth, renderheight);
		rigfx.drawImage((WritableImage)renderview.cubemap.forwardview.renderimageobject, renderposx1start, renderposy1start, rendersize, rendersize);
		rigfx.drawImage((WritableImage)renderview.cubemap.backwardview.renderimageobject, renderposx2start, renderposy1start, rendersize, rendersize);
		rigfx.drawImage((WritableImage)renderview.cubemap.topview.renderimageobject, renderposx3start, renderposy1start, rendersize, rendersize);
		rigfx.drawImage((WritableImage)renderview.cubemap.leftview.renderimageobject, renderposx1start, renderposy2start, rendersize, rendersize);
		rigfx.drawImage((WritableImage)renderview.cubemap.bottomview.renderimageobject, renderposx2start, renderposy2start, rendersize, rendersize);
		rigfx.drawImage((WritableImage)renderview.cubemap.rightview.renderimageobject, renderposx3start, renderposy2start, rendersize, rendersize);
		renderview.renderimageobject = renderimage.snapshot(null, null);
		return renderview;
	}

}
