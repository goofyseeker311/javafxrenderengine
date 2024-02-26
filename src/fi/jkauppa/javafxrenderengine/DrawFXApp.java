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
	private Color drawcolor = Color.BLACK;
	private float[] drawcolorhsb = {0.0f, 1.0f, 0.0f};
	private Color erasecolor = new Color(1.0f,1.0f,1.0f,0.0f);
	private int pencilsize = 1;
	private int pencilshape = 1;
	private double pencilangle = 0;
	private boolean penciloverridemode = false;
	private float penciltransparency = 1.0f;
	private int oldpencilsize = 1;
	private boolean drawlinemode = false;
	private int mousestartlocationx = -1, mousestartlocationy = -1;  
	private int mouselastlocationx = -1, mouselastlocationy = -1;  
	private int mouselocationx = -1, mouselocationy = -1;
	private Canvas renderbuffer = null;
	private Canvas outputbuffer = null;
	private Canvas dragbuffer = null;
	private ImagePattern bgpattern = null;
	private WritableImage pencilbuffer = null;
	
	public DrawFXApp(Group root) {
		this.root = root;
		this.scene = root.getScene();
		Canvas bgpatternimage = new Canvas(64,64);
		GraphicsContext pgfx = bgpatternimage.getGraphicsContext2D();
		pgfx.setFill(Color.WHITE);
		pgfx.fillRect(0, 0, bgpatternimage.getWidth(), bgpatternimage.getHeight());
		pgfx.setFill(null);
		pgfx.setStroke(Color.BLACK);
		pgfx.strokeLine(31, 0, 31, 63);
		pgfx.strokeLine(0, 31, 63, 31);
		WritableImage bgimage = bgpatternimage.snapshot(null, null);
		this.bgpattern = new ImagePattern(bgimage);
	}
	@Override public void update() {
		this.renderwidth = (int)this.scene.getWidth();
		this.renderheight = (int)this.scene.getHeight();
		if ((renderbuffer==null)||(renderbuffer.getWidth()!=this.renderwidth)||(renderbuffer.getHeight()!=this.renderheight)) {
			Canvas oldimage = this.renderbuffer;
			this.outputbuffer = new Canvas(this.renderwidth,this.renderheight);
			this.renderbuffer = new Canvas(this.renderwidth,this.renderheight);
			this.dragbuffer = new Canvas(this.renderwidth,this.renderheight);
			GraphicsContext gfx = this.renderbuffer.getGraphicsContext2D();
			gfx.clearRect(0, 0, this.renderwidth, this.renderheight);
			if (oldimage!=null) {
				gfx.drawImage(oldimage.snapshot(null, null), 0, 0);
			}
		}
		GraphicsContext g2 = this.outputbuffer.getGraphicsContext2D();
		g2.setFill(this.bgpattern);
		g2.fillRect(0, 0, this.renderwidth, this.renderheight);
		g2.setFill(null);
		g2.drawImage(renderbuffer.snapshot(null, null), 0, 0);
		if ((this.penciloverridemode)&&(this.pencilbuffer!=null)) {
    		double pencilsizescalefactor = ((double)this.pencilsize)/((double)this.pencilbuffer.getWidth());
			g2.setFill(this.bgpattern);
			int drawlocationx = this.mouselocationx-(int)Math.round((double)this.pencilbuffer.getWidth()*pencilsizescalefactor/2.0f);
			int drawlocationy = this.mouselocationy-(int)Math.round((double)this.pencilbuffer.getHeight()*pencilsizescalefactor/2.0f);
			int drawwidth = (int)Math.round(this.pencilbuffer.getWidth()*pencilsizescalefactor);
			int drawheight = (int)Math.round(this.pencilbuffer.getHeight()*pencilsizescalefactor);
			g2.fillRect(drawlocationx, drawlocationy, drawwidth, drawheight);
		}
		if (this.drawlinemode) {
			this.drawPencilLine(g2, this.mousestartlocationx, this.mousestartlocationy, this.mouselocationx, this.mouselocationy, false, false);
		} else {
			this.drawPencil(g2, this.mouselocationx, this.mouselocationy, false, false);
		}
		ParallelCamera camera = new ParallelCamera();
		this.scene.setCursor(Cursor.DEFAULT);
		this.scene.setFill(null);
		if (this.outputbuffer!=null) {
			WritableImage renderimage = this.outputbuffer.snapshot(null, null);
	        ImageView renderimageview = new ImageView();
	        renderimageview.setImage(renderimage);
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
		if (event.getEventType().equals(KeyEvent.KEY_PRESSED)) {
			KeyEvent keyevent = (KeyEvent)event;
			if (keyevent.getCode()==KeyCode.ESCAPE) {
				//TODO options menu
			} else if (keyevent.getCode()==KeyCode.ENTER) {
				if ((!keyevent.isControlDown())&&(!keyevent.isAltDown())&&(!keyevent.isShiftDown())&&(!keyevent.isMetaDown())) {
			    	this.penciloverridemode = !this.penciloverridemode;
			    }
			} else if (keyevent.getCode()==KeyCode.BACK_SPACE) {
				if (this.renderbuffer!=null) {
					GraphicsContext gfx = this.renderbuffer.getGraphicsContext2D();
					gfx.clearRect(0, 0, this.renderbuffer.getWidth(), this.renderbuffer.getHeight());
				}
			} else if (keyevent.getCode()==KeyCode.C) {
				if (keyevent.isControlDown()) {
					ClipboardContent content = new ClipboardContent();
					WritableImage contentimage = this.renderbuffer.snapshot(null, null);
					content.putImage(contentimage);
					this.cb.setContent(content);
				}
			} else if (keyevent.getCode()==KeyCode.V) {
				if (keyevent.isControlDown()) {
					if (this.cb.hasImage()) {
			        	Image contentimage = this.cb.getImage();
						GraphicsContext loadimagevolatilegfx = this.renderbuffer.getGraphicsContext2D();
						loadimagevolatilegfx.drawImage(contentimage, 0, 0);
					}
				}
			} else if (keyevent.getCode()==KeyCode.INSERT) {
				this.drawcolorhsb[0] += 0.01f;
				if (this.drawcolorhsb[0]>1.0f) {this.drawcolorhsb[0] = 0.0f;}
				Color hsbcolor = Color.hsb(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
				float[] colorvalues = {(float)hsbcolor.getRed(), (float)hsbcolor.getGreen(), (float)hsbcolor.getBlue(), (float)hsbcolor.getOpacity()};
				this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		    	if (this.pencilbuffer!=null) {
		    		this.pencilbuffer = null;
		    		this.pencilsize = this.oldpencilsize;
		    	}
			} else if (keyevent.getCode()==KeyCode.DELETE) {
				this.drawcolorhsb[0] -= 0.01f;
				if (this.drawcolorhsb[0]<0.0f) {this.drawcolorhsb[0] = 1.0f;}
				Color hsbcolor = Color.hsb(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
				float[] colorvalues = {(float)hsbcolor.getRed(), (float)hsbcolor.getGreen(), (float)hsbcolor.getBlue(), (float)hsbcolor.getOpacity()};
				this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		    	if (this.pencilbuffer!=null) {
		    		this.pencilbuffer = null;
		    		this.pencilsize = this.oldpencilsize;
		    	}
			} else if (keyevent.getCode()==KeyCode.HOME) {
				this.drawcolorhsb[1] += 0.01f;
				if (this.drawcolorhsb[1]>1.0f) {this.drawcolorhsb[1] = 1.0f;}
				Color hsbcolor = Color.hsb(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
				float[] colorvalues = {(float)hsbcolor.getRed(), (float)hsbcolor.getGreen(), (float)hsbcolor.getBlue(), (float)hsbcolor.getOpacity()};
				this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		    	if (this.pencilbuffer!=null) {
		    		this.pencilbuffer = null;
		    		this.pencilsize = this.oldpencilsize;
		    	}
			} else if (keyevent.getCode()==KeyCode.END) {
				this.drawcolorhsb[1] -= 0.01f;
				if (this.drawcolorhsb[1]<0.0f) {this.drawcolorhsb[1] = 0.0f;}
				Color hsbcolor = Color.hsb(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
				float[] colorvalues = {(float)hsbcolor.getRed(), (float)hsbcolor.getGreen(), (float)hsbcolor.getBlue(), (float)hsbcolor.getOpacity()};
				this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		    	if (this.pencilbuffer!=null) {
		    		this.pencilbuffer = null;
		    		this.pencilsize = this.oldpencilsize;
		    	}
			} else if (keyevent.getCode()==KeyCode.PAGE_UP) {
				this.drawcolorhsb[2] += 0.01f;
				if (this.drawcolorhsb[2]>1.0f) {this.drawcolorhsb[2] = 1.0f;}
				Color hsbcolor = Color.hsb(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
				float[] colorvalues = {(float)hsbcolor.getRed(), (float)hsbcolor.getGreen(), (float)hsbcolor.getBlue(), (float)hsbcolor.getOpacity()};
				this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		    	if (this.pencilbuffer!=null) {
		    		this.pencilbuffer = null;
		    		this.pencilsize = this.oldpencilsize;
		    	}
			} else if (keyevent.getCode()==KeyCode.PAGE_DOWN) {
				this.drawcolorhsb[2] -= 0.01f;
				if (this.drawcolorhsb[2]<0.0f) {this.drawcolorhsb[2] = 0.0f;}
				Color hsbcolor = Color.hsb(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
				float[] colorvalues = {(float)hsbcolor.getRed(), (float)hsbcolor.getGreen(), (float)hsbcolor.getBlue(), (float)hsbcolor.getOpacity()};
				this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
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
				float[] colorvalues = {(float)this.drawcolor.getRed(), (float)this.drawcolor.getGreen(), (float)this.drawcolor.getBlue(), (float)this.drawcolor.getOpacity()};
				this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
			} else if (keyevent.getCode()==KeyCode.NUMPAD8) {
				this.penciltransparency -= 0.01f;
				if (this.penciltransparency<0.0f) {this.penciltransparency = 0.0f;}
				float[] colorvalues = {(float)this.drawcolor.getRed(), (float)this.drawcolor.getGreen(), (float)this.drawcolor.getBlue(), (float)this.drawcolor.getOpacity()};
				this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
			} else if (keyevent.getCode()==KeyCode.NUMPAD6) {
				this.pencilangle += 0.01f;
				if (this.pencilangle>(2.0f*Math.PI)) {this.pencilangle = 0.0f;}
			} else if (keyevent.getCode()==KeyCode.NUMPAD5) {
				this.pencilangle -= 0.01f;
				if (this.pencilangle<0.0f) {this.pencilangle = 2.0f*Math.PI;}
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
		    		if (savefileextension.equals(pngextensionfilter)) {
						UtilLib.saveImageFormat(savefile.getPath(), SwingFXUtils.fromFXImage(this.renderbuffer.snapshot(null, null), null), new PNGFileFilter());
		    		} else if (savefileextension.equals(jpgextensionfilter)) {
						UtilLib.saveImageFormat(savefile.getPath(), SwingFXUtils.fromFXImage(this.renderbuffer.snapshot(null, null), null), new JPGFileFilter());
		    		} else if (savefileextension.equals(gifextensionfilter)) {
						UtilLib.saveImageFormat(savefile.getPath(), SwingFXUtils.fromFXImage(this.renderbuffer.snapshot(null, null), null), new GIFFileFilter());
		    		} else if (savefileextension.equals(bmpextensionfilter)) {
						UtilLib.saveImageFormat(savefile.getPath(), SwingFXUtils.fromFXImage(this.renderbuffer.snapshot(null, null), null), new BMPFileFilter());
		    		} else if (savefileextension.equals(wbmpextensionfilter)) {
						UtilLib.saveImageFormat(savefile.getPath(), SwingFXUtils.fromFXImage(this.renderbuffer.snapshot(null, null), null), new WBMPFileFilter());
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
					    	dragimagegfx.clearRect(0, 0, this.renderbuffer.getWidth(), this.renderbuffer.getHeight());
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
						drawPencilLine(renderbuffergfx, this.mousestartlocationx, this.mousestartlocationy, this.mouselocationx, this.mouselocationy, mouse3up, this.penciloverridemode);
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
			    boolean mouse1down = (mouseevent.getButton().equals(MouseButton.PRIMARY))&&((!mouseevent.isControlDown())&&(!mouseevent.isAltDown())&&(!mouseevent.isShiftDown())&&(!mouseevent.isMetaDown()));
			    boolean mouse3down = (mouseevent.getButton().equals(MouseButton.SECONDARY))&&((!mouseevent.isControlDown())&&(!mouseevent.isAltDown())&&(!mouseevent.isShiftDown())&&(!mouseevent.isMetaDown()));
			    if (mouse1down||mouse3down) {
			    	this.drawPencil(renderbuffergfx, this.mouselocationx, this.mouselocationy, mouse3down, this.penciloverridemode);
				}			
			    boolean mouse1altdown = (mouseevent.getButton().equals(MouseButton.PRIMARY))&&((!mouseevent.isControlDown())&&(mouseevent.isAltDown())&&(!mouseevent.isShiftDown())&&(!mouseevent.isMetaDown()));
			    boolean mouse3altdown = (mouseevent.getButton().equals(MouseButton.SECONDARY))&&((!mouseevent.isControlDown())&&(mouseevent.isAltDown())&&(!mouseevent.isShiftDown())&&(!mouseevent.isMetaDown()));
			    if (mouse1altdown||mouse3altdown) {
				    this.drawlinemode = true;
			    }
			    boolean mouse1shiftdown = (mouseevent.getButton().equals(MouseButton.PRIMARY))&&((!mouseevent.isControlDown())&&(!mouseevent.isAltDown())&&(mouseevent.isShiftDown())&&(!mouseevent.isMetaDown()));
			    if (mouse1shiftdown) {
			    	Color pickeddrawcolor = this.renderbuffer.snapshot(null, null).getPixelReader().getColor(this.mouselocationx, this.mouselocationy);
			    	float[] drawcolorhsbcomp = {(float)pickeddrawcolor.getHue(), (float)pickeddrawcolor.getSaturation(), (float)pickeddrawcolor.getBrightness()};
					float[] colorvalues = {(float)pickeddrawcolor.getRed(), (float)pickeddrawcolor.getGreen(), (float)pickeddrawcolor.getBlue()};
					this.drawcolorhsb = drawcolorhsbcomp;
					this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
			    }
			    boolean mouse1controldown = (mouseevent.getButton().equals(MouseButton.PRIMARY))&&((mouseevent.isControlDown())&&(!mouseevent.isAltDown())&&(!mouseevent.isShiftDown())&&(!mouseevent.isMetaDown()));
			    if (mouse1controldown) {
			    	//TODO select canvas region
			    }
			    boolean mouse2down = (mouseevent.getButton().equals(MouseButton.MIDDLE))&&((!mouseevent.isControlDown())&&(!mouseevent.isAltDown())&&(!mouseevent.isShiftDown())&&(!mouseevent.isMetaDown()));
			    if (mouse2down) {
			    	GraphicsContext dragimagegfx = this.dragbuffer.getGraphicsContext2D();
			    	dragimagegfx.clearRect(0, 0, this.renderbuffer.getWidth(), this.renderbuffer.getHeight());
			    	dragimagegfx.drawImage(this.renderbuffer.snapshot(null, null), mousedeltax, mousedeltay);
			    	renderbuffergfx.clearRect(0, 0, this.renderbuffer.getWidth(), this.renderbuffer.getHeight());
			    	renderbuffergfx.drawImage(this.dragbuffer.snapshot(null, null), 0, 0);
			    }
			    boolean mouse2shiftdown = (mouseevent.getButton().equals(MouseButton.MIDDLE))&&((!mouseevent.isControlDown())&&(!mouseevent.isAltDown())&&(mouseevent.isShiftDown())&&(!mouseevent.isMetaDown()));
			    if (mouse2shiftdown) {
			    	//TODO <tbd>
			    }
			}
		} else if (event.getEventType().equals(ScrollEvent.SCROLL)) {
			ScrollEvent scrollevent = (ScrollEvent)event;
			double scrollticksX = -scrollevent.getDeltaX()/scrollevent.getMultiplierX();
			double scrollticksY = -scrollevent.getDeltaY()/scrollevent.getMultiplierY();
		    boolean mousewheeldown = ((!scrollevent.isControlDown())&&(!scrollevent.isAltDown())&&(!scrollevent.isShiftDown())&&(!scrollevent.isMetaDown()));
		    if (mousewheeldown) {
		    	this.pencilsize += scrollticksY*((this.pencilsize>16)?this.pencilsize/16:1);
				if (this.pencilsize<1) {
					this.pencilsize = 1;
				}
		    }
		    boolean mousewheelctrldown = ((scrollevent.isControlDown())&&(!scrollevent.isAltDown())&&(!scrollevent.isShiftDown())&&(!scrollevent.isMetaDown()));
		    if (mousewheelctrldown) {
		    	this.drawcolorhsb[0] += 0.01f*scrollticksY;
		    	if (this.drawcolorhsb[0]>1.0f) {this.drawcolorhsb[0] = 0.0f;}
		    	else if (this.drawcolorhsb[0]<0.0f) {this.drawcolorhsb[0] = 1.0f;}
		    	Color hsbcolor = Color.hsb(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
		    	float[] colorvalues = {(float)hsbcolor.getRed(), (float)hsbcolor.getGreen(), (float)hsbcolor.getBlue()};
		    	this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		    }
		    boolean mousewheelaltdown = ((!scrollevent.isControlDown())&&(scrollevent.isAltDown())&&(!scrollevent.isShiftDown())&&(!scrollevent.isMetaDown()));
		    if (mousewheelaltdown) {
		    	this.drawcolorhsb[2] += 0.01f*scrollticksY;
		    	if (this.drawcolorhsb[2]>1.0f) {this.drawcolorhsb[2] = 1.0f;}
		    	else if (this.drawcolorhsb[2]<0.0f) {this.drawcolorhsb[2] = 0.0f;}
		    	Color hsbcolor = Color.hsb(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
		    	float[] colorvalues = {(float)hsbcolor.getRed(), (float)hsbcolor.getGreen(), (float)hsbcolor.getBlue()};
		    	this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		    }
		    boolean mousewheelctrlaltdown = ((scrollevent.isControlDown())&&(scrollevent.isAltDown())&&(!scrollevent.isShiftDown())&&(!scrollevent.isMetaDown()));
		    if (mousewheelctrlaltdown) {
		    	this.drawcolorhsb[1] += 0.01f*scrollticksY;
		    	if (this.drawcolorhsb[1]>1.0f) {this.drawcolorhsb[1] = 1.0f;}
		    	else if (this.drawcolorhsb[1]<0.0f) {this.drawcolorhsb[1] = 0.0f;}
		    	Color hsbcolor = Color.hsb(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
		    	float[] colorvalues = {(float)hsbcolor.getRed(), (float)hsbcolor.getGreen(), (float)hsbcolor.getBlue()};
		    	this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		    }
		    boolean mousewheelshiftdown = ((!scrollevent.isControlDown())&&(!scrollevent.isAltDown())&&(scrollevent.isShiftDown())&&(!scrollevent.isMetaDown()));
		    if (mousewheelshiftdown) {
				this.pencilangle += 0.05f*scrollticksX;
				if (this.pencilangle>(2.0f*Math.PI)) {
					this.pencilangle = 0.0f;
				} else if (this.pencilangle<0.0f) {
					this.pencilangle = 2.0f*Math.PI;
				}
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

	private void drawPencil(GraphicsContext g, int mousex, int mousey, boolean erasemode, boolean overridemode) {
		g.setFill(null);
		g.setStroke(null);
		int pencilwidth = (int)Math.ceil((double)(this.pencilsize-1)/2.0f);
    	if (this.pencilbuffer!=null) {
	    	if (erasemode) {
	    		if (overridemode) {
	    			//g.setComposite(AlphaComposite.Clear);
	    		} else {
	    			//g.setComposite(AlphaComposite.DstOut);
	    		}
	    	} else {
	    		if (overridemode) {
	    			//g.setComposite(AlphaComposite.Src);
	    		}
    		}
    		double pencilsizescalefactor = ((double)this.pencilsize)/((double)this.pencilbuffer.getWidth());
    		int halfwidth = (int)Math.floor(((double)this.pencilbuffer.getWidth())*pencilsizescalefactor/2.0f);
    		int halfheight = (int)Math.floor(((double)this.pencilbuffer.getHeight())*pencilsizescalefactor/2.0f);
    		int drawlocationx = mousex - halfwidth;
    		int drawlocationy = mousey - halfheight;
    		Affine penciltransform = new Affine();
    		penciltransform.appendTranslation(drawlocationx, drawlocationy);
    		penciltransform.appendRotation(this.pencilangle,halfwidth,halfheight);
    		penciltransform.appendScale(pencilsizescalefactor, pencilsizescalefactor);
    		g.setTransform(penciltransform);
    		g.drawImage(this.pencilbuffer, 0, 0);
    	} else {
	    	if (erasemode) {
	    		g.setFill(this.erasecolor);
	    		g.setStroke(this.erasecolor);
	    	} else {
	    		if (overridemode) {
		    		//g.setComposite(AlphaComposite.Src);
	    		}
    			g.setFill(this.drawcolor);
    			g.setStroke(this.drawcolor);
	    	}
			if (this.pencilshape==2) {
				g.fillRoundRect(mousex-pencilwidth, mousey-pencilwidth, this.pencilsize, this.pencilsize, 5, 5);
			} else if (this.pencilshape==3) {
				g.fillOval(mousex-pencilwidth, mousey-pencilwidth, this.pencilsize, this.pencilsize);
			} else if (this.pencilshape==4) {
				g.strokeRect(mousex-pencilwidth, mousey-pencilwidth, this.pencilsize, this.pencilsize);
			} else if (this.pencilshape==5) {
				g.strokeRoundRect(mousex-pencilwidth, mousey-pencilwidth, this.pencilsize, this.pencilsize, 5, 5);
			} else if (this.pencilshape==6) {
				g.strokeOval(mousex-pencilwidth, mousey-pencilwidth, this.pencilsize, this.pencilsize);
			}else {
				g.fillRect(mousex-pencilwidth, mousey-pencilwidth, this.pencilsize, this.pencilsize);
			}
    	}
	}
	private void drawPencilLine(GraphicsContext g, int mousestartx, int mousestarty, int mousex, int mousey, boolean erasemode, boolean overridemode) {
		double linedistx = mousex-mousestartx;
		double linedisty = mousey-mousestarty;
		int linestepnum = (int)Math.ceil(Math.sqrt(linedistx*linedistx+linedisty*linedisty))+1;
		double linestepx = linedistx/linestepnum;
		double linestepy = linedisty/linestepnum;
		for (int i=0;i<linestepnum;i++) {
			int drawposx = (int)Math.round(this.mousestartlocationx + i*linestepx);
			int drawposy = (int)Math.round(this.mousestartlocationy + i*linestepy);
	    	this.drawPencil(g, drawposx, drawposy, erasemode, overridemode);
		}
	}

}
