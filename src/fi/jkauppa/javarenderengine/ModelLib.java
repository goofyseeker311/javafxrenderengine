package fi.jkauppa.javarenderengine;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import fi.jkauppa.javarenderengine.UtilLib.ImageFileFilters.PNGFileFilter;

public class ModelLib {
	public static class Material implements Comparable<Material> {
		public String materialname = null;
		public int materialid = -1;
		public int imageid = -1;
		public BufferedImage fileimage = null;
		public BufferedImage ambientfileimage = null;
		public BufferedImage specularfileimage = null;
		public BufferedImage specularhighfileimage = null;
		public BufferedImage emissivefileimage = null;
		public BufferedImage alphafileimage = null;
		public BufferedImage roughnessfileimage = null;
		public BufferedImage metallicfileimage = null;
		public BufferedImage sheenfileimage = null;
		public BufferedImage bumpfileimage = null;
		public BufferedImage dispfileimage = null;
		public BufferedImage decalfileimage = null;
		public String filename = null;
		public Color facecolor = null;
		public Color ambientcolor = null;
		public Color specularcolor = null;
		public Color emissivecolor = null;
		public float specularexp = 250.0f;
		public float emissivity = 0.0f;
		public float transparency = 1.0f;
		public float roughness = 0.0f;
		public float metallic = 0.0f;
		public float sheen = 0.0f;
		public float coatthickness = 0.0f;
		public float coatroughtness = 0.0f;
		public float anisotropy = 0.0f;
		public float anisotropyrot = 0.0f;
		public float refraction = 1.45f;
		public Material() {}
		public Material(Color facecolori, float transparencyi, BufferedImage fileimagei) {this.facecolor=facecolori;this.transparency=transparencyi;this.fileimage=fileimagei;}
		@Override public int compareTo(Material o) {
			int k=-1;
			ColorComparator colorcomp = new ColorComparator();
			ImageComparator imagecomp = new ImageComparator();
			k = colorcomp.compare(this.facecolor, o.facecolor);
			if (k==0) { k = imagecomp.compare(this.fileimage, o.fileimage); }
			if (k==0) { k = colorcomp.compare(this.ambientcolor, o.ambientcolor); }
			if (k==0) { k = imagecomp.compare(this.ambientfileimage, o.ambientfileimage); }
			if (k==0) { k = colorcomp.compare(this.emissivecolor, o.emissivecolor); }
			if (k==0) { k = imagecomp.compare(this.emissivefileimage, o.emissivefileimage); }
			if (k==0) { k = colorcomp.compare(this.specularcolor, o.specularcolor); }
			if (k==0) { k = imagecomp.compare(this.specularfileimage, o.specularfileimage); }
			return k;
		}
		@Override public boolean equals(Object o) {
			boolean k=false;
			if ((o!=null)&&(o.getClass().equals(this.getClass()))){
				Material co=(Material)o;
				if(this.compareTo(co)==0){
					k=true;
				}
			}
			return k;
		}
		public Material copy(){
			Material k = new Material();
			k.materialname = this.materialname;
			k.fileimage = this.fileimage;
			k.ambientfileimage = this.ambientfileimage;
			k.specularfileimage = this.specularfileimage;
			k.specularhighfileimage = this.specularhighfileimage;
			k.emissivefileimage = this.emissivefileimage;
			k.alphafileimage = this.alphafileimage;
			k.roughnessfileimage = this.roughnessfileimage;
			k.metallicfileimage = this.metallicfileimage;
			k.sheenfileimage = this.sheenfileimage;
			k.bumpfileimage = this.bumpfileimage;
			k.dispfileimage = this.dispfileimage;
			k.decalfileimage = this.decalfileimage;
			k.filename = this.filename;
			k.facecolor = this.facecolor;
			k.ambientcolor = this.ambientcolor;
			k.specularcolor = this.specularcolor;
			k.emissivecolor = this.emissivecolor;
			k.specularexp = this.specularexp;
			k.emissivity = this.emissivity;
			k.transparency = this.transparency;
			k.roughness = this.roughness;
			k.metallic = this.metallic;
			k.sheen = this.sheen;
			k.coatthickness = this.coatthickness;
			k.coatroughtness = this.coatroughtness;
			k.anisotropy = this.anisotropy;
			k.anisotropyrot = this.anisotropyrot;
			k.refraction = this.refraction;
			return k;
		}
	}

	public static class ColorComparator implements Comparator<Color> {
		@Override public int compare(Color o1, Color o2) {
			int k = -1;
			if (o1==o2) {
				k=0;
			} else if((o1!=null)&&(o2!=null)) {
				float[] o1ccomp = o1.getRGBComponents(new float[4]);
				float[] o2ccomp = o2.getRGBComponents(new float[4]);
				if (o1ccomp[0]>o2ccomp[0]) {
					k=1;
				} else if (o1ccomp[0]==o2ccomp[0]) {
					if (o1ccomp[1]>o2ccomp[1]){
						k=1;
					} else if (o1ccomp[1]==o2ccomp[1]) {
						if (o1ccomp[2]>o2ccomp[2]) {
							k=1;
						} else if(o1ccomp[2]==o2ccomp[2]) {
							if (o1ccomp[3]>o2ccomp[3]) {
								k=1;
							} else if (o1ccomp[3]==o2ccomp[3]) {
								k = 0;
							}
						}
					}
				}
			} else if (o1!=null) {
				k=1;
			}
			return k;
		}
	}
	public static class ImageComparator implements Comparator<Image> {
		@Override public int compare(Image o1, Image o2) {
			int k = -1;
			if (o1==o2) {
				k=0;
			} else if((o1!=null)&&(o2!=null)) {
				k=o1.toString().compareTo(o2.toString());
			} else if (o1!=null) {
				k=1;
			}
			return k;
		}
	}
	public static class SphereComparator implements Comparator<Sphere> {
		public Position origin = new Position(0.0f,0.0f,0.0f);
		public SphereComparator(Position origini) {this.origin = origini;}
		@Override public int compare(Sphere o1, Sphere o2) {
			int k = -1;
			Sphere[] spheres = {o1,o2};
			Direction[] spheredir = MathLib.vectorFromPoints(this.origin, spheres);
			double[] spheredist = MathLib.vectorLength(spheredir);
			if (spheredist[0]>spheredist[1]) {
				k = 1;
			} else if (spheredist[0]==spheredist[1]) {
				k = 0;
			}
			return k;
		}

	}

	public static class RenderView {
		public BufferedImage renderimage = null;
		public Object renderimageobject = null;
		public Cubemap cubemap = null;
		public Spheremap spheremap = null;
		public double[][] sbuffer = null;
		public double[][] zbuffer = null;
		public Entity[][] ebuffer = null;
		public Triangle[][] tbuffer = null;
		public Direction[][] nbuffer = null;
		public Coordinate[][] cbuffer = null;
		public Entity[] mouseoverentity = null;
		public Triangle[] mouseovertriangle = null;
		public Position[] mouseoververtex = null;
		public Line[] mouseoverline = null;
		public int mouselocationx=0,mouselocationy=0; 
		public Position pos = null;
		public Matrix rot = null;
		public Plane[] nearclipplane = null;
		public int renderwidth=0, renderheight=0;
		public int rendersize=0;
		public double hfov=0.0f, vfov=0.0f;
		public boolean rendered = false;
		public boolean unlit = false;
		public Direction[] dirs = null;
		public Direction[][] rays = null;
		public Ray[][] vrays = null;
		public Plane[] planes = null;
		public PlaneRay[] planerays = null;
		public Direction[] fwddirs = null;
	}

	public static class Cubemap {
		public RenderView topview=null,bottomview=null,leftview=null,rightview=null,forwardview=null,backwardview=null;
	}
	public static class Spheremap {
		public RenderView sphereview=null;
	}

