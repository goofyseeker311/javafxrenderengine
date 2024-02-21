package fi.jkauppa.javarenderengine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.VolatileImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import fi.jkauppa.javarenderengine.JavaRenderEngine.AppHandlerPanel;
import fi.jkauppa.javarenderengine.ModelLib.Coordinate;
import fi.jkauppa.javarenderengine.ModelLib.Direction;
import fi.jkauppa.javarenderengine.ModelLib.Entity;
import fi.jkauppa.javarenderengine.ModelLib.Line;
import fi.jkauppa.javarenderengine.ModelLib.Material;
import fi.jkauppa.javarenderengine.ModelLib.Matrix;
import fi.jkauppa.javarenderengine.ModelLib.Position;
import fi.jkauppa.javarenderengine.ModelLib.RenderView;
import fi.jkauppa.javarenderengine.ModelLib.Rotation;
import fi.jkauppa.javarenderengine.ModelLib.Triangle;
import fi.jkauppa.javarenderengine.UtilLib.ModelFileFilters.OBJFileFilter;
import fi.jkauppa.javarenderengine.UtilLib.ModelFileFilters.STLFileFilter;
import fi.jkauppa.javarenderengine.ModelLib.Plane;

public class CADApp extends AppHandlerPanel {
	private static final long serialVersionUID = 1L;
	private boolean draglinemode = false;
	private boolean snaplinemode = false;
	private boolean texturemode = false;
	private boolean erasemode = false;
	private Material drawmat = new Material(Color.getHSBColor(0.0f, 0.0f, 1.0f),1.0f,null);
	private int renderoutputwidth = 7680, renderoutputheight = 4320;
	private int rendercubemapoutputsize = 4096, rendercubemapoutputwidth = 3*rendercubemapoutputsize, rendercubemapoutputheight = 2*rendercubemapoutputsize;
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
	private final double defaultcamdist = 1371.023f;
	private final Position[] defaultcampos = {new Position(0.0f,0.0f,defaultcamdist)};
	private final Rotation defaultcamrot = new Rotation(0.0f, 0.0f, 0.0f);
	private Position drawstartpos = new Position(0,0,0);
	private Position editpos = new Position(0.0f,0.0f,0.0f);
	private Position mousepos = this.editpos;
	private Position[] campos = this.defaultcampos;
	private Rotation camrot = this.defaultcamrot;
	private Matrix cameramat = MathLib.rotationMatrix(0.0f, 0.0f, 0.0f);
	private final Direction[] lookdirs = MathLib.projectedCameraDirections(cameramat);
	private Direction[] camdirs = lookdirs;
	private double hfov = 70.0f;
	private double vfov = 43.0f;
	private int gridstep = 20;
	private TreeSet<Line> linelisttree = new TreeSet<Line>();
	private Entity[] entitylist = null;
	private JFileChooser filechooser = UtilLib.createModelFileChooser();
	private JFileChooser imagechooser = UtilLib.createImageFileChooser();
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
	private RenderView renderview = null;
	
