package fi.jkauppa.javafxrenderengine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import fi.jkauppa.javafxrenderengine.JavaFXRenderEngine.AppFXHandler;
import fi.jkauppa.javarenderengine.UtilLib;
import fi.jkauppa.javarenderengine.UtilLib.ImageFileFilters.BMPFileFilter;
import fi.jkauppa.javarenderengine.UtilLib.ImageFileFilters.GIFFileFilter;
import fi.jkauppa.javarenderengine.UtilLib.ImageFileFilters.JPGFileFilter;
import fi.jkauppa.javarenderengine.UtilLib.ImageFileFilters.PNGFileFilter;
import fi.jkauppa.javarenderengine.UtilLib.ImageFileFilters.WBMPFileFilter;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.ParallelCamera;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.transform.Affine;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class DrawFXApp extends AppFXHandler {
	private Group root = null;
	private Scene scene = null;
	private SnapshotParameters snap = new SnapshotParameters();
	private double[] hsbdrawcolor = {0.0f,1.0f,0.0f,1.0f};
	private Color drawcolor = Color.hsb(hsbdrawcolor[0],hsbdrawcolor[1],hsbdrawcolor[2],hsbdrawcolor[3]);
	private int pencilsize = 1;
	private int pencilshape = 1;
	private double pencilangle = 0;
	private float penciltransparency = 1.0f;
	private int oldpencilsize = 1;
	private boolean drawlinemode = false;
	private boolean rotatemode = false;
	private int mousestartlocationx = -1, mousestartlocationy = -1;  
	private int mouselastlocationx = -1, mouselastlocationy = -1;  
	private int mouselocationx = -1, mouselocationy = -1;
	private Canvas renderbuffer = null;
	private Canvas outputbuffer = null;
	private Canvas dragbuffer = null;
	private WritableImage bgimage = null;
	private ImagePattern bgpattern = null;
	private WritableImage pencilbuffer = null;
	
	public DrawFXApp(Group root) {
		this.root = root;
		this.scene = root.getScene();
		this.snap.setFill(Color.TRANSPARENT);
		Canvas bgpatternimage = new Canvas(64,64);
		GraphicsContext pgfx = bgpatternimage.getGraphicsContext2D();
		pgfx.setFill(Color.WHITE);
		pgfx.fillRect(0, 0, bgpatternimage.getWidth(), bgpatternimage.getHeight());
		pgfx.setFill(null);
		pgfx.setStroke(Color.BLACK);
		pgfx.strokeLine(31, 0, 31, 63);
		pgfx.strokeLine(0, 31, 63, 31);
		this.bgimage = bgpatternimage.snapshot(this.snap, null);
		this.bgpattern = new ImagePattern(this.bgimage, 0.0f, 0.0f, bgpatternimage.getWidth(), bgpatternimage.getHeight(), false);
	}
	@Override public void update() {
		this.renderwidth = (int)this.scene.getWidth();
		this.renderheight = (int)this.scene.getHeight();
		this.scene.setCursor(Cursor.DEFAULT);
		this.scene.setFill(Color.TRANSPARENT);
		if ((this.renderbuffer==null)||(this.renderbuffer.getWidth()!=this.renderwidth)||(this.renderbuffer.getHeight()!=this.renderheight)) {
			Canvas oldimage = this.renderbuffer;
			this.outputbuffer = new Canvas(this.renderwidth,this.renderheight);
			this.renderbuffer = new Canvas(this.renderwidth,this.renderheight);
			this.dragbuffer = new Canvas(this.renderwidth,this.renderheight);
			GraphicsContext gfx = this.renderbuffer.getGraphicsContext2D();
			gfx.clearRect(0, 0, this.renderwidth,this.renderheight);
			if (oldimage!=null) {
				gfx.drawImage(oldimage.snapshot(this.snap, null), 0, 0);
			}
		}
		GraphicsContext g2 = this.outputbuffer.getGraphicsContext2D();
		g2.setFill(this.bgpattern);
		g2.fillRect(0, 0, this.renderwidth, this.renderheight);
		g2.drawImage(this.renderbuffer.snapshot(this.snap, null), 0, 0);
		if (this.drawlinemode) {
			this.drawPencilLine(g2, this.mousestartlocationx, this.mousestartlocationy, this.mouselocationx, this.mouselocationy, false);
		} else {
			this.drawPencil(g2, this.mouselocationx, this.mouselocationy, false);
		}
		ParallelCamera camera = new ParallelCamera();
		if (this.outputbuffer!=null) {
			WritableImage outputimage = this.outputbuffer.snapshot(this.snap, null);
	        ImageView renderimageview = new ImageView();
	        renderimageview.setImage(outputimage);
	        renderimageview.setFitWidth(this.renderwidth);
	        renderimageview.setPreserveRatio(true);
	        renderimageview.setSmooth(true);
	        renderimageview.setCache(true);
			root.getChildren().setAll(renderimageview);
		}
		this.scene.setCamera(camera);
	}
	
	@Override public void tick() {}
	@Override public void pulse() {}
	
	@Override public void handle(Event event) {
		if (event.getEventType().equals(KeyEvent.KEY_RELEASED)) {
			KeyEvent keyevent = (KeyEvent)event;
			if (keyevent.getCode()==KeyCode.TAB) {
				this.rotatemode = false;
			}
		} else if (event.getEventType().equals(KeyEvent.KEY_PRESSED)) {
			KeyEvent keyevent = (KeyEvent)event;
			if (keyevent.getCode()==KeyCode.ESCAPE) {
				//TODO options menu
			} else if (keyevent.getCode()==KeyCode.ENTER) {
				//TODO <tbd>
			} else if (keyevent.getCode()==KeyCode.TAB) {
				this.rotatemode = true;
			} else if (keyevent.getCode()==KeyCode.BACK_SPACE) {
				if (this.renderbuffer!=null) {
					GraphicsContext gfx = this.renderbuffer.getGraphicsContext2D();
					gfx.clearRect(0, 0, this.renderbuffer.getWidth(), this.renderbuffer.getHeight());
				}
			} else if (keyevent.getCode()==KeyCode.C) {
				if (keyevent.isControlDown()) {
					ClipboardContent content = new ClipboardContent();
					WritableImage contentimage = this.renderbuffer.snapshot(this.snap, null);
					content.putImage(contentimage);
					this.cb.setContent(content);
				}
			} else if (keyevent.getCode()==KeyCode.V) {
				if (keyevent.isControlDown()) {
					if (this.cb.hasImage()) {
			        	Image contentimage = this.cb.getImage();
						GraphicsContext gfx = this.renderbuffer.getGraphicsContext2D();
						gfx.clearRect(0, 0, contentimage.getWidth(), contentimage.getHeight());
						gfx.drawImage(contentimage, 0, 0);
					}
				}
			} else if (keyevent.getCode()==KeyCode.INSERT) {
		    	if (keyevent.isShiftDown()) {
		    		this.hsbdrawcolor[0] = this.hsbdrawcolor[0] + 20.0f;
		    	} else {
		    		this.hsbdrawcolor[0] = this.hsbdrawcolor[0] + 1.0f;
		    	}
				if (this.hsbdrawcolor[0]>360.0f) {this.hsbdrawcolor[0] = 0.0f;}
				this.drawcolor = Color.hsb(this.hsbdrawcolor[0],this.hsbdrawcolor[1],this.hsbdrawcolor[2],this.penciltransparency);
		    	if (this.pencilbuffer!=null) {
		    		this.pencilbuffer = null;
		    		this.pencilsize = this.oldpencilsize;
		    	}
			} else if (keyevent.getCode()==KeyCode.DELETE) {
		    	if (keyevent.isShiftDown()) {
		    		this.hsbdrawcolor[0] = this.hsbdrawcolor[0] - 20.0f;
		    	} else {
		    		this.hsbdrawcolor[0] = this.hsbdrawcolor[0] - 1.0f;
		    	}
				if (this.hsbdrawcolor[0]<0.0f) {this.hsbdrawcolor[0] = 360.0f;}
				this.drawcolor = Color.hsb(this.hsbdrawcolor[0],this.hsbdrawcolor[1],this.hsbdrawcolor[2],this.penciltransparency);
		    	if (this.pencilbuffer!=null) {
		    		this.pencilbuffer = null;
		    		this.pencilsize = this.oldpencilsize;
		    	}
			} else if (keyevent.getCode()==KeyCode.HOME) {
		    	if (keyevent.isShiftDown()) {
		    		this.hsbdrawcolor[1] = this.hsbdrawcolor[1] + 0.20f;
		    	} else {
					this.hsbdrawcolor[1] = this.hsbdrawcolor[1] + 0.01f;
		    	}
				if (this.hsbdrawcolor[1]>1.0f) {this.hsbdrawcolor[1] = 1.0f;}
				this.drawcolor = Color.hsb(this.hsbdrawcolor[0],this.hsbdrawcolor[1],this.hsbdrawcolor[2],this.penciltransparency);
		    	if (this.pencilbuffer!=null) {
		    		this.pencilbuffer = null;
		    		this.pencilsize = this.oldpencilsize;
		    	}
			} else if (keyevent.getCode()==KeyCode.END) {
		    	if (keyevent.isShiftDown()) {
		    		this.hsbdrawcolor[1] = this.hsbdrawcolor[1] - 0.20f;
		    	} else {
					this.hsbdrawcolor[1] = this.hsbdrawcolor[1] - 0.01f;
		    	}
				if (this.hsbdrawcolor[1]<0.0f) {this.hsbdrawcolor[1] = 0.0f;}
				this.drawcolor = Color.hsb(this.hsbdrawcolor[0],this.hsbdrawcolor[1],this.hsbdrawcolor[2],this.penciltransparency);
		    	if (this.pencilbuffer!=null) {
		    		this.pencilbuffer = null;
		    		this.pencilsize = this.oldpencilsize;
		    	}
			} else if (keyevent.getCode()==KeyCode.PAGE_UP) {
		    	if (keyevent.isShiftDown()) {
		    		this.hsbdrawcolor[2] = this.hsbdrawcolor[2] + 0.20f;
		    	} else {
					this.hsbdrawcolor[2] = this.hsbdrawcolor[2] + 0.01f;
		    	}
				if (this.hsbdrawcolor[2]>1.0f) {this.hsbdrawcolor[2] = 1.0f;}
				this.drawcolor = Color.hsb(this.hsbdrawcolor[0],this.hsbdrawcolor[1],this.hsbdrawcolor[2],this.penciltransparency);
		    	if (this.pencilbuffer!=null) {
		    		this.pencilbuffer = null;
		    		this.pencilsize = this.oldpencilsize;
		    	}
			} else if (keyevent.getCode()==KeyCode.PAGE_DOWN) {
		    	if (keyevent.isShiftDown()) {
		    		this.hsbdrawcolor[2] = this.hsbdrawcolor[2] - 0.20f;
		    	} else {
					this.hsbdrawcolor[2] = this.hsbdrawcolor[2] - 0.01f;
		    	}
				if (this.hsbdrawcolor[2]<0.0f) {this.hsbdrawcolor[2] = 0.0f;}
				this.drawcolor = Color.hsb(this.hsbdrawcolor[0],this.hsbdrawcolor[1],this.hsbdrawcolor[2],this.penciltransparency);
		    	if (this.pencilbuffer!=null) {
		    		this.pencilbuffer = null;
		    		this.pencilsize = this.oldpencilsize;
		    	}
			} else if (keyevent.getCode()==KeyCode.ADD) {
				this.pencilsize += 1;
			} else if (keyevent.getCode()==KeyCode.SUBTRACT) {
				this.pencilsize -= 1;
				if (this.pencilsize<1) {this.pencilsize = 1;}
			} else if (keyevent.getCode()==KeyCode.DIVIDE) {
		    	this.pencilshape -= 1;
		    	if (this.pencilshape<1) {this.pencilshape = 6;}
		    	if (this.pencilbuffer!=null) {
		    		this.pencilbuffer = null;
		    		this.pencilsize = this.oldpencilsize;
		    	}
			} else if (keyevent.getCode()==KeyCode.MULTIPLY) {
		    	this.pencilshape += 1;
		    	if (this.pencilshape>6) {this.pencilshape = 1;}
		    	if (this.pencilbuffer!=null) {
		    		this.pencilbuffer = null;
		    		this.pencilsize = this.oldpencilsize;
		    	}
			} else if (keyevent.getCode()==KeyCode.NUMPAD9) {
				this.penciltransparency += 0.01f;
				if (this.penciltransparency>1.0f) {this.penciltransparency = 1.0f;}
				this.drawcolor = Color.hsb(this.hsbdrawcolor[0],this.hsbdrawcolor[1],this.hsbdrawcolor[2],this.penciltransparency);
			} else if (keyevent.getCode()==KeyCode.NUMPAD8) {
				this.penciltransparency -= 0.01f;
				if (this.penciltransparency<0.0f) {this.penciltransparency = 0.0f;}
				this.drawcolor = Color.hsb(this.hsbdrawcolor[0],this.hsbdrawcolor[1],this.hsbdrawcolor[2],this.penciltransparency);
			} else if (keyevent.getCode()==KeyCode.NUMPAD6) {
		    	if (keyevent.isShiftDown()) {
		    		this.pencilangle += 20.0f*0.05f;
		    	} else {
		    		this.pencilangle += 1.0f*0.05f;
		    	}
				if (this.pencilangle>360.0f) {
					this.pencilangle = 0.0f;
				}
			} else if (keyevent.getCode()==KeyCode.NUMPAD5) {
		    	if (keyevent.isShiftDown()) {
		    		this.pencilangle -= 20.0f*0.05f;
		    	} else {
		    		this.pencilangle -= 1.0f*0.05f;
		    	}
				if (this.pencilangle<0.0f) {
					this.pencilangle = 360.0f;
				}
			} else if (keyevent.getCode()==KeyCode.F1) {
				//TODO help pop-up window
			} else if (keyevent.getCode()==KeyCode.F2) {
	        	FileChooser filechooser = new FileChooser();
	        	filechooser.setInitialDirectory(new File(this.userdir));
		    	filechooser.setTitle("Save File");
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
		    		BufferedImage saveimage = SwingFXUtils.fromFXImage(this.renderbuffer.snapshot(this.snap, null), null);
		    		if (savefileextension.equals(pngextensionfilter)) {
						UtilLib.saveImageFormat(savefile.getPath(), saveimage, new PNGFileFilter());
		    		} else if (savefileextension.equals(jpgextensionfilter)) {
						UtilLib.saveImageFormat(savefile.getPath(), saveimage, new JPGFileFilter());
		    		} else if (savefileextension.equals(gifextensionfilter)) {
						UtilLib.saveImageFormat(savefile.getPath(), saveimage, new GIFFileFilter());
		    		} else if (savefileextension.equals(bmpextensionfilter)) {
						UtilLib.saveImageFormat(savefile.getPath(), saveimage, new BMPFileFilter());
		    		} else if (savefileextension.equals(wbmpextensionfilter)) {
						UtilLib.saveImageFormat(savefile.getPath(), saveimage, new WBMPFileFilter());
		    		}
				}
			} else if (keyevent.getCode()==KeyCode.F3) {
	        	FileChooser filechooser = new FileChooser();
	        	filechooser.setInitialDirectory(new File(this.userdir));
		    	filechooser.setTitle("Load File");
		    	ExtensionFilter imageextensionfilter = new ExtensionFilter("Image files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp", "*.wbmp");
	        	filechooser.getExtensionFilters().add(imageextensionfilter);
	        	filechooser.setSelectedExtensionFilter(imageextensionfilter);
		    	File loadfile = filechooser.showOpenDialog(this.scene.getWindow());
		    	if (loadfile!=null) {
					if (loadfile.getParent()!=null) {this.userdir = loadfile.getParent();}
					BufferedImage loadimage = UtilLib.loadImage(loadfile.getPath(), false);
					if (loadimage!=null) {
					    boolean f3shiftdown = ((!keyevent.isControlDown())&&(!keyevent.isAltDown())&&(keyevent.isShiftDown())&&(!keyevent.isMetaDown()));
					    if (f3shiftdown) {
					    	this.oldpencilsize = this.pencilsize;
							this.pencilsize = loadimage.getWidth();
					    	this.pencilbuffer = SwingFXUtils.toFXImage(loadimage, null);
					    }else{
					    	GraphicsContext dragimagegfx = this.renderbuffer.getGraphicsContext2D();
					    	dragimagegfx.clearRect(0, 0, loadimage.getWidth(), loadimage.getHeight());
					    	dragimagegfx.drawImage(SwingFXUtils.toFXImage(loadimage, null), 0, 0);
					    }
					}
				}
			} else if (keyevent.getCode()==KeyCode.F4) {
				//TODO tools/color pop-up window
			}
		} else if (event.getEventType().equals(MouseEvent.MOUSE_MOVED)) {
			MouseEvent mouseevent = (MouseEvent)event;
			this.mouselocationx=(int)mouseevent.getSceneX();
			this.mouselocationy=(int)mouseevent.getSceneY();
		} else if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
			MouseEvent mouseevent = (MouseEvent)event;
			this.mouselocationx=(int)mouseevent.getSceneX();
			this.mouselocationy=(int)mouseevent.getSceneY();
			this.mousestartlocationx=this.mouselocationx;
			this.mousestartlocationy=this.mouselocationy;
			this.handle(mouseevent.copyFor(mouseevent.getSource(), this.scene.getWindow(), MouseEvent.MOUSE_DRAGGED));
		} else if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
			MouseEvent mouseevent = (MouseEvent)event;
			if (this.renderbuffer!=null) {
				GraphicsContext renderbuffergfx = this.renderbuffer.getGraphicsContext2D();
			    boolean mouse1up = mouseevent.getButton().equals(MouseButton.PRIMARY);
			    boolean mouse3up = mouseevent.getButton().equals(MouseButton.SECONDARY);
				if (mouse1up||mouse3up) {
					if (this.drawlinemode) {
						this.drawlinemode=false;
						drawPencilLine(renderbuffergfx, this.mousestartlocationx, this.mousestartlocationy, this.mouselocationx, this.mouselocationy, mouse3up);
					}
				}
			}
		} else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
			MouseEvent mouseevent = (MouseEvent)event;
			this.mouselastlocationx=this.mouselocationx;
			this.mouselastlocationy=this.mouselocationy;
			this.mouselocationx=(int)mouseevent.getSceneX();
			this.mouselocationy=(int)mouseevent.getSceneY();
	    	int mousedeltax = this.mouselocationx - this.mouselastlocationx; 
	    	int mousedeltay = this.mouselocationy - this.mouselastlocationy;
			if (this.renderbuffer!=null) {
				GraphicsContext renderbuffergfx = this.renderbuffer.getGraphicsContext2D();
			    boolean mouse1down = (mouseevent.getButton().equals(MouseButton.PRIMARY))&&((!mouseevent.isControlDown())&&(!mouseevent.isAltDown())&&(!mouseevent.isMetaDown()));
			    boolean mouse3down = (mouseevent.getButton().equals(MouseButton.SECONDARY))&&((!mouseevent.isControlDown())&&(!mouseevent.isAltDown())&&(!mouseevent.isMetaDown()));
			    if (mouse1down||mouse3down) {
			    	this.drawPencil(renderbuffergfx, this.mouselocationx, this.mouselocationy, mouse3down);
				}
			    boolean mouse1altdown = (mouseevent.getButton().equals(MouseButton.PRIMARY))&&((!mouseevent.isControlDown())&&(mouseevent.isAltDown())&&(!mouseevent.isMetaDown()));
			    boolean mouse3altdown = (mouseevent.getButton().equals(MouseButton.SECONDARY))&&((!mouseevent.isControlDown())&&(mouseevent.isAltDown())&&(!mouseevent.isMetaDown()));
			    if (mouse1altdown||mouse3altdown) {
				    this.drawlinemode = true;
			    }
			    boolean mouse1ctrldown = (mouseevent.getButton().equals(MouseButton.PRIMARY))&&((mouseevent.isControlDown())&&(!mouseevent.isAltDown())&&(!mouseevent.isMetaDown()));
			    if (mouse1ctrldown) {
			    	this.drawcolor = this.renderbuffer.snapshot(this.snap, null).getPixelReader().getColor(this.mouselocationx, this.mouselocationy);
			    	double[] newhsbdrawcolor = {this.drawcolor.getHue(), this.drawcolor.getSaturation(), this.drawcolor.getBrightness(), this.penciltransparency};
			    	this.hsbdrawcolor = newhsbdrawcolor;
			    }
			    boolean mouse1controldown = (mouseevent.getButton().equals(MouseButton.PRIMARY))&&((mouseevent.isControlDown())&&(!mouseevent.isAltDown())&&(!mouseevent.isMetaDown()));
			    if (mouse1controldown) {
			    	//TODO select canvas region
			    }
			    boolean mouse2down = (mouseevent.getButton().equals(MouseButton.MIDDLE))&&((!mouseevent.isControlDown())&&(!mouseevent.isAltDown())&&(!mouseevent.isMetaDown()));
			    if (mouse2down) {
			    	GraphicsContext dragimagegfx = this.dragbuffer.getGraphicsContext2D();
			    	dragimagegfx.clearRect(0, 0, this.renderbuffer.getWidth(), this.renderbuffer.getHeight());
			    	dragimagegfx.drawImage(this.renderbuffer.snapshot(this.snap, null), mousedeltax, mousedeltay);
			    	renderbuffergfx.clearRect(0, 0, this.renderbuffer.getWidth(), this.renderbuffer.getHeight());
			    	renderbuffergfx.drawImage(this.dragbuffer.snapshot(this.snap, null), 0, 0);
			    }
			}
		} else if (event.getEventType().equals(ScrollEvent.SCROLL)) {
			ScrollEvent scrollevent = (ScrollEvent)event;
			double scrollticksX = -scrollevent.getDeltaX()/scrollevent.getMultiplierX();
			double scrollticksY = -scrollevent.getDeltaY()/scrollevent.getMultiplierY();
		    boolean mousewheeldown = ((!scrollevent.isControlDown())&&(!scrollevent.isAltDown())&&(!scrollevent.isMetaDown()));
		    if (mousewheeldown) {
		    	if (this.rotatemode) {
			    	if (scrollevent.isShiftDown()) {
			    		this.pencilangle -= 20.0f*0.05f*scrollticksX;
			    	} else {
			    		this.pencilangle -= 1.0f*0.05f*scrollticksY;
			    	}
					if (this.pencilangle>360.0f) {
						this.pencilangle = 0.0f;
					} else if (this.pencilangle<0.0f) {
						this.pencilangle = 360.0f;
					}
		    	} else {
			    	if (scrollevent.isShiftDown()) {
			    		this.pencilsize -= 20.0f*scrollticksX*((this.pencilsize>16)?this.pencilsize/16:1);
			    	} else {
			    		this.pencilsize -= 1.0f*scrollticksY*((this.pencilsize>16)?this.pencilsize/16:1);
			    	}
					if (this.pencilsize<1) {
						this.pencilsize = 1;
					}
		    	}
		    }
		    boolean mousewheelctrldown = ((scrollevent.isControlDown())&&(!scrollevent.isAltDown())&&(!scrollevent.isMetaDown()));
		    if (mousewheelctrldown) {
		    	if (scrollevent.isShiftDown()) {
		    		this.hsbdrawcolor[0] = this.hsbdrawcolor[0] + 20.0f*scrollticksX;
		    	} else {
		    		this.hsbdrawcolor[0] = this.hsbdrawcolor[0] + 1.0f*scrollticksY;
		    	}
				if (this.hsbdrawcolor[0]>360.0f) {this.hsbdrawcolor[0] = 0.0f;}
				if (this.hsbdrawcolor[0]<0.0f) {this.hsbdrawcolor[0] = 360.0f;}
				this.drawcolor = Color.hsb(this.hsbdrawcolor[0],this.hsbdrawcolor[1],this.hsbdrawcolor[2],this.penciltransparency);
		    }
		    boolean mousewheelaltdown = ((!scrollevent.isControlDown())&&(scrollevent.isAltDown())&&(!scrollevent.isMetaDown()));
		    if (mousewheelaltdown) {
		    	if (scrollevent.isShiftDown()) {
		    		this.hsbdrawcolor[1] = this.hsbdrawcolor[1] - 0.20f*scrollticksX;
		    	} else {
		    		this.hsbdrawcolor[1] = this.hsbdrawcolor[1] - 0.01f*scrollticksY;
		    	}
				if (this.hsbdrawcolor[1]>1.0f) {this.hsbdrawcolor[1] = 1.0f;}
				if (this.hsbdrawcolor[1]<0.0f) {this.hsbdrawcolor[1] = 0.0f;}
				this.drawcolor = Color.hsb(this.hsbdrawcolor[0],this.hsbdrawcolor[1],this.hsbdrawcolor[2],this.penciltransparency);
		    }
		    boolean mousewheelctrlaltdown = ((scrollevent.isControlDown())&&(scrollevent.isAltDown())&&(!scrollevent.isMetaDown()));
		    if (mousewheelctrlaltdown) {
		    	if (scrollevent.isShiftDown()) {
		    		this.hsbdrawcolor[2] = this.hsbdrawcolor[2] - 0.20f*scrollticksX;
		    	} else {
		    		this.hsbdrawcolor[2] = this.hsbdrawcolor[2] - 0.01f*scrollticksY;
		    	}
				if (this.hsbdrawcolor[2]>1.0f) {this.hsbdrawcolor[2] = 1.0f;}
				if (this.hsbdrawcolor[2]<0.0f) {this.hsbdrawcolor[2] = 0.0f;}
				this.drawcolor = Color.hsb(this.hsbdrawcolor[0],this.hsbdrawcolor[1],this.hsbdrawcolor[2],this.penciltransparency);
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
                	BufferedImage loadimage = UtilLib.loadImage(file.getPath(), false);
			    	this.oldpencilsize = this.pencilsize;
					this.pencilsize = loadimage.getWidth();
					this.pencilbuffer = SwingFXUtils.toFXImage(loadimage, null);
                }
	        	success = true;
			}
        	dragevent.setDropCompleted(success);
		}
	}

	private void drawPencil(GraphicsContext g, int mousex, int mousey, boolean erasemode) {
    	if (this.pencilbuffer!=null) {
    		double pencilsizescalefactor = ((double)this.pencilsize)/((double)this.pencilbuffer.getWidth());
    		int scalewidth = (int)Math.floor(((double)this.pencilbuffer.getWidth())*pencilsizescalefactor);
    		int scaleheight = (int)Math.floor(((double)this.pencilbuffer.getHeight())*pencilsizescalefactor);
    		int halfscalewidth = (int)(scalewidth/2.0f);
    		int halfscaleheight = (int)(scaleheight/2.0f);
    		int drawlocationx = mousex - halfscalewidth;
    		int drawlocationy = mousey - halfscaleheight;
    		Affine penciltransform = new Affine();
    		penciltransform.appendTranslation(drawlocationx, drawlocationy);
    		penciltransform.appendRotation(this.pencilangle,halfscalewidth,halfscaleheight);
    		g.save();
    		g.transform(penciltransform);
    		if (erasemode) {
    			g.clearRect(0, 0, scalewidth, scaleheight);
    		} else {
    			g.drawImage(this.pencilbuffer, 0, 0, scalewidth, scaleheight);
    		}
	        g.restore();
    	} else {
    		int pencilwidth = (int)Math.ceil((double)(this.pencilsize-1)/2.0f);
    		int drawlocationx = mousex - pencilwidth;
    		int drawlocationy = mousey - pencilwidth;
    		Affine penciltransform = new Affine();
    		penciltransform.appendTranslation(drawlocationx, drawlocationy);
    		penciltransform.appendRotation(this.pencilangle,pencilwidth,pencilwidth);
    		g.save();
    		g.transform(penciltransform);
    		if (erasemode) {
	    		g.clearRect(0, 0, this.pencilsize, this.pencilsize);
    		} else {
    			g.setFill(this.drawcolor);
    			g.setStroke(this.drawcolor);
    			if (this.pencilshape==2) {
    				g.fillRoundRect(0, 0, this.pencilsize, this.pencilsize, 5, 5);
    			} else if (this.pencilshape==3) {
    				g.fillOval(0, 0, this.pencilsize, this.pencilsize);
    			} else if (this.pencilshape==4) {
    				g.strokeRect(0, 0, this.pencilsize, this.pencilsize);
    			} else if (this.pencilshape==5) {
    				g.strokeRoundRect(0, 0, this.pencilsize, this.pencilsize, 5, 5);
    			} else if (this.pencilshape==6) {
    				g.strokeOval(0, 0, this.pencilsize, this.pencilsize);
    			}else {
    				g.fillRect(0, 0, this.pencilsize, this.pencilsize);
    			}
    		}
    		g.restore();
    	}
	}
	private void drawPencilLine(GraphicsContext g, int mousestartx, int mousestarty, int mousex, int mousey, boolean erasemode) {
		double linedistx = mousex-mousestartx;
		double linedisty = mousey-mousestarty;
		int linestepnum = (int)Math.ceil(Math.sqrt(linedistx*linedistx+linedisty*linedisty))+1;
		double linestepx = linedistx/linestepnum;
		double linestepy = linedisty/linestepnum;
		for (int i=0;i<linestepnum;i++) {
			int drawposx = (int)Math.round(this.mousestartlocationx + i*linestepx);
			int drawposy = (int)Math.round(this.mousestartlocationy + i*linestepy);
	    	this.drawPencil(g, drawposx, drawposy, erasemode);
		}
	}

}
