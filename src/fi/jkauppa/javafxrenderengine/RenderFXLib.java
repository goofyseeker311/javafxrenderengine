package fi.jkauppa.javafxrenderengine;

import java.awt.Rectangle;

import fi.jkauppa.javarenderengine.MathLib;
import fi.jkauppa.javarenderengine.RenderLib;
import fi.jkauppa.javarenderengine.ModelLib.Axis;
import fi.jkauppa.javarenderengine.ModelLib.Coordinate;
import fi.jkauppa.javarenderengine.ModelLib.Cubemap;
import fi.jkauppa.javarenderengine.ModelLib.Direction;
import fi.jkauppa.javarenderengine.ModelLib.Entity;
import fi.jkauppa.javarenderengine.ModelLib.Line;
import fi.jkauppa.javarenderengine.ModelLib.Material;
import fi.jkauppa.javarenderengine.ModelLib.Matrix;
import fi.jkauppa.javarenderengine.ModelLib.Plane;
import fi.jkauppa.javarenderengine.ModelLib.Position;
import fi.jkauppa.javarenderengine.ModelLib.RenderView;
import fi.jkauppa.javarenderengine.ModelLib.Sphere;
import fi.jkauppa.javarenderengine.ModelLib.Triangle;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;

public class RenderFXLib {
	public static class RenderMeshView extends MeshView {
		public Entity swent;
		public Triangle swtri;
	}
	public static class RenderCylinder extends Cylinder {
		public RenderCylinder(double radius, double height, int divisions) {
			super(radius, height, divisions);
		}
		public Entity swent;
		public Line swline;
	}