	public static class Position implements Comparable<Position> {public double x=0,y=0,z=0; public Coordinate tex=new Coordinate(0.0f,0.0f); public Material mat=new Material(); public Object hwent=null, hwpos=null; public Position(double xi,double yi,double zi){this.x=xi;this.y=yi;this.z=zi;}
	@Override public int compareTo(Position o){
		int k = -1;
		if (this.z>o.z) {
			k = 1;
		} else if (this.z==o.z) {
			if (this.y>o.y) {
				k = 1;
			} else if (this.y==o.y) {
				if (this.x>o.x) {
					k = 1;
				} else if (this.x==o.x) {
					k = 0;
				}
			}
		}
		return k;
	}
	@Override public boolean equals(Object o) {
		boolean k = false;
		if ((o!=null)&&(o.getClass().equals(this.getClass()))) {
			Position os = (Position)o;
			if ((this.x==os.x)&&(this.y==os.y)&&(this.z==os.z)) {
				k = true;
			}
		}
		return k;
	}
	public Position copy(){Position k=new Position(this.x,this.y,this.z); k.tex=this.tex.copy();k.mat=this.mat.copy();k.hwent=this.hwent;k.hwpos=this.hwpos; return k;}
	public Position invert(){Position k=this.copy(); k.x=-k.x;k.y=-k.y;k.z=-k.z; return k;}
	public boolean isZero(){return (this.x==0.0f)&&(this.y==0.0f)&&(this.z==0.0f);}
	public boolean isFinite(){return (Double.isFinite(this.x))&&(Double.isFinite(this.y))&&(Double.isFinite(this.z));}
	public void setValue(Position value) {this.x=value.x;this.y=value.y;this.z=value.z;}
	public void translateSelf(Position pos) {setValue(translate(pos));}
	public void translateSelf(Direction dir, double mult) {setValue(translate(dir,mult));}
	public Position translate(Position pos) {Position[]k={this};k=MathLib.translate(k,pos);return k[0];}
	public Position translate(Direction dir, double mult) {Position[]k={this};k=MathLib.translate(k,dir,mult);return k[0];}
	public void rotateSelfAroundAxisPos(Position pos, Direction axis, double angle) {setValue(rotateAroundAxisPos(pos,axis,angle));}
	public Position rotateAroundAxisPos(Position pos, Direction axis, double angle) {Position[]k={this};k=MathLib.rotateAroundAxisPos(k,pos,axis,angle);return k[0];}
	public void scaleSelfAroundPos(Position pos, Scaling scale) {setValue(scaleAroundPos(pos,scale));}
	public Position scaleAroundPos(Position pos, Scaling scale) {Position[]k={this};k=MathLib.scaleAroundPos(k,pos,scale);return k[0];}
	}
	public static class Direction implements Comparable<Direction> {public double dx=0,dy=0,dz=0; public Direction(double dxi,double dyi,double dzi){this.dx=dxi;this.dy=dyi;this.dz=dzi;}
	@Override public int compareTo(Direction o){
		int k = -1;
		if (this.dz>o.dz) {
			k = 1;
		} else if (this.dz==o.dz) {
			if (this.dy>o.dy) {
				k = 1;
			} else if (this.dy==o.dy) {
				if (this.dx>o.dx) {
					k = 1;
				} else if (this.dx==o.dx) {
					k = 0;
				}
			}
		}
		return k;
	}
	@Override public boolean equals(Object o) {
		boolean k = false;
		if ((o!=null)&&(o.getClass().equals(this.getClass()))) {
			Direction os = (Direction)o;
			if ((this.dx==os.dx)&&(this.dy==os.dy)&&(this.dz==os.dz)) {
				k = true;
			}
		}
		return k;
	}
	public Direction copy(){return new Direction(this.dx,this.dy,this.dz);}
	public Direction invert(){return new Direction(-this.dx,-this.dy,-this.dz);}
	public boolean isZero(){return (this.dx==0.0f)&&(this.dy==0.0f)&&(this.dz==0.0f);}
	public boolean isFinite(){return (Double.isFinite(this.dx))&&(Double.isFinite(this.dy))&&(Double.isFinite(this.dz));}
	public void setValue(Direction value) {this.dx=value.dx;this.dy=value.dy;this.dz=value.dz;}
	public void translateSelf(Position pos) {setValue(translate(pos));}
	public void translateSelf(Direction dir, double mult) {setValue(translate(dir,mult));}
	public Direction translate(Position pos) {Direction[]k={this};k=MathLib.translate(k,pos);return k[0];}
	public Direction translate(Direction dir, double mult) {Direction[]k={this};k=MathLib.translate(k,dir,mult);return k[0];}
	public void rotateSelfAroundAxisPos(Position pos, Direction axis, double angle) {setValue(rotateAroundAxisPos(pos,axis,angle));}
	public Direction rotateAroundAxisPos(Position pos, Direction axis, double angle) {Direction[]k={this};k=MathLib.rotateAroundAxisPos(k,pos,axis,angle);return k[0];}
	public void scaleSelfAroundPos(Position pos, Scaling scale) {setValue(scaleAroundPos(pos,scale));}
	public Direction scaleAroundPos(Position pos, Scaling scale) {Direction[]k={this};k=MathLib.scaleAroundPos(k,pos,scale);return k[0];}
	}
	public static class Axis { public Position pos; public Direction fwd=new Direction(1.0f,0.0f,0.0f),rgt=new Direction(0.0f,1.0f,0.0f),up=new Direction(0.0f,0.0f,1.0f); public Axis(Position posi, Direction fwdi, Direction rgti, Direction upi){this.pos=posi;this.fwd=fwdi;this.rgt=rgti;this.up=upi;}
	public Axis copy(){return new Axis(this.pos.copy(),this.fwd.copy(),this.rgt.copy(),this.up.copy());}
	public void setValue(Axis value) {this.pos=value.pos;this.fwd=value.fwd;this.rgt=value.rgt;this.up=value.up;}
	public void translateSelf(Position pos) {setValue(translate(pos));}
	public void translateSelf(Direction dir, double mult) {setValue(translate(dir,mult));}
	public Axis translate(Position pos) {Axis[]k={this};k=MathLib.translate(k,pos);return k[0];}
	public Axis translate(Direction dir, double mult) {Axis[]k={this};k=MathLib.translate(k,dir,mult);return k[0];}
	public void rotateSelfAroundAxisPos(Position pos, Direction axis, double angle) {setValue(rotateAroundAxisPos(pos,axis,angle));}
	public Axis rotateAroundAxisPos(Position pos, Direction axis, double angle) {Axis[]k={this};k=MathLib.rotateAroundAxisPos(k,pos,axis,angle);return k[0];}
	public void scaleSelfAroundPos(Position pos, Scaling scale) {setValue(scaleAroundPos(pos,scale));}
	public Axis scaleAroundPos(Position pos, Scaling scale) {Axis[]k={this};k=MathLib.scaleAroundPos(k,pos,scale);return k[0];}
	}
	public static class Coordinate implements Comparable<Coordinate> {public double u=0,v=0; public Coordinate(double ui,double vi){this.u=ui;this.v=vi;}
	@Override public int compareTo(Coordinate o){
		int k = -1;
		if (this.u>o.u) {
			k = 1;
		} else if (this.u==o.u) {
			if (this.v>o.v) {
				k = 1;
			} else if (this.v==o.v) {
				k = 0;
			}
		}
		return k;
	}
	@Override public boolean equals(Object o) {
		boolean k = false;
		if ((o!=null)&&(o.getClass().equals(this.getClass()))) {
			Coordinate os = (Coordinate)o;
			if ((this.u==os.u)&&(this.v==os.v)) {
				k = true;
			}
		}
		return k;
	}
	public Coordinate copy(){return new Coordinate(this.u,this.v);}
	public Coordinate invert(){return new Coordinate(-this.u,-this.v);}
	public boolean isZero(){return (this.u==0.0f)&&(this.v==0.0f);}
	public boolean isFinite(){return (Double.isFinite(this.u))&&(Double.isFinite(this.v));}
	public void setValue(Coordinate value) {this.u=value.u;this.v=value.v;}
	public void translateSelf(Position pos) {setValue(translate(pos));}
	public void translateSelf(Direction dir, double mult) {setValue(translate(dir,mult));}
	public Coordinate translate(Position pos) {Coordinate[]k={this};k=MathLib.translate(k,pos);return k[0];}
	public Coordinate translate(Direction dir, double mult) {Coordinate[]k={this};k=MathLib.translate(k,dir,mult);return k[0];}
	public void rotateSelfAroundAxisPos(Position pos, Direction axis, double angle) {setValue(rotateAroundAxisPos(pos,axis,angle));}
	public Coordinate rotateAroundAxisPos(Position pos, Direction axis, double angle) {Coordinate[]k={this};k=MathLib.rotateAroundAxisPos(k,pos,axis,angle);return k[0];}
	public void scaleSelfAroundPos(Position pos, Scaling scale) {setValue(scaleAroundPos(pos,scale));}
	public Coordinate scaleAroundPos(Position pos, Scaling scale) {Coordinate[]k={this};k=MathLib.scaleAroundPos(k,pos,scale);return k[0];}
	}
	public static class Rotation {public double x,y,z; public Rotation(double xi,double yi,double zi){this.x=xi;this.y=yi;this.z=zi;}
	@Override public boolean equals(Object o) {
		boolean k = false;
		if ((o!=null)&&(o.getClass().equals(this.getClass()))) {
			Rotation os = (Rotation)o;
			if ((this.x==os.x)&&(this.y==os.y)&&(this.z==os.z)) {
				k = true;
			}
		}
		return k;
	}
	public Rotation copy(){return new Rotation(this.x,this.y,this.z);}
	}
	public static class Scaling {public double x,y,z; public Scaling(double xi,double yi,double zi){this.x=xi;this.y=yi;this.z=zi;}
	@Override public boolean equals(Object o) {
		boolean k = false;
		if ((o!=null)&&(o.getClass().equals(this.getClass()))) {
			Rotation os = (Rotation)o;
			if ((this.x==os.x)&&(this.y==os.y)&&(this.z==os.z)) {
				k = true;
			}
		}
		return k;
	}
	public Scaling copy(){return new Scaling(this.x,this.y,this.z);}
	}
	public static class Sphere implements Comparable<Sphere> {public double x=0,y=0,z=0,r=0; public Sphere(double xi,double yi,double zi,double ri){this.x=xi;this.y=yi;this.z=zi;this.r=ri;}
	@Override public int compareTo(Sphere o) {
		int k = -1;
		if (this.z>o.z) {
			k = 1;
		} else if (this.z==o.z) {
			if (this.y>o.y) {
				k = 1;
			} else if (this.y==o.y) {
				if (this.x>o.x) {
					k = 1;
				} else if (this.x==o.x) {
					if (this.r>o.r) {
						k = 1;
					} else if (this.r==o.r) {
						k = 0;
					}
				}
			}
		}
		return k;
	}
	@Override public boolean equals(Object o) {
		boolean k = false;
		if ((o!=null)&&(o.getClass().equals(this.getClass()))) {
			Sphere os = (Sphere)o;
			if ((this.x==os.x)&&(this.y==os.y)&&(this.z==os.z)&&(this.r==os.r)) {
				k = true;
			}
		}
		return k;
	}
	public Sphere copy(){Sphere k = new Sphere(this.x,this.y,this.z,this.r); return k;}
	public void setValue(Sphere value) {this.x=value.x;this.y=value.y;this.z=value.z;this.r=value.r;}
	public void translateSelf(Position pos) {setValue(translate(pos));}
	public void translateSelf(Direction dir, double mult) {setValue(translate(dir,mult));}
	public Sphere translate(Position pos) {Sphere[]k={this};k=MathLib.translate(k,pos);return k[0];}
	public Sphere translate(Direction dir, double mult) {Sphere[]k={this};k=MathLib.translate(k,dir,mult);return k[0];}
	public void rotateSelfAroundAxisPos(Position pos, Direction axis, double angle) {setValue(rotateAroundAxisPos(pos,axis,angle));}
	public Sphere rotateAroundAxisPos(Position pos, Direction axis, double angle) {Sphere[]k={this};k=MathLib.rotateAroundAxisPos(k,pos,axis,angle);return k[0];}
	public void scaleSelfAroundPos(Position pos, Scaling scale) {setValue(scaleAroundPos(pos,scale));}
	public Sphere scaleAroundPos(Position pos, Scaling scale) {Sphere[]k={this};k=MathLib.scaleAroundPos(k,pos,scale);return k[0];}
	}
	public static class Ellipsoid {public double x=0,y=0,z=0,rx=0,ry=0,rz=0; public Ellipsoid(double xi,double yi,double zi,double rxi,double ryi,double rzi){this.x=xi;this.y=yi;this.z=zi;this.rx=rxi;this.ry=ryi;this.rz=rzi;}}
	public static class Cube {public Axis dim=new Axis(new Position(0.0f,0.0f,0.0f),new Direction(0.0f,0.0f,0.0f),new Direction(0.0f,0.0f,0.0f),new Direction(0.0f,0.0f,0.0f)); public Cube(Axis dimi){this.dim=dimi;}
	public Cube copy(){Cube k=new Cube(this.dim.copy()); return k;}
	public void setValue(Cube value) {this.dim=value.dim;}
	public void translateSelf(Position pos) {setValue(translate(pos));}
	public void translateSelf(Direction dir, double mult) {setValue(translate(dir,mult));}
	public Cube translate(Position pos) {Cube[]k={this};k=MathLib.translate(k,pos);return k[0];}
	public Cube translate(Direction dir, double mult) {Cube[]k={this};k=MathLib.translate(k,dir,mult);return k[0];}
	public void rotateSelfAroundAxisPos(Position pos, Direction axis, double angle) {setValue(rotateAroundAxisPos(pos,axis,angle));}
	public Cube rotateAroundAxisPos(Position pos, Direction axis, double angle) {Cube[]k={this};k=MathLib.rotateAroundAxisPos(k,pos,axis,angle);return k[0];}
	public void scaleSelfAroundPos(Position pos, Scaling scale) {setValue(scaleAroundPos(pos,scale));}
	public Cube scaleAroundPos(Position pos, Scaling scale) {Cube[]k={this};k=MathLib.scaleAroundPos(k,pos,scale);return k[0];}
	}
	public static class Cuboid {public Position pos1=new Position(0.0f,0.0f,0.0f),pos2=new Position(0.0f,0.0f,0.0f),pos3=new Position(0.0f,0.0f,0.0f),pos4=new Position(0.0f,0.0f,0.0f),pos5=new Position(0.0f,0.0f,0.0f),pos6=new Position(0.0f,0.0f,0.0f),pos7=new Position(0.0f,0.0f,0.0f),pos8=new Position(0.0f,0.0f,0.0f); public Cuboid(Position pos1i,Position pos2i,Position pos3i,Position pos4i,Position pos5i,Position pos6i,Position pos7i,Position pos8i){this.pos1=pos1i;this.pos2=pos2i;this.pos3=pos3i;this.pos4=pos4i;this.pos5=pos5i;this.pos6=pos6i;this.pos7=pos7i;this.pos8=pos8i;}
	public Cuboid copy(){Cuboid k = new Cuboid(this.pos1.copy(),this.pos2.copy(),this.pos3.copy(),this.pos4.copy(),this.pos5.copy(),this.pos6.copy(),this.pos7.copy(),this.pos8.copy()); return k;}
	public void setValue(Cuboid value) {this.pos1=value.pos1;this.pos2=value.pos2;this.pos3=value.pos3;this.pos4=value.pos4;this.pos5=value.pos5;this.pos6=value.pos6;this.pos7=value.pos7;this.pos8=value.pos8;}
	public void translateSelf(Position pos) {setValue(translate(pos));}
	public void translateSelf(Direction dir, double mult) {setValue(translate(dir,mult));}
	public Cuboid translate(Position pos) {Cuboid[]k={this};k=MathLib.translate(k,pos);return k[0];}
	public Cuboid translate(Direction dir, double mult) {Cuboid[]k={this};k=MathLib.translate(k,dir,mult);return k[0];}
	public void rotateSelfAroundAxisPos(Position pos, Direction axis, double angle) {setValue(rotateAroundAxisPos(pos,axis,angle));}
	public Cuboid rotateAroundAxisPos(Position pos, Direction axis, double angle) {Cuboid[]k={this};k=MathLib.rotateAroundAxisPos(k,pos,axis,angle);return k[0];}
	public void scaleSelfAroundPos(Position pos, Scaling scale) {setValue(scaleAroundPos(pos,scale));}
	public Cuboid scaleAroundPos(Position pos, Scaling scale) {Cuboid[]k={this};k=MathLib.scaleAroundPos(k,pos,scale);return k[0];}
	}
	public static class Quad {public Position pos1=new Position(0.0f,0.0f,0.0f),pos2=new Position(0.0f,0.0f,0.0f),pos3=new Position(0.0f,0.0f,0.0f),pos4=new Position(0.0f,0.0f,0.0f); public Direction norm=new Direction(0.0f,0.0f,0.0f); public Material mat=new Material(); public Material[] lmatl=null; public Quad(Position pos1i,Position pos2i,Position pos3i,Position pos4i){this.pos1=pos1i;this.pos2=pos2i;this.pos3=pos3i;this.pos4=pos4i;}
	public Quad copy(){Quad k = new Quad(this.pos1.copy(),this.pos2.copy(),this.pos3.copy(),this.pos4.copy()); k.norm=this.norm.copy();k.mat=this.mat.copy();k.lmatl=this.lmatl; return k;}
	public void setValue(Quad value) {this.pos1=value.pos1;this.pos2=value.pos2;this.pos3=value.pos3;this.pos4=value.pos4;}
	public void translateSelf(Position pos) {setValue(translate(pos));}
	public void translateSelf(Direction dir, double mult) {setValue(translate(dir,mult));}
	public Quad translate(Position pos) {Quad[]k={this};k=MathLib.translate(k,pos);return k[0];}
	public Quad translate(Direction dir, double mult) {Quad[]k={this};k=MathLib.translate(k,dir,mult);return k[0];}
	public void rotateSelfAroundAxisPos(Position pos, Direction axis, double angle) {setValue(rotateAroundAxisPos(pos,axis,angle));}
	public Quad rotateAroundAxisPos(Position pos, Direction axis, double angle) {Quad[]k={this};k=MathLib.rotateAroundAxisPos(k,pos,axis,angle);return k[0];}
	public void scaleSelfAroundPos(Position pos, Scaling scale) {setValue(scaleAroundPos(pos,scale));}
	public Quad scaleAroundPos(Position pos, Scaling scale) {Quad[]k={this};k=MathLib.scaleAroundPos(k,pos,scale);return k[0];}
	}
	public static class Ellipse {public double x=0,y=0,z=0,rx=0,ry=0,rz=0; public Ellipse(double xi, double yi, double zi, double rxi, double ryi, double rzi){this.x=xi;this.y=yi;this.z=zi;this.rx=rxi;this.ry=ryi;this.rz=rzi;}}
	public static class Arc {public double x=0,y=0,z=0,r=0,ang1=0,ang2=0; public Arc(double xi, double yi, double zi, double ri, double ang1i, double ang2i){this.x=xi;this.y=yi;this.z=zi;this.r=ri;this.ang1=ang1i;this.ang2=ang2i;}}
	public static class Circle {public Position origin=new Position(0.0f,0.0f,0.0f); public double r=0; public Circle(Position origini, double ri){this.origin=origini;this.r=ri;}}
	public static class Cylinder {public Axis dim=new Axis(new Position(0.0f,0.0f,0.0f),new Direction(1.0f,0.0f,0.0f),new Direction(1.0f,0.0f,0.0f),new Direction(1.0f,0.0f,0.0f)); public Cylinder(Axis dimi){this.dim=dimi;}}
	public static class Ray {public Position pos=new Position(0.0f,0.0f,0.0f); public Direction dir=new Direction(0.0f,0.0f,0.0f); public Ray(Position posi, Direction diri){this.pos=posi;this.dir=diri;}
	public Ray copy(){Ray k = new Ray(this.pos.copy(),this.dir.copy()); return k;}
	public Ray invert(){return new Ray(this.pos, this.dir.invert());}
	public void setValue(Ray value) {this.pos=value.pos;this.dir=value.dir;}
	public void translateSelf(Position pos) {setValue(translate(pos));}
	public void translateSelf(Direction dir, double mult) {setValue(translate(dir,mult));}
	public Ray translate(Position pos) {Position[]k={this.pos};k=MathLib.translate(k,pos);return new Ray(k[0],this.dir);}
	public Ray translate(Direction dir, double mult) {Position[]k={this.pos};k=MathLib.translate(k,dir,mult);return new Ray(k[0],this.dir);}
	public void rotateSelfAroundAxisPos(Position pos, Direction axis, double angle) {setValue(rotateAroundAxisPos(pos,axis,angle));}
	public Ray rotateAroundAxisPos(Position pos, Direction axis, double angle) {Ray[]k={this};k=MathLib.rotateAroundAxisPos(k,pos,axis,angle);return k[0];}
	public void scaleSelfAroundPos(Position pos, Scaling scale) {setValue(scaleAroundPos(pos,scale));}
	public Ray scaleAroundPos(Position pos, Scaling scale) {Ray[]k={this};k=MathLib.scaleAroundPos(k,pos,scale);return k[0];}
	}
	public static class PlaneRay {public Position pos=new Position(0.0f,0.0f,0.0f); public Direction dir=new Direction(0.0f,0.0f,0.0f); public Plane plane=new Plane(0, 0, 0, 0); public double vfov=0; public PlaneRay(Position posi, Direction diri, Plane planei, double vfovi){this.pos=posi;this.dir=diri;this.plane=planei;this.vfov=vfovi;}
	public PlaneRay copy(){PlaneRay k=new PlaneRay(this.pos.copy(),this.dir.copy(),this.plane.copy(),this.vfov); return k;}
	public void setValue(PlaneRay value) {this.pos=value.pos;this.dir=value.dir;this.plane=value.plane;this.vfov=value.vfov;}
	public void translateSelf(Position pos) {setValue(translate(pos));}
	public void translateSelf(Direction dir, double mult) {setValue(translate(dir,mult));}
	public PlaneRay translate(Position pos) {Position[]k={this.pos};Plane[]k2={this.plane};k=MathLib.translate(k,pos);k2=MathLib.translate(k2,pos);return new PlaneRay(k[0],this.dir,k2[0],this.vfov);}
	public PlaneRay translate(Direction dir, double mult) {Position[]k={this.pos};Plane[]k2={this.plane};k=MathLib.translate(k,dir,mult);k2=MathLib.translate(k2,dir,mult);return new PlaneRay(k[0],this.dir,k2[0],this.vfov);}
	public void rotateSelfAroundAxisPos(Position pos, Direction axis, double angle) {setValue(rotateAroundAxisPos(pos,axis,angle));}
	public PlaneRay rotateAroundAxisPos(Position pos, Direction axis, double angle) {PlaneRay[]k={this};k=MathLib.rotateAroundAxisPos(k,pos,axis,angle);return k[0];}
	public void scaleSelfAroundPos(Position pos, Scaling scale) {setValue(scaleAroundPos(pos,scale));}
	public PlaneRay scaleAroundPos(Position pos, Scaling scale) {PlaneRay[]k={this};k=MathLib.scaleAroundPos(k,pos,scale);return k[0];}
	}
	public static class Plane {public double a=0,b=0,c=0,d=0; public Plane(double ai,double bi,double ci,double di){this.a=ai;this.b=bi;this.c=ci;this.d=di;}
	public Plane copy(){Plane k = new Plane(this.a,this.b,this.c,this.d); return k;}
	public Plane invert(){return new Plane(-this.a,-this.b,-this.c,-this.d);}
	public boolean isFinite(){return (Double.isFinite(this.a))&&(Double.isFinite(this.b))&&(Double.isFinite(this.c))&&(Double.isFinite(this.d));}
	public void setValue(Plane value) {this.a=value.a;this.b=value.b;this.c=value.c;this.d=value.d;}
	public void translateSelf(Position pos) {setValue(translate(pos));}
	public void translateSelf(Direction dir, double mult) {setValue(translate(dir,mult));}
	public Plane translate(Position pos) {Plane[]k={this};k=MathLib.translate(k,pos);return k[0];}
	public Plane translate(Direction dir, double mult) {Plane[]k={this};k=MathLib.translate(k,dir,mult);return k[0];}
	public void rotateSelfAroundAxisPos(Position pos, Direction axis, double angle) {setValue(rotateAroundAxisPos(pos,axis,angle));}
	public Plane rotateAroundAxisPos(Position pos, Direction axis, double angle) {Plane[]k={this};k=MathLib.rotateAroundAxisPos(k,pos,axis,angle);return k[0];}
	public void scaleSelfAroundPos(Position pos, Scaling scale) {setValue(scaleAroundPos(pos,scale));}
	public Plane scaleAroundPos(Position pos, Scaling scale) {Plane[]k={this};k=MathLib.scaleAroundPos(k,pos,scale);return k[0];}
	}
	public static class Line implements Comparable<Line> {public Position pos1=new Position(0.0f,0.0f,0.0f),pos2=new Position(0.0f,0.0f,0.0f); public Material mat=new Material(); public Object hwent=null, hwline=null; public Line(Position pos1i,Position pos2i){this.pos1=pos1i;this.pos2=pos2i;}
	@Override public int compareTo(Line o){
		int k=-1;
		Line ts=this.sort();
		Line os=o.sort();
		if (ts.pos1.z>os.pos1.z) {
			k=1;
		}else if (ts.pos1.z==os.pos1.z) {
			if(ts.pos2.z>os.pos2.z) {
				k=1;
			} else if (ts.pos2.z==os.pos2.z) {
				if(ts.pos1.y>os.pos1.y){
					k=1;
				}else if (ts.pos1.y==os.pos1.y) {
					if(ts.pos2.y>os.pos2.y) {
						k=1;
					} else if (ts.pos2.y==os.pos2.y) {
						if(ts.pos1.x>os.pos1.x) {
							k=1;
						} else if (ts.pos1.x==os.pos1.x) {
							if (ts.pos2.x>os.pos2.x) {
								k=1;
							} else if (ts.pos2.x==os.pos2.x) {
								k=0;
							}}}}}}
		return k;
	}
	@Override public boolean equals(Object o) {
		boolean k = false;
		if ((o!=null)&&(o.getClass().equals(this.getClass()))) {
			Line os = ((Line)o).sort();
			Line ts=this.sort();
			if ((ts.pos1.x==os.pos1.x)&&(ts.pos1.y==os.pos1.y)&&(ts.pos1.z==os.pos1.z)&&(ts.pos2.x==os.pos2.x)&&(ts.pos2.y==os.pos2.y)&&(ts.pos2.z==os.pos2.z)) {
				k = true;
			}
		}
		return k;
	}
	public Line copy(){Line k = new Line(this.pos1.copy(),this.pos2.copy()); k.mat=this.mat.copy();k.hwent=this.hwent;k.hwline=this.hwline; return k;}
	public Line swap(){return new Line(this.pos2,this.pos1);}
	public Line sort(){Line k=this;if (this.pos1.compareTo(this.pos2)==1) {k=this.swap();}return k;}
	public boolean isFinite(){return (this.pos1!=null)&&(this.pos2!=null)&&(this.pos1.isFinite())&&(this.pos2.isFinite());}
	public void setValue(Line value) {this.pos1=value.pos1;this.pos2=value.pos2;}
	public void translateSelf(Position pos) {setValue(translate(pos));}
	public void translateSelf(Direction dir, double mult) {setValue(translate(dir,mult));}
	public Line translate(Position pos) {Line[]k={this};k=MathLib.translate(k,pos);return k[0];}
	public Line translate(Direction dir, double mult) {Line[]k={this};k=MathLib.translate(k,dir,mult);return k[0];}
	public void rotateSelfAroundAxisPos(Position pos, Direction axis, double angle) {setValue(rotateAroundAxisPos(pos,axis,angle));}
	public Line rotateAroundAxisPos(Position pos, Direction axis, double angle) {Line[]k={this};k=MathLib.rotateAroundAxisPos(k,pos,axis,angle);return k[0];}
	public void scaleSelfAroundPos(Position pos, Scaling scale) {setValue(scaleAroundPos(pos,scale));}
	public Line scaleAroundPos(Position pos, Scaling scale) {Line[]k={this};k=MathLib.scaleAroundPos(k,pos,scale);return k[0];}
	}
	public static class Tetrahedron implements Comparable<Tetrahedron> {public Position pos1=new Position(0.0f,0.0f,0.0f),pos2=new Position(0.0f,0.0f,0.0f),pos3=new Position(0.0f,0.0f,0.0f),pos4=new Position(0.0f,0.0f,0.0f); public Tetrahedron(Position pos1i,Position pos2i, Position pos3i,Position pos4i){this.pos1=pos1i;this.pos2=pos2i;this.pos3=pos3i;this.pos4=pos4i;}
	@Override public int compareTo(Tetrahedron o) {
		int k = -1;
		Position[] tposarray = {this.pos1,this.pos2,this.pos3,this.pos4};
		Position[] oposarray = {o.pos1,o.pos2,o.pos3,o.pos4};
		Arrays.sort(tposarray);
		Arrays.sort(oposarray);
		if (tposarray[0].z>oposarray[0].z) {
			k = 1;
		} else if (tposarray[0].z==oposarray[0].z) {
			if (tposarray[1].z>oposarray[1].z) {
				k = 1;
			} else if (tposarray[1].z==oposarray[1].z) {
				if (tposarray[2].z>oposarray[2].z) {
					k = 1;
				} else if (tposarray[2].z==oposarray[2].z) {
					if (tposarray[3].z>oposarray[3].z) {
						k = 1;
					} else if (tposarray[3].z==oposarray[3].z) {
						if (tposarray[0].y>oposarray[0].y) {
							k = 1;
						} else if (tposarray[0].y==oposarray[0].y) {
							if (tposarray[1].y>oposarray[1].y) {
								k = 1;
							} else if (tposarray[1].y==oposarray[1].y) {
								if (tposarray[2].y>oposarray[2].y) {
									k = 1;
								} else if (tposarray[2].y==oposarray[2].y) {
									if (tposarray[3].y>oposarray[3].y) {
										k = 1;
									} else if (tposarray[3].y==oposarray[3].y) {
										if (tposarray[0].x>oposarray[0].x) {
											k = 1;
										} else if (tposarray[0].x==oposarray[0].x) {
											if (tposarray[1].x>oposarray[1].x) {
												k = 1;
											} else if (tposarray[1].x==oposarray[1].x) {
												if (tposarray[2].x>oposarray[2].x) {
													k = 1;
												} else if (tposarray[2].x==oposarray[2].x) {
													if (tposarray[3].x>oposarray[3].x) {
														k = 1;
													} else if (tposarray[3].x==oposarray[3].x) {
														k = 0;
													}}}}}}}}}}}}
		return k;
	}
	@Override public boolean equals(Object o) {
		boolean k = false;
		if ((o!=null)&&(o.getClass().equals(this.getClass()))) {
			Tetrahedron co = (Tetrahedron)o;
			Position[] tp = {this.pos1,this.pos2,this.pos3,this.pos4};
			Position[] op = {co.pos1,co.pos2,co.pos3,this.pos4};
			Arrays.sort(tp);
			Arrays.sort(op);
			boolean arraytest1 = (tp[0].x==op[0].x)&&(tp[0].y==op[0].y)&&(tp[0].z==op[0].z);
			boolean arraytest2 = (tp[1].x==op[1].x)&&(tp[1].y==op[1].y)&&(tp[1].z==op[1].z);
			boolean arraytest3 = (tp[2].x==op[2].x)&&(tp[2].y==op[2].y)&&(tp[2].z==op[2].z);
			boolean arraytest4 = (tp[3].x==op[3].x)&&(tp[3].y==op[3].y)&&(tp[3].z==op[3].z);
			if ((arraytest1)&&(arraytest2)&&(arraytest3)&&(arraytest4)) {
				k = true;
			}
		}
		return k;
	}
	public Tetrahedron copy(){Tetrahedron k = new Tetrahedron(this.pos1.copy(),this.pos2.copy(),this.pos3.copy(),this.pos4.copy()); return k;}
	public void setValue(Tetrahedron value) {this.pos1=value.pos1;this.pos2=value.pos2;this.pos3=value.pos3;this.pos4=value.pos4;}
	public void translateSelf(Position pos) {setValue(translate(pos));}
	public void translateSelf(Direction dir, double mult) {setValue(translate(dir,mult));}
	public Tetrahedron translate(Position pos) {Tetrahedron[]k={this};k=MathLib.translate(k,pos);return k[0];}
	public Tetrahedron translate(Direction dir, double mult) {Tetrahedron[]k={this};k=MathLib.translate(k,dir,mult);return k[0];}
	public void rotateSelfAroundAxisPos(Position pos, Direction axis, double angle) {setValue(rotateAroundAxisPos(pos,axis,angle));}
	public Tetrahedron rotateAroundAxisPos(Position pos, Direction axis, double angle) {Tetrahedron[]k={this};k=MathLib.rotateAroundAxisPos(k,pos,axis,angle);return k[0];}
	public void scaleSelfAroundPos(Position pos, Scaling scale) {setValue(scaleAroundPos(pos,scale));}
	public Tetrahedron scaleAroundPos(Position pos, Scaling scale) {Tetrahedron[]k={this};k=MathLib.scaleAroundPos(k,pos,scale);return k[0];}
	}
	public static class Triangle implements Comparable<Triangle> {public Position pos1=new Position(0.0f,0.0f,0.0f),pos2=new Position(0.0f,0.0f,0.0f),pos3=new Position(0.0f,0.0f,0.0f); public Direction norm=new Direction(0.0f,0.0f,0.0f); public Material mat=new Material(); public Material[] lmatl=null; public Object hwent=null, hwtri=null;
	public Triangle(Position pos1i,Position pos2i,Position pos3i) {this.pos1=pos1i;this.pos2=pos2i;this.pos3=pos3i;}
	@Override public int compareTo(Triangle o) {
		int k = -1;
		Position[] tposarray = {this.pos1,this.pos2,this.pos3};
		Position[] oposarray = {o.pos1,o.pos2,o.pos3};
		Arrays.sort(tposarray);
		Arrays.sort(oposarray);
		if (tposarray[0].z>oposarray[0].z) {
			k = 1;
		} else if (tposarray[0].z==oposarray[0].z) {
			if (tposarray[1].z>oposarray[1].z) {
				k = 1;
			} else if (tposarray[1].z==oposarray[1].z) {
				if (tposarray[2].z>oposarray[2].z) {
					k = 1;
				} else if (tposarray[2].z==oposarray[2].z) {
					if (tposarray[0].y>oposarray[0].y) {
						k = 1;
					} else if (tposarray[0].y==oposarray[0].y) {
						if (tposarray[1].y>oposarray[1].y) {
							k = 1;
						} else if (tposarray[1].y==oposarray[1].y) {
							if (tposarray[2].y>oposarray[2].y) {
								k = 1;
							} else if (tposarray[2].y==oposarray[2].y) {
								if (tposarray[0].x>oposarray[0].x) {
									k = 1;
								} else if (tposarray[0].x==oposarray[0].x) {
									if (tposarray[1].x>oposarray[1].x) {
										k = 1;
									} else if (tposarray[1].x==oposarray[1].x) {
										if (tposarray[2].x>oposarray[2].x) {
											k = 1;
										} else if (tposarray[2].x==oposarray[2].x) {
											k = 0;
										}}}}}}}}}
		return k;
	}
	@Override public boolean equals(Object o) {
		boolean k = false;
		if ((o!=null)&&(o.getClass().equals(this.getClass()))) {
			Triangle co = (Triangle)o;
			Position[] tposarray = {this.pos1,this.pos2,this.pos3};
			Position[] oposarray = {co.pos1,co.pos2,co.pos3};
			Arrays.sort(tposarray);
			Arrays.sort(oposarray);
			if ((tposarray[0].compareTo(oposarray[0])==0)&&(tposarray[1].compareTo(oposarray[1])==0)&&(tposarray[2].compareTo(oposarray[2])==0)) {
				k = true;
			}
		}
		return k;
	}
	public Triangle copy(){Triangle k=new Triangle(this.pos1.copy(),this.pos2.copy(),this.pos3.copy());k.norm=this.norm.copy();k.mat=this.mat.copy();k.lmatl=this.lmatl;k.hwent=this.hwent;k.hwtri=this.hwtri;return k;}
	public void setValue(Triangle value) {this.pos1=value.pos1;this.pos2=value.pos2;this.pos3=value.pos3;this.norm=value.norm;this.mat=value.mat;this.lmatl=value.lmatl;}
	public void translateSelf(Position pos) {setValue(translate(pos));}
	public void translateSelf(Direction dir, double mult) {setValue(translate(dir,mult));}
	public Triangle translate(Position pos) {Triangle[]k={this};k=MathLib.translate(k,pos);return k[0];}
	public Triangle translate(Direction dir, double mult) {Triangle[]k={this};k=MathLib.translate(k,dir,mult);return k[0];}
	public void rotateSelfAroundAxisPos(Position pos, Direction axis, double angle) {setValue(rotateAroundAxisPos(pos,axis,angle));}
	public Triangle rotateAroundAxisPos(Position pos, Direction axis, double angle) {Triangle[]k={this};k=MathLib.rotateAroundAxisPos(k,pos,axis,angle);return k[0];}
	public void scaleSelfAroundPos(Position pos, Scaling scale) {setValue(scaleAroundPos(pos,scale));}
	public Triangle scaleAroundPos(Position pos, Scaling scale) {Triangle[]k={this};k=MathLib.scaleAroundPos(k,pos,scale);return k[0];}
	}
	public static class Entity implements Comparable<Entity> {
		public Entity[] childlist = null;
		public Triangle[] trianglelist = null;
		public Quad[] quadlist = null;
		public Line[] linelist = null;
		public Position[] vertexlist = null;
		public Material[] materiallist = null;
		public BufferedImage[] imagelist = null;
		public Sphere sphereboundaryvolume = null;
		public Cube aabbboundaryvolume = null;
		public Matrix transform = null;
		public Position translation = null;
		@Override public int compareTo(Entity o) {return this.sphereboundaryvolume.compareTo(o.sphereboundaryvolume);}
		public Entity copy(){Entity k=new Entity();k.childlist=this.childlist;k.trianglelist=this.trianglelist;k.linelist=this.linelist;k.vertexlist=this.vertexlist;k.sphereboundaryvolume=this.sphereboundaryvolume;k.aabbboundaryvolume=this.aabbboundaryvolume;k.transform=this.transform;k.translation=this.translation;return k;}
		public void setValue(Entity value) {
			this.childlist=value.childlist;
			this.trianglelist=value.trianglelist;
			this.linelist=value.linelist;
			this.vertexlist=value.vertexlist;
			this.sphereboundaryvolume=value.sphereboundaryvolume;
			this.aabbboundaryvolume=value.aabbboundaryvolume;
		}
		public void translateSelf(Position pos) {setValue(translate(pos));}
		public void translateSelf(Direction dir, double mult) {setValue(translate(dir,mult));}
		public Entity translate(Position pos) {
			Entity k = new Entity();
			if (this.childlist!=null) {k.childlist=new Entity[this.childlist.length]; for (int i=0;i<k.childlist.length;i++) {k.childlist[i] = this.childlist[i].translate(pos);}}
			if (this.trianglelist!=null) {k.trianglelist=new Triangle[this.trianglelist.length]; for (int i=0;i<k.trianglelist.length;i++) {k.trianglelist[i] = this.trianglelist[i].translate(pos);}}
			if (this.linelist!=null) {k.linelist=new Line[this.linelist.length]; for (int i=0;i<k.linelist.length;i++) {k.linelist[i] = this.linelist[i].translate(pos);}}
			if (this.vertexlist!=null) {k.vertexlist=new Position[this.vertexlist.length]; for (int i=0;i<k.vertexlist.length;i++) {k.vertexlist[i] = this.vertexlist[i].translate(pos);}}
			if (this.sphereboundaryvolume!=null) {k.sphereboundaryvolume = this.sphereboundaryvolume.translate(pos);}
			if (this.aabbboundaryvolume!=null) {k.aabbboundaryvolume = this.aabbboundaryvolume.translate(pos);}
			return k;
		}
		public Entity translate(Direction dir, double mult) {
			Entity k = this.copy();
			if (this.childlist!=null) {k.childlist=new Entity[this.childlist.length]; for (int i=0;i<k.childlist.length;i++) {k.childlist[i] = this.childlist[i].translate(dir,mult);}}
			if (this.trianglelist!=null) {k.trianglelist=new Triangle[this.trianglelist.length]; for (int i=0;i<k.trianglelist.length;i++) {k.trianglelist[i] = this.trianglelist[i].translate(dir,mult);}}
			if (this.linelist!=null) {k.linelist=new Line[this.linelist.length]; for (int i=0;i<k.linelist.length;i++) {k.linelist[i] = this.linelist[i].translate(dir,mult);}}
			if (this.vertexlist!=null) {k.vertexlist=new Position[this.vertexlist.length]; for (int i=0;i<k.vertexlist.length;i++) {k.vertexlist[i] = this.vertexlist[i].translate(dir,mult);}}
			if (this.sphereboundaryvolume!=null) {k.sphereboundaryvolume = this.sphereboundaryvolume.translate(dir,mult);}
			if (this.aabbboundaryvolume!=null) {k.aabbboundaryvolume = this.aabbboundaryvolume.translate(dir,mult);}
			return k;
		}
		public void rotateSelfAroundAxisPos(Position pos, Direction axis, double angle) {setValue(rotateAroundAxisPos(pos,axis,angle));}
		public Entity rotateAroundAxisPos(Position pos, Direction axis, double angle) {
			Entity k = this.copy();
			if (this.childlist!=null) {k.childlist=new Entity[this.childlist.length]; for (int i=0;i<k.childlist.length;i++) {k.childlist[i] = this.childlist[i].rotateAroundAxisPos(pos,axis,angle);}}
			if (this.trianglelist!=null) {k.trianglelist=new Triangle[this.trianglelist.length]; for (int i=0;i<k.trianglelist.length;i++) {k.trianglelist[i] = this.trianglelist[i].rotateAroundAxisPos(pos,axis,angle);}}
			if (this.linelist!=null) {k.linelist=new Line[this.linelist.length]; for (int i=0;i<k.linelist.length;i++) {k.linelist[i] = this.linelist[i].rotateAroundAxisPos(pos,axis,angle);}}
			if (this.vertexlist!=null) {k.vertexlist=new Position[this.vertexlist.length]; for (int i=0;i<k.vertexlist.length;i++) {k.vertexlist[i] = this.vertexlist[i].rotateAroundAxisPos(pos,axis,angle);}}
			if (this.sphereboundaryvolume!=null) {k.sphereboundaryvolume = this.sphereboundaryvolume.rotateAroundAxisPos(pos,axis,angle);}
			if (this.aabbboundaryvolume!=null) {k.aabbboundaryvolume = this.aabbboundaryvolume.rotateAroundAxisPos(pos,axis,angle);}
			return k;
		}
		public void scaleSelfAroundPos(Position pos, Scaling scale) {setValue(scaleAroundPos(pos,scale));}
		public Entity scaleAroundPos(Position pos, Scaling scale) {
			Entity k = this.copy();
			if (this.childlist!=null) {k.childlist=new Entity[this.childlist.length]; for (int i=0;i<k.childlist.length;i++) {k.childlist[i] = this.childlist[i].scaleAroundPos(pos,scale);}}
			if (this.trianglelist!=null) {k.trianglelist=new Triangle[this.trianglelist.length]; for (int i=0;i<k.trianglelist.length;i++) {k.trianglelist[i] = this.trianglelist[i].scaleAroundPos(pos,scale);}}
			if (this.linelist!=null) {k.linelist=new Line[this.linelist.length]; for (int i=0;i<k.linelist.length;i++) {k.linelist[i] = this.linelist[i].scaleAroundPos(pos,scale);}}
			if (this.vertexlist!=null) {k.vertexlist=new Position[this.vertexlist.length]; for (int i=0;i<k.vertexlist.length;i++) {k.vertexlist[i] = this.vertexlist[i].scaleAroundPos(pos,scale);}}
			if (this.sphereboundaryvolume!=null) {k.sphereboundaryvolume = this.sphereboundaryvolume.scaleAroundPos(pos,scale);}
			if (this.aabbboundaryvolume!=null) {k.aabbboundaryvolume = this.aabbboundaryvolume.scaleAroundPos(pos,scale);}
			return k;
		}
	}
	public static class Matrix {public double a11,a12,a13,a21,a22,a23,a31,a32,a33; public Matrix(double a11i,double a12i,double a13i,double a21i,double a22i,double a23i,double a31i,double a32i,double a33i){this.a11=a11i;this.a12=a12i;this.a13=a13i;this.a21=a21i;this.a22=a22i;this.a23=a23i;this.a31=a31i;this.a32=a32i;this.a33=a33i;}
	@Override public boolean equals(Object o) {
		boolean k = false;
		if ((o!=null)&&(o.getClass().equals(this.getClass()))) {
			Matrix co = (Matrix)o;
			if ((this.a11==co.a11)&&(this.a12==co.a12)&&(this.a13==co.a13)&&(this.a21==co.a21)&&(this.a22==co.a22)&&(this.a23==co.a23)&&(this.a31==co.a31)&&(this.a32==co.a32)&&(this.a33==co.a33)) {
				k = true;
			}
		}
		return k;
	}
	public Matrix copy(){Matrix k=new Matrix(this.a11,this.a12,this.a13,this.a21,this.a22,this.a23,this.a31,this.a32,this.a33);return k;}
	}

