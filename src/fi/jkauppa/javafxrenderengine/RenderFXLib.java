package fi.jkauppa.javafxrenderengine;

import java.awt.Rectangle;

import fi.jkauppa.javarenderengine.MathLib;
import fi.jkauppa.javarenderengine.ModelLib.Cube;
import fi.jkauppa.javarenderengine.ModelLib.Cubemap;
import fi.jkauppa.javarenderengine.ModelLib.Direction;
import fi.jkauppa.javarenderengine.ModelLib.Entity;
import fi.jkauppa.javarenderengine.ModelLib.Line;
import fi.jkauppa.javarenderengine.ModelLib.Material;
import fi.jkauppa.javarenderengine.ModelLib.Matrix;
import fi.jkauppa.javarenderengine.ModelLib.Position;
import fi.jkauppa.javarenderengine.ModelLib.RenderView;
import fi.jkauppa.javarenderengine.ModelLib.Sphere;
import fi.jkauppa.javarenderengine.ModelLib.Triangle;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;

public class RenderFXLib {
	public static class RenderMeshView extends MeshView {
		public Entity swent;
		public Triangle swtri;
		public Line swline;
	}
	public static class RenderSphere extends javafx.scene.shape.Sphere {
		public RenderSphere(double radius, int divisions) {super(radius, divisions);}
		public Entity swent;
		public Line swline;
		public Position swpos;
	}

	public static Group constructLineFXScene(Group root, Line[] linelist) {
		double linewidth = 1.0f;
		double halfwidth = linewidth/2.0f;
		Cube[] linecubelist = MathLib.lineCube(linelist, halfwidth, halfwidth);
		for (int j=0;j<linelist.length;j++) {
			Line[] vline = {linelist[j]};
			Position[] tripos = {vline[0].pos1, vline[0].pos2};
			Cube[] vlinecube = {linecubelist[j]};
			Triangle[] tri = MathLib.cubeTriangles(vlinecube[0]);
			TriangleMesh trimesh = constructTriangleMesh(tri);
			RenderMeshView trimeshview = new RenderMeshView();
			trimeshview.swent = null;
			trimeshview.swline = vline[0];
			trimeshview.setMesh(trimesh);
			trimeshview.setCullFace(CullFace.NONE);
			PhongMaterial trimat = new PhongMaterial();
			trimat.setDiffuseColor(Color.BLUE);
			trimeshview.setMaterial(trimat);
			root.getChildren().add(trimeshview);
			RenderSphere spherepos1 = new RenderSphere(5.0f*linewidth, 1);
			RenderSphere spherepos2 = new RenderSphere(5.0f*linewidth, 1);
			vline[0].hwent = null;
			vline[0].hwline = trimeshview;
			vline[0].pos1.hwent = null;
			vline[0].pos1.hwpos = spherepos1;
			vline[0].pos2.hwent = null;
			vline[0].pos2.hwpos = spherepos2;
			spherepos1.swent = null;
			spherepos1.swline = vline[0];
			spherepos1.swpos = tripos[0];
			spherepos2.swent = null;
			spherepos2.swline = vline[0];
			spherepos2.swpos = tripos[1];
			spherepos1.setTranslateX(tripos[0].x);
			spherepos1.setTranslateY(tripos[0].y);
			spherepos1.setTranslateZ(tripos[0].z);
			spherepos2.setTranslateX(tripos[1].x);
			spherepos2.setTranslateY(tripos[1].y);
			spherepos2.setTranslateZ(tripos[1].z);
			PhongMaterial spheremat = new PhongMaterial();
			spheremat.setDiffuseColor(Color.BLACK);
			spherepos1.setMaterial(spheremat);
			spherepos2.setMaterial(spheremat);
			root.getChildren().add(spherepos1);
			root.getChildren().add(spherepos2);
		}
		return root;
	}
	
