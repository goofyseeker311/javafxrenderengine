package fi.jkauppa.javarenderengine;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.VolatileImage;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;

import fi.jkauppa.javarenderengine.ModelLib.AxisAlignedBoundingBox;
import fi.jkauppa.javarenderengine.ModelLib.Coordinate;
import fi.jkauppa.javarenderengine.ModelLib.Cuboid;
import fi.jkauppa.javarenderengine.ModelLib.Direction;
import fi.jkauppa.javarenderengine.ModelLib.Line;
import fi.jkauppa.javarenderengine.ModelLib.Matrix;
import fi.jkauppa.javarenderengine.ModelLib.Plane;
import fi.jkauppa.javarenderengine.ModelLib.PlaneRay;
import fi.jkauppa.javarenderengine.ModelLib.Position;
import fi.jkauppa.javarenderengine.ModelLib.Quad;
import fi.jkauppa.javarenderengine.ModelLib.Ray;
import fi.jkauppa.javarenderengine.ModelLib.RenderView;
import fi.jkauppa.javarenderengine.ModelLib.Sphere;
import fi.jkauppa.javarenderengine.ModelLib.Triangle;

public class JavaRenderEngine extends JFrame implements ActionListener,KeyListener,MouseListener,MouseMotionListener,MouseWheelListener {
	private static final long serialVersionUID = 1L;
	private DrawApp drawapp = new DrawApp();
	private CADApp cadapp = new CADApp();
	private ModelApp modelapp = new ModelApp();
	private EditorApp editorapp = new EditorApp();
	private GameApp gameapp = new GameApp();
	private AppHandlerPanel activeapp = null;
	private DropTargetHandler droptargethandler = new DropTargetHandler();
	private double fpstarget = 120.0f;
	private int fpstargetdelay = (int)Math.floor(1000.0f/fpstarget);
	private Timer timer = new Timer(this.fpstargetdelay,this);
	private final int defaultimagecanvaswidth = 1920;
	private final int defaultimagecanvasheight= 1080;
	private boolean windowedmode = true;
	private VolatileImage logoimage = UtilLib.loadImage("res/icons/logo.png", true);
	
	public JavaRenderEngine() {
		if (this.logoimage!=null) {this.setIconImage(this.logoimage);}
		this.setTitle("Java Render Engine v2.6.14");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setJMenuBar(null);
		if (!windowedmode) {
			this.setUndecorated(true);
			this.setExtendedState(this.getExtendedState()|JFrame.MAXIMIZED_BOTH);
		}else {
			this.setLocationByPlatform(true);
		}
		this.setFocusTraversalKeysEnabled(false);
		this.setDropTarget(this.droptargethandler);
		this.setActiveApp(drawapp);
		this.setVisible(true);
		timer.start();
	}