	public static class ModelFaceVertexIndex {
		public int vertexindex = -1; 
		public int textureindex = -1; 
		public int normalindex = -1; 
		public ModelFaceVertexIndex(int vertexindexi, int textureindexi, int normalindexi) {this.vertexindex = vertexindexi; this.textureindex = textureindexi; this.normalindex = normalindexi;}
	}
	public static class ModelFaceIndex {
		public ModelFaceVertexIndex[] facevertexindex = null;
		public String usemtl = null;
		public ModelFaceIndex(ModelFaceVertexIndex[] facevertexindexi){this.facevertexindex=facevertexindexi;}
	}
	public static class ModelLineIndex {
		public int[] linevertexindex = null;
		public ModelLineIndex(int[] linevertexindexi){this.linevertexindex=linevertexindexi;}
	}
	public static class ModelObject {
		public String objectname = null;
		public ModelFaceIndex[] faceindex = null;
		public ModelLineIndex[] lineindex = null;
		public ModelObject(String objectnamei) {this.objectname = objectnamei;}
	}
	public static class Model {
		public String filename = null;
		public String mtllib = null;
		public Position[] vertexlist = null;
		public Direction[] facenormals = null;
		public Coordinate[] texturecoords = null;
		public Material[] materials= null;
		public BufferedImage[] images = null;
		public ModelObject[] objects= null;
		public Model(String filenamei) {this.filename = filenamei;}
	}

