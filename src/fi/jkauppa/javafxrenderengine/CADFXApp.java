package fi.jkauppa.javafxrenderengine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.filechooser.FileFilter;

import fi.jkauppa.javafxrenderengine.JavaFXRenderEngine.AppFXHandler;
import fi.jkauppa.javarenderengine.MathLib;
import fi.jkauppa.javarenderengine.RenderLib;
import fi.jkauppa.javarenderengine.UtilLib;
import fi.jkauppa.javarenderengine.ModelLib.Coordinate;
import fi.jkauppa.javarenderengine.ModelLib.Direction;
import fi.jkauppa.javarenderengine.ModelLib.Entity;
import fi.jkauppa.javarenderengine.ModelLib.Line;
import fi.jkauppa.javarenderengine.ModelLib.Material;
import fi.jkauppa.javarenderengine.ModelLib.Matrix;
import fi.jkauppa.javarenderengine.ModelLib.Plane;
import fi.jkauppa.javarenderengine.ModelLib.Position;
import fi.jkauppa.javarenderengine.ModelLib.RenderView;
import fi.jkauppa.javarenderengine.ModelLib.Rotation;
import fi.jkauppa.javarenderengine.ModelLib.Scaling;
import fi.jkauppa.javarenderengine.ModelLib.Sphere;
import fi.jkauppa.javarenderengine.ModelLib.Triangle;
import fi.jkauppa.javarenderengine.UtilLib.ImageFileFilters.BMPFileFilter;
import fi.jkauppa.javarenderengine.UtilLib.ImageFileFilters.GIFFileFilter;
import fi.jkauppa.javarenderengine.UtilLib.ImageFileFilters.JPGFileFilter;
import fi.jkauppa.javarenderengine.UtilLib.ImageFileFilters.PNGFileFilter;
import fi.jkauppa.javarenderengine.UtilLib.ImageFileFilters.WBMPFileFilter;
import fi.jkauppa.javarenderengine.UtilLib.ModelFileFilters.OBJFileFilter;
import fi.jkauppa.javarenderengine.UtilLib.ModelFileFilters.STLFileFilter;
import javafx.event.Event;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Affine;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class CADFXApp extends AppFXHandler {
	private Group root = null;
	private Scene scene = null;
	private Group entities = new Group();
	private Group defaultscenelineroot = new Group();
	private Group defaultscenetriangleroot = new Group();
	private Group unlitscenetriangleroot = new Group();
	private GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	private GraphicsDevice gd = ge.getDefaultScreenDevice();
	private GraphicsConfiguration gc = gd.getDefaultConfiguration();
	private boolean draglinemode = false;
	private boolean snaplinemode = false;
	private boolean texturemode = false;
	private boolean erasemode = false;
	private Material drawmat = new Material(Color.getHSBColor(0.0f, 0.0f, 1.0f),1.0f,null);
	private int renderoutputwidth = 3840, renderoutputheight = 2160;
	private int rendercubemapoutputsize = 2048, rendercubemapoutputwidth = 3*rendercubemapoutputsize, rendercubemapoutputheight = 2*rendercubemapoutputsize;
	private int renderspheremapoutputwidth = 2*renderoutputwidth, renderspheremapoutputheight = renderoutputheight;
	private int renderbounces = 2;
	private Color renderbackgroundcolor = Color.BLACK;
	private int polygonfillmode = 1;
	private boolean unlitrender = false;
	private Entity entitybuffer = null;
	private Position[] selecteddragvertex = null;
	private Entity[] selecteddragentity = null;
	private Entity[] mouseoverentity = null;
	private Triangle[] mouseovertriangle = null;
	private Position[] mouseoververtex = null;
	private Line[] mouseoverline = null;
	private int mouselocationx = 0, mouselocationy = 0;
	private int mouselastlocationx = -1, mouselastlocationy = -1; 
	private double hfov = 70.0f;
	private double vfov = 43.0f;
	private int gridstep = 20;
	private double editplanedistance = (((double)this.renderwidth)/2.0f)/MathLib.tand(this.hfov/2.0f);
	private final Position[] defaultcampos = {new Position(0.0f,0.0f,editplanedistance)};
	private final Rotation defaultcamrot = new Rotation(0.0f, 0.0f, 0.0f);
	private Position drawstartpos = new Position(0,0,0);
	private Position editpos = new Position(0.0f,0.0f,0.0f);
	private Position mousepos = this.editpos;
	private Position[] campos = this.defaultcampos;
	private Rotation camrot = this.defaultcamrot;
	private Matrix cameramat = MathLib.rotationMatrix(0.0f, 0.0f, 0.0f);
	private final Direction[] lookdirs = MathLib.projectedCameraDirections(cameramat);
	private Direction[] camdirs = lookdirs;
	private Set<Line> linelisttree = new TreeSet<Line>();
	private Line[] linelist = null;
	private Entity[] entitylist = null;
	private boolean leftkeydown = false;
	private boolean rightkeydown = false;
	private boolean upwardkeydown = false;
	private boolean downwardkeydown = false;
	private boolean forwardkeydown = false;
	private boolean backwardkeydown = false;
	private boolean rollrightkeydown = false;
	private boolean rollleftkeydown = false;
	private boolean pitchupkeydown = false;
	private boolean pitchdownkeydown = false;
	private boolean yawleftkeydown = false;
	private boolean yawrightkeydown = false;
	private SnapshotParameters snap = new SnapshotParameters();
	private WritableImage bgimage = null;
	private ImagePattern bgpattern = null;
	private RenderView renderview = null;
	
	public CADFXApp(Group root) {
		this.root = root;
		this.scene = root.getScene();
		this.snap.setFill(javafx.scene.paint.Color.TRANSPARENT);
		Canvas bgpatternimage = new Canvas(this.gridstep, this.gridstep);
		GraphicsContext pgfx = bgpatternimage.getGraphicsContext2D();
		pgfx.setFill(javafx.scene.paint.Color.WHITE);
		pgfx.fillRect(0, 0, bgpatternimage.getWidth(), bgpatternimage.getHeight());
		pgfx.setFill(null);
		pgfx.setStroke(javafx.scene.paint.Color.BLACK);
		pgfx.strokeLine(0, 0, 0, gridstep-1);
		pgfx.strokeLine(0, 0, gridstep-1, 0);
		this.bgimage = bgpatternimage.snapshot(this.snap, null);
		this.bgpattern = new ImagePattern(this.bgimage, 0.0f, 0.0f, bgpatternimage.getWidth(), bgpatternimage.getHeight(), false);
	}
	
	@Override public void update() {
		this.renderwidth = (int)this.scene.getWidth();
		this.renderheight = (int)this.scene.getHeight();
		this.editplanedistance = (((double)this.renderwidth)/2.0f)/MathLib.tand(this.hfov/2.0f);
		PerspectiveCamera camera = new PerspectiveCamera(true);
		camera.setFarClip(1000000.0f);
		camera.setVerticalFieldOfView(false);
		camera.setFieldOfView(this.hfov);
		camera.getTransforms().clear();
		camera.setTranslateX(this.campos[0].x);
		camera.setTranslateY(this.campos[0].y);
		camera.setTranslateZ(this.campos[0].z);
		Affine transform = RenderFXLib.matrixAffine(this.cameramat);
		camera.getTransforms().add(transform);
		scene.setCamera(camera);
		this.scene.setCursor(Cursor.CROSSHAIR);
		if (this.polygonfillmode==1) {
			this.scene.setFill(this.bgpattern);
			this.entities = this.defaultscenelineroot;
		} else if (this.polygonfillmode==2) {
			this.scene.setFill(Paint.valueOf("BLACK"));
			if (this.unlitrender) {
				this.entities = this.unlitscenetriangleroot;
			} else {
				this.entities = this.defaultscenetriangleroot;
			}
		}
		root.getChildren().setAll(this.entities);
	}
	
	@Override public void tick() {
		updateCamera();
	}
	@Override public void pulse() {}

	private void updateCamera() {
		double movementstep = 1000.0f*this.diffticktimesec;
		if (this.snaplinemode) {
			movementstep *= this.gridstep;
		}
		if (this.leftkeydown) {
			this.campos = MathLib.translate(campos, this.camdirs[1], -movementstep);
			System.out.println("CADApp: keyPressed: key A: camera position to left="+this.campos[0].x+","+this.campos[0].y+","+this.campos[0].z);
		} else if (this.rightkeydown) {
			this.campos = MathLib.translate(campos, this.camdirs[1], movementstep);
			System.out.println("CADApp: keyPressed: key D: camera position to right="+this.campos[0].x+","+this.campos[0].y+","+this.campos[0].z);
		}
		if (this.forwardkeydown) {
			this.campos = MathLib.translate(campos, this.camdirs[0], movementstep);
			System.out.println("CADApp: keyPressed: key +/SPACE: camera position to forward="+this.campos[0].x+","+this.campos[0].y+","+this.campos[0].z);
		} else if (this.backwardkeydown) {
			this.campos = MathLib.translate(campos, this.camdirs[0], -movementstep);
			System.out.println("CADApp: keyPressed: key -/C: camera position to backward="+this.campos[0].x+","+this.campos[0].y+","+this.campos[0].z);
		}
		if (this.upwardkeydown) {
			this.campos = MathLib.translate(campos, this.camdirs[2], -movementstep);
			System.out.println("CADApp: keyPressed: key W: camera position to upward="+this.campos[0].x+","+this.campos[0].y+","+this.campos[0].z);
		} else if (this.downwardkeydown) {
			this.campos = MathLib.translate(campos, this.camdirs[2], movementstep);
			System.out.println("CADApp: keyPressed: key S: camera position to downward="+this.campos[0].x+","+this.campos[0].y+","+this.campos[0].z);
		}
		if (this.rollleftkeydown) {
			this.camrot = this.camrot.copy();
			this.camrot.y -= (movementstep/((double)this.gridstep));
			System.out.println("CADApp: keyPressed: key Q: camera roll rotation left="+this.camrot.x+","+this.camrot.y+","+this.camrot.z);
		} else if (this.rollrightkeydown) {
			this.camrot = this.camrot.copy();
			this.camrot.y += (movementstep/((double)this.gridstep));
			System.out.println("CADApp: keyPressed: key E: camera roll rotation right="+this.camrot.x+","+this.camrot.y+","+this.camrot.z);
		}
		if (this.yawleftkeydown) {
			this.camrot = this.camrot.copy();
        	this.camrot.z += (movementstep/((double)this.gridstep));
			System.out.println("CADApp: keyPressed: key ARROW-LEFT: camera yaw rotation left="+this.camrot.x+","+this.camrot.y+","+this.camrot.z);
		} else if (this.yawrightkeydown) {
			this.camrot = this.camrot.copy();
        	this.camrot.z -= (movementstep/((double)this.gridstep));
			System.out.println("CADApp: keyPressed: key ARROW-RIGHT: camera yaw rotation right="+this.camrot.x+","+this.camrot.y+","+this.camrot.z);
		}
		if (this.pitchupkeydown) {
			this.camrot = this.camrot.copy();
        	this.camrot.x += (movementstep/((double)this.gridstep));
			System.out.println("CADApp: keyPressed: key ARROW-DOWN: camera yaw rotation down="+this.camrot.x+","+this.camrot.y+","+this.camrot.z);
		} else if (this.pitchdownkeydown) {
			this.camrot = this.camrot.copy();
        	this.camrot.x -= (movementstep/((double)this.gridstep));
			System.out.println("CADApp: keyPressed: key ARROW-UP: camera yaw rotation up="+this.camrot.x+","+this.camrot.y+","+this.camrot.z);
		}
		Matrix camrotmat = MathLib.rotationMatrixLookHorizontalRoll(this.camrot);
		Direction[] camlookdirs = MathLib.projectedCameraDirections(camrotmat);
		Position[] editposa = MathLib.translate(this.campos, camlookdirs[0], this.editplanedistance);
		this.mousepos = MathLib.cameraPlanePosition(this.editpos, this.mouselocationx, this.mouselocationy, this.renderwidth, this.renderheight, this.snaplinemode, this.gridstep, this.cameramat);
		this.editpos = editposa[0];
		this.cameramat = camrotmat;
		this.camdirs = camlookdirs;
		(new RenderViewUpdater()).start();
	}
	
	@Override public void handle(Event event) {
		if (event.getEventType().equals(KeyEvent.KEY_RELEASED)) {
			KeyEvent keyevent = (KeyEvent)event;
			if (keyevent.getCode()==KeyCode.UP) {
				this.pitchupkeydown = false;
			} else if (keyevent.getCode()==KeyCode.DOWN) {
				this.pitchdownkeydown = false;
			} else if (keyevent.getCode()==KeyCode.LEFT) {
				this.yawleftkeydown = false;
			} else if (keyevent.getCode()==KeyCode.RIGHT) {
				this.yawrightkeydown = false;
			} else if (keyevent.getCode()==KeyCode.SHIFT) {
				this.snaplinemode = false;
			} else if (keyevent.getCode()==KeyCode.TAB) {
				this.texturemode = false;
			} else if (keyevent.getCode()==KeyCode.PERIOD) {
				this.erasemode = false;
			} else if (keyevent.getCode()==KeyCode.W) {
				this.upwardkeydown = false;
			} else if (keyevent.getCode()==KeyCode.S) {
				this.downwardkeydown = false;
			} else if (keyevent.getCode()==KeyCode.A) {
				this.leftkeydown = false;
			} else if (keyevent.getCode()==KeyCode.D) {
				this.rightkeydown = false;
			} else if (keyevent.getCode()==KeyCode.SPACE) {
				this.forwardkeydown = false;
			} else if (keyevent.getCode()==KeyCode.C) {
				this.backwardkeydown = false;
			} else if (keyevent.getCode()==KeyCode.Q) {
				this.rollleftkeydown = false;
			} else if (keyevent.getCode()==KeyCode.E) {
				this.rollrightkeydown = false;
			} else if (keyevent.getCode()==KeyCode.ADD) {
				this.forwardkeydown = false;
			} else if (keyevent.getCode()==KeyCode.SUBTRACT) {
				this.backwardkeydown = false;
			}
		} else if (event.getEventType().equals(KeyEvent.KEY_PRESSED)) {
			KeyEvent keyevent = (KeyEvent)event;
			if (keyevent.getCode()==KeyCode.BACK_SPACE) {
				if (keyevent.isControlDown()) {
					if (this.entitylist!=null) {
						for (int j=0;j<this.entitylist.length;j++) {
							if (this.entitylist[j].trianglelist!=null) {
								for (int i=0;i<this.entitylist[j].trianglelist.length;i++) {
									if (this.entitylist[j].trianglelist[i].mat!=null) {
										this.entitylist[j].trianglelist[i].mat = this.entitylist[j].trianglelist[i].mat.copy();
										this.entitylist[j].trianglelist[i].mat.ambientcolor = null;
										this.entitylist[j].trianglelist[i].mat.ambientfileimage = null;
									}
								}
							}
						}
					}
				} else {
					if (!keyevent.isShiftDown()) {
						this.linelisttree.clear();
						this.linelist = null;
						this.entitylist = null;
						this.entities = new Group();
						this.defaultscenelineroot = new Group();
						this.defaultscenetriangleroot = new Group();
						this.unlitscenetriangleroot = new Group();
					}
					this.campos = this.defaultcampos;
					this.camrot = this.defaultcamrot;
					System.out.println("CADApp: keyPressed: key BACKSPACE: reset camera position="+this.campos[0].x+","+this.campos[0].y+","+this.campos[0].z);
				}
			} else if (keyevent.getCode()==KeyCode.INSERT) {
				float[] drawcolorhsb = Color.RGBtoHSB(this.drawmat.facecolor.getRed(), this.drawmat.facecolor.getGreen(), this.drawmat.facecolor.getBlue(), new float[3]);
				drawcolorhsb[0] += 0.01f; if (drawcolorhsb[0]>1.0f) {drawcolorhsb[0] = 0.0f;}
				Color hsbcolor = Color.getHSBColor(drawcolorhsb[0], drawcolorhsb[1], drawcolorhsb[2]);
				float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
				Color newfacecolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.drawmat.transparency);
				this.drawmat = this.drawmat.copy();
				this.drawmat.facecolor = newfacecolor;
				this.drawmat.fileimage = null;
				this.entitybuffer = null;
				System.out.println("CADApp: keyPressed: key INSERT: draw material color hue positive: r="+colorvalues[0]+" g="+colorvalues[1]+" b="+colorvalues[2]+" tr="+this.drawmat.transparency);
			} else if (keyevent.getCode()==KeyCode.DELETE) {
				float[] drawcolorhsb = Color.RGBtoHSB(this.drawmat.facecolor.getRed(), this.drawmat.facecolor.getGreen(), this.drawmat.facecolor.getBlue(), new float[3]);
				drawcolorhsb[0] -= 0.01f; if (drawcolorhsb[0]<0.0f) {drawcolorhsb[0] = 1.0f;}
				Color hsbcolor = Color.getHSBColor(drawcolorhsb[0], drawcolorhsb[1], drawcolorhsb[2]);
				float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
				Color newfacecolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.drawmat.transparency);
				this.drawmat = this.drawmat.copy();
				this.drawmat.facecolor = newfacecolor;
				this.drawmat.fileimage = null;
				this.entitybuffer = null;
				System.out.println("CADApp: keyPressed: key DELETE: draw material color hue negative: r="+colorvalues[0]+" g="+colorvalues[1]+" b="+colorvalues[2]+" tr="+this.drawmat.transparency);
			} else if (keyevent.getCode()==KeyCode.HOME) {
				float[] drawcolorhsb = Color.RGBtoHSB(this.drawmat.facecolor.getRed(), this.drawmat.facecolor.getGreen(), this.drawmat.facecolor.getBlue(), new float[3]);
				drawcolorhsb[1] += 0.01f; if (drawcolorhsb[1]>1.0f) {drawcolorhsb[1] = 1.0f;}
				Color hsbcolor = Color.getHSBColor(drawcolorhsb[0], drawcolorhsb[1], drawcolorhsb[2]);
				float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
				Color newfacecolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.drawmat.transparency);
				this.drawmat = this.drawmat.copy();
				this.drawmat.facecolor = newfacecolor;
				this.drawmat.fileimage = null;
				this.entitybuffer = null;
				System.out.println("CADApp: keyPressed: key HOME: draw material color saturation positive: r="+colorvalues[0]+" g="+colorvalues[1]+" b="+colorvalues[2]+" tr="+this.drawmat.transparency);
			} else if (keyevent.getCode()==KeyCode.END) {
				float[] drawcolorhsb = Color.RGBtoHSB(this.drawmat.facecolor.getRed(), this.drawmat.facecolor.getGreen(), this.drawmat.facecolor.getBlue(), new float[3]);
				drawcolorhsb[1] -= 0.01f; if (drawcolorhsb[1]<0.0f) {drawcolorhsb[1] = 0.0f;}
				Color hsbcolor = Color.getHSBColor(drawcolorhsb[0], drawcolorhsb[1], drawcolorhsb[2]);
				float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
				Color newfacecolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.drawmat.transparency);
				this.drawmat = this.drawmat.copy();
				this.drawmat.facecolor = newfacecolor;
				this.drawmat.fileimage = null;
				this.entitybuffer = null;
				System.out.println("CADApp: keyPressed: key END: draw material color saturation negative: r="+colorvalues[0]+" g="+colorvalues[1]+" b="+colorvalues[2]+" tr="+this.drawmat.transparency);
			} else if (keyevent.getCode()==KeyCode.PAGE_UP) {
				float[] drawcolorhsb = Color.RGBtoHSB(this.drawmat.facecolor.getRed(), this.drawmat.facecolor.getGreen(), this.drawmat.facecolor.getBlue(), new float[3]);
				drawcolorhsb[2] += 0.01f; if (drawcolorhsb[2]>1.0f) {drawcolorhsb[2] = 1.0f;}
				Color hsbcolor = Color.getHSBColor(drawcolorhsb[0], drawcolorhsb[1], drawcolorhsb[2]);
				float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
				Color newfacecolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.drawmat.transparency);
				this.drawmat = this.drawmat.copy();
				this.drawmat.facecolor = newfacecolor;
				this.drawmat.fileimage = null;
				this.entitybuffer = null;
				System.out.println("CADApp: keyPressed: key PAGEUP: draw material color brightness positive: r="+colorvalues[0]+" g="+colorvalues[1]+" b="+colorvalues[2]+" tr="+this.drawmat.transparency);
			} else if (keyevent.getCode()==KeyCode.PAGE_DOWN) {
				float[] drawcolorhsb = Color.RGBtoHSB(this.drawmat.facecolor.getRed(), this.drawmat.facecolor.getGreen(), this.drawmat.facecolor.getBlue(), new float[3]);
				drawcolorhsb[2] -= 0.01f; if (drawcolorhsb[2]<0.0f) {drawcolorhsb[2] = 0.0f;}
				Color hsbcolor = Color.getHSBColor(drawcolorhsb[0], drawcolorhsb[1], drawcolorhsb[2]);
				float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
				Color newfacecolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.drawmat.transparency);
				this.drawmat = this.drawmat.copy();
				this.drawmat.facecolor = newfacecolor;
				this.drawmat.fileimage = null;
				this.entitybuffer = null;
				System.out.println("CADApp: keyPressed: key PAGEDOWN: draw material color brightness negative: r="+colorvalues[0]+" g="+colorvalues[1]+" b="+colorvalues[2]+" tr="+this.drawmat.transparency);
			} else if (keyevent.getCode()==KeyCode.MULTIPLY) {
				float newemissivity = this.drawmat.emissivity*1.1f; if (newemissivity<=0.0f) {newemissivity = 0.00001f;} if (newemissivity>1.0f) {newemissivity = 1.0f;}
				float[] drawcolorcomp = this.drawmat.facecolor.getRGBComponents(new float[4]);
				this.drawmat = this.drawmat.copy();
				this.drawmat.emissivity = newemissivity;
				float[] newemissivecolor = {drawcolorcomp[0]*newemissivity,drawcolorcomp[1]*newemissivity,drawcolorcomp[2]*newemissivity,1.0f};
				this.drawmat.emissivecolor = new Color(newemissivecolor[0],newemissivecolor[1],newemissivecolor[2],newemissivecolor[3]);
				if (this.drawmat.fileimage!=null) {
					this.drawmat.emissivefileimage = gc.createCompatibleImage(this.drawmat.fileimage.getWidth(), this.drawmat.fileimage.getHeight(), Transparency.TRANSLUCENT);
					Graphics2D emgfx = this.drawmat.emissivefileimage.createGraphics();
					emgfx.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, newemissivity));
					emgfx.drawImage(this.drawmat.fileimage, 0, 0, null);
					emgfx.dispose();
				}
				System.out.println("CADApp: keyPressed: key NUMPAD*: draw material emissivity positive: r="+drawcolorcomp[0]+" g="+drawcolorcomp[1]+" b="+drawcolorcomp[2]+" em="+this.drawmat.emissivity);
			} else if (keyevent.getCode()==KeyCode.DIVIDE) {
				float newemissivity = this.drawmat.emissivity/1.1f;
				float[] drawcolorcomp = this.drawmat.facecolor.getRGBComponents(new float[4]);
				this.drawmat = this.drawmat.copy();
				this.drawmat.emissivity = newemissivity;
				float[] newemissivecolor = {drawcolorcomp[0]*newemissivity,drawcolorcomp[1]*newemissivity,drawcolorcomp[2]*newemissivity,1.0f};
				this.drawmat.emissivecolor = new Color(newemissivecolor[0],newemissivecolor[1],newemissivecolor[2],newemissivecolor[3]);
				if (this.drawmat.fileimage!=null) {
					this.drawmat.emissivefileimage = gc.createCompatibleImage(this.drawmat.fileimage.getWidth(), this.drawmat.fileimage.getHeight(), Transparency.TRANSLUCENT);
					Graphics2D emgfx = this.drawmat.emissivefileimage.createGraphics();
					emgfx.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, newemissivity));
					emgfx.drawImage(this.drawmat.fileimage, 0, 0, null);
					emgfx.dispose();
				}
				System.out.println("CADApp: keyPressed: key NUMPAD/: draw material emissivity negative: r="+drawcolorcomp[0]+" g="+drawcolorcomp[1]+" b="+drawcolorcomp[2]+" em="+this.drawmat.emissivity);
			} else if (keyevent.getCode()==KeyCode.NUMPAD9) {
				float newtransparency = this.drawmat.transparency+0.01f; if (newtransparency>1.0f) {newtransparency = 1.0f;}
				float[] colorvalues = this.drawmat.facecolor.getRGBColorComponents(new float[3]);
				Color newfacecolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],newtransparency);
				this.drawmat = this.drawmat.copy();
				this.drawmat.facecolor = newfacecolor;
				this.drawmat.transparency = newtransparency;
				System.out.println("CADApp: keyPressed: key NUMPAD9: draw material transparency positive: r="+colorvalues[0]+" g="+colorvalues[1]+" b="+colorvalues[2]+" tr="+this.drawmat.transparency);
			} else if (keyevent.getCode()==KeyCode.NUMPAD8) {
				float newtransparency = this.drawmat.transparency-0.01f; if (newtransparency<0.0f) {newtransparency = 0.0f;}
				float[] colorvalues = this.drawmat.facecolor.getRGBColorComponents(new float[3]);
				Color newfacecolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],newtransparency);
				this.drawmat = this.drawmat.copy();
				this.drawmat.facecolor = newfacecolor;
				this.drawmat.transparency = newtransparency;
				System.out.println("CADApp: keyPressed: key NUMPAD8: draw material transparency negative: r="+colorvalues[0]+" g="+colorvalues[1]+" b="+colorvalues[2]+" tr="+this.drawmat.transparency);
			} else if (keyevent.getCode()==KeyCode.NUMPAD7) {
				if ((this.mouseovertriangle!=null)&&(this.mouseovertriangle.length>0)) {
					Triangle[] stri = {this.mouseovertriangle[this.mouseovertriangle.length-1]};
					if ((stri[0].norm==null)||(stri[0].norm.isZero())) {
						Plane[] striplane = MathLib.trianglePlane(stri);
						Direction[] strinorm = MathLib.planeNormal(striplane);
						stri[0].norm = strinorm[0];
					} else {
						stri[0].norm = stri[0].norm.invert();
					}
					System.out.println("CADApp: keyPressed: key NUMPAD7: invert triangle normal="+stri[0].norm.dx+","+stri[0].norm.dy+" "+stri[0].norm.dz);
				}
			} else if (keyevent.getCode()==KeyCode.NUMPAD6) {
				float newroughness = this.drawmat.roughness+0.01f; if (newroughness>1.0f) {newroughness = 1.0f;}
				float[] drawcolorcomp = this.drawmat.facecolor.getRGBComponents(new float[4]);
				this.drawmat = this.drawmat.copy();
				this.drawmat.roughness = newroughness;
				System.out.println("CADApp: keyPressed: key NUMPAD6: draw material roughness positive: r="+drawcolorcomp[0]+" g="+drawcolorcomp[1]+" b="+drawcolorcomp[2]+" rg="+this.drawmat.roughness);
			} else if (keyevent.getCode()==KeyCode.NUMPAD5) {
				float newroughness = this.drawmat.roughness-0.01f; if (newroughness<0.0f) {newroughness = 0.0f;}
				float[] drawcolorcomp = this.drawmat.facecolor.getRGBComponents(new float[4]);
				this.drawmat = this.drawmat.copy();
				this.drawmat.roughness = newroughness;
				System.out.println("CADApp: keyPressed: key NUMPAD5: draw material roughness negative: r="+drawcolorcomp[0]+" g="+drawcolorcomp[1]+" b="+drawcolorcomp[2]+" rg="+this.drawmat.roughness);
			} else if (keyevent.getCode()==KeyCode.NUMPAD4) {
				if ((this.mouseovertriangle!=null)&&(this.mouseovertriangle.length>0)) {
					Triangle[] stri = {this.mouseovertriangle[this.mouseovertriangle.length-1]};
					stri[0].norm = new Direction(0.0f,0.0f,0.0f);
					System.out.println("CADApp: keyPressed: key NUMPAD4: reset triangle normal="+stri[0].norm.dx+","+stri[0].norm.dy+" "+stri[0].norm.dz);
				}
			} else if (keyevent.getCode()==KeyCode.NUMPAD3) {
				float newmetallic = this.drawmat.metallic+0.01f; if (newmetallic>1.0f) {newmetallic = 1.0f;}
				float[] drawcolorcomp = this.drawmat.facecolor.getRGBComponents(new float[4]);
				this.drawmat = this.drawmat.copy();
				this.drawmat.metallic = newmetallic;
				System.out.println("CADApp: keyPressed: key NUMPAD3: draw material metallic positive: r="+drawcolorcomp[0]+" g="+drawcolorcomp[1]+" b="+drawcolorcomp[2]+" mt="+this.drawmat.metallic);
			} else if (keyevent.getCode()==KeyCode.NUMPAD2) {
				float newmetallic = this.drawmat.metallic-0.01f; if (newmetallic<0.0f) {newmetallic = 0.0f;}
				float[] drawcolorcomp = this.drawmat.facecolor.getRGBComponents(new float[4]);
				this.drawmat = this.drawmat.copy();
				this.drawmat.metallic = newmetallic;
				System.out.println("CADApp: keyPressed: key NUMPAD2: draw material metallic negative: r="+drawcolorcomp[0]+" g="+drawcolorcomp[1]+" b="+drawcolorcomp[2]+" mt="+this.drawmat.metallic);
			} else if (keyevent.getCode()==KeyCode.NUMPAD1) {
		    	Triangle mousetriangle = null;
	    		if ((this.renderview!=null)&&(this.renderview.tbuffer!=null)) {
	    			if ((this.mouselocationx>=0)&&(this.mouselocationx<this.renderwidth)&&(this.mouselocationy>=0)&&(this.mouselocationy<this.renderheight)) {
		    			mousetriangle = this.renderview.tbuffer[this.mouselocationy][this.mouselocationx];
	    			}
	    		} else if ((this.mouseovertriangle!=null)&&(this.mouseovertriangle.length>0)) {
	    			mousetriangle = this.mouseovertriangle[this.mouseovertriangle.length-1];
	    		}
				if (mousetriangle!=null) {
		    		Coordinate defaultpos1tex = new Coordinate(0.0f,0.0f);
		    		Coordinate defaultpos2tex = new Coordinate(1.0f,0.0f);
		    		Coordinate defaultpos3tex = new Coordinate(1.0f,1.0f);
		    		Coordinate newpos1tex = defaultpos1tex;
		    		Coordinate newpos2tex = defaultpos2tex;
		    		Coordinate newpos3tex = defaultpos3tex;
					if ((mousetriangle.pos1.tex!=null)&&(mousetriangle.pos2.tex!=null)&&(mousetriangle.pos3.tex!=null)) {
						if ((mousetriangle.pos1.tex.equals(newpos1tex))&&(mousetriangle.pos2.tex.equals(newpos2tex))&&(mousetriangle.pos3.tex.equals(newpos3tex))) {
				    		newpos1tex = defaultpos2tex;
				    		newpos2tex = defaultpos3tex;
				    		newpos3tex = defaultpos1tex;
						} else if ((mousetriangle.pos1.tex.equals(newpos2tex))&&(mousetriangle.pos2.tex.equals(newpos3tex))&&(mousetriangle.pos3.tex.equals(newpos1tex))) {
				    		newpos1tex = defaultpos3tex;
				    		newpos2tex = defaultpos1tex;
				    		newpos3tex = defaultpos2tex;
						} else if ((mousetriangle.pos1.tex.equals(newpos3tex))&&(mousetriangle.pos2.tex.equals(newpos1tex))&&(mousetriangle.pos3.tex.equals(newpos2tex))) {
				    		newpos1tex = defaultpos1tex;
				    		newpos2tex = defaultpos3tex;
				    		newpos3tex = defaultpos2tex;
						} else if ((mousetriangle.pos1.tex.equals(newpos1tex))&&(mousetriangle.pos2.tex.equals(newpos3tex))&&(mousetriangle.pos3.tex.equals(newpos2tex))) {
				    		newpos1tex = defaultpos3tex;
				    		newpos2tex = defaultpos2tex;
				    		newpos3tex = defaultpos1tex;
						} else if ((mousetriangle.pos1.tex.equals(newpos3tex))&&(mousetriangle.pos2.tex.equals(newpos2tex))&&(mousetriangle.pos3.tex.equals(newpos1tex))) {
				    		newpos1tex = defaultpos2tex;
				    		newpos2tex = defaultpos1tex;
				    		newpos3tex = defaultpos3tex;
						}
					}
		    		mousetriangle.pos1.tex = newpos1tex;
		    		mousetriangle.pos2.tex = newpos2tex;
		    		mousetriangle.pos3.tex = newpos3tex;
					System.out.println("CADApp: keyPressed: key NUMPAD1: reset/rotate/mirror texture coordinate="+mousetriangle.pos1.tex.u+","+mousetriangle.pos1.tex.v+" "+mousetriangle.pos2.tex.u+","+mousetriangle.pos2.tex.v+" "+mousetriangle.pos3.tex.u+","+mousetriangle.pos3.tex.v);
				}
			} else if (keyevent.getCode()==KeyCode.NUMPAD0) {
				(new EntityListUpdater()).start();
			} else if (keyevent.getCode()==KeyCode.ENTER) {
				if (keyevent.isShiftDown()) {
					this.unlitrender = !this.unlitrender;
					System.out.println("CADApp: keyPressed: key SHIFT-ENTER: unlitrender="+this.unlitrender);
				} else if (keyevent.isControlDown()) {
					int bounces = 2;
					this.entities.getChildren().clear();
					RenderFXLib.renderSurfaceFaceLightmapCubemapView(this.entitylist, this.entities, 32, bounces);
				} else {
					this.polygonfillmode += 1;
					if (this.polygonfillmode>2) {
						this.polygonfillmode = 1;
					}
					System.out.println("CADApp: keyPressed: key ENTER: polygonfillmode="+this.polygonfillmode);
				}
			} else if (keyevent.getCode()==KeyCode.UP) {
				this.pitchupkeydown = true;
			} else if (keyevent.getCode()==KeyCode.DOWN) {
				this.pitchdownkeydown = true;
			} else if (keyevent.getCode()==KeyCode.LEFT) {
				this.yawleftkeydown = true;
			} else if (keyevent.getCode()==KeyCode.RIGHT) {
				this.yawrightkeydown = true;
			} else if (keyevent.getCode()==KeyCode.TAB) {
				this.texturemode = true;
			} else if (keyevent.getCode()==KeyCode.SHIFT) {
				this.snaplinemode = true;
			} else if (keyevent.getCode()==KeyCode.PERIOD) {
				this.erasemode = true;
			} else if (keyevent.getCode()==KeyCode.W) {
				this.upwardkeydown = true;
			} else if (keyevent.getCode()==KeyCode.S) {
				this.downwardkeydown = true;
			} else if (keyevent.getCode()==KeyCode.A) {
				this.leftkeydown = true;
			} else if (keyevent.getCode()==KeyCode.D) {
				this.rightkeydown = true;
			} else if (keyevent.getCode()==KeyCode.SPACE) {
				this.forwardkeydown = true;
			} else if (keyevent.getCode()==KeyCode.C) {
				this.backwardkeydown = true;
			} else if (keyevent.getCode()==KeyCode.Q) {
				this.rollleftkeydown = true;
			} else if (keyevent.getCode()==KeyCode.E) {
				this.rollrightkeydown = true;
			} else if (keyevent.getCode()==KeyCode.ADD) {
				this.forwardkeydown = true;
			} else if (keyevent.getCode()==KeyCode.SUBTRACT) {
				this.backwardkeydown = true;
			} else if (keyevent.getCode()==KeyCode.F2) {
			    boolean f2shiftdown = ((!keyevent.isControlDown())&&(!keyevent.isAltDown())&&(keyevent.isShiftDown())&&(!keyevent.isMetaDown()));
	        	FileChooser filechooser = new FileChooser();
	        	filechooser.setInitialDirectory(new File(this.userdir));
		    	filechooser.setTitle("Save File");
		    	ExtensionFilter objextensionfilter = new ExtensionFilter("OBJ Model file", "*.obj");
		    	ExtensionFilter stlextensionfilter = new ExtensionFilter("STL Model file", "*.stl");
	        	filechooser.getExtensionFilters().add(objextensionfilter);
	        	filechooser.getExtensionFilters().add(stlextensionfilter);
	        	filechooser.setSelectedExtensionFilter(objextensionfilter);
		    	File savefile = filechooser.showSaveDialog(this.scene.getWindow());
		    	if (savefile!=null) {
					if (savefile.getParent()!=null) {this.userdir = savefile.getParent();}
		    		ExtensionFilter loadfileextension = filechooser.getSelectedExtensionFilter();
		    		if (loadfileextension.equals(objextensionfilter)) {
		    			UtilLib.saveModelFormat(savefile.getPath(), this.entitylist, new OBJFileFilter(), f2shiftdown);
		    		} else if (loadfileextension.equals(objextensionfilter)) {
			    		UtilLib.saveModelFormat(savefile.getPath(), this.entitylist, new STLFileFilter(), f2shiftdown);
		    		}
				}
			} else if (keyevent.getCode()==KeyCode.F3) {
			    boolean f3shiftdown = ((!keyevent.isControlDown())&&(!keyevent.isAltDown())&&(keyevent.isShiftDown())&&(!keyevent.isMetaDown()));
			    boolean f3ctrldown = ((keyevent.isControlDown())&&(!keyevent.isAltDown())&&(!keyevent.isShiftDown())&&(!keyevent.isMetaDown()));
			    if (f3shiftdown) {
			    	this.snaplinemode = false;
		        	FileChooser filechooser = new FileChooser();
		        	filechooser.setInitialDirectory(new File(this.userdir));
			    	filechooser.setTitle("Load Texture");
			    	ExtensionFilter imageextensionfilter = new ExtensionFilter("Image files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp", "*.wbmp");
		        	filechooser.getExtensionFilters().add(imageextensionfilter);
		        	filechooser.setSelectedExtensionFilter(imageextensionfilter);
			    	File loadfile = filechooser.showOpenDialog(this.scene.getWindow());
			    	if (loadfile!=null) {
						if (loadfile.getParent()!=null) {this.userdir = loadfile.getParent();}
						BufferedImage fileimage = UtilLib.loadImage(loadfile.getPath(), false);
						this.drawmat = this.drawmat.copy();
						this.drawmat.fileimage = fileimage;
					}
			    } else if (f3ctrldown) {
		        	FileChooser filechooser = new FileChooser();
		        	filechooser.setInitialDirectory(new File(this.userdir));
			    	filechooser.setTitle("Insert Model");
			    	ExtensionFilter objextensionfilter = new ExtensionFilter("OBJ Model file", "*.obj");
			    	ExtensionFilter stlextensionfilter = new ExtensionFilter("STL Model file", "*.stl");
		        	filechooser.getExtensionFilters().add(objextensionfilter);
		        	filechooser.getExtensionFilters().add(stlextensionfilter);
		        	filechooser.setSelectedExtensionFilter(objextensionfilter);
			    	File loadfile = filechooser.showOpenDialog(this.scene.getWindow());
			    	if (loadfile!=null) {
						if (loadfile.getParent()!=null) {this.userdir = loadfile.getParent();}
			    		ExtensionFilter loadfileextension = filechooser.getSelectedExtensionFilter();
			    		if (loadfileextension.equals(objextensionfilter)) {
			    			this.entitybuffer = UtilLib.loadModelFormat(loadfile.getPath(), new OBJFileFilter(), false);
							RenderFXLib.constructLineFXScene(this.defaultscenelineroot, this.entitybuffer.linelist);
							RenderFXLib.constructTriangleFXScene(this.defaultscenetriangleroot, this.entitybuffer.childlist, false);
							RenderFXLib.constructTriangleFXScene(this.unlitscenetriangleroot, this.entitybuffer.childlist, true);
			    		} else if (loadfileextension.equals(stlextensionfilter)) {
			    			this.entitybuffer = UtilLib.loadModelFormat(loadfile.getPath(), new STLFileFilter(), false);
							RenderFXLib.constructLineFXScene(this.defaultscenelineroot, this.entitybuffer.linelist);
							RenderFXLib.constructTriangleFXScene(this.defaultscenetriangleroot, this.entitybuffer.childlist, false);
							RenderFXLib.constructTriangleFXScene(this.unlitscenetriangleroot, this.entitybuffer.childlist, true);
			    		}
					}
			    } else {
		        	FileChooser filechooser = new FileChooser();
		        	filechooser.setInitialDirectory(new File(this.userdir));
			    	filechooser.setTitle("Load Model");
			    	ExtensionFilter objextensionfilter = new ExtensionFilter("OBJ Model file", "*.obj");
			    	ExtensionFilter stlextensionfilter = new ExtensionFilter("STL Model file", "*.stl");
		        	filechooser.getExtensionFilters().add(objextensionfilter);
		        	filechooser.getExtensionFilters().add(stlextensionfilter);
		        	filechooser.setSelectedExtensionFilter(objextensionfilter);
			    	File loadfile = filechooser.showOpenDialog(this.scene.getWindow());
			    	if (loadfile!=null) {
						if (loadfile.getParent()!=null) {this.userdir = loadfile.getParent();}
			    		ExtensionFilter loadfileextension = filechooser.getSelectedExtensionFilter();
			    		if (loadfileextension.equals(objextensionfilter)) {
			    			Entity loadentity = UtilLib.loadModelFormat(loadfile.getPath(), new OBJFileFilter(), false);
							this.entitylist = loadentity.childlist;
							this.linelisttree.addAll(Arrays.asList(loadentity.linelist));
							this.linelist = linelisttree.toArray(new Line[linelisttree.size()]);
							this.defaultscenelineroot = RenderFXLib.constructLineFXScene(new Group(), this.linelist);
							this.defaultscenetriangleroot = RenderFXLib.constructTriangleFXScene(new Group(), this.entitylist, false);
							this.unlitscenetriangleroot = RenderFXLib.constructTriangleFXScene(new Group(), this.entitylist, true);
			    		} else if (loadfileextension.equals(stlextensionfilter)) {
				    		Entity loadentity = UtilLib.loadModelFormat(loadfile.getPath(), new STLFileFilter(), false);
							this.entitylist = loadentity.childlist;
							this.linelisttree.addAll(Arrays.asList(loadentity.linelist));
							this.linelist = linelisttree.toArray(new Line[linelisttree.size()]);
							this.defaultscenelineroot = RenderFXLib.constructLineFXScene(new Group(), this.linelist);
							this.defaultscenetriangleroot = RenderFXLib.constructTriangleFXScene(new Group(), this.entitylist, false);
							this.unlitscenetriangleroot = RenderFXLib.constructTriangleFXScene(new Group(), this.entitylist, true);
			    		}
					}
			    }
			} else if (keyevent.getCode()==KeyCode.F4) {
			    boolean f4down = ((!keyevent.isControlDown())&&(!keyevent.isAltDown())&&(!keyevent.isShiftDown())&&(!keyevent.isMetaDown()));
			    boolean f4shiftdown = ((!keyevent.isControlDown())&&(!keyevent.isAltDown())&&(keyevent.isShiftDown())&&(!keyevent.isMetaDown()));
			    boolean f4ctrldown = ((keyevent.isControlDown())&&(!keyevent.isAltDown())&&(!keyevent.isShiftDown())&&(!keyevent.isMetaDown()));
	        	FileChooser filechooser = new FileChooser();
	        	filechooser.setInitialDirectory(new File(this.userdir));
		    	filechooser.setTitle("Render Image");
		    	ExtensionFilter pngextensionfilter = new ExtensionFilter("PNG Image file", "*.png");
		    	ExtensionFilter jpgextensionfilter = new ExtensionFilter("JPG Image file", "*.jpg", "*.jpeg");
		    	ExtensionFilter gifextensionfilter = new ExtensionFilter("GIF Image file", "*.gif");
		    	ExtensionFilter bmpextensionfilter = new ExtensionFilter("BMP Image file", "*.bmp");
		    	ExtensionFilter wbmpextensionfilter = new ExtensionFilter("WBMP Image file", "*.wbmp");
	        	filechooser.getExtensionFilters().add(pngextensionfilter);
	        	filechooser.getExtensionFilters().add(jpgextensionfilter);
	        	filechooser.getExtensionFilters().add(gifextensionfilter);
	        	filechooser.getExtensionFilters().add(bmpextensionfilter);
	        	filechooser.getExtensionFilters().add(wbmpextensionfilter);
	        	filechooser.setSelectedExtensionFilter(pngextensionfilter);
		    	File savefile = filechooser.showSaveDialog(this.scene.getWindow());
		    	if (savefile!=null) {
					if (savefile.getParent()!=null) {this.userdir = savefile.getParent();}
		    		ExtensionFilter savefileextension = filechooser.getSelectedExtensionFilter();
		    		FileFilter saveformat = new PNGFileFilter();
		    		if (savefileextension.equals(jpgextensionfilter)) {
		    			saveformat = new JPGFileFilter();
		    		} else if (savefileextension.equals(gifextensionfilter)) {
		    			saveformat = new GIFFileFilter();
		    		} else if (savefileextension.equals(bmpextensionfilter)) {
		    			saveformat = new BMPFileFilter();
		    		} else if (savefileextension.equals(wbmpextensionfilter)) {
		    			saveformat = new WBMPFileFilter();
			    	}
		    		boolean rendercubemap = f4ctrldown;
		    		boolean renderspheremap = f4shiftdown;
		    		boolean renderbackground = f4down;
		    		(new ImageRenderer(savefile.getPath(), saveformat, rendercubemap, renderspheremap, renderbackground)).start();
				}
			}
		} else if (event.getEventType().equals(MouseEvent.MOUSE_MOVED)) {
			MouseEvent mouseevent = (MouseEvent)event;
			this.mouselocationx=(int)mouseevent.getSceneX();
			this.mouselocationy=(int)mouseevent.getSceneY();
		} else if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
			MouseEvent mouseevent = (MouseEvent)event;
			this.mouselocationx=(int)mouseevent.getSceneX();
			this.mouselocationy=(int)mouseevent.getSceneY();
		    boolean mouse1down = ((mouseevent.getButton().equals(MouseButton.PRIMARY))&&(!mouseevent.isControlDown())&&(!mouseevent.isAltDown())&&(!mouseevent.isMetaDown()));
			if (mouse1down) {
    			if (this.entitybuffer!=null) {
					Entity[] copyentitybuffer = new Entity[this.entitybuffer.childlist.length];
					for (int i=0;i<this.entitybuffer.childlist.length;i++) {
	        			copyentitybuffer[i] = this.entitybuffer.childlist[i].translate(this.mousepos);
	        		}
					Line[] copyentitybufferlinelist = new Line[this.entitybuffer.linelist.length];
					for (int i=0;i<this.entitybuffer.linelist.length;i++) {
						copyentitybufferlinelist[i] = this.entitybuffer.linelist[i].translate(this.mousepos);
					}
					if (this.entitylist!=null) {
						Entity[] copyentitylist = Arrays.copyOf(this.entitylist, this.entitylist.length+copyentitybuffer.length);
						for (int i=0;i<copyentitybuffer.length;i++) {
							copyentitylist[this.entitylist.length+i] = copyentitybuffer[i];
						}
						this.entitylist = copyentitylist;
					} else {
						this.entitylist = copyentitybuffer;
					}
					this.linelisttree.addAll(Arrays.asList(copyentitybufferlinelist));
					this.linelist = linelisttree.toArray(new Line[linelisttree.size()]);
    			}
			}
		    boolean mouse1ctrldown = ((mouseevent.getButton().equals(MouseButton.PRIMARY))&&(mouseevent.isControlDown())&&(!mouseevent.isAltDown())&&(!mouseevent.isMetaDown()));
	    	if (mouse1ctrldown) {
				if ((this.mouseoververtex!=null)&&(this.mouseoververtex.length>0)) {
		    		if (this.snaplinemode) {
		    			this.draglinemode = true;
		    			this.selecteddragvertex = this.mouseoververtex;
		    		} else {
		    			this.draglinemode = true;
		    			Position[] selectedvertex = {this.mouseoververtex[this.mouseoververtex.length-1]};
		    			this.selecteddragvertex = selectedvertex; 
		    		}
		    	}
	    	}
		    boolean mouse1altdown = ((mouseevent.getButton().equals(MouseButton.PRIMARY))&&(mouseevent.isAltDown()));
	    	if ((mouse1altdown)&&(this.polygonfillmode==1)) {
	    		this.drawstartpos = null;
				if (this.snaplinemode) {
					if ((this.mouseoververtex!=null)&&(this.mouseoververtex.length>0)) {
						this.drawstartpos = this.mouseoververtex[this.mouseoververtex.length-1].copy(); 
					}
				}
	    		Position[] drawposarray = {this.mousepos};
				if (this.drawstartpos==null) {
					this.drawstartpos = drawposarray[0].copy();
				}
				Line addline = new Line(this.drawstartpos, drawposarray[0]);
				this.linelisttree.add(addline);
				this.draglinemode = true;
				this.selecteddragvertex = drawposarray;
				(new EntityListUpdater()).start();
				System.out.println("CADApp: mousePressed: key ALT-LMB: adding line="+addline.pos1.x+","+addline.pos1.y+","+addline.pos1.z+" "+addline.pos2.x+","+addline.pos2.y+","+addline.pos2.z);
	    	}
		    boolean mouse3down = mouseevent.getButton().equals(MouseButton.SECONDARY);
	    	if (mouse3down) {
	    		if (!this.erasemode) {
			    	if ((this.mouseoverentity!=null)&&(mouseoverentity.length>0)) {
		    			Entity[] mouseentity = {this.mouseoverentity[this.mouseoverentity.length-1]};
		    			this.drawstartpos = this.mousepos;
		    			this.selecteddragentity = mouseentity;
		    		}
	    		}
	    	}
			this.handle(mouseevent.copyFor(mouseevent.getSource(), this.scene.getWindow(), MouseEvent.MOUSE_DRAGGED));
		} else if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
			MouseEvent mouseevent = (MouseEvent)event;
		    boolean mouse1up = mouseevent.getButton().equals(MouseButton.PRIMARY);
		    boolean mouse3up = mouseevent.getButton().equals(MouseButton.SECONDARY);
			if (mouse1up) {
				this.draglinemode = false;
			}
			if (mouse3up) {
				this.selecteddragentity = null;
			}
		} else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
			MouseEvent mouseevent = (MouseEvent)event;
			this.mouselastlocationx=this.mouselocationx;
			this.mouselastlocationy=this.mouselocationy;
			this.mouselocationx=(int)mouseevent.getSceneX();
			this.mouselocationy=(int)mouseevent.getSceneY();
		    boolean mouse1down = ((mouseevent.getButton().equals(MouseButton.PRIMARY))&&(!mouseevent.isControlDown())&&(!mouseevent.isAltDown())&&(!mouseevent.isShiftDown())&&(!mouseevent.isMetaDown()));
	    	if (mouse1down) {
	    		if (this.erasemode) {
	        		if ((this.polygonfillmode==1)&&(this.mouseoverline!=null)&&(this.mouseoverline.length>0)) {
	        			for (int i=0;i<this.mouseoverline.length;i++) {
	        				System.out.println("CADApp: mouseDragged: key PERIOD-LMB: erase line="+this.mouseoverline[i].pos1.x+" "+this.mouseoverline[i].pos1.y+" "+this.mouseoverline[i].pos1.z+" "+this.mouseoverline[i].pos2.x+" "+this.mouseoverline[i].pos2.y+" "+this.mouseoverline[i].pos2.z);
	        				this.linelisttree.remove(this.mouseoverline[i]);
	        			}
	    				(new EntityListUpdater()).start();
	    			}
	        		if ((this.polygonfillmode!=1)&&(this.mouseoverentity!=null)&&(mouseoverentity.length>0)&&(this.entitylist!=null)) {
		    			Entity[] mouseentity = {this.mouseoverentity[this.mouseoverentity.length-1]};
				    	Triangle mousetriangle = null;
			    		if ((this.renderview!=null)&&(this.renderview.tbuffer!=null)) {
			    			if ((this.mouselocationx>=0)&&(this.mouselocationx<this.renderwidth)&&(this.mouselocationy>=0)&&(this.mouselocationy<this.renderheight)) {
				    			mousetriangle = this.renderview.tbuffer[this.mouselocationy][this.mouselocationx];
			    			}
			    		} else if ((this.mouseovertriangle!=null)&&(this.mouseovertriangle.length>0)) {
			    			mousetriangle = this.mouseovertriangle[this.mouseovertriangle.length-1];
			    		}
						if (mousetriangle!=null) {
			    			Line[] mouseentitylinelist = MathLib.generateLineList(mouseentity);
			    			ArrayList<Line> mouseentitylinelistarray = new ArrayList<Line>(Arrays.asList(mouseentitylinelist));
							ArrayList<Triangle> trianglelistarray = new ArrayList<Triangle>(Arrays.asList(mouseentity[0].trianglelist));
							trianglelistarray.remove(mousetriangle);
							mouseentity[0].trianglelist = trianglelistarray.toArray(new Triangle[trianglelistarray.size()]);
							Line[] mouseentitytrianglelinelist = MathLib.generateLineList(mouseentity[0].trianglelist);
							mouseentitylinelistarray.removeAll(Arrays.asList(mouseentitytrianglelinelist));
			    			this.linelisttree.removeAll(mouseentitylinelistarray);
			    			this.linelist = linelisttree.toArray(new Line[linelisttree.size()]);
						}
		    		}
	    		} else {
			    	Triangle mousetriangle = null;
		    		if ((this.renderview!=null)&&(this.renderview.tbuffer!=null)) {
		    			if ((this.mouselocationx>=0)&&(this.mouselocationx<this.renderwidth)&&(this.mouselocationy>=0)&&(this.mouselocationy<this.renderheight)) {
			    			mousetriangle = this.renderview.tbuffer[this.mouselocationy][this.mouselocationx];
		    			}
		    		} else if ((this.mouseovertriangle!=null)&&(this.mouseovertriangle.length>0)) {
		    			mousetriangle = this.mouseovertriangle[this.mouseovertriangle.length-1];
		    		}
					if (mousetriangle!=null) {
						mousetriangle.mat = this.drawmat;
						float[] facecolorcomp = mousetriangle.mat.facecolor.getRGBComponents(new float[4]);
						System.out.println("CADApp: mouseDragged: key DRAG-LMB: painted material color: r="+facecolorcomp[0]+" g="+facecolorcomp[1]+" b="+facecolorcomp[2]+" tr="+facecolorcomp[3]);
					}
	    		}
	    	}
		    boolean mouse1shiftdown = ((mouseevent.getButton().equals(MouseButton.PRIMARY))&&(!mouseevent.isControlDown())&&(!mouseevent.isAltDown())&&(mouseevent.isShiftDown())&&(!mouseevent.isMetaDown()));
		    if (mouse1shiftdown) {
		    	Triangle mousetriangle = null;
	    		if ((this.renderview!=null)&&(this.renderview.tbuffer!=null)) {
	    			if ((this.mouselocationx>=0)&&(this.mouselocationx<this.renderwidth)&&(this.mouselocationy>=0)&&(this.mouselocationy<this.renderheight)) {
		    			mousetriangle = this.renderview.tbuffer[this.mouselocationy][this.mouselocationx];
	    			}
	    		} else if ((this.mouseovertriangle!=null)&&(this.mouseovertriangle.length>0)) {
	    			mousetriangle = this.mouseovertriangle[this.mouseovertriangle.length-1];
	    		}
				if (mousetriangle!=null) {
					this.drawmat = mousetriangle.mat;
					float[] facecolorcomp = this.drawmat.facecolor.getRGBComponents(new float[4]);
					System.out.println("CADApp: mouseDragged: key SHIFT-DRAG-LMB: selected material color: r="+facecolorcomp[0]+" g="+facecolorcomp[1]+" b="+facecolorcomp[2]+" tr="+facecolorcomp[3]);
				}
		    }
		    boolean mouse1ctrldown = ((mouseevent.getButton().equals(MouseButton.PRIMARY))&&(mouseevent.isControlDown()));
		    boolean mouse1altdown = ((mouseevent.getButton().equals(MouseButton.PRIMARY))&&(mouseevent.isAltDown()));
	    	if (mouse1ctrldown||mouse1altdown) {
	    		if (this.draglinemode) {
	    			Position drawlocation = null;
					if (this.snaplinemode) {
						if ((this.mouseoververtex!=null)&&(this.mouseoververtex.length>0)) {
							drawlocation = this.mouseoververtex[this.mouseoververtex.length-1].copy(); 
						}
					}
					if (drawlocation==null) {
		        		Position[] drawposarray = {this.mousepos};
		    			drawlocation = drawposarray[0];
					}
					for (int i=0;i<this.selecteddragvertex.length;i++) {
						this.selecteddragvertex[i].x = drawlocation.x;
						this.selecteddragvertex[i].y = drawlocation.y;
						this.selecteddragvertex[i].z = drawlocation.z;
					}
					(new EntityListUpdater()).start();
					System.out.println("CADApp: mouseDragged: key CTRL/ALT-DRAG-LMB: drag vertex position="+drawlocation.x+","+drawlocation.y+","+drawlocation.z);
	    		}
			}
		    boolean mouse2down = ((mouseevent.getButton().equals(MouseButton.MIDDLE))&&(!mouseevent.isControlDown())&&(!mouseevent.isAltDown())&&(!mouseevent.isMetaDown()));
	    	if (mouse2down) {
	    		if (this.texturemode) {
			    	Triangle mousetriangle = null;
		    		if ((this.renderview!=null)&&(this.renderview.tbuffer!=null)) {
		    			if ((this.mouselocationx>=0)&&(this.mouselocationx<this.renderwidth)&&(this.mouselocationy>=0)&&(this.mouselocationy<this.renderheight)) {
			    			mousetriangle = this.renderview.tbuffer[this.mouselocationy][this.mouselocationx];
		    			}
		    		} else if ((this.mouseovertriangle!=null)&&(this.mouseovertriangle.length>0)) {
		    			mousetriangle = this.mouseovertriangle[this.mouseovertriangle.length-1];
		    		}
					if (mousetriangle!=null) {
			        	int mousedeltax = this.mouselocationx - this.mouselastlocationx;
			        	int mousedeltay = this.mouselocationy - this.mouselastlocationy;
			    		double movementstep = 1.0f;
			    		if (this.snaplinemode) {
			    			movementstep = this.gridstep;
			    		}
			        	double texdeltau = movementstep*mousedeltax*0.001f;
			        	double texdeltav = movementstep*mousedeltay*0.001f;
			        	mousetriangle.pos1.tex = mousetriangle.pos1.tex.copy();
			        	mousetriangle.pos2.tex = mousetriangle.pos2.tex.copy();
			        	mousetriangle.pos3.tex = mousetriangle.pos3.tex.copy();
			    		mousetriangle.pos1.tex.u -= texdeltau;
			    		mousetriangle.pos1.tex.v += texdeltav;
			    		mousetriangle.pos2.tex.u -= texdeltau;
			    		mousetriangle.pos2.tex.v += texdeltav;
			    		mousetriangle.pos3.tex.u -= texdeltau;
			    		mousetriangle.pos3.tex.v += texdeltav;
						System.out.println("CADApp: mouseDragged: key TAB-DRAG-CMB: drag texture coordinate="+mousetriangle.pos1.tex.u+","+mousetriangle.pos1.tex.v+" "+mousetriangle.pos2.tex.u+","+mousetriangle.pos2.tex.v+" "+mousetriangle.pos3.tex.u+","+mousetriangle.pos3.tex.v);
					}
	    		}
	    	}
		    boolean mouse2ctrldown = ((mouseevent.getButton().equals(MouseButton.MIDDLE))&&(!mouseevent.isControlDown()));
	    	if (mouse2ctrldown) {
	    		if (!this.texturemode) {
		    		double movementstep = 1.0f;
		    		if (this.snaplinemode) {
		    			movementstep = this.gridstep;
		    		}
		        	int mousedeltax = this.mouselocationx - this.mouselastlocationx; 
		        	int mousedeltay = this.mouselocationy - this.mouselastlocationy;
					this.campos = MathLib.translate(campos, this.camdirs[1], -mousedeltax*movementstep);
					this.campos = MathLib.translate(campos, this.camdirs[2], -mousedeltay*movementstep);
					System.out.println("CADApp: mouseDragged: key DRAG-CMB: camera position="+this.campos[0].x+","+this.campos[0].y+","+this.campos[0].z);
	    		}
	    	}
		    boolean mouse2ctrlaltdown = ((mouseevent.getButton().equals(MouseButton.MIDDLE))&&(mouseevent.isControlDown()));
	    	if (mouse2ctrlaltdown) {
	    		if (!this.texturemode) {
		    		double movementstep = 1.0f;
		    		if (this.snaplinemode) {
		    			movementstep = this.gridstep;
		    		}
		        	int mousedeltax = this.mouselocationx - this.mouselastlocationx; 
		        	int mousedeltay = this.mouselocationy - this.mouselastlocationy;
		        	this.camrot = this.camrot.copy();
		        	this.camrot.z -= mousedeltax*(movementstep/((double)this.gridstep))*0.1f;
		        	this.camrot.x -= mousedeltay*(movementstep/((double)this.gridstep))*0.1f;
					System.out.println("CADApp: mouseDragged: key CTRL-DRAG-CMB: camera rotation angles="+this.camrot.x+","+this.camrot.y+","+this.camrot.z);
	    		}
	    	}
		    boolean mouse3down = ((mouseevent.getButton().equals(MouseButton.SECONDARY))&&(!mouseevent.isControlDown())&&(!mouseevent.isAltDown())&&(!mouseevent.isMetaDown()));
	    	if (mouse3down) {
	    		if ((this.erasemode)&&(this.entitylist!=null)) {
		    		if ((this.mouseoverentity!=null)&&(mouseoverentity.length>0)) {
		    			Entity[] mouseentity = {this.mouseoverentity[this.mouseoverentity.length-1]};
		    			Line[] mouseentitylinelist = MathLib.generateLineList(mouseentity);
		    			this.linelisttree.removeAll(Arrays.asList(mouseentitylinelist));
		    			ArrayList<Entity> entitylistarray = new ArrayList<Entity>(Arrays.asList(this.entitylist));
		    			entitylistarray.remove(mouseentity[0]);
		    			this.entitylist = entitylistarray.toArray(new Entity[entitylistarray.size()]);
		    			this.linelist = linelisttree.toArray(new Line[linelisttree.size()]);
		    		}
	    		} else if (this.entitybuffer!=null) {
	    			//TODO place insert model on mouse-over ground position
	    		} else if (this.selecteddragentity!=null) {
	    			Entity selectedentity = this.selecteddragentity[0];
	    			Line[] selectedentitylinelist = MathLib.generateLineList(this.selecteddragentity);
	    			this.linelisttree.removeAll(Arrays.asList(selectedentitylinelist));
	        		Position[] drawposarray = {this.mousepos};
	    			Direction[] drawmovedir = MathLib.vectorFromPoints(this.drawstartpos, drawposarray);
	    			this.drawstartpos = drawposarray[0];
	    			selectedentity.translateSelf(drawmovedir[0], 1.0f);
	    			Line[] movedentitylinelist = MathLib.generateLineList(this.selecteddragentity);
	    			this.linelisttree.addAll(Arrays.asList(movedentitylinelist));
	    			this.linelist = linelisttree.toArray(new Line[linelisttree.size()]);
	    			System.out.println("CADApp: mouseDragged: key DRAG-RMB: move entity ="+selectedentity.sphereboundaryvolume.x+" "+selectedentity.sphereboundaryvolume.y+" "+selectedentity.sphereboundaryvolume.z);
	    		}
	    	}
		    boolean mouse3altdown = ((mouseevent.getButton().equals(MouseButton.SECONDARY))&&(!mouseevent.isControlDown())&&(mouseevent.isAltDown())&&(!mouseevent.isMetaDown()));
	    	if (mouse3altdown) {
    			if (this.entitybuffer!=null) {
    				Sphere[] entitycentersphere = {this.entitybuffer.sphereboundaryvolume};
	    			Position[] entitycenterspherepos = MathLib.sphereVertexList(entitycentersphere);
		        	int mousedeltax = this.mouselocationx - this.mouselastlocationx;
		        	int mousedeltay = this.mouselocationy - this.mouselastlocationy;
		        	double scalemultx = ((double)mousedeltax)/10000.0f;
		        	double scalemulty = ((double)mousedeltay)/10000.0f;
		        	if (mouseevent.isShiftDown()) {
		        		scalemultx *= this.gridstep;
		        		scalemulty *= this.gridstep;
		        	}
		        	scalemultx = 1.0f+scalemultx;
		        	scalemulty = 1.0f-scalemulty;
    				for (int i=0;i<this.entitybuffer.childlist.length;i++) {
    					this.entitybuffer.childlist[i].scaleSelfAroundPos(entitycenterspherepos[0], new Scaling(scalemultx, scalemultx, scalemultx));
    					this.entitybuffer.childlist[i].scaleSelfAroundPos(entitycenterspherepos[0], new Scaling(1, 1, scalemulty));
    				}
    				for (int i=0;i<this.entitybuffer.linelist.length;i++) {
    					this.entitybuffer.linelist[i].scaleSelfAroundPos(entitycenterspherepos[0], new Scaling(scalemultx, scalemultx, scalemultx));
    					this.entitybuffer.linelist[i].scaleSelfAroundPos(entitycenterspherepos[0], new Scaling(1, 1, scalemulty));
    				}
    			} else if (this.selecteddragentity!=null) {
	    			Entity selectedentity = this.selecteddragentity[0];
	    			Line[] selectedentitylinelist = MathLib.generateLineList(this.selecteddragentity);
	    			Sphere[] selectedentitysphere = MathLib.entitySphereList(this.selecteddragentity);
	    			Position[] selectedentitypos = MathLib.sphereVertexList(selectedentitysphere);
	    			this.linelisttree.removeAll(Arrays.asList(selectedentitylinelist));
		        	int mousedeltax = this.mouselocationx - this.mouselastlocationx;
		        	int mousedeltay = this.mouselocationy - this.mouselastlocationy;
		        	double scalemultx = ((double)mousedeltax)/10000.0f;
		        	double scalemulty = ((double)mousedeltay)/10000.0f;
		        	if (mouseevent.isShiftDown()) {
		        		scalemultx *= this.gridstep;
		        		scalemulty *= this.gridstep;
		        	}
		        	scalemultx = 1.0f+scalemultx;
		        	scalemulty = 1.0f-scalemulty;
	    			selectedentity.scaleSelfAroundPos(selectedentitypos[0], new Scaling(scalemultx, scalemultx, scalemultx));
	    			selectedentity.scaleSelfAroundPos(selectedentitypos[0], new Scaling(1, 1, scalemulty));
	    			Line[] movedentitylinelist = MathLib.generateLineList(this.selecteddragentity);
	    			this.linelisttree.addAll(Arrays.asList(movedentitylinelist));
	    			this.linelist = linelisttree.toArray(new Line[linelisttree.size()]);
	    			System.out.println("CADApp: mouseDragged: key SHIFT-DRAG-RMB: rotate entity ="+selectedentity.sphereboundaryvolume.x+" "+selectedentity.sphereboundaryvolume.y+" "+selectedentity.sphereboundaryvolume.z);
		    	}
	    	}
		    boolean mouse3ctrldown = ((mouseevent.getButton().equals(MouseButton.SECONDARY))&&(mouseevent.isControlDown())&&(!mouseevent.isAltDown())&&(!mouseevent.isMetaDown()));
	    	if (mouse3ctrldown) {
    			if (this.entitybuffer!=null) {
    				Sphere[] entitycentersphere = {this.entitybuffer.sphereboundaryvolume};
	    			Position[] entitycenterspherepos = MathLib.sphereVertexList(entitycentersphere);
		        	int mousedeltax = this.mouselocationx - this.mouselastlocationx;
		        	int mousedeltay = this.mouselocationy - this.mouselastlocationy;
		        	double rotmultx = -((double)mousedeltax)/100.0f;
		        	double rotmulty = ((double)mousedeltay)/100.0f;
		        	if (mouseevent.isShiftDown()) {
		        		rotmultx *= this.gridstep;
		        		rotmulty *= this.gridstep;
		        	}
    				for (int i=0;i<this.entitybuffer.childlist.length;i++) {
    					this.entitybuffer.childlist[i].rotateSelfAroundAxisPos(entitycenterspherepos[0], this.camdirs[1], rotmulty);
    					this.entitybuffer.childlist[i].rotateSelfAroundAxisPos(entitycenterspherepos[0], this.camdirs[2], rotmultx);
    				}
    				for (int i=0;i<this.entitybuffer.linelist.length;i++) {
    					this.entitybuffer.linelist[i].rotateSelfAroundAxisPos(entitycenterspherepos[0], this.camdirs[1], rotmulty);
    					this.entitybuffer.linelist[i].rotateSelfAroundAxisPos(entitycenterspherepos[0], this.camdirs[2], rotmultx);
    				}
    			} else if (this.selecteddragentity!=null) {
	    			Entity selectedentity = this.selecteddragentity[0];
	    			Line[] selectedentitylinelist = MathLib.generateLineList(this.selecteddragentity);
	    			Sphere[] selectedentitysphere = MathLib.entitySphereList(this.selecteddragentity);
	    			Position[] selectedentitypos = MathLib.sphereVertexList(selectedentitysphere);
	    			this.linelisttree.removeAll(Arrays.asList(selectedentitylinelist));
		        	int mousedeltax = this.mouselocationx - this.mouselastlocationx;
		        	int mousedeltay = this.mouselocationy - this.mouselastlocationy;
		        	double rotmultx = -((double)mousedeltax)/100.0f;
		        	double rotmulty = ((double)mousedeltay)/100.0f;
		        	if (mouseevent.isShiftDown()) {
		        		rotmultx *= this.gridstep;
		        		rotmulty *= this.gridstep;
		        	}
	    			selectedentity.rotateSelfAroundAxisPos(selectedentitypos[0], this.camdirs[1], rotmulty);
	    			selectedentity.rotateSelfAroundAxisPos(selectedentitypos[0], this.camdirs[2], rotmultx);
	    			Line[] movedentitylinelist = MathLib.generateLineList(this.selecteddragentity);
	    			this.linelisttree.addAll(Arrays.asList(movedentitylinelist));
	    			this.linelist = linelisttree.toArray(new Line[linelisttree.size()]);
	    			System.out.println("CADApp: mouseDragged: key SHIFT-DRAG-RMB: rotate entity ="+selectedentity.sphereboundaryvolume.x+" "+selectedentity.sphereboundaryvolume.y+" "+selectedentity.sphereboundaryvolume.z);
		    	}
	    	}
		} else if (event.getEventType().equals(ScrollEvent.SCROLL)) {
			ScrollEvent scrollevent = (ScrollEvent)event;
			double scrollticksX = -scrollevent.getDeltaX()/scrollevent.getMultiplierX();
			double scrollticksY = -scrollevent.getDeltaY()/scrollevent.getMultiplierY();
		    boolean mousewheeldown = ((!scrollevent.isControlDown())&&(!scrollevent.isAltDown())&&(!scrollevent.isShiftDown())&&(!scrollevent.isMetaDown()));
		    if (mousewheeldown) {
		    	if (this.texturemode) {
			    	Triangle mousetriangle = null;
		    		if ((this.renderview!=null)&&(this.renderview.tbuffer!=null)) {
		    			if ((this.mouselocationx>=0)&&(this.mouselocationx<this.renderwidth)&&(this.mouselocationy>=0)&&(this.mouselocationy<this.renderheight)) {
			    			mousetriangle = this.renderview.tbuffer[this.mouselocationy][this.mouselocationx];
		    			}
		    		} else if ((this.mouseovertriangle!=null)&&(this.mouseovertriangle.length>0)) {
		    			mousetriangle = this.mouseovertriangle[this.mouseovertriangle.length-1];
		    		}
					if (mousetriangle!=null) {
			        	mousetriangle.pos1.tex = mousetriangle.pos1.tex.copy();
			        	mousetriangle.pos2.tex = mousetriangle.pos2.tex.copy();
			        	mousetriangle.pos3.tex = mousetriangle.pos3.tex.copy();
			    		mousetriangle.pos1.tex.u *= 1-(0.01f*scrollticksY);
			    		mousetriangle.pos1.tex.v *= 1-(0.01f*scrollticksY);
			    		mousetriangle.pos2.tex.u *= 1-(0.01f*scrollticksY);
			    		mousetriangle.pos2.tex.v *= 1-(0.01f*scrollticksY);
			    		mousetriangle.pos3.tex.u *= 1-(0.01f*scrollticksY);
			    		mousetriangle.pos3.tex.v *= 1-(0.01f*scrollticksY);
						System.out.println("CADApp: mouseWheelMoved: key TAB-MWHEEL: zoom texture coordinate="+mousetriangle.pos1.tex.u+","+mousetriangle.pos1.tex.v+" "+mousetriangle.pos2.tex.u+","+mousetriangle.pos2.tex.v+" "+mousetriangle.pos3.tex.u+","+mousetriangle.pos3.tex.v);
					}
		    	}
		    }
		    boolean mousewheelshiftdown = ((!scrollevent.isControlDown())&&(!scrollevent.isAltDown())&&(scrollevent.isShiftDown())&&(!scrollevent.isMetaDown()));
		    if (mousewheelshiftdown) {
		    	if (this.texturemode) {
			    	Triangle mousetriangle = null;
		    		if ((this.renderview!=null)&&(this.renderview.tbuffer!=null)) {
		    			if ((this.mouselocationx>=0)&&(this.mouselocationx<this.renderwidth)&&(this.mouselocationy>=0)&&(this.mouselocationy<this.renderheight)) {
			    			mousetriangle = this.renderview.tbuffer[this.mouselocationy][this.mouselocationx];
		    			}
		    		} else if ((this.mouseovertriangle!=null)&&(this.mouseovertriangle.length>0)) {
		    			mousetriangle = this.mouseovertriangle[this.mouseovertriangle.length-1];
		    		}
					if (mousetriangle!=null) {
			        	AffineTransform textr = new AffineTransform();
			        	textr.rotate(0.01f*scrollticksX);
			        	Point2D pos1tex = new Point2D.Double(mousetriangle.pos1.tex.u,mousetriangle.pos1.tex.v); 
			        	Point2D pos2tex = new Point2D.Double(mousetriangle.pos2.tex.u,mousetriangle.pos2.tex.v); 
			        	Point2D pos3tex = new Point2D.Double(mousetriangle.pos3.tex.u,mousetriangle.pos3.tex.v); 
			        	textr.transform(pos1tex, pos1tex);
			        	textr.transform(pos2tex, pos2tex);
			        	textr.transform(pos3tex, pos3tex);
			        	mousetriangle.pos1.tex = new Coordinate(pos1tex.getX(),pos1tex.getY());
			        	mousetriangle.pos2.tex = new Coordinate(pos2tex.getX(),pos2tex.getY());
			        	mousetriangle.pos3.tex = new Coordinate(pos3tex.getX(),pos3tex.getY());
						System.out.println("CADApp: mouseWheelMoved: key TAB-SHIFT-MWHEEL: rotate texture coordinate="+mousetriangle.pos1.tex.u+","+mousetriangle.pos1.tex.v+" "+mousetriangle.pos2.tex.u+","+mousetriangle.pos2.tex.v+" "+mousetriangle.pos3.tex.u+","+mousetriangle.pos3.tex.v);
					}
		    	}
		    }
		    boolean mousewheelctrlshiftdown = ((scrollevent.isControlDown())&&(!scrollevent.isAltDown())&&(scrollevent.isShiftDown())&&(!scrollevent.isMetaDown()));
		    if (mousewheelctrlshiftdown) {
		    	if (this.texturemode) {
			    	Triangle mousetriangle = null;
		    		if ((this.renderview!=null)&&(this.renderview.tbuffer!=null)) {
		    			if ((this.mouselocationx>=0)&&(this.mouselocationx<this.renderwidth)&&(this.mouselocationy>=0)&&(this.mouselocationy<this.renderheight)) {
			    			mousetriangle = this.renderview.tbuffer[this.mouselocationy][this.mouselocationx];
		    			}
		    		} else if ((this.mouseovertriangle!=null)&&(this.mouseovertriangle.length>0)) {
		    			mousetriangle = this.mouseovertriangle[this.mouseovertriangle.length-1];
		    		}
					if (mousetriangle!=null) {
			        	AffineTransform textr = new AffineTransform();
			        	textr.scale(1+0.01f*scrollticksX,1.0f);
			        	Point2D pos1tex = new Point2D.Double(mousetriangle.pos1.tex.u,mousetriangle.pos1.tex.v); 
			        	Point2D pos2tex = new Point2D.Double(mousetriangle.pos2.tex.u,mousetriangle.pos2.tex.v); 
			        	Point2D pos3tex = new Point2D.Double(mousetriangle.pos3.tex.u,mousetriangle.pos3.tex.v); 
			        	textr.transform(pos1tex, pos1tex);
			        	textr.transform(pos2tex, pos2tex);
			        	textr.transform(pos3tex, pos3tex);
			        	mousetriangle.pos1.tex = new Coordinate(pos1tex.getX(),pos1tex.getY());
			        	mousetriangle.pos2.tex = new Coordinate(pos2tex.getX(),pos2tex.getY());
			        	mousetriangle.pos3.tex = new Coordinate(pos3tex.getX(),pos3tex.getY());
						System.out.println("CADApp: mouseWheelMoved: key TAB-ALT-MWHEEL: scale texture coordinate="+mousetriangle.pos1.tex.u+","+mousetriangle.pos1.tex.v+" "+mousetriangle.pos2.tex.u+","+mousetriangle.pos2.tex.v+" "+mousetriangle.pos3.tex.u+","+mousetriangle.pos3.tex.v);
					}
		    	}
		    }
		    boolean mousewheelctrldown = ((scrollevent.isControlDown())&&(!scrollevent.isAltDown())&&(!scrollevent.isShiftDown())&&(!scrollevent.isMetaDown()));
		    if (mousewheelctrldown) {
		    	if (this.texturemode) {
			    	Triangle mousetriangle = null;
		    		if ((this.renderview!=null)&&(this.renderview.tbuffer!=null)) {
		    			if ((this.mouselocationx>=0)&&(this.mouselocationx<this.renderwidth)&&(this.mouselocationy>=0)&&(this.mouselocationy<this.renderheight)) {
			    			mousetriangle = this.renderview.tbuffer[this.mouselocationy][this.mouselocationx];
		    			}
		    		} else if ((this.mouseovertriangle!=null)&&(this.mouseovertriangle.length>0)) {
		    			mousetriangle = this.mouseovertriangle[this.mouseovertriangle.length-1];
		    		}
					if (mousetriangle!=null) {
			        	AffineTransform textr = new AffineTransform();
			        	textr.shear(0.01f*scrollticksY,0.0f);
			        	Point2D pos1tex = new Point2D.Double(mousetriangle.pos1.tex.u,mousetriangle.pos1.tex.v); 
			        	Point2D pos2tex = new Point2D.Double(mousetriangle.pos2.tex.u,mousetriangle.pos2.tex.v); 
			        	Point2D pos3tex = new Point2D.Double(mousetriangle.pos3.tex.u,mousetriangle.pos3.tex.v); 
			        	textr.transform(pos1tex, pos1tex);
			        	textr.transform(pos2tex, pos2tex);
			        	textr.transform(pos3tex, pos3tex);
			        	mousetriangle.pos1.tex = new Coordinate(pos1tex.getX(),pos1tex.getY());
			        	mousetriangle.pos2.tex = new Coordinate(pos2tex.getX(),pos2tex.getY());
			        	mousetriangle.pos3.tex = new Coordinate(pos3tex.getX(),pos3tex.getY());
						System.out.println("CADApp: mouseWheelMoved: key TAB-CTRL-MWHEEL: shear texture coordinate="+mousetriangle.pos1.tex.u+","+mousetriangle.pos1.tex.v+" "+mousetriangle.pos2.tex.u+","+mousetriangle.pos2.tex.v+" "+mousetriangle.pos3.tex.u+","+mousetriangle.pos3.tex.v);
					}
		    	}
		    }
	    	if (!this.texturemode) {
				double movementstep = 0.0f;
				if (scrollevent.isShiftDown()) {
					movementstep = 200.0f*scrollticksX;
				} else {
					movementstep = 200.0f*scrollticksY;
				}
				if (this.snaplinemode) {
					movementstep *= this.gridstep;
				}
				this.campos = MathLib.translate(campos, this.camdirs[0], -movementstep);
				System.out.println("CADApp: mouseWheelMoved: key MWHEEL: camera position="+this.campos[0].x+","+this.campos[0].y+","+this.campos[0].z);
	    	}
		} else if (event.getEventType().equals(DragEvent.DRAG_OVER)) {
			DragEvent dragevent = (DragEvent)event;
			Dragboard db = dragevent.getDragboard();
			if (db.hasFiles()) {
				dragevent.acceptTransferModes(TransferMode.ANY);
			}
		} else if (event.getEventType().equals(DragEvent.DRAG_DROPPED)) {
			DragEvent dragevent = (DragEvent)event;
			Dragboard db = dragevent.getDragboard();
			boolean success = false;
			if (db.hasFiles()) {
				List<File> files = (List<File>)db.getFiles();
                for (Iterator<File> i=files.iterator();i.hasNext();) {
                	File file = i.next();
                	String filepath = file.getPath();
                	String filename = file.getName();
                	if (UtilLib.isImageFilename(filename)) {
                		BufferedImage fileimage = UtilLib.loadImage(filepath, false);
						this.drawmat = this.drawmat.copy();
						this.drawmat.fileimage = fileimage;
                	} else if (UtilLib.isModelFilename(filename)) {
                		if (filename.toLowerCase().endsWith(".obj")) {
                			FileFilter loadfileformat = new OBJFileFilter();
                			this.entitybuffer = UtilLib.loadModelFormat(filepath, loadfileformat, false);
                		} else if (filename.toLowerCase().endsWith(".stl")) {
                			FileFilter loadfileformat = new STLFileFilter();
                			this.entitybuffer = UtilLib.loadModelFormat(filepath, loadfileformat, false);
                		}
                	}
                }
	        	success = true;
			}
        	dragevent.setDropCompleted(success);
		}
	}
	
	private class EntityListUpdater extends Thread {
		private static boolean entitylistupdaterrunning = false;
		public void run() {
			if (!EntityListUpdater.entitylistupdaterrunning) {
				EntityListUpdater.entitylistupdaterrunning = true;
				ArrayList<Triangle> entitylisttrianglearray = new ArrayList<Triangle>();
				if (entitylist!=null) {
					for (int i=0;i<entitylist.length;i++) {
						if (entitylist[i].trianglelist!=null) {
							entitylisttrianglearray.addAll(Arrays.asList(entitylist[i].trianglelist));
						}
					}
				}
				Line[] copylinelist = linelisttree.toArray(new Line[linelisttree.size()]);
				Entity[] newentitylist = MathLib.generateEntityList(copylinelist);
				Material newmat = drawmat;
				for (int j=0;j<newentitylist.length;j++) {
					for (int i=0;i<newentitylist[j].trianglelist.length;i++) {
						newentitylist[j].trianglelist[i].mat = newmat;
						newentitylist[j].trianglelist[i].norm = new Direction(0.0f,0.0f,0.0f);
						newentitylist[j].trianglelist[i].pos1.tex = new Coordinate(0.0f,0.0f);
						newentitylist[j].trianglelist[i].pos2.tex = new Coordinate(1.0f,0.0f);
						newentitylist[j].trianglelist[i].pos3.tex = new Coordinate(0.0f,1.0f);
						int searchindex = entitylisttrianglearray.indexOf(newentitylist[j].trianglelist[i]);
						if (searchindex>=0) {
							newentitylist[j].trianglelist[i] = entitylisttrianglearray.get(searchindex).copy(); 
						}
					}
				}
				entitylist = newentitylist;
    			linelist = linelisttree.toArray(new Line[linelisttree.size()]);
				EntityListUpdater.entitylistupdaterrunning = false;
			}
		}
	}

	private class RenderViewUpdater extends Thread {
		private static boolean renderupdaterrunning = false;
		public void run() {
			if (!RenderViewUpdater.renderupdaterrunning) {
				RenderViewUpdater.renderupdaterrunning = true;
				int bounces = 0;
				if (polygonfillmode==1) {
					RenderView mouseoverview = RenderLib.renderProjectedView(campos[0], entitylist, renderwidth, hfov, renderheight, vfov, cameramat, unlitrender, 1, bounces, null, null, null, mouselocationx, mouselocationy);
					RenderView drawrenderview = RenderLib.renderProjectedLineViewHardware(campos[0], linelist, renderwidth, hfov, renderheight, vfov, true, cameramat, mouselocationx, mouselocationy);
					if (entitybuffer!=null) {
						Line[] copyentitybufferlinelist = new Line[entitybuffer.linelist.length];
						for (int i=0;i<entitybuffer.linelist.length;i++) {
							copyentitybufferlinelist[i] = entitybuffer.linelist[i].translate(mousepos);
						}
						RenderView entitybufferview = RenderLib.renderProjectedLineViewHardware(campos[0], copyentitybufferlinelist, renderwidth, hfov, renderheight, vfov, false, cameramat, mouselocationx, mouselocationy);
						Graphics2D viewgfx = drawrenderview.renderimage.createGraphics();
						viewgfx.setComposite(AlphaComposite.SrcOver);
						viewgfx.drawImage(entitybufferview.renderimage, 0, 0, null);
						viewgfx.dispose();
					}
					renderview = drawrenderview;
					mouseoverline = drawrenderview.mouseoverline;
					mouseoververtex = drawrenderview.mouseoververtex;
					mouseoverentity = mouseoverview.mouseoverentity;
					mouseovertriangle = mouseoverview.mouseovertriangle;
					renderview.tbuffer = mouseoverview.tbuffer;
					renderview.cbuffer = mouseoverview.cbuffer;
				} else if (polygonfillmode==2) { 
					RenderView drawrenderview = RenderLib.renderProjectedView(campos[0], entitylist, renderwidth, hfov, renderheight, vfov, cameramat, unlitrender, 1, bounces, null, null, null, mouselocationx, mouselocationy);
					if (entitybuffer!=null) {
						Entity[] copyentitybuffer = new Entity[entitybuffer.childlist.length];
						for (int i=0;i<entitybuffer.childlist.length;i++) {
		        			copyentitybuffer[i] = entitybuffer.childlist[i].translate(mousepos);
		        		}
						RenderView entitybufferview = RenderLib.renderProjectedView(campos[0], copyentitybuffer, renderwidth, hfov, renderheight, vfov, cameramat, unlitrender, 1, bounces, null, null, null, mouselocationx, mouselocationy);
						Graphics2D viewgfx = drawrenderview.renderimage.createGraphics();
						viewgfx.setComposite(AlphaComposite.SrcOver);
						viewgfx.drawImage(entitybufferview.renderimage, 0, 0, null);
						viewgfx.dispose();
					}
					renderview = drawrenderview;
					mouseoverline = null;
					mouseoververtex = null;
					mouseoverentity = drawrenderview.mouseoverentity;
					mouseovertriangle = drawrenderview.mouseovertriangle;
				} else if (polygonfillmode==3) {
					RenderView drawrenderview = RenderLib.renderProjectedView(campos[0], entitylist, renderwidth, hfov, renderheight, vfov, cameramat, unlitrender, 2, bounces, null, null, null, mouselocationx, mouselocationy);
					if (entitybuffer!=null) {
						Entity[] copyentitybuffer = new Entity[entitybuffer.childlist.length];
						for (int i=0;i<entitybuffer.childlist.length;i++) {
		        			copyentitybuffer[i] = entitybuffer.childlist[i].translate(mousepos);
		        		}
						RenderView entitybufferview = RenderLib.renderProjectedView(campos[0], copyentitybuffer, renderwidth, hfov, renderheight, vfov, cameramat, unlitrender, 2, bounces, null, null, null, mouselocationx, mouselocationy);
						Graphics2D viewgfx = drawrenderview.renderimage.createGraphics();
						viewgfx.setComposite(AlphaComposite.SrcOver);
						viewgfx.drawImage(entitybufferview.renderimage, 0, 0, null);
						viewgfx.dispose();
					}
					renderview = drawrenderview;
					mouseoverline = null;
					mouseoververtex = null;
					mouseoverentity = drawrenderview.mouseoverentity;
					mouseovertriangle = drawrenderview.mouseovertriangle;
				}
				RenderViewUpdater.renderupdaterrunning = false;
			}
		}
	}

	private class ImageRenderer extends Thread {
		private String filename;
		private FileFilter saveformat;
		private boolean rendercubemap;
		private boolean renderspheremap;
		private boolean renderbackground;
		public ImageRenderer(String filenamei, FileFilter saveformati, boolean rendercubemapi, boolean renderspheremapi, boolean renderbackgroundi) {
			this.filename = filenamei;
			this.saveformat = saveformati;
			this.rendercubemap = rendercubemapi;
			this.renderspheremap = renderspheremapi;
			this.renderbackground = renderbackgroundi;
		}
		public void run() {
			RenderView renderimageview = null;
			if (rendercubemap) {
				renderimageview = RenderLib.renderCubemapView(campos[0], entitylist, rendercubemapoutputwidth, rendercubemapoutputheight, rendercubemapoutputsize, cameramat, unlitrender, 3, renderbounces, null, null, null, mouselocationx, mouselocationy);
			} else if (renderspheremap) {
				renderimageview = RenderLib.renderSpheremapView(campos[0], entitylist, renderspheremapoutputwidth, renderspheremapoutputheight, cameramat, unlitrender, 2, renderbounces, null, null, null, mouselocationx, mouselocationy);
			} else {
				renderimageview = RenderLib.renderProjectedView(campos[0], entitylist, renderoutputwidth, hfov, renderoutputheight, vfov, cameramat, unlitrender, 3, renderbounces, null, null, null, mouselocationx, mouselocationy);
			}
			BufferedImage renderimage = renderimageview.renderimage;
			if (renderbackground) {
				BufferedImage blackbgimage = gc.createCompatibleImage(renderimage.getWidth(), renderimage.getHeight(), Transparency.TRANSLUCENT);
				Graphics2D bbggfx = blackbgimage.createGraphics();
				bbggfx.setComposite(AlphaComposite.Src);
				bbggfx.setColor(renderbackgroundcolor);
				bbggfx.fillRect(0, 0, renderimage.getWidth(), renderimage.getHeight());
				bbggfx.setComposite(AlphaComposite.SrcOver);
				bbggfx.drawImage(renderimage, 0, 0, null);
				bbggfx.dispose();
				renderimage = blackbgimage;
			}
    		UtilLib.saveImageFormat(filename, renderimage, saveformat);
		}
	}
	
}
