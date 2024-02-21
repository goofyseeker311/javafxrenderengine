package fi.jkauppa.javarenderengine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Transparency;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import fi.jkauppa.javarenderengine.JavaRenderEngine.AppHandlerPanel;
import fi.jkauppa.javarenderengine.ModelLib.Direction;
import fi.jkauppa.javarenderengine.ModelLib.Entity;
import fi.jkauppa.javarenderengine.ModelLib.Matrix;
import fi.jkauppa.javarenderengine.ModelLib.Position;
import fi.jkauppa.javarenderengine.ModelLib.RenderView;
import fi.jkauppa.javarenderengine.ModelLib.Rotation;

public class ModelApp extends AppHandlerPanel {
	private static final long serialVersionUID = 1L;
	private Entity[] entitylist = null;
	private final Position[] defaultcampos = {new Position(0.0f,0.0f,0.0f)};
	private final Rotation defaultcamrot = new Rotation(0.0f, 0.0f, 0.0f);
	private Position[] campos = this.defaultcampos;
	private Rotation camrot = this.defaultcamrot;
	private Matrix cameramat = MathLib.rotationMatrix(0.0f, 0.0f, 0.0f);
	private final Direction[] lookdirs = MathLib.projectedCameraDirections(cameramat);
	private Direction[] camdirs = lookdirs;
	private double hfov = 70.0f;
	private double vfov = 43.0f;
	private int polygonfillmode = 1;
	private boolean unlitrender = false;
	private JFileChooser filechooser = UtilLib.createModelFileChooser();
	private boolean leftkeydown = false;
	private boolean rightkeydown = false;
	private boolean upwardkeydown = false;
	private boolean downwardkeydown = false;
	private boolean forwardkeydown = false;
	private boolean backwardkeydown = false;
	private boolean rollrightkeydown = false;
	private boolean rollleftkeydown = false;
	private int mouselastlocationx = -1, mouselastlocationy = -1; 
	private int mouselocationx = -1, mouselocationy = -1;
	private BufferedImage cursorimage = gc.createCompatibleImage(1, 1, Transparency.TRANSLUCENT);
	private Cursor customcursor = null;
	private RenderView renderview = null;
	