	public static void saveWaveFrontOBJFile(String filename, Model model) {
		if ((filename!=null)&&(model!=null)) {
			try {
				File saveobjfile = new File(filename);
				BufferedWriter modelobjfile = new BufferedWriter(new FileWriter(saveobjfile, false));
				modelobjfile.write("mtllib "+model.mtllib);
				modelobjfile.newLine();
				for (int i=0;i<model.vertexlist.length;i++) {
					modelobjfile.write("v "+model.vertexlist[i].x+" "+model.vertexlist[i].y+" "+model.vertexlist[i].z);
					modelobjfile.newLine();
				}
				for (int i=0;i<model.facenormals.length;i++) {
					modelobjfile.write("vn "+model.facenormals[i].dx+" "+model.facenormals[i].dy+" "+model.facenormals[i].dz);
					modelobjfile.newLine();
				}
				for (int i=0;i<model.texturecoords.length;i++) {
					modelobjfile.write("vt "+model.texturecoords[i].u+" "+model.texturecoords[i].v);
					modelobjfile.newLine();
				}
				String lastusemtl = null;
				for (int k=0;k<model.objects.length;k++) {
					modelobjfile.newLine();
					modelobjfile.write("o "+model.objects[k].objectname);
					modelobjfile.newLine();
					if ((model.objects[k].faceindex!=null)&&(model.objects[k].faceindex.length>0)) {
						for (int j=0;j<model.objects[k].faceindex.length;j++) {
							if ((j==0)||(!model.objects[k].faceindex[j].usemtl.equals(lastusemtl))) {
								modelobjfile.write("usemtl "+model.objects[k].faceindex[j].usemtl);
								modelobjfile.newLine();
								lastusemtl = model.objects[k].faceindex[j].usemtl;
							}
							modelobjfile.write("f ");
							for (int i=0;i<model.objects[k].faceindex[j].facevertexindex.length;i++) {
								if (i>0) {modelobjfile.write(" ");}
								modelobjfile.write(""+model.objects[k].faceindex[j].facevertexindex[i].vertexindex);
								if ((model.objects[k].faceindex[j].facevertexindex[i].textureindex>0)&&(model.objects[k].faceindex[j].facevertexindex[i].normalindex>0)) {
									modelobjfile.write("/");
									modelobjfile.write(""+model.objects[k].faceindex[j].facevertexindex[i].textureindex);
									modelobjfile.write("/");
									modelobjfile.write(""+model.objects[k].faceindex[j].facevertexindex[i].normalindex);
								} else if (model.objects[k].faceindex[j].facevertexindex[i].textureindex>0) {
									modelobjfile.write("/");
									modelobjfile.write(""+model.objects[k].faceindex[j].facevertexindex[i].textureindex);
								} else if (model.objects[k].faceindex[j].facevertexindex[i].normalindex>0) {
									modelobjfile.write("/");
									modelobjfile.write("/");
									modelobjfile.write(""+model.objects[k].faceindex[j].facevertexindex[i].normalindex);
								}
							}
							modelobjfile.newLine();
						}
					}
					if ((model.objects[k].lineindex!=null)&&(model.objects[k].lineindex.length>0)) {
						for (int j=0;j<model.objects[k].lineindex.length;j++) {
							modelobjfile.write("l");
							for (int i=0;i<model.objects[k].lineindex[j].linevertexindex.length;i++) {
								modelobjfile.write(" "+model.objects[k].lineindex[j].linevertexindex[i]);
							}
							modelobjfile.newLine();
						}
					}
				}
				modelobjfile.close();
				saveWaveFrontMTLFile(new File(saveobjfile.getParent(), model.mtllib).getPath(), model);
			} catch(Exception ex){ex.printStackTrace();}
		}
	}

