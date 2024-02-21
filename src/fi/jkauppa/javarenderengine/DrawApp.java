package fi.jkauppa.javarenderengine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.Transparency;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import fi.jkauppa.javarenderengine.JavaRenderEngine.AppHandlerPanel;
import fi.jkauppa.javarenderengine.UtilLib.ImageTransferable;

public class DrawApp extends AppHandlerPanel {
	private static final long serialVersionUID = 1L;
	private VolatileImage renderbuffer = null;
	private VolatileImage dragbuffer = null;
	private TexturePaint bgpattern = null;
	private Color drawcolor = Color.BLACK;
	private float[] drawcolorhsb = {0.0f, 1.0f, 0.0f};
	private Color erasecolor = new Color(1.0f,1.0f,1.0f,0.0f);
	private int pencilsize = 1;
	private int pencilshape = 1;
	private double pencilangle = 0;
	private boolean penciloverridemode = false;
	private float penciltransparency = 1.0f;
	private VolatileImage pencilbuffer = null;
	private int oldpencilsize = 1;
	private boolean drawlinemode = false;
	private int mousestartlocationx = -1, mousestartlocationy = -1;  
	private int mouselastlocationx = -1, mouselastlocationy = -1;  
	private int mouselocationx = -1, mouselocationy = -1;
	private JFileChooser filechooser = UtilLib.createImageFileChooser();
	