	public static Group constructLineFXScene(Group root, Line[] linelist) {
		Direction[] linedirlist = MathLib.vectorFromPoints(linelist);
		Position[] lineposlist  = MathLib.linePosition(linelist);
		Plane[] lineplanelist = MathLib.planeFromNormalAtPoint(lineposlist, linedirlist);
		Axis[] linevecs = MathLib.planeVectors(lineplanelist);
		for (int j=0;j<linelist.length;j++) {
			Line[] vline = {linelist[j]};
			Axis[] vlineaxis = {linevecs[j]};
			Position[] tripos = {vline[0].pos1, vline[0].pos2};
			double linewidth = 1.0f;
			double halfwidth = linewidth/2.0f;
			Position[] tripos1 = MathLib.translate(MathLib.translate(tripos, vlineaxis[0].rgt, -halfwidth), vlineaxis[0].up, halfwidth);
			Position[] tripos2 = MathLib.translate(MathLib.translate(tripos, vlineaxis[0].rgt, halfwidth), vlineaxis[0].up, halfwidth);
			Position[] tripos3 = MathLib.translate(MathLib.translate(tripos, vlineaxis[0].rgt, halfwidth), vlineaxis[0].up, -halfwidth);
			Position[] tripos4 = MathLib.translate(MathLib.translate(tripos, vlineaxis[0].rgt, -halfwidth), vlineaxis[0].up, -halfwidth);
			Triangle[] tri = {
					new Triangle(tripos1[0],tripos2[0],tripos1[1]), new Triangle(tripos2[1],tripos2[0],tripos1[1]),
					new Triangle(tripos2[0],tripos3[0],tripos2[1]), new Triangle(tripos3[1],tripos3[0],tripos2[1]),
					new Triangle(tripos3[0],tripos4[0],tripos3[1]), new Triangle(tripos4[1],tripos4[0],tripos3[1]),
					new Triangle(tripos4[0],tripos1[0],tripos4[1]), new Triangle(tripos1[1],tripos1[0],tripos4[1]),
					};
			Direction[] trinorm = {vlineaxis[0].up, vlineaxis[0].rgt, vlineaxis[0].up.invert(), vlineaxis[0].rgt.invert()};
			float[] tripoints = {
					(float)tri[0].pos1.x, (float)tri[0].pos1.y, (float)tri[0].pos1.z, (float)tri[0].pos2.x, (float)tri[0].pos2.y, (float)tri[0].pos2.z, (float)tri[0].pos3.x, (float)tri[0].pos3.y, (float)tri[0].pos3.z,
					(float)tri[1].pos1.x, (float)tri[1].pos1.y, (float)tri[1].pos1.z, (float)tri[1].pos2.x, (float)tri[1].pos2.y, (float)tri[1].pos2.z, (float)tri[1].pos3.x, (float)tri[1].pos3.y, (float)tri[1].pos3.z,
					(float)tri[2].pos1.x, (float)tri[2].pos1.y, (float)tri[2].pos1.z, (float)tri[2].pos2.x, (float)tri[2].pos2.y, (float)tri[2].pos2.z, (float)tri[2].pos3.x, (float)tri[2].pos3.y, (float)tri[2].pos3.z,
					(float)tri[3].pos1.x, (float)tri[3].pos1.y, (float)tri[3].pos1.z, (float)tri[3].pos2.x, (float)tri[3].pos2.y, (float)tri[3].pos2.z, (float)tri[3].pos3.x, (float)tri[3].pos3.y, (float)tri[3].pos3.z,
					(float)tri[4].pos1.x, (float)tri[4].pos1.y, (float)tri[4].pos1.z, (float)tri[4].pos2.x, (float)tri[4].pos2.y, (float)tri[4].pos2.z, (float)tri[4].pos3.x, (float)tri[4].pos3.y, (float)tri[4].pos3.z,
					(float)tri[5].pos1.x, (float)tri[5].pos1.y, (float)tri[5].pos1.z, (float)tri[5].pos2.x, (float)tri[5].pos2.y, (float)tri[5].pos2.z, (float)tri[5].pos3.x, (float)tri[5].pos3.y, (float)tri[5].pos3.z,
					(float)tri[6].pos1.x, (float)tri[6].pos1.y, (float)tri[6].pos1.z, (float)tri[6].pos2.x, (float)tri[6].pos2.y, (float)tri[6].pos2.z, (float)tri[6].pos3.x, (float)tri[6].pos3.y, (float)tri[6].pos3.z,
					(float)tri[7].pos1.x, (float)tri[7].pos1.y, (float)tri[7].pos1.z, (float)tri[7].pos2.x, (float)tri[7].pos2.y, (float)tri[7].pos2.z, (float)tri[7].pos3.x, (float)tri[7].pos3.y, (float)tri[7].pos3.z,
					};
			float[] tricoords = {(float)tri[0].pos1.tex.u,1.0f-(float)tri[0].pos1.tex.v,(float)tri[0].pos2.tex.u,1.0f-(float)tri[0].pos2.tex.v,(float)tri[0].pos3.tex.u,1.0f-(float)tri[0].pos3.tex.v};
			float[] trinorms = {
					(float)trinorm[0].dx, (float)trinorm[0].dy, (float)trinorm[0].dz,
					(float)trinorm[1].dx, (float)trinorm[1].dy, (float)trinorm[1].dz,
					(float)trinorm[2].dx, (float)trinorm[2].dy, (float)trinorm[2].dz,
					(float)trinorm[3].dx, (float)trinorm[3].dy, (float)trinorm[3].dz,
					};
			int[] trifacenorm = {
					0, 0, 0, 1, 0, 1, 2, 0, 2,
					3, 0, 0, 4, 0, 1, 5, 0, 2,
					6, 1, 0, 7, 1, 1, 8, 1, 2,
					9, 1, 0, 10, 1, 1, 11, 1, 2,
					12, 2, 0, 13, 2, 1, 14, 2, 2,
					15, 2, 0, 16, 2, 1, 17, 2, 2,
					18, 3, 0, 19, 3, 1, 20, 3, 2,
					21, 3, 0, 22, 3, 1, 23, 3, 2,
					};
			TriangleMesh trimesh = new TriangleMesh(VertexFormat.POINT_NORMAL_TEXCOORD);
			trimesh.getPoints().addAll(tripoints);
			trimesh.getTexCoords().addAll(tricoords);
			trimesh.getNormals().addAll(trinorms);
			trimesh.getFaces().addAll(trifacenorm);
			RenderMeshView trimeshview = new RenderMeshView();
			tri[0].hwent = null;
			tri[0].hwtri = trimeshview;
			trimeshview.swent = null;
			trimeshview.swtri = tri[0];
			trimeshview.setMesh(trimesh);
			trimeshview.setCullFace(CullFace.NONE);
			PhongMaterial trimat = new PhongMaterial();
			trimat.setDiffuseColor(Color.BLUE);
			trimeshview.setMaterial(trimat);
			root.getChildren().add(trimeshview);
		}
		return root;
	}
	
	public static Group constructTriangleFXScene(Group root, Entity[] entitylist, boolean unlit) {
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
		return root;
	}

	public static Affine matrixAffine(Matrix vmat) {
		Affine transform = Affine.affine(vmat.a11, vmat.a12, vmat.a13, 0, vmat.a21, vmat.a22, vmat.a23, 0, vmat.a31, vmat.a32, vmat.a33, 0);
		transform.append(new Rotate(180.0f,new Point3D(1.0f,0.0f,0.0f)));
		return transform;		
	}
	public static java.awt.Color awtColor(Color color) {
		return new java.awt.Color((float)color.getRed(),(float)color.getGreen(),(float)color.getBlue(),(float)color.getOpacity());
	}
	public static Color fxColor(java.awt.Color color) {
		float[] colorcomp = color.getRGBComponents(null);
		return new Color(colorcomp[0],colorcomp[1],colorcomp[2],colorcomp[3]);
	}
	