	public static void saveWaveFrontMTLFile(String filename, Model model) {
		if ((filename!=null)&&(model!=null)) {
			try {
				File savemtlfile = new File(filename);
				TreeMap<Material,String> modelmaterialimages = new TreeMap<Material,String>();
				BufferedWriter modelobjfile = new BufferedWriter(new FileWriter(savemtlfile, false));
				for (int i=0;i<model.materials.length;i++) {
					modelobjfile.write("newmtl "+model.materials[i].materialname);
					modelobjfile.newLine();
					modelobjfile.write("Ns "+model.materials[i].specularexp);
					modelobjfile.newLine();
					if (model.materials[i].ambientcolor!=null) {
						float[] rgbcolor = model.materials[i].ambientcolor.getRGBColorComponents(new float[3]);
						modelobjfile.write("Ka "+rgbcolor[0]+" "+rgbcolor[1]+" "+rgbcolor[2]);
						modelobjfile.newLine();
					}
					if (model.materials[i].facecolor!=null) {
						float[] rgbcolor = model.materials[i].facecolor.getRGBColorComponents(new float[3]);
						modelobjfile.write("Kd "+rgbcolor[0]+" "+rgbcolor[1]+" "+rgbcolor[2]);
						modelobjfile.newLine();
					}
					if (model.materials[i].specularcolor!=null) {
						float[] rgbcolor = model.materials[i].specularcolor.getRGBColorComponents(new float[3]);
						modelobjfile.write("Ks "+rgbcolor[0]+" "+rgbcolor[1]+" "+rgbcolor[2]);
						modelobjfile.newLine();
					}
					if (model.materials[i].emissivecolor!=null) {
						float[] rgbcolor = model.materials[i].emissivecolor.getRGBColorComponents(new float[3]);
						modelobjfile.write("Ke "+rgbcolor[0]+" "+rgbcolor[1]+" "+rgbcolor[2]);
						modelobjfile.newLine();
					}
					modelobjfile.write("Ni "+model.materials[i].refraction);
					modelobjfile.newLine();
					modelobjfile.write("d "+model.materials[i].transparency);
					modelobjfile.newLine();
					modelobjfile.write("illum 2");
					modelobjfile.newLine();
					modelobjfile.write("Pr "+model.materials[i].roughness);
					modelobjfile.newLine();
					modelobjfile.write("Pm "+model.materials[i].metallic);
					modelobjfile.newLine();
					modelobjfile.write("Ps "+model.materials[i].sheen);
					modelobjfile.newLine();
					modelobjfile.write("Pc "+model.materials[i].coatthickness);
					modelobjfile.newLine();
					modelobjfile.write("Pcr "+model.materials[i].coatroughtness);
					modelobjfile.newLine();
					modelobjfile.write("aniso "+model.materials[i].anisotropy);
					modelobjfile.newLine();
					modelobjfile.write("anisor "+model.materials[i].anisotropyrot);
					modelobjfile.newLine();
					if (model.materials[i].fileimage!=null) {
						String savefilename = model.materials[i].filename;
						String parentdirectory = savemtlfile.getParent();
						Material savefileimagematerial = new Material(Color.WHITE,1.0f,model.materials[i].fileimage);
						if (modelmaterialimages.containsKey(savefileimagematerial)) {
							savefilename = modelmaterialimages.get(savefileimagematerial);
						} else {
							String imagefilename = new File(parentdirectory, savefilename).getPath();
							UtilLib.saveImageFormat(imagefilename, savefileimagematerial.fileimage, new PNGFileFilter());
							modelmaterialimages.put(savefileimagematerial, savefilename);
						}
						modelobjfile.write("map_Kd "+savefilename);
						modelobjfile.newLine();
					}
					if (model.materials[i].ambientfileimage!=null) {
						String savefilename = model.materials[i].filename;
						savefilename = savefilename.substring(0, savefilename.length()-4)+"_ambient.png";
						String parentdirectory = savemtlfile.getParent();
						Material savefileimagematerial = new Material(Color.WHITE,1.0f,model.materials[i].ambientfileimage);
						if (modelmaterialimages.containsKey(savefileimagematerial)) {
							savefilename = modelmaterialimages.get(savefileimagematerial);
						} else {
							String imagefilename = new File(parentdirectory, savefilename).getPath();
							UtilLib.saveImageFormat(imagefilename, savefileimagematerial.fileimage, new PNGFileFilter());
							modelmaterialimages.put(savefileimagematerial, savefilename);
						}
						modelobjfile.write("map_Ka "+savefilename);
						modelobjfile.newLine();
					}
					if (model.materials[i].specularfileimage!=null) {
						String savefilename = model.materials[i].filename;
						savefilename = savefilename.substring(0, savefilename.length()-4)+"_specular.png";
						String parentdirectory = savemtlfile.getParent();
						Material savefileimagematerial = new Material(Color.WHITE,1.0f,model.materials[i].specularfileimage);
						if (modelmaterialimages.containsKey(savefileimagematerial)) {
							savefilename = modelmaterialimages.get(savefileimagematerial);
						} else {
							String imagefilename = new File(parentdirectory, savefilename).getPath();
							UtilLib.saveImageFormat(imagefilename, savefileimagematerial.fileimage, new PNGFileFilter());
							modelmaterialimages.put(savefileimagematerial, savefilename);
						}
						modelobjfile.write("map_Ks "+savefilename);
						modelobjfile.newLine();
					}
					if (model.materials[i].specularhighfileimage!=null) {
						String savefilename = model.materials[i].filename;
						savefilename = savefilename.substring(0, savefilename.length()-4)+"_spechigh.png";
						String parentdirectory = savemtlfile.getParent();
						Material savefileimagematerial = new Material(Color.WHITE,1.0f,model.materials[i].specularhighfileimage);
						if (modelmaterialimages.containsKey(savefileimagematerial)) {
							savefilename = modelmaterialimages.get(savefileimagematerial);
						} else {
							String imagefilename = new File(parentdirectory, savefilename).getPath();
							UtilLib.saveImageFormat(imagefilename, savefileimagematerial.fileimage, new PNGFileFilter());
							modelmaterialimages.put(savefileimagematerial, savefilename);
						}
						modelobjfile.write("map_Ns "+savefilename);
						modelobjfile.newLine();
					}
					if (model.materials[i].emissivefileimage!=null) {
						String savefilename = model.materials[i].filename;
						savefilename = savefilename.substring(0, savefilename.length()-4)+"_emissive.png";
						String parentdirectory = savemtlfile.getParent();
						Material savefileimagematerial = new Material(Color.WHITE,1.0f,model.materials[i].emissivefileimage);
						if (modelmaterialimages.containsKey(savefileimagematerial)) {
							savefilename = modelmaterialimages.get(savefileimagematerial);
						} else {
							String imagefilename = new File(parentdirectory, savefilename).getPath();
							UtilLib.saveImageFormat(imagefilename, savefileimagematerial.fileimage, new PNGFileFilter());
							modelmaterialimages.put(savefileimagematerial, savefilename);
						}
						modelobjfile.write("map_Ke "+savefilename);
						modelobjfile.newLine();
					}
					if (model.materials[i].roughnessfileimage!=null) {
						String savefilename = model.materials[i].filename;
						savefilename = savefilename.substring(0, savefilename.length()-4)+"_roughness.png";
						String parentdirectory = savemtlfile.getParent();
						Material savefileimagematerial = new Material(Color.WHITE,1.0f,model.materials[i].roughnessfileimage);
						if (modelmaterialimages.containsKey(savefileimagematerial)) {
							savefilename = modelmaterialimages.get(savefileimagematerial);
						} else {
							String imagefilename = new File(parentdirectory, savefilename).getPath();
							UtilLib.saveImageFormat(imagefilename, savefileimagematerial.fileimage, new PNGFileFilter());
							modelmaterialimages.put(savefileimagematerial, savefilename);
						}
						modelobjfile.write("map_Pr "+savefilename);
						modelobjfile.newLine();
					}
					if (model.materials[i].metallicfileimage!=null) {
						String savefilename = model.materials[i].filename;
						savefilename = savefilename.substring(0, savefilename.length()-4)+"_metallic.png";
						String parentdirectory = savemtlfile.getParent();
						Material savefileimagematerial = new Material(Color.WHITE,1.0f,model.materials[i].metallicfileimage);
						if (modelmaterialimages.containsKey(savefileimagematerial)) {
							savefilename = modelmaterialimages.get(savefileimagematerial);
						} else {
							String imagefilename = new File(parentdirectory, savefilename).getPath();
							UtilLib.saveImageFormat(imagefilename, savefileimagematerial.fileimage, new PNGFileFilter());
							modelmaterialimages.put(savefileimagematerial, savefilename);
						}
						modelobjfile.write("map_Pm "+savefilename);
						modelobjfile.newLine();
					}
					if (model.materials[i].sheenfileimage!=null) {
						String savefilename = model.materials[i].filename;
						savefilename = savefilename.substring(0, savefilename.length()-4)+"_sheen.png";
						String parentdirectory = savemtlfile.getParent();
						Material savefileimagematerial = new Material(Color.WHITE,1.0f,model.materials[i].sheenfileimage);
						if (modelmaterialimages.containsKey(savefileimagematerial)) {
							savefilename = modelmaterialimages.get(savefileimagematerial);
						} else {
							String imagefilename = new File(parentdirectory, savefilename).getPath();
							UtilLib.saveImageFormat(imagefilename, savefileimagematerial.fileimage, new PNGFileFilter());
							modelmaterialimages.put(savefileimagematerial, savefilename);
						}
						modelobjfile.write("map_Ps "+savefilename);
						modelobjfile.newLine();
					}
					if (model.materials[i].alphafileimage!=null) {
						String savefilename = model.materials[i].filename;
						savefilename = savefilename.substring(0, savefilename.length()-4)+"_alpha.png";
						String parentdirectory = savemtlfile.getParent();
						Material savefileimagematerial = new Material(Color.WHITE,1.0f,model.materials[i].alphafileimage);
						if (modelmaterialimages.containsKey(savefileimagematerial)) {
							savefilename = modelmaterialimages.get(savefileimagematerial);
						} else {
							String imagefilename = new File(parentdirectory, savefilename).getPath();
							UtilLib.saveImageFormat(imagefilename, savefileimagematerial.fileimage, new PNGFileFilter());
							modelmaterialimages.put(savefileimagematerial, savefilename);
						}
						modelobjfile.write("map_d "+savefilename);
						modelobjfile.newLine();
					}
					if (model.materials[i].bumpfileimage!=null) {
						String savefilename = model.materials[i].filename;
						savefilename = savefilename.substring(0, savefilename.length()-4)+"_bump.png";
						String parentdirectory = savemtlfile.getParent();
						Material savefileimagematerial = new Material(Color.WHITE,1.0f,model.materials[i].bumpfileimage);
						if (modelmaterialimages.containsKey(savefileimagematerial)) {
							savefilename = modelmaterialimages.get(savefileimagematerial);
						} else {
							String imagefilename = new File(parentdirectory, savefilename).getPath();
							UtilLib.saveImageFormat(imagefilename, savefileimagematerial.fileimage, new PNGFileFilter());
							modelmaterialimages.put(savefileimagematerial, savefilename);
						}
						modelobjfile.write("bump "+savefilename);
						modelobjfile.newLine();
					}
					if (model.materials[i].dispfileimage!=null) {
						String savefilename = model.materials[i].filename;
						savefilename = savefilename.substring(0, savefilename.length()-4)+"_disp.png";
						String parentdirectory = savemtlfile.getParent();
						Material savefileimagematerial = new Material(Color.WHITE,1.0f,model.materials[i].dispfileimage);
						if (modelmaterialimages.containsKey(savefileimagematerial)) {
							savefilename = modelmaterialimages.get(savefileimagematerial);
						} else {
							String imagefilename = new File(parentdirectory, savefilename).getPath();
							UtilLib.saveImageFormat(imagefilename, savefileimagematerial.fileimage, new PNGFileFilter());
							modelmaterialimages.put(savefileimagematerial, savefilename);
						}
						modelobjfile.write("disp "+savefilename);
						modelobjfile.newLine();
					}
					if (model.materials[i].decalfileimage!=null) {
						String savefilename = model.materials[i].filename;
						savefilename = savefilename.substring(0, savefilename.length()-4)+"_decal.png";
						String parentdirectory = savemtlfile.getParent();
						Material savefileimagematerial = new Material(Color.WHITE,1.0f,model.materials[i].decalfileimage);
						if (modelmaterialimages.containsKey(savefileimagematerial)) {
							savefilename = modelmaterialimages.get(savefileimagematerial);
						} else {
							String imagefilename = new File(parentdirectory, savefilename).getPath();
							UtilLib.saveImageFormat(imagefilename, savefileimagematerial.fileimage, new PNGFileFilter());
							modelmaterialimages.put(savefileimagematerial, savefilename);
						}
						modelobjfile.write("decal "+savefilename);
						modelobjfile.newLine();
					}
					modelobjfile.newLine();
				}
				modelobjfile.close();
			} catch(Exception ex){ex.printStackTrace();}
		}
	}