	public static Group constructTriangleFXScene(Group root, Entity[] entitylist, boolean unlit) {
		for (int k=0;k<entitylist.length;k++) {
			Entity[] ent = {entitylist[k]};
			for (int j=0;j<ent[0].trianglelist.length;j++) {
				Triangle[] tri = {ent[0].trianglelist[j]};
				TriangleMesh trimesh = constructTriangleMesh(tri);
				RenderMeshView trimeshview = new RenderMeshView();
				tri[0].hwent = null;
				tri[0].hwtri = trimeshview;
				trimeshview.swent = ent[0];
				trimeshview.swline = null;
				trimeshview.swtri = tri[0];
				trimeshview.setMesh(trimesh);
				trimeshview.setCullFace(CullFace.NONE);
				PhongMaterial trimat = new PhongMaterial();
				float roughnessmult = 1.0f-tri[0].mat.roughness;
				Color trianglecolor = Color.WHITE;
				Color lightmapcolor = null;
				Color emissivecolor = null;
				WritableImage diffusemap = null;
				WritableImage emissivemap = null;
				if (tri[0].mat.facecolor!=null) {trianglecolor = RenderFXLib.fxColor(tri[0].mat.facecolor);}
				if (tri[0].mat.ambientcolor!=null) {lightmapcolor = RenderFXLib.fxColor(tri[0].mat.ambientcolor);}
				if (tri[0].mat.emissivecolor!=null) {emissivecolor = RenderFXLib.fxColor(tri[0].mat.emissivecolor);}
				if (tri[0].mat.fileimage!=null) {diffusemap = SwingFXUtils.toFXImage(tri[0].mat.fileimage, null);}
				if (tri[0].mat.emissivefileimage!=null) {emissivemap = SwingFXUtils.toFXImage(tri[0].mat.emissivefileimage, null);}
				SnapshotParameters snap = new SnapshotParameters();
				snap.setFill(Color.TRANSPARENT);
				if (unlit) {
					Color tricolor = Color.BLACK;
					if (lightmapcolor!=null) {
						double tricolorr = trianglecolor.getRed()*lightmapcolor.getRed()*roughnessmult;
						double tricolorg = trianglecolor.getGreen()*lightmapcolor.getGreen()*roughnessmult;
						double tricolorb = trianglecolor.getBlue()*lightmapcolor.getBlue()*roughnessmult;
						double tricolora = trianglecolor.getOpacity();
						if (tricolorr>1.0f) {tricolorr=1.0f;}
						if (tricolorg>1.0f) {tricolorg=1.0f;}
						if (tricolorb>1.0f) {tricolorb=1.0f;}
						if (tricolora>1.0f) {tricolora=1.0f;}
						tricolor = new Color(tricolorr,tricolorg,tricolorb,tricolora);
						if (diffusemap!=null) {
							Canvas diffusecanvas = new Canvas((int)diffusemap.getWidth(), (int)diffusemap.getHeight());
							GraphicsContext diffusecanvasgfx = diffusecanvas.getGraphicsContext2D();
							diffusecanvasgfx.clearRect(0, 0, (int)diffusemap.getWidth(), (int)diffusemap.getHeight());
							diffusecanvasgfx.setFill(lightmapcolor);
							diffusecanvasgfx.fillRect(0, 0, (int)diffusemap.getWidth(), (int)diffusemap.getHeight());
							Canvas roughnessmultcanvas = new Canvas((int)diffusemap.getWidth(), (int)diffusemap.getHeight());
							GraphicsContext roughnessmultcanvasgfx = roughnessmultcanvas.getGraphicsContext2D();
							roughnessmultcanvasgfx.setFill(new Color(roughnessmult,roughnessmult,roughnessmult,1.0f));
							roughnessmultcanvasgfx.fillRect(0, 0, (int)diffusemap.getWidth(), (int)diffusemap.getHeight());
							WritableImage roughnessimage = roughnessmultcanvas.snapshot(snap, null);
							diffusecanvasgfx.setGlobalBlendMode(BlendMode.MULTIPLY);
							diffusecanvasgfx.drawImage(diffusemap, 0, 0);
							diffusecanvasgfx.drawImage(roughnessimage, 0, 0);
							if (emissivemap!=null) {
								diffusecanvasgfx.setGlobalBlendMode(BlendMode.ADD);
								diffusecanvasgfx.drawImage(emissivemap, 0, 0, (int)diffusemap.getWidth(), (int)diffusemap.getHeight());
							} else if (emissivecolor!=null) {
								Canvas emissivecanvas = new Canvas((int)diffusemap.getWidth(), (int)diffusemap.getHeight());
								GraphicsContext emissivecanvasgfx = emissivecanvas.getGraphicsContext2D();
								emissivecanvasgfx.clearRect(0, 0, (int)diffusemap.getWidth(), (int)diffusemap.getHeight());
								emissivecanvasgfx.setFill(emissivecolor);
								emissivecanvasgfx.fillRect(0, 0, (int)diffusemap.getWidth(), (int)diffusemap.getHeight());
								WritableImage emissiveimage = emissivecanvas.snapshot(snap, null);
								diffusecanvasgfx.setGlobalBlendMode(BlendMode.ADD);
								diffusecanvasgfx.drawImage(emissiveimage, 0, 0);
							}
							WritableImage diffuseimage = diffusecanvas.snapshot(snap, null);
							trimat.setDiffuseMap(diffuseimage);
							trimat.setSelfIlluminationMap(diffuseimage);
						} else {
							trimat.setDiffuseColor(tricolor);
						}
					} else if ((diffusemap!=null)&&((emissivemap!=null)||(emissivecolor!=null))) {
						Canvas diffusecanvas = new Canvas((int)diffusemap.getWidth(), (int)diffusemap.getHeight());
						GraphicsContext diffusecanvasgfx = diffusecanvas.getGraphicsContext2D();
						diffusecanvasgfx.clearRect(0, 0, (int)diffusemap.getWidth(), (int)diffusemap.getHeight());
						diffusecanvasgfx.drawImage(diffusemap, 0, 0);
						Canvas roughnessmultcanvas = new Canvas((int)diffusemap.getWidth(), (int)diffusemap.getHeight());
						GraphicsContext roughnessmultcanvasgfx = roughnessmultcanvas.getGraphicsContext2D();
						roughnessmultcanvasgfx.setFill(new Color(roughnessmult,roughnessmult,roughnessmult,1.0f));
						roughnessmultcanvasgfx.fillRect(0, 0, (int)diffusemap.getWidth(), (int)diffusemap.getHeight());
						WritableImage roughnessimage = roughnessmultcanvas.snapshot(snap, null);
						diffusecanvasgfx.setGlobalBlendMode(BlendMode.MULTIPLY);
						diffusecanvasgfx.drawImage(emissivemap, 0, 0, (int)diffusemap.getWidth(), (int)diffusemap.getHeight());
						diffusecanvasgfx.drawImage(roughnessimage, 0, 0);
						if (emissivemap!=null) {
							diffusecanvasgfx.setGlobalBlendMode(BlendMode.ADD);
							diffusecanvasgfx.drawImage(emissivemap, 0, 0, (int)diffusemap.getWidth(), (int)diffusemap.getHeight());
						} else if (emissivecolor!=null) {
							Canvas emissivecanvas = new Canvas((int)diffusemap.getWidth(), (int)diffusemap.getHeight());
							GraphicsContext emissivecanvasgfx = emissivecanvas.getGraphicsContext2D();
							emissivecanvasgfx.clearRect(0, 0, (int)diffusemap.getWidth(), (int)diffusemap.getHeight());
							emissivecanvasgfx.setFill(emissivecolor);
							emissivecanvasgfx.fillRect(0, 0, (int)diffusemap.getWidth(), (int)diffusemap.getHeight());
							WritableImage emissiveimage = emissivecanvas.snapshot(snap, null);
							diffusecanvasgfx.setGlobalBlendMode(BlendMode.ADD);
							diffusecanvasgfx.drawImage(emissiveimage, 0, 0);
						}
						WritableImage diffuseimage = diffusecanvas.snapshot(snap, null);
						trimat.setDiffuseMap(diffuseimage);
						trimat.setSelfIlluminationMap(diffuseimage);
					} else if (emissivemap!=null) {
						trimat.setDiffuseMap(emissivemap);
						trimat.setSelfIlluminationMap(emissivemap);
					} else if (emissivecolor!=null) {
						trimat.setDiffuseColor(emissivecolor);
						WritableImage emissiveimage = new WritableImage(1,1);
						PixelWriter emissiveimagewriter = emissiveimage.getPixelWriter();
						emissiveimagewriter.setColor(0, 0, emissivecolor);
						trimat.setSelfIlluminationMap(emissiveimage);
					} else {
						trimat.setDiffuseColor(tricolor);
						WritableImage emissiveimage = new WritableImage(1,1);
						PixelWriter emissiveimagewriter = emissiveimage.getPixelWriter();
						emissiveimagewriter.setColor(0, 0, tricolor);
						trimat.setSelfIlluminationMap(emissiveimage);
					}
				} else {
					Color tricolor = Color.BLACK;
					double tricolorr = trianglecolor.getRed()*roughnessmult;
					double tricolorg = trianglecolor.getGreen()*roughnessmult;
					double tricolorb = trianglecolor.getBlue()*roughnessmult;
					double tricolora = trianglecolor.getOpacity();
					if (tricolorr>1.0f) {tricolorr=1.0f;}
					if (tricolorg>1.0f) {tricolorg=1.0f;}
					if (tricolorb>1.0f) {tricolorb=1.0f;}
					if (tricolora>1.0f) {tricolora=1.0f;}
					tricolor = new Color(tricolorr,tricolorg,tricolorb,tricolora);
					trimat.setDiffuseColor(tricolor);
					if ((diffusemap!=null)&&((emissivemap!=null)||(emissivecolor!=null))) {
						Canvas diffusecanvas = new Canvas((int)diffusemap.getWidth(), (int)diffusemap.getHeight());
						GraphicsContext diffusecanvasgfx = diffusecanvas.getGraphicsContext2D();
						diffusecanvasgfx.clearRect(0, 0, (int)diffusemap.getWidth(), (int)diffusemap.getHeight());
						diffusecanvasgfx.drawImage(diffusemap, 0, 0);
						Canvas roughnessmultcanvas = new Canvas((int)diffusemap.getWidth(), (int)diffusemap.getHeight());
						GraphicsContext roughnessmultcanvasgfx = roughnessmultcanvas.getGraphicsContext2D();
						roughnessmultcanvasgfx.setFill(new Color(roughnessmult,roughnessmult,roughnessmult,1.0f));
						roughnessmultcanvasgfx.fillRect(0, 0, (int)diffusemap.getWidth(), (int)diffusemap.getHeight());
						WritableImage roughnessimage = roughnessmultcanvas.snapshot(snap, null);
						diffusecanvasgfx.setGlobalBlendMode(BlendMode.MULTIPLY);
						diffusecanvasgfx.drawImage(emissivemap, 0, 0, (int)diffusemap.getWidth(), (int)diffusemap.getHeight());
						diffusecanvasgfx.drawImage(roughnessimage, 0, 0);
						if (emissivemap!=null) {
							diffusecanvasgfx.setGlobalBlendMode(BlendMode.ADD);
							diffusecanvasgfx.drawImage(emissivemap, 0, 0, (int)diffusemap.getWidth(), (int)diffusemap.getHeight());
						} else if (emissivecolor!=null) {
							Canvas emissivecanvas = new Canvas((int)diffusemap.getWidth(), (int)diffusemap.getHeight());
							GraphicsContext emissivecanvasgfx = emissivecanvas.getGraphicsContext2D();
							emissivecanvasgfx.clearRect(0, 0, (int)diffusemap.getWidth(), (int)diffusemap.getHeight());
							emissivecanvasgfx.setFill(emissivecolor);
							emissivecanvasgfx.fillRect(0, 0, (int)diffusemap.getWidth(), (int)diffusemap.getHeight());
							WritableImage emissiveimage = emissivecanvas.snapshot(snap, null);
							diffusecanvasgfx.setGlobalBlendMode(BlendMode.ADD);
							diffusecanvasgfx.drawImage(emissiveimage, 0, 0);
						}
						WritableImage diffuseimage = diffusecanvas.snapshot(snap, null);
						trimat.setDiffuseMap(diffuseimage);
						trimat.setSelfIlluminationMap(diffuseimage);
					} else if (emissivemap!=null) {
						trimat.setSelfIlluminationMap(emissivemap);
					} else if (emissivecolor!=null) {
						WritableImage emissiveimage = new WritableImage(1,1);
						PixelWriter emissiveimagewriter = emissiveimage.getPixelWriter();
						emissiveimagewriter.setColor(0, 0, emissivecolor);
						trimat.setSelfIlluminationMap(emissiveimage);
					}
				}
				trimeshview.setMaterial(trimat);
				root.getChildren().add(trimeshview);
			}
		}
		return root;
	}
	