	public ModelApp() {
		cursorimage.setRGB(0, 0, (new Color(0.0f,0.0f,0.0f,0.0f)).getRGB());
		this.customcursor = tk.createCustomCursor(cursorimage, new Point(0,0), "customcursor");
		this.setCursor(this.customcursor);
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
			g2.drawImage(renderview.renderimage, 0, 0, null);
		}
	}

	@Override public void timerTick() {
		double movementstep = 20.0f;
		if (this.leftkeydown) {
			this.campos = MathLib.translate(campos, this.camdirs[1], -movementstep);
		} else if (this.rightkeydown) {
			this.campos = MathLib.translate(campos, this.camdirs[1], movementstep);
		}
		if (this.forwardkeydown) {
			this.campos = MathLib.translate(campos, this.camdirs[0], movementstep);
		} else if (this.backwardkeydown) {
			this.campos = MathLib.translate(campos, this.camdirs[0], -movementstep);
		}
		if (this.upwardkeydown) {
			this.campos = MathLib.translate(campos, this.camdirs[2], -movementstep);
		} else if (this.downwardkeydown) {
			this.campos = MathLib.translate(campos, this.camdirs[2], movementstep);
		}
		if (this.rollleftkeydown) {
			this.camrot = this.camrot.copy();
			this.camrot.y -= 1.0f;
		} else if (this.rollrightkeydown) {
			this.camrot = this.camrot.copy();
			this.camrot.y += 1.0f;
		}
		updateCameraDirections();
		(new RenderViewUpdater()).start();
	}

	private void updateCameraDirections() {
		Matrix camrotmat = MathLib.rotationMatrixLookHorizontalRoll(this.camrot);
		Direction[] camlookdirs = MathLib.matrixMultiply(this.lookdirs, camrotmat);
		this.cameramat = camrotmat;
		this.camdirs = camlookdirs;
	}
	
	@Override public void keyReleased(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_A) {
		this.leftkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_D) {
			this.rightkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_W) {
			this.forwardkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_S) {
			this.backwardkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_SPACE) {
			this.upwardkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_C) {
			this.downwardkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_Q) {
			this.rollleftkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_E) {
			this.rollrightkeydown = false;
		}
	}
	
	@Override public void keyPressed(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_BACK_SPACE) {
			this.entitylist = null;
			this.campos = this.defaultcampos;
			this.camrot = this.defaultcamrot;
			updateCameraDirections();
		} else if (e.getKeyCode()==KeyEvent.VK_A) {
			this.leftkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_D) {
			this.rightkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_SPACE) {
			this.upwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_C) {
			this.downwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_W) {
			this.forwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_S) {
			this.backwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_Q) {
			this.rollleftkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_E) {
			this.rollrightkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_ENTER) {
			if (e.isShiftDown()) {
				this.unlitrender = !this.unlitrender;
				System.out.println("ModelApp: keyPressed: key SHIFT-ENTER: unlitrender="+this.unlitrender);
			} else {
				this.polygonfillmode += 1;
				if (this.polygonfillmode>8) {
					this.polygonfillmode = 1;
				}
				System.out.println("ModelApp: keyPressed: key ENTER: polygonfillmode="+this.polygonfillmode);
			}
		} else if (e.getKeyCode()==KeyEvent.VK_F3) {
			this.filechooser.setDialogTitle("Load File");
			this.filechooser.setApproveButtonText("Load");
			if (this.filechooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
				File loadfile = this.filechooser.getSelectedFile();
				FileFilter loadfileformat = this.filechooser.getFileFilter();
				Entity loadentity = UtilLib.loadModelFormat(loadfile.getPath(), loadfileformat, false);
				this.entitylist = loadentity.childlist;
			}
		}
	}
	
	@Override public void keyTyped(KeyEvent e) {}
	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseDragged(MouseEvent e) {}
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {
		if (this.isFocusOwner()) {
			Point windowscreenlocation = this.getLocationOnScreen();
			int origindeltax = (int)Math.floor(((double)(this.getWidth()-1))/2.0f);
			int origindeltay = (int)Math.floor(((double)(this.getHeight()-1))/2.0f);
			int windowcenterx = windowscreenlocation.x + origindeltax;
			int windowcentery = windowscreenlocation.y + origindeltay;
			this.mouselocationx = this.lastrenderwidth/2; 
			this.mouselocationy = this.lastrenderheight/2; 
			try {
				Robot mouserobot = new Robot();
				mouserobot.mouseMove(windowcenterx, windowcentery);
			} catch (Exception ex) {ex.printStackTrace();}
		}
	}
	@Override public void mouseMoved(MouseEvent e) {
		if (this.isFocusOwner()) {
			this.mouselastlocationx=this.mouselocationx;this.mouselastlocationy=this.mouselocationy;
			this.mouselocationx=e.getX();this.mouselocationy=e.getY();
	    	int mousedeltax = this.mouselocationx - this.mouselastlocationx; 
	    	int mousedeltay = this.mouselocationy - this.mouselastlocationy;
			this.camrot = this.camrot.copy();
	    	this.camrot.z -= mousedeltax*0.1f;
	    	this.camrot.x -= mousedeltay*0.1f;
	    	updateCameraDirections();
			if ((this.mouselocationx<=0)||(this.mouselocationy<=0)||(this.mouselocationx>=(this.lastrenderwidth-1))||(this.mouselocationy>=(this.lastrenderheight-1))) {
				mouseExited(e);
			}
		}
	}
	@Override public void mouseWheelMoved(MouseWheelEvent e) {}
	@Override public void drop(DropTargetDropEvent dtde) {}

	private class RenderViewUpdater extends Thread {
		private static boolean renderupdaterrunning = false;
		public void run() {
			if (!RenderViewUpdater.renderupdaterrunning) {
				RenderViewUpdater.renderupdaterrunning = true;
				int bounces = 0;
				if (ModelApp.this.polygonfillmode==1) {
					ModelApp.this.renderview = RenderLib.renderProjectedView(ModelApp.this.campos[0], ModelApp.this.entitylist, ModelApp.this.getWidth(), ModelApp.this.hfov, ModelApp.this.getHeight(), ModelApp.this.vfov, ModelApp.this.cameramat, ModelApp.this.unlitrender, 1, bounces, null, null, null, ModelApp.this.mouselocationx, ModelApp.this.mouselocationy);
				} else if (ModelApp.this.polygonfillmode==2) {
					ModelApp.this.renderview = RenderLib.renderCubemapView(ModelApp.this.campos[0], ModelApp.this.entitylist, ModelApp.this.getWidth(), ModelApp.this.getHeight(), (int)Math.floor(((double)ModelApp.this.getHeight())/2.0f), ModelApp.this.cameramat, ModelApp.this.unlitrender, 1, bounces, null, null, null, ModelApp.this.mouselocationx, ModelApp.this.mouselocationy);
				} else if (ModelApp.this.polygonfillmode==3) {
					ModelApp.this.renderview = RenderLib.renderProjectedView(ModelApp.this.campos[0], ModelApp.this.entitylist, ModelApp.this.getWidth(), ModelApp.this.hfov, ModelApp.this.getHeight(), ModelApp.this.vfov, ModelApp.this.cameramat, ModelApp.this.unlitrender, 2, bounces, null, null, null, ModelApp.this.mouselocationx, ModelApp.this.mouselocationy);
				} else if (ModelApp.this.polygonfillmode==4) {
					ModelApp.this.renderview = RenderLib.renderSpheremapView(ModelApp.this.campos[0], ModelApp.this.entitylist, ModelApp.this.getWidth(), ModelApp.this.getHeight(), ModelApp.this.cameramat, ModelApp.this.unlitrender, 1, bounces, null, null, null, ModelApp.this.mouselocationx, ModelApp.this.mouselocationy);
				} else if (ModelApp.this.polygonfillmode==5) {
					ModelApp.this.renderview = RenderLib.renderCubemapView(ModelApp.this.campos[0], ModelApp.this.entitylist, ModelApp.this.getWidth(), ModelApp.this.getHeight(), (int)Math.floor(((double)ModelApp.this.getHeight())/2.0f), ModelApp.this.cameramat, ModelApp.this.unlitrender, 2, bounces, null, null, null, ModelApp.this.mouselocationx, ModelApp.this.mouselocationy);
				} else if (ModelApp.this.polygonfillmode==6) {
					ModelApp.this.renderview = RenderLib.renderProjectedView(ModelApp.this.campos[0], ModelApp.this.entitylist, ModelApp.this.getWidth(), ModelApp.this.hfov, ModelApp.this.getHeight(), ModelApp.this.vfov, ModelApp.this.cameramat, ModelApp.this.unlitrender, 3, bounces, null, null, null, ModelApp.this.mouselocationx, ModelApp.this.mouselocationy);
				} else if (ModelApp.this.polygonfillmode==7) {
					ModelApp.this.renderview = RenderLib.renderSpheremapView(ModelApp.this.campos[0], ModelApp.this.entitylist, ModelApp.this.getWidth(), ModelApp.this.getHeight(), ModelApp.this.cameramat, ModelApp.this.unlitrender, 2, bounces, null, null, null, ModelApp.this.mouselocationx, ModelApp.this.mouselocationy);
				} else if (ModelApp.this.polygonfillmode==8) {
					ModelApp.this.renderview = RenderLib.renderCubemapView(ModelApp.this.campos[0], ModelApp.this.entitylist, ModelApp.this.getWidth(), ModelApp.this.getHeight(), (int)Math.floor(((double)ModelApp.this.getHeight())/2.0f), ModelApp.this.cameramat, ModelApp.this.unlitrender, 3, bounces, null, null, null, ModelApp.this.mouselocationx, ModelApp.this.mouselocationy);
				}
				RenderViewUpdater.renderupdaterrunning = false;
			}
		}
	}
	
}