	public static Model loadWaveFrontOBJFile(String filename, boolean loadresourcefromjar) {
		Model k = null;
		if (filename!=null) {
			BufferedReader modelobjfile = null;
			try {
				File loadobjfile = new File(filename);
				if (loadresourcefromjar) {
					modelobjfile = new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream(loadobjfile.getPath().replace(File.separatorChar, '/'))));
				}else {
					modelobjfile = new BufferedReader(new FileReader(loadobjfile));
				}
				if (modelobjfile!=null) {
					k = new Model(filename);
					ArrayList<ModelObject> modelobjects = new ArrayList<ModelObject>();
					ArrayList<Position> modelvertexlist = new ArrayList<Position>();
					ArrayList<Direction> modelfacenormals = new ArrayList<Direction>();
					ArrayList<Coordinate> modeltexturecoords = new ArrayList<Coordinate>();
					ArrayList<ModelFaceIndex> modelfaceindex = new ArrayList<ModelFaceIndex>(); 
					ArrayList<ModelLineIndex> modellineindex = new ArrayList<ModelLineIndex>();
					String lastusemtl = null;
					String fline = null;
					while((fline=modelobjfile.readLine())!=null) {
						fline = fline.trim();
						if (fline.toLowerCase().startsWith("#")) {
						}else if (fline.toLowerCase().startsWith("mtllib ")) {
							String farg = fline.substring(7).trim();
							File loadmtlfile = new File(loadobjfile.getParent(),farg);
							k.mtllib = farg;
							Model mtlmaterials = loadWaveFrontMTLFile(loadmtlfile.getPath(), loadresourcefromjar);
							k.materials = mtlmaterials.materials;
							k.images = mtlmaterials.images;
						}else if ((fline.toLowerCase().startsWith("o "))||(fline.toLowerCase().startsWith("g "))) {
							if (modelobjects.size()>0) {
								modelobjects.get(modelobjects.size()-1).faceindex = modelfaceindex.toArray(new ModelFaceIndex[modelfaceindex.size()]);
								modelobjects.get(modelobjects.size()-1).lineindex = modellineindex.toArray(new ModelLineIndex[modellineindex.size()]);
							}
							String farg = fline.substring(2).trim();
							modelobjects.add(new ModelObject(farg));
							modelfaceindex = new ArrayList<ModelFaceIndex>();
							modellineindex = new ArrayList<ModelLineIndex>();
						}else if (fline.toLowerCase().startsWith("v ")) {
							String farg = fline.substring(2).trim();
							String[] fargsplit = farg.split(" ");
							modelvertexlist.add(new Position(Double.parseDouble(fargsplit[0]), Double.parseDouble(fargsplit[1]), Double.parseDouble(fargsplit[2])));
						}else if (fline.toLowerCase().startsWith("vn ")) {
							String farg = fline.substring(3).trim();
							String[] fargsplit = farg.split(" ");
							modelfacenormals.add(new Direction(Double.parseDouble(fargsplit[0]), Double.parseDouble(fargsplit[1]), Double.parseDouble(fargsplit[2])));
						}else if (fline.toLowerCase().startsWith("vt ")) {
							String farg = fline.substring(3).trim();
							String[] fargsplit = farg.split(" ");
							modeltexturecoords.add(new Coordinate(Double.parseDouble(fargsplit[0]), Double.parseDouble(fargsplit[1])));
						}else if (fline.toLowerCase().startsWith("usemtl ")) {
							String farg = fline.substring(7).trim();
							lastusemtl = farg;
						}else if (fline.toLowerCase().startsWith("l ")) {
							String farg = fline.substring(2).trim();
							String[] fargsplit = farg.split(" ");
							int[] modellinevertexindex = new int[fargsplit.length]; 
							for (int i=0;i<fargsplit.length;i++) {
								modellinevertexindex[i] = Integer.parseInt(fargsplit[i]);
							}
							modellineindex.add(new ModelLineIndex(modellinevertexindex));
						}else if (fline.toLowerCase().startsWith("f ")) {
							String farg = fline.substring(2).trim();
							String[] fargsplit = farg.split(" ");
							ArrayList<ModelFaceVertexIndex> modelfacevertexindex = new ArrayList<ModelFaceVertexIndex>(); 
							for (int j=0;j<fargsplit.length;j++) {
								String[] fargsplit2 = fargsplit[j].split("/");
								for (int i=0;i<fargsplit2.length;i++) {
									if (fargsplit2[i].isBlank()) {
										fargsplit2[i] = "0";
									}
								}
								if (fargsplit2.length==1) {
									modelfacevertexindex.add(new ModelFaceVertexIndex(Integer.parseInt(fargsplit2[0]),0,0));
								} else if (fargsplit2.length==2) {
									modelfacevertexindex.add(new ModelFaceVertexIndex(Integer.parseInt(fargsplit2[0]),Integer.parseInt(fargsplit2[1]),0));
								} else {
									modelfacevertexindex.add(new ModelFaceVertexIndex(Integer.parseInt(fargsplit2[0]),Integer.parseInt(fargsplit2[1]),Integer.parseInt(fargsplit2[2])));
								}
							}
							ModelFaceIndex newmodelfaceindex = new ModelFaceIndex(modelfacevertexindex.toArray(new ModelFaceVertexIndex[modelfacevertexindex.size()]));
							newmodelfaceindex.usemtl = lastusemtl;
							modelfaceindex.add(newmodelfaceindex);
						}
					}
					if (modelobjects.size()>0) {
						modelobjects.get(modelobjects.size()-1).faceindex = modelfaceindex.toArray(new ModelFaceIndex[modelfaceindex.size()]);
						modelobjects.get(modelobjects.size()-1).lineindex = modellineindex.toArray(new ModelLineIndex[modellineindex.size()]);
					}
					k.objects = modelobjects.toArray(new ModelObject[modelobjects.size()]);
					k.vertexlist = modelvertexlist.toArray(new Position[modelvertexlist.size()]);
					k.facenormals = modelfacenormals.toArray(new Direction[modelfacenormals.size()]);
					k.texturecoords = modeltexturecoords.toArray(new Coordinate[modeltexturecoords.size()]);
				}
				modelobjfile.close();
			} catch(Exception ex) {ex.printStackTrace();}
		}
		return k;
	}

	public static Model loadWaveFrontMTLFile(String filename, boolean loadresourcefromjar) {
		Model k = new Model(null);
		if (filename!=null) {
			BufferedReader modelmtlfile = null;
			try {
				File loadmtlfile = new File(filename);
				if (loadresourcefromjar) {
					modelmtlfile = new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream(loadmtlfile.getPath().replace(File.separatorChar, '/'))));
				}else {
					modelmtlfile = new BufferedReader(new FileReader(loadmtlfile));
				}
				if (modelmtlfile!=null) {
					TreeMap<String,Material> modelmaterialimages = new TreeMap<String,Material>();
					ArrayList<Material> modelmaterials = new ArrayList<Material>();
					String fline = null;
					while((fline=modelmtlfile.readLine())!=null) {
						fline = fline.trim();
						if (fline.toLowerCase().startsWith("#")) {
						}else if (fline.toLowerCase().startsWith("newmtl ")) {
							String farg = fline.substring(7).trim();
							modelmaterials.add(new Material(Color.WHITE, 1.0f, null));
							modelmaterials.get(modelmaterials.size()-1).materialname = farg;
						}else if (fline.toLowerCase().startsWith("map_kd ")) {
							String farg = fline.substring(7).trim();
							File loadimgfile = new File(loadmtlfile.getParent(),farg);
							modelmaterials.get(modelmaterials.size()-1).filename = farg;
							BufferedImage loadimage = null;
							if (modelmaterialimages.containsKey(farg)) {
								loadimage = modelmaterialimages.get(farg).fileimage;
							} else {
								loadimage = UtilLib.loadImage(loadimgfile.getPath(), loadresourcefromjar);
								modelmaterialimages.put(farg,new Material(Color.WHITE,1.0f,loadimage));
							}
							modelmaterials.get(modelmaterials.size()-1).fileimage = loadimage;
						}else if (fline.toLowerCase().startsWith("map_ka ")) {
							String farg = fline.substring(7).trim();
							File loadimgfile = new File(loadmtlfile.getParent(),farg);
							modelmaterials.get(modelmaterials.size()-1).filename = farg;
							BufferedImage loadimage = null;
							if (modelmaterialimages.containsKey(farg)) {
								loadimage = modelmaterialimages.get(farg).fileimage;
							} else {
								loadimage = UtilLib.loadImage(loadimgfile.getPath(), loadresourcefromjar);
								modelmaterialimages.put(farg,new Material(Color.WHITE,1.0f,loadimage));
							}
							modelmaterials.get(modelmaterials.size()-1).ambientfileimage = loadimage;
						}else if (fline.toLowerCase().startsWith("map_ks ")) {
							String farg = fline.substring(7).trim();
							File loadimgfile = new File(loadmtlfile.getParent(),farg);
							modelmaterials.get(modelmaterials.size()-1).filename = farg;
							BufferedImage loadimage = null;
							if (modelmaterialimages.containsKey(farg)) {
								loadimage = modelmaterialimages.get(farg).fileimage;
							} else {
								loadimage = UtilLib.loadImage(loadimgfile.getPath(), loadresourcefromjar);
								modelmaterialimages.put(farg,new Material(Color.WHITE,1.0f,loadimage));
							}
							modelmaterials.get(modelmaterials.size()-1).specularfileimage = loadimage;
						}else if (fline.toLowerCase().startsWith("map_ke ")) {
							String farg = fline.substring(7).trim();
							File loadimgfile = new File(loadmtlfile.getParent(),farg);
							modelmaterials.get(modelmaterials.size()-1).filename = farg;
							BufferedImage loadimage = null;
							if (modelmaterialimages.containsKey(farg)) {
								loadimage = modelmaterialimages.get(farg).fileimage;
							} else {
								loadimage = UtilLib.loadImage(loadimgfile.getPath(), loadresourcefromjar);
								modelmaterialimages.put(farg,new Material(Color.WHITE,1.0f,loadimage));
							}
							modelmaterials.get(modelmaterials.size()-1).emissivefileimage = loadimage;
						}else if (fline.toLowerCase().startsWith("map_pr ")) {
							String farg = fline.substring(7).trim();
							File loadimgfile = new File(loadmtlfile.getParent(),farg);
							modelmaterials.get(modelmaterials.size()-1).filename = farg;
							BufferedImage loadimage = null;
							if (modelmaterialimages.containsKey(farg)) {
								loadimage = modelmaterialimages.get(farg).fileimage;
							} else {
								loadimage = UtilLib.loadImage(loadimgfile.getPath(), loadresourcefromjar);
								modelmaterialimages.put(farg,new Material(Color.WHITE,1.0f,loadimage));
							}
							modelmaterials.get(modelmaterials.size()-1).roughnessfileimage = loadimage;
						}else if (fline.toLowerCase().startsWith("map_pm ")) {
							String farg = fline.substring(7).trim();
							File loadimgfile = new File(loadmtlfile.getParent(),farg);
							modelmaterials.get(modelmaterials.size()-1).filename = farg;
							BufferedImage loadimage = null;
							if (modelmaterialimages.containsKey(farg)) {
								loadimage = modelmaterialimages.get(farg).fileimage;
							} else {
								loadimage = UtilLib.loadImage(loadimgfile.getPath(), loadresourcefromjar);
								modelmaterialimages.put(farg,new Material(Color.WHITE,1.0f,loadimage));
							}
							modelmaterials.get(modelmaterials.size()-1).metallicfileimage = loadimage;
						}else if (fline.toLowerCase().startsWith("map_ps ")) {
							String farg = fline.substring(7).trim();
							File loadimgfile = new File(loadmtlfile.getParent(),farg);
							modelmaterials.get(modelmaterials.size()-1).filename = farg;
							BufferedImage loadimage = null;
							if (modelmaterialimages.containsKey(farg)) {
								loadimage = modelmaterialimages.get(farg).fileimage;
							} else {
								loadimage = UtilLib.loadImage(loadimgfile.getPath(), loadresourcefromjar);
								modelmaterialimages.put(farg,new Material(Color.WHITE,1.0f,loadimage));
							}
							modelmaterials.get(modelmaterials.size()-1).sheenfileimage = loadimage;
						}else if (fline.toLowerCase().startsWith("map_ns ")) {
							String farg = fline.substring(7).trim();
							File loadimgfile = new File(loadmtlfile.getParent(),farg);
							modelmaterials.get(modelmaterials.size()-1).filename = farg;
							BufferedImage loadimage = null;
							if (modelmaterialimages.containsKey(farg)) {
								loadimage = modelmaterialimages.get(farg).fileimage;
							} else {
								loadimage = UtilLib.loadImage(loadimgfile.getPath(), loadresourcefromjar);
								modelmaterialimages.put(farg,new Material(Color.WHITE,1.0f,loadimage));
							}
							modelmaterials.get(modelmaterials.size()-1).specularhighfileimage = loadimage;
						}else if (fline.toLowerCase().startsWith("map_d ")) {
							String farg = fline.substring(6).trim();
							File loadimgfile = new File(loadmtlfile.getParent(),farg);
							modelmaterials.get(modelmaterials.size()-1).filename = farg;
							BufferedImage loadimage = null;
							if (modelmaterialimages.containsKey(farg)) {
								loadimage = modelmaterialimages.get(farg).fileimage;
							} else {
								loadimage = UtilLib.loadImage(loadimgfile.getPath(), loadresourcefromjar);
								modelmaterialimages.put(farg,new Material(Color.WHITE,1.0f,loadimage));
							}
							modelmaterials.get(modelmaterials.size()-1).alphafileimage = loadimage;
						}else if (fline.toLowerCase().startsWith("bump ")) {
							String farg = fline.substring(5).trim();
							File loadimgfile = new File(loadmtlfile.getParent(),farg);
							modelmaterials.get(modelmaterials.size()-1).filename = farg;
							BufferedImage loadimage = null;
							if (modelmaterialimages.containsKey(farg)) {
								loadimage = modelmaterialimages.get(farg).fileimage;
							} else {
								loadimage = UtilLib.loadImage(loadimgfile.getPath(), loadresourcefromjar);
								modelmaterialimages.put(farg,new Material(Color.WHITE,1.0f,loadimage));
							}
							modelmaterials.get(modelmaterials.size()-1).bumpfileimage = loadimage;
						}else if (fline.toLowerCase().startsWith("disp ")) {
							String farg = fline.substring(5).trim();
							File loadimgfile = new File(loadmtlfile.getParent(),farg);
							modelmaterials.get(modelmaterials.size()-1).filename = farg;
							BufferedImage loadimage = null;
							if (modelmaterialimages.containsKey(farg)) {
								loadimage = modelmaterialimages.get(farg).fileimage;
							} else {
								loadimage = UtilLib.loadImage(loadimgfile.getPath(), loadresourcefromjar);
								modelmaterialimages.put(farg,new Material(Color.WHITE,1.0f,loadimage));
							}
							modelmaterials.get(modelmaterials.size()-1).dispfileimage = loadimage;
						}else if (fline.toLowerCase().startsWith("decal ")) {
							String farg = fline.substring(5).trim();
							File loadimgfile = new File(loadmtlfile.getParent(),farg);
							modelmaterials.get(modelmaterials.size()-1).filename = farg;
							BufferedImage loadimage = null;
							if (modelmaterialimages.containsKey(farg)) {
								loadimage = modelmaterialimages.get(farg).fileimage;
							} else {
								loadimage = UtilLib.loadImage(loadimgfile.getPath(), loadresourcefromjar);
								modelmaterialimages.put(farg,new Material(Color.WHITE,1.0f,loadimage));
							}
							modelmaterials.get(modelmaterials.size()-1).decalfileimage = loadimage;
						}else if (fline.toLowerCase().startsWith("kd ")) {
							String farg = fline.substring(3).trim();
							String[] fargsplit = farg.split(" ");
							modelmaterials.get(modelmaterials.size()-1).facecolor = new Color(Float.parseFloat(fargsplit[0]), Float.parseFloat(fargsplit[1]), Float.parseFloat(fargsplit[2]));
						}else if (fline.toLowerCase().startsWith("ka ")) {
							String farg = fline.substring(3).trim();
							String[] fargsplit = farg.split(" ");
							modelmaterials.get(modelmaterials.size()-1).ambientcolor = new Color(Float.parseFloat(fargsplit[0]), Float.parseFloat(fargsplit[1]), Float.parseFloat(fargsplit[2]));
						}else if (fline.toLowerCase().startsWith("ke ")) {
							String farg = fline.substring(3).trim();
							String[] fargsplit = farg.split(" ");
							modelmaterials.get(modelmaterials.size()-1).emissivecolor = new Color(Float.parseFloat(fargsplit[0]), Float.parseFloat(fargsplit[1]), Float.parseFloat(fargsplit[2]));
						}else if (fline.toLowerCase().startsWith("ks ")) {
							String farg = fline.substring(3).trim();
							String[] fargsplit = farg.split(" ");
							modelmaterials.get(modelmaterials.size()-1).specularcolor = new Color(Float.parseFloat(fargsplit[0]), Float.parseFloat(fargsplit[1]), Float.parseFloat(fargsplit[2]));
						}else if (fline.toLowerCase().startsWith("Ns ")) {
							String farg = fline.substring(2).trim();
							modelmaterials.get(modelmaterials.size()-1).specularexp = Float.parseFloat(farg);
						}else if (fline.toLowerCase().startsWith("d ")) {
							String farg = fline.substring(2).trim();
							modelmaterials.get(modelmaterials.size()-1).transparency = Float.parseFloat(farg);
						}else if (fline.toLowerCase().startsWith("ni ")) {
							String farg = fline.substring(3).trim();
							modelmaterials.get(modelmaterials.size()-1).refraction = Float.parseFloat(farg);
						}else if (fline.toLowerCase().startsWith("pr ")) {
							String farg = fline.substring(3).trim();
							modelmaterials.get(modelmaterials.size()-1).roughness = Float.parseFloat(farg);
						}else if (fline.toLowerCase().startsWith("pm ")) {
							String farg = fline.substring(3).trim();
							modelmaterials.get(modelmaterials.size()-1).metallic = Float.parseFloat(farg);
						}else if (fline.toLowerCase().startsWith("pc ")) {
							String farg = fline.substring(3).trim();
							modelmaterials.get(modelmaterials.size()-1).coatthickness = Float.parseFloat(farg);
						}else if (fline.toLowerCase().startsWith("pcr ")) {
							String farg = fline.substring(4).trim();
							modelmaterials.get(modelmaterials.size()-1).coatroughtness = Float.parseFloat(farg);
						}else if (fline.toLowerCase().startsWith("aniso ")) {
							String farg = fline.substring(6).trim();
							modelmaterials.get(modelmaterials.size()-1).anisotropy = Float.parseFloat(farg);
						}else if (fline.toLowerCase().startsWith("anisor ")) {
							String farg = fline.substring(7).trim();
							modelmaterials.get(modelmaterials.size()-1).anisotropyrot = Float.parseFloat(farg);
						}
					}
					Set<String> imagecollectionkeys = modelmaterialimages.keySet();
					String[] imagepaths = imagecollectionkeys.toArray(new String[imagecollectionkeys.size()]);
					Collection<Material> imagecollectionvalues = modelmaterialimages.values();
					Material[] materialimages = imagecollectionvalues.toArray(new Material[imagecollectionvalues.size()]);
					k.images = new BufferedImage[materialimages.length];
					for (int i=0;i<k.images.length;i++) {
						k.images[i] = materialimages[i].fileimage;
					}
					k.materials = modelmaterials.toArray(new Material[modelmaterials.size()]);
					for (int i=0;i<k.materials.length;i++) {
						k.materials[i].materialid = i;
						if (k.materials[i].filename!=null) {
							k.materials[i].imageid = Arrays.binarySearch(imagepaths, k.materials[i].filename);
						}
					}
				}
				modelmtlfile.close();
			} catch(Exception ex) {ex.printStackTrace();}
		}
		return k;
	}

	public static void saveSTLFile(String filename, Triangle[] model, String objectname) {
		if ((filename!=null)&&(model!=null)) {
			try {
				File savestlfile = new File(filename);
				BufferedWriter modelstlfile = new BufferedWriter(new FileWriter(savestlfile, false));
				modelstlfile.write("solid "+objectname);
				modelstlfile.newLine();
				for (int i=0;i<model.length;i++) {
					modelstlfile.write("facet normal "+String.format("%1.4e",model[i].norm.dx).replace(',','.')+" "+String.format("%1.4e",model[i].norm.dy).replace(',','.')+" "+String.format("%1.4e",model[i].norm.dz).replace(',','.'));
					modelstlfile.newLine();
					modelstlfile.write("\touter loop");
					modelstlfile.newLine();
					modelstlfile.write("\t\tvertex "+String.format("%1.4e",model[i].pos1.x).replace(',','.')+" "+String.format("%1.4e",model[i].pos1.y).replace(',','.')+" "+String.format("%1.4e",model[i].pos1.z).replace(',','.'));
					modelstlfile.newLine();
					modelstlfile.write("\t\tvertex "+String.format("%1.4e",model[i].pos2.x).replace(',','.')+" "+String.format("%1.4e",model[i].pos2.y).replace(',','.')+" "+String.format("%1.4e",model[i].pos2.z).replace(',','.'));
					modelstlfile.newLine();
					modelstlfile.write("\t\tvertex "+String.format("%1.4e",model[i].pos3.x).replace(',','.')+" "+String.format("%1.4e",model[i].pos3.y).replace(',','.')+" "+String.format("%1.4e",model[i].pos3.z).replace(',','.'));
					modelstlfile.newLine();
					modelstlfile.write("\tendloop");
					modelstlfile.newLine();
					modelstlfile.write("endfacet");
					modelstlfile.newLine();
				}
				modelstlfile.write("endsolid "+objectname);
				modelstlfile.newLine();
				modelstlfile.close();
			} catch(Exception ex){ex.printStackTrace();}
		}
	}

	public static Triangle[] loadSTLFile(String filename, boolean loadresourcefromjar) {
		Triangle[] k = null;
		if (filename!=null) {
			BufferedReader modelstlfile = null;
			try {
				File loadstlfile = new File(filename);
				if (loadresourcefromjar) {
					modelstlfile = new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream(loadstlfile.getPath().replace(File.separatorChar, '/'))));
				}else {
					modelstlfile = new BufferedReader(new FileReader(loadstlfile));
				}
				if (modelstlfile!=null) {
					ArrayList<Triangle> modeltriangles = new ArrayList<Triangle>();
					String fline = null;
					String objectname = null;
					Triangle lasttri = null;
					Direction lastnorm = null;
					Position lastpos1 = null;
					Position lastpos2 = null;
					Position lastpos3 = null;
					while((fline=modelstlfile.readLine())!=null) {
						fline = fline.trim();
						if (fline.toLowerCase().startsWith("#")) {
						}else if (fline.toLowerCase().startsWith("solid")) {
							String farg = fline.substring(5).trim();
							objectname = farg;
							objectname = objectname.trim(); 
						}else if (fline.toLowerCase().startsWith("facet normal ")) {
							String farg = fline.substring(13).trim();
							String[] fargsplit = farg.split(" ");
							lastnorm = new Direction(Double.parseDouble(fargsplit[0]),Double.parseDouble(fargsplit[1]),Double.parseDouble(fargsplit[2]));
						}else if (fline.toLowerCase().startsWith("outer loop")) {
						}else if (fline.toLowerCase().startsWith("vertex ")) {
							String farg = fline.substring(7).trim();
							String[] fargsplit = farg.split(" ");
							Position newpos = new Position(Double.parseDouble(fargsplit[0]),Double.parseDouble(fargsplit[1]),Double.parseDouble(fargsplit[2]));
							if (lastpos1==null) {
								lastpos1 = newpos; 
							} else if (lastpos2==null) {
								lastpos2 = newpos;
							} else if (lastpos3==null) {
								lastpos3 = newpos;
							}
						}else if (fline.toLowerCase().startsWith("endloop")) {
							lasttri = new Triangle(lastpos1,lastpos2,lastpos3);
							lastpos1 = null;
							lastpos2 = null;
							lastpos3 = null;
						}else if (fline.toLowerCase().startsWith("endfacet")) {
							lasttri.norm = lastnorm;
							modeltriangles.add(lasttri);
							lastnorm = null;
						}else if (fline.toLowerCase().startsWith("endsolid")) {
						}
					}
					k = modeltriangles.toArray(new Triangle[modeltriangles.size()]);
				}
				modelstlfile.close();
			} catch(Exception ex) {ex.printStackTrace();}
		}
		return k;
	}

	public static void saveSTLFileEntity(String filename, Entity entity, String objectname) {
		Entity[] entitylist = entity.childlist;
		ArrayList<Triangle> savemodeltrianglearray = new ArrayList<Triangle>();  
		for (int i=0;i<entitylist.length;i++) {
			Triangle[] copytrianglelist = entitylist[i].trianglelist;
			savemodeltrianglearray.addAll(Arrays.asList(copytrianglelist));
		}
		Triangle[] savemodel = savemodeltrianglearray.toArray(new Triangle[savemodeltrianglearray.size()]);
		if (filename.toLowerCase().endsWith(".stl")) {
			filename = filename.substring(0, filename.length()-4).concat(".stl");
		} else {
			filename = filename.concat(".stl");
		}
		ModelLib.saveSTLFile(filename, savemodel, objectname);
	}
	public static void saveOBJFileEntity(String filename, Entity entity, boolean savesurfaceonly) {
		File savefile = new File(filename);
		Entity[] entitylist = entity.childlist;
		Model savemodel = new Model(savefile.getPath());
		String saveobjfile = savefile.getPath();
		String savemtlfile = savefile.getName();
		String saveimgfile = savefile.getName();
		if (savemtlfile.toLowerCase().endsWith(".obj")) {
			savemtlfile = savemtlfile.substring(0, savemtlfile.length()-4).concat(".mtl");
			saveimgfile = savemtlfile.substring(0, savemtlfile.length()-4);
		} else {
			saveobjfile = saveobjfile.concat(".obj");
			savemtlfile = savemtlfile.concat(".mtl");
		}
		savemodel.mtllib = savemtlfile;
		TreeSet<Material> materiallistarray = new TreeSet<Material>();
		TreeSet<Position> vertexlistarray = new TreeSet<Position>();
		TreeSet<Direction> normallistarray = new TreeSet<Direction>();
		TreeSet<Coordinate> texcoordlistarray = new TreeSet<Coordinate>();
		savemodel.objects = new ModelObject[entitylist.length];
		normallistarray.add(new Direction(0, 0, 0));
		texcoordlistarray.add(new Coordinate(0.0f,0.0f));
		for (int j=0;j<entitylist.length;j++) {
			savemodel.objects[j] = new ModelObject("JREOBJ"+(j+1));
			Triangle[] copytrianglelist = entitylist[j].trianglelist;
			vertexlistarray.addAll(Arrays.asList(entitylist[j].vertexlist));
			for (int i=0;i<copytrianglelist.length;i++) {
				vertexlistarray.add(copytrianglelist[i].pos1);
				vertexlistarray.add(copytrianglelist[i].pos2);
				vertexlistarray.add(copytrianglelist[i].pos3);
				normallistarray.add(copytrianglelist[i].norm);
				texcoordlistarray.add(copytrianglelist[i].pos1.tex);
				texcoordlistarray.add(copytrianglelist[i].pos2.tex);
				texcoordlistarray.add(copytrianglelist[i].pos3.tex);
				if (copytrianglelist[i].mat.facecolor==null) {copytrianglelist[i].mat.facecolor = Color.WHITE;}
				materiallistarray.add(copytrianglelist[i].mat);
			}
		}
		savemodel.materials = materiallistarray.toArray(new Material[materiallistarray.size()]);
		savemodel.vertexlist = vertexlistarray.toArray(new Position[vertexlistarray.size()]);
		savemodel.facenormals = normallistarray.toArray(new Direction[normallistarray.size()]);
		savemodel.texturecoords = texcoordlistarray.toArray(new Coordinate[texcoordlistarray.size()]);
		int imagenum = 0;
		for (int i=0;i<savemodel.materials.length;i++) {
			savemodel.materials[i].materialname = "JREMAT"+(i+1);
			if (savemodel.materials[i].fileimage!=null) {
				imagenum += 1;
				savemodel.materials[i].filename = saveimgfile+"_"+imagenum+".png";
			}
		}
		for (int j=0;j<entitylist.length;j++) {
			Triangle[] copytrianglelist = entitylist[j].trianglelist;
			for (int i=0;i<copytrianglelist.length;i++) {
				ModelFaceVertexIndex[] trianglevertex = new ModelFaceVertexIndex[3];
				int trianglefacenormalind = Arrays.binarySearch(savemodel.facenormals, copytrianglelist[i].norm)+1;
				trianglevertex[0] = new ModelFaceVertexIndex(Arrays.binarySearch(savemodel.vertexlist,copytrianglelist[i].pos1)+1,Arrays.binarySearch(savemodel.texturecoords,copytrianglelist[i].pos1.tex)+1,trianglefacenormalind);
				trianglevertex[1] = new ModelFaceVertexIndex(Arrays.binarySearch(savemodel.vertexlist,copytrianglelist[i].pos2)+1,Arrays.binarySearch(savemodel.texturecoords,copytrianglelist[i].pos2.tex)+1,trianglefacenormalind);
				trianglevertex[2] = new ModelFaceVertexIndex(Arrays.binarySearch(savemodel.vertexlist,copytrianglelist[i].pos3)+1,Arrays.binarySearch(savemodel.texturecoords,copytrianglelist[i].pos3.tex)+1,trianglefacenormalind);
				Material copymaterial = copytrianglelist[i].mat;
				int searchmatindex = Arrays.binarySearch(savemodel.materials, copymaterial);
				ModelFaceIndex[] objectfaceindex = savemodel.objects[j].faceindex;
				ArrayList<ModelFaceIndex> faceindexarray = (objectfaceindex!=null)?(new ArrayList<ModelFaceIndex>(Arrays.asList(objectfaceindex))):(new ArrayList<ModelFaceIndex>());
				ModelFaceIndex newmodelfaceindex = new ModelFaceIndex(trianglevertex);
				newmodelfaceindex.usemtl = savemodel.materials[searchmatindex].materialname;
				faceindexarray.add(newmodelfaceindex);
				savemodel.objects[j].faceindex = faceindexarray.toArray(new ModelFaceIndex[faceindexarray.size()]);
			}
			if (!savesurfaceonly) {
				if (entitylist[j].linelist!=null) {
					Line[] uniquelinelist = entitylist[j].linelist;
					for (int i=0;i<uniquelinelist.length;i++) {
						if (uniquelinelist[i].pos1.compareTo(uniquelinelist[i].pos2)!=0) {
							int[] linevertex = new int[2];
							linevertex[0] = Arrays.binarySearch(savemodel.vertexlist, uniquelinelist[i].pos1)+1;
							linevertex[1] = Arrays.binarySearch(savemodel.vertexlist, uniquelinelist[i].pos2)+1;
							ArrayList<ModelLineIndex> lineindexarray = (savemodel.objects[j].lineindex!=null)?(new ArrayList<ModelLineIndex>(Arrays.asList(savemodel.objects[j].lineindex))):(new ArrayList<ModelLineIndex>());
							lineindexarray.add(new ModelLineIndex(linevertex));
							savemodel.objects[j].lineindex = lineindexarray.toArray(new ModelLineIndex[lineindexarray.size()]);
						} else {
							int[] linevertex = new int[1];
							linevertex[0] = Arrays.binarySearch(savemodel.vertexlist, uniquelinelist[i].pos1)+1;
							ArrayList<ModelLineIndex> lineindexarray = (savemodel.objects[j].lineindex!=null)?(new ArrayList<ModelLineIndex>(Arrays.asList(savemodel.objects[j].lineindex))):(new ArrayList<ModelLineIndex>());
							lineindexarray.add(new ModelLineIndex(linevertex));
							savemodel.objects[j].lineindex = lineindexarray.toArray(new ModelLineIndex[lineindexarray.size()]);
						}
					}
				}
			}
		}
		ModelLib.saveWaveFrontOBJFile(saveobjfile, savemodel);
	}

	public static Entity loadSTLFileEntity(String filename, boolean loadresourcefromjar) {
		Entity loadentity = new Entity();
		Entity[] newentitylist = {new Entity()};
		newentitylist[0].trianglelist = ModelLib.loadSTLFile(filename, loadresourcefromjar);
		for (int i=0;i<newentitylist[0].trianglelist.length;i++) {
			if (newentitylist[0].trianglelist[i].mat==null) {
				newentitylist[0].trianglelist[i].mat = new Material(Color.WHITE,1.0f,null);
			}
		}
		loadentity.childlist = newentitylist;
		loadentity.linelist = MathLib.generateLineList(newentitylist[0].trianglelist);
		loadentity.vertexlist = MathLib.generateVertexList(loadentity.linelist);
		loadentity.aabbboundaryvolume = MathLib.axisAlignedBoundingBox(loadentity.vertexlist);
		loadentity.sphereboundaryvolume = MathLib.pointCloudCircumSphere(loadentity.vertexlist);
		newentitylist[0].vertexlist = loadentity.vertexlist;
		newentitylist[0].aabbboundaryvolume = loadentity.aabbboundaryvolume;
		newentitylist[0].sphereboundaryvolume = loadentity.sphereboundaryvolume;
		return loadentity;
	}
	public static Entity loadOBJFileEntity(String filename, boolean loadresourcefromjar) {
		Entity loadentity = new Entity();
		TreeSet<Line> linelisttree = new TreeSet<Line>();
		Model loadmodel = ModelLib.loadWaveFrontOBJFile(filename, loadresourcefromjar);
		loadentity.materiallist = loadmodel.materials;
		loadentity.imagelist = loadmodel.images;
		ArrayList<Entity> newentitylist = new ArrayList<Entity>();
		for (int j=0;j<loadmodel.objects.length;j++) {
			Entity newentity = new Entity();
			TreeSet<Line> newlinelisttree = new TreeSet<Line>();
			TreeSet<Line> newnontrianglelinelisttree = new TreeSet<Line>();
			ArrayList<Triangle> newtrianglelistarray = new ArrayList<Triangle>();
			for (int i=0;i<loadmodel.objects[j].faceindex.length;i++) {
				if (loadmodel.objects[j].faceindex[i].facevertexindex.length==1) {
					Position pos1 = loadmodel.vertexlist[loadmodel.objects[j].faceindex[i].facevertexindex[0].vertexindex-1];
					Line newline = new Line(pos1.copy(), pos1.copy());
					newlinelisttree.add(newline);
					linelisttree.add(newline);
					newnontrianglelinelisttree.add(newline);
				} else if (loadmodel.objects[j].faceindex[i].facevertexindex.length==2) {
					Position pos1 = loadmodel.vertexlist[loadmodel.objects[j].faceindex[i].facevertexindex[0].vertexindex-1];
					Position pos2 = loadmodel.vertexlist[loadmodel.objects[j].faceindex[i].facevertexindex[1].vertexindex-1];
					Line newline = new Line(pos1.copy(), pos2.copy());
					newlinelisttree.add(newline);
					linelisttree.add(newline);
					newnontrianglelinelisttree.add(newline);
				} else if (loadmodel.objects[j].faceindex[i].facevertexindex.length==3) {
					Material foundmat = null;
					for (int n=0;(n<loadmodel.materials.length)&&(foundmat==null);n++) {
						if (loadmodel.objects[j].faceindex[i].usemtl.equals(loadmodel.materials[n].materialname)) {
							foundmat = loadmodel.materials[n];
						}
					}
					if (foundmat==null) {
						foundmat = new Material(Color.WHITE,1.0f,null);
					}
					Position pos1 = loadmodel.vertexlist[loadmodel.objects[j].faceindex[i].facevertexindex[0].vertexindex-1];
					Position pos2 = loadmodel.vertexlist[loadmodel.objects[j].faceindex[i].facevertexindex[1].vertexindex-1];
					Position pos3 = loadmodel.vertexlist[loadmodel.objects[j].faceindex[i].facevertexindex[2].vertexindex-1];
					Direction norm = new Direction(0.0f, 0.0f, 0.0f);
					Coordinate tex1 = new Coordinate(0.0f,0.0f);
					Coordinate tex2 = new Coordinate(1.0f,0.0f);
					Coordinate tex3 = new Coordinate(1.0f,1.0f);
					if (loadmodel.objects[j].faceindex[i].facevertexindex[0].normalindex>0) {
						norm = loadmodel.facenormals[loadmodel.objects[j].faceindex[i].facevertexindex[0].normalindex-1];
					}
					if (loadmodel.objects[j].faceindex[i].facevertexindex[0].textureindex>0) {
						tex1 = loadmodel.texturecoords[loadmodel.objects[j].faceindex[i].facevertexindex[0].textureindex-1];
					}
					if (loadmodel.objects[j].faceindex[i].facevertexindex[1].textureindex>0) {
						tex2 = loadmodel.texturecoords[loadmodel.objects[j].faceindex[i].facevertexindex[1].textureindex-1];
					}
					if (loadmodel.objects[j].faceindex[i].facevertexindex[2].textureindex>0) {
						tex3 = loadmodel.texturecoords[loadmodel.objects[j].faceindex[i].facevertexindex[2].textureindex-1];
					}
					Triangle tri = new Triangle(new Position(pos1.x,pos1.y,pos1.z),new Position(pos2.x,pos2.y,pos2.z),new Position(pos3.x,pos3.y,pos3.z));
					tri.norm = norm;
					tri.pos1.tex = tex1;
					tri.pos2.tex = tex2;
					tri.pos3.tex = tex3;
					tri.mat = foundmat;
					newtrianglelistarray.add(tri);
					Line newline1 = new Line(pos1.copy(), pos2.copy());
					Line newline2 = new Line(pos1.copy(), pos3.copy());
					Line newline3 = new Line(pos2.copy(), pos3.copy());
					newlinelisttree.add(newline1);
					newlinelisttree.add(newline2);
					newlinelisttree.add(newline3);
					linelisttree.add(newline1);
					linelisttree.add(newline2);
					linelisttree.add(newline3);
				} else {
					Position[] pos = new Position[loadmodel.objects[j].faceindex[i].facevertexindex.length];
					for (int m=0;m<loadmodel.objects[j].faceindex[i].facevertexindex.length;m++) {
						pos[m] = loadmodel.vertexlist[loadmodel.objects[j].faceindex[i].facevertexindex[m].vertexindex-1];
						if (m>0) {
							Line newline = new Line(pos[m].copy(), pos[m-1].copy());
							newlinelisttree.add(newline);
							linelisttree.add(newline);
							newnontrianglelinelisttree.add(newline);
						}
					}
					Line newline = new Line(pos[0].copy(), pos[loadmodel.objects[j].faceindex[i].facevertexindex.length-1].copy());
					newlinelisttree.add(newline);
					linelisttree.add(newline);
					newnontrianglelinelisttree.add(newline);
				}
			}
			for (int i=0;i<loadmodel.objects[j].lineindex.length;i++) {
				if (loadmodel.objects[j].lineindex[i].linevertexindex.length==1) {
					Position pos = loadmodel.vertexlist[loadmodel.objects[j].lineindex[i].linevertexindex[0]-1];
					Line newline = new Line(pos.copy(), pos.copy());
					newlinelisttree.add(newline);
					linelisttree.add(newline);
					newnontrianglelinelisttree.add(newline);
				} else {
					Position[] pos = new Position[loadmodel.objects[j].lineindex[i].linevertexindex.length];
					for (int m=0;m<loadmodel.objects[j].lineindex[i].linevertexindex.length;m++) {
						pos[m] = loadmodel.vertexlist[loadmodel.objects[j].lineindex[i].linevertexindex[m]-1];
						if (m>0) {
							Line newline = new Line(pos[m].copy(), pos[m-1].copy());
							newlinelisttree.add(newline);
							linelisttree.add(newline);
							newnontrianglelinelisttree.add(newline);
						}
					}
				}
			}
			newentity.trianglelist = newtrianglelistarray.toArray(new Triangle[newtrianglelistarray.size()]);
			newentity.linelist = newnontrianglelinelisttree.toArray(new Line[newnontrianglelinelisttree.size()]);
			Line[] newlinelist = newlinelisttree.toArray(new Line[newlinelisttree.size()]);
			if (newlinelist.length>0) {
				newentity.vertexlist = MathLib.generateVertexList(newlinelist);
				newentity.aabbboundaryvolume = MathLib.axisAlignedBoundingBox(newentity.vertexlist);
				newentity.sphereboundaryvolume = MathLib.pointCloudCircumSphere(newentity.vertexlist);
				newentitylist.add(newentity);
			}
		}
		Entity[] entitylist = newentitylist.toArray(new Entity[newentitylist.size()]);
		Line[] linelist = linelisttree.toArray(new Line[linelisttree.size()]);
		loadentity.childlist = entitylist;
		loadentity.linelist = linelist;
		loadentity.vertexlist = MathLib.generateVertexList(loadentity.linelist);
		loadentity.aabbboundaryvolume = MathLib.axisAlignedBoundingBox(loadentity.vertexlist);
		loadentity.sphereboundaryvolume = MathLib.pointCloudCircumSphere(loadentity.vertexlist);
		return loadentity;
	}

}