	public CADApp() {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		this.setFocusTraversalKeysEnabled(false);
	}
	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setComposite(AlphaComposite.Src);
		g2.setColor(Color.BLACK);
		g2.setPaint(null);
		g2.setClip(null);
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		this.vfov = MathLib.calculateVfov(this.getWidth(), this.getHeight(), hfov);
		if (this.renderview!=null) {
			g2.drawImage(this.renderview.renderimage, 0, 0, null);
		}
	}
	
	@Override public void timerTick() {
		double movementstep = 1.0f;
		if (this.snaplinemode) {
			movementstep = this.gridstep;
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
        	this.camrot.z += (movementstep/((double)this.gridstep))*1.0f;
			System.out.println("CADApp: keyPressed: key ARROW-LEFT: camera yaw rotation left="+this.camrot.x+","+this.camrot.y+","+this.camrot.z);
		} else if (this.yawrightkeydown) {
			this.camrot = this.camrot.copy();
        	this.camrot.z -= (movementstep/((double)this.gridstep))*1.0f;
			System.out.println("CADApp: keyPressed: key ARROW-RIGHT: camera yaw rotation right="+this.camrot.x+","+this.camrot.y+","+this.camrot.z);
		}
		if (this.pitchupkeydown) {
			this.camrot = this.camrot.copy();
        	this.camrot.x += (movementstep/((double)this.gridstep))*1.0f;
			System.out.println("CADApp: keyPressed: key ARROW-DOWN: camera yaw rotation down="+this.camrot.x+","+this.camrot.y+","+this.camrot.z);
		} else if (this.pitchdownkeydown) {
			this.camrot = this.camrot.copy();
        	this.camrot.x -= (movementstep/((double)this.gridstep))*1.0f;
			System.out.println("CADApp: keyPressed: key ARROW-UP: camera yaw rotation up="+this.camrot.x+","+this.camrot.y+","+this.camrot.z);
		}
		updateCameraDirections();
		(new RenderViewUpdater()).start();
	}

	private void updateCameraDirections() {
		Matrix camrotmat = MathLib.rotationMatrixLookHorizontalRoll(this.camrot);
		Direction[] camlookdirs = MathLib.projectedCameraDirections(camrotmat);
		Position[] editposa = MathLib.translate(this.campos, camlookdirs[0], this.defaultcamdist);
		this.mousepos = MathLib.cameraPlanePosition(this.editpos, this.mouselocationx, this.mouselocationy, this.getWidth(), this.getHeight(), this.snaplinemode, this.gridstep, this.cameramat);
		this.editpos = editposa[0];
		this.cameramat = camrotmat;
		this.camdirs = camlookdirs;
	}
	
	@Override public void keyTyped(KeyEvent e) {}
	@Override public void keyReleased(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_UP) {
			this.pitchupkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_DOWN) {
			this.pitchdownkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_LEFT) {
			this.yawleftkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_RIGHT) {
			this.yawrightkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_SHIFT) {
			this.snaplinemode = false;
		} else if (e.getKeyCode()==KeyEvent.VK_TAB) {
			this.texturemode = false;
		} else if (e.getKeyCode()==KeyEvent.VK_PERIOD) {
			this.erasemode = false;
		} else if (e.getKeyCode()==KeyEvent.VK_W) {
			this.upwardkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_S) {
			this.downwardkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_A) {
			this.leftkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_D) {
			this.rightkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_SPACE) {
			this.forwardkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_C) {
			this.backwardkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_Q) {
			this.rollleftkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_E) {
			this.rollrightkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_ADD) {
			this.forwardkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_SUBTRACT) {
			this.backwardkeydown = false;
		}
	}
	@Override public void keyPressed(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_BACK_SPACE) {
			if (e.isControlDown()) {
				if (this.entitylist!=null) {
					for (int j=0;j<this.entitylist.length;j++) {
						if (this.entitylist[j].trianglelist!=null) {
							for (int i=0;i<this.entitylist[j].trianglelist.length;i++) {
								if (this.entitylist[j].trianglelist[i].mat!=null) {
									this.entitylist[j].trianglelist[i].mat = this.entitylist[j].trianglelist[i].mat.copy();
									this.entitylist[j].trianglelist[i].mat.ambientcolor = null;
									this.entitylist[j].trianglelist[i].mat.ambientfileimage = null;
									this.entitylist[j].trianglelist[i].mat.ambientsnapimage = null;
								}
							}
						}
					}
				}
			} else {
				if (!e.isShiftDown()) {
					this.linelisttree.clear();
					this.entitylist = null;
				}
				this.campos = this.defaultcampos;
				this.camrot = this.defaultcamrot;
				System.out.println("CADApp: keyPressed: key BACKSPACE: reset camera position="+this.campos[0].x+","+this.campos[0].y+","+this.campos[0].z);
			}
		} else if (e.getKeyCode()==KeyEvent.VK_INSERT) {
			float[] drawcolorhsb = Color.RGBtoHSB(this.drawmat.facecolor.getRed(), this.drawmat.facecolor.getGreen(), this.drawmat.facecolor.getBlue(), new float[3]);
			drawcolorhsb[0] += 0.01f; if (drawcolorhsb[0]>1.0f) {drawcolorhsb[0] = 0.0f;}
			Color hsbcolor = Color.getHSBColor(drawcolorhsb[0], drawcolorhsb[1], drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			Color newfacecolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.drawmat.transparency);
			this.drawmat = this.drawmat.copy();
			this.drawmat.facecolor = newfacecolor;
			this.drawmat.fileimage = null;
			this.drawmat.snapimage = null;
			this.entitybuffer = null;
			System.out.println("CADApp: keyPressed: key INSERT: draw material color hue positive: r="+colorvalues[0]+" g="+colorvalues[1]+" b="+colorvalues[2]+" tr="+this.drawmat.transparency);
		} else if (e.getKeyCode()==KeyEvent.VK_DELETE) {
			float[] drawcolorhsb = Color.RGBtoHSB(this.drawmat.facecolor.getRed(), this.drawmat.facecolor.getGreen(), this.drawmat.facecolor.getBlue(), new float[3]);
			drawcolorhsb[0] -= 0.01f; if (drawcolorhsb[0]<0.0f) {drawcolorhsb[0] = 1.0f;}
			Color hsbcolor = Color.getHSBColor(drawcolorhsb[0], drawcolorhsb[1], drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			Color newfacecolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.drawmat.transparency);
			this.drawmat = this.drawmat.copy();
			this.drawmat.facecolor = newfacecolor;
			this.drawmat.fileimage = null;
			this.drawmat.snapimage = null;
			this.entitybuffer = null;
			System.out.println("CADApp: keyPressed: key DELETE: draw material color hue negative: r="+colorvalues[0]+" g="+colorvalues[1]+" b="+colorvalues[2]+" tr="+this.drawmat.transparency);
		} else if (e.getKeyCode()==KeyEvent.VK_HOME) {
			float[] drawcolorhsb = Color.RGBtoHSB(this.drawmat.facecolor.getRed(), this.drawmat.facecolor.getGreen(), this.drawmat.facecolor.getBlue(), new float[3]);
			drawcolorhsb[1] += 0.01f; if (drawcolorhsb[1]>1.0f) {drawcolorhsb[1] = 1.0f;}
			Color hsbcolor = Color.getHSBColor(drawcolorhsb[0], drawcolorhsb[1], drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			Color newfacecolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.drawmat.transparency);
			this.drawmat = this.drawmat.copy();
			this.drawmat.facecolor = newfacecolor;
			this.drawmat.fileimage = null;
			this.drawmat.snapimage = null;
			this.entitybuffer = null;
			System.out.println("CADApp: keyPressed: key HOME: draw material color saturation positive: r="+colorvalues[0]+" g="+colorvalues[1]+" b="+colorvalues[2]+" tr="+this.drawmat.transparency);
		} else if (e.getKeyCode()==KeyEvent.VK_END) {
			float[] drawcolorhsb = Color.RGBtoHSB(this.drawmat.facecolor.getRed(), this.drawmat.facecolor.getGreen(), this.drawmat.facecolor.getBlue(), new float[3]);
			drawcolorhsb[1] -= 0.01f; if (drawcolorhsb[1]<0.0f) {drawcolorhsb[1] = 0.0f;}
			Color hsbcolor = Color.getHSBColor(drawcolorhsb[0], drawcolorhsb[1], drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			Color newfacecolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.drawmat.transparency);
			this.drawmat = this.drawmat.copy();
			this.drawmat.facecolor = newfacecolor;
			this.drawmat.fileimage = null;
			this.drawmat.snapimage = null;
			this.entitybuffer = null;
			System.out.println("CADApp: keyPressed: key END: draw material color saturation negative: r="+colorvalues[0]+" g="+colorvalues[1]+" b="+colorvalues[2]+" tr="+this.drawmat.transparency);
		} else if (e.getKeyCode()==KeyEvent.VK_PAGE_UP) {
			float[] drawcolorhsb = Color.RGBtoHSB(this.drawmat.facecolor.getRed(), this.drawmat.facecolor.getGreen(), this.drawmat.facecolor.getBlue(), new float[3]);
			drawcolorhsb[2] += 0.01f; if (drawcolorhsb[2]>1.0f) {drawcolorhsb[2] = 1.0f;}
			Color hsbcolor = Color.getHSBColor(drawcolorhsb[0], drawcolorhsb[1], drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			Color newfacecolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.drawmat.transparency);
			this.drawmat = this.drawmat.copy();
			this.drawmat.facecolor = newfacecolor;
			this.drawmat.fileimage = null;
			this.drawmat.snapimage = null;
			this.entitybuffer = null;
			System.out.println("CADApp: keyPressed: key PAGEUP: draw material color brightness positive: r="+colorvalues[0]+" g="+colorvalues[1]+" b="+colorvalues[2]+" tr="+this.drawmat.transparency);
		} else if (e.getKeyCode()==KeyEvent.VK_PAGE_DOWN) {
			float[] drawcolorhsb = Color.RGBtoHSB(this.drawmat.facecolor.getRed(), this.drawmat.facecolor.getGreen(), this.drawmat.facecolor.getBlue(), new float[3]);
			drawcolorhsb[2] -= 0.01f; if (drawcolorhsb[2]<0.0f) {drawcolorhsb[2] = 0.0f;}
			Color hsbcolor = Color.getHSBColor(drawcolorhsb[0], drawcolorhsb[1], drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			Color newfacecolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.drawmat.transparency);
			this.drawmat = this.drawmat.copy();
			this.drawmat.facecolor = newfacecolor;
			this.drawmat.fileimage = null;
			this.drawmat.snapimage = null;
			this.entitybuffer = null;
			System.out.println("CADApp: keyPressed: key PAGEDOWN: draw material color brightness negative: r="+colorvalues[0]+" g="+colorvalues[1]+" b="+colorvalues[2]+" tr="+this.drawmat.transparency);
		} else if (e.getKeyCode()==KeyEvent.VK_MULTIPLY) {
			float newemissivity = this.drawmat.emissivity*1.1f; if (newemissivity<=0.0f) {newemissivity = 0.00001f;} if (newemissivity>1.0f) {newemissivity = 1.0f;}
			float[] drawcolorcomp = this.drawmat.facecolor.getRGBComponents(new float[4]);
			this.drawmat = this.drawmat.copy();
			this.drawmat.emissivity = newemissivity;
			float[] newemissivecolor = {drawcolorcomp[0]*newemissivity,drawcolorcomp[1]*newemissivity,drawcolorcomp[2]*newemissivity,1.0f};
			this.drawmat.emissivecolor = new Color(newemissivecolor[0],newemissivecolor[1],newemissivecolor[2],newemissivecolor[3]);
			if (this.drawmat.fileimage!=null) {
				this.drawmat.emissivefileimage = gc.createCompatibleVolatileImage(this.drawmat.fileimage.getWidth(), this.drawmat.fileimage.getHeight(), Transparency.TRANSLUCENT);
				Graphics2D emgfx = this.drawmat.emissivefileimage.createGraphics();
				emgfx.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, newemissivity));
				emgfx.drawImage(this.drawmat.fileimage, 0, 0, null);
				emgfx.dispose();
			}
			System.out.println("CADApp: keyPressed: key NUMPAD*: draw material emissivity positive: r="+drawcolorcomp[0]+" g="+drawcolorcomp[1]+" b="+drawcolorcomp[2]+" em="+this.drawmat.emissivity);
		} else if (e.getKeyCode()==KeyEvent.VK_DIVIDE) {
			float newemissivity = this.drawmat.emissivity/1.1f;
			float[] drawcolorcomp = this.drawmat.facecolor.getRGBComponents(new float[4]);
			this.drawmat = this.drawmat.copy();
			this.drawmat.emissivity = newemissivity;
			float[] newemissivecolor = {drawcolorcomp[0]*newemissivity,drawcolorcomp[1]*newemissivity,drawcolorcomp[2]*newemissivity,1.0f};
			this.drawmat.emissivecolor = new Color(newemissivecolor[0],newemissivecolor[1],newemissivecolor[2],newemissivecolor[3]);
			if (this.drawmat.fileimage!=null) {
				this.drawmat.emissivefileimage = gc.createCompatibleVolatileImage(this.drawmat.fileimage.getWidth(), this.drawmat.fileimage.getHeight(), Transparency.TRANSLUCENT);
				Graphics2D emgfx = this.drawmat.emissivefileimage.createGraphics();
				emgfx.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, newemissivity));
				emgfx.drawImage(this.drawmat.fileimage, 0, 0, null);
				emgfx.dispose();
			}
			System.out.println("CADApp: keyPressed: key NUMPAD/: draw material emissivity negative: r="+drawcolorcomp[0]+" g="+drawcolorcomp[1]+" b="+drawcolorcomp[2]+" em="+this.drawmat.emissivity);
		} else if (e.getKeyCode()==KeyEvent.VK_NUMPAD9) {
			float newtransparency = this.drawmat.transparency+0.01f; if (newtransparency>1.0f) {newtransparency = 1.0f;}
			float[] colorvalues = this.drawmat.facecolor.getRGBColorComponents(new float[3]);
			Color newfacecolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],newtransparency);
			this.drawmat = this.drawmat.copy();
			this.drawmat.facecolor = newfacecolor;
			this.drawmat.transparency = newtransparency;
			System.out.println("CADApp: keyPressed: key NUMPAD9: draw material transparency positive: r="+colorvalues[0]+" g="+colorvalues[1]+" b="+colorvalues[2]+" tr="+this.drawmat.transparency);
		} else if (e.getKeyCode()==KeyEvent.VK_NUMPAD8) {
			float newtransparency = this.drawmat.transparency-0.01f; if (newtransparency<0.0f) {newtransparency = 0.0f;}
			float[] colorvalues = this.drawmat.facecolor.getRGBColorComponents(new float[3]);
			Color newfacecolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],newtransparency);
			this.drawmat = this.drawmat.copy();
			this.drawmat.facecolor = newfacecolor;
			this.drawmat.transparency = newtransparency;
			System.out.println("CADApp: keyPressed: key NUMPAD8: draw material transparency negative: r="+colorvalues[0]+" g="+colorvalues[1]+" b="+colorvalues[2]+" tr="+this.drawmat.transparency);
		} else if (e.getKeyCode()==KeyEvent.VK_NUMPAD7) {
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
		} else if (e.getKeyCode()==KeyEvent.VK_NUMPAD6) {
			float newroughness = this.drawmat.roughness+0.01f; if (newroughness>1.0f) {newroughness = 1.0f;}
			float[] drawcolorcomp = this.drawmat.facecolor.getRGBComponents(new float[4]);
			this.drawmat = this.drawmat.copy();
			this.drawmat.roughness = newroughness;
			System.out.println("CADApp: keyPressed: key NUMPAD6: draw material roughness positive: r="+drawcolorcomp[0]+" g="+drawcolorcomp[1]+" b="+drawcolorcomp[2]+" rg="+this.drawmat.roughness);
		} else if (e.getKeyCode()==KeyEvent.VK_NUMPAD5) {
			float newroughness = this.drawmat.roughness-0.01f; if (newroughness<0.0f) {newroughness = 0.0f;}
			float[] drawcolorcomp = this.drawmat.facecolor.getRGBComponents(new float[4]);
			this.drawmat = this.drawmat.copy();
			this.drawmat.roughness = newroughness;
			System.out.println("CADApp: keyPressed: key NUMPAD5: draw material roughness negative: r="+drawcolorcomp[0]+" g="+drawcolorcomp[1]+" b="+drawcolorcomp[2]+" rg="+this.drawmat.roughness);
		} else if (e.getKeyCode()==KeyEvent.VK_NUMPAD4) {
			if ((this.mouseovertriangle!=null)&&(this.mouseovertriangle.length>0)) {
				Triangle[] stri = {this.mouseovertriangle[this.mouseovertriangle.length-1]};
				stri[0].norm = new Direction(0.0f,0.0f,0.0f);
				System.out.println("CADApp: keyPressed: key NUMPAD4: reset triangle normal="+stri[0].norm.dx+","+stri[0].norm.dy+" "+stri[0].norm.dz);
			}
		} else if (e.getKeyCode()==KeyEvent.VK_NUMPAD3) {
			float newmetallic = this.drawmat.metallic+0.01f; if (newmetallic>1.0f) {newmetallic = 1.0f;}
			float[] drawcolorcomp = this.drawmat.facecolor.getRGBComponents(new float[4]);
			this.drawmat = this.drawmat.copy();
			this.drawmat.metallic = newmetallic;
			System.out.println("CADApp: keyPressed: key NUMPAD3: draw material metallic positive: r="+drawcolorcomp[0]+" g="+drawcolorcomp[1]+" b="+drawcolorcomp[2]+" mt="+this.drawmat.metallic);
		} else if (e.getKeyCode()==KeyEvent.VK_NUMPAD2) {
			float newmetallic = this.drawmat.metallic-0.01f; if (newmetallic<0.0f) {newmetallic = 0.0f;}
			float[] drawcolorcomp = this.drawmat.facecolor.getRGBComponents(new float[4]);
			this.drawmat = this.drawmat.copy();
			this.drawmat.metallic = newmetallic;
			System.out.println("CADApp: keyPressed: key NUMPAD2: draw material metallic negative: r="+drawcolorcomp[0]+" g="+drawcolorcomp[1]+" b="+drawcolorcomp[2]+" mt="+this.drawmat.metallic);
		} else if (e.getKeyCode()==KeyEvent.VK_NUMPAD1) {
	    	Triangle mousetriangle = null;
    		if ((this.renderview!=null)&&(this.renderview.tbuffer!=null)) {
    			if ((this.mouselocationx>=0)&&(this.mouselocationx<this.getWidth())&&(this.mouselocationy>=0)&&(this.mouselocationy<this.getHeight())) {
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
		} else if (e.getKeyCode()==KeyEvent.VK_NUMPAD0) {
			(new EntityListUpdater()).start();
		} else if (e.getKeyCode()==KeyEvent.VK_ENTER) {
			if (e.isShiftDown()) {
				this.unlitrender = !this.unlitrender;
				System.out.println("CADApp: keyPressed: key SHIFT-ENTER: unlitrender="+this.unlitrender);
			} else if (e.isControlDown()) {
		    	(new EntityLightMapUpdater()).start();
			} else {
				this.polygonfillmode += 1;
				if (this.polygonfillmode>3) {
					this.polygonfillmode = 1;
				}
				System.out.println("CADApp: keyPressed: key ENTER: polygonfillmode="+this.polygonfillmode);
			}
		} else if (e.getKeyCode()==KeyEvent.VK_UP) {
			this.pitchupkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_DOWN) {
			this.pitchdownkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_LEFT) {
			this.yawleftkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_RIGHT) {
			this.yawrightkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_TAB) {
			this.texturemode = true;
		} else if (e.getKeyCode()==KeyEvent.VK_SHIFT) {
			this.snaplinemode = true;
		} else if (e.getKeyCode()==KeyEvent.VK_PERIOD) {
			this.erasemode = true;
		} else if (e.getKeyCode()==KeyEvent.VK_W) {
			this.upwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_S) {
			this.downwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_A) {
			this.leftkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_D) {
			this.rightkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_SPACE) {
			this.forwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_C) {
			this.backwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_Q) {
			this.rollleftkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_E) {
			this.rollrightkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_ADD) {
			this.forwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_SUBTRACT) {
			this.backwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_F2) {
			this.filechooser.setDialogTitle("Save File");
			this.filechooser.setApproveButtonText("Save");
			if (this.filechooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
			    int onmask = KeyEvent.SHIFT_DOWN_MASK;
			    int offmask = KeyEvent.ALT_DOWN_MASK|KeyEvent.CTRL_DOWN_MASK;
			    boolean f2shiftdown = (e.getModifiersEx() & (onmask | offmask)) == onmask;
				File savefile = this.filechooser.getSelectedFile();
				FileFilter savefileformat = this.filechooser.getFileFilter();
				UtilLib.saveModelFormat(savefile.getPath(), this.entitylist, savefileformat, f2shiftdown);
			}
		} else if (e.getKeyCode()==KeyEvent.VK_F3) {
		    int onmaskf3shiftdown = KeyEvent.SHIFT_DOWN_MASK;
		    int offmaskf3shiftdown = KeyEvent.CTRL_DOWN_MASK|KeyEvent.ALT_DOWN_MASK;
		    boolean f3shiftdown = (e.getModifiersEx() & (onmaskf3shiftdown | offmaskf3shiftdown)) == onmaskf3shiftdown;
		    int onmaskf3ctrldown = KeyEvent.CTRL_DOWN_MASK;
		    int offmaskf3ctrldown = KeyEvent.SHIFT_DOWN_MASK|KeyEvent.ALT_DOWN_MASK;
		    boolean f3ctrldown = (e.getModifiersEx() & (onmaskf3ctrldown | offmaskf3ctrldown)) == onmaskf3ctrldown;
		    if (f3shiftdown) {
		    	this.snaplinemode = false;
				this.imagechooser.setDialogTitle("Load File");
				this.imagechooser.setApproveButtonText("Load");
				if (this.imagechooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
					File loadfile = this.imagechooser.getSelectedFile();
					VolatileImage fileimage = UtilLib.loadImage(loadfile.getPath(), false);
					this.drawmat = this.drawmat.copy();
					this.drawmat.fileimage = fileimage;
					this.drawmat.snapimage = fileimage.getSnapshot();
				}
		    } else if (f3ctrldown) {
				this.filechooser.setDialogTitle("Load File");
				this.filechooser.setApproveButtonText("Load");
				if (this.filechooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
					File loadfile = this.filechooser.getSelectedFile();
					FileFilter loadfileformat = this.filechooser.getFileFilter();
					this.entitybuffer = UtilLib.loadModelFormat(loadfile.getPath(), loadfileformat, false);
				}
		    } else {
				this.filechooser.setDialogTitle("Load File");
				this.filechooser.setApproveButtonText("Load");
				if (this.filechooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
					File loadfile = this.filechooser.getSelectedFile();
					FileFilter loadfileformat = this.filechooser.getFileFilter();
					Entity loadentity = UtilLib.loadModelFormat(loadfile.getPath(), loadfileformat, false);
					this.entitylist = loadentity.childlist;
					this.linelisttree.addAll(Arrays.asList(loadentity.linelist));
				}
		    }
		} else if (e.getKeyCode()==KeyEvent.VK_F4) {
		    int onmaskf4down = 0;
		    int offmaskf4down = KeyEvent.SHIFT_DOWN_MASK|KeyEvent.ALT_DOWN_MASK|KeyEvent.CTRL_DOWN_MASK;
		    boolean f4down = (e.getModifiersEx() & (onmaskf4down | offmaskf4down)) == onmaskf4down;
		    int onmaskf4shiftdown = KeyEvent.SHIFT_DOWN_MASK;
		    int offmaskf4shiftdown = KeyEvent.ALT_DOWN_MASK|KeyEvent.CTRL_DOWN_MASK;
		    boolean f4shiftdown = (e.getModifiersEx() & (onmaskf4shiftdown | offmaskf4shiftdown)) == onmaskf4shiftdown;
		    int onmaskf4ctrldown = KeyEvent.CTRL_DOWN_MASK;
		    int offmaskf4ctrldown = KeyEvent.ALT_DOWN_MASK|KeyEvent.SHIFT_DOWN_MASK;
		    boolean f4ctrldown = (e.getModifiersEx() & (onmaskf4ctrldown | offmaskf4ctrldown)) == onmaskf4ctrldown;
			this.imagechooser.setDialogTitle("Render File");
			this.imagechooser.setApproveButtonText("Render");
			if (this.imagechooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
				File savefile = this.imagechooser.getSelectedFile();
				FileFilter savefileformat = this.imagechooser.getFileFilter();
				RenderView renderimageview = null;
				if (f4ctrldown) {
					renderimageview = RenderLib.renderCubemapView(this.campos[0], this.entitylist, this.rendercubemapoutputwidth, this.rendercubemapoutputheight, this.rendercubemapoutputsize, this.cameramat, this.unlitrender, 3, this.renderbounces, null, null, null, this.mouselocationx, this.mouselocationy);
				} else if (f4shiftdown) {
					renderimageview = RenderLib.renderSpheremapView(this.campos[0], this.entitylist, this.renderspheremapoutputwidth, this.renderspheremapoutputheight, this.cameramat, this.unlitrender, 2, this.renderbounces, null, null, null, this.mouselocationx, this.mouselocationy);
				} else {
					renderimageview = RenderLib.renderProjectedView(this.campos[0], this.entitylist, this.renderoutputwidth, this.hfov, this.renderoutputheight, this.vfov, this.cameramat, this.unlitrender, 3, this.renderbounces, null, null, null, this.mouselocationx, this.mouselocationy);
				}
				VolatileImage renderimage = renderimageview.renderimage;
				if (f4down) {
					VolatileImage blackbgimage = gc.createCompatibleVolatileImage(renderimage.getWidth(), renderimage.getHeight(), Transparency.TRANSLUCENT);
					Graphics2D bbggfx = blackbgimage.createGraphics();
					bbggfx.setComposite(AlphaComposite.Src);
					bbggfx.setColor(this.renderbackgroundcolor);
					bbggfx.fillRect(0, 0, renderimage.getWidth(), renderimage.getHeight());
					bbggfx.setComposite(AlphaComposite.SrcOver);
					bbggfx.drawImage(renderimage, 0, 0, null);
					bbggfx.dispose();
					renderimage = blackbgimage;
				}
				UtilLib.saveImageFormat(savefile.getPath(), renderimage, savefileformat);
			}
		}
	}
	
	@Override public void mouseMoved(MouseEvent e) {
		this.mouselocationx=e.getX();this.mouselocationy=e.getY();
	}
	@Override public void mousePressed(MouseEvent e) {
		this.mouselocationx=e.getX();this.mouselocationy=e.getY();
	    int onmask1ctrldown = MouseEvent.BUTTON1_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK;
	    int offmask1ctrldown = MouseEvent.ALT_DOWN_MASK;
	    boolean mouse1ctrldown = ((e.getModifiersEx() & (onmask1ctrldown | offmask1ctrldown)) == onmask1ctrldown);
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
	    int onmask1alt = MouseEvent.BUTTON1_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    int offmask1alt = 0;
	    boolean mouse1altdown = ((e.getModifiersEx() & (onmask1alt | offmask1alt)) == onmask1alt);
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
	    int onmask3 = MouseEvent.BUTTON3_DOWN_MASK;
	    int offmask3 = 0;
	    boolean mouse3down = ((e.getModifiersEx() & (onmask3 | offmask3)) == onmask3);
    	if (mouse3down) {
    		if (!this.erasemode) {
		    	if ((this.mouseoverentity!=null)&&(mouseoverentity.length>0)) {
	    			Entity[] mouseentity = {this.mouseoverentity[this.mouseoverentity.length-1]};
	    			this.drawstartpos = this.mousepos;
	    			this.selecteddragentity = mouseentity;
	    		}
    		}
    	}
		mouseDragged(e);
	}
	@Override public void mouseReleased(MouseEvent e) {
	    boolean mouse1up = e.getButton()==MouseEvent.BUTTON1;
	    boolean mouse3up = e.getButton()==MouseEvent.BUTTON3;
		if (mouse1up) {
			this.draglinemode = false;
		}
		if (mouse3up) {
			this.selecteddragentity = null;
		}
	}
	
	public void mouseDragged(MouseEvent e) {
		this.mouselastlocationx=this.mouselocationx;this.mouselastlocationy=this.mouselocationy;
		this.mouselocationx=e.getX();this.mouselocationy=e.getY();
	    int onmask1down = MouseEvent.BUTTON1_DOWN_MASK;
	    int offmask1down = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK|MouseEvent.SHIFT_DOWN_MASK;
	    boolean mouse1down = ((e.getModifiersEx() & (onmask1down | offmask1down)) == onmask1down);
    	if (mouse1down) {
    		if (this.erasemode) {
        		if ((this.mouseoverline!=null)&&(this.mouseoverline.length>0)) {
        			for (int i=0;i<this.mouseoverline.length;i++) {
        				System.out.println("CADApp: mouseDragged: key PERIOD-LMB: erase line="+this.mouseoverline[i].pos1.x+" "+this.mouseoverline[i].pos1.y+" "+this.mouseoverline[i].pos1.z+" "+this.mouseoverline[i].pos2.x+" "+this.mouseoverline[i].pos2.y+" "+this.mouseoverline[i].pos2.z);
        				this.linelisttree.remove(this.mouseoverline[i]);
        			}
    				(new EntityListUpdater()).start();
    			}
        		if ((this.mouseoverentity!=null)&&(mouseoverentity.length>0)&&(this.entitylist!=null)) {
	    			Entity[] mouseentity = {this.mouseoverentity[this.mouseoverentity.length-1]};
			    	Triangle mousetriangle = null;
		    		if ((this.renderview!=null)&&(this.renderview.tbuffer!=null)) {
		    			if ((this.mouselocationx>=0)&&(this.mouselocationx<this.getWidth())&&(this.mouselocationy>=0)&&(this.mouselocationy<this.getHeight())) {
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
					}
	    		}
    		} else if (this.entitybuffer!=null) {
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
    		} else {
		    	Triangle mousetriangle = null;
	    		if ((this.renderview!=null)&&(this.renderview.tbuffer!=null)) {
	    			if ((this.mouselocationx>=0)&&(this.mouselocationx<this.getWidth())&&(this.mouselocationy>=0)&&(this.mouselocationy<this.getHeight())) {
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
	    int onmask1shiftdown = MouseEvent.BUTTON1_DOWN_MASK|MouseEvent.SHIFT_DOWN_MASK;
	    int offmask1shiftdown = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    boolean mouse1shiftdown = ((e.getModifiersEx() & (onmask1shiftdown | offmask1shiftdown)) == onmask1shiftdown);
	    if (mouse1shiftdown) {
	    	Triangle mousetriangle = null;
    		if ((this.renderview!=null)&&(this.renderview.tbuffer!=null)) {
    			if ((this.mouselocationx>=0)&&(this.mouselocationx<this.getWidth())&&(this.mouselocationy>=0)&&(this.mouselocationy<this.getHeight())) {
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
	    int onmask1ctrldown = MouseEvent.BUTTON1_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK;
	    int offmask1ctrldown = 0;
	    boolean mouse1ctrldown = ((e.getModifiersEx() & (onmask1ctrldown | offmask1ctrldown)) == onmask1ctrldown);
	    int onmask1altdown = MouseEvent.BUTTON1_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    int offmask1altdown = 0;
	    boolean mouse1altdown = ((e.getModifiersEx() & (onmask1altdown | offmask1altdown)) == onmask1altdown);
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
	    int onmask2down = MouseEvent.BUTTON2_DOWN_MASK;
	    int offmask2down = MouseEvent.ALT_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK;
	    boolean mouse2down = ((e.getModifiersEx() & (onmask2down | offmask2down)) == onmask2down);
    	if (mouse2down) {
    		if (this.texturemode) {
		    	Triangle mousetriangle = null;
	    		if ((this.renderview!=null)&&(this.renderview.tbuffer!=null)) {
	    			if ((this.mouselocationx>=0)&&(this.mouselocationx<this.getWidth())&&(this.mouselocationy>=0)&&(this.mouselocationy<this.getHeight())) {
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
	    int onmask2ctrldown = MouseEvent.BUTTON2_DOWN_MASK;
	    int offmask2ctrldown = MouseEvent.CTRL_DOWN_MASK;
	    boolean mouse2ctrldown = ((e.getModifiersEx() & (onmask2ctrldown | offmask2ctrldown)) == onmask2ctrldown);
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
	    int onmask2ctrlaltdown = MouseEvent.BUTTON2_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK;
	    int offmask2ctrlaltdown = 0;
	    boolean mouse2ctrlaltdown = ((e.getModifiersEx() & (onmask2ctrlaltdown | offmask2ctrlaltdown)) == onmask2ctrlaltdown);
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
	    int onmask3down = MouseEvent.BUTTON3_DOWN_MASK;
	    int offmask3down = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK|MouseEvent.SHIFT_DOWN_MASK;
	    boolean mouse3down = ((e.getModifiersEx() & (onmask3down | offmask3down)) == onmask3down);
    	if (mouse3down) {
    		if ((this.erasemode)&&(this.entitylist!=null)) {
	    		if ((this.mouseoverentity!=null)&&(mouseoverentity.length>0)) {
	    			Entity[] mouseentity = {this.mouseoverentity[this.mouseoverentity.length-1]};
	    			Line[] mouseentitylinelist = MathLib.generateLineList(mouseentity);
	    			this.linelisttree.removeAll(Arrays.asList(mouseentitylinelist));
	    			ArrayList<Entity> entitylistarray = new ArrayList<Entity>(Arrays.asList(this.entitylist));
	    			entitylistarray.remove(mouseentity[0]);
	    			this.entitylist = entitylistarray.toArray(new Entity[entitylistarray.size()]);
	    		}
    		} else if (this.selecteddragentity!=null) {
    			Entity selectedentity = this.selecteddragentity[0];
    			Line[] selectedentitylinelist = MathLib.generateLineList(this.selecteddragentity);
    			this.linelisttree.removeAll(Arrays.asList(selectedentitylinelist));
        		Position[] drawposarray = {this.mousepos};
    			Direction[] drawmovedir = MathLib.vectorFromPoints(this.drawstartpos, drawposarray);
    			selectedentity.translateSelf(drawmovedir[0], 1.0f);
    			Line[] movedentitylinelist = MathLib.generateLineList(this.selecteddragentity);
    			this.linelisttree.addAll(Arrays.asList(movedentitylinelist));
    			this.drawstartpos = drawposarray[0];
    			System.out.println("CADApp: mouseDragged: key DRAG-RMB: move entity ="+selectedentity.sphereboundaryvolume.x+" "+selectedentity.sphereboundaryvolume.y+" "+selectedentity.sphereboundaryvolume.z);
    		}
    	}
	    int onmask3shiftdown = MouseEvent.BUTTON3_DOWN_MASK|MouseEvent.SHIFT_DOWN_MASK;
	    int offmask3shiftdown = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    boolean mouse3shiftdown = ((e.getModifiersEx() & (onmask3shiftdown | offmask3shiftdown)) == onmask3shiftdown);
	    if (mouse3shiftdown) {
	    	//TODO <tbd>
	    }
	    int onmask3altdown = MouseEvent.BUTTON3_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    int offmask3altdown = MouseEvent.CTRL_DOWN_MASK;
	    boolean mouse3altdown = ((e.getModifiersEx() & (onmask3altdown | offmask3altdown)) == onmask3altdown);
    	if (mouse3altdown) {
    		//TODO <tbd>
    	}
	    int onmask3ctrldown = MouseEvent.BUTTON3_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK;
	    int offmask3ctrldown = MouseEvent.ALT_DOWN_MASK;
	    boolean mouse3ctrldown = ((e.getModifiersEx() & (onmask3ctrldown | offmask3ctrldown)) == onmask3ctrldown);
    	if (mouse3ctrldown) {
    		//TODO <tbd>
    	}
	}
	@Override public void mouseWheelMoved(MouseWheelEvent e) {
	    int onmask = 0;
	    int offmask = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK|MouseEvent.SHIFT_DOWN_MASK;
	    boolean mousewheeldown = ((e.getModifiersEx() & (onmask | offmask)) == onmask);
	    if (mousewheeldown) {
	    	if (this.texturemode) {
		    	Triangle mousetriangle = null;
	    		if ((this.renderview!=null)&&(this.renderview.tbuffer!=null)) {
	    			if ((this.mouselocationx>=0)&&(this.mouselocationx<this.getWidth())&&(this.mouselocationy>=0)&&(this.mouselocationy<this.getHeight())) {
		    			mousetriangle = this.renderview.tbuffer[this.mouselocationy][this.mouselocationx];
	    			}
	    		} else if ((this.mouseovertriangle!=null)&&(this.mouseovertriangle.length>0)) {
	    			mousetriangle = this.mouseovertriangle[this.mouseovertriangle.length-1];
	    		}
				if (mousetriangle!=null) {
		        	mousetriangle.pos1.tex = mousetriangle.pos1.tex.copy();
		        	mousetriangle.pos2.tex = mousetriangle.pos2.tex.copy();
		        	mousetriangle.pos3.tex = mousetriangle.pos3.tex.copy();
		    		mousetriangle.pos1.tex.u *= 1-(0.01f*e.getWheelRotation());
		    		mousetriangle.pos1.tex.v *= 1-(0.01f*e.getWheelRotation());
		    		mousetriangle.pos2.tex.u *= 1-(0.01f*e.getWheelRotation());
		    		mousetriangle.pos2.tex.v *= 1-(0.01f*e.getWheelRotation());
		    		mousetriangle.pos3.tex.u *= 1-(0.01f*e.getWheelRotation());
		    		mousetriangle.pos3.tex.v *= 1-(0.01f*e.getWheelRotation());
					System.out.println("CADApp: mouseWheelMoved: key TAB-MWHEEL: zoom texture coordinate="+mousetriangle.pos1.tex.u+","+mousetriangle.pos1.tex.v+" "+mousetriangle.pos2.tex.u+","+mousetriangle.pos2.tex.v+" "+mousetriangle.pos3.tex.u+","+mousetriangle.pos3.tex.v);
				}
	    	}
	    }
	    int onmaskshiftdown = MouseEvent.SHIFT_DOWN_MASK;
	    int offmaskshiftdown = MouseEvent.ALT_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK;
	    boolean mousewheelshiftdown = ((e.getModifiersEx() & (onmaskshiftdown | offmaskshiftdown)) == onmaskshiftdown);
	    if (mousewheelshiftdown) {
	    	if (this.texturemode) {
		    	Triangle mousetriangle = null;
	    		if ((this.renderview!=null)&&(this.renderview.tbuffer!=null)) {
	    			if ((this.mouselocationx>=0)&&(this.mouselocationx<this.getWidth())&&(this.mouselocationy>=0)&&(this.mouselocationy<this.getHeight())) {
		    			mousetriangle = this.renderview.tbuffer[this.mouselocationy][this.mouselocationx];
	    			}
	    		} else if ((this.mouseovertriangle!=null)&&(this.mouseovertriangle.length>0)) {
	    			mousetriangle = this.mouseovertriangle[this.mouseovertriangle.length-1];
	    		}
				if (mousetriangle!=null) {
		        	AffineTransform textr = new AffineTransform();
		        	textr.rotate(0.01f*e.getWheelRotation());
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
	    int onmaskctrlshiftdown = MouseEvent.CTRL_DOWN_MASK|MouseEvent.SHIFT_DOWN_MASK;
	    int offmaskctrlshiftdown = MouseEvent.ALT_DOWN_MASK;
	    boolean mousewheelctrlshiftdown = ((e.getModifiersEx() & (onmaskctrlshiftdown | offmaskctrlshiftdown)) == onmaskctrlshiftdown);
	    if (mousewheelctrlshiftdown) {
	    	if (this.texturemode) {
		    	Triangle mousetriangle = null;
	    		if ((this.renderview!=null)&&(this.renderview.tbuffer!=null)) {
	    			if ((this.mouselocationx>=0)&&(this.mouselocationx<this.getWidth())&&(this.mouselocationy>=0)&&(this.mouselocationy<this.getHeight())) {
		    			mousetriangle = this.renderview.tbuffer[this.mouselocationy][this.mouselocationx];
	    			}
	    		} else if ((this.mouseovertriangle!=null)&&(this.mouseovertriangle.length>0)) {
	    			mousetriangle = this.mouseovertriangle[this.mouseovertriangle.length-1];
	    		}
				if (mousetriangle!=null) {
		        	AffineTransform textr = new AffineTransform();
		        	textr.scale(1+0.01f*e.getWheelRotation(),1.0f);
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
	    int onmaskaltshiftdown = MouseEvent.CTRL_DOWN_MASK;
	    int offmaskaltshiftdown = MouseEvent.SHIFT_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    boolean mousewheelaltshiftdown = ((e.getModifiersEx() & (onmaskaltshiftdown | offmaskaltshiftdown)) == onmaskaltshiftdown);
	    if (mousewheelaltshiftdown) {
	    	if (this.texturemode) {
		    	Triangle mousetriangle = null;
	    		if ((this.renderview!=null)&&(this.renderview.tbuffer!=null)) {
	    			if ((this.mouselocationx>=0)&&(this.mouselocationx<this.getWidth())&&(this.mouselocationy>=0)&&(this.mouselocationy<this.getHeight())) {
		    			mousetriangle = this.renderview.tbuffer[this.mouselocationy][this.mouselocationx];
	    			}
	    		} else if ((this.mouseovertriangle!=null)&&(this.mouseovertriangle.length>0)) {
	    			mousetriangle = this.mouseovertriangle[this.mouseovertriangle.length-1];
	    		}
				if (mousetriangle!=null) {
		        	AffineTransform textr = new AffineTransform();
		        	textr.shear(0.01f*e.getWheelRotation(),0.0f);
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
	    int onmaskctrldown = 0;
	    int offmaskctrldown = 0;
	    boolean mousewheelctrldown = ((e.getModifiersEx() & (onmaskctrldown | offmaskctrldown)) == onmaskctrldown);
	    if (mousewheelctrldown) {
	    	if (!this.texturemode) {
				double movementstep = 200.0f*e.getWheelRotation();
				if (this.snaplinemode) {
					movementstep *= this.gridstep;
				}
				this.campos = MathLib.translate(campos, this.camdirs[0], -movementstep);
				System.out.println("CADApp: mouseWheelMoved: key MWHEEL: camera position="+this.campos[0].x+","+this.campos[0].y+","+this.campos[0].z);
	    	}
	    }
	}
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	
	@SuppressWarnings("unchecked")
	@Override public void drop(DropTargetDropEvent dtde) {
		dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
		Transferable dtt = dtde.getTransferable();
        try {
            if ((dtt!=null)&&(dtt.isDataFlavorSupported(DataFlavor.javaFileListFlavor))) {
				List<File> files = (List<File>)dtt.getTransferData(DataFlavor.javaFileListFlavor);
                for (Iterator<File> i=files.iterator();i.hasNext();) {
                	File file = i.next();
                	String filepath = file.getPath();
                	String filename = file.getName();
                	if (UtilLib.isImageFilename(filename)) {
	                    VolatileImage fileimage = UtilLib.loadImage(filepath, false);
						this.drawmat = this.drawmat.copy();
						this.drawmat.fileimage = fileimage;
						this.drawmat.snapimage = fileimage.getSnapshot();
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
            }
        } catch (Exception ex){ex.printStackTrace();}
	}

	private class EntityListUpdater extends Thread {
		private static boolean entitylistupdaterrunning = false;
		public void run() {
			if (!EntityListUpdater.entitylistupdaterrunning) {
				EntityListUpdater.entitylistupdaterrunning = true;
				ArrayList<Triangle> entitylisttrianglearray = new ArrayList<Triangle>();
				if (CADApp.this.entitylist!=null) {
					for (int i=0;i<CADApp.this.entitylist.length;i++) {
						if (CADApp.this.entitylist[i].trianglelist!=null) {
							entitylisttrianglearray.addAll(Arrays.asList(CADApp.this.entitylist[i].trianglelist));
						}
					}
				}
				Line[] copylinelist = CADApp.this.linelisttree.toArray(new Line[CADApp.this.linelisttree.size()]);
				Entity[] newentitylist = MathLib.generateEntityList(copylinelist);
				Material newmat = CADApp.this.drawmat;
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
				CADApp.this.entitylist = newentitylist;
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
				if (CADApp.this.polygonfillmode==1) {
					Line[] linelist = CADApp.this.linelisttree.toArray(new Line[CADApp.this.linelisttree.size()]);
					RenderView drawrenderview = RenderLib.renderProjectedLineViewHardware(CADApp.this.campos[0], linelist, CADApp.this.getWidth(), CADApp.this.hfov, CADApp.this.getHeight(), CADApp.this.vfov, true, CADApp.this.cameramat, CADApp.this.mouselocationx, CADApp.this.mouselocationy);
					if (CADApp.this.entitybuffer!=null) {
						Line[] copyentitybufferlinelist = new Line[CADApp.this.entitybuffer.linelist.length];
						for (int i=0;i<CADApp.this.entitybuffer.linelist.length;i++) {
							copyentitybufferlinelist[i] = CADApp.this.entitybuffer.linelist[i].translate(CADApp.this.mousepos);
						}
						RenderView entitybufferview = RenderLib.renderProjectedLineViewHardware(CADApp.this.campos[0], copyentitybufferlinelist, CADApp.this.getWidth(), CADApp.this.hfov, CADApp.this.getHeight(), CADApp.this.vfov, false, CADApp.this.cameramat, CADApp.this.mouselocationx, CADApp.this.mouselocationy);
						Graphics2D viewgfx = drawrenderview.renderimage.createGraphics();
						viewgfx.setComposite(AlphaComposite.SrcOver);
						viewgfx.drawImage(entitybufferview.renderimage, 0, 0, null);
						viewgfx.dispose();
					}
					CADApp.this.renderview = drawrenderview;
					CADApp.this.mouseoverline = drawrenderview.mouseoverline;
					CADApp.this.mouseoververtex = drawrenderview.mouseoververtex;
					CADApp.this.mouseoverentity = null;
					CADApp.this.mouseovertriangle = null;
				} else if (CADApp.this.polygonfillmode==2) { 
					RenderView drawrenderview = RenderLib.renderProjectedView(CADApp.this.campos[0], CADApp.this.entitylist, CADApp.this.getWidth(), CADApp.this.hfov, CADApp.this.getHeight(), CADApp.this.vfov, CADApp.this.cameramat, CADApp.this.unlitrender, 1, bounces, null, null, null, CADApp.this.mouselocationx, CADApp.this.mouselocationy);
					if (CADApp.this.entitybuffer!=null) {
						Entity[] copyentitybuffer = new Entity[CADApp.this.entitybuffer.childlist.length];
						for (int i=0;i<CADApp.this.entitybuffer.childlist.length;i++) {
		        			copyentitybuffer[i] = CADApp.this.entitybuffer.childlist[i].translate(CADApp.this.mousepos);
		        		}
						RenderView entitybufferview = RenderLib.renderProjectedView(CADApp.this.campos[0], copyentitybuffer, CADApp.this.getWidth(), CADApp.this.hfov, CADApp.this.getHeight(), CADApp.this.vfov, CADApp.this.cameramat, CADApp.this.unlitrender, 1, bounces, null, null, null, CADApp.this.mouselocationx, CADApp.this.mouselocationy);
						Graphics2D viewgfx = drawrenderview.renderimage.createGraphics();
						viewgfx.setComposite(AlphaComposite.SrcOver);
						viewgfx.drawImage(entitybufferview.renderimage, 0, 0, null);
						viewgfx.dispose();
					}
					CADApp.this.renderview = drawrenderview;
					CADApp.this.mouseoverline = null;
					CADApp.this.mouseoververtex = null;
					CADApp.this.mouseoverentity = drawrenderview.mouseoverentity;
					CADApp.this.mouseovertriangle = drawrenderview.mouseovertriangle;
				} else if (CADApp.this.polygonfillmode==3) {
					RenderView drawrenderview = RenderLib.renderProjectedView(CADApp.this.campos[0], CADApp.this.entitylist, CADApp.this.getWidth(), CADApp.this.hfov, CADApp.this.getHeight(), CADApp.this.vfov, CADApp.this.cameramat, CADApp.this.unlitrender, 2, bounces, null, null, null, CADApp.this.mouselocationx, CADApp.this.mouselocationy);
					if (CADApp.this.entitybuffer!=null) {
						Entity[] copyentitybuffer = new Entity[CADApp.this.entitybuffer.childlist.length];
						for (int i=0;i<CADApp.this.entitybuffer.childlist.length;i++) {
		        			copyentitybuffer[i] = CADApp.this.entitybuffer.childlist[i].translate(CADApp.this.mousepos);
		        		}
						RenderView entitybufferview = RenderLib.renderProjectedView(CADApp.this.campos[0], copyentitybuffer, CADApp.this.getWidth(), CADApp.this.hfov, CADApp.this.getHeight(), CADApp.this.vfov, CADApp.this.cameramat, CADApp.this.unlitrender, 2, bounces, null, null, null, CADApp.this.mouselocationx, CADApp.this.mouselocationy);
						Graphics2D viewgfx = drawrenderview.renderimage.createGraphics();
						viewgfx.setComposite(AlphaComposite.SrcOver);
						viewgfx.drawImage(entitybufferview.renderimage, 0, 0, null);
						viewgfx.dispose();
					}
					CADApp.this.renderview = drawrenderview;
					CADApp.this.mouseoverline = null;
					CADApp.this.mouseoververtex = null;
					CADApp.this.mouseoverentity = drawrenderview.mouseoverentity;
					CADApp.this.mouseovertriangle = drawrenderview.mouseovertriangle;
				}
				RenderViewUpdater.renderupdaterrunning = false;
			}
		}
	}

	private class EntityLightMapUpdater extends Thread {
		private static boolean entitylightmapupdaterrunning = false;
		public void run() {
			if (!EntityLightMapUpdater.entitylightmapupdaterrunning) {
				EntityLightMapUpdater.entitylightmapupdaterrunning = true;
				int bounces = 2;
				RenderLib.renderSurfaceFaceLightmapCubemapView(CADApp.this.entitylist, 32, bounces, 3);
				EntityLightMapUpdater.entitylightmapupdaterrunning = false;
			}
		}
	}
	
}