	public static TriangleMesh constructTriangleMesh(Triangle[] vtri) {
		TriangleMesh k = null;
		if (vtri!=null) {
			k = new TriangleMesh(VertexFormat.POINT_NORMAL_TEXCOORD);
			for (int i=0;i<vtri.length;i++) {
				float[] tripoints = {(float)vtri[i].pos1.x, (float)vtri[i].pos1.y, (float)vtri[i].pos1.z, (float)vtri[i].pos2.x, (float)vtri[i].pos2.y, (float)vtri[i].pos2.z, (float)vtri[i].pos3.x, (float)vtri[i].pos3.y, (float)vtri[i].pos3.z};
				float[] tricoords = {(float)vtri[i].pos1.tex.u,1.0f-(float)vtri[i].pos1.tex.v,(float)vtri[i].pos2.tex.u,1.0f-(float)vtri[i].pos2.tex.v,(float)vtri[i].pos3.tex.u,1.0f-(float)vtri[i].pos3.tex.v};
				float[] trinorms = {(float)vtri[i].norm.dx, (float)vtri[i].norm.dy, (float)vtri[i].norm.dz};
				int[] triface = {3*i, i, 3*i, 3*i+1, i, 3*i+1, 3*i+2, i, 3*i+2};
				k.getPoints().addAll(tripoints);
				k.getNormals().addAll(trinorms);
				k.getTexCoords().addAll(tricoords);
				k.getFaces().addAll(triface);
			}
		}
		return k;
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
									float p4pixelrt = multiplier*p4pixelr/pixelcount;
									float p4pixelgt = multiplier*p4pixelg/pixelcount;
									float p4pixelbt = multiplier*p4pixelb/pixelcount;
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
										entitylist[j].trianglelist[i].mat.ambientcolor = entitylist[j].trianglelist[i].lmatl[l].facecolor;
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