	public static RenderView renderProjectedView(Position campos, Entity[] entitylist, Group root, int renderwidth, double hfov, int renderheight, double vfov, Matrix viewrot, int bounces, double nclipdist, Triangle nodrawtriangle, Rectangle drawrange, int mouselocationx, int mouselocationy) {
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
		scene.setFill(Color.TRANSPARENT);
		scene.setCamera(camera);
		WritableImage renderimage = scene.snapshot(null);
		scene.setRoot(new Group());
		renderview.renderimageobject = renderimage;
		return renderview;
	}

	public static RenderView renderCubemapView(Position campos, Entity[] entitylist, Group root, int renderwidth, int renderheight, int rendersize, Matrix viewrot, int bounces, double nclipdist, Triangle nodrawtriangle, Rectangle drawrange, int mouselocationx, int mouselocationy) {
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
		renderview.cubemap.topview = renderProjectedView(renderview.pos, entitylist, root, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, topmatrix, bounces, nclipdist, nodrawtriangle, drawrange, mouselocationx, mouselocationy);
		renderview.cubemap.bottomview = renderProjectedView(renderview.pos, entitylist, root, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, bottommatrix, bounces, nclipdist, nodrawtriangle, drawrange, mouselocationx, mouselocationy); 
		renderview.cubemap.forwardview = renderProjectedView(renderview.pos, entitylist, root, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, forwardmatrix, bounces, nclipdist, nodrawtriangle, drawrange, mouselocationx, mouselocationy);
		renderview.cubemap.rightview = renderProjectedView(renderview.pos, entitylist, root, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, rightmatrix, bounces, nclipdist, nodrawtriangle, drawrange, mouselocationx, mouselocationy);
		renderview.cubemap.backwardview = renderProjectedView(renderview.pos, entitylist, root, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, backwardmatrix, bounces, nclipdist, nodrawtriangle, drawrange, mouselocationx, mouselocationy);
		renderview.cubemap.leftview = renderProjectedView(renderview.pos, entitylist, root, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, leftmatrix, bounces, nclipdist, nodrawtriangle, drawrange, mouselocationx, mouselocationy);
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
		SnapshotParameters snap = new SnapshotParameters();
		snap.setFill(Color.TRANSPARENT);
		renderview.renderimageobject = renderimage.snapshot(snap, null);
		return renderview;
	}

