package fi.jkauppa.javafxrenderengine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
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
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class DrawFXApp extends AppFXHandler {
	private ParallelCamera camera = new ParallelCamera();
	private Scene scene = null;
	private int renderwidth = 0;
	private int renderheight = 0;
	private GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	private GraphicsDevice gd = ge.getDefaultScreenDevice();
	private GraphicsConfiguration gc = gd.getDefaultConfiguration();
	private BufferedImage outputbuffer = null;
	private BufferedImage renderbuffer = null;
	private BufferedImage dragbuffer = null;
	private TexturePaint bgpattern = null;
	private Color drawcolor = Color.BLACK;
	private float[] drawcolorhsb = {0.0f, 1.0f, 0.0f};
	private Color erasecolor = new Color(1.0f,1.0f,1.0f,0.0f);
	private int pencilsize = 1;
	private int pencilshape = 1;
	private double pencilangle = 0;
	private boolean penciloverridemode = false;
	private float penciltransparency = 1.0f;
	private BufferedImage pencilbuffer = null;
	private int oldpencilsize = 1;
	private boolean drawlinemode = false;
	private int mousestartlocationx = -1, mousestartlocationy = -1;  
	private int mouselastlocationx = -1, mouselastlocationy = -1;  
	private int mouselocationx = -1, mouselocationy = -1;
	
	public DrawFXApp() {
		BufferedImage bgpatternimage = gc.createCompatibleImage(64, 64, Transparency.OPAQUE);
		Graphics2D pgfx = bgpatternimage.createGraphics();
		pgfx.setColor(Color.WHITE);
		pgfx.fillRect(0, 0, bgpatternimage.getWidth(), bgpatternimage.getHeight());
		pgfx.setColor(Color.BLACK);
		pgfx.drawLine(31, 0, 31, 63);
		pgfx.drawLine(0, 31, 63, 31);
		pgfx.dispose();
		this.bgpattern = new TexturePaint(bgpatternimage,new Rectangle(0, 0, 64, 64));
	}
	@Override public void update(Group root) {
		this.scene = root.getScene();
		this.renderwidth = (int)this.scene.getWidth();
		this.renderheight = (int)this.scene.getHeight();
		this.scene.setCursor(Cursor.DEFAULT);
		this.scene.setCamera(camera);
		scene.setFill(Paint.valueOf("WHITE"));
		root.getChildren().clear();
		if ((renderbuffer==null)||(renderbuffer.getWidth()!=this.renderwidth)||(renderbuffer.getHeight()!=this.renderheight)) {
			BufferedImage oldimage = this.renderbuffer;
			this.outputbuffer = gc.createCompatibleImage(this.renderwidth,this.renderheight, Transparency.TRANSLUCENT);
			this.renderbuffer = gc.createCompatibleImage(this.renderwidth,this.renderheight, Transparency.TRANSLUCENT);
			this.dragbuffer = gc.createCompatibleImage(this.renderwidth,this.renderheight, Transparency.TRANSLUCENT);
			Graphics2D gfx = this.renderbuffer.createGraphics();
			gfx.setComposite(AlphaComposite.Clear);
			gfx.fillRect(0, 0, this.renderwidth,this.renderheight);
			if (oldimage!=null) {
				gfx.setComposite(AlphaComposite.Src);
				gfx.drawImage(oldimage, 0, 0, null);
			}
		}
		Graphics2D g2 = this.outputbuffer.createGraphics();
		g2.setPaint(this.bgpattern);
		g2.fillRect(0, 0, this.renderwidth, this.renderheight);
		g2.setPaint(null);
		g2.drawImage(renderbuffer, 0, 0, null);
		if ((this.penciloverridemode)&&(this.pencilbuffer!=null)) {
    		double pencilsizescalefactor = ((double)this.pencilsize)/((double)this.pencilbuffer.getWidth());
			g2.setComposite(AlphaComposite.Src);
			g2.setPaint(this.bgpattern);
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
		g2.dispose();
		if (this.outputbuffer!=null) {
			WritableImage renderimage = SwingFXUtils.toFXImage(this.outputbuffer, null);
	        ImageView renderimageview = new ImageView();
	        renderimageview.setImage(renderimage);
	        renderimageview.setFitWidth(this.renderwidth);
	        renderimageview.setPreserveRatio(true);
	        renderimageview.setSmooth(true);
	        renderimageview.setCache(true);
			root.getChildren().add(renderimageview);
		}
	}
	
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
					Graphics2D gfx = this.renderbuffer.createGraphics();
					gfx.setComposite(AlphaComposite.Src);
					gfx.setColor(this.erasecolor);
					gfx.fillRect(0, 0, this.renderbuffer.getWidth(), this.renderbuffer.getHeight());
					gfx.dispose();
				}
			} else if (keyevent.getCode()==KeyCode.C) {
				if (keyevent.isControlDown()) {
					final ClipboardContent content = new ClipboardContent();
					WritableImage contentimage = SwingFXUtils.toFXImage(this.renderbuffer, null);
					content.putImage(contentimage);
					this.cb.setContent(content);
				}
			} else if (keyevent.getCode()==KeyCode.V) {
				if (keyevent.isControlDown()) {
					if (this.cb.hasImage()) {
			        	Image contentimage = this.cb.getImage();
			        	BufferedImage image = SwingFXUtils.fromFXImage(contentimage, null);
						Graphics2D loadimagevolatilegfx = this.renderbuffer.createGraphics();
						loadimagevolatilegfx.setComposite(AlphaComposite.Src);
						loadimagevolatilegfx.drawImage(image, 0, 0, null);
						loadimagevolatilegfx.dispose();
					}
				}
			} else if (keyevent.getCode()==KeyCode.INSERT) {
				this.drawcolorhsb[0] += 0.01f;
				if (this.drawcolorhsb[0]>1.0f) {this.drawcolorhsb[0] = 0.0f;}
				Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
				float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
				this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		    	if (this.pencilbuffer!=null) {
		    		this.pencilbuffer = null;
		    		this.pencilsize = this.oldpencilsize;
		    	}
			} else if (keyevent.getCode()==KeyCode.DELETE) {
				this.drawcolorhsb[0] -= 0.01f;
				if (this.drawcolorhsb[0]<0.0f) {this.drawcolorhsb[0] = 1.0f;}
				Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
				float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
				this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		    	if (this.pencilbuffer!=null) {
		    		this.pencilbuffer = null;
		    		this.pencilsize = this.oldpencilsize;
		    	}
			} else if (keyevent.getCode()==KeyCode.HOME) {
				this.drawcolorhsb[1] += 0.01f;
				if (this.drawcolorhsb[1]>1.0f) {this.drawcolorhsb[1] = 1.0f;}
				Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
				float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
				this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		    	if (this.pencilbuffer!=null) {
		    		this.pencilbuffer = null;
		    		this.pencilsize = this.oldpencilsize;
		    	}
			} else if (keyevent.getCode()==KeyCode.END) {
				this.drawcolorhsb[1] -= 0.01f;
				if (this.drawcolorhsb[1]<0.0f) {this.drawcolorhsb[1] = 0.0f;}
				Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
				float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
				this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		    	if (this.pencilbuffer!=null) {
		    		this.pencilbuffer = null;
		    		this.pencilsize = this.oldpencilsize;
		    	}
			} else if (keyevent.getCode()==KeyCode.PAGE_UP) {
				this.drawcolorhsb[2] += 0.01f;
				if (this.drawcolorhsb[2]>1.0f) {this.drawcolorhsb[2] = 1.0f;}
				Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
				float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
				this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		    	if (this.pencilbuffer!=null) {
		    		this.pencilbuffer = null;
		    		this.pencilsize = this.oldpencilsize;
		    	}
			} else if (keyevent.getCode()==KeyCode.PAGE_DOWN) {
				this.drawcolorhsb[2] -= 0.01f;
				if (this.drawcolorhsb[2]<0.0f) {this.drawcolorhsb[2] = 0.0f;}
				Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
				float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
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
				float[] colorvalues = this.drawcolor.getRGBColorComponents(new float[3]);
				this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
			} else if (keyevent.getCode()==KeyCode.NUMPAD8) {
				this.penciltransparency -= 0.01f;
				if (this.penciltransparency<0.0f) {this.penciltransparency = 0.0f;}
				float[] colorvalues = this.drawcolor.getRGBColorComponents(new float[3]);
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
		    	filechooser.setTitle("Save File");
		    	ExtensionFilter pngextensionfilter = new ExtensionFilter("PNG Image file", "*.png");
		    	ExtensionFilter jpgextensionfilter = new ExtensionFilter("JPG Image file", "*.jpg", "*.jepg");
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
		    		ExtensionFilter savefileextension = filechooser.getSelectedExtensionFilter();
		    		if (savefileextension.equals(pngextensionfilter)) {
						UtilLib.saveImageFormat(savefile.getPath(), this.renderbuffer, new PNGFileFilter());
		    		} else if (savefileextension.equals(jpgextensionfilter)) {
						UtilLib.saveImageFormat(savefile.getPath(), this.renderbuffer, new JPGFileFilter());
		    		} else if (savefileextension.equals(gifextensionfilter)) {
						UtilLib.saveImageFormat(savefile.getPath(), this.renderbuffer, new GIFFileFilter());
		    		} else if (savefileextension.equals(bmpextensionfilter)) {
						UtilLib.saveImageFormat(savefile.getPath(), this.renderbuffer, new BMPFileFilter());
		    		} else if (savefileextension.equals(wbmpextensionfilter)) {
						UtilLib.saveImageFormat(savefile.getPath(), this.renderbuffer, new WBMPFileFilter());
		    		}
				}
			} else if (keyevent.getCode()==KeyCode.F3) {
	        	FileChooser filechooser = new FileChooser();
		    	filechooser.setTitle("Load File");
		    	ExtensionFilter pngextensionfilter = new ExtensionFilter("PNG Image file", "*.png");
		    	ExtensionFilter jpgextensionfilter = new ExtensionFilter("JPG Image file", "*.jpg", "*.jepg");
		    	ExtensionFilter gifextensionfilter = new ExtensionFilter("GIF Image file", "*.gif");
		    	ExtensionFilter bmpextensionfilter = new ExtensionFilter("BMP Image file", "*.bmp");
		    	ExtensionFilter wbmpextensionfilter = new ExtensionFilter("WBMP Image file", "*.wbmp");
	        	filechooser.getExtensionFilters().add(pngextensionfilter);
	        	filechooser.getExtensionFilters().add(jpgextensionfilter);
	        	filechooser.getExtensionFilters().add(gifextensionfilter);
	        	filechooser.getExtensionFilters().add(bmpextensionfilter);
	        	filechooser.getExtensionFilters().add(wbmpextensionfilter);
	        	filechooser.setSelectedExtensionFilter(pngextensionfilter);
		    	File loadfile = filechooser.showOpenDialog(this.scene.getWindow());
		    	if (loadfile!=null) {
					BufferedImage loadimage = UtilLib.loadImage(loadfile.getPath(), false);
					if (loadimage!=null) {
					    boolean f3shiftdown = ((!keyevent.isControlDown())&&(!keyevent.isAltDown())&&(keyevent.isShiftDown())&&(!keyevent.isMetaDown()));
					    if (f3shiftdown) {
					    	this.oldpencilsize = this.pencilsize;
							this.pencilsize = loadimage.getWidth();
					    	this.pencilbuffer = loadimage;
					    }else{
					    	Graphics2D dragimagegfx = this.renderbuffer.createGraphics();
					    	dragimagegfx.setComposite(AlphaComposite.Clear);
					    	dragimagegfx.fillRect(0, 0, this.renderbuffer.getWidth(), this.renderbuffer.getHeight());
					    	dragimagegfx.setComposite(AlphaComposite.Src);
					    	dragimagegfx.drawImage(loadimage, 0, 0, null);
					    	dragimagegfx.dispose();
					    }
					}
				}
			} else if (keyevent.getCode()==KeyCode.F4) {
				//TODO tools/color pop-up window
			}
		} else if (event.getEventType().equals(MouseEvent.MOUSE_MOVED)) {
			MouseEvent mouseevent = (MouseEvent)event;
			this.mouselocationx=(int)mouseevent.getX();
			this.mouselocationy=(int)mouseevent.getY();
		} else if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
			MouseEvent mouseevent = (MouseEvent)event;
			this.mouselocationx=(int)mouseevent.getX();
			this.mouselocationy=(int)mouseevent.getY();
			this.mousestartlocationx=this.mouselocationx;
			this.mousestartlocationy=this.mouselocationy;
			this.handle(mouseevent.copyFor(mouseevent.getSource(), this.scene.getWindow(), MouseEvent.MOUSE_DRAGGED));
		} else if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
			MouseEvent mouseevent = (MouseEvent)event;
			if (this.renderbuffer!=null) {
				Graphics2D renderbuffergfx = this.renderbuffer.createGraphics();
				renderbuffergfx.setColor(this.drawcolor);
			    boolean mouse1up = mouseevent.getButton()==MouseButton.PRIMARY;
			    boolean mouse3up = mouseevent.getButton()==MouseButton.SECONDARY;
				if (mouse1up||mouse3up) {
					if (this.drawlinemode) {
						this.drawlinemode=false;
						drawPencilLine(renderbuffergfx, this.mousestartlocationx, this.mousestartlocationy, this.mouselocationx, this.mouselocationy, mouse3up, this.penciloverridemode);
					}
				}
				renderbuffergfx.dispose();
			}
		} else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
			MouseEvent mouseevent = (MouseEvent)event;
			this.mouselastlocationx=this.mouselocationx;
			this.mouselastlocationy=this.mouselocationy;
			this.mouselocationx=(int)mouseevent.getX();
			this.mouselocationy=(int)mouseevent.getY();
	    	int mousedeltax = this.mouselocationx - this.mouselastlocationx; 
	    	int mousedeltay = this.mouselocationy - this.mouselastlocationy;
			if (this.renderbuffer!=null) {
				Graphics2D renderbuffergfx = this.renderbuffer.createGraphics();
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
					int colorvalue = this.renderbuffer.getRGB(this.mouselocationx, this.mouselocationy);
					Color pickeddrawcolor = new Color(colorvalue);
					this.drawcolorhsb = Color.RGBtoHSB(pickeddrawcolor.getRed(), pickeddrawcolor.getGreen(), pickeddrawcolor.getBlue(), new float[3]);
					float[] colorvalues = pickeddrawcolor.getRGBColorComponents(new float[3]);
					this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
			    }
			    boolean mouse1controldown = (mouseevent.getButton().equals(MouseButton.PRIMARY))&&((mouseevent.isControlDown())&&(!mouseevent.isAltDown())&&(!mouseevent.isShiftDown())&&(!mouseevent.isMetaDown()));
			    if (mouse1controldown) {
			    	//TODO select canvas region
			    }
			    boolean mouse2down = (mouseevent.getButton().equals(MouseButton.MIDDLE))&&((!mouseevent.isControlDown())&&(!mouseevent.isAltDown())&&(!mouseevent.isShiftDown())&&(!mouseevent.isMetaDown()));
			    if (mouse2down) {
			    	Graphics2D dragimagegfx = this.dragbuffer.createGraphics();
			    	dragimagegfx.setComposite(AlphaComposite.Clear);
			    	dragimagegfx.fillRect(0, 0, this.renderbuffer.getWidth(), this.renderbuffer.getHeight());
			    	dragimagegfx.setComposite(AlphaComposite.Src);
			    	dragimagegfx.drawImage(this.renderbuffer, mousedeltax, mousedeltay, null);
			    	dragimagegfx.dispose();
			    	renderbuffergfx.setComposite(AlphaComposite.Clear);
			    	renderbuffergfx.fillRect(0, 0, this.renderbuffer.getWidth(), this.renderbuffer.getHeight());
			    	renderbuffergfx.setComposite(AlphaComposite.Src);
			    	renderbuffergfx.drawImage(dragbuffer, 0, 0, null);
			    }
			    boolean mouse2shiftdown = (mouseevent.getButton().equals(MouseButton.MIDDLE))&&((!mouseevent.isControlDown())&&(!mouseevent.isAltDown())&&(mouseevent.isShiftDown())&&(!mouseevent.isMetaDown()));
			    if (mouse2shiftdown) {
			    	//TODO <tbd>
			    }
		    	renderbuffergfx.dispose();
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
					this.pencilbuffer = loadimage;
                }
	        	success = true;
			}
        	dragevent.setDropCompleted(success);
		} else if (event.getEventType().equals(ScrollEvent.SCROLL)) {
			ScrollEvent scrollevent = (ScrollEvent)event;
		    boolean mousewheeldown = ((!scrollevent.isControlDown())&&(!scrollevent.isAltDown())&&(!scrollevent.isShiftDown())&&(!scrollevent.isMetaDown()));
		    if (mousewheeldown) {
				double scrollticks = scrollevent.getDeltaY()/scrollevent.getMultiplierY();
		    	this.pencilsize -= scrollticks*((this.pencilsize>16)?this.pencilsize/16:1);
				if (this.pencilsize<1) {
					this.pencilsize = 1;
				}
		    }
		    boolean mousewheelctrldown = ((scrollevent.isControlDown())&&(!scrollevent.isAltDown())&&(!scrollevent.isShiftDown())&&(!scrollevent.isMetaDown()));
		    if (mousewheelctrldown) {
				double scrollticks = scrollevent.getDeltaY()/scrollevent.getMultiplierY();
		    	this.drawcolorhsb[0] -= 0.01f*scrollticks;
		    	if (this.drawcolorhsb[0]>1.0f) {this.drawcolorhsb[0] = 0.0f;}
		    	else if (this.drawcolorhsb[0]<0.0f) {this.drawcolorhsb[0] = 1.0f;}
		    	Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
		    	float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
		    	this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		    }
		    boolean mousewheelaltdown = ((!scrollevent.isControlDown())&&(scrollevent.isAltDown())&&(!scrollevent.isShiftDown())&&(!scrollevent.isMetaDown()));
		    if (mousewheelaltdown) {
				double scrollticks = scrollevent.getDeltaY()/scrollevent.getMultiplierY();
		    	this.drawcolorhsb[2] -= 0.01f*scrollticks;
		    	if (this.drawcolorhsb[2]>1.0f) {this.drawcolorhsb[2] = 1.0f;}
		    	else if (this.drawcolorhsb[2]<0.0f) {this.drawcolorhsb[2] = 0.0f;}
		    	Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
		    	float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
		    	this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		    }
		    boolean mousewheelctrlaltdown = ((scrollevent.isControlDown())&&(scrollevent.isAltDown())&&(!scrollevent.isShiftDown())&&(!scrollevent.isMetaDown()));
		    if (mousewheelctrlaltdown) {
				double scrollticks = scrollevent.getDeltaY()/scrollevent.getMultiplierY();
		    	this.drawcolorhsb[1] -= 0.01f*scrollticks;
		    	if (this.drawcolorhsb[1]>1.0f) {this.drawcolorhsb[1] = 1.0f;}
		    	else if (this.drawcolorhsb[1]<0.0f) {this.drawcolorhsb[1] = 0.0f;}
		    	Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
		    	float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
		    	this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		    }
		    boolean mousewheelshiftdown = ((!scrollevent.isControlDown())&&(!scrollevent.isAltDown())&&(scrollevent.isShiftDown())&&(!scrollevent.isMetaDown()));
		    if (mousewheelshiftdown) {
				double scrollticks = scrollevent.getDeltaX()/scrollevent.getMultiplierX();
				this.pencilangle -= 0.05f*scrollticks;
				if (this.pencilangle>(2.0f*Math.PI)) {
					this.pencilangle = 0.0f;
				} else if (this.pencilangle<0.0f) {
					this.pencilangle = 2.0f*Math.PI;
				}
		    }
		}
	}

	private void drawPencil(Graphics2D g, int mousex, int mousey, boolean erasemode, boolean overridemode) {
		g.setComposite(AlphaComposite.SrcOver);
		g.setPaint(null);
		g.setColor(null);
		int pencilwidth = (int)Math.ceil((double)(this.pencilsize-1)/2.0f);
    	if (this.pencilbuffer!=null) {
	    	if (erasemode) {
	    		if (overridemode) {
	    			g.setComposite(AlphaComposite.Clear);
	    		} else {
	    			g.setComposite(AlphaComposite.DstOut);
	    		}
	    	} else {
	    		if (overridemode) {
	    			g.setComposite(AlphaComposite.Src);
	    		}
    		}
    		double pencilsizescalefactor = ((double)this.pencilsize)/((double)this.pencilbuffer.getWidth());
    		int halfwidth = (int)Math.floor(((double)this.pencilbuffer.getWidth())*pencilsizescalefactor/2.0f);
    		int halfheight = (int)Math.floor(((double)this.pencilbuffer.getHeight())*pencilsizescalefactor/2.0f);
    		int drawlocationx = mousex - halfwidth;
    		int drawlocationy = mousey - halfheight;
    		AffineTransform penciltransform = new AffineTransform();
    		penciltransform.translate(drawlocationx, drawlocationy);
    		penciltransform.rotate(this.pencilangle,halfwidth,halfheight);
    		penciltransform.scale(pencilsizescalefactor, pencilsizescalefactor);
    		g.drawImage(this.pencilbuffer, penciltransform, null);
    	} else {
	    	if (erasemode) {
	    		g.setComposite(AlphaComposite.Src);
	    		g.setColor(this.erasecolor);
	    	} else {
	    		if (overridemode) {
		    		g.setComposite(AlphaComposite.Src);
	    		}
    			g.setColor(this.drawcolor);
	    	}
			if (this.pencilshape==2) {
				g.fillRoundRect(mousex-pencilwidth, mousey-pencilwidth, this.pencilsize, this.pencilsize, 5, 5);
			} else if (this.pencilshape==3) {
				g.fillOval(mousex-pencilwidth, mousey-pencilwidth, this.pencilsize, this.pencilsize);
			} else if (this.pencilshape==4) {
				g.drawRect(mousex-pencilwidth, mousey-pencilwidth, this.pencilsize, this.pencilsize);
			} else if (this.pencilshape==5) {
				g.drawRoundRect(mousex-pencilwidth, mousey-pencilwidth, this.pencilsize, this.pencilsize, 5, 5);
			} else if (this.pencilshape==6) {
				g.drawOval(mousex-pencilwidth, mousey-pencilwidth, this.pencilsize, this.pencilsize);
			}else {
				g.fillRect(mousex-pencilwidth, mousey-pencilwidth, this.pencilsize, this.pencilsize);
			}
    	}
	}
	private void drawPencilLine(Graphics2D g, int mousestartx, int mousestarty, int mousex, int mousey, boolean erasemode, boolean overridemode) {
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