	public DrawApp() {
		BufferedImage bgpatternimage = gc.createCompatibleImage(64, 64, Transparency.OPAQUE);
		Graphics2D pgfx = bgpatternimage.createGraphics();
		pgfx.setColor(Color.WHITE);
		pgfx.fillRect(0, 0, bgpatternimage.getWidth(), bgpatternimage.getHeight());
		pgfx.setColor(Color.BLACK);
		pgfx.drawLine(31, 0, 31, 63);
		pgfx.drawLine(0, 31, 63, 31);
		pgfx.dispose();
		this.bgpattern = new TexturePaint(bgpatternimage,new Rectangle(0, 0, 64, 64));
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		this.setFocusTraversalKeysEnabled(false);
	}
	
	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		if ((renderbuffer==null)||(renderbuffer.getWidth()!=this.getWidth())||(renderbuffer.getHeight()!=this.getHeight())) {
			VolatileImage oldimage = this.renderbuffer;
			this.renderbuffer = gc.createCompatibleVolatileImage(this.getWidth(),this.getHeight(), Transparency.TRANSLUCENT);
			this.dragbuffer = gc.createCompatibleVolatileImage(this.getWidth(),this.getHeight(), Transparency.TRANSLUCENT);
			Graphics2D gfx = this.renderbuffer.createGraphics();
			gfx.setComposite(AlphaComposite.Clear);
			gfx.fillRect(0, 0, this.getWidth(),this.getHeight());
			if (oldimage!=null) {
				gfx.setComposite(AlphaComposite.Src);
				gfx.drawImage(oldimage, 0, 0, null);
			}
		}
		g2.setPaint(this.bgpattern);
		g2.fillRect(0, 0, renderbuffer.getWidth(), renderbuffer.getHeight());
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
	}

	@Override public void timerTick() {}
	@Override public void keyTyped(KeyEvent e) {}
	@Override public void keyReleased(KeyEvent e) {}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_ESCAPE) {
			//TODO options menu
		} else if (e.getKeyCode()==KeyEvent.VK_ENTER) {
		    int onmaska = 0;
		    int offmaska = KeyEvent.ALT_DOWN_MASK|KeyEvent.SHIFT_DOWN_MASK|KeyEvent.CTRL_DOWN_MASK;
		    boolean enterdown = (e.getModifiersEx() & (onmaska | offmaska)) == onmaska;
		    if(enterdown) {
		    	this.penciloverridemode = !this.penciloverridemode;
		    }
		} else if (e.getKeyCode()==KeyEvent.VK_BACK_SPACE) {
			if (this.renderbuffer!=null) {
				Graphics2D gfx = this.renderbuffer.createGraphics();
				gfx.setComposite(AlphaComposite.Src);
				gfx.setColor(this.erasecolor);
				gfx.fillRect(0, 0, this.renderbuffer.getWidth(), this.renderbuffer.getHeight());
				gfx.dispose();
			}
		} else if (e.getKeyCode()==KeyEvent.VK_C) {
			if (e.isControlDown()) {
				ImageTransferable transferable = new ImageTransferable(this.renderbuffer.getSnapshot());
				this.cb.setContents(transferable, null);
			}
		} else if (e.getKeyCode()==KeyEvent.VK_V) {
			if (e.isControlDown()) {
				Transferable cbt = this.cb.getContents(null);
				if ((cbt!=null)&&(cbt.isDataFlavorSupported(DataFlavor.imageFlavor))) {
			        try {
		                Image image = (Image)cbt.getTransferData(DataFlavor.imageFlavor);
						Graphics2D loadimagevolatilegfx = this.renderbuffer.createGraphics();
						loadimagevolatilegfx.setComposite(AlphaComposite.Src);
						loadimagevolatilegfx.drawImage(image, 0, 0, null);
						loadimagevolatilegfx.dispose();
			        } catch (Exception ex){ex.printStackTrace();}
				}
			}
		} else if (e.getKeyCode()==KeyEvent.VK_INSERT) {
			this.drawcolorhsb[0] += 0.01f;
			if (this.drawcolorhsb[0]>1.0f) {this.drawcolorhsb[0] = 0.0f;}
			Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
	    	if (this.pencilbuffer!=null) {
	    		this.pencilbuffer = null;
	    		this.pencilsize = this.oldpencilsize;
	    	}
		} else if (e.getKeyCode()==KeyEvent.VK_DELETE) {
			this.drawcolorhsb[0] -= 0.01f;
			if (this.drawcolorhsb[0]<0.0f) {this.drawcolorhsb[0] = 1.0f;}
			Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
	    	if (this.pencilbuffer!=null) {
	    		this.pencilbuffer = null;
	    		this.pencilsize = this.oldpencilsize;
	    	}
		} else if (e.getKeyCode()==KeyEvent.VK_HOME) {
			this.drawcolorhsb[1] += 0.01f;
			if (this.drawcolorhsb[1]>1.0f) {this.drawcolorhsb[1] = 1.0f;}
			Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
	    	if (this.pencilbuffer!=null) {
	    		this.pencilbuffer = null;
	    		this.pencilsize = this.oldpencilsize;
	    	}
		} else if (e.getKeyCode()==KeyEvent.VK_END) {
			this.drawcolorhsb[1] -= 0.01f;
			if (this.drawcolorhsb[1]<0.0f) {this.drawcolorhsb[1] = 0.0f;}
			Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
	    	if (this.pencilbuffer!=null) {
	    		this.pencilbuffer = null;
	    		this.pencilsize = this.oldpencilsize;
	    	}
		} else if (e.getKeyCode()==KeyEvent.VK_PAGE_UP) {
			this.drawcolorhsb[2] += 0.01f;
			if (this.drawcolorhsb[2]>1.0f) {this.drawcolorhsb[2] = 1.0f;}
			Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
	    	if (this.pencilbuffer!=null) {
	    		this.pencilbuffer = null;
	    		this.pencilsize = this.oldpencilsize;
	    	}
		} else if (e.getKeyCode()==KeyEvent.VK_PAGE_DOWN) {
			this.drawcolorhsb[2] -= 0.01f;
			if (this.drawcolorhsb[2]<0.0f) {this.drawcolorhsb[2] = 0.0f;}
			Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
	    	if (this.pencilbuffer!=null) {
	    		this.pencilbuffer = null;
	    		this.pencilsize = this.oldpencilsize;
	    	}
		} else if (e.getKeyCode()==KeyEvent.VK_ADD) {
			this.pencilsize += 1;
		} else if (e.getKeyCode()==KeyEvent.VK_SUBTRACT) {
			this.pencilsize -= 1;
			if (this.pencilsize<1) {this.pencilsize = 1;}
		} else if (e.getKeyCode()==KeyEvent.VK_DIVIDE) {
	    	this.pencilshape -= 1;
	    	if (this.pencilshape<1) {this.pencilshape = 6;}
	    	if (this.pencilbuffer!=null) {
	    		this.pencilbuffer = null;
	    		this.pencilsize = this.oldpencilsize;
	    	}
		} else if (e.getKeyCode()==KeyEvent.VK_MULTIPLY) {
	    	this.pencilshape += 1;
	    	if (this.pencilshape>6) {this.pencilshape = 1;}
	    	if (this.pencilbuffer!=null) {
	    		this.pencilbuffer = null;
	    		this.pencilsize = this.oldpencilsize;
	    	}
		} else if (e.getKeyCode()==KeyEvent.VK_NUMPAD9) {
			this.penciltransparency += 0.01f;
			if (this.penciltransparency>1.0f) {this.penciltransparency = 1.0f;}
			float[] colorvalues = this.drawcolor.getRGBColorComponents(new float[3]);
			this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		} else if (e.getKeyCode()==KeyEvent.VK_NUMPAD8) {
			this.penciltransparency -= 0.01f;
			if (this.penciltransparency<0.0f) {this.penciltransparency = 0.0f;}
			float[] colorvalues = this.drawcolor.getRGBColorComponents(new float[3]);
			this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		} else if (e.getKeyCode()==KeyEvent.VK_NUMPAD6) {
			this.pencilangle += 0.01f;
			if (this.pencilangle>(2.0f*Math.PI)) {this.pencilangle = 0.0f;}
		} else if (e.getKeyCode()==KeyEvent.VK_NUMPAD5) {
			this.pencilangle -= 0.01f;
			if (this.pencilangle<0.0f) {this.pencilangle = 2.0f*Math.PI;}
		} else if (e.getKeyCode()==KeyEvent.VK_F1) {
			//TODO help pop-up window
		} else if (e.getKeyCode()==KeyEvent.VK_F2) {
			this.filechooser.setDialogTitle("Save File");
			this.filechooser.setApproveButtonText("Save");
			if (this.filechooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
				File savefile = this.filechooser.getSelectedFile();
				FileFilter savefileformat = this.filechooser.getFileFilter();
				UtilLib.saveImageFormat(savefile.getPath(), this.renderbuffer, savefileformat);
			}
		} else if (e.getKeyCode()==KeyEvent.VK_F3) {
			this.filechooser.setDialogTitle("Load File");
			this.filechooser.setApproveButtonText("Load");
			if (this.filechooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
				File loadfile = this.filechooser.getSelectedFile();
				VolatileImage loadimage = UtilLib.loadImage(loadfile.getPath(), false);
				if (loadimage!=null) {
				    int onmask = KeyEvent.SHIFT_DOWN_MASK;
				    int offmask = KeyEvent.ALT_DOWN_MASK|KeyEvent.CTRL_DOWN_MASK;
				    boolean f3shiftdown = (e.getModifiersEx() & (onmask | offmask)) == onmask;
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
		} else if (e.getKeyCode()==KeyEvent.VK_F4) {
			//TODO tools/color pop-up window
		}
	}

	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	
	@Override public void mouseMoved(MouseEvent e) {this.mouselocationx=e.getX();this.mouselocationy=e.getY();}
	@Override public void mousePressed(MouseEvent e) {this.mouselocationx=e.getX();this.mouselocationy=e.getY();this.mousestartlocationx=this.mouselocationx;this.mousestartlocationy=this.mouselocationy;mouseDragged(e);}
	@Override public void mouseReleased(MouseEvent e) {
		if (this.renderbuffer!=null) {
			Graphics2D renderbuffergfx = this.renderbuffer.createGraphics();
			renderbuffergfx.setColor(this.drawcolor);
		    boolean mouse1up = e.getButton()==MouseEvent.BUTTON1;
		    boolean mouse3up = e.getButton()==MouseEvent.BUTTON3;
			if (mouse1up||mouse3up) {
				if (this.drawlinemode) {
					this.drawlinemode=false;
					drawPencilLine(renderbuffergfx, this.mousestartlocationx, this.mousestartlocationy, this.mouselocationx, this.mouselocationy, mouse3up, this.penciloverridemode);
					this.renderbuffer.contentsLost();
				}
			}
			renderbuffergfx.dispose();
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		this.mouselastlocationx=this.mouselocationx;this.mouselastlocationy=this.mouselocationy;
		this.mouselocationx=e.getX();this.mouselocationy=e.getY();
    	int mousedeltax = this.mouselocationx - this.mouselastlocationx; 
    	int mousedeltay = this.mouselocationy - this.mouselastlocationy;
		if (this.renderbuffer!=null) {
			Graphics2D renderbuffergfx = this.renderbuffer.createGraphics();
		    int onmask1 = MouseEvent.BUTTON1_DOWN_MASK;
		    int offmask1 = MouseEvent.SHIFT_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
		    int onmask3 = MouseEvent.BUTTON3_DOWN_MASK;
		    int offmask3 = MouseEvent.SHIFT_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
		    boolean mouse1down = ((e.getModifiersEx() & (onmask1 | offmask1)) == onmask1);
		    boolean mouse3down = ((e.getModifiersEx() & (onmask3 | offmask3)) == onmask3);
		    if (mouse1down||mouse3down) {
		    	this.drawPencil(renderbuffergfx, e.getX(), e.getY(), mouse3down, this.penciloverridemode);
		    	this.renderbuffer.contentsLost();
			}			
		    int onmask1c = MouseEvent.BUTTON1_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
		    int offmask1c = MouseEvent.SHIFT_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK;
		    int onmask3c = MouseEvent.BUTTON3_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
		    int offmask3c = MouseEvent.SHIFT_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK;
		    boolean mouse1altdown = ((e.getModifiersEx() & (onmask1c | offmask1c)) == onmask1c);
		    boolean mouse3altdown = ((e.getModifiersEx() & (onmask3c | offmask3c)) == onmask3c);
		    if (mouse1altdown||mouse3altdown) {
			    this.drawlinemode = true;
		    }
		    int onmask1a = MouseEvent.BUTTON1_DOWN_MASK|MouseEvent.SHIFT_DOWN_MASK;
		    int offmask1a = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
		    boolean mouse1shiftdown = ((e.getModifiersEx() & (onmask1a | offmask1a)) == onmask1a);
		    if (mouse1shiftdown) {
				int colorvalue = this.renderbuffer.getSnapshot().getRGB(e.getX(), e.getY());
				Color pickeddrawcolor = new Color(colorvalue);
				this.drawcolorhsb = Color.RGBtoHSB(pickeddrawcolor.getRed(), pickeddrawcolor.getGreen(), pickeddrawcolor.getBlue(), new float[3]);
				float[] colorvalues = pickeddrawcolor.getRGBColorComponents(new float[3]);
				this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		    }
		    int onmask1b = MouseEvent.BUTTON1_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK;
		    int offmask1b = MouseEvent.SHIFT_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
		    boolean mouse1controldown = ((e.getModifiersEx() & (onmask1b | offmask1b)) == onmask1b);
		    if (mouse1controldown) {
		    	//TODO select canvas region
		    }
		    int onmask2 = MouseEvent.BUTTON2_DOWN_MASK;
		    int offmask2 = MouseEvent.SHIFT_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
		    boolean mouse2down = ((e.getModifiersEx() & (onmask2 | offmask2)) == onmask2);
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
		    	this.renderbuffer.contentsLost();
		    }
		    int onmask2a = MouseEvent.BUTTON2_DOWN_MASK|MouseEvent.SHIFT_DOWN_MASK;
		    int offmask2a = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
		    boolean mouse2shiftdown = ((e.getModifiersEx() & (onmask2a | offmask2a)) == onmask2a);
		    if (mouse2shiftdown) {
		    	//TODO <tbd>
		    }
	    	renderbuffergfx.dispose();
		}
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
	    int onmask4 = 0;
	    int offmask4 = MouseEvent.SHIFT_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    boolean mousewheeldown = ((e.getModifiersEx() & (onmask4 | offmask4)) == onmask4);
	    if (mousewheeldown) {
	    	this.pencilsize += e.getWheelRotation()*((this.pencilsize>16)?this.pencilsize/16:1);
			if (this.pencilsize<1) {
				this.pencilsize = 1;
			}
	    }
	    int onmask4a = MouseEvent.CTRL_DOWN_MASK;
	    int offmask4a = MouseEvent.SHIFT_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    boolean mousewheelctrldown = ((e.getModifiersEx() & (onmask4a | offmask4a)) == onmask4a);
	    if (mousewheelctrldown) {
	    	this.drawcolorhsb[0] += 0.01f*e.getWheelRotation();
	    	if (this.drawcolorhsb[0]>1.0f) {this.drawcolorhsb[0] = 0.0f;}
	    	else if (this.drawcolorhsb[0]<0.0f) {this.drawcolorhsb[0] = 1.0f;}
	    	Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
	    	float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
	    	this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
	    }
	    int onmask4b = MouseEvent.ALT_DOWN_MASK;
	    int offmask4b = MouseEvent.SHIFT_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK;
	    boolean mousewheelaltdown = ((e.getModifiersEx() & (onmask4b | offmask4b)) == onmask4b);
	    if (mousewheelaltdown) {
	    	this.drawcolorhsb[2] += 0.01f*e.getWheelRotation();
	    	if (this.drawcolorhsb[2]>1.0f) {this.drawcolorhsb[2] = 1.0f;}
	    	else if (this.drawcolorhsb[2]<0.0f) {this.drawcolorhsb[2] = 0.0f;}
	    	Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
	    	float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
	    	this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
	    }
	    int onmask4c = MouseEvent.ALT_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK;
	    int offmask4c = MouseEvent.SHIFT_DOWN_MASK;
	    boolean mousewheelctrlaltdown = ((e.getModifiersEx() & (onmask4c | offmask4c)) == onmask4c);
	    if (mousewheelctrlaltdown) {
	    	this.drawcolorhsb[1] += 0.01f*e.getWheelRotation();
	    	if (this.drawcolorhsb[1]>1.0f) {this.drawcolorhsb[1] = 1.0f;}
	    	else if (this.drawcolorhsb[1]<0.0f) {this.drawcolorhsb[1] = 0.0f;}
	    	Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
	    	float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
	    	this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
	    }
	    int onmask4d = MouseEvent.SHIFT_DOWN_MASK;
	    int offmask4d = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    boolean mousewheelshiftdown = ((e.getModifiersEx() & (onmask4d | offmask4d)) == onmask4d);
	    if (mousewheelshiftdown) {
			this.pencilangle += 0.05f*e.getWheelRotation();
			if (this.pencilangle>(2.0f*Math.PI)) {
				this.pencilangle = 0.0f;
			} else if (this.pencilangle<0.0f) {
				this.pencilangle = 2.0f*Math.PI;
			}
	    }
	}

	@SuppressWarnings("unchecked")
	@Override public void drop(DropTargetDropEvent dtde) {
		dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
		Transferable dtt = dtde.getTransferable();
        try {
            if ((dtt!=null)&&(dtt.isDataFlavorSupported(DataFlavor.javaFileListFlavor))) {
				List<File> files = (List<File>)dtt.getTransferData(DataFlavor.javaFileListFlavor);
                for (Iterator<File> i=files.iterator();i.hasNext();) {
                	File file = i.next();
                    VolatileImage loadimage = UtilLib.loadImage(file.getPath(), false);
			    	this.oldpencilsize = this.pencilsize;
					this.pencilsize = loadimage.getWidth();
					this.pencilbuffer = loadimage;
                }
            }
        } catch (Exception ex){ex.printStackTrace();}
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