	public static void renderSurfaceFaceLightmapCubemapView(Entity[] entitylist, Group root, int rendersize, int bounces) {
		float multiplier = 1000.0f;
		Direction[][] cubemaprays = MathLib.projectedRays(rendersize, rendersize, 90.0f, 90.0f, MathLib.rotationMatrix(0.0f, 0.0f, 0.0f), false);
		double[][] cubemapraylen = new double[rendersize][rendersize];
		for (int i=0;i<cubemaprays.length;i++) {
			cubemapraylen[i] = MathLib.vectorLength(cubemaprays[i]);
		}
		int lightbounces = (bounces>0)?bounces:1;
		for (int l=0;l<lightbounces;l++) {
			if (entitylist!=null) {
				for (int j=0;j<entitylist.length;j++) {
					if (entitylist[j]!=null) {
						if (entitylist[j].trianglelist!=null) {
							Sphere[] trianglespherelist = MathLib.triangleInSphere(entitylist[j].trianglelist);
							for (int i=0;i<entitylist[j].trianglelist.length;i++) {
								if (entitylist[j].trianglelist[i]!=null) {
									Sphere[] trianglesphere = {trianglespherelist[i]};
									Position[] trianglespherepoint = MathLib.sphereVertexList(trianglesphere);
									RenderView p4pixelview = renderCubemapView(trianglespherepoint[0], entitylist, root, rendersize*3, rendersize*2, rendersize, MathLib.rotationMatrix(0, 0, 0), bounces, 0, entitylist[j].trianglelist[i], null, 0, 0);
									RenderView[] cubemapviews = new RenderView[6];
									cubemapviews[0] = p4pixelview.cubemap.backwardview;
									cubemapviews[1] = p4pixelview.cubemap.bottomview;
									cubemapviews[2] = p4pixelview.cubemap.forwardview;
									cubemapviews[3] = p4pixelview.cubemap.leftview;
									cubemapviews[4] = p4pixelview.cubemap.rightview;
									cubemapviews[5] = p4pixelview.cubemap.topview;
									float p4pixelr = 0.0f;
									float p4pixelg = 0.0f;
									float p4pixelb = 0.0f;
									float pixelcount = 6*rendersize*rendersize;
									if (entitylist[j].trianglelist[i].mat.emissivecolor!=null) {
										float[] triangleemissivecolorcomp = entitylist[j].trianglelist[i].mat.emissivecolor.getRGBComponents(new float[4]);
										p4pixelr = triangleemissivecolorcomp[0]*pixelcount;
										p4pixelg = triangleemissivecolorcomp[1]*pixelcount;
										p4pixelb = triangleemissivecolorcomp[2]*pixelcount;
									}
									for (int k=0;k<cubemapviews.length;k++) {
										WritableImage renderimage = (WritableImage)cubemapviews[k].renderimageobject;
										PixelReader pixelreader = renderimage.getPixelReader();
										for (int ky=0;ky<renderimage.getHeight();ky++) {
											for (int kx=0;kx<renderimage.getWidth();kx++) {
												Color p4pixelcolor = pixelreader.getColor(kx, ky);
												double[] p4pixelcolorcomp = {p4pixelcolor.getRed(), p4pixelcolor.getGreen(), p4pixelcolor.getBlue(), p4pixelcolor.getOpacity()};
												p4pixelr += p4pixelcolorcomp[0]/cubemapraylen[ky][kx];
												p4pixelg += p4pixelcolorcomp[1]/cubemapraylen[ky][kx];
												p4pixelb += p4pixelcolorcomp[2]/cubemapraylen[ky][kx];
											}
										}
									}
									float p4pixelrt = multiplier*p4pixelr/(float)Math.pow(pixelcount,l+1);
									float p4pixelgt = multiplier*p4pixelg/(float)Math.pow(pixelcount,l+1);
									float p4pixelbt = multiplier*p4pixelb/(float)Math.pow(pixelcount,l+1);
									if (p4pixelrt>1.0f) {p4pixelrt=1.0f;}
									if (p4pixelgt>1.0f) {p4pixelgt=1.0f;}
									if (p4pixelbt>1.0f) {p4pixelbt=1.0f;}
									Color p4pixelcolor = new Color(p4pixelrt, p4pixelgt, p4pixelbt, 1.0f);
									System.out.println("RenderLib: renderSurfaceFaceLightmapCubemapView: bounce["+(l+1)+"] entitylist["+(j+1)+"/"+entitylist.length+"]["+(i+1)+"/"+entitylist[j].trianglelist.length+"]="+trianglespherepoint[0].x+","+trianglespherepoint[0].y+","+trianglespherepoint[0].z);
									if ((entitylist[j].trianglelist[i].lmatl==null)||(entitylist[j].trianglelist[i].lmatl.length!=lightbounces)) {
										entitylist[j].trianglelist[i].lmatl = new Material[lightbounces];
									}
									entitylist[j].trianglelist[i].lmatl[l] = new Material(awtColor(p4pixelcolor), 1.0f, null);
								}
							}
						}
					}
				}
			}
			if (entitylist!=null) {
				for (int j=0;j<entitylist.length;j++) {
					if (entitylist[j]!=null) {
						if (entitylist[j].trianglelist!=null) {
							for (int i=0;i<entitylist[j].trianglelist.length;i++) {
								if (entitylist[j].trianglelist[i]!=null) {
									if ((entitylist[j].trianglelist[i].mat!=null)&&(entitylist[j].trianglelist[i].lmatl!=null)) {
										entitylist[j].trianglelist[i].mat = entitylist[j].trianglelist[i].mat.copy();
										if (entitylist[j].trianglelist[i].mat.ambientcolor==null) {
											entitylist[j].trianglelist[i].mat.ambientcolor = java.awt.Color.BLACK;
										}
										float[] ambcolorcomp = entitylist[j].trianglelist[i].mat.ambientcolor.getRGBComponents(new float[4]);
										float[] lightcolorcomp = entitylist[j].trianglelist[i].lmatl[l].facecolor.getRGBComponents(new float[4]);
										float newr = ambcolorcomp[0]+lightcolorcomp[0];
										float newg = ambcolorcomp[1]+lightcolorcomp[1];
										float newb = ambcolorcomp[2]+lightcolorcomp[2];
										if (newr>1.0f) {newr=1.0f;}
										if (newg>1.0f) {newg=1.0f;}
										if (newb>1.0f) {newb=1.0f;}
										entitylist[j].trianglelist[i].mat.ambientcolor = new java.awt.Color(newr,newg,newb,1.0f);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
}