	public static void main(String[] args) {
		System.setProperty("sun.java2d.opengl", "true");
		String userdir = System.getProperty("user.dir");
		System.out.println("JavaRenderEngine: main: userdir="+userdir);
		try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());} catch (Exception ex) {}
        
		Position campos=new Position(0.0f,0.0f,0.0f);
		Position[] camposa = {campos};
		Position[] campos2 = {new Position(1.0f,2.0f,3.0f), new Position(1.0f,2.0f,3.0f)};
		Position[] campos3 = {new Position(1.0f,0.0f,0.0f), new Position(1.0f,0.0f,0.0f)};
		Position[] campos4 = {new Position(1.0f,0.0f,0.0f), new Position(1.0f,1.0f,0.0f), new Position(1.0f,0.0f,1.0f)};
		campos4[0].tex = new Coordinate(0.0f,0.0f);
		campos4[1].tex = new Coordinate(1.0f,0.0f);
		campos4[2].tex = new Coordinate(0.0f,1.0f);
		System.out.println("JavaRenderEngine: main: campos: "+campos.x+" "+campos.y+" "+campos.z);
		Direction[] camvec = MathLib.vectorFromPoints(campos2, campos3);
		for (int i=0;i<camvec.length;i++) {System.out.println("JavaRenderEngine: main: camvec: "+camvec[i].dx+" "+camvec[i].dy+" "+camvec[i].dz);}
		Direction[] camdir = {new Direction(1.0f,0.0f,0.0f)};
		Direction[] camdir2 = {new Direction(1.0f,-1.0f,1.0f), new Direction(1.0f,-1.0f,1.0f)};
		Direction[] camdir3 = {new Direction(1.0f,0.0f,0.0f), new Direction(1.0f,0.0f,0.0f)};
		Direction[] camdir4 = {new Direction(0.0f,0.0f,1.0f)};
		Direction[] camcross = MathLib.vectorCross(camdir2, camdir3);
		for (int i=0;i<camcross.length;i++) {System.out.println("JavaRenderEngine: main: camcross: "+camcross[i].dx+" "+camcross[i].dy+" "+camcross[i].dz);}
		double[] camdir2len = MathLib.vectorLength(camdir2);
		for (int i=0;i<camdir2len.length;i++) {System.out.println("JavaRenderEngine: main: camdir2len: "+camdir2len[i]);}
		Triangle[] ptri = {new Triangle(campos4[0],campos4[1],campos4[2]), new Triangle(campos4[0],campos4[1],campos4[2]), new Triangle(campos4[0],campos4[1],campos4[2])};
		Plane[] tpplane = MathLib.trianglePlane(ptri);
		System.out.println("JavaRenderEngine: main: tpplane: "+tpplane[0].a+" "+tpplane[0].b+" "+tpplane[0].c+" "+tpplane[0].d);
		double[] camdot = MathLib.vectorDot(camdir2, campos2);
		for (int i=0;i<camdot.length;i++) {System.out.println("JavaRenderEngine: main: camdot: "+camdot[i]+" "+camdot[i]+" "+camdot[i]);}
		Direction[] camdirnorm = MathLib.normalizeVector(camdir);
		Direction[] camdir2norm = MathLib.normalizeVector(camdir2);
		Direction[] camdir3norm = MathLib.normalizeVector(camdir3);
		Direction[] camdir4norm = MathLib.normalizeVector(camdir4);
		System.out.println("JavaRenderEngine: main: camdirnorm: "+camdirnorm[0].dx+" "+camdirnorm[0].dy+" "+camdirnorm[0].dz);
		System.out.println("JavaRenderEngine: main: camdir2norm: "+camdir2norm[0].dx+" "+camdir2norm[0].dy+" "+camdir2norm[0].dz);
		System.out.println("JavaRenderEngine: main: camdir3norm: "+camdir3norm[0].dx+" "+camdir3norm[0].dy+" "+camdir3norm[0].dz);
		System.out.println("JavaRenderEngine: main: camdir4norm: "+camdir4norm[0].dx+" "+camdir4norm[0].dy+" "+camdir4norm[0].dz);
		for (int i=0;i<camdir2norm.length;i++) {System.out.println("JavaRenderEngine: main: camdir2norm: "+camdir2norm[i].dx+" "+camdir2norm[i].dy+" "+camdir2norm[i].dz);}
		Plane[] tplane = {new Plane(1.0f,0.0f,0.0f,-2.0f)};
		Plane[] tplane2 = {new Plane(1.0f,0.0f,0.0f,-2.0f), new Plane(1.0f,0.0f,0.0f,-2.0f), new Plane(1.0f,0.0f,0.0f,-2.0f)};
		Plane[] tplane3 = {tplane[0], tplane[0].invert()};
		Direction[] tdir = {camdir[0], camdir[0].invert()};
		double[][] cpdist = MathLib.rayPlaneDistance(campos, camdirnorm, tplane);
		double[][] cpdist2 = MathLib.rayPlaneDistance(campos, camdir2norm, tplane2);
		double[][] cpdist3 = MathLib.rayPlaneDistance(campos, camdirnorm, tpplane);
		double[][] cpdist4 = MathLib.rayPlaneDistance(campos, tdir, tplane3);
		Position[][] cpdistint4 = MathLib.rayPlaneIntersection(campos, tdir, tplane3);
		System.out.println("JavaRenderEngine: main: cpdist[0][0]: "+cpdist[0][0]);
		for (int i=0;i<cpdist2.length;i++) {for (int j=0;j<cpdist2[i].length;j++) {System.out.println("JavaRenderEngine: main: cpdist2["+i+"]["+j+"]: "+cpdist2[i][j]);}}
		for (int i=0;i<cpdist3.length;i++) {for (int j=0;j<cpdist3[i].length;j++) {System.out.println("JavaRenderEngine: main: cpdist3["+i+"]["+j+"]: "+cpdist3[i][j]);}}
		for (int i=0;i<cpdist4.length;i++) {for (int j=0;j<cpdist4[i].length;j++) {System.out.println("JavaRenderEngine: main: cpdist4["+i+"]["+j+"]: "+cpdist4[i][j]);}}
		for (int i=0;i<cpdistint4.length;i++) {for (int j=0;j<cpdistint4[i].length;j++) {if (cpdistint4[i][j]!=null) { System.out.println("JavaRenderEngine: main: cpdistint4["+i+"]["+j+"]: "+cpdistint4[i][j].x+" "+cpdistint4[i][j].y+" "+cpdistint4[i][j].z);} else {System.out.println("JavaRenderEngine: main: cpdistint4["+i+"]["+j+"]: no hit.");}}}
		Plane[] pplane = MathLib.planeFromNormalAtPoint(campos2, camdir2norm);
		Plane[] camplane = MathLib.planeFromNormalAtPoint(camposa, camdir4norm);
		for (int i=0;i<pplane.length;i++) {System.out.println("JavaRenderEngine: main: pplane: "+pplane[i].a+" "+pplane[i].b+" "+pplane[i].c+" "+pplane[i].d);}
		for (int i=0;i<camplane.length;i++) {System.out.println("JavaRenderEngine: main: camplane: "+camplane[i].a+" "+camplane[i].b+" "+camplane[i].c+" "+camplane[i].d);}
		Direction[] zerodir = {new Direction(0.0f,0.0f,0.0f)};
		double[] camdirang = MathLib.vectorAngle(camdir2, camdir3);
		double[] camdirang2 = MathLib.vectorAngle(zerodir, zerodir);
		for (int i=0;i<camdirang.length;i++) {System.out.println("JavaRenderEngine: main: camdirang: "+camdirang[i]);}
		for (int i=0;i<camdirang2.length;i++) {System.out.println("JavaRenderEngine: main: camdirang2: "+camdirang2[i]);}
		Position[][] camrtint = MathLib.rayTriangleIntersection(campos, camdir3, ptri);
		for (int i=0;i<camrtint.length;i++) {for (int j=0;j<camrtint[i].length;j++) {if(camrtint[i][j]!=null) {System.out.println("JavaRenderEngine: main: camrtint["+i+"]["+j+"]: "+camrtint[i][j].x+" "+camrtint[i][j].y+" "+camrtint[i][j].z+" ("+camrtint[i][j].tex.u+" "+camrtint[i][j].tex.v+")");}else{System.out.println("JavaRenderEngine: main: camrtint["+i+"]["+j+"]: no hit.");}}}
		Line[][] camptint = MathLib.planeTriangleIntersection(camplane, ptri);
		for (int i=0;i<camptint.length;i++) {for (int j=0;j<camptint[i].length;j++) {if(camptint[i][j]!=null) {System.out.println("JavaRenderEngine: main: camptint["+i+"]["+j+"]: "+camptint[i][j].pos1.x+" "+camptint[i][j].pos1.y+" "+camptint[i][j].pos1.z+" ("+camptint[i][j].pos1.tex.u+" "+camptint[i][j].pos1.tex.v+"), "+camptint[i][j].pos2.x+" "+camptint[i][j].pos2.y+" "+camptint[i][j].pos2.z+" ("+camptint[i][j].pos2.tex.u+" "+camptint[i][j].pos2.tex.v+")");}else{System.out.println("JavaRenderEngine: main: camptint["+i+"]["+j+"]: no hit.");}}}
		Matrix mat1 = new Matrix(1,0,0,0,1,0,0,0,1);
		Matrix mat2 = new Matrix(0.6124,0.6124,0.5000,0.3536,0.3536,-0.8660,-0.7071,0.7071,0);
		Matrix matout1 = MathLib.matrixMultiply(mat1, mat2);
		Matrix matout2 = MathLib.matrixMultiply(mat2, mat1);
		Matrix matout3 = MathLib.matrixMultiply(mat2, mat2);
		System.out.println("JavaRenderEngine: main: matout1: "+matout1.a11+" "+matout1.a12+" "+matout1.a13); System.out.println("JavaRenderEngine: main: matout1: "+matout1.a21+" "+matout1.a22+" "+matout1.a23); System.out.println("JavaRenderEngine: main: matout1: "+matout1.a31+" "+matout1.a32+" "+matout1.a33);
		System.out.println("JavaRenderEngine: main: matout2: "+matout2.a11+" "+matout2.a12+" "+matout2.a13); System.out.println("JavaRenderEngine: main: matout2: "+matout2.a21+" "+matout2.a22+" "+matout2.a23); System.out.println("JavaRenderEngine: main: matout2: "+matout2.a31+" "+matout2.a32+" "+matout2.a33);
		System.out.println("JavaRenderEngine: main: matout3: "+matout3.a11+" "+matout3.a12+" "+matout3.a13); System.out.println("JavaRenderEngine: main: matout3: "+matout3.a21+" "+matout3.a22+" "+matout3.a23); System.out.println("JavaRenderEngine: main: matout3: "+matout3.a31+" "+matout3.a32+" "+matout3.a33);
		Position[] campos4rotmat1 = MathLib.matrixMultiply(campos4, mat1);
		Position[] campos4rotmat2 = MathLib.matrixMultiply(campos4, mat2);
		for (int i=0;i<campos4rotmat1.length;i++) {System.out.println("JavaRenderEngine: main: campos4rotmat1: "+campos4rotmat1[i].x+" "+campos4rotmat1[i].y+" "+campos4rotmat1[i].z);}
		for (int i=0;i<campos4rotmat2.length;i++) {System.out.println("JavaRenderEngine: main: campos4rotmat2: "+campos4rotmat2[i].x+" "+campos4rotmat2[i].y+" "+campos4rotmat2[i].z);}
		Direction[] camdir2normrotmat1 = MathLib.matrixMultiply(camdir2norm, mat1);
		Direction[] camdir2normrotmat2 = MathLib.matrixMultiply(camdir2norm, mat2);
		for (int i=0;i<camdir2normrotmat1.length;i++) {System.out.println("JavaRenderEngine: main: camdir2normrotmat1: "+camdir2normrotmat1[i].dx+" "+camdir2normrotmat1[i].dy+" "+camdir2normrotmat1[i].dz);}
		for (int i=0;i<camdir2normrotmat2.length;i++) {System.out.println("JavaRenderEngine: main: camdir2normrotmat2: "+camdir2normrotmat2[i].dx+" "+camdir2normrotmat2[i].dy+" "+camdir2normrotmat2[i].dz);}
		Matrix matrot1 = MathLib.rotationMatrix(0, 0, 0);
		Matrix matrot2 = MathLib.rotationMatrix(90, 45, 30);
		System.out.println("JavaRenderEngine: main: matrot1: "+matrot1.a11+" "+matrot1.a12+" "+matrot1.a13); System.out.println("JavaRenderEngine: main: matrot1: "+matrot1.a21+" "+matrot1.a22+" "+matrot1.a23); System.out.println("JavaRenderEngine: main: matrot1: "+matrot1.a31+" "+matrot1.a32+" "+matrot1.a33);
		System.out.println("JavaRenderEngine: main: matrot2: "+matrot2.a11+" "+matrot2.a12+" "+matrot2.a13); System.out.println("JavaRenderEngine: main: matrot2: "+matrot2.a21+" "+matrot2.a22+" "+matrot2.a23); System.out.println("JavaRenderEngine: main: matrot2: "+matrot2.a31+" "+matrot2.a32+" "+matrot2.a33);
		double[] unsortedlist = {5, -2, 7, 15, 3, -2, 0, 2, 7};
		int[] sortedlistidx = UtilLib.indexSort(unsortedlist);
		double[] sortedlist = UtilLib.indexValues(unsortedlist,sortedlistidx);
		System.out.print("JavaRenderEngine: main: unsortedlist:"); for (int i=0;i<unsortedlist.length;i++) {System.out.print(" "+unsortedlist[i]);} System.out.println();
		System.out.print("JavaRenderEngine: main: sortedlistidx:"); for (int i=0;i<sortedlistidx.length;i++) {System.out.print(" "+sortedlistidx[i]);} System.out.println();
		System.out.print("JavaRenderEngine: main: sortedlist:"); for (int i=0;i<sortedlist.length;i++) {System.out.print(" "+sortedlist[i]);} System.out.println();
		Sphere[] vsphere1 = {new Sphere(0,0,0,2)};
		Sphere[] vsphere2 = {new Sphere(0,0,2,2), new Sphere(0,4,0,2), new Sphere(4,4,0,2)};
		Sphere[] vsphere3 = {new Sphere(0,0,0,2), new Sphere(0,0,3.9,2), new Sphere(0,3.9,0,2), new Sphere(3.9,0,0,2)};
		boolean[][] ssint = MathLib.sphereSphereIntersection(vsphere1, vsphere2);
		Integer[][] ssint2 = MathLib.mutualSphereIntersection(vsphere3);
		System.out.print("JavaRenderEngine: main: ssint["+ssint.length+"]["+ssint[0].length+"]="); for (int j=0;j<ssint.length;j++) {for (int i=0;i<ssint[0].length;i++) {System.out.print(" "+ssint[j][i]);}System.out.println();}
		for (int j=0;j<ssint2.length;j++) {System.out.print("JavaRenderEngine: main: ssint2["+j+"]=");for (int i=0;i<ssint2[j].length;i++){System.out.print(" "+ssint2[j][i]);}System.out.println();}
		Line[] pline = new Line[3]; pline[0]=new Line(new Position(1,0,-1),new Position(1,0,1)); pline[1]=new Line(new Position(-1,0,1),new Position(1,0,1)); pline[2]=new Line(new Position(1,0,1),new Position(1,0,3));
		Position[][] camplint = MathLib.planeLineIntersection(camplane, pline);
		for (int i=0;i<camplint.length;i++) {for (int j=0;j<camplint[i].length;j++) {if(camplint[i][j]!=null) {System.out.println("JavaRenderEngine: main: camplint["+i+"]["+j+"]: "+camplint[i][j].x+" "+camplint[i][j].y+" "+camplint[i][j].z);}else{System.out.println("JavaRenderEngine: main: camplint["+i+"]["+j+"]: no hit.");}}}
		double[] pang = MathLib.projectedAngles(8, 70.0f);
		for (int i=0;i<pang.length;i++) {System.out.println("JavaRenderEngine: main: pang["+i+"]="+pang[i]);}
		double[] prjstep = MathLib.projectedStep(8, 70.0f);
		double[] prjangles = MathLib.projectedAngles(8, 70.0f);
		Direction[] prjdirs = MathLib.projectedPlaneRayDirections(matrot1);
		Direction[] prjdirs2 = MathLib.projectedPlaneRayDirections(matrot2);
		Direction[] prjvectors = MathLib.projectedPlaneRayVectors(8, 70.0f, matrot1, true);
		PlaneRay[] prjplaneray = MathLib.projectedPlaneRays(campos, 9, 9, 90, 90, matrot1);
		for (int i=0;i<prjstep.length;i++) {System.out.println("JavaRenderEngine: main: prjstep["+i+"]: "+prjstep[i]);}
		for (int i=0;i<prjangles.length;i++) {System.out.println("JavaRenderEngine: main: prjangles["+i+"]: "+prjangles[i]);}
		System.out.println("JavaRenderEngine: main: prjdirs[0]="+prjdirs[0].dx+","+prjdirs[0].dy+","+prjdirs[0].dz); System.out.println("JavaRenderEngine: main: prjdirs[1]="+prjdirs[1].dx+","+prjdirs[1].dy+","+prjdirs[1].dz); System.out.println("JavaRenderEngine: main: prjdirs[2]="+prjdirs[2].dx+","+prjdirs[2].dy+","+prjdirs[2].dz);
		System.out.println("JavaRenderEngine: main: prjdirs2[0]="+prjdirs2[0].dx+","+prjdirs2[0].dy+","+prjdirs2[0].dz); System.out.println("JavaRenderEngine: main: prjdirs2[1]="+prjdirs2[1].dx+","+prjdirs2[1].dy+","+prjdirs2[1].dz); System.out.println("JavaRenderEngine: main: prjdirs2[2]="+prjdirs2[2].dx+","+prjdirs2[2].dy+","+prjdirs2[2].dz);
		for (int i=0;i<prjvectors.length;i++) {System.out.println("JavaRenderEngine: main: prjvectors["+i+"]: "+prjvectors[i].dx+" "+prjvectors[i].dy+" "+prjvectors[i].dz);}
		for (int i=0;i<prjplaneray.length;i++) {System.out.println("JavaRenderEngine: main: prjplaneray["+i+"]: pos="+prjplaneray[i].pos.x+" "+prjplaneray[i].pos.y+" "+prjplaneray[i].pos.z+" dir="+prjplaneray[i].dir.dx+" "+prjplaneray[i].dir.dy+" "+prjplaneray[i].dir.dz+" plane="+prjplaneray[i].plane.a+" "+prjplaneray[i].plane.b+" "+prjplaneray[i].plane.c+" "+prjplaneray[i].plane.d+" vfov="+prjplaneray[i].vfov);}
		Direction[][] prjrays = MathLib.projectedRays(4, 3, 70, 39, matrot1, true);
		for (int j=0;j<prjrays.length;j++) {System.out.print("JavaRenderEngine: main: prjrays["+j+"]=");for (int i=0;i<prjrays[j].length;i++) {System.out.print(" ["+prjrays[j][i].dx+","+prjrays[j][i].dy+","+prjrays[j][i].dz+"]");}System.out.println();}
		double[] sang = MathLib.spheremapAngles(8, 360.0f);
		Matrix smat = MathLib.rotationMatrix(-90.0f, 0.0f, 0.0f);
		Direction[] svectors = MathLib.spheremapVectors(9, smat);
		Direction[][] srays = MathLib.spheremapRays(9, 5, smat);
		for (int i=0;i<sang.length;i++) {System.out.println("JavaRenderEngine: main: sang["+i+"]="+sang[i]);}
		for (int i=0;i<svectors.length;i++) {System.out.println("JavaRenderEngine: main: svectors["+i+"]="+svectors[i].dx+" "+svectors[i].dy+" "+svectors[i].dz);}
		for (int j=0;j<srays.length;j++) { for (int i=0;i<srays[0].length;i++) {System.out.println("JavaRenderEngine: main: srays["+j+"]["+i+"]="+srays[j][i].dx+" "+srays[j][i].dy+" "+srays[j][i].dz);}}
		Direction[] camfwd = {new Direction(1,0,0)};
		Direction[] camrgt = {new Direction(0,1,0)};
		Direction[] camup = MathLib.vectorCross(camfwd[0],camrgt);
		System.out.println("JavaRenderEngine: main: camup="+camup[0].dx+" "+camup[0].dy+" "+camup[0].dz);
		Position[] vpoint = {new Position(0,0,0)};
		Position[] vplanepoint = {new Position(1,1,0)};
		Direction[] vplanenormal = {new Direction(-1,-1,0)};
		Plane[] vplane = MathLib.planeFromNormalAtPoint(vplanepoint, vplanenormal);
		Position[] vpoints = {new Position(1.0f,0.0f,0.0f),new Position(3.0f,0.0f,0.0f)}; 
		double[][] vppdist = MathLib.planePointDistance(vpoint, vplane);
		double[][] vppdist2 = MathLib.planePointDistance(campos2, tplane2);
		double[][] vppdist3 = MathLib.planePointDistance(vpoints, tplane3);
		System.out.println("JavaRenderEngine: main: vppdist="+vppdist[0][0]);
		for (int j=0;j<vppdist2.length;j++) {System.out.print("JavaRenderEngine: main: vppdist2["+j+"]="); for (int i=0;i<vppdist2[0].length;i++) {System.out.print(" "+vppdist2[j][i]);}System.out.println();}
		for (int j=0;j<vppdist3.length;j++) {System.out.print("JavaRenderEngine: main: vppdist3["+j+"]="); for (int i=0;i<vppdist3[0].length;i++) {System.out.print(" "+vppdist3[j][i]);}System.out.println();}
		Position[] vertexlist = {new Position(-5,3,9),new Position(-7,-3,-1),new Position(4,-6,-7),new Position(2,4,11)};
		AxisAlignedBoundingBox aabb = MathLib.axisAlignedBoundingBox(vertexlist);
		Sphere pointcloudsphere = MathLib.pointCloudCircumSphere(vertexlist);
		double[] trianglearea = MathLib.triangleArea(ptri);
		Sphere[] trianglesphere = MathLib.triangleCircumSphere(ptri);
		Sphere[] triangleinsphere = MathLib.triangleInSphere(ptri);
		System.out.println("JavaRenderEngine: main: aabb="+aabb.pos1.x+","+aabb.pos1.y+","+aabb.pos1.z+" "+aabb.pos2.x+" "+aabb.pos2.y+" "+aabb.pos2.z);
		System.out.println("JavaRenderEngine: main: boundingsphere="+pointcloudsphere.x+","+pointcloudsphere.y+","+pointcloudsphere.z+" "+pointcloudsphere.r);
		System.out.println("JavaRenderEngine: main: trianglearea="+trianglearea[0]);
		for (int i=0;i<trianglesphere.length;i++) {System.out.println("JavaRenderEngine: main: trianglesphere["+i+"]="+trianglesphere[i].x+" "+trianglesphere[i].y+" "+trianglesphere[i].z+" "+trianglesphere[i].r);}
		for (int i=0;i<triangleinsphere.length;i++) {System.out.println("JavaRenderEngine: main: triangleinsphere["+i+"]="+triangleinsphere[i].x+" "+triangleinsphere[i].y+" "+triangleinsphere[i].z+" "+triangleinsphere[i].r);}
		Position pmsvpos = new Position(0,0,0);
		Position[] pmstripos = {new Position(0.0f,-3.9f,0.0f),new Position(-1.0f,-3.9f,0.0f),new Position(1.0f,-3.9f,0.0f),new Position(0.0f,-3.9f,1.0f),new Position(0.0f,-3.9f,-1.0f),new Position(-3.9f,0.0f,0.0f),new Position(3.9f,0.0f,0.0f),new Position(0.0f,3.9f,0.0f),new Position(-1.0f,3.9f,0.0f),new Position(1.0f,3.9f,0.0f),new Position(1.0f,3.9f,1.0f),new Position(-1.0f,3.9f,1.0f),new Position(1.0f,3.9f,1.0f),new Position(-1.0f,3.9f,-1.0f),new Position(1.0f,3.9f,-1.0f),new Position(0.0f,3.9f,3.9f),new Position(0.0f,3.9f,-3.9f),new Position(0.0f,0.0f,3.9f),new Position(0.0f,0.0f,-3.9f),new Position(0.0f,0.0f,3.9f),new Position(0.0f,0.0f,-3.9f), new Position(0.0f,-1.0f,1.0f),new Position(0.0f,-1.0f,-1.0f),new Position(0.0f,-0.5f,1.0f),new Position(0.0f,-0.5f,-1.0f),new Position(0.0f,1.0f,1.0f),new Position(0.0f,1.0f,-1.0f),new Position(0.0f,0.5f,1.0f),new Position(0.0f,0.5f,-1.0f), new Position(1.0f,0.0f,1.0f),new Position(1.0f,0.0f,-1.0f),new Position(0.5f,0.0f,1.0f),new Position(0.5f,0.0f,-1.0f),new Position(-1.0f,-0.0f,1.0f),new Position(-1.0f,0.0f,-1.0f),new Position(-0.5f,0.0f,1.0f),new Position(-0.5f,0.0f,-1.0f)};
		Triangle[] pmsvtri = {new Triangle(pmstripos[0],pmstripos[1],pmstripos[3]), new Triangle(pmstripos[0],pmstripos[2],pmstripos[3]), new Triangle(pmstripos[0],pmstripos[1],pmstripos[4]), new Triangle(pmstripos[0],pmstripos[2],pmstripos[4]), new Triangle(pmstripos[8],pmstripos[9],pmstripos[10]), new Triangle(pmstripos[1],pmstripos[2],pmstripos[7]), new Triangle(pmstripos[3],pmstripos[11],pmstripos[12]), new Triangle(pmstripos[4],pmstripos[13],pmstripos[14])};
		Sphere[] pmsvsph = {new Sphere(0,0,0,2), new Sphere(0,0,3.9,2), new Sphere(0,3.9,0,2), new Sphere(3.9,0,0,2)};
		Matrix pmsrotx = MathLib.rotationMatrix(-90.0f, 0.0f, 0.0f);
		Matrix pmsrotz = MathLib.rotationMatrix(0.0f, 0.0f, 0.0f);
		Matrix pmsrot = MathLib.matrixMultiply(pmsrotz, pmsrotx);
		Rectangle[] pmtint = MathLib.projectedTriangleIntersection(pmsvpos, pmsvtri, 64, 64, 90, 90, pmsrot, null);
		Rectangle[] pmsint = MathLib.projectedSphereIntersection(pmsvpos, pmsvsph, 64, 64, 90, 90, pmsrot, null);
		Rectangle[][] cmsint = MathLib.cubemapSphereIntersection(pmsvpos, pmsvsph, 64, null);
		Rectangle[] smsint = MathLib.spheremapSphereIntersection(pmsvpos, pmsvsph, 64, 64, pmsrot, null);
		Coordinate[] smpint = MathLib.spheremapPoint(pmsvpos, pmstripos, 64, 64, pmsrot, null);
		Rectangle[] smtint = MathLib.spheremapTriangleIntersection(pmsvpos, pmsvtri, 64, 64, pmsrot, null);
		for (int i=0;i<pmtint.length;i++){if(pmtint[i]==null){pmtint[i]=new Rectangle(-1,-1,1,1);}}
		for (int i=0;i<pmtint.length;i++) {System.out.println("JavaRenderEngine: main: pmtint["+i+"]= "+pmtint[i].x+","+pmtint[i].y+","+(pmtint[i].x+pmtint[i].width-1)+","+(pmtint[i].y+pmtint[i].height-1));}
		for (int j=0;j<cmsint.length;j++) {for (int i=0;i<cmsint[0].length;i++){if(cmsint[j][i]==null){cmsint[j][i]=new Rectangle(-1,-1,1,1);}}}
		for (int j=0;j<cmsint[0].length;j++) {System.out.print("JavaRenderEngine: main: cmsint["+j+"]="); for (int i=0;i<cmsint.length;i++) {System.out.print(" "+cmsint[i][j].x+","+cmsint[i][j].y+","+(cmsint[i][j].x+cmsint[i][j].width-1)+","+(cmsint[i][j].y+cmsint[i][j].height-1));} System.out.println();}
		for (int i=0;i<pmsint.length;i++){if(pmsint[i]==null){pmsint[i]=new Rectangle(-1,-1,1,1);}}
		for (int i=0;i<pmsint.length;i++) {System.out.println("JavaRenderEngine: main: pmsint["+i+"]= "+pmsint[i].x+","+pmsint[i].y+","+(pmsint[i].x+pmsint[i].width-1)+","+(pmsint[i].y+pmsint[i].height-1));}
		for (int i=0;i<smsint.length;i++){if(smsint[i]==null){smsint[i]=new Rectangle(-1,-1,1,1);}}
		for (int i=0;i<smsint.length;i++) {System.out.println("JavaRenderEngine: main: smsint["+i+"]= "+smsint[i].x+","+smsint[i].y+","+(smsint[i].x+smsint[i].width-1)+","+(smsint[i].y+smsint[i].height-1));}
		for (int i=0;i<smpint.length;i++){if(smpint[i]==null){smpint[i]=new Coordinate(-1,-1);}}
		for (int i=0;i<smpint.length;i++) {System.out.println("JavaRenderEngine: main: smpint["+i+"]= "+smpint[i].u+","+smpint[i].v);}
		for (int i=0;i<smtint.length;i++){if(smtint[i]==null){smtint[i]=new Rectangle(-1,-1,1,1);}}
		for (int i=0;i<smtint.length;i++) {System.out.println("JavaRenderEngine: main: smtint["+i+"]= "+smtint[i].x+","+smtint[i].y+","+(smtint[i].x+smtint[i].width-1)+","+(smtint[i].y+smtint[i].height-1));}
		Triangle[] sdtri = MathLib.subDivideTriangle(ptri);
		for (int i=0;i<sdtri.length;i++) {System.out.println("JavaRenderEngine: main: sdtri["+i+"]="+sdtri[i].pos1.x+","+sdtri[i].pos1.y+","+sdtri[i].pos1.z+" "+sdtri[i].pos2.x+","+sdtri[i].pos2.y+","+sdtri[i].pos2.z+" "+sdtri[i].pos3.x+","+sdtri[i].pos3.y+","+sdtri[i].pos3.z);}
		Position[] tpoint = {new Position(0.0f,0.0f,0.0f),new Position(-50.0f,30.0f,45.0f),new Position(-8.0f,3.0f,9.0f),new Position(-6.0f,1.0f,4.0f)};
		boolean[] aabbpint = MathLib.vertexAxisAlignedBoundingBoxIntersection(aabb, tpoint);
		for (int i=0;i<aabbpint.length;i++) {System.out.println("JavaRenderEngine: main: aabbpint[i]="+aabbpint[i]);}
		Direction[] aabbdirlist = {new Direction(1,0,0),new Direction(0,1,0),new Direction(1,1,0)};
		AxisAlignedBoundingBox[] aabblist = {aabb,aabb};
		Position cubcam1 = new Position(-10.0f,1.0f,0.0f);
		Position cubcam2 = new Position(1.0f,-10.0f,0.0f);
		Cuboid cuboid = new Cuboid(new Position(aabb.pos1.x,aabb.pos1.y,aabb.pos1.z),new Position(aabb.pos2.x,aabb.pos1.y,aabb.pos1.z),new Position(aabb.pos1.x,aabb.pos2.y,aabb.pos1.z),new Position(aabb.pos2.x,aabb.pos2.y,aabb.pos1.z),new Position(aabb.pos1.x,aabb.pos1.y,aabb.pos2.z),new Position(aabb.pos2.x,aabb.pos1.y,aabb.pos2.z),new Position(aabb.pos1.x,aabb.pos2.y,aabb.pos2.z),new Position(aabb.pos2.x,aabb.pos2.y,aabb.pos2.z));
		Cuboid[] cuboidlist = {cuboid,cuboid};
		Line[][] raabbint = MathLib.rayAxisAlignedBoundingBoxIntersection(cubcam1, aabbdirlist, aabblist);
		Line[][] raabbint2 = MathLib.rayAxisAlignedBoundingBoxIntersection(cubcam2, aabbdirlist, aabblist);
		Line[][] rcubint = MathLib.rayCuboidIntersection(cubcam1, aabbdirlist, cuboidlist);
		Line[][] rcubint2 = MathLib.rayCuboidIntersection(cubcam2, aabbdirlist, cuboidlist);
		for (int j=0;j<raabbint.length;j++) {for (int i=0;i<raabbint[0].length;i++) {if (raabbint[j][i]!=null) {System.out.println("JavaRenderEngine: main: raabbint["+j+"]["+i+"]="+raabbint[j][i].pos1.x+","+raabbint[j][i].pos1.y+","+raabbint[j][i].pos1.z+" "+raabbint[j][i].pos2.x+","+raabbint[j][i].pos2.y+","+raabbint[j][i].pos2.z);} else {System.out.println("JavaRenderEngine: main: raabbint["+j+"]["+i+"]=no hit.");}}}
		for (int j=0;j<raabbint2.length;j++) {for (int i=0;i<raabbint2[0].length;i++) {if (raabbint2[j][i]!=null) {System.out.println("JavaRenderEngine: main: raabbint2["+j+"]["+i+"]="+raabbint2[j][i].pos1.x+","+raabbint2[j][i].pos1.y+","+raabbint2[j][i].pos1.z+" "+raabbint2[j][i].pos2.x+","+raabbint2[j][i].pos2.y+","+raabbint2[j][i].pos2.z);} else {System.out.println("JavaRenderEngine: main: raabbint2["+j+"]["+i+"]=no hit.");}}}
		for (int j=0;j<rcubint.length;j++) {for (int i=0;i<rcubint[0].length;i++) {if (rcubint[j][i]!=null) {System.out.println("JavaRenderEngine: main: rcubint["+j+"]["+i+"]="+rcubint[j][i].pos1.x+","+rcubint[j][i].pos1.y+","+rcubint[j][i].pos1.z+" "+rcubint[j][i].pos2.x+","+rcubint[j][i].pos2.y+","+rcubint[j][i].pos2.z);} else {System.out.println("JavaRenderEngine: main: rcubint["+j+"]["+i+"]=no hit.");}}}
		for (int j=0;j<rcubint2.length;j++) {for (int i=0;i<rcubint2[0].length;i++) {if (rcubint2[j][i]!=null) {System.out.println("JavaRenderEngine: main: rcubint2["+j+"]["+i+"]="+rcubint2[j][i].pos1.x+","+rcubint2[j][i].pos1.y+","+rcubint2[j][i].pos1.z+" "+rcubint2[j][i].pos2.x+","+rcubint2[j][i].pos2.y+","+rcubint2[j][i].pos2.z);} else {System.out.println("JavaRenderEngine: main: rcubint2["+j+"]["+i+"]=no hit.");}}}
		Position prjpoint = new Position(0.0f,0.0f,0.0f);
		Position[] prjpoints = {new Position(5.0f,0.0f,0.0f),new Position(5.0f,0.0f,-5.0f),new Position(0.0f,0.0f,-5.0f)};
		Matrix prjmat = MathLib.rotationMatrix(0.0f, 0.0f, 0.0f);
		Coordinate[] prjcoords = MathLib.projectedPoint(prjpoint, prjpoints, 64, 90.0f, 64, 90.0f, prjmat, null);
		for (int i=0;i<prjcoords.length;i++) { if(prjcoords[i]!=null){System.out.println("JavaRenderEngine: main: prjcoords["+i+"]="+prjcoords[i].u+" "+prjcoords[i].v);}else{System.out.println("JavaRenderEngine: main: prjcoords["+i+"]=not visible.");}}
		Position rpdpos = new Position(0.0f,0.0f,0.0f); 
		Position rpdpos2 = new Position(0.0f,0.0f,1.0f); 
		Direction[] rpddir = {new Direction(1.0f,0.0f,0.0f),new Direction(1.0f,0.0f,1.0f)};
		Position[] rpdpoint = {new Position(1.0f,0.0f,0.0f),new Position(1.0f,0.0f,0.0f),new Position(1.0f,0.0f,0.0f)};
		double[][] rptdist = MathLib.rayPointDistance(rpdpos, rpddir, rpdpoint);
		double[][] rptdist2 = MathLib.rayPointDistance(rpdpos2, rpddir, rpdpoint);
		for (int j=0;j<rptdist.length;j++) {for (int i=0;i<rptdist[0].length;i++) {System.out.println("JavaRenderEngine: main: rptdist["+j+"]["+i+"]= "+rptdist[j][i]);}}
		for (int j=0;j<rptdist2.length;j++) {for (int i=0;i<rptdist2[0].length;i++) {System.out.println("JavaRenderEngine: main: rptdist2["+j+"]["+i+"]= "+rptdist2[j][i]);}}
		double vsindn45 = MathLib.sind(-45.0f); System.out.println("JavaRenderEngine: main: vsindn45="+vsindn45);
		double vsind0 = MathLib.sind(0.0f); System.out.println("JavaRenderEngine: main: vsind0="+vsind0);
		double vsind45 = MathLib.sind(45.0f); System.out.println("JavaRenderEngine: main: vsind45="+vsind45);
		double vsind90 = MathLib.sind(90.0f); System.out.println("JavaRenderEngine: main: vsind90="+vsind90);
		double vsind180 = MathLib.sind(180.0f); System.out.println("JavaRenderEngine: main: vsind180="+vsind180);
		double vsind270 = MathLib.sind(270.0f); System.out.println("JavaRenderEngine: main: vsind270="+vsind270);
		double vsind360 = MathLib.sind(360.0f); System.out.println("JavaRenderEngine: main: vsind360="+vsind360);
		double vasind0 = MathLib.asind(0.0f); System.out.println("JavaRenderEngine: main: vasind0="+vasind0);
		double vasindn1 = MathLib.asind(-1.0f); System.out.println("JavaRenderEngine: main: vasindn1="+vasindn1);
		double vasindnis2 = MathLib.asind(-1.0f/Math.sqrt(2.0f)); System.out.println("JavaRenderEngine: main: vasindnis2="+vasindnis2);
		double vasindp1 = MathLib.asind(1.0f); System.out.println("JavaRenderEngine: main: vasindp1="+vasindp1);
		double vasindpis2 = MathLib.asind(1.0f/Math.sqrt(2.0f)); System.out.println("JavaRenderEngine: main: vasindpis2="+vasindpis2);
		double vcosdn45 = MathLib.cosd(-45.0f); System.out.println("JavaRenderEngine: main: vcosdn45="+vcosdn45);
		double vcosd0 = MathLib.cosd(0.0f); System.out.println("JavaRenderEngine: main: vcosd0="+vcosd0);
		double vcosd45 = MathLib.cosd(45.0f); System.out.println("JavaRenderEngine: main: vcosd45="+vcosd45);
		double vcosd90 = MathLib.cosd(90.0f); System.out.println("JavaRenderEngine: main: vcosd90="+vcosd90);
		double vcosd180 = MathLib.cosd(180.0f); System.out.println("JavaRenderEngine: main: vcosd180="+vcosd180);
		double vcosd270 = MathLib.cosd(270.0f); System.out.println("JavaRenderEngine: main: vcosd270="+vcosd270);
		double vcosd360 = MathLib.cosd(360.0f); System.out.println("JavaRenderEngine: main: vcosd360="+vcosd360);
		double vacosd0 = MathLib.acosd(0.0f); System.out.println("JavaRenderEngine: main: vacosd0="+vacosd0);
		double vacosdn1 = MathLib.acosd(-1.0f); System.out.println("JavaRenderEngine: main: vacosdn1="+vacosdn1);
		double vacosdnis2 = MathLib.acosd(-1.0f/Math.sqrt(2.0f)); System.out.println("JavaRenderEngine: main: vacosdnis2="+vacosdnis2);
		double vacosdp1 = MathLib.acosd(1.0f); System.out.println("JavaRenderEngine: main: vacosdp1="+vacosdp1);
		double vacosdpis2 = MathLib.acosd(1.0f/Math.sqrt(2.0f)); System.out.println("JavaRenderEngine: main: vacosdpis2="+vacosdpis2);
		double vtandn45 = MathLib.tand(-45.0f); System.out.println("JavaRenderEngine: main: vtandn45="+vtandn45);
		double vtand0 = MathLib.tand(0.0f); System.out.println("JavaRenderEngine: main: vtand0="+vtand0);
		double vtand45 = MathLib.tand(45.0f); System.out.println("JavaRenderEngine: main: vtand45="+vtand45);
		double vtand90 = MathLib.tand(90.0f); System.out.println("JavaRenderEngine: main: vtand90="+vtand90);
		double vtand180 = MathLib.tand(180.0f); System.out.println("JavaRenderEngine: main: vtand180="+vtand180);
		double vtand270 = MathLib.tand(270.0f); System.out.println("JavaRenderEngine: main: vtand270="+vtand270);
		double vtand360 = MathLib.tand(360.0f); System.out.println("JavaRenderEngine: main: vtand360="+vtand360);
		double vatand0 = MathLib.atand(0.0f); System.out.println("JavaRenderEngine: main: vatand0="+vatand0);
		double vatandn1 = MathLib.atand(-1.0f); System.out.println("JavaRenderEngine: main: vatandn1="+vatandn1);
		double vatandnis2 = MathLib.atand(-1.0f/Math.sqrt(2.0f)); System.out.println("JavaRenderEngine: main: vatandnis2="+vatandnis2);
		double vatandp1 = MathLib.atand(1.0f); System.out.println("JavaRenderEngine: main: vatandp1="+vatandp1);
		double vatandpis2 = MathLib.atand(1.0f/Math.sqrt(2.0f)); System.out.println("JavaRenderEngine: main: vatandpis2="+vatandpis2);
		Position vrqipos = new Position(0.0f,0.0f,0.0f);
		Direction[] vrqidir = {new Direction(0.0f,-3.0f,0.0f),new Direction(0.0f,-3.0f,1.0f)};
		Position[] vrqipoints = {new Position(0.0f,-3.0f,0.0f),new Position(0.0f,-3.0f,1.0f),new Position(1.0f,-3.0f,1.0f),new Position(1.0f,-3.0f,0.0f)};
		Line vrqiline = new Line(vrqipoints[0],vrqipoints[1]);
		Triangle vrqitri = new Triangle(vrqipoints[0],vrqipoints[1],vrqipoints[2]);
		Quad vrqiquad = new Quad(vrqipoints[0],vrqipoints[1],vrqipoints[2],vrqipoints[3]);
		Line[] vrqilines = {vrqiline, vrqiline, vrqiline};
		Triangle[] vrqitris = {vrqitri, vrqitri, vrqitri};
		Quad[] vrqiquads = {vrqiquad, vrqiquad, vrqiquad};
		Matrix vrqimat = MathLib.rotationMatrix(-90, 0, 0);
		Position[][] rqint = MathLib.rayQuadIntersection(vrqipos, vrqidir, vrqiquads);
		Coordinate[][] prjlcoords = MathLib.projectedLine(vrqipos, vrqilines, 64, 90, 64, 90, vrqimat, null);
		Coordinate[][] prjtcoords = MathLib.projectedTriangle(vrqipos, vrqitris, 64, 90, 64, 90, vrqimat, null);
		Coordinate[][] prjqcoords = MathLib.projectedQuad(vrqipos, vrqiquads, 64, 90, 64, 90, vrqimat, null);
		for (int j=0;j<rqint.length;j++) {for (int i=0;i<rqint[0].length;i++) {if(rqint[j][i]!=null){System.out.println("JavaRenderEngine: main: rqint["+i+"]["+j+"]="+rqint[j][i].x+" "+rqint[j][i].y+" "+rqint[j][i].z);}else{System.out.println("JavaRenderEngine: main: rqint["+i+"]["+j+"]=no hit.");}}}
		for (int j=0;j<prjlcoords.length;j++) { for (int i=0;i<prjlcoords[j].length;i++) { if(prjlcoords[j][i]!=null){System.out.println("JavaRenderEngine: main: prjlcoords["+j+"]["+i+"]="+prjlcoords[j][i].u+","+prjlcoords[j][i].v);}else{System.out.println("JavaRenderEngine: main: prjlcoords["+j+"]["+i+"]=not visible.");}}}
		for (int j=0;j<prjtcoords.length;j++) { for (int i=0;i<prjtcoords[j].length;i++) { if(prjtcoords[j][i]!=null){System.out.println("JavaRenderEngine: main: prjtcoords["+j+"]["+i+"]="+prjtcoords[j][i].u+","+prjtcoords[j][i].v);}else{System.out.println("JavaRenderEngine: main: prjtcoords["+j+"]["+i+"]=not visible.");}}}
		for (int j=0;j<prjqcoords.length;j++) { for (int i=0;i<prjqcoords[j].length;i++) { if(prjqcoords[j][i]!=null){System.out.println("JavaRenderEngine: main: prjqcoords["+j+"]["+i+"]="+prjqcoords[j][i].u+","+prjqcoords[j][i].v);}else{System.out.println("JavaRenderEngine: main: prjqcoords["+j+"]["+i+"]=not visible.");}}}
		String[] stringstosplit = {"0//0", "0/0/0", "0//"};
		for (int j=0;j<stringstosplit.length;j++) {String[] stringsplit = stringstosplit[j].split("/");System.out.print("JavaRenderEngine: main: stringstosplit["+j+"]=\""+stringsplit.length+"\","+stringstosplit[j]+":");for (int i=0;i<stringsplit.length;i++) { if(!stringsplit[i].isBlank()) {System.out.print(" "+Integer.parseInt(stringsplit[i]));}}System.out.println();}
		Rectangle[] negrect = {new Rectangle(0,0,-5,-5),new Rectangle(0,0,0,0),new Rectangle(0,0,-1,-1),new Rectangle(0,0,1,1)};
		for (int i=0;i<negrect.length;i++) {System.out.println("JavaRenderEngine: main: negrect["+i+"]= "+negrect[i].x+","+negrect[i].y+","+(negrect[i].x+negrect[i].width-1)+","+(negrect[i].y+negrect[i].height-1));}
		Position vpos = new Position(0.0f,0.0f,0.0f);
		Position lpos1 = new Position(3.0f,0.0f,-1.0f);
		Position lpos2 = new Position(3.0f,0.0f,1.0f);
		Line vline = new Line(lpos1,lpos2);
		Line[] vlines = {vline, vline};
		double[] projangles = MathLib.projectedAngles(8, 90);
		double[][] lenfrac = MathLib.linearAngleLengthInterpolation(vpos, vlines, projangles);
		for (int j=0;j<lenfrac.length;j++) {for (int i=0;i<lenfrac[j].length;i++) {System.out.println("JavaRenderEngine: main: lenfrac["+j+"]["+i+"]="+lenfrac[j][i]);}}
		Position[] rotpos = {new Position(-1,0,0), new Position(1,0,0)};
		Direction axisaround = new Direction(0.0f,-1.0f,0.0f);
		Direction lookatdir = new Direction(1.0f,0.0f,0.0f);
		Matrix zerorot = MathLib.rotationMatrix(0.0f, 0.0f, 0.0f);
		Direction[] camdirs = MathLib.projectedCameraDirections(zerorot);
		Matrix rotposmat1 = MathLib.rotationMatrixAroundAxis(axisaround, -90.0f);
		Matrix rotposmat2 = MathLib.rotationMatrixLookDir(lookatdir, 0.0f);
		Position[] rotpos1 = MathLib.matrixMultiply(rotpos, rotposmat1);
		Direction[] rotdir2 = MathLib.matrixMultiply(camdirs, rotposmat2);
		for (int i=0;i<rotpos1.length;i++) {System.out.println("JavaRenderEngine: main: rotpos1="+rotpos1[i].x+" "+rotpos1[i].y+" "+rotpos1[i].z);}
		for (int i=0;i<rotdir2.length;i++) {System.out.println("JavaRenderEngine: main: rotdir2="+rotdir2[i].dx+" "+rotdir2[i].dy+" "+rotdir2[i].dz);}
		Position[] smpcpos = {new Position(0.0f,-2.0f,0.5f), new Position(0.0f,-3.0f,0.0f), new Position(1.0f,-3.0f,0.0f), new Position(0.0f,-3.0f,1.0f), new Position(1.0f,0.0f,0.0f), new Position(1.0f,1.0f,0.0f), new Position(1.0f,0.0f,1.0f)};
		Direction[] smrdir = {new Direction(0.0f, -1.0f, 0.0f), new Direction(-1.0f, -1.0f, 0.0f), new Direction(1.0f, -1.0f, 0.0f), new Direction(0.0f, -1.0f, 1.0f), new Direction(0.0f, -1.0f, -1.0f)};
		Direction[] smprdir = {new Direction(1.0f, 0.0f, 0.0f), new Direction(1.0f, -1.0f, 0.0f), new Direction(1.0f, 1.0f, 0.0f), new Direction(1.0f, 0.0f, 0.0f), new Direction(1.0f, 0.0f, 0.0f)};
		Ray[] smrray = {new Ray(smpcpos[0], smrdir[0]), new Ray(smpcpos[0], smrdir[1]), new Ray(smpcpos[0], smrdir[2]), new Ray(smpcpos[0], smrdir[3]), new Ray(smpcpos[0], smrdir[4])};
		Plane[] smrdirplane = MathLib.planeFromNormalAtPoint(smpcpos[0], smprdir);
		PlaneRay[] smplaneray = {new PlaneRay(smpcpos[0],smrdir[0],smrdirplane[0],90.0f), new PlaneRay(smpcpos[0],smrdir[1],smrdirplane[1],90.0f), new PlaneRay(smpcpos[0],smrdir[2],smrdirplane[2],90.0f), new PlaneRay(smpcpos[0],smrdir[3],smrdirplane[3],90.0f), new PlaneRay(smpcpos[0],smrdir[4],smrdirplane[4],90.0f)};
		Matrix smpcrot = MathLib.rotationMatrix(-90.0f, 0.0f, 0.0f);
		Triangle smpctri1 = new Triangle(smpcpos[1],smpcpos[2],smpcpos[3]);
		Triangle smpctri2 = new Triangle(smpcpos[4],smpcpos[5],smpcpos[6]);
		Triangle[] smpctris = {smpctri1, smpctri2};
		Plane[] smpcplanes = MathLib.trianglePlane(smpctris);
		RenderView[] smpc = MathLib.surfaceMirrorProjectedCamera(smpcpos[0], smpcplanes, 90, 90, smpcrot);
		RenderView[] srpc = MathLib.surfaceRefractionProjectedCamera(smpcpos[0], smpcplanes, 64, 70, 64, 43, smpcrot, 1.0f, 1.45f);
		RenderView[] srpc2 = MathLib.surfaceRefractionProjectedCamera(smpcpos[0], smpcplanes, 64, 70, 64, 43, smpcrot, 1.45f, 1.0f);
		Ray[][] smrmray = MathLib.surfaceMirrorRay(smrray, smpcplanes);
		Ray[][] smrrray = MathLib.surfaceRefractionRay(smrray, smpcplanes, 1.0f, 1.45f);
		Ray[][] smrrray2 = MathLib.surfaceRefractionRay(smrray, smpcplanes, 1.45f, 1.0f);
		PlaneRay[][] smpray = MathLib.surfaceMirrorPlaneRay(smplaneray, smpcplanes);
		PlaneRay[][] srpray = MathLib.surfaceRefractionPlaneRay(smplaneray, smpcplanes, 1.0f, 1.45f);
		for (int i=0;i<smpc.length;i++) {if (smpc[i]!=null) {System.out.println("JavaRenderEngine: main: smpc["+i+"] pos="+smpc[i].pos.x+" "+smpc[i].pos.y+" "+smpc[i].pos.z+" hfov="+smpc[i].hfov+" vfov="+smpc[i].vfov);System.out.println("JavaRenderEngine: main: smpc["+i+"] a1="+smpc[i].rot.a11+" "+smpc[i].rot.a12+" "+smpc[i].rot.a13);System.out.println("JavaRenderEngine: main: smpc["+i+"] a2="+smpc[i].rot.a21+" "+smpc[i].rot.a22+" "+smpc[i].rot.a23);System.out.println("JavaRenderEngine: main: smpc["+i+"] a3="+smpc[i].rot.a31+" "+smpc[i].rot.a32+" "+smpc[i].rot.a33);} else {System.out.println("JavaRenderEngine: main: smpc["+i+"]=not visible.");}}
		for (int i=0;i<srpc.length;i++) {if (srpc[i]!=null) {System.out.println("JavaRenderEngine: main: srpc["+i+"] pos="+srpc[i].pos.x+" "+srpc[i].pos.y+" "+srpc[i].pos.z+" hfov="+srpc[i].hfov+" vfov="+srpc[i].vfov);System.out.println("JavaRenderEngine: main: srpc["+i+"] a1="+srpc[i].rot.a11+" "+srpc[i].rot.a12+" "+srpc[i].rot.a13);System.out.println("JavaRenderEngine: main: srpc["+i+"] a2="+srpc[i].rot.a21+" "+srpc[i].rot.a22+" "+srpc[i].rot.a23);System.out.println("JavaRenderEngine: main: srpc["+i+"] a3="+srpc[i].rot.a31+" "+srpc[i].rot.a32+" "+srpc[i].rot.a33);} else {System.out.println("JavaRenderEngine: main: srpc["+i+"]=not visible.");}}
		for (int i=0;i<srpc2.length;i++) {if (srpc2[i]!=null) {System.out.println("JavaRenderEngine: main: srpc2["+i+"] pos="+srpc2[i].pos.x+" "+srpc2[i].pos.y+" "+srpc2[i].pos.z+" hfov="+srpc2[i].hfov+" vfov="+srpc2[i].vfov);System.out.println("JavaRenderEngine: main: srpc2["+i+"] a1="+srpc2[i].rot.a11+" "+srpc2[i].rot.a12+" "+srpc2[i].rot.a13);System.out.println("JavaRenderEngine: main: srpc2["+i+"] a2="+srpc2[i].rot.a21+" "+srpc2[i].rot.a22+" "+srpc2[i].rot.a23);System.out.println("JavaRenderEngine: main: srpc2["+i+"] a3="+srpc2[i].rot.a31+" "+srpc2[i].rot.a32+" "+srpc2[i].rot.a33);} else {System.out.println("JavaRenderEngine: main: srpc2["+i+"]=not visible.");}}
		for (int j=0;j<smrmray.length;j++) {for (int i=0;i<smrmray[j].length;i++) {if (smrmray[j][i]!=null) {System.out.println("JavaRenderEngine: main: smrmray["+j+"]["+i+"] pos="+smrmray[j][i].pos.x+" "+smrmray[j][i].pos.y+" "+smrmray[j][i].pos.z+" dir="+smrmray[j][i].dir.dx+" "+smrmray[j][i].dir.dy+" "+smrmray[j][i].dir.dz);}else{System.out.println("JavaRenderEngine: main: smrmray["+j+"]["+i+"]=not visible.");}}}
		for (int j=0;j<smrrray.length;j++) {for (int i=0;i<smrrray[j].length;i++) {if (smrrray[j][i]!=null) {System.out.println("JavaRenderEngine: main: smrrray["+j+"]["+i+"] pos="+smrrray[j][i].pos.x+" "+smrrray[j][i].pos.y+" "+smrrray[j][i].pos.z+" dir="+smrrray[j][i].dir.dx+" "+smrrray[j][i].dir.dy+" "+smrrray[j][i].dir.dz);}else{System.out.println("JavaRenderEngine: main: smrrray["+j+"]["+i+"]=not visible.");}}}
		for (int j=0;j<smrrray2.length;j++) {for (int i=0;i<smrrray2[j].length;i++) {if (smrrray2[j][i]!=null) {System.out.println("JavaRenderEngine: main: smrrray2["+j+"]["+i+"] pos="+smrrray2[j][i].pos.x+" "+smrrray2[j][i].pos.y+" "+smrrray2[j][i].pos.z+" dir="+smrrray2[j][i].dir.dx+" "+smrrray2[j][i].dir.dy+" "+smrrray2[j][i].dir.dz);}else{System.out.println("JavaRenderEngine: main: smrrray2["+j+"]["+i+"]=not visible.");}}}
		for (int j=0;j<smpray.length;j++) {for (int i=0;i<smpray[j].length;i++) {if (smpray[j][i]!=null) {System.out.println("JavaRenderEngine: main: smpray["+j+"]["+i+"] pos="+smpray[j][i].pos.x+" "+smpray[j][i].pos.y+" "+smpray[j][i].pos.z+" dir="+smpray[j][i].dir.dx+" "+smpray[j][i].dir.dy+" "+smpray[j][i].dir.dz+" plane="+smpray[j][i].plane.a+" "+smpray[j][i].plane.b+" "+smpray[j][i].plane.c+" "+smpray[j][i].plane.d+" vfov="+smpray[j][i].vfov);}else{System.out.println("JavaRenderEngine: main: smpray["+j+"]["+i+"]=not visible.");}}}
		for (int j=0;j<srpray.length;j++) {for (int i=0;i<srpray[j].length;i++) {if (srpray[j][i]!=null) {System.out.println("JavaRenderEngine: main: srpray["+j+"]["+i+"] pos="+srpray[j][i].pos.x+" "+srpray[j][i].pos.y+" "+srpray[j][i].pos.z+" dir="+srpray[j][i].dir.dx+" "+srpray[j][i].dir.dy+" "+srpray[j][i].dir.dz+" plane="+srpray[j][i].plane.a+" "+srpray[j][i].plane.b+" "+srpray[j][i].plane.c+" "+srpray[j][i].plane.d+" vfov="+srpray[j][i].vfov);}else{System.out.println("JavaRenderEngine: main: srpray["+j+"]["+i+"]=not visible.");}}}
		Position papos = new Position(0.0f,0.0f,0.0f);
		Direction[] padir = {new Direction(1.0f,0.0f,0.0f),new Direction(0.0f,-1.0f,0.0f)};
		Plane[] paplane = MathLib.planeFromNormalAtPoint(papos, padir);
		double[] paangles = MathLib.planeAngle(paplane[0], paplane);
		for (int i=0;i<paangles.length;i++) {System.out.println("JavaRenderEngine: main: paangles[i]="+paangles[i]);}
		Position[] trpos1 = {new Position(3.0f, 2.0f, 1.0f)};
		Position trposto = new Position(1.0f, 1.0f, 1.0f);
		Position[] trpos2 = MathLib.translate(trpos1, trposto);
		trpos2 = MathLib.translate(trpos1, trposto);
		trpos2 = MathLib.translate(trpos1, trposto);
		for (int i=0;i<trpos2.length;i++) {System.out.println("JavaRenderEngine: main: trpos2="+trpos2[i].x+" "+trpos2[i].y+" "+trpos2[i].z);}
		Position[] ttplanepos = {new Position(0,0,0), new Position(1,0,0)};
		Direction[] ttplanenorm = {new Direction(1.0f,0,0)};
		Direction ttplanedir = new Direction(1.0f,0,0);
		Plane[] ttplane = MathLib.planeFromNormalAtPoint(ttplanepos[0], ttplanenorm);
		Plane[] ttplanes = {ttplane[0],ttplane[0],ttplane[0]};
		Position[] poplane = MathLib.pointOnPlane(ttplanes);
		Plane[] ttplanetr1 = MathLib.translate(ttplanes, ttplanedir, 0.1f);
		Plane[] ttplanetr2 = MathLib.translate(ttplanes, ttplanepos[1]);
		for (int i=0;i<poplane.length;i++) {System.out.println("JavaRenderEngine: main: poplane="+poplane[i].x+" "+poplane[i].y+" "+poplane[i].z);}
		for (int i=0;i<ttplanetr1.length;i++) {System.out.println("JavaRenderEngine: main: ttplanetr1="+ttplanetr1[i].a+" "+ttplanetr1[i].b+" "+ttplanetr1[i].c+" "+ttplanetr1[i].d);}
		for (int i=0;i<ttplanetr2.length;i++) {System.out.println("JavaRenderEngine: main: ttplanetr2="+ttplanetr2[i].a+" "+ttplanetr2[i].b+" "+ttplanetr2[i].c+" "+ttplanetr2[i].d);}
		Position vplanepos = new Position(0.0f,0.0f,0.0f);
		Direction[] vplanedir1 = {new Direction(1.0f,0.0f,0.0f), new Direction(0.0f,-1.0f,0.0f), new Direction(-1.0f,0.0f,0.0f), new Direction(0.0f,1.0f,0.0f), new Direction(-1.0f,0.0f,-1.0f), new Direction(1.0f,1.0f,0.0f), new Direction(1.0f,-1.0f,0.0f)};
		Direction[] vplanedir2 = {new Direction(0.0f,1.0f,0.0f), new Direction(-1.0f,0.0f,0.0f), new Direction(0.0f,-1.0f,0.0f), new Direction(1.0f,0.0f,0.0f), new Direction(0.0f,-1.0f,1.0f), new Direction(1.0f,1.0f,0.0f), new Direction(1.0f,-1.0f,0.0f)};
		Direction[] vplanedir3 = {new Direction(0.0f,0.0f,1.0f), new Direction(0.0f,-1.0f,0.0f), new Direction(0.0f,0.0f,-1.0f), new Direction(0.0f,1.0f,0.0f), new Direction(-1.0f,0.0f,-1.0f), new Direction(0.0f,1.0f,1.0f), new Direction(0.0f,-1.0f,1.0f)};
		Plane[] vppintplane1 = MathLib.planeFromNormalAtPoint(vplanepos, vplanedir1);
		Plane[] vppintplane2 = MathLib.planeFromNormalAtPoint(vplanepos, vplanedir2);
		Plane[] vppintplane3 = MathLib.planeFromNormalAtPoint(vplanepos, vplanedir3);
		Plane[] vppintplane11 = {vppintplane1[0]};
		Plane[] vppintplane12 = {vppintplane1[0], vppintplane1[1], vppintplane1[2], vppintplane1[3], vppintplane1[4], vppintplane1[5], vppintplane1[6]};
		Plane[] vppintplane21 = {vppintplane2[0]};
		Plane[] vppintplane22 = {vppintplane2[0], vppintplane2[1], vppintplane2[2], vppintplane2[3], vppintplane2[4], vppintplane2[5], vppintplane2[6]};
		Plane[] vppintplane31 = {vppintplane3[0]};
		Plane[] vppintplane32 = {vppintplane3[0], vppintplane3[1], vppintplane3[2], vppintplane3[3], vppintplane3[4], vppintplane3[5], vppintplane3[6]};
		Line[][] vppint1 = MathLib.planePlaneIntersection(vppintplane11, vppintplane12);
		Line[][] vppint2 = MathLib.planePlaneIntersection(vppintplane21, vppintplane22);
		Line[][] vppint3 = MathLib.planePlaneIntersection(vppintplane31, vppintplane32);
		Line[][] vppint1okvals = {{null, new Line(new Position(0.0f, 0.0f, 0.0f), new Position(0.0f, 0.0f, -1.0f)), null, new Line(new Position(0.0f, 0.0f, 0.0f), new Position(0.0f, 0.0f, 1.0f)), new Line(new Position(0.0f, 0.0f, 0.0f), new Position(0.0f, 1.0f, 0.0f)), new Line(new Position(0.0f, 0.0f, 0.0f), new Position(0.0f, 0.0f, 1.0f)), new Line(new Position(0.0f, 0.0f, 0.0f), new Position(0.0f, 0.0f, -1.0f))}};
		Line[][] vppint2okvals = {{null, new Line(new Position(0.0f, 0.0f, 0.0f), new Position(0.0f, 0.0f, 1.0f)), null, new Line(new Position(0.0f, 0.0f, 0.0f), new Position(0.0f, 0.0f, -1.0f)), new Line(new Position(0.0f, 0.0f, 0.0f), new Position(1.0f, 0.0f, 0.0f)), new Line(new Position(0.0f, 0.0f, 0.0f), new Position(0.0f, 0.0f, -1.0f)), new Line(new Position(0.0f, 0.0f, 0.0f), new Position(0.0f, 0.0f, -1.0f))}};
		Line[][] vppint3okvals = {{null, new Line(new Position(0.0f, 0.0f, 0.0f), new Position(1.0f, 0.0f, 0.0f)), null, new Line(new Position(0.0f, 0.0f, 0.0f), new Position(-1.0f, 0.0f, 0.0f)), new Line(new Position(0.0f, 0.0f, 0.0f), new Position(0.0f, -1.0f, 0.0f)), new Line(new Position(0.0f, 0.0f, 0.0f), new Position(-1.0f, 0.0f, 0.0f)), new Line(new Position(0.0f, 0.0f, 0.0f), new Position(1.0f, 0.0f, 0.0f))}};
		for (int j=0;j<vppint1.length;j++) {for (int i=0;i<vppint1[j].length;i++) {System.out.print("JavaRenderEngine: main: vppint1["+j+"]["+i+"]="); if (vppint1[j][i]!=null) {System.out.print(vppint1[j][i].pos1.x+" "+vppint1[j][i].pos1.y+" "+vppint1[j][i].pos1.z+" "+vppint1[j][i].pos2.x+" "+vppint1[j][i].pos2.y+" "+vppint1[j][i].pos2.z);} else {System.out.print("equal plane."); } if (((vppint1[j][i]==vppint1okvals[j][i]))||(vppint1[j][i].equals(vppint1okvals[j][i]))) {System.out.println(" [match]");} else {System.out.println(" **no match**");} }}
		for (int j=0;j<vppint2.length;j++) {for (int i=0;i<vppint2[j].length;i++) {System.out.print("JavaRenderEngine: main: vppint2["+j+"]["+i+"]="); if (vppint2[j][i]!=null) {System.out.print(vppint2[j][i].pos1.x+" "+vppint2[j][i].pos1.y+" "+vppint2[j][i].pos1.z+" "+vppint2[j][i].pos2.x+" "+vppint2[j][i].pos2.y+" "+vppint2[j][i].pos2.z);} else {System.out.print("equal plane."); } if (((vppint2[j][i]==vppint2okvals[j][i]))||(vppint2[j][i].equals(vppint2okvals[j][i]))) {System.out.println(" [match]");} else {System.out.println(" **no match**");} }}
		for (int j=0;j<vppint3.length;j++) {for (int i=0;i<vppint3[j].length;i++) {System.out.print("JavaRenderEngine: main: vppint3["+j+"]["+i+"]="); if (vppint3[j][i]!=null) {System.out.print(vppint3[j][i].pos1.x+" "+vppint3[j][i].pos1.y+" "+vppint3[j][i].pos1.z+" "+vppint3[j][i].pos2.x+" "+vppint3[j][i].pos2.y+" "+vppint3[j][i].pos2.z);} else {System.out.print("equal plane."); } if (((vppint3[j][i]==vppint3okvals[j][i]))||(vppint3[j][i].equals(vppint3okvals[j][i]))) {System.out.println(" [match]");} else {System.out.println(" **no match**");} }}
		double[] modnum = {-0.5f, 1.3f, 0.25f, 5.1f, 3.4f};
		double[] modnumout1 = {MathLib.mod(modnum[0], 1.5f), MathLib.mod(modnum[1], 1.5f), MathLib.mod(modnum[2], 1.5f), MathLib.mod(modnum[3], 1.5f), MathLib.mod(modnum[4], 1.5f)};
		double[] modnumout2 = {MathLib.mod(modnum[0], 2.0f), MathLib.mod(modnum[1], 2.0f), MathLib.mod(modnum[2], 2.0f), MathLib.mod(modnum[3], 2.0f), MathLib.mod(modnum[4], 2.0f)};
		Coordinate[] texreps = {new Coordinate(0.5f, -0.4f), new Coordinate(1.5f, -1.4f)};
		Coordinate[] texrepmodeone = MathLib.mod(texreps);
		for (int i=0;i<modnumout1.length;i++) {System.out.println("JavaRenderEngine: main: modnumout1[i]="+modnumout1[i]);}
		for (int i=0;i<modnumout2.length;i++) {System.out.println("JavaRenderEngine: main: modnumout2[i]="+modnumout2[i]);}
		for (int i=0;i<texrepmodeone.length;i++) {System.out.println("JavaRenderEngine: main: texrepmodeone[i]="+texrepmodeone[i].u+" "+texrepmodeone[i].v);}
		Position[] rrintvpos1 = {new Position(0,0,0)};
		Direction[] rrintvdir1 = {new Direction(1,1,0)};
		Position[] rrintvpos2 = {new Position(1,0,0), new Position(1,0,0)};
		Direction[] rrintvdir2 = {new Direction(0,1,0), new Direction(0,1,1)};
		Ray[] rrintvray1 = {new Ray(rrintvpos1[0], rrintvdir1[0])};
		Ray[] rrintvray2 = {new Ray(rrintvpos2[0], rrintvdir2[0]), new Ray(rrintvpos2[1], rrintvdir2[1])};
		Position[][] rrint = MathLib.rayRayIntersection(rrintvray1, rrintvray2);
		for (int j=0;j<rrint.length;j++) {for (int i=0;i<rrint[j].length;i++) { if (rrint[j][i]!=null) {System.out.println("JavaRenderEngine: main: rrint["+j+"]["+i+"]="+rrint[j][i].x+" "+rrint[j][i].y+" "+rrint[j][i].z);} else {System.out.println("JavaRenderEngine: main: rrint["+j+"]["+i+"]=no hit.");}}}
		Double[] snum = {1.0d, -5.0d, 2.0d, 0.0d, 7.0d};
		Integer[] snumind = UtilLib.objectIndexSort(snum,null);
		System.out.print("JavaRenderEngine: main: snumind="); for (int i=0;i<snumind.length;i++) {System.out.print(" "+snumind[i]);}System.out.println();
		System.out.print("JavaRenderEngine: main: snumval="); for (int i=0;i<snumind.length;i++) {System.out.print(" "+snum[snumind[i]]);}System.out.println();
		double[] refanglein = {0.0f, 15.0f, 30.0f, 45.0f};
		for (int i=0;i<refanglein.length;i++) {double refangleout1 = MathLib.refractionOutAngle(refanglein[i], 1.0f, 1.45f);double refangleout2 = MathLib.refractionOutAngle(refanglein[i], 1.45f, 1.0f);System.out.println("JavaRenderEngine: main: refanglein: refangleout1="+refangleout1+" refangleout2="+refangleout2);}
		
		new JavaRenderEngine();
	}
	
	public static abstract class AppHandlerPanel extends JPanel implements KeyListener,MouseListener,MouseMotionListener,MouseWheelListener {
		private static final long serialVersionUID = 1L;
		public int lastrenderwidth = 0, lastrenderheight = 0;
		public long newupdate = System.currentTimeMillis();
		public long lastupdate = newupdate;
		public long ticktime = 0;
		public double ticktimesec = 0.0f;
		public double ticktimefps = 0.0f;
		public GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		public GraphicsDevice gd = ge.getDefaultScreenDevice();
		public GraphicsConfiguration gc = gd.getDefaultConfiguration();
		public Toolkit tk = Toolkit.getDefaultToolkit();
		public Clipboard cb = tk.getSystemClipboard();
		
		@Override public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if ((this.lastrenderwidth!=this.getWidth())||(this.lastrenderheight!=this.getHeight())) {
				this.lastrenderwidth = this.getWidth();
				this.lastrenderheight = this.getHeight();
				System.out.println("JavaRenderEngine: main: Window: Resolution "+this.getWidth()+"x"+this.getHeight());
			}
			this.newupdate = System.currentTimeMillis();
			this.ticktime = this.newupdate-this.lastupdate;
			this.ticktimesec = ((double)this.ticktime)/1000.0f;
			this.ticktimefps = 1000.0f/((double)this.ticktime);
		}
		public abstract void drop(DropTargetDropEvent dtde);
		public abstract void timerTick();
	}
	
	private class DropTargetHandler extends DropTarget {
		private static final long serialVersionUID = 1L;
		@Override public synchronized void drop(DropTargetDropEvent dtde) {System.out.println("JavaRenderEngine: main: DropTarget: drop");if (JavaRenderEngine.this.activeapp!=null) {JavaRenderEngine.this.activeapp.drop(dtde);}}
	}

	private void setActiveApp(AppHandlerPanel activeappi) {
		int appcanvaswidth = this.defaultimagecanvaswidth; 
		int appcanvasheight = this.defaultimagecanvasheight; 
		if (this.activeapp!=null) {
			this.activeapp.removeMouseListener(this);
			this.activeapp.removeMouseMotionListener(this);
			this.activeapp.removeMouseWheelListener(this);
			this.activeapp.removeKeyListener(this);
			appcanvaswidth = this.activeapp.getWidth();
			appcanvasheight = this.activeapp.getHeight();
		}
		this.activeapp = activeappi;
		this.activeapp.addMouseListener(this);
		this.activeapp.addMouseMotionListener(this);
		this.activeapp.addMouseWheelListener(this);
		this.activeapp.addKeyListener(this);
		this.activeapp.setPreferredSize(new Dimension(appcanvaswidth,appcanvasheight));
		this.activeapp.setSize(appcanvaswidth,appcanvasheight);
		this.setContentPane(this.activeapp);
		this.pack();
		this.activeapp.requestFocusInWindow();
	}
	
	@Override public void actionPerformed(ActionEvent e) {
		if (this.activeapp!=null) {this.activeapp.timerTick();}
		Dimension componentsize=this.activeapp.getSize();
		Rectangle paintregion=new Rectangle(0,0,componentsize.width,componentsize.height);
		this.activeapp.paintImmediately(paintregion);
	}
	@Override public void mouseWheelMoved(MouseWheelEvent e) {if (this.activeapp!=null) {this.activeapp.mouseWheelMoved(e);}}
	@Override public void mouseDragged(MouseEvent e) {if (this.activeapp!=null) {this.activeapp.mouseDragged(e);}}
	@Override public void mouseMoved(MouseEvent e) {if (this.activeapp!=null) {this.activeapp.mouseMoved(e);}}
	@Override public void mouseClicked(MouseEvent e) {if (this.activeapp!=null) {this.activeapp.mouseClicked(e);}}
	@Override public void mousePressed(MouseEvent e) {if (this.activeapp!=null) {this.activeapp.mousePressed(e);}}
	@Override public void mouseReleased(MouseEvent e) {if (this.activeapp!=null) {this.activeapp.mouseReleased(e);}}
	@Override public void mouseEntered(MouseEvent e) {if (this.activeapp!=null) {this.activeapp.mouseEntered(e);}}
	@Override public void mouseExited(MouseEvent e) {if (this.activeapp!=null) {this.activeapp.mouseExited(e);}}
	@Override public void keyTyped(KeyEvent e) {if (this.activeapp!=null) {this.activeapp.keyTyped(e);}}
	@Override public void keyReleased(KeyEvent e) {if (this.activeapp!=null) {this.activeapp.keyReleased(e);}}

	@Override public void keyPressed(KeyEvent e) {
	    int altonmask = KeyEvent.ALT_DOWN_MASK;
	    int altoffmask = KeyEvent.SHIFT_DOWN_MASK|KeyEvent.CTRL_DOWN_MASK;
	    boolean altdownmask = (e.getModifiersEx() & (altonmask | altoffmask)) == altonmask;
		if ((e.getKeyCode()==KeyEvent.VK_ALT)) {
			if (this.activeapp!=null) {this.activeapp.keyPressed(e);}
			e.consume();
		} else if ((e.getKeyCode()==KeyEvent.VK_F4)&&(altdownmask)) {
			System.out.println("JavaRenderEngine: main: keyPressed: ALT+VK_F4: closing software");
		} else if ((e.getKeyCode()==KeyEvent.VK_ENTER)&&(altdownmask)) {
			System.out.println("JavaRenderEngine: main: keyPressed: ALT+VK_ENTER");
	    	this.dispose();
	    	if (!windowedmode) {
	    		windowedmode = true;
	    		this.setExtendedState(this.getExtendedState()&~JFrame.MAXIMIZED_BOTH);
	    		this.setUndecorated(false);
	    		this.activeapp.setPreferredSize(new Dimension(this.defaultimagecanvaswidth,this.defaultimagecanvasheight));
	    		this.activeapp.setSize(this.defaultimagecanvaswidth,this.defaultimagecanvasheight);
	    		this.pack();
	    	}else {
	    		windowedmode = false;
	    		this.setExtendedState(this.getExtendedState()|JFrame.MAXIMIZED_BOTH);
	    		this.setUndecorated(true);
	    	}
	    	this.setVisible(true);
			this.activeapp.requestFocusInWindow();
		}else if (e.getKeyCode()==KeyEvent.VK_F5) {
			System.out.println("JavaRenderEngine: main: keyPressed: VK_F5");
			this.setActiveApp(this.drawapp);
		}else if (e.getKeyCode()==KeyEvent.VK_F6) {
			System.out.println("JavaRenderEngine: main: keyPressed: VK_F6");
			this.setActiveApp(this.cadapp);
		}else if (e.getKeyCode()==KeyEvent.VK_F7) {
			System.out.println("JavaRenderEngine: main: keyPressed: VK_F7");
			this.setActiveApp(this.modelapp);
		}else if (e.getKeyCode()==KeyEvent.VK_F8) {
			System.out.println("JavaRenderEngine: main: keyPressed: VK_F8");
			this.setActiveApp(this.editorapp);
		}else if (e.getKeyCode()==KeyEvent.VK_F9) {
			System.out.println("JavaRenderEngine: main: keyPressed: VK_F9");
			this.setActiveApp(this.gameapp);
		}else if (e.getKeyCode()==KeyEvent.VK_F10) {
			System.out.println("JavaRenderEngine: main: keyPressed: VK_F10");
			//TODO <tbd>
			e.consume();
		}else if (e.getKeyCode()==KeyEvent.VK_F11) {
			System.out.println("JavaRenderEngine: main: keyPressed: VK_F11");
			//TODO <tbd>
		}else if (e.getKeyCode()==KeyEvent.VK_F12) {
			System.out.println("JavaRenderEngine: main: keyPressed: VK_F12");
			VolatileImage componentimage = this.activeapp.gc.createCompatibleVolatileImage(this.activeapp.getWidth(),this.activeapp.getHeight(), Transparency.OPAQUE);
			Graphics2D gfx = componentimage.createGraphics();
			this.activeapp.paintComponent(gfx);
			gfx.dispose();
			File screenshotfile = new File("screenshot1.png");
			int screenshotnum = 1;
			while (screenshotfile.exists()) {
				screenshotnum += 1;
				screenshotfile = new File("screenshot"+screenshotnum+".png");
			}
			UtilLib.saveImageFormat(screenshotfile.getPath(), componentimage, new UtilLib.ImageFileFilters.PNGFileFilter());
		}else {
			if (this.activeapp!=null) {this.activeapp.keyPressed(e);}
		}
	}

}
