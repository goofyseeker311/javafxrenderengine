package fi.jkauppa.javafxrenderengine;

import java.io.File;

import fi.jkauppa.javafxrenderengine.JavaFXRenderEngine.AppFXHandler;
import fi.jkauppa.javarenderengine.MathLib;
import fi.jkauppa.javarenderengine.RenderLib;
import fi.jkauppa.javarenderengine.UtilLib;
import fi.jkauppa.javarenderengine.UtilLib.ModelFileFilters.OBJFileFilter;
import fi.jkauppa.javarenderengine.UtilLib.ModelFileFilters.STLFileFilter;
import fi.jkauppa.javarenderengine.ModelLib.Direction;
import fi.jkauppa.javarenderengine.ModelLib.Entity;
import fi.jkauppa.javarenderengine.ModelLib.Matrix;
import fi.jkauppa.javarenderengine.ModelLib.Position;
import fi.jkauppa.javarenderengine.ModelLib.RenderView;
import fi.jkauppa.javarenderengine.ModelLib.Rotation;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.ParallelCamera;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.robot.Robot;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ModelFXApp extends AppFXHandler {
	private ParallelCamera camera = new ParallelCamera();
	private Scene scene = null;
	private Entity[] entitylist = null;
	private final Position[] defaultcampos = {new Position(0.0f,0.0f,0.0f)};
	private final Rotation defaultcamrot = new Rotation(0.0f, 0.0f, 0.0f);
	private Position[] campos = this.defaultcampos;
	private Rotation camrot = this.defaultcamrot;
	private Matrix cameramat = MathLib.rotationMatrix(0.0f, 0.0f, 0.0f);
	private final Direction[] lookdirs = MathLib.projectedCameraDirections(cameramat);
	private Direction[] camdirs = lookdirs;
	private int renderwidth = 0;
	private int renderheight = 0;
	private double hfov = 70.0f;
	private double vfov = 43.0f;
	private int polygonfillmode = 1;
	private boolean unlitrender = false;
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
	private RenderView renderview = null;
	
	public ModelFXApp() {}
	
	@Override public void update(Group root) {
		this.scene = root.getScene();
		this.renderwidth = (int)this.scene.getWidth();
		this.renderheight = (int)this.scene.getHeight();
		this.scene.getWidth();
		this.scene.setCursor(Cursor.NONE);
		this.scene.setCamera(camera);
		this.scene.setFill(Paint.valueOf("BLACK"));
		root.getChildren().clear();
		this.vfov = MathLib.calculateVfov(this.renderwidth, this.renderheight, this.hfov);
		if ((renderview!=null)&&(renderview.renderimage!=null)) {
			WritableImage renderimage = SwingFXUtils.toFXImage(renderview.renderimage, null);
	        ImageView renderimageview = new ImageView();
	        renderimageview.setImage(renderimage);
	        renderimageview.setFitWidth(this.renderwidth);
	        renderimageview.setPreserveRatio(true);
	        renderimageview.setSmooth(true);
	        renderimageview.setCache(true);
			root.getChildren().add(renderimageview);
		}
	}

	@Override public void pulse() {
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
	
	@Override public void handle(Event event) {
		if (event.getEventType().equals(KeyEvent.KEY_RELEASED)) {
			KeyEvent keyevent = (KeyEvent)event;
			if (keyevent.getCode()==KeyCode.A) {
				this.leftkeydown = false;
				} else if (keyevent.getCode()==KeyCode.D) {
					this.rightkeydown = false;
				} else if (keyevent.getCode()==KeyCode.W) {
					this.forwardkeydown = false;
				} else if (keyevent.getCode()==KeyCode.S) {
					this.backwardkeydown = false;
				} else if (keyevent.getCode()==KeyCode.SPACE) {
					this.upwardkeydown = false;
				} else if (keyevent.getCode()==KeyCode.C) {
					this.downwardkeydown = false;
				} else if (keyevent.getCode()==KeyCode.Q) {
					this.rollleftkeydown = false;
				} else if (keyevent.getCode()==KeyCode.E) {
					this.rollrightkeydown = false;
				}
		} else if (event.getEventType().equals(KeyEvent.KEY_PRESSED)) {
			KeyEvent keyevent = (KeyEvent)event;
			if (keyevent.getCode()==KeyCode.BACK_SPACE) {
				this.entitylist = null;
				this.campos = this.defaultcampos;
				this.camrot = this.defaultcamrot;
				updateCameraDirections();
			} else if (keyevent.getCode()==KeyCode.A) {
				this.leftkeydown = true;
			} else if (keyevent.getCode()==KeyCode.D) {
				this.rightkeydown = true;
			} else if (keyevent.getCode()==KeyCode.SPACE) {
				this.upwardkeydown = true;
			} else if (keyevent.getCode()==KeyCode.C) {
				this.downwardkeydown = true;
			} else if (keyevent.getCode()==KeyCode.W) {
				this.forwardkeydown = true;
			} else if (keyevent.getCode()==KeyCode.S) {
				this.backwardkeydown = true;
			} else if (keyevent.getCode()==KeyCode.Q) {
				this.rollleftkeydown = true;
			} else if (keyevent.getCode()==KeyCode.E) {
				this.rollrightkeydown = true;
			} else if (keyevent.getCode()==KeyCode.ENTER) {
				if ((!keyevent.isControlDown())&&(!keyevent.isAltDown())&&(!keyevent.isMetaDown())) {
					if (keyevent.isShiftDown()) {
						this.unlitrender = !this.unlitrender;
						System.out.println("ModelApp: keyPressed: key SHIFT-ENTER: unlitrender="+this.unlitrender);
					} else {
						this.polygonfillmode += 1;
						if (this.polygonfillmode>8) {
							this.polygonfillmode = 1;
						}
						System.out.println("ModelApp: keyPressed: key ENTER: polygonfillmode="+this.polygonfillmode);
					}
				}
			} else if (keyevent.getCode()==KeyCode.F3) {
	        	FileChooser filechooser = new FileChooser();
		    	filechooser.setTitle("Open Resource File");
		    	ExtensionFilter objextensionfilter = new ExtensionFilter("OBJ Model file", "*.obj");
		    	ExtensionFilter stlextensionfilter = new ExtensionFilter("STL Model file", "*.stl");
	        	filechooser.getExtensionFilters().add(objextensionfilter);
	        	filechooser.getExtensionFilters().add(stlextensionfilter);
	        	filechooser.setSelectedExtensionFilter(objextensionfilter);
		    	File loadfile = filechooser.showOpenDialog(this.scene.getWindow());
		    	if (loadfile!=null) {
		    		ExtensionFilter loadfileextension = filechooser.getSelectedExtensionFilter();
		    		if (loadfileextension.equals(objextensionfilter)) {
		    			Entity loadentity = UtilLib.loadModelFormat(loadfile.getPath(), new OBJFileFilter(), false);
						this.entitylist = loadentity.childlist;
		    		} else if (loadfileextension.equals(stlextensionfilter)) {
		    			Entity loadentity = UtilLib.loadModelFormat(loadfile.getPath(), new STLFileFilter(), false);
						this.entitylist = loadentity.childlist;
		    		}
		    	}
			}
		} else if (event.getEventType().equals(MouseEvent.MOUSE_MOVED)) {
			if (this.scene.getWindow().isFocused()) {
				MouseEvent mouseevent = (MouseEvent)event;
				this.mouselastlocationx=this.mouselocationx;this.mouselastlocationy=this.mouselocationy;
				this.mouselocationx=(int)mouseevent.getX();this.mouselocationy=(int)mouseevent.getY();
		    	int mousedeltax = this.mouselocationx - this.mouselastlocationx; 
		    	int mousedeltay = this.mouselocationy - this.mouselastlocationy;
				this.camrot = this.camrot.copy();
		    	this.camrot.z -= mousedeltax*0.1f;
		    	this.camrot.x -= mousedeltay*0.1f;
		    	updateCameraDirections();
				if ((this.mouselocationx<=0)||(this.mouselocationy<=0)||(this.mouselocationx>=(this.renderwidth-1))||(this.mouselocationy>=(this.renderheight-1))) {
					this.handle(mouseevent.copyFor(mouseevent.getSource(), this.scene.getWindow(), MouseEvent.MOUSE_EXITED));
				}
			}
		} else if (event.getEventType().equals(MouseEvent.MOUSE_EXITED)) {
			if (this.scene.getWindow().isFocused()) {
				int windowscreenlocationx = (int)this.scene.getWindow().getX();
				int windowscreenlocationy = (int)this.scene.getWindow().getY();
				int origindeltax = (int)Math.floor(((double)(this.renderwidth-1))/2.0f);
				int origindeltay = (int)Math.floor(((double)(this.renderheight-1))/2.0f);
				int windowcenterx = windowscreenlocationx + origindeltax;
				int windowcentery = windowscreenlocationy + origindeltay;
				this.mouselocationx = this.renderwidth/2; 
				this.mouselocationy = this.renderheight/2; 
				Robot mouserobot = new Robot();
				mouserobot.mouseMove(windowcenterx, windowcentery);
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
					renderview = RenderLib.renderProjectedView(campos[0], entitylist, renderwidth, hfov, renderheight, vfov, cameramat, unlitrender, 1, bounces, null, null, null, mouselocationx, mouselocationy);
				} else if (polygonfillmode==2) {
					renderview = RenderLib.renderCubemapView(campos[0], entitylist, renderwidth, renderheight, (int)Math.floor(((double)renderheight)/2.0f), cameramat, unlitrender, 1, bounces, null, null, null, mouselocationx, mouselocationy);
				} else if (polygonfillmode==3) {
					renderview = RenderLib.renderProjectedView(campos[0], entitylist, renderwidth, hfov, renderheight, vfov, cameramat, unlitrender, 2, bounces, null, null, null, mouselocationx, mouselocationy);
				} else if (polygonfillmode==4) {
					renderview = RenderLib.renderSpheremapView(campos[0], entitylist, renderwidth, renderheight, cameramat, unlitrender, 1, bounces, null, null, null, mouselocationx, mouselocationy);
				} else if (polygonfillmode==5) {
					renderview = RenderLib.renderCubemapView(campos[0], entitylist, renderwidth, renderheight, (int)Math.floor(((double)renderheight)/2.0f), cameramat, unlitrender, 2, bounces, null, null, null, mouselocationx, mouselocationy);
				} else if (polygonfillmode==6) {
					renderview = RenderLib.renderProjectedView(campos[0], entitylist, renderwidth, hfov, renderheight, vfov, cameramat, unlitrender, 3, bounces, null, null, null, mouselocationx, mouselocationy);
				} else if (polygonfillmode==7) {
					renderview = RenderLib.renderSpheremapView(campos[0], entitylist, renderwidth, renderheight, cameramat, unlitrender, 2, bounces, null, null, null, mouselocationx, mouselocationy);
				} else if (polygonfillmode==8) {
					renderview = RenderLib.renderCubemapView(campos[0], entitylist, renderwidth, renderheight, (int)Math.floor(((double)renderheight)/2.0f), cameramat, unlitrender, 3, bounces, null, null, null, mouselocationx, mouselocationy);
				}
				RenderViewUpdater.renderupdaterrunning = false;
			}
		}
	}
	
}
