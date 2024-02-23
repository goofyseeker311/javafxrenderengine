package fi.jkauppa.javarenderengine;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import fi.jkauppa.javarenderengine.ModelLib.AxisAlignedBoundingBox;
import fi.jkauppa.javarenderengine.ModelLib.Coordinate;
import fi.jkauppa.javarenderengine.ModelLib.Cuboid;
import fi.jkauppa.javarenderengine.ModelLib.Direction;
import fi.jkauppa.javarenderengine.ModelLib.Entity;
import fi.jkauppa.javarenderengine.ModelLib.Line;
import fi.jkauppa.javarenderengine.ModelLib.Matrix;
import fi.jkauppa.javarenderengine.ModelLib.Plane;
import fi.jkauppa.javarenderengine.ModelLib.PlaneRay;
import fi.jkauppa.javarenderengine.ModelLib.Position;
import fi.jkauppa.javarenderengine.ModelLib.Quad;
import fi.jkauppa.javarenderengine.ModelLib.Ray;
import fi.jkauppa.javarenderengine.ModelLib.RenderView;
import fi.jkauppa.javarenderengine.ModelLib.Rotation;
import fi.jkauppa.javarenderengine.ModelLib.Sphere;
import fi.jkauppa.javarenderengine.ModelLib.Tetrahedron;
import fi.jkauppa.javarenderengine.ModelLib.Triangle;

public class MathLib {
	public static double sind(double value) {return Math.sin((Math.PI/180.0f)*value);}
	public static double asind(double value) {return (180.0f/Math.PI)*Math.asin(value);}
	public static double cosd(double value) {return Math.cos((Math.PI/180.0f)*value);}
	public static double acosd(double value) {return (180.0f/Math.PI)*Math.acos(value);}
	public static double tand(double value) {return Math.tan((Math.PI/180.0f)*value);}
	public static double atand(double value) {return (180.0f/Math.PI)*Math.atan(value);}
	
	public static double[] vectorDot(Direction[] vdir, Position vpoint){double[] k=null; if((vdir!=null)&&(vpoint!=null)){k=new double[vdir.length];for(int n=0;n<vdir.length;n++){k[n] = vdir[n].dx*vpoint.x+vdir[n].dy*vpoint.y+vdir[n].dz*vpoint.z;}}return k;}
	public static double[] vectorDot(Direction[] vdir, Position[] vpoint){double[] k=null; if((vdir!=null)&&(vpoint!=null)&&(vdir.length==vpoint.length)){k=new double[vdir.length];for(int n=0;n<vdir.length;n++){k[n] = vdir[n].dx*vpoint[n].x+vdir[n].dy*vpoint[n].y+vdir[n].dz*vpoint[n].z;}}return k;}
	public static double[] vectorDot(Direction vdir1, Direction[] vdir2){double[] k=null; if((vdir1!=null)&&(vdir2!=null)){k=new double[vdir2.length];for(int n=0;n<vdir2.length;n++){k[n] = vdir1.dx*vdir2[n].dx+vdir1.dy*vdir2[n].dy+vdir1.dz*vdir2[n].dz;}}return k;}
	public static double vectorDot(Direction vdir1, Direction vdir2){return vdir1.dx*vdir2.dx+vdir1.dy*vdir2.dy+vdir1.dz*vdir2.dz;}
	public static double[] vectorDot(Direction[] vdir1, Direction[] vdir2){double[] k=null; if((vdir1!=null)&&(vdir2!=null)&&(vdir1.length==vdir2.length)){k=new double[vdir1.length];for(int n=0;n<vdir1.length;n++){k[n] = vdir1[n].dx*vdir2[n].dx+vdir1[n].dy*vdir2[n].dy+vdir1[n].dz*vdir2[n].dz;}}return k;}
	public static double[] vectorDot(Direction[] vdir){double[] k=null; if(vdir!=null){k=new double[vdir.length];for(int n=0;n<vdir.length;n++){k[n] = vdir[n].dx*vdir[n].dx+vdir[n].dy*vdir[n].dy+vdir[n].dz*vdir[n].dz;}}return k;}
	public static double vectorDot(Direction vdir){return vdir.dx*vdir.dx+vdir.dy*vdir.dy+vdir.dz*vdir.dz;}
	public static double[] vectorDot(Plane[] vplane, Position vpoint){double[] k=null; if((vplane!=null)&&(vpoint!=null)){k=new double[vplane.length];for(int n=0;n<vplane.length;n++){k[n] = vplane[n].a*vpoint.x+vplane[n].b*vpoint.y+vplane[n].c*vpoint.z+vplane[n].d;}}return k;}
	public static double[] vectorDot(Plane[] vplane, Direction vdir){double[] k=null; if((vplane!=null)&&(vdir!=null)){k=new double[vplane.length];for(int n=0;n<vplane.length;n++){k[n] = vplane[n].a*vdir.dx+vplane[n].b*vdir.dy+vplane[n].c*vdir.dz;}}return k;}

	public static Direction[] vectorCross(Direction vdir1, Direction[] vdir2) {
		Direction[] k=null;
		if ((vdir1!=null)&&(vdir2!=null)) {
			k=new Direction[vdir2.length];
			for (int n=0;n<vdir2.length;n++) {
				k[n] = new Direction(vdir1.dy*vdir2[n].dz-vdir1.dz*vdir2[n].dy,-(vdir1.dx*vdir2[n].dz-vdir1.dz*vdir2[n].dx),vdir1.dx*vdir2[n].dy-vdir1.dy*vdir2[n].dx);
			}
		}
		return k;
	}
	public static Direction[] vectorCross(Direction[] vdir1, Direction vdir2) {
		Direction[] k=null;
		if ((vdir1!=null)&&(vdir2!=null)) {
			k=new Direction[vdir1.length];
			for (int n=0;n<vdir1.length;n++) {
				k[n] = new Direction(vdir1[n].dy*vdir2.dz-vdir1[n].dz*vdir2.dy,-(vdir1[n].dx*vdir2.dz-vdir1[n].dz*vdir2.dx),vdir1[n].dx*vdir2.dy-vdir1[n].dy*vdir2.dx);
			}
		}
		return k;
	}
	public static Direction[] vectorCross(Direction[] vdir1, Direction[] vdir2) {
		Direction[] k=null;
		if ((vdir1!=null)&&(vdir2!=null)&&(vdir1.length==vdir2.length)) {
			k=new Direction[vdir1.length];
			for (int n=0;n<vdir1.length;n++) {
				k[n] = new Direction(vdir1[n].dy*vdir2[n].dz-vdir1[n].dz*vdir2[n].dy,-(vdir1[n].dx*vdir2[n].dz-vdir1[n].dz*vdir2[n].dx),vdir1[n].dx*vdir2[n].dy-vdir1[n].dy*vdir2[n].dx);
			}
		}
		return k;
	}
	public static double[] vectorLength(Direction[] vdir) {
		double[] k = null;
		if (vdir!=null) {
			k = new double[vdir.length];
			double[] vdirlength = vectorDot(vdir);
			for (int n=0;n<vdir.length;n++) {
				k[n] = Math.sqrt(vdirlength[n]);
			}
		}
		return k;
	}
	public static double[] vectorLengthMax(Direction[] vdir) {
		double[] k = null;
		if (vdir!=null) {
			k = new double[vdir.length];
			for (int n=0;n<vdir.length;n++) {
				if (vdir[n]!=null) {
					double vdirlength = vectorDot(vdir[n]);
					k[n] = Math.sqrt(vdirlength);
				} else {
					k[n] = Double.MAX_VALUE;
				}
			}
		}
		return k;
	}
	public static double[] vectorLength(Position[] vpos1,Position[] vpos2) {
		Direction[] lengthvector = vectorFromPoints(vpos1, vpos2);
		return vectorLength(lengthvector);
	}
	public static double[] vectorAngle(Direction vdir1, Direction[] vdir2) {
		double[] k = null;
		if ((vdir1!=null)&&(vdir2!=null)) {
			k = new double[vdir2.length];
			Direction[] vdir1ar = new Direction[1]; vdir1ar[0] = vdir1; 
			double[] vdir1length = vectorLength(vdir1ar);
			double[] vdir2length = vectorLength(vdir2);
			double[] vdir12dot = vectorDot(vdir1,vdir2);
			for (int n=0;n<vdir2.length;n++) {
				k[n] = acosd(vdir12dot[n]/(vdir1length[0]*vdir2length[n]));
			}
		}
		return k;
	}
	public static double[] vectorAngle(Direction[] vdir1, Direction[] vdir2) {
		double[] k = null;
		if ((vdir1!=null)&&(vdir2!=null)&&(vdir1.length==vdir2.length)) {
			k = new double[vdir1.length];
			double[] vdir1length = vectorLength(vdir1);
			double[] vdir2length = vectorLength(vdir2);
			double[] vdir12dot = vectorDot(vdir1,vdir2);
			for (int n=0;n<vdir1.length;n++) {
				k[n] = acosd(vdir12dot[n]/(vdir1length[n]*vdir2length[n]));
			}
		}
		return k;
	}
	public static double[] planeAngle(Plane vplane1, Plane[] vplane2) {
		double[] k = null;
		if ((vplane1!=null)&&(vplane2!=null)) {
			k = new double[vplane2.length];
			Plane[] vplane = {vplane1};
			Direction[] vdir1 = planeNormal(vplane);
			Direction[] vdir2 = planeNormal(vplane2);
			Direction vdir = vdir1[0];
			k = vectorAngle(vdir,vdir2);
		}
		return k;
	}
	public static double[] planeAngle(Plane[] vplane1, Plane[] vplane2) {
		double[] k = null;
		if ((vplane1!=null)&&(vplane2!=null)&&(vplane1.length==vplane2.length)) {
			k = new double[vplane1.length];
			Direction[] vdir1 = planeNormal(vplane1);
			Direction[] vdir2 = planeNormal(vplane2);
			k = vectorAngle(vdir1,vdir2);
		}
		return k;
	}
	public static Direction[] normalizeVector(Direction[] vdir) {
		Direction[] k = null;
		if (vdir!=null) {
			k = new Direction[vdir.length];
			double[] vdirlength =  vectorLength(vdir);
			for (int n=0;n<vdir.length;n++) {
				k[n] = new Direction(vdir[n].dx/vdirlength[n], vdir[n].dy/vdirlength[n], vdir[n].dz/vdirlength[n]);
			}
		}
		return k;
	}
	public static Direction[] planeNormal(Plane[] vplane) {
		Direction[] k = null;
		if (vplane!=null) {
			k = new Direction[vplane.length];
			for (int n=0;n<vplane.length;n++) {
				k[n] = new Direction(vplane[n].a,vplane[n].b,vplane[n].c);
			}
			k = normalizeVector(k);
		}
		return k;
	}
	public static Plane[] planeFromNormalAtPoint(Position vpoint, Direction[] vnormal) {
		Plane[] k = null;
		if ((vpoint!=null)&&(vnormal!=null)) {
			k = new Plane[vnormal.length];
			Direction[] nm = normalizeVector(vnormal);
			double[] dv = vectorDot(nm,vpoint);
			for (int n=0;n<vnormal.length;n++) {
				k[n] = new Plane(nm[n].dx,nm[n].dy,nm[n].dz,-dv[n]);
			}
		}
		return k;
	}
	public static Plane[] planeFromNormalAtPoint(Position[] vpoint, Direction[] vnormal) {
		Plane[] k = null;
		if ((vpoint!=null)&&(vnormal!=null)&&(vpoint.length==vnormal.length)) {
			k = new Plane[vpoint.length];
			Direction[] nm = normalizeVector(vnormal);
			double[] dv = vectorDot(nm,vpoint);
			for (int n=0;n<vpoint.length;n++) {
				k[n] = new Plane(nm[n].dx,nm[n].dy,nm[n].dz,-dv[n]);
			}
		}
		return k;
	}
	public static Direction[] triangleNormal(Triangle[] vtri) {
		Direction[] k = null;
		if (vtri!=null) {
			k = new Direction[vtri.length];
			for (int i=0;i<vtri.length;i++) {
				Direction trianglenorm = vtri[i].norm;
				if ((trianglenorm==null)||(trianglenorm.isZero())) {
					Triangle[] vtriangle = {vtri[i]};
					Plane[] vtriangleplane = trianglePlane(vtriangle);
					Direction[] trianglenormal = planeNormal(vtriangleplane);
					trianglenorm = trianglenormal[0];
				}
				k[i] = trianglenorm;
			}
		}
		return k;
	}
	public static Plane[] trianglePlane(Triangle[] vtri) {
		Plane[] k = null;
		if (vtri!=null) {
			k = new Plane[vtri.length];
			for (int n=0;n<vtri.length;n++) {
				Plane[] pplane = null;
				Position[] p1 = {vtri[n].pos1};
				Direction[] nm = null;
				if ((vtri[n].norm!=null)&&(!vtri[n].norm.isZero())) {
					Direction[] norm = {vtri[n].norm};
					nm = norm;
				} else {
					Position[] p2 = {vtri[n].pos2};
					Position[] p3 = {vtri[n].pos3};
					Direction[] v1 = vectorFromPoints(p1, p2);
					Direction[] v2 = vectorFromPoints(p1, p3);
					nm = normalizeVector(vectorCross(v1, v2));
				}
				pplane = planeFromNormalAtPoint(p1, nm);
				k[n] = pplane[0];
			}
		}
		return k;
	}
	public static Direction[] vectorFromPoints(Position vpoint1, Position[] vpoint2) {
		Direction[] k = null;
		if ((vpoint1!=null)&&(vpoint2!=null)) {
			k = new Direction[vpoint2.length];
			for (int n=0;n<vpoint2.length;n++) {
				if (vpoint2[n]!=null) {
					k[n] = new Direction(vpoint2[n].x-vpoint1.x, vpoint2[n].y-vpoint1.y, vpoint2[n].z-vpoint1.z);
				}
			}
		}
		return k;
	}
	public static Direction[] vectorFromPoints(Position[] vpoint1, Position[] vpoint2) {
		Direction[] k = null;
		if ((vpoint1!=null)&&(vpoint2!=null)&&(vpoint1.length==vpoint2.length)) {
			k = new Direction[vpoint1.length];
			for (int n=0;n<vpoint1.length;n++) {
				if ((vpoint1[n]!=null)&&(vpoint2[n]!=null)) {
					k[n] = new Direction(vpoint2[n].x-vpoint1[n].x, vpoint2[n].y-vpoint1[n].y, vpoint2[n].z-vpoint1[n].z);
				}
			}
		}
		return k;
	}
	public static Direction[] vectorFromPoints(Position vpoint1, Sphere[] vsphere) {
		Direction[] k = null;
		if ((vpoint1!=null)&&(vsphere!=null)) {
			k = new Direction[vsphere.length];
			for (int n=0;n<vsphere.length;n++) {
				if (vsphere[n]!=null) {
					k[n] = new Direction(vsphere[n].x-vpoint1.x, vsphere[n].y-vpoint1.y, vsphere[n].z-vpoint1.z);
				}
			}
		}
		return k;
	}
	public static Direction[] vectorFromPoints(Line[] vline) {
		Direction[] k = null;
		if (vline!=null) {
			k = new Direction[vline.length];
			for (int n=0;n<vline.length;n++) {
				if (vline[n]!=null) {
					k[n] = new Direction(vline[n].pos2.x-vline[n].pos1.x, vline[n].pos2.y-vline[n].pos1.y, vline[n].pos2.z-vline[n].pos1.z);
				}
			}
		}
		return k;
	}
	public static double[][] planePointDistance(Position[] vpoint, Plane[] vplane) {
		double[][] k = null;
		if ((vpoint!=null)&&(vplane!=null)) {
			Direction[] vplanedir = new Direction[vplane.length];
			for (int n=0;n<vplanedir.length;n++) {
				vplanedir[n] = new Direction(vplane[n].a, vplane[n].b, vplane[n].c);
			}
			double[] vplanelen = vectorLength(vplanedir);
			k = new double[vpoint.length][vplane.length];
			for (int n=0;n<vpoint.length;n++) {
				double[] top = vectorDot(vplane, vpoint[n]);
				for (int m=0;m<vplane.length;m++) {
					k[n][m] = top[m]/vplanelen[m];
				}
			}
		}
		return k;
	}
	public static double[][] rayPointDistance(Position vpos, Direction[] vdir, Position[] vpoint) {
		double[][] k = null;
		if ((vpos!=null)&&(vdir!=null)&&(vpoint!=null)) {
			k = new double[vdir.length][vpoint.length];
			Direction[] raypospointdir = vectorFromPoints(vpos, vpoint);
			double[] vdirlength = vectorLength(vdir);
			for (int n=0;n<vdir.length;n++) {
				Direction[] vdircross = vectorCross(vdir[n], raypospointdir);
				double[] vdircrosslen = vectorLength(vdircross); 
				for (int m=0;m<vpoint.length;m++) {
					k[n][m] = vdircrosslen[m]/vdirlength[n];
				}
			}
		}
		return k;
	}
	public static double[][] rayPlaneDistance(Position vpos, Direction[] vdir, Plane[] vplane) {
		double[][] k = null;
		if ((vpos!=null)&&(vdir!=null)&&(vplane!=null)) {
			k = new double[vdir.length][vplane.length];
			for (int n=0;n<vdir.length;n++) {
				double[] top = vectorDot(vplane, vpos);
				double[] bottom = vectorDot(vplane, vdir[n]);
				for (int m=0;m<vplane.length;m++) {
					k[n][m] = -top[m]/bottom[m];
				}
			}
		}
		return k;
	}
	public static Position[][] rayPlaneIntersection(Position vpos, Direction[] vdir, Plane[] vplane) {
		Position[][] k = null;
		if ((vpos!=null)&&(vdir!=null)&&(vplane!=null)) {
			k = new Position[vdir.length][vplane.length];
			Position[] vposa = {vpos};
			double[][] rpdist = rayPlaneDistance(vpos, vdir, vplane);
			for (int n=0;n<vdir.length;n++) {
				for (int m=0;m<vplane.length;m++) {
					if (Double.isFinite(rpdist[n][m])) {
						Position[] rpint = translate(vposa,vdir[n],rpdist[n][m]);
						k[n][m] = rpint[0];
					}
				}
			}
		}
		return k;
	}
	public static Position[][] rayRayIntersection(Ray[] vray1, Ray[] vray2) {
		Position[][] k = null;
		if ((vray1!=null)&&(vray2!=null)) {
			k = new Position[vray1.length][vray2.length];
			for (int m=0;m<vray1.length;m++) {
				for (int n=0;n<vray2.length;n++) {
					Position[] pospt1 = {vray1[m].pos};
					Position[] pospt2 = {vray2[n].pos};
					Direction[] posdir13 = {vray1[m].dir};
					Direction[] posdir23 = {vray2[n].dir};
					Position[] pos13d = translate(pospt1, posdir13[0], 1.0f);
					Position[] pos23d = translate(pospt2, posdir23[0], 1.0f);
					Triangle[] raytriangles = {new Triangle(pospt1[0], pos13d[0], pospt2[0]), new Triangle(pospt2[0], pos23d[0], pospt1[0])};
					Plane[] rayplanes = trianglePlane(raytriangles);
					Direction[] rayplanenormals = planeNormal(rayplanes);
					Direction[] raynorm1 = {rayplanenormals[0]};
					Direction[] raynorm2 = {rayplanenormals[1]};
					double[] rrintangle = vectorAngle(raynorm1, raynorm2);
					if ((rrintangle[0]==0.0f)||(rrintangle[0]==180.0f)) {
						Position pos1 = vray1[m].pos;
						Position pos2 = vray2[n].pos;
						Direction dir1 = vray1[m].dir;
						Direction dir2 = vray2[n].dir;
						Position[] pos1a = {vray1[m].pos};
						double k1xyt = (pos1.x-pos2.x)*dir2.dy-(pos1.y-pos2.y)*dir2.dx;
						double k1xyb = dir1.dy*dir2.dx-dir1.dx*dir2.dy;
						double k1xzt = (pos1.x-pos2.x)*dir2.dz-(pos1.z-pos2.z)*dir2.dx;
						double k1xzb = dir1.dz*dir2.dx-dir1.dx*dir2.dz;
						double k1yzt = (pos1.y-pos2.y)*dir2.dz-(pos1.z-pos2.z)*dir2.dy;
						double k1yzb = dir1.dz*dir2.dy-dir1.dy*dir2.dz;
						if (k1xyb!=0) {
							double k1 = k1xyt/k1xyb;
							Position[] pos3 = translate(pos1a,dir1,k1);
							k[m][n] = pos3[0];
						} else if (k1xzb!=0) {
							double k1 = k1xzt/k1xzb;
							Position[] pos3 = translate(pos1a,dir1,k1);
							k[m][n] = pos3[0];
						} else if (k1yzb!=0) {
							double k1 = k1yzt/k1yzb;
							Position[] pos3 = translate(pos1a,dir1,k1);
							k[m][n] = pos3[0];
						}
					}
				}
			}
		}
		return k;
	}
	
	public static Position[][] rayTriangleIntersection(Position vpos, Direction[] vdir, Triangle[] vtri) {
		Position[][] k = null;
		if ((vpos!=null)&&(vdir!=null)&&(vtri!=null)) {
			k = new Position[vdir.length][vtri.length];
			Plane[] tplanes = trianglePlane(vtri);
			double[][] tpdist = rayPlaneDistance(vpos, vdir, tplanes);
			for (int n=0;n<vdir.length;n++) {
				for (int m=0;m<vtri.length;m++) {
					if (Double.isFinite(tpdist[n][m])) {
						Position[] p4 = {new Position(vpos.x+vdir[n].dx*tpdist[n][m],vpos.y+vdir[n].dy*tpdist[n][m],vpos.z+vdir[n].dz*tpdist[n][m])};
						Position[] p1 = {vtri[m].pos1};
						Position[] p2 = {vtri[m].pos2};
						Position[] p3 = {vtri[m].pos3};
						Direction[] v12 = vectorFromPoints(p1, p2); Direction[] v21 = vectorFromPoints(p2, p1);
						Direction[] v13 = vectorFromPoints(p1, p3); Direction[] v31 = vectorFromPoints(p3, p1);
						Direction[] v23 = vectorFromPoints(p2, p3); Direction[] v32 = vectorFromPoints(p3, p2);
						double[] vl12 = vectorLength(v12);
						double[] vl13 = vectorLength(v13);
						double[] a1 = vectorAngle(v12,v13);
						double[] a2 = vectorAngle(v21,v23);
						double[] a3 = vectorAngle(v31,v32);
						double[] ai1 = vectorAngle(v21,v13);
						Direction[] t1 = vectorFromPoints(p1, p4);
						Direction[] t2 = vectorFromPoints(p2, p4);
						Direction[] t3 = vectorFromPoints(p3, p4);
						double[] tl1 = vectorLength(t1);
						double[] h12 = vectorAngle(v12,t1); double[] h13 = vectorAngle(v13,t1);
						double[] h21 = vectorAngle(v21,t2); double[] h23 = vectorAngle(v23,t2);
						double[] h31 = vectorAngle(v31,t3); double[] h32 = vectorAngle(v32,t3);
						boolean isatpoint1 = (t1[0].dx==0)&&(t1[0].dy==0)&&(t1[0].dz==0);
						boolean isatpoint2 = (t2[0].dx==0)&&(t2[0].dy==0)&&(t2[0].dz==0);
						boolean isatpoint3 = (t3[0].dx==0)&&(t3[0].dy==0)&&(t3[0].dz==0);
						boolean withinangles = (h12[0]<=a1[0])&&(h13[0]<=a1[0])&&(h21[0]<=a2[0])&&(h23[0]<=a2[0])&&(h31[0]<=a3[0])&&(h32[0]<=a3[0]);
						if(isatpoint1||isatpoint2||isatpoint3||withinangles) {
							if ((p1[0].tex!=null)&&(p2[0].tex!=null)&&(p3[0].tex!=null)) {
								if (isatpoint1) {
									p4[0].tex = p1[0].tex.copy();
								} else if (isatpoint2) {
									p4[0].tex = p2[0].tex.copy();
								} else if (isatpoint3) {
									p4[0].tex = p3[0].tex.copy();
								} else {
									double n12len = tl1[0]*(sind(h13[0])/sind(ai1[0]));
									double n13len = tl1[0]*(sind(h12[0])/sind(ai1[0]));
									double n12mult = n12len/vl12[0];
									double n13mult = n13len/vl13[0];
									double u12delta = p2[0].tex.u-p1[0].tex.u;
									double v12delta = p2[0].tex.v-p1[0].tex.v;
									double u13delta = p3[0].tex.u-p1[0].tex.u;
									double v13delta = p3[0].tex.v-p1[0].tex.v;
									p4[0].tex = new Coordinate(p1[0].tex.u+u12delta*n12mult+u13delta*n13mult,p1[0].tex.v+v12delta*n12mult+v13delta*n13mult);
								}
							}
							k[n][m] = p4[0];
						}
					}
				}
			}
		}
		return k;
	}
	public static Position[][] rayQuadIntersection(Position vpos, Direction[] vdir, Quad[] vquad) {
		Position[][] k = null;
		if ((vpos!=null)&&(vdir!=null)&&(vquad!=null)) {
			k = new Position[vdir.length][vquad.length];
			for (int m=0;m<vquad.length;m++) {
				Triangle vaabbtri1 = new Triangle(vquad[m].pos1,vquad[m].pos2,vquad[m].pos3);
				Triangle vaabbtri2 = new Triangle(vquad[m].pos2,vquad[m].pos3,vquad[m].pos4);
				Triangle[] vaabbtrilist = {vaabbtri1,vaabbtri2};
				Position[][] vaabbtrirayint = rayTriangleIntersection(vpos, vdir, vaabbtrilist);
				for (int n=0;n<vaabbtrirayint.length;n++) {
					Position vaabbtrirayhitpos1 = null; 
					for (int i=0;i<vaabbtrirayint[0].length;i++) {
						if (vaabbtrirayint[n][i]!=null) {
							if (vaabbtrirayhitpos1==null) {
								vaabbtrirayhitpos1 = vaabbtrirayint[n][i];
							}
						}
					}
					if (vaabbtrirayhitpos1!=null) {
						k[n][m] = vaabbtrirayhitpos1;
					}
				}
			}
		}
		return k;
	}
	public static Line[][] rayAxisAlignedBoundingBoxIntersection(Position vpos, Direction[] vdir, AxisAlignedBoundingBox[] vaabb) {
		Line[][] k = null;
		if ((vpos!=null)&&(vdir!=null)&&(vaabb!=null)) {
			k = new Line[vdir.length][vaabb.length];
			for (int m=0;m<vaabb.length;m++) {
				double vaabbxmin=vaabb[m].pos1.x; double vaabbxmax=vaabb[m].pos2.x;
				if (vaabbxmin>vaabbxmax) {vaabbxmin=vaabb[m].pos2.x;vaabbxmax=vaabb[m].pos1.x;}
				double vaabbymin=vaabb[m].pos1.y; double vaabbymax=vaabb[m].pos2.y;
				if (vaabbymin>vaabbymax) {vaabbymin=vaabb[m].pos2.y;vaabbymax=vaabb[m].pos1.y;}
				double vaabbzmin=vaabb[m].pos1.z;double vaabbzmax=vaabb[m].pos2.z;
				if (vaabbzmin>vaabbzmax) {vaabbzmin=vaabb[m].pos2.z;vaabbzmax=vaabb[m].pos1.z;}
				Position vaabbpos1 = new Position(vaabbxmin,vaabbymin,vaabbzmin);
				Position vaabbpos2 = new Position(vaabbxmax,vaabbymax,vaabbzmax);
				Direction[] xyzplanenormals = {new Direction(1,0,0),new Direction(0,1,0),new Direction(0,0,1)};
				Plane[] xyzplanes1 = planeFromNormalAtPoint(vaabbpos1, xyzplanenormals);
				Plane[] xyzplanes2 = planeFromNormalAtPoint(vaabbpos2, xyzplanenormals);
				double[][] xyzcp1dist = rayPlaneDistance(vpos, vdir, xyzplanes1);
				double[][] xyzcp2dist = rayPlaneDistance(vpos, vdir, xyzplanes2);
				for (int n=0;n<vdir.length;n++) {
					Position[] xyzplanesint1 = new Position[3]; 
					Position[] xyzplanesint2 = new Position[3]; 
					for (int i=0;i<3;i++) {xyzplanesint1[i] = new Position(vpos.x+xyzcp1dist[n][i]*vdir[n].dx,vpos.y+xyzcp1dist[n][i]*vdir[n].dy,vpos.z+xyzcp1dist[n][i]*vdir[n].dz);}
					for (int i=0;i<3;i++) {xyzplanesint2[i] = new Position(vpos.x+xyzcp2dist[n][i]*vdir[n].dx,vpos.y+xyzcp2dist[n][i]*vdir[n].dy,vpos.z+xyzcp2dist[n][i]*vdir[n].dz);}
					TreeSet<Position> vaabbplanehitpositions = new TreeSet<Position>(); 
					if ((xyzplanesint1[0].y>=vaabbpos1.y)&&(xyzplanesint1[0].y<=vaabbpos2.y)&&(xyzplanesint1[0].z>=vaabbpos1.z)&&(xyzplanesint1[0].z<=vaabbpos2.z)) {
						vaabbplanehitpositions.add(xyzplanesint1[0]);
					}
					if ((xyzplanesint1[1].x>=vaabbpos1.x)&&(xyzplanesint1[1].x<=vaabbpos2.x)&&(xyzplanesint1[1].z>=vaabbpos1.z)&&(xyzplanesint1[1].z<=vaabbpos2.z)) {
						vaabbplanehitpositions.add(xyzplanesint1[1]);
					}
					if ((xyzplanesint1[2].x>=vaabbpos1.x)&&(xyzplanesint1[2].x<=vaabbpos2.x)&&(xyzplanesint1[2].y>=vaabbpos1.y)&&(xyzplanesint1[2].y<=vaabbpos2.y)) {
						vaabbplanehitpositions.add(xyzplanesint1[2]);
					}
					if ((xyzplanesint2[0].y>=vaabbpos1.y)&&(xyzplanesint2[0].y<=vaabbpos2.y)&&(xyzplanesint2[0].z>=vaabbpos1.z)&&(xyzplanesint2[0].z<=vaabbpos2.z)) {
						vaabbplanehitpositions.add(xyzplanesint2[0]);
					}
					if ((xyzplanesint2[1].x>=vaabbpos1.x)&&(xyzplanesint2[1].x<=vaabbpos2.x)&&(xyzplanesint2[1].z>=vaabbpos1.z)&&(xyzplanesint2[1].z<=vaabbpos2.z)) {
						vaabbplanehitpositions.add(xyzplanesint2[1]);
					}
					if ((xyzplanesint2[2].x>=vaabbpos1.x)&&(xyzplanesint2[2].x<=vaabbpos2.x)&&(xyzplanesint2[2].y>=vaabbpos1.y)&&(xyzplanesint2[2].y<=vaabbpos2.y)) {
						vaabbplanehitpositions.add(xyzplanesint2[2]);
					}
					Position[] vaabbplanerayhitposlist = vaabbplanehitpositions.toArray(new Position[vaabbplanehitpositions.size()]);
					if (vaabbplanerayhitposlist.length>1) {
						k[n][m] = new Line(vaabbplanerayhitposlist[0],vaabbplanerayhitposlist[1]);
					} else if (vaabbplanerayhitposlist.length>0) {
						k[n][m] = new Line(vaabbplanerayhitposlist[0],vaabbplanerayhitposlist[0]);
					}
				}
			}
		}
		return k;
	}
	public static Line[][] rayCuboidIntersection(Position vpos, Direction[] vdir, Cuboid[] vcub) {
		Line[][] k = null;
		if ((vpos!=null)&&(vdir!=null)&&(vcub!=null)) {
			k = new Line[vdir.length][vcub.length];
			for (int m=0;m<vcub.length;m++) {
				Triangle vaabbtri1 = new Triangle(vcub[m].pos1,vcub[m].pos2,vcub[m].pos3);
				Triangle vaabbtri2 = new Triangle(vcub[m].pos1,vcub[m].pos3,vcub[m].pos4);
				Triangle vaabbtri3 = new Triangle(vcub[m].pos5,vcub[m].pos6,vcub[m].pos7);
				Triangle vaabbtri4 = new Triangle(vcub[m].pos5,vcub[m].pos7,vcub[m].pos8);
				Triangle vaabbtri5 = new Triangle(vcub[m].pos1,vcub[m].pos2,vcub[m].pos7);
				Triangle vaabbtri6 = new Triangle(vcub[m].pos1,vcub[m].pos7,vcub[m].pos8);
				Triangle vaabbtri7 = new Triangle(vcub[m].pos3,vcub[m].pos4,vcub[m].pos5);
				Triangle vaabbtri8 = new Triangle(vcub[m].pos3,vcub[m].pos5,vcub[m].pos6);
				Triangle vaabbtri9 = new Triangle(vcub[m].pos1,vcub[m].pos4,vcub[m].pos5);
				Triangle vaabbtri10 = new Triangle(vcub[m].pos1,vcub[m].pos5,vcub[m].pos8);
				Triangle vaabbtri11 = new Triangle(vcub[m].pos2,vcub[m].pos3,vcub[m].pos6);
				Triangle vaabbtri12 = new Triangle(vcub[m].pos2,vcub[m].pos6,vcub[m].pos7);
				Triangle[] vaabbtrilist = {vaabbtri1,vaabbtri2,vaabbtri3,vaabbtri4,vaabbtri5,vaabbtri6,vaabbtri7,vaabbtri8,vaabbtri9,vaabbtri10,vaabbtri11,vaabbtri12};
				Position[][] vaabbtrirayint = rayTriangleIntersection(vpos, vdir, vaabbtrilist);
				for (int n=0;n<vaabbtrirayint.length;n++) {
					Position vaabbtrirayhitpos1 = null; 
					Position vaabbtrirayhitpos2 = null;
					for (int i=0;i<vaabbtrirayint[0].length;i++) {
						if (vaabbtrirayint[n][i]!=null) {
							if (vaabbtrirayhitpos1==null) {
								vaabbtrirayhitpos1 = vaabbtrirayint[n][i];
							} else if (vaabbtrirayhitpos1!=vaabbtrirayint[n][i]) {
								vaabbtrirayhitpos2 = vaabbtrirayint[n][i];
							}
						}
					}
					if ((vaabbtrirayhitpos1!=null)&&(vaabbtrirayhitpos2==null)) {
						k[n][m] = new Line(vaabbtrirayhitpos1,vaabbtrirayhitpos1);
					} else if ((vaabbtrirayhitpos1!=null)&&(vaabbtrirayhitpos2!=null)) {
						k[n][m] = new Line(vaabbtrirayhitpos1,vaabbtrirayhitpos2);
					}
				}
			}
		}
		return k;
	}
	public static Line[][] planeTriangleIntersection(Plane[] vplane, Triangle[] vtri) {
		Line[][] k = null;
		if ((vplane!=null)&&(vtri!=null)) {
			k = new Line[vplane.length][vtri.length]; 
			for (int m=0;m<vtri.length;m++) {
				Position[] p1 = new Position[1]; p1[0] = vtri[m].pos1;
				Position[] p2 = new Position[1]; p2[0] = vtri[m].pos2;
				Position[] p3 = new Position[1]; p3[0] = vtri[m].pos3;
				Direction[] vtri12 = vectorFromPoints(p1, p2);
				Direction[] vtri13 = vectorFromPoints(p1, p3);
				Direction[] vtri23 = vectorFromPoints(p2, p3);
				double[][] ptd12 = rayPlaneDistance(vtri[m].pos1, vtri12, vplane);
				double[][] ptd13 = rayPlaneDistance(vtri[m].pos1, vtri13, vplane);
				double[][] ptd23 = rayPlaneDistance(vtri[m].pos2, vtri23, vplane);
				for (int n=0;n<vplane.length;n++) {
					boolean ptlhit12 = (ptd12[0][n]>=0)&(ptd12[0][n]<=1);
					boolean ptlhit13 = (ptd13[0][n]>=0)&(ptd13[0][n]<=1);
					boolean ptlhit23 = (ptd23[0][n]>=0)&(ptd23[0][n]<=1);
					if (ptlhit12|ptlhit13|ptlhit23) {
						Position ptlint12 = new Position(vtri[m].pos1.x+ptd12[0][n]*vtri12[0].dx,vtri[m].pos1.y+ptd12[0][n]*vtri12[0].dy,vtri[m].pos1.z+ptd12[0][n]*vtri12[0].dz);
						Position ptlint13 = new Position(vtri[m].pos1.x+ptd13[0][n]*vtri13[0].dx,vtri[m].pos1.y+ptd13[0][n]*vtri13[0].dy,vtri[m].pos1.z+ptd13[0][n]*vtri13[0].dz);
						Position ptlint23 = new Position(vtri[m].pos2.x+ptd23[0][n]*vtri23[0].dx,vtri[m].pos2.y+ptd23[0][n]*vtri23[0].dy,vtri[m].pos2.z+ptd23[0][n]*vtri23[0].dz);
						if ((vtri[m].pos1.tex!=null)&&(vtri[m].pos2.tex!=null)&&(vtri[m].pos3.tex!=null)) {
							double udelta12 = vtri[m].pos2.tex.u-vtri[m].pos1.tex.u;
							double vdelta12 = vtri[m].pos2.tex.v-vtri[m].pos1.tex.v;
							double udelta13 = vtri[m].pos3.tex.u-vtri[m].pos1.tex.u;
							double vdelta13 = vtri[m].pos3.tex.v-vtri[m].pos1.tex.v;
							double udelta23 = vtri[m].pos3.tex.u-vtri[m].pos2.tex.u;
							double vdelta23 = vtri[m].pos3.tex.v-vtri[m].pos2.tex.v;
							ptlint12.tex = new Coordinate(vtri[m].pos1.tex.u+ptd12[0][n]*udelta12,vtri[m].pos1.tex.v+ptd12[0][n]*vdelta12);
							ptlint13.tex = new Coordinate(vtri[m].pos1.tex.u+ptd13[0][n]*udelta13,vtri[m].pos1.tex.v+ptd13[0][n]*vdelta13);
							ptlint23.tex = new Coordinate(vtri[m].pos2.tex.u+ptd23[0][n]*udelta23,vtri[m].pos2.tex.v+ptd23[0][n]*vdelta23);
						}
						if (ptlhit12&&ptlhit13) {
							k[n][m] = new Line(ptlint12,ptlint13);
						} else if (ptlhit12&&ptlhit23) {
							k[n][m] = new Line(ptlint12,ptlint23);
						} else if (ptlhit13&&ptlhit23) {
							k[n][m] = new Line(ptlint13,ptlint23);
						}
					}
				}
			}
		}
		return k;
	}
	public static Position[][] planeLineIntersection(Plane[] vplane, Line[] vline) {
		Position[][] k = null;
		if ((vplane!=null)&&(vline!=null)) {
			k = new Position[vplane.length][vline.length]; 
			for (int m=0;m<vline.length;m++) {
				Position[] p1 = new Position[1]; p1[0] = vline[m].pos1;
				Position[] p2 = new Position[1]; p2[0] = vline[m].pos2;
				Direction[] vline12 = vectorFromPoints(p1, p2);
				double[][] ptd1 = rayPlaneDistance(vline[m].pos1, vline12, vplane);
				for (int n=0;n<vplane.length;n++) {
					if ((ptd1[0][n]>=0)&(ptd1[0][n]<=1)) {
						Position ptlint = new Position(vline[m].pos1.x+ptd1[0][n]*vline12[0].dx,vline[m].pos1.y+ptd1[0][n]*vline12[0].dy,vline[m].pos1.z+ptd1[0][n]*vline12[0].dz);
						if ((vline[m].pos1.tex!=null)&&(vline[m].pos2.tex!=null)) {
							double udelta = vline[m].pos2.tex.u-vline[m].pos1.tex.u;
							double vdelta = vline[m].pos2.tex.v-vline[m].pos1.tex.v;
							ptlint.tex = new Coordinate(vline[m].pos1.tex.u+ptd1[0][n]*udelta,vline[m].pos1.tex.v+ptd1[0][n]*vdelta);
						}
						k[n][m] = ptlint;
					}
				}
			}
		}
		return k;
	}
	public static Line[][] planePlaneIntersection(Plane[] vplane1, Plane[] vplane2) {
		Line[][] k = null;
		if ((vplane1!=null)&&(vplane2!=null)) {
			k = new Line[vplane1.length][vplane2.length];
			Direction[] vplanenorm1 = planeNormal(vplane1);
			Direction[] vplanenorm2 = planeNormal(vplane2);
			for (int m=0;m<vplane1.length;m++) {
				double[] ppintangle = vectorAngle(vplanenorm1[m], vplanenorm2);
				for (int n=0;n<vplane2.length;n++) {
					if ((ppintangle[n]>0.0f)&&(ppintangle[n]<180.0f)) {
						Plane vpplane1 = vplane1[m];
						Plane vpplane2 = vplane2[n];
						Direction[] vplanedir1 = {vplanenorm1[m]};
						Direction[] vplanedir2 = {vplanenorm2[n]};
						Direction[] ppintdir = vectorCross(vplanedir1, vplanedir2);
						Direction[] ppintdirn = normalizeVector(ppintdir);
						double z = 0.0f;
						double y = 0.0f;
						double x = 0.0f;
						if (vpplane1.a!=0) {
							double kw = vpplane2.d*vpplane1.a-vpplane1.d*vpplane2.a;
							if (vpplane2.b!=0) {
								double kb = vpplane1.b*vpplane2.a-vpplane2.b*vpplane1.a;
								z = 0.0f;
								y = kw/kb;
								x = (-vpplane1.d-vpplane1.b)/vpplane1.a;
							}else if (vpplane2.c!=0) {
								double kb = vpplane1.c*vpplane2.a-vpplane2.c*vpplane1.a;
								z = kw/kb;
								y = 0.0f;
								x = (-vpplane1.d-vpplane1.c)/vpplane1.a;
							}
						} else if (vpplane1.b!=0) {
							double kw = vpplane2.d*vpplane1.b-vpplane1.d*vpplane2.b;
							if (vpplane2.a!=0) {
								double kb = vpplane1.a*vpplane2.b-vpplane2.a*vpplane1.b;
								z = 0.0f;
								y = (-vpplane1.d-vpplane1.a)/vpplane1.b;
								x = kw/kb;
							}else if (vpplane2.c!=0) {
								double kb = vpplane1.c*vpplane2.b-vpplane2.c*vpplane1.b;
								z = kw/kb;
								y = (-vpplane1.d-vpplane1.c)/vpplane1.b;
								x = 0.0f;
							}
						} else if (vpplane1.c!=0) {
							double kw = vpplane2.d*vpplane1.c-vpplane1.d*vpplane2.c;
							if (vpplane2.a!=0) {
								double kb = vpplane1.a*vpplane2.c-vpplane2.a*vpplane1.c;
								z = (-vpplane1.d-vpplane1.a)/vpplane1.c;
								y = 0.0f;
								x = kw/kb;
							}else if (vpplane2.b!=0) {
								double kb = vpplane1.b*vpplane2.c-vpplane2.b*vpplane1.c;
								z = (-vpplane1.d-vpplane1.b)/vpplane1.c;
								y = kw/kb;
								x = 0.0f;
							}
						}
						Position[] ppintpos = {new Position(x,y,z)};
						Position[] ppintpos2 = translate(ppintpos, ppintdirn[0], 1.0f);
						k[m][n] = new Line(ppintpos[0], ppintpos2[0]);
					}
				}
			}
		}
		return k;
	}
	
	public static boolean[][] sphereSphereIntersection(Sphere[] vsphere1, Sphere[] vsphere2) {
		boolean[][] k = null;
		if ((vsphere1.length>0)&&(vsphere2.length>0)) {
			k = new boolean[vsphere1.length][vsphere2.length];
			for (int j=0;j<vsphere1.length;j++) {
				for (int i=0;i<vsphere2.length;i++) {
					k[j][i] = Math.sqrt(Math.pow(vsphere2[i].x-vsphere1[j].x,2)+Math.pow(vsphere2[i].y-vsphere1[j].y,2)+Math.pow(vsphere2[i].z-vsphere1[j].z,2))<=(vsphere2[i].r+vsphere1[j].r); 
				}
			}
		}
		return k;
	}
	
	public static Integer[][] mutualSphereIntersection(Sphere[] vsphere) {
		Integer[][] k = new Integer[vsphere.length][0];
		double[] xlist = new double[2*vsphere.length];
		double[] ylist = new double[2*vsphere.length];
		double[] zlist = new double[2*vsphere.length];
		for (int i=0; i<vsphere.length; i++) {
			xlist[2*i] = vsphere[i].x-vsphere[i].r;
			xlist[2*i+1] = vsphere[i].x+vsphere[i].r;
			ylist[2*i] = vsphere[i].y-vsphere[i].r;
			ylist[2*i+1] = vsphere[i].y+vsphere[i].r;
			zlist[2*i] = vsphere[i].z-vsphere[i].r;
			zlist[2*i+1] = vsphere[i].z+vsphere[i].r;
		}
		int[] xlistsidx = UtilLib.indexSort(xlist);
		int[] ylistsidx = UtilLib.indexSort(ylist);
		int[] zlistsidx = UtilLib.indexSort(zlist);
		double[] xlistsval = UtilLib.indexValues(xlist,xlistsidx);
		double[] ylistsval = UtilLib.indexValues(ylist,ylistsidx);
		double[] zlistsval = UtilLib.indexValues(zlist,zlistsidx);
		for (int j=0;j<vsphere.length;j++) {
			int xlistidx1 = Arrays.binarySearch(xlistsval,xlist[j*2]);
			int xlistidx2 = Arrays.binarySearch(xlistsval,xlist[j*2+1]);
			int ylistidx1 = Arrays.binarySearch(ylistsval,ylist[j*2]);
			int ylistidx2 = Arrays.binarySearch(ylistsval,ylist[j*2+1]);
			int zlistidx1 = Arrays.binarySearch(zlistsval,zlist[j*2]);
			int zlistidx2 = Arrays.binarySearch(zlistsval,zlist[j*2+1]);
			HashSet<Integer> mutualxindex = new HashSet<Integer>();
			HashSet<Integer> mutualyindex = new HashSet<Integer>();
			HashSet<Integer> mutualzindex = new HashSet<Integer>();
			for (int i=xlistidx1;i<xlistidx2;i++) {
				mutualxindex.add(Math.floorDiv(xlistsidx[i],2));
			}
			for (int i=ylistidx1;i<ylistidx2;i++) {
				mutualyindex.add(Math.floorDiv(ylistsidx[i],2));
			}
			for (int i=zlistidx1;i<zlistidx2;i++) {
				mutualzindex.add(Math.floorDiv(zlistsidx[i],2));
			}
			HashSet<Integer> mutualindex = new HashSet<Integer>(Arrays.asList(mutualxindex.toArray(new Integer[mutualxindex.size()])));
			mutualindex.retainAll(mutualyindex);
			mutualindex.retainAll(mutualzindex);
			mutualindex.remove(j);
			Integer[] intersectionlist = mutualindex.toArray(new Integer[mutualindex.size()]);
			Sphere[] intersectionspheres = new Sphere[intersectionlist.length];
			for (int i=0;i<intersectionlist.length;i++) {
				intersectionspheres[i] = vsphere[intersectionlist[i]];
			}
			Sphere[] vspherei = {vsphere[j]}; 
			boolean[][] sphereintersection = sphereSphereIntersection(vspherei, intersectionspheres);
			ArrayList<Integer> intersectinglist = new ArrayList<Integer>();
			for (int i=0;i<intersectionlist.length;i++) {
				if (sphereintersection[0][i]) {
					intersectinglist.add(intersectionlist[i]);
				}
			}
			k[j] = intersectinglist.toArray(new Integer[intersectinglist.size()]);
		}
		for (int j=0;j<k.length;j++) {
			for (int i=0;i<k[j].length;i++) {
				HashSet<Integer> newlist = new HashSet<Integer>(Arrays.asList(k[k[j][i]]));
				newlist.add(j);
				k[k[j][i]] = newlist.toArray(new Integer[newlist.size()]);
			}
		}
		return k;
	}
	
	public static boolean[] vertexAxisAlignedBoundingBoxIntersection(AxisAlignedBoundingBox vaabb, Position[] vpoint) {
		boolean[] k = null;
		if ((vaabb!=null)&&(vpoint!=null)) {
			k = new boolean[vpoint.length];
			for (int i=0;i<vpoint.length;i++) {
				k[i] = false;
				if ((vpoint[i].x>=vaabb.pos1.x)&&(vpoint[i].x<=vaabb.pos2.x)&&(vpoint[i].y>=vaabb.pos1.y)&&(vpoint[i].y<=vaabb.pos2.y)&&(vpoint[i].z>=vaabb.pos1.z)&&(vpoint[i].z<=vaabb.pos2.z)) {
					k[i] = true;
				}
			}
		}
		return k;
	}
	public static boolean[] lineAxisAlignedBoundingBoxIntersection(AxisAlignedBoundingBox vaabb, Line[] vline) {
		//TODO include intersection lines that completely cross over the axis aligned bounding box, not only those lines that have vertices inside the bounding box 
		boolean[] k = null;
		if ((vaabb!=null)&&(vline!=null)) {
			k = new boolean[vline.length];
			for (int i=0;i<vline.length;i++) {
				k[i] = false;
				Position[] vpoint = {vline[i].pos1,vline[i].pos2};
				boolean[] vertexint = vertexAxisAlignedBoundingBoxIntersection(vaabb, vpoint);
				if (vertexint[0]||vertexint[1]) {
					k[i] = true;
				}
			}
		}
		return k;
	}
	public static boolean[] triangleAxisAlignedBoundingBoxIntersection(AxisAlignedBoundingBox vaabb, Triangle[] vtri) {
		boolean[] k = null;
		if ((vaabb!=null)&&(vtri!=null)) {
			k = new boolean[vtri.length];
			for (int i=0;i<vtri.length;i++) {
				k[i] = false;
				Position[] vpoint = {vtri[i].pos1,vtri[i].pos2,vtri[i].pos3};
				boolean[] vertexint = vertexAxisAlignedBoundingBoxIntersection(vaabb, vpoint);
				if (vertexint[0]||vertexint[1]||vertexint[2]) {
					k[i] = true;
				}
			}
		}
		return k;
	}
	public static boolean[] tetrahedronAxisAlignedBoundingBoxIntersection(AxisAlignedBoundingBox vaabb, Tetrahedron[] vtet) {
		boolean[] k = null;
		if ((vaabb!=null)&&(vtet!=null)) {
			k = new boolean[vtet.length];
			for (int i=0;i<vtet.length;i++) {
				k[i] = false;
				Position[] vpoint = {vtet[i].pos1,vtet[i].pos2,vtet[i].pos3,vtet[i].pos4};
				boolean[] vertexint = vertexAxisAlignedBoundingBoxIntersection(vaabb, vpoint);
				if (vertexint[0]||vertexint[1]||vertexint[2]||vertexint[3]) {
					k[i] = true;
				}
			}
		}
		return k;
	}
	
	public static Matrix matrixMultiply(Matrix vmat1, Matrix vmat2) {
		Matrix k = null;
		if ((vmat1!=null)&&(vmat2!=null)) {
			k = new Matrix(
					vmat1.a11*vmat2.a11+vmat1.a12*vmat2.a21+vmat1.a13*vmat2.a31,
					vmat1.a11*vmat2.a12+vmat1.a12*vmat2.a22+vmat1.a13*vmat2.a32,
					vmat1.a11*vmat2.a13+vmat1.a12*vmat2.a23+vmat1.a13*vmat2.a33,
					vmat1.a21*vmat2.a11+vmat1.a22*vmat2.a21+vmat1.a23*vmat2.a31,
					vmat1.a21*vmat2.a12+vmat1.a22*vmat2.a22+vmat1.a23*vmat2.a32,
					vmat1.a21*vmat2.a13+vmat1.a22*vmat2.a23+vmat1.a23*vmat2.a33,
					vmat1.a31*vmat2.a11+vmat1.a32*vmat2.a21+vmat1.a33*vmat2.a31,
					vmat1.a31*vmat2.a12+vmat1.a32*vmat2.a22+vmat1.a33*vmat2.a32,
					vmat1.a31*vmat2.a13+vmat1.a32*vmat2.a23+vmat1.a33*vmat2.a33
					);
		}
		return k;
	}
	public static Position[] matrixMultiply(Position[] vpoint, Matrix vmat) {
		Position[] k = null;
		if ((vpoint!=null)&&(vmat!=null)) {
			k = new Position[vpoint.length];
			for (int n=0;n<vpoint.length;n++) {
				k[n] = vpoint[n].copy();
				k[n].x = vpoint[n].x*vmat.a11+vpoint[n].y*vmat.a12+vpoint[n].z*vmat.a13;
				k[n].y = vpoint[n].x*vmat.a21+vpoint[n].y*vmat.a22+vpoint[n].z*vmat.a23;
				k[n].z = vpoint[n].x*vmat.a31+vpoint[n].y*vmat.a32+vpoint[n].z*vmat.a33;
			}
		}
		return k;
	}
	public static Direction[] matrixMultiply(Direction[] vdir, Matrix vmat) {
		Direction[] k = null;
		if ((vdir!=null)&&(vmat!=null)) {
			k = new Direction[vdir.length];
			for (int n=0;n<vdir.length;n++) {
				k[n] = vdir[n].copy();
				k[n].dx = vdir[n].dx*vmat.a11+vdir[n].dy*vmat.a12+vdir[n].dz*vmat.a13;
				k[n].dy = vdir[n].dx*vmat.a21+vdir[n].dy*vmat.a22+vdir[n].dz*vmat.a23;
				k[n].dz = vdir[n].dx*vmat.a31+vdir[n].dy*vmat.a32+vdir[n].dz*vmat.a33;
			}
		}
		return k;
	}
	public static Coordinate[] matrixMultiply(Coordinate[] vcoord, Matrix vmat) {
		Coordinate[] k = null;
		if ((vcoord!=null)&&(vmat!=null)) {
			k = new Coordinate[vcoord.length];
			for (int n=0;n<vcoord.length;n++) {
				k[n] = vcoord[n].copy();
				k[n].u = vcoord[n].u*vmat.a11+vcoord[n].v*vmat.a12;
				k[n].v = vcoord[n].u*vmat.a21+vcoord[n].v*vmat.a22;
			}
		}
		return k;
	}
	public static Line[] matrixMultiply(Line[] vline, Matrix vmat) {
		Line[] k = null;
		if ((vline!=null)&&(vmat!=null)) {
			k = new Line[vline.length];
			for (int n=0;n<vline.length;n++) {
				k[n] = vline[n].copy();
				k[n].pos1.x = vline[n].pos1.x*vmat.a11+vline[n].pos1.y*vmat.a12+vline[n].pos1.z*vmat.a13;
				k[n].pos1.y = vline[n].pos1.x*vmat.a21+vline[n].pos1.y*vmat.a22+vline[n].pos1.z*vmat.a23;
				k[n].pos1.z = vline[n].pos1.x*vmat.a31+vline[n].pos1.y*vmat.a32+vline[n].pos1.z*vmat.a33;
				k[n].pos2.x = vline[n].pos2.x*vmat.a11+vline[n].pos2.y*vmat.a12+vline[n].pos2.z*vmat.a13;
				k[n].pos2.y = vline[n].pos2.x*vmat.a21+vline[n].pos2.y*vmat.a22+vline[n].pos2.z*vmat.a23;
				k[n].pos2.z = vline[n].pos2.x*vmat.a31+vline[n].pos2.y*vmat.a32+vline[n].pos2.z*vmat.a33;
			}
		}
		return k;
	}
	public static Sphere[] matrixMultiply(Sphere[] vsph, Matrix vmat) {
		Sphere[] k = null;
		if ((vsph!=null)&&(vmat!=null)) {
			k = new Sphere[vsph.length];
			for (int n=0;n<vsph.length;n++) {
				k[n] = vsph[n].copy();
				k[n].x = vsph[n].x*vmat.a11+vsph[n].y*vmat.a12+vsph[n].z*vmat.a13;
				k[n].y = vsph[n].x*vmat.a21+vsph[n].y*vmat.a22+vsph[n].z*vmat.a23;
				k[n].z = vsph[n].x*vmat.a31+vsph[n].y*vmat.a32+vsph[n].z*vmat.a33;
			}
		}
		return k;
	}
	public static Triangle[] matrixMultiply(Triangle[] vtri, Matrix vmat) {
		Triangle[] k = null;
		if ((vtri!=null)&&(vmat!=null)) {
			k = new Triangle[vtri.length];
			for (int n=0;n<vtri.length;n++) {
				k[n] = vtri[n].copy();
				k[n].pos1.x = vtri[n].pos1.x*vmat.a11+vtri[n].pos1.y*vmat.a12+vtri[n].pos1.z*vmat.a13;
				k[n].pos1.y = vtri[n].pos1.x*vmat.a21+vtri[n].pos1.y*vmat.a22+vtri[n].pos1.z*vmat.a23;
				k[n].pos1.z = vtri[n].pos1.x*vmat.a31+vtri[n].pos1.y*vmat.a32+vtri[n].pos1.z*vmat.a33;
				k[n].pos2.x = vtri[n].pos2.x*vmat.a11+vtri[n].pos2.y*vmat.a12+vtri[n].pos2.z*vmat.a13;
				k[n].pos2.y = vtri[n].pos2.x*vmat.a21+vtri[n].pos2.y*vmat.a22+vtri[n].pos2.z*vmat.a23;
				k[n].pos2.z = vtri[n].pos2.x*vmat.a31+vtri[n].pos2.y*vmat.a32+vtri[n].pos2.z*vmat.a33;
				k[n].pos3.x = vtri[n].pos3.x*vmat.a11+vtri[n].pos3.y*vmat.a12+vtri[n].pos3.z*vmat.a13;
				k[n].pos3.y = vtri[n].pos3.x*vmat.a21+vtri[n].pos3.y*vmat.a22+vtri[n].pos3.z*vmat.a23;
				k[n].pos3.z = vtri[n].pos3.x*vmat.a31+vtri[n].pos3.y*vmat.a32+vtri[n].pos3.z*vmat.a33;
			}
		}
		return k;
	}
	public static Cuboid[] matrixMultiply(Cuboid[] vcuboid, Matrix vmat) {
		Cuboid[] k = null;
		if ((vcuboid!=null)&&(vmat!=null)) {
			k = new Cuboid[vcuboid.length];
			for (int n=0;n<vcuboid.length;n++) {
				k[n] = vcuboid[n].copy();
				k[n].pos1.x = vcuboid[n].pos1.x*vmat.a11+vcuboid[n].pos1.y*vmat.a12+vcuboid[n].pos1.z*vmat.a13;
				k[n].pos1.y = vcuboid[n].pos1.x*vmat.a21+vcuboid[n].pos1.y*vmat.a22+vcuboid[n].pos1.z*vmat.a23;
				k[n].pos1.z = vcuboid[n].pos1.x*vmat.a31+vcuboid[n].pos1.y*vmat.a32+vcuboid[n].pos1.z*vmat.a33;
				k[n].pos2.x = vcuboid[n].pos2.x*vmat.a11+vcuboid[n].pos2.y*vmat.a12+vcuboid[n].pos2.z*vmat.a13;
				k[n].pos2.y = vcuboid[n].pos2.x*vmat.a21+vcuboid[n].pos2.y*vmat.a22+vcuboid[n].pos2.z*vmat.a23;
				k[n].pos2.z = vcuboid[n].pos2.x*vmat.a31+vcuboid[n].pos2.y*vmat.a32+vcuboid[n].pos2.z*vmat.a33;
				k[n].pos3.x = vcuboid[n].pos3.x*vmat.a11+vcuboid[n].pos3.y*vmat.a12+vcuboid[n].pos3.z*vmat.a13;
				k[n].pos3.y = vcuboid[n].pos3.x*vmat.a21+vcuboid[n].pos3.y*vmat.a22+vcuboid[n].pos3.z*vmat.a23;
				k[n].pos3.z = vcuboid[n].pos3.x*vmat.a31+vcuboid[n].pos3.y*vmat.a32+vcuboid[n].pos3.z*vmat.a33;
				k[n].pos4.x = vcuboid[n].pos4.x*vmat.a11+vcuboid[n].pos4.y*vmat.a12+vcuboid[n].pos4.z*vmat.a13;
				k[n].pos4.y = vcuboid[n].pos4.x*vmat.a21+vcuboid[n].pos4.y*vmat.a22+vcuboid[n].pos4.z*vmat.a23;
				k[n].pos4.z = vcuboid[n].pos4.x*vmat.a31+vcuboid[n].pos4.y*vmat.a32+vcuboid[n].pos4.z*vmat.a33;
				k[n].pos5.x = vcuboid[n].pos5.x*vmat.a11+vcuboid[n].pos5.y*vmat.a12+vcuboid[n].pos5.z*vmat.a13;
				k[n].pos5.y = vcuboid[n].pos5.x*vmat.a21+vcuboid[n].pos5.y*vmat.a22+vcuboid[n].pos5.z*vmat.a23;
				k[n].pos5.z = vcuboid[n].pos5.x*vmat.a31+vcuboid[n].pos5.y*vmat.a32+vcuboid[n].pos5.z*vmat.a33;
				k[n].pos6.x = vcuboid[n].pos6.x*vmat.a11+vcuboid[n].pos6.y*vmat.a12+vcuboid[n].pos6.z*vmat.a13;
				k[n].pos6.y = vcuboid[n].pos6.x*vmat.a21+vcuboid[n].pos6.y*vmat.a22+vcuboid[n].pos6.z*vmat.a23;
				k[n].pos6.z = vcuboid[n].pos6.x*vmat.a31+vcuboid[n].pos6.y*vmat.a32+vcuboid[n].pos6.z*vmat.a33;
				k[n].pos7.x = vcuboid[n].pos7.x*vmat.a11+vcuboid[n].pos7.y*vmat.a12+vcuboid[n].pos7.z*vmat.a13;
				k[n].pos7.y = vcuboid[n].pos7.x*vmat.a21+vcuboid[n].pos7.y*vmat.a22+vcuboid[n].pos7.z*vmat.a23;
				k[n].pos7.z = vcuboid[n].pos7.x*vmat.a31+vcuboid[n].pos7.y*vmat.a32+vcuboid[n].pos7.z*vmat.a33;
				k[n].pos8.x = vcuboid[n].pos8.x*vmat.a11+vcuboid[n].pos8.y*vmat.a12+vcuboid[n].pos8.z*vmat.a13;
				k[n].pos8.y = vcuboid[n].pos8.x*vmat.a21+vcuboid[n].pos8.y*vmat.a22+vcuboid[n].pos8.z*vmat.a23;
				k[n].pos8.z = vcuboid[n].pos8.x*vmat.a31+vcuboid[n].pos8.y*vmat.a32+vcuboid[n].pos8.z*vmat.a33;
			}
		}
		return k;
	}
	public static Quad[] matrixMultiply(Quad[] vquad, Matrix vmat) {
		Quad[] k = null;
		if ((vquad!=null)&&(vmat!=null)) {
			k = new Quad[vquad.length];
			for (int n=0;n<vquad.length;n++) {
				k[n] = vquad[n].copy();
				k[n].pos1.x = vquad[n].pos1.x*vmat.a11+vquad[n].pos1.y*vmat.a12+vquad[n].pos1.z*vmat.a13;
				k[n].pos1.y = vquad[n].pos1.x*vmat.a21+vquad[n].pos1.y*vmat.a22+vquad[n].pos1.z*vmat.a23;
				k[n].pos1.z = vquad[n].pos1.x*vmat.a31+vquad[n].pos1.y*vmat.a32+vquad[n].pos1.z*vmat.a33;
				k[n].pos2.x = vquad[n].pos2.x*vmat.a11+vquad[n].pos2.y*vmat.a12+vquad[n].pos2.z*vmat.a13;
				k[n].pos2.y = vquad[n].pos2.x*vmat.a21+vquad[n].pos2.y*vmat.a22+vquad[n].pos2.z*vmat.a23;
				k[n].pos2.z = vquad[n].pos2.x*vmat.a31+vquad[n].pos2.y*vmat.a32+vquad[n].pos2.z*vmat.a33;
				k[n].pos3.x = vquad[n].pos3.x*vmat.a11+vquad[n].pos3.y*vmat.a12+vquad[n].pos3.z*vmat.a13;
				k[n].pos3.y = vquad[n].pos3.x*vmat.a21+vquad[n].pos3.y*vmat.a22+vquad[n].pos3.z*vmat.a23;
				k[n].pos3.z = vquad[n].pos3.x*vmat.a31+vquad[n].pos3.y*vmat.a32+vquad[n].pos3.z*vmat.a33;
				k[n].pos4.x = vquad[n].pos4.x*vmat.a11+vquad[n].pos4.y*vmat.a12+vquad[n].pos4.z*vmat.a13;
				k[n].pos4.y = vquad[n].pos4.x*vmat.a21+vquad[n].pos4.y*vmat.a22+vquad[n].pos4.z*vmat.a23;
				k[n].pos4.z = vquad[n].pos4.x*vmat.a31+vquad[n].pos4.y*vmat.a32+vquad[n].pos4.z*vmat.a33;
			}
		}
		return k;
	}
	public static AxisAlignedBoundingBox[] matrixMultiply(AxisAlignedBoundingBox[] vaabb, Matrix vmat) {
		AxisAlignedBoundingBox[] k = null;
		if ((vaabb!=null)&&(vmat!=null)) {
			k = new AxisAlignedBoundingBox[vaabb.length];
			for (int n=0;n<vaabb.length;n++) {
				k[n] = vaabb[n].copy();
				k[n].pos1.x = vaabb[n].pos1.x*vmat.a11+vaabb[n].pos1.y*vmat.a12+vaabb[n].pos1.z*vmat.a13;
				k[n].pos1.y = vaabb[n].pos1.x*vmat.a21+vaabb[n].pos1.y*vmat.a22+vaabb[n].pos1.z*vmat.a23;
				k[n].pos1.z = vaabb[n].pos1.x*vmat.a31+vaabb[n].pos1.y*vmat.a32+vaabb[n].pos1.z*vmat.a33;
				k[n].pos2.x = vaabb[n].pos2.x*vmat.a11+vaabb[n].pos2.y*vmat.a12+vaabb[n].pos2.z*vmat.a13;
				k[n].pos2.y = vaabb[n].pos2.x*vmat.a21+vaabb[n].pos2.y*vmat.a22+vaabb[n].pos2.z*vmat.a23;
				k[n].pos2.z = vaabb[n].pos2.x*vmat.a31+vaabb[n].pos2.y*vmat.a32+vaabb[n].pos2.z*vmat.a33;
			}
		}
		return k;
	}
	public static Position[] translate(Position[] vpoint, Position vpos) {
		Position[] k = null;
		if ((vpoint!=null)&&(vpos!=null)) {
			k = new Position[vpoint.length];
			for (int n=0;n<vpoint.length;n++) {
				k[n] = vpoint[n].copy();
				k[n].x = vpoint[n].x+vpos.x;
				k[n].y = vpoint[n].y+vpos.y;
				k[n].z = vpoint[n].z+vpos.z;
			}
		}
		return k;
	}
	public static Direction[] translate(Direction[] vdir, Position vpos) {
		Direction[] k = null;
		if ((vdir!=null)&&(vpos!=null)) {
			k = new Direction[vdir.length];
			for (int n=0;n<vdir.length;n++) {
				k[n] = vdir[n].copy();
				k[n].dx = vdir[n].dx+vpos.x;
				k[n].dy = vdir[n].dy+vpos.y;
				k[n].dz = vdir[n].dz+vpos.z;
			}
		}
		return k;
	}
	public static Coordinate[] translate(Coordinate[] vcoord, Position vpos) {
		Coordinate[] k = null;
		if ((vcoord!=null)&&(vpos!=null)) {
			k = new Coordinate[vcoord.length];
			for (int n=0;n<vcoord.length;n++) {
				k[n] = vcoord[n].copy();
				k[n].u = vcoord[n].u+vpos.x;
				k[n].v = vcoord[n].v+vpos.y;
			}
		}
		return k;
	}
	public static Line[] translate(Line[] vline, Position vpos) {
		Line[] k = null;
		if ((vline!=null)&&(vpos!=null)) {
			k = new Line[vline.length];
			for (int n=0;n<vline.length;n++) {
				k[n] = vline[n].copy();
				k[n].pos1.x = vline[n].pos1.x+vpos.x;
				k[n].pos1.y = vline[n].pos1.y+vpos.y;
				k[n].pos1.z = vline[n].pos1.z+vpos.z;
				k[n].pos2.x = vline[n].pos2.x+vpos.x;
				k[n].pos2.y = vline[n].pos2.y+vpos.y;
				k[n].pos2.z = vline[n].pos2.z+vpos.z;
			}
		}
		return k;
	}
	public static Sphere[] translate(Sphere[] vsph, Position vpos) {
		Sphere[] k = null;
		if ((vsph!=null)&&(vpos!=null)) {
			k = new Sphere[vsph.length];
			for (int n=0;n<vsph.length;n++) {
				k[n] = vsph[n].copy();
				k[n].x = vsph[n].x+vpos.x;
				k[n].y = vsph[n].y+vpos.y;
				k[n].z = vsph[n].z+vpos.z;
			}
		}
		return k;
	}
	public static Plane[] translate(Plane[] vplane, Position vpos) {
		Direction[] planenormals = planeNormal(vplane);
		return planeFromNormalAtPoint(vpos, planenormals);
	}
	public static Triangle[] translate(Triangle[] vtri, Position vpos) {
		Triangle[] k = null;
		if ((vtri!=null)&&(vpos!=null)) {
			k = new Triangle[vtri.length];
			for (int n=0;n<vtri.length;n++) {
				k[n] = vtri[n].copy();
				k[n].pos1.x = vtri[n].pos1.x+vpos.x;
				k[n].pos1.y = vtri[n].pos1.y+vpos.y;
				k[n].pos1.z = vtri[n].pos1.z+vpos.z;
				k[n].pos2.x = vtri[n].pos2.x+vpos.x;
				k[n].pos2.y = vtri[n].pos2.y+vpos.y;
				k[n].pos2.z = vtri[n].pos2.z+vpos.z;
				k[n].pos3.x = vtri[n].pos3.x+vpos.x;
				k[n].pos3.y = vtri[n].pos3.y+vpos.y;
				k[n].pos3.z = vtri[n].pos3.z+vpos.z;
			}
		}
		return k;
	}
	public static Cuboid[] translate(Cuboid[] vcuboid, Position vpos) {
		Cuboid[] k = null;
		if ((vcuboid!=null)&&(vpos!=null)) {
			k = new Cuboid[vcuboid.length];
			for (int n=0;n<vcuboid.length;n++) {
				k[n] = vcuboid[n].copy();
				k[n].pos1.x = vcuboid[n].pos1.x+vpos.x;
				k[n].pos1.y = vcuboid[n].pos1.y+vpos.y;
				k[n].pos1.z = vcuboid[n].pos1.z+vpos.z;
				k[n].pos2.x = vcuboid[n].pos2.x+vpos.x;
				k[n].pos2.y = vcuboid[n].pos2.y+vpos.y;
				k[n].pos2.z = vcuboid[n].pos2.z+vpos.z;
				k[n].pos3.x = vcuboid[n].pos3.x+vpos.x;
				k[n].pos3.y = vcuboid[n].pos3.y+vpos.y;
				k[n].pos3.z = vcuboid[n].pos3.z+vpos.z;
				k[n].pos4.x = vcuboid[n].pos4.x+vpos.x;
				k[n].pos4.y = vcuboid[n].pos4.y+vpos.y;
				k[n].pos4.z = vcuboid[n].pos4.z+vpos.z;
				k[n].pos5.x = vcuboid[n].pos5.x+vpos.x;
				k[n].pos5.y = vcuboid[n].pos5.y+vpos.y;
				k[n].pos5.z = vcuboid[n].pos5.z+vpos.z;
				k[n].pos6.x = vcuboid[n].pos6.x+vpos.x;
				k[n].pos6.y = vcuboid[n].pos6.y+vpos.y;
				k[n].pos6.z = vcuboid[n].pos6.z+vpos.z;
				k[n].pos7.x = vcuboid[n].pos7.x+vpos.x;
				k[n].pos7.y = vcuboid[n].pos7.y+vpos.y;
				k[n].pos7.z = vcuboid[n].pos7.z+vpos.z;
				k[n].pos8.x = vcuboid[n].pos8.x+vpos.x;
				k[n].pos8.y = vcuboid[n].pos8.y+vpos.y;
				k[n].pos8.z = vcuboid[n].pos8.z+vpos.z;
			}
		}
		return k;
	}
	public static Quad[] translate(Quad[] vquad, Position vpos) {
		Quad[] k = null;
		if ((vquad!=null)&&(vpos!=null)) {
			k = new Quad[vquad.length];
			for (int n=0;n<vquad.length;n++) {
				k[n] = vquad[n].copy();
				k[n].pos1.x = vquad[n].pos1.x+vpos.x;
				k[n].pos1.y = vquad[n].pos1.y+vpos.y;
				k[n].pos1.z = vquad[n].pos1.z+vpos.z;
				k[n].pos2.x = vquad[n].pos2.x+vpos.x;
				k[n].pos2.y = vquad[n].pos2.y+vpos.y;
				k[n].pos2.z = vquad[n].pos2.z+vpos.z;
				k[n].pos3.x = vquad[n].pos3.x+vpos.x;
				k[n].pos3.y = vquad[n].pos3.y+vpos.y;
				k[n].pos3.z = vquad[n].pos3.z+vpos.z;
				k[n].pos4.x = vquad[n].pos4.x+vpos.x;
				k[n].pos4.y = vquad[n].pos4.y+vpos.y;
				k[n].pos4.z = vquad[n].pos4.z+vpos.z;
			}
		}
		return k;
	}
	public static Tetrahedron[] translate(Tetrahedron[] vtetra, Position vpos) {
		Tetrahedron[] k = null;
		if ((vtetra!=null)&&(vpos!=null)) {
			k = new Tetrahedron[vtetra.length];
			for (int n=0;n<vtetra.length;n++) {
				k[n] = vtetra[n].copy();
				k[n].pos1.x = vtetra[n].pos1.x+vpos.x;
				k[n].pos1.y = vtetra[n].pos1.y+vpos.y;
				k[n].pos1.z = vtetra[n].pos1.z+vpos.z;
				k[n].pos2.x = vtetra[n].pos2.x+vpos.x;
				k[n].pos2.y = vtetra[n].pos2.y+vpos.y;
				k[n].pos2.z = vtetra[n].pos2.z+vpos.z;
				k[n].pos3.x = vtetra[n].pos3.x+vpos.x;
				k[n].pos3.y = vtetra[n].pos3.y+vpos.y;
				k[n].pos3.z = vtetra[n].pos3.z+vpos.z;
				k[n].pos4.x = vtetra[n].pos4.x+vpos.x;
				k[n].pos4.y = vtetra[n].pos4.y+vpos.y;
				k[n].pos4.z = vtetra[n].pos4.z+vpos.z;
			}
		}
		return k;
	}
	public static AxisAlignedBoundingBox[] translate(AxisAlignedBoundingBox[] vaabb, Position vpos) {
		AxisAlignedBoundingBox[] k = null;
		if ((vaabb!=null)&&(vpos!=null)) {
			k = new AxisAlignedBoundingBox[vaabb.length];
			for (int n=0;n<vaabb.length;n++) {
				k[n] = vaabb[n].copy();
				k[n].pos1.x = vaabb[n].pos1.x+vpos.x;
				k[n].pos1.y = vaabb[n].pos1.y+vpos.y;
				k[n].pos1.z = vaabb[n].pos1.z+vpos.z;
				k[n].pos2.x = vaabb[n].pos2.x+vpos.x;
				k[n].pos2.y = vaabb[n].pos2.y+vpos.y;
				k[n].pos2.z = vaabb[n].pos2.z+vpos.z;
			}
		}
		return k;
	}
	public static Position[] translate(Position[] vpoint, Direction vdir, double mult) {
		Position[] k = null;
		if ((vpoint!=null)&&(vdir!=null)) {
			k = new Position[vpoint.length];
			for (int n=0;n<vpoint.length;n++) {
				k[n] = vpoint[n].copy();
				k[n].x = vpoint[n].x+mult*vdir.dx;
				k[n].y = vpoint[n].y+mult*vdir.dy;
				k[n].z = vpoint[n].z+mult*vdir.dz;
			}
		}
		return k;
	}
	public static Direction[] translate(Direction[] vvec, Direction vdir, double mult) {
		Direction[] k = null;
		if ((vvec!=null)&&(vdir!=null)) {
			k = new Direction[vvec.length];
			for (int n=0;n<vvec.length;n++) {
				k[n] = vvec[n].copy();
				k[n].dx = vvec[n].dx+mult*vdir.dx;
				k[n].dy = vvec[n].dy+mult*vdir.dy;
				k[n].dz = vvec[n].dz+mult*vdir.dz;
			}
		}
		return k;
	}
	public static Coordinate[] translate(Coordinate[] vcoord, Direction vdir, double mult) {
		Coordinate[] k = null;
		if ((vcoord!=null)&&(vdir!=null)) {
			k = new Coordinate[vcoord.length];
			for (int n=0;n<vcoord.length;n++) {
				k[n] = vcoord[n].copy();
				k[n].u = vcoord[n].u+mult*vdir.dx;
				k[n].v = vcoord[n].v+mult*vdir.dy;
			}
		}
		return k;
	}
	public static Line[] translate(Line[] vline, Direction vdir, double mult) {
		Line[] k = null;
		if ((vline!=null)&&(vdir!=null)) {
			k = new Line[vline.length];
			for (int n=0;n<vline.length;n++) {
				k[n] = vline[n].copy();
				k[n].pos1.x = vline[n].pos1.x+mult*vdir.dx;
				k[n].pos1.y = vline[n].pos1.y+mult*vdir.dy;
				k[n].pos1.z = vline[n].pos1.z+mult*vdir.dz;
				k[n].pos2.x = vline[n].pos2.x+mult*vdir.dx;
				k[n].pos2.y = vline[n].pos2.y+mult*vdir.dy;
				k[n].pos2.z = vline[n].pos2.z+mult*vdir.dz;
			}
		}
		return k;
	}
	public static Sphere[] translate(Sphere[] vsph, Direction vdir, double mult) {
		Sphere[] k = null;
		if ((vsph!=null)&&(vdir!=null)) {
			k = new Sphere[vsph.length];
			for (int n=0;n<vsph.length;n++) {
				k[n] = vsph[n].copy();
				k[n].x = vsph[n].x+mult*vdir.dx;
				k[n].y = vsph[n].y+mult*vdir.dy;
				k[n].z = vsph[n].z+mult*vdir.dz;
			}
		}
		return k;
	}
	public static Plane[] translate(Plane[] vplane, Direction vdir, double mult) {
		Direction[] planenormals = planeNormal(vplane);
		Position[] planepoints = pointOnPlane(vplane);
		Position[] translatedplanepoints = translate(planepoints, vdir, mult);
		return planeFromNormalAtPoint(translatedplanepoints, planenormals);
	}
	public static Triangle[] translate(Triangle[] vtri, Direction vdir, double mult) {
		Triangle[] k = null;
		if ((vtri!=null)&&(vdir!=null)) {
			k = new Triangle[vtri.length];
			for (int n=0;n<vtri.length;n++) {
				k[n] = vtri[n].copy();
				k[n].pos1.x = vtri[n].pos1.x+mult*vdir.dx;
				k[n].pos1.y = vtri[n].pos1.y+mult*vdir.dy;
				k[n].pos1.z = vtri[n].pos1.z+mult*vdir.dz;
				k[n].pos2.x = vtri[n].pos2.x+mult*vdir.dx;
				k[n].pos2.y = vtri[n].pos2.y+mult*vdir.dy;
				k[n].pos2.z = vtri[n].pos2.z+mult*vdir.dz;
				k[n].pos3.x = vtri[n].pos3.x+mult*vdir.dx;
				k[n].pos3.y = vtri[n].pos3.y+mult*vdir.dy;
				k[n].pos3.z = vtri[n].pos3.z+mult*vdir.dz;
			}
		}
		return k;
	}
	public static Cuboid[] translate(Cuboid[] vcuboid, Direction vdir, double mult) {
		Cuboid[] k = null;
		if ((vcuboid!=null)&&(vdir!=null)) {
			k = new Cuboid[vcuboid.length];
			for (int n=0;n<vcuboid.length;n++) {
				k[n] = vcuboid[n].copy();
				k[n].pos1.x = vcuboid[n].pos1.x+mult*vdir.dx;
				k[n].pos1.y = vcuboid[n].pos1.y+mult*vdir.dy;
				k[n].pos1.z = vcuboid[n].pos1.z+mult*vdir.dz;
				k[n].pos2.x = vcuboid[n].pos2.x+mult*vdir.dx;
				k[n].pos2.y = vcuboid[n].pos2.y+mult*vdir.dy;
				k[n].pos2.z = vcuboid[n].pos2.z+mult*vdir.dz;
				k[n].pos3.x = vcuboid[n].pos3.x+mult*vdir.dx;
				k[n].pos3.y = vcuboid[n].pos3.y+mult*vdir.dy;
				k[n].pos3.z = vcuboid[n].pos3.z+mult*vdir.dz;
				k[n].pos4.x = vcuboid[n].pos4.x+mult*vdir.dx;
				k[n].pos4.y = vcuboid[n].pos4.y+mult*vdir.dy;
				k[n].pos4.z = vcuboid[n].pos4.z+mult*vdir.dz;
				k[n].pos5.x = vcuboid[n].pos5.x+mult*vdir.dx;
				k[n].pos5.y = vcuboid[n].pos5.y+mult*vdir.dy;
				k[n].pos5.z = vcuboid[n].pos5.z+mult*vdir.dz;
				k[n].pos6.x = vcuboid[n].pos6.x+mult*vdir.dx;
				k[n].pos6.y = vcuboid[n].pos6.y+mult*vdir.dy;
				k[n].pos6.z = vcuboid[n].pos6.z+mult*vdir.dz;
				k[n].pos7.x = vcuboid[n].pos7.x+mult*vdir.dx;
				k[n].pos7.y = vcuboid[n].pos7.y+mult*vdir.dy;
				k[n].pos7.z = vcuboid[n].pos7.z+mult*vdir.dz;
				k[n].pos8.x = vcuboid[n].pos8.x+mult*vdir.dx;
				k[n].pos8.y = vcuboid[n].pos8.y+mult*vdir.dy;
				k[n].pos8.z = vcuboid[n].pos8.z+mult*vdir.dz;
			}
		}
		return k;
	}
	public static Quad[] translate(Quad[] vquad, Direction vdir, double mult) {
		Quad[] k = null;
		if ((vquad!=null)&&(vdir!=null)) {
			k = new Quad[vquad.length];
			for (int n=0;n<vquad.length;n++) {
				k[n] = vquad[n].copy();
				k[n].pos1.x = vquad[n].pos1.x+mult*vdir.dx;
				k[n].pos1.y = vquad[n].pos1.y+mult*vdir.dy;
				k[n].pos1.z = vquad[n].pos1.z+mult*vdir.dz;
				k[n].pos2.x = vquad[n].pos2.x+mult*vdir.dx;
				k[n].pos2.y = vquad[n].pos2.y+mult*vdir.dy;
				k[n].pos2.z = vquad[n].pos2.z+mult*vdir.dz;
				k[n].pos3.x = vquad[n].pos3.x+mult*vdir.dx;
				k[n].pos3.y = vquad[n].pos3.y+mult*vdir.dy;
				k[n].pos3.z = vquad[n].pos3.z+mult*vdir.dz;
				k[n].pos4.x = vquad[n].pos4.x+mult*vdir.dx;
				k[n].pos4.y = vquad[n].pos4.y+mult*vdir.dy;
				k[n].pos4.z = vquad[n].pos4.z+mult*vdir.dz;
			}
		}
		return k;
	}
	public static Tetrahedron[] translate(Tetrahedron[] vtetra, Direction vdir, double mult) {
		Tetrahedron[] k = null;
		if ((vtetra!=null)&&(vdir!=null)) {
			k = new Tetrahedron[vtetra.length];
			for (int n=0;n<vtetra.length;n++) {
				k[n] = vtetra[n].copy();
				k[n].pos1.x = vtetra[n].pos1.x+mult*vdir.dx;
				k[n].pos1.y = vtetra[n].pos1.y+mult*vdir.dy;
				k[n].pos1.z = vtetra[n].pos1.z+mult*vdir.dz;
				k[n].pos2.x = vtetra[n].pos2.x+mult*vdir.dx;
				k[n].pos2.y = vtetra[n].pos2.y+mult*vdir.dy;
				k[n].pos2.z = vtetra[n].pos2.z+mult*vdir.dz;
				k[n].pos3.x = vtetra[n].pos3.x+mult*vdir.dx;
				k[n].pos3.y = vtetra[n].pos3.y+mult*vdir.dy;
				k[n].pos3.z = vtetra[n].pos3.z+mult*vdir.dz;
				k[n].pos4.x = vtetra[n].pos4.x+mult*vdir.dx;
				k[n].pos4.y = vtetra[n].pos4.y+mult*vdir.dy;
				k[n].pos4.z = vtetra[n].pos4.z+mult*vdir.dz;
			}
		}
		return k;
	}
	public static AxisAlignedBoundingBox[] translate(AxisAlignedBoundingBox[] vaabb, Direction vdir, double mult) {
		AxisAlignedBoundingBox[] k = null;
		if ((vaabb!=null)&&(vdir!=null)) {
			k = new AxisAlignedBoundingBox[vaabb.length];
			for (int n=0;n<vaabb.length;n++) {
				k[n] = vaabb[n].copy();
				k[n].pos1.x = vaabb[n].pos1.x+mult*vdir.dx;
				k[n].pos1.y = vaabb[n].pos1.y+mult*vdir.dy;
				k[n].pos1.z = vaabb[n].pos1.z+mult*vdir.dz;
				k[n].pos2.x = vaabb[n].pos2.x+mult*vdir.dx;
				k[n].pos2.y = vaabb[n].pos2.y+mult*vdir.dy;
				k[n].pos2.z = vaabb[n].pos2.z+mult*vdir.dz;
			}
		}
		return k;
	}
	
	public static Position[] rotateAroundAxisPos(Position[] vpos, Position pos, Direction axis, double axisr) {
		Matrix rotmat = rotationMatrixAroundAxis(axis, axisr);
		Position zeropos = new Position(0.0f,0.0f,0.0f);
		Position[] posa = {pos};
		Direction[] posdir = vectorFromPoints(zeropos, posa);
		Position[] vposrot = translate(vpos, posdir[0], -1.0f);
		vposrot = matrixMultiply(vposrot, rotmat);
		vposrot = translate(vposrot, posdir[0], 1.0f);
		return vposrot;
	}
	
	public static Matrix rotationMatrix(double xaxisr, double yaxisr, double zaxisr) {
		Matrix xrot = new Matrix(1,0,0,0,cosd(xaxisr),-sind(xaxisr),0,sind(xaxisr),cosd(xaxisr));
		Matrix yrot = new Matrix(cosd(yaxisr),0,sind(yaxisr),0,1,0,-sind(yaxisr),0,cosd(yaxisr));
		Matrix zrot = new Matrix(cosd(zaxisr),-sind(zaxisr),0,sind(zaxisr),cosd(zaxisr),0,0,0,1);
		return matrixMultiply(zrot,matrixMultiply(yrot, xrot));
	}
	public static Matrix rotationMatrixAroundAxis(Direction axis, double axisr) {
		Direction[] axisa = {axis};
		Direction[] axisan = normalizeVector(axisa);
		Direction axisn = axisan[0];
		double cosdval = cosd(axisr);
		double sindval = sind(axisr);
		return new Matrix(cosdval+axisn.dx*axisn.dx*(1-cosdval),axisn.dx*axisn.dy*(1-cosdval)-axisn.dz*sindval,axisn.dx*axisn.dz*(1-cosdval)+axisn.dy*sindval,
				axisn.dy*axisn.dx*(1-cosdval)+axisn.dz*sindval,cosdval+axisn.dy*axisn.dy*(1-cosdval),axisn.dy*axisn.dz*(1-cosdval)-axisn.dx*sindval,
				axisn.dz*axisn.dx*(1-cosdval)-axisn.dy*sindval,axisn.dz*axisn.dy*(1-cosdval)+axisn.dx*sindval,cosdval+axisn.dz*axisn.dz*(1-cosdval));
	}
	public static Matrix rotationMatrixLookDir(Direction lookat, double rollaxis) {
		Direction[] defaultlookatdir = {new Direction(0,0,-1)};
		Direction[] defaultforwarddir = {new Direction(0,-1,0)};
		Direction[] lookata = {lookat};
		Direction[] lookatn = normalizeVector(lookata);
		double[] camrotxa = vectorAngle(defaultlookatdir, lookatn);
		double[] camrotya = vectorAngle(defaultforwarddir, lookatn);
		double camrotz = (lookatn[0].dx)*camrotya[0];
		double camroty = rollaxis;
		double camrotx = -camrotxa[0];
		if (!Double.isFinite(camroty)) {camroty=0.0f;}
		if (!Double.isFinite(camrotx)) {camrotx=90.0f;}
		Rotation camrot = new Rotation(camrotx,camroty,camrotz);
		return rotationMatrixLookHorizontalRoll(camrot);
	}
	public static Matrix rotationMatrixLookHorizontalRoll(Rotation rotation) {
		double camrotz = rotation.z;
		double camroty = rotation.y;
		double camrotx = rotation.x;
		Matrix camrotmatz = rotationMatrix(0.0f, 0.0f, camrotz);
		Matrix camrotmaty = rotationMatrix(0.0f, camroty, 0.0f);
		Matrix camrotmatx = rotationMatrix(camrotx, 0.0f, 0.0f);
		Matrix eyeonemat = rotationMatrix(0.0f, 0.0f, 0.0f);
		Matrix camrotmat = matrixMultiply(eyeonemat, camrotmatz);
		camrotmat = matrixMultiply(camrotmat, camrotmaty);
		camrotmat = matrixMultiply(camrotmat, camrotmatx);
		return camrotmat;
	}

	public static Sphere[] entitySphereList(Entity[] entitylist) {
		Sphere[] k = null;
		if ((entitylist!=null)&&(entitylist.length>0)) {
			k = new Sphere[entitylist.length]; 
			for (int i=0;i<entitylist.length;i++) {
				k[i] = entitylist[i].sphereboundaryvolume;
			}
		}
		return k;
	}
	
	public static Position[] sphereVertexList(Sphere[] spherelist) {
		Position[] k = null;
		if (spherelist!=null) {
			k = new Position[spherelist.length];
			for (int i=0;i<spherelist.length;i++) {
				k[i] = new Position(spherelist[i].x,spherelist[i].y,spherelist[i].z);
			}
		}
		return k;
	}
	
	public static Position[] generateVertexList(Line[] linelist) {
		TreeSet<Position> vertexlist = new TreeSet<Position>();
		for (int i=0;i<linelist.length;i++) {
			vertexlist.add(linelist[i].pos1);
			vertexlist.add(linelist[i].pos2);
		}
		return vertexlist.toArray(new Position[vertexlist.size()]);
	}
	public static Position[] generateVertexList(Triangle[] trianglelist) {
		TreeSet<Position> vertexlist = new TreeSet<Position>();
		for (int i=0;i<trianglelist.length;i++) {
			vertexlist.add(trianglelist[i].pos1);
			vertexlist.add(trianglelist[i].pos2);
			vertexlist.add(trianglelist[i].pos3);
		}
		return vertexlist.toArray(new Position[vertexlist.size()]);
	}
	public static Line[] generateLineList(Triangle[] trianglelist) {
		TreeSet<Line> linelist = new TreeSet<Line>();
		for (int i=0;i<trianglelist.length;i++) {
			linelist.add(new Line(trianglelist[i].pos1,trianglelist[i].pos2));
			linelist.add(new Line(trianglelist[i].pos1,trianglelist[i].pos3));
			linelist.add(new Line(trianglelist[i].pos2,trianglelist[i].pos3));
		}
		return linelist.toArray(new Line[linelist.size()]);
	}
	public static Line[] generateLineList(Entity[] entitylist) {
		TreeSet<Line> linelist = new TreeSet<Line>();
		for (int i=0;i<entitylist.length;i++) {
			if (entitylist[i].childlist!=null) {
				linelist.addAll(Arrays.asList(generateLineList(entitylist[i].childlist)));
			}
			if (entitylist[i].trianglelist!=null) {
				linelist.addAll(Arrays.asList(generateLineList(entitylist[i].trianglelist)));
			}
			if (entitylist[i].linelist!=null) {
				linelist.addAll(Arrays.asList(entitylist[i].linelist));
			}
		}
		return linelist.toArray(new Line[linelist.size()]);
	}
	public static Triangle[] generateTriangleList(Line[] linelist) {
		ArrayList<Triangle> trianglelist = new ArrayList<Triangle>();
		for (int k=0;k<linelist.length;k++) {
			Line kline = linelist[k];
			for (int j=k+1;j<linelist.length;j++) {
				Line jline = linelist[j];
				boolean kjlineconnected11 = kline.pos1.compareTo(jline.pos1)==0;
				boolean kjlineconnected12 = kline.pos1.compareTo(jline.pos2)==0;
				boolean kjlineconnected21 = kline.pos2.compareTo(jline.pos1)==0;
				boolean kjlineconnected22 = kline.pos2.compareTo(jline.pos2)==0;
				boolean kjlineconnected = kjlineconnected11||kjlineconnected12||kjlineconnected21||kjlineconnected22;
				if (kjlineconnected) {
					boolean klinefirst = (kjlineconnected11||kjlineconnected12)?true:false;
					boolean jlinefirst = (kjlineconnected11||kjlineconnected21)?true:false;
					for (int i=j+1;i<linelist.length;i++) {
						Line iline = linelist[i];
						Position klinefreevertex = klinefirst?kline.pos2:kline.pos1;
						Position jlinefreevertex = jlinefirst?jline.pos2:jline.pos1;
						Position connectedvertex = klinefirst?kline.pos1:kline.pos2;
						boolean ilinecoonnected = (klinefreevertex.compareTo(iline.pos1)==0)&&(jlinefreevertex.compareTo(iline.pos2)==0);
						boolean ilinecoonnectedr = (klinefreevertex.compareTo(iline.pos2)==0)&&(jlinefreevertex.compareTo(iline.pos1)==0);
						if (ilinecoonnected||ilinecoonnectedr) {
							trianglelist.add(new Triangle(connectedvertex,klinefreevertex,jlinefreevertex));
						}
					}
				}
			}
		}
		return trianglelist.toArray(new Triangle[trianglelist.size()]);
	}
	public static Line[] generateNonTriangleLineList(Line[] linelist) {
		Triangle[] trianglelist = generateTriangleList(linelist);
		TreeSet<Line> trianglelinelistarray = new TreeSet<Line>(Arrays.asList(generateLineList(trianglelist)));
		TreeSet<Line> linelistarray = new TreeSet<Line>(Arrays.asList(linelist));
		linelistarray.removeAll(trianglelinelistarray);
		return linelistarray.toArray(new Line[linelistarray.size()]);
	}

	public static Quad[] generateQuadList(Line[] linelist) {
		//TODO generate quad list
		return null;
	}
	
	public static Tetrahedron[] generateTetrahedronList(Line[] linelist) {
		Triangle[] uniquetrianglelist = generateTriangleList(linelist);
		TreeSet<Tetrahedron> tetrahedronlist = new TreeSet<Tetrahedron>();
		for (int k=0;k<uniquetrianglelist.length;k++) {
			Triangle trianglek = uniquetrianglelist[k]; 
			for (int j=k+1;j<uniquetrianglelist.length;j++) {
				Triangle trianglej = uniquetrianglelist[j];
				Position[] trianglevertexkj = {trianglek.pos1,trianglek.pos2,trianglek.pos3,trianglej.pos1,trianglej.pos2,trianglej.pos3};
				TreeSet<Position> uniquetrianglevertexkj = new TreeSet<Position>(Arrays.asList(trianglevertexkj));
				if (uniquetrianglevertexkj.size()==4) {
					for (int i=j+1;i<uniquetrianglelist.length;i++) {
						Triangle trianglei = uniquetrianglelist[i];
						Position[] trianglevertexkji = {trianglek.pos1,trianglek.pos2,trianglek.pos3,trianglej.pos1,trianglej.pos2,trianglej.pos3,trianglei.pos1,trianglei.pos2,trianglei.pos3};
						TreeSet<Position> uniquetrianglevertexkjiset = new TreeSet<Position>(Arrays.asList(trianglevertexkji));
						Position[] uniquetrianglevertexkji = uniquetrianglevertexkjiset.toArray(new Position[uniquetrianglevertexkjiset.size()]);
						if (uniquetrianglevertexkji.length==4) {
							Triangle triangle1 = new Triangle(uniquetrianglevertexkji[0],uniquetrianglevertexkji[1],uniquetrianglevertexkji[2]);
							Triangle triangle2 = new Triangle(uniquetrianglevertexkji[1],uniquetrianglevertexkji[2],uniquetrianglevertexkji[3]);
							Triangle[] triangles12 = {triangle1,triangle2};
							Plane[] triangle1plane = trianglePlane(triangles12);
							if ((!((triangle1plane[0].a==triangle1plane[1].a)&&(triangle1plane[0].b==triangle1plane[1].b)&&(triangle1plane[0].c==triangle1plane[1].c)))&&(!((triangle1plane[0].a==-triangle1plane[1].a)&&(triangle1plane[0].b==-triangle1plane[1].b)&&(triangle1plane[0].c==-triangle1plane[1].c)))) {
								tetrahedronlist.add(new Tetrahedron(uniquetrianglevertexkji[0],uniquetrianglevertexkji[1],uniquetrianglevertexkji[2],uniquetrianglevertexkji[3]));
							}
						}
					}
				}
			}
		}
		return tetrahedronlist.toArray(new Tetrahedron[tetrahedronlist.size()]);
	}

	public static Cuboid[] generateCuboidList(Line[] linelist) {
		//TODO generate cuboid volume list
		return null;
	}
	
	public static Triangle[] generateSurfaceList(Line[] linelist) {
		TreeSet<Triangle> surfacelistarray = new TreeSet<Triangle>(); 
		Tetrahedron[] newtetrahedronlist = generateTetrahedronList(linelist);
		for (int j=0;j<newtetrahedronlist.length;j++) {
			Triangle tetrahedronside1 = new Triangle(newtetrahedronlist[j].pos1, newtetrahedronlist[j].pos2, newtetrahedronlist[j].pos3); 
			Triangle tetrahedronside2 = new Triangle(newtetrahedronlist[j].pos1, newtetrahedronlist[j].pos2, newtetrahedronlist[j].pos4); 
			Triangle tetrahedronside3 = new Triangle(newtetrahedronlist[j].pos1, newtetrahedronlist[j].pos3, newtetrahedronlist[j].pos4); 
			Triangle tetrahedronside4 = new Triangle(newtetrahedronlist[j].pos2, newtetrahedronlist[j].pos3, newtetrahedronlist[j].pos4);
			Triangle[] tetrahedronsides = {tetrahedronside1,tetrahedronside2,tetrahedronside3,tetrahedronside4};
			Plane[] tetrahedronsideplanes = trianglePlane(tetrahedronsides);
			Position[] tetrahedronsidepoints = {newtetrahedronlist[j].pos4,newtetrahedronlist[j].pos3,newtetrahedronlist[j].pos2,newtetrahedronlist[j].pos1};
			for (int i=0;i<tetrahedronsides.length;i++) {
				Direction tetrahedronsideplanenormal = new Direction(tetrahedronsideplanes[i].a,tetrahedronsideplanes[i].b,tetrahedronsideplanes[i].c);
				Position[] tetrahedronsidepoint = {tetrahedronsidepoints[i]};
				Plane[] tetrahedronsideplane = {tetrahedronsideplanes[i]};
				double[][] tetrahedronsideplanepointdist = planePointDistance(tetrahedronsidepoint, tetrahedronsideplane);
				if (tetrahedronsideplanepointdist[0][0]>0) {
					tetrahedronsideplanenormal = new Direction(-tetrahedronsideplanes[i].a,-tetrahedronsideplanes[i].b,-tetrahedronsideplanes[i].c);
				}
				tetrahedronsides[i].norm = tetrahedronsideplanenormal;
				if (surfacelistarray.contains(tetrahedronsides[i])) {
					surfacelistarray.remove(tetrahedronsides[i]);
				} else {
					surfacelistarray.add(tetrahedronsides[i]);
				}
			}
		}
		return surfacelistarray.toArray(new Triangle[surfacelistarray.size()]);
	}
	
	public static Entity[] generateEntityList(Line[] linelist) {
		ArrayList<Entity> newentitylistarray = new ArrayList<Entity>();
		for (int j=0;j<linelist.length;j++) {
			Entity foundent1 = null;
			Entity foundent2 = null;
			for (Iterator<Entity> i=newentitylistarray.iterator();(i.hasNext())&&((foundent1==null)||(foundent2==null));) {
				Entity searchent = i.next();
				if (searchent.linelist!=null) {
					Position[] searchvert = generateVertexList(searchent.linelist);
					TreeSet<Position> searchvertarray = new TreeSet<Position>(Arrays.asList(searchvert));
					if (searchvertarray.contains(linelist[j].pos1)) {
						foundent1 = searchent; 
					} else if (searchvertarray.contains(linelist[j].pos2)) {
						foundent2 = searchent; 
					}
				}
			}
			if ((foundent1!=null)&&(foundent2!=null)&&(!foundent1.equals(foundent2))) {
				TreeSet<Line> newlinelistarray = new TreeSet<Line>(Arrays.asList(foundent1.linelist));
				newlinelistarray.addAll(Arrays.asList(foundent2.linelist));
				newlinelistarray.add(linelist[j]);
				foundent1.linelist = newlinelistarray.toArray(new Line[newlinelistarray.size()]);
				newentitylistarray.remove(foundent2);
			} else if ((foundent1!=null)||(foundent2!=null)) {
				Entity foundent = foundent1; 
				if (foundent2!=null) {foundent = foundent2;}
				TreeSet<Line> newlinelistarray = new TreeSet<Line>(Arrays.asList(foundent.linelist));
				newlinelistarray.add(linelist[j]);
				foundent.linelist = newlinelistarray.toArray(new Line[newlinelistarray.size()]);
			} else {
				Entity newentity = new Entity();
				Line[] newlinelist = {linelist[j]}; 
				newentity.linelist = newlinelist; 
				newentitylistarray.add(newentity);
			}
		}
		for (Iterator<Entity> i=newentitylistarray.iterator();i.hasNext();) {
			Entity processent = i.next();
			processent.trianglelist = generateTriangleList(processent.linelist);
			processent.vertexlist = generateVertexList(processent.linelist);
			processent.linelist = generateNonTriangleLineList(processent.linelist);
			processent.aabbboundaryvolume = axisAlignedBoundingBox(processent.vertexlist);
			processent.sphereboundaryvolume = pointCloudCircumSphere(processent.vertexlist);
		}
		Entity[] entitylist = newentitylistarray.toArray(new Entity[newentitylistarray.size()]); 
		return entitylist;
	}
	
	public static void generateEntityListOctree(Entity[] entitylist) {
		if (entitylist!=null) {
			for (int j=0;j<entitylist.length;j++) {
				Entity[] newchildren = {new Entity(),new Entity(),new Entity(),new Entity(),new Entity(),new Entity(),new Entity(),new Entity()}; 
				double xlimit = (entitylist[j].aabbboundaryvolume.pos1.x+entitylist[j].aabbboundaryvolume.pos2.x)/2.0f;
				double ylimit = (entitylist[j].aabbboundaryvolume.pos1.y+entitylist[j].aabbboundaryvolume.pos2.y)/2.0f;
				double zlimit = (entitylist[j].aabbboundaryvolume.pos1.z+entitylist[j].aabbboundaryvolume.pos2.z)/2.0f;
				newchildren[0].aabbboundaryvolume = new AxisAlignedBoundingBox(entitylist[j].aabbboundaryvolume.pos1, new Position(xlimit,ylimit,zlimit));
				newchildren[1].aabbboundaryvolume = new AxisAlignedBoundingBox(new Position(xlimit,entitylist[j].aabbboundaryvolume.pos1.y,entitylist[j].aabbboundaryvolume.pos1.z),new Position(entitylist[j].aabbboundaryvolume.pos2.x,ylimit,zlimit));
				newchildren[2].aabbboundaryvolume = new AxisAlignedBoundingBox(new Position(entitylist[j].aabbboundaryvolume.pos1.x,ylimit,entitylist[j].aabbboundaryvolume.pos1.z),new Position(xlimit,entitylist[j].aabbboundaryvolume.pos2.y,zlimit));
				newchildren[3].aabbboundaryvolume = new AxisAlignedBoundingBox(new Position(xlimit,ylimit,entitylist[j].aabbboundaryvolume.pos1.z),new Position(entitylist[j].aabbboundaryvolume.pos2.x,entitylist[j].aabbboundaryvolume.pos2.y,zlimit));
				newchildren[4].aabbboundaryvolume = new AxisAlignedBoundingBox(new Position(entitylist[j].aabbboundaryvolume.pos1.x,entitylist[j].aabbboundaryvolume.pos1.y,zlimit),new Position(xlimit,ylimit,entitylist[j].aabbboundaryvolume.pos2.z));
				newchildren[5].aabbboundaryvolume = new AxisAlignedBoundingBox(new Position(xlimit,entitylist[j].aabbboundaryvolume.pos1.y,zlimit),new Position(entitylist[j].aabbboundaryvolume.pos2.x,ylimit,entitylist[j].aabbboundaryvolume.pos2.z));
				newchildren[6].aabbboundaryvolume = new AxisAlignedBoundingBox(new Position(entitylist[j].aabbboundaryvolume.pos1.x,ylimit,zlimit),new Position(xlimit,entitylist[j].aabbboundaryvolume.pos2.y,entitylist[j].aabbboundaryvolume.pos2.z));
				newchildren[7].aabbboundaryvolume = new AxisAlignedBoundingBox(new Position(xlimit,ylimit,zlimit),entitylist[j].aabbboundaryvolume.pos2);
				ArrayList<Entity> newchildlistarray = new ArrayList<Entity>();
				for (int n=0;n<newchildren.length;n++) {
					if (entitylist[j].trianglelist!=null) {
						boolean[] taabbint = triangleAxisAlignedBoundingBoxIntersection(newchildren[n].aabbboundaryvolume, entitylist[j].trianglelist);
						ArrayList<Triangle> triintarray = new ArrayList<Triangle>();
						for (int i=0;i<taabbint.length;i++) {if (taabbint[i]) {triintarray.add(entitylist[j].trianglelist[i]);}}
						newchildren[n].trianglelist = triintarray.toArray(new Triangle[triintarray.size()]);
					}
					if (entitylist[j].linelist!=null) {
						boolean[] laabbint = lineAxisAlignedBoundingBoxIntersection(newchildren[n].aabbboundaryvolume, entitylist[j].linelist);
						ArrayList<Line> lineintarray = new ArrayList<Line>();
						for (int i=0;i<laabbint.length;i++) {if (laabbint[i]) {lineintarray.add(entitylist[j].linelist[i]);}}
						newchildren[n].linelist = lineintarray.toArray(new Line[lineintarray.size()]);
					}
					if (entitylist[j].vertexlist!=null) {
						boolean[] vaabbint = vertexAxisAlignedBoundingBoxIntersection(newchildren[n].aabbboundaryvolume, entitylist[j].vertexlist);
						ArrayList<Position> vertintarray = new ArrayList<Position>();
						for (int i=0;i<vaabbint.length;i++) {if (vaabbint[i]) {vertintarray.add(entitylist[j].vertexlist[i]);}}
						newchildren[n].vertexlist = vertintarray.toArray(new Position[vertintarray.size()]);
					}
					if ((newchildren[n].vertexlist!=null)&&(newchildren[n].vertexlist.length>0)) {
						double aabbxcenter = (newchildren[n].aabbboundaryvolume.pos1.x+newchildren[n].aabbboundaryvolume.pos2.x)/2.0f;
						double aabbycenter = (newchildren[n].aabbboundaryvolume.pos1.y+newchildren[n].aabbboundaryvolume.pos2.y)/2.0f;
						double aabbzcenter = (newchildren[n].aabbboundaryvolume.pos1.z+newchildren[n].aabbboundaryvolume.pos2.z)/2.0f;
						Position[] aabbcenter = {new Position(aabbxcenter,aabbycenter,aabbzcenter)};
						Position[] aabbedge = {new Position(newchildren[n].aabbboundaryvolume.pos1.x,newchildren[n].aabbboundaryvolume.pos1.y,newchildren[n].aabbboundaryvolume.pos1.z)};
						Direction[] aabbdir = vectorFromPoints(aabbcenter, aabbedge);
						double[] aabbradius = vectorLength(aabbdir);
						newchildren[n].sphereboundaryvolume = new Sphere(aabbxcenter,aabbycenter,aabbzcenter,aabbradius[0]);
						newchildlistarray.add(newchildren[n]);
					}
				}
				entitylist[j].childlist = newchildlistarray.toArray(new Entity[newchildlistarray.size()]);
			}
		}
	}
	
	public static Triangle[] subDivideTriangle(Triangle[] vtri) {
		Triangle[] k = null;
		if (vtri!=null) {
			k = new Triangle[2*vtri.length];
			for (int i=0;i<vtri.length;i++) {
				Position[] trianglepos1 = {vtri[i].pos1,vtri[i].pos1,vtri[i].pos2};
				Position[] trianglepos2 = {vtri[i].pos2,vtri[i].pos3,vtri[i].pos3};
				Direction[] trianglelines = vectorFromPoints(trianglepos1, trianglepos2);
				double[] trianglelineslen = vectorLength(trianglelines);
				int[] lineindex = UtilLib.indexSort(trianglelineslen);
				Position newtrianglepoint = new Position(trianglepos1[lineindex[2]].x+0.5f*trianglelines[lineindex[2]].dx, trianglepos1[lineindex[2]].y+0.5f*trianglelines[lineindex[2]].dy, trianglepos1[lineindex[2]].z+0.5f*trianglelines[lineindex[2]].dz);
				if ((trianglepos1[lineindex[2]].tex!=null)&&(trianglepos2[lineindex[2]].tex!=null)) {
					newtrianglepoint.tex = new Coordinate(trianglepos1[lineindex[2]].tex.u+0.5f*(trianglepos2[lineindex[2]].tex.u-trianglepos1[lineindex[2]].tex.u),trianglepos1[lineindex[2]].tex.v+0.5f*(trianglepos2[lineindex[2]].tex.v-trianglepos1[lineindex[2]].tex.v));
				}
				Triangle newtriangle1 = null;
				Triangle newtriangle2 = null;
				if (lineindex[2]==0) {
					newtriangle1 = new Triangle(newtrianglepoint,vtri[i].pos1,vtri[i].pos3);
					newtriangle2 = new Triangle(newtrianglepoint,vtri[i].pos2,vtri[i].pos3);
				} else if (lineindex[2]==1) {
					newtriangle1 = new Triangle(newtrianglepoint,vtri[i].pos1,vtri[i].pos2);
					newtriangle2 = new Triangle(newtrianglepoint,vtri[i].pos3,vtri[i].pos2);
				} else {
					newtriangle1 = new Triangle(newtrianglepoint,vtri[i].pos2,vtri[i].pos1);
					newtriangle2 = new Triangle(newtrianglepoint,vtri[i].pos3,vtri[i].pos1);
				}
				newtriangle1.mat = vtri[i].mat;
				newtriangle2.mat = vtri[i].mat;
				newtriangle1.norm = vtri[i].norm;
				newtriangle2.norm = vtri[i].norm;
				k[i*2] = newtriangle1;
				k[i*2+1] = newtriangle2;
			}
		}
		return k;
	}
	
	public static double[] spheremapAngles(int vres, double vfov) {
		double[] k = new double[vres];
		double halfvfov = vfov/2.0f;
		double vstep = vfov/((double)(vres-1));
		for (int i=0;i<vres;i++){k[i]=-halfvfov+vstep*i;}
		return k;
	}
	public static Direction[] spheremapVectors(int hres, Matrix vmat) {
		double[] hangles  = spheremapAngles(hres, 360.0f);
		Direction[] vvecs = new Direction[hres];
		for (int i=0;i<hres;i++) {
			vvecs[i] = new Direction(sind(hangles[i]), 0.0f, -cosd(hangles[i]));
		}
		Direction[] vvecsrot = matrixMultiply(vvecs, vmat);
		return vvecsrot;
	}
	public static PlaneRay[] spheremapPlaneRays(Position vpos, int hres, int vres, Matrix vmat) {
		PlaneRay[] k = new PlaneRay[hres];
		double[] hangles  = spheremapAngles(hres, 360.0f);
		Direction[] smvecs = new Direction[hres];
		for (int i=0;i<hres;i++) {
			smvecs[i] = new Direction(cosd(hangles[i]), 0.0f, sind(hangles[i]));
		}
		Direction[] smvecsrot = matrixMultiply(smvecs, vmat);
		Plane[] smplanes = planeFromNormalAtPoint(vpos, smvecsrot);
		Direction[] smdirs = spheremapVectors(hres, vmat);
		for (int i=0;i<hres;i++) {
			k[i] = new PlaneRay(vpos,smdirs[i],smplanes[i],180.0f);
		}
		return k;
	}
	public static Direction[][] spheremapRays(int hres, int vres, Matrix vmat) {
		Direction[][] k = new Direction[vres][hres];
		double[] hangles  = spheremapAngles(hres, 360.0f);
		double[] vangles  = spheremapAngles(vres, 180.0f);
		Direction[] vvecs = new Direction[vres];
		for (int i=0;i<vres;i++) {
			vvecs[i] = new Direction(0.0f, -sind(vangles[i]), -cosd(vangles[i]));
		}
		for (int i=0;i<hres;i++) {
			Matrix hmat = rotationMatrix(0, -hangles[i], 0);
			Matrix hdmat = matrixMultiply(vmat, hmat);
			Direction[] hvvecs = matrixMultiply(vvecs, hdmat);
			for (int j=0;j<vres;j++) {
				k[j][i] = hvvecs[j];
			}
		}
		
		return k;
	}
	
	public static double[] projectedStep(int res, double fov) {
		double[] k = new double[res];
		double halfvfov = fov/2.0f;
		double stepmax = Math.abs(tand(halfvfov));
		double stepmin = -stepmax;
		double step = 2.0f/((double)(res-1))*stepmax;
		for (int i=0;i<res;i++){k[i]=stepmin+step*i;}
		return k;
	}
	public static double[] projectedAngles(int res, double fov) {
		double[] k = new double[res];
		double[] hd = projectedStep(res, fov);
		for (int i=0;i<res;i++){k[i]=atand(hd[i]);}
		return k;
	}
	public static Direction[] projectedCameraDirections(Matrix vmat) {
		Direction[] rightdirupvectors = new Direction[3];
		Direction dirvector = new Direction(0,0,-1);
		Direction rightvector = new Direction(1,0,0);
		Direction upvector = new Direction(0,-1,0);
		rightdirupvectors[0] = dirvector;
		rightdirupvectors[1] = rightvector;
		rightdirupvectors[2] = upvector;
	    return matrixMultiply(rightdirupvectors, vmat);
	}
	public static Direction[] projectedPlaneRayDirections(Matrix vmat) {
		Direction[] rightdirupvectors = new Direction[3];
		Direction dirvector = new Direction(0,0,-1);
		Direction rightvector = new Direction(0,1,0);
		Direction upvector = new Direction(1,0,0);
		rightdirupvectors[0] = dirvector;
		rightdirupvectors[1] = rightvector;
		rightdirupvectors[2] = upvector;
	    return matrixMultiply(rightdirupvectors, vmat);
	}
	public static Direction[] projectedPlaneRayVectors(int hres, double hfov, Matrix vmat, boolean norm) {
	    double[] steps = projectedStep(hres,hfov);
		Direction[] fwdvectors = new Direction[hres];
	    for (int i=0;i<hres;i++) {
	    	fwdvectors[i] = new Direction(steps[i], 0, -1);
	    }
		if (norm) {
			fwdvectors = normalizeVector(fwdvectors);
		}
	    return matrixMultiply(fwdvectors, vmat);
	}
	public static PlaneRay[] projectedPlaneRays(Position vpos, int hres, int vres, double hfov, double vfov, Matrix vmat) {
		PlaneRay[] k = new PlaneRay[hres];
		Direction[] dirrightupvectors = projectedPlaneRayDirections(vmat);
		Direction rightvector = dirrightupvectors[1];
	    double[] hsteps = projectedStep(hres,hfov);
	    double[] vsteps = projectedStep(vres,vfov);
	    double vstepmin = vsteps[0];
		Direction[] fwdvectors = new Direction[hres];
	    for (int i=0;i<hres;i++) {
	    	fwdvectors[i] = new Direction(hsteps[i], 0, -1);
	    }
	    Direction[] fwdvectorsn = normalizeVector(fwdvectors);
	    Direction[] fwdvectorsnrot =  matrixMultiply(fwdvectorsn, vmat);
		Direction[] planenormalvectors = vectorCross(fwdvectorsnrot, rightvector);
		planenormalvectors = normalizeVector(planenormalvectors);
	    Plane[] prjplanes = planeFromNormalAtPoint(vpos, planenormalvectors);
	    for (int i=0;i<hres;i++) {
	    	Direction[] fwdupvector = {new Direction(fwdvectors[i].dx,vstepmin,fwdvectors[i].dz)};
	    	double[] fwdupangle = vectorAngle(fwdvectors[i], fwdupvector);
	    	k[i] = new PlaneRay(vpos,fwdvectorsnrot[i],prjplanes[i],2.0f*fwdupangle[0]);
	    }
		return k;
	}
	public static Direction[][] projectedRays(int vhres, int vvres, double vhfov, double vvfov, Matrix vmat, boolean norm) {
		Direction[][] k = new Direction[vvres][vhres];
		double[] hstep = projectedStep(vhres, vhfov);
		double[] vstep = projectedStep(vvres, vvfov);
		for (int j=0;j<vvres;j++) {
			for (int i=0;i<vhres;i++) {
				k[j][i] = new Direction(hstep[i],-vstep[j],-1);
			}
			if (norm) {
				k[j] = normalizeVector(k[j]);
			}
			k[j] = matrixMultiply(k[j], vmat);
		}
		return k;
	}
	public static Ray[][] projectedRays(Position campos, int vhres, int vvres, double vhfov, double vvfov, Matrix vmat, boolean norm) {
		Ray[][] k = new Ray[vvres][vhres];
		Direction[][] rays = projectedRays(vhres, vvres, vhfov, vvfov, vmat, norm);
		for (int j=0;j<vvres;j++) {
			for (int i=0;i<vhres;i++) {
				k[j][i] = new Ray(campos, rays[j][i]);
			}
		}
		return k;
	}
	public static Coordinate[] projectedPoint(Position vpos, Position[] vpoint, int hres, double hfov, int vres, double vfov, Matrix vmat, Plane nclipplane) {
		Coordinate[] k = null;
		if ((vpos!=null)&&(vpoint!=null)&&(vmat!=null)) {
			k = new Coordinate[vpoint.length];
			double halfhfovmult = (1.0f/tand(hfov/2.0f));
			double halfvfovmult = (1.0f/tand(vfov/2.0f));
			double origindeltax = ((double)(hres-1))/2.0f;
			double origindeltay = ((double)(vres-1))/2.0f;
			double halfhres = ((double)(hres-1))/2.0f;
			double halfvres = ((double)(vres-1))/2.0f;
			Direction[] dirrightupvectors = projectedCameraDirections(vmat);
			Plane[] dirrightupplanes = planeFromNormalAtPoint(vpos, dirrightupvectors);
			double[][] fwdintpointsdist = planePointDistance(vpoint, dirrightupplanes);
			for (int i=0;i<vpoint.length;i++) {
				if (fwdintpointsdist[i][0]>=1.0f) {
					double hind = halfhfovmult*halfhres*(fwdintpointsdist[i][1]/fwdintpointsdist[i][0])+origindeltax;
					double vind = halfvfovmult*halfvres*(fwdintpointsdist[i][2]/fwdintpointsdist[i][0])+origindeltay;
					k[i] = new Coordinate(hind,vind);
				}
			}
		}
		return k;
	}
	public static Coordinate[][] projectedLine(Position vpos, Line[] vline, int hres, double hfov, int vres, double vfov, Matrix vmat, Plane nclipplane) {
		Coordinate[][] k = null;
		if ((vpos!=null)&&(vline!=null)&&(vmat!=null)) {
			k = new Coordinate[vline.length][3];
			Direction[] dirs = projectedCameraDirections(vmat);
			Direction[] camdir = {dirs[0]};
			Position[] camposa = {vpos};
			Position[] rendercutpos = translate(camposa, dirs[0], 1.1d);
			Plane[] rendercutplane = planeFromNormalAtPoint(rendercutpos, camdir);
			Position[][] vlinepos = new Position[3][vline.length];
			Coordinate[][] vlinepospixels = new Coordinate[2][vline.length];
			for (int i=0;i<vline.length;i++) {
				vlinepos[0][i] = vline[i].pos1;
				vlinepos[1][i] = vline[i].pos2;
			}
			for (int j=0;j<2;j++) {
				Coordinate[] vlinepospixel = projectedPoint(vpos, vlinepos[j], hres, hfov, vres, vfov, vmat, nclipplane);
				for (int i=0;i<vlinepos[j].length;i++) {
					k[i][j] = vlinepospixel[i];
				}
			}
			for (int i=0;i<vline.length;i++) {
				vlinepos[2][i] = vline[i].pos1;
				boolean vlinepos1visible = vlinepospixels[0][i]!=null;
				boolean vlinepos2visible = vlinepospixels[1][i]!=null;
				if (vlinepos1visible||vlinepos2visible) {
					if (!(vlinepos1visible&&vlinepos2visible)) {
						Position[] vlinepos1 = {vline[i].pos1};
						Position[] vlinepos2 = {vline[i].pos2};
						Direction[] vlinedir12 = vectorFromPoints(vlinepos1, vlinepos2);
						double[][] vlinedir12dist = rayPlaneDistance(vlinepos1[0], vlinedir12, rendercutplane);
						Position[] vlinepos3 = translate(vlinepos1, vlinedir12[0], vlinedir12dist[0][0]);
						vlinepos[2][i] = vlinepos3[0];
					}
				}
			}
			for (int j=2;j<vlinepos.length;j++) {
				Coordinate[] vlinepospixel = projectedPoint(vpos, vlinepos[j], hres, hfov, vres, vfov, vmat, nclipplane);
				for (int i=0;i<vlinepos[j].length;i++) {
					k[i][j] = vlinepospixel[i];
				}
			}
		}
		return k;
	}
	public static Coordinate[][] projectedTriangle(Position vpos, Triangle[] vtri, int hres, double hfov, int vres, double vfov, Matrix vmat, Plane nclipplane) {
		Coordinate[][] k = null;
		if ((vpos!=null)&&(vtri!=null)&&(vmat!=null)) {
			k = new Coordinate[vtri.length][5];
			Direction[] dirs = projectedCameraDirections(vmat);
			Direction[] camdir = {dirs[0]};
			Position[] camposa = {vpos};
			Position[] rendercutpos = translate(camposa, dirs[0], 1.1d);
			Plane[] rendercutplane = planeFromNormalAtPoint(rendercutpos, camdir);
			Plane[] neartriclipplane = {nclipplane};
			Position[][] vtripos = new Position[5][vtri.length];
			Coordinate[][] vtripospixels = new Coordinate[3][vtri.length];
			for (int i=0;i<vtri.length;i++) {
				vtripos[0][i] = vtri[i].pos1;
				vtripos[1][i] = vtri[i].pos2;
				vtripos[2][i] = vtri[i].pos3;
			}
			for (int j=0;j<3;j++) {
				vtripospixels[j] = projectedPoint(vpos, vtripos[j], hres, hfov, vres, vfov, vmat, nclipplane);
				double[][] vtripospixeldist = null;
				if (nclipplane!=null) {
					vtripospixeldist = planePointDistance(vtripos[j], neartriclipplane);
				}
				for (int i=0;i<vtripos[j].length;i++) {
					if ((nclipplane==null)||(vtripospixeldist[i][0]>=0.1f)) {
						k[i][j] = vtripospixels[j][i];
					}
				}
			}
			for (int i=0;i<vtri.length;i++) {
				vtripos[3][i] = vtri[i].pos1;
				vtripos[4][i] = vtri[i].pos1;
				boolean vtripos1visible = vtripospixels[0][i]!=null;
				boolean vtripos2visible = vtripospixels[1][i]!=null;
				boolean vtripos3visible = vtripospixels[2][i]!=null;
				if (vtripos1visible||vtripos2visible||vtripos3visible) {
					if (!(vtripos1visible&&vtripos2visible&&vtripos3visible)) {
						Position[] vtripos1 = {vtri[i].pos1};
						Position[] vtripos2 = {vtri[i].pos2};
						Position[] vtripos3 = {vtri[i].pos3};
						Direction[] vtridir12 = vectorFromPoints(vtripos1, vtripos2);
						Direction[] vtridir13 = vectorFromPoints(vtripos1, vtripos3);
						Direction[] vtridir23 = vectorFromPoints(vtripos2, vtripos3);
						double[][] vtridir12dist = rayPlaneDistance(vtripos1[0], vtridir12, rendercutplane);
						double[][] vtridir13dist = rayPlaneDistance(vtripos1[0], vtridir13, rendercutplane);
						double[][] vtridir23dist = rayPlaneDistance(vtripos2[0], vtridir23, rendercutplane);
						Position[] vtripos12 = translate(vtripos1, vtridir12[0], vtridir12dist[0][0]);
						Position[] vtripos13 = translate(vtripos1, vtridir13[0], vtridir13dist[0][0]);
						Position[] vtripos23 = translate(vtripos2, vtridir23[0], vtridir23dist[0][0]);
						if (vtripos1visible&&vtripos2visible) {
							vtripos[3][i] = vtripos13[0];
							vtripos[4][i] = vtripos23[0];
						} else if (vtripos1visible&&vtripos3visible) {
							vtripos[3][i] = vtripos12[0];
							vtripos[4][i] = vtripos23[0];
						} else if (vtripos2visible&&vtripos3visible) {
							vtripos[3][i] = vtripos12[0];
							vtripos[4][i] = vtripos13[0];
						} else if (vtripos1visible) {
							vtripos[3][i] = vtripos12[0];
							vtripos[4][i] = vtripos13[0];
						} else if (vtripos2visible) {
							vtripos[3][i] = vtripos12[0];
							vtripos[4][i] = vtripos23[0];
						} else if (vtripos3visible) {
							vtripos[3][i] = vtripos13[0];
							vtripos[4][i] = vtripos23[0];
						}
					}
				}
			}
			for (int j=3;j<vtripos.length;j++) {
				Coordinate[] vtripospixel = projectedPoint(vpos, vtripos[j], hres, hfov, vres, vfov, vmat, nclipplane);
				for (int i=0;i<vtripos[j].length;i++) {
					k[i][j] = vtripospixel[i];
				}
			}
		}
		return k;
	}
	public static Coordinate[][] projectedQuad(Position vpos, Quad[] vquad, int hres, double hfov, int vres, double vfov, Matrix vmat, Plane nclipplane) {
		Coordinate[][] k = null;
		if ((vpos!=null)&&(vquad!=null)&&(vmat!=null)) {
			k = new Coordinate[vquad.length][8];
			Direction[] dirs = projectedCameraDirections(vmat);
			Direction[] camdir = {dirs[0]};
			Position[] camposa = {vpos};
			Position[] rendercutpos = translate(camposa, dirs[0], 1.1d);
			Plane[] rendercutplane = planeFromNormalAtPoint(rendercutpos, camdir);
			Position[][] vquadpos = new Position[8][vquad.length];
			Coordinate[][] vquadpospixels = new Coordinate[4][vquad.length];
			for (int i=0;i<vquad.length;i++) {
				vquadpos[0][i] = vquad[i].pos1;
				vquadpos[1][i] = vquad[i].pos2;
				vquadpos[2][i] = vquad[i].pos3;
				vquadpos[3][i] = vquad[i].pos4;
			}
			for (int j=0;j<4;j++) {
				vquadpospixels[j] = projectedPoint(vpos, vquadpos[j], hres, hfov, vres, vfov, vmat, nclipplane);
				for (int i=0;i<vquadpos[j].length;i++) {
					k[i][j] = vquadpospixels[j][i];
				}
			}
			for (int i=0;i<vquad.length;i++) {
				vquadpos[4][i] = vquad[i].pos1;
				vquadpos[5][i] = vquad[i].pos1;
				vquadpos[6][i] = vquad[i].pos1;
				vquadpos[7][i] = vquad[i].pos1;
				boolean vquadpos1visible = vquadpospixels[0][i]!=null;
				boolean vquadpos2visible = vquadpospixels[1][i]!=null;
				boolean vquadpos3visible = vquadpospixels[2][i]!=null;
				boolean vquadpos4visible = vquadpospixels[3][i]!=null;
				if (vquadpos1visible||vquadpos2visible||vquadpos3visible||vquadpos4visible) {
					if (!(vquadpos1visible&&vquadpos2visible&&vquadpos3visible)) {
						Position[] vquadpos1 = {vquad[i].pos1};
						Position[] vquadpos2 = {vquad[i].pos2};
						Position[] vquadpos3 = {vquad[i].pos3};
						Position[] vquadpos4 = {vquad[i].pos4};
						Direction[] vquaddir12 = vectorFromPoints(vquadpos1, vquadpos2);
						Direction[] vquaddir23 = vectorFromPoints(vquadpos2, vquadpos3);
						Direction[] vquaddir34 = vectorFromPoints(vquadpos3, vquadpos4);
						Direction[] vquaddir14 = vectorFromPoints(vquadpos1, vquadpos4);
						double[][] vquaddir12dist = rayPlaneDistance(vquadpos1[0], vquaddir12, rendercutplane);
						double[][] vquaddir23dist = rayPlaneDistance(vquadpos2[0], vquaddir23, rendercutplane);
						double[][] vquaddir34dist = rayPlaneDistance(vquadpos3[0], vquaddir34, rendercutplane);
						double[][] vquaddir14dist = rayPlaneDistance(vquadpos1[0], vquaddir14, rendercutplane);
						Position[] vquadpos12 = translate(vquadpos1, vquaddir12[0], vquaddir12dist[0][0]);
						Position[] vquadpos23 = translate(vquadpos2, vquaddir23[0], vquaddir23dist[0][0]);
						Position[] vquadpos34 = translate(vquadpos3, vquaddir34[0], vquaddir34dist[0][0]);
						Position[] vquadpos14 = translate(vquadpos1, vquaddir14[0], vquaddir14dist[0][0]);
						if (vquadpos1visible&&vquadpos2visible&&vquadpos3visible) {
							vquadpos[4][i] = vquadpos34[0];
							vquadpos[5][i] = vquadpos14[0];
						} else if (vquadpos1visible&&vquadpos2visible&&vquadpos4visible) {
							vquadpos[4][i] = vquadpos23[0];
							vquadpos[5][i] = vquadpos34[0];
						} else if (vquadpos1visible&&vquadpos3visible&&vquadpos4visible) {
							vquadpos[4][i] = vquadpos12[0];
							vquadpos[5][i] = vquadpos23[0];
						} else if (vquadpos2visible&&vquadpos3visible&&vquadpos4visible) {
							vquadpos[4][i] = vquadpos12[0];
							vquadpos[5][i] = vquadpos14[0];
						} else if (vquadpos1visible&&vquadpos2visible) {
							vquadpos[4][i] = vquadpos23[0];
							vquadpos[5][i] = vquadpos14[0];
						} else if (vquadpos1visible&&vquadpos3visible) {
							vquadpos[4][i] = vquadpos12[0];
							vquadpos[5][i] = vquadpos23[0];
							vquadpos[6][i] = vquadpos34[0];
							vquadpos[7][i] = vquadpos14[0];
						} else if (vquadpos1visible&&vquadpos4visible) {
							vquadpos[4][i] = vquadpos12[0];
							vquadpos[5][i] = vquadpos34[0];
						} else if (vquadpos2visible&&vquadpos3visible) {
							vquadpos[4][i] = vquadpos12[0];
							vquadpos[5][i] = vquadpos34[0];
						} else if (vquadpos2visible&&vquadpos4visible) {
							vquadpos[4][i] = vquadpos12[0];
							vquadpos[5][i] = vquadpos23[0];
							vquadpos[6][i] = vquadpos34[0];
							vquadpos[7][i] = vquadpos14[0];
						} else if (vquadpos3visible&&vquadpos4visible) {
							vquadpos[4][i] = vquadpos23[0];
							vquadpos[5][i] = vquadpos14[0];
						} else if (vquadpos1visible) {
							vquadpos[4][i] = vquadpos12[0];
							vquadpos[5][i] = vquadpos14[0];
						} else if (vquadpos2visible) {
							vquadpos[4][i] = vquadpos12[0];
							vquadpos[5][i] = vquadpos23[0];
						} else if (vquadpos3visible) {
							vquadpos[4][i] = vquadpos23[0];
							vquadpos[5][i] = vquadpos34[0];
						} else if (vquadpos4visible) {
							vquadpos[4][i] = vquadpos34[0];
							vquadpos[5][i] = vquadpos14[0];
						}
					}
				}
			}
			for (int j=4;j<vquadpos.length;j++) {
				Coordinate[] vquadpospixel = projectedPoint(vpos, vquadpos[j], hres, hfov, vres, vfov, vmat, nclipplane);
				for (int i=0;i<vquadpos[j].length;i++) {
					k[i][j] = vquadpospixel[i];
				}
			}
		}
		return k;
	}

	public static Rectangle[] projectedTriangleIntersection(Position vpos, Triangle[] vtri, int hres, int vres, double hfov, double vfov, Matrix vmat, Plane nclipplane) {
		Rectangle[] k = null;
		if ((vpos!=null)&&(vtri!=null)&&(vmat!=null)) {
			k = new Rectangle[vtri.length];
			Coordinate[][] projectedtriangles = projectedTriangle(vpos, vtri, hres, hfov, vres, vfov, vmat, nclipplane);
			for (int j=0;j<projectedtriangles.length;j++) {
				Coordinate coord1 = projectedtriangles[j][0];
				Coordinate coord2 = projectedtriangles[j][1];
				Coordinate coord3 = projectedtriangles[j][2];
				Coordinate coord4 = projectedtriangles[j][3];
				Coordinate coord5 = projectedtriangles[j][4];
				if ((coord1!=null)||(coord2!=null)||(coord3!=null)) {
					Polygon trianglepolygon = new Polygon();
					if ((coord1!=null)&&(coord2!=null)&&(coord3!=null)) {
						trianglepolygon.addPoint((int)Math.round(coord1.u), (int)Math.round(coord1.v));
						trianglepolygon.addPoint((int)Math.round(coord2.u), (int)Math.round(coord2.v));
						trianglepolygon.addPoint((int)Math.round(coord3.u), (int)Math.round(coord3.v));
					} else {
						if (coord1!=null) {trianglepolygon.addPoint((int)Math.round(coord1.u), (int)Math.round(coord1.v));}
						if (coord2!=null) {trianglepolygon.addPoint((int)Math.round(coord2.u), (int)Math.round(coord2.v));}
						if (coord3!=null) {trianglepolygon.addPoint((int)Math.round(coord3.u), (int)Math.round(coord3.v));}
						trianglepolygon.addPoint((int)Math.round(coord5.u), (int)Math.round(coord5.v));
						trianglepolygon.addPoint((int)Math.round(coord4.u), (int)Math.round(coord4.v));
					}
					double minx = Double.POSITIVE_INFINITY;
					double maxx = Double.NEGATIVE_INFINITY;
					double miny = Double.POSITIVE_INFINITY;
					double maxy = Double.NEGATIVE_INFINITY;
					for (int i=0;i<trianglepolygon.npoints;i++) {
						if (trianglepolygon.xpoints[i]<minx) {minx = trianglepolygon.xpoints[i];}
						if (trianglepolygon.xpoints[i]>maxx) {maxx = trianglepolygon.xpoints[i];}
						if (trianglepolygon.ypoints[i]<miny) {miny = trianglepolygon.ypoints[i];}
						if (trianglepolygon.ypoints[i]>maxy) {maxy = trianglepolygon.ypoints[i];}
					}
					int minxind = (int)Math.ceil(minx); 
					int maxxind = (int)Math.floor(maxx); 
					int minyind = (int)Math.ceil(miny); 
					int maxyind = (int)Math.floor(maxy);
					if (minxind<0) {minxind=0;}
					if (maxxind>=hres) {maxxind=hres-1;}
					if (minyind<0) {minyind=0;}
					if (maxyind>=vres) {maxyind=vres-1;}
					int triwidth = maxxind-minxind+1; 
					int triheight = maxyind-minyind+1;
					if ((minxind<hres)&&(maxxind>=0)&&(minyind<vres)&&(maxyind>=0)&&(triwidth>0)&&(triheight>0)) {
						k[j] = new Rectangle(minxind,minyind,triwidth,triheight);
					}
				}
			}
		}
		return k;
	}
	
	public static Rectangle[] projectedSphereIntersection(Position vpos, Sphere[] vsphere, int hres, int vres, double hfov, double vfov, Matrix vmat, Plane nclipplane) {
		Rectangle[] k = null;
		if ((vpos!=null)&&(vsphere!=null)&&(vmat!=null)) {
			k = new Rectangle[vsphere.length];
			Position[] vpoint = sphereVertexList(vsphere);
			Direction[] lvec = vectorFromPoints(vpos, vsphere);
			double[] lvecl = vectorLength(lvec);
			double halfhfov = hfov/2.0f;
			double halfvfov = vfov/2.0f;
			double halfhfovmult = (1.0f/tand(halfhfov));
			double halfvfovmult = (1.0f/tand(halfvfov));
			double origindeltax = ((double)(hres-1))/2.0f;
			double origindeltay = ((double)(vres-1))/2.0f;
			double halfhres = ((double)(hres-1))/2.0f;
			double halfvres = ((double)(vres-1))/2.0f;
			Direction[] dirrightupvectors = projectedCameraDirections(vmat);
			Plane[] dirrightupplanes = planeFromNormalAtPoint(vpos, dirrightupvectors);
			double[][] fwdintpointsdist = planePointDistance(vpoint, dirrightupplanes);
			for (int i=0;i<vpoint.length;i++) {
				double prjspherehalfang = asind(vsphere[i].r/lvecl[i]);
				if (!Double.isFinite(prjspherehalfang)) {prjspherehalfang = 180.0f;}
				double hangle = atand(fwdintpointsdist[i][1]/fwdintpointsdist[i][0]);
				double vangle = atand(fwdintpointsdist[i][2]/fwdintpointsdist[i][0]);
				double hangle1 = hangle-prjspherehalfang;
				double hangle2 = hangle+prjspherehalfang;
				double vangle1 = vangle-prjspherehalfang;
				double vangle2 = vangle+prjspherehalfang;
				if (hangle1<-halfhfov) {hangle1=-halfhfov;}
				if (hangle2>halfhfov) {hangle2=halfhfov;}
				if (vangle1<-halfvfov) {vangle1=-halfvfov;}
				if (vangle2>halfvfov) {vangle2=halfvfov;}
				int hcenterind1 = (int)Math.ceil(halfhfovmult*halfhres*(tand(hangle1))+origindeltax);
				int hcenterind2 = (int)Math.floor(halfhfovmult*halfhres*(tand(hangle2))+origindeltax);
				int vcenterind1 = (int)Math.ceil(halfvfovmult*halfvres*(tand(vangle1))+origindeltay);
				int vcenterind2 = (int)Math.floor(halfvfovmult*halfvres*(tand(vangle2))+origindeltay);
				if (hcenterind1<0) {hcenterind1=0;}
				if (hcenterind2>=hres) {hcenterind2=hres-1;}
				if (vcenterind1<0) {vcenterind1=0;}
				if (vcenterind2>=vres) {vcenterind2=vres-1;}
				int spherewidth = hcenterind2-hcenterind1+1;
				int sphereheight = vcenterind2-vcenterind1+1;
				if ((hcenterind1<hres)&&(hcenterind2>=0)&&(vcenterind1<vres)&&(vcenterind2>=0)&&(spherewidth>0)&&(sphereheight>0)) {
					k[i] = new Rectangle(hcenterind1,vcenterind1,spherewidth,sphereheight);
				}
			}
		}
		return k;
	}
	public static Rectangle[][] cubemapSphereIntersection(Position vpos, Sphere[] vsphere, int vres, Plane nclipplane) {
		Rectangle[][] k = new Rectangle[6][vsphere.length];
		Matrix rotxp0 = rotationMatrix(0.0f, 0.0f, 0.0f);
		Matrix rotxp90 = rotationMatrix(-90.0f, 0.0f, 0.0f);
		Matrix rotxp180 = rotationMatrix(-180.0f, 0.0f, 0.0f);
		Matrix rotzn90 = rotationMatrix(0.0f, 0.0f, -90.0f);
		Matrix rotzp90 = rotationMatrix(0.0f, 0.0f, 90.0f);
		Matrix rotzp180 = rotationMatrix(0.0f, 0.0f, 180.0f);
		Matrix rotxp90zn90 = matrixMultiply(rotzn90, rotxp90);
		Matrix rotxp90zp90 = matrixMultiply(rotzp90, rotxp90);
		Matrix rotxp90zp180 = matrixMultiply(rotzp180, rotxp90);
		k[0] = projectedSphereIntersection(vpos, vsphere, vres, vres, 90, 90, rotxp90zn90, nclipplane);
		k[1] = projectedSphereIntersection(vpos, vsphere, vres, vres, 90, 90, rotxp90, nclipplane);
		k[2] = projectedSphereIntersection(vpos, vsphere, vres, vres, 90, 90, rotxp90zp90, nclipplane);
		k[3] = projectedSphereIntersection(vpos, vsphere, vres, vres, 90, 90, rotxp90zp180, nclipplane);
		k[4] = projectedSphereIntersection(vpos, vsphere, vres, vres, 90, 90, rotxp180, nclipplane);
		k[5] = projectedSphereIntersection(vpos, vsphere, vres, vres, 90, 90, rotxp0, nclipplane);
		return k;
	}

	public static Coordinate[] spheremapPoint(Position vpos, Position[] vpoint, int hres, int vres, Matrix vmat, Plane nclipplane) {
		Coordinate[] k = null;
		if ((vpos!=null)&&(vpoint!=null)&&(vmat!=null)) {
			k = new Coordinate[vpoint.length];
			double halfhfov = 360.0f/2.0f;
			double halfvfov = 180.0f/2.0f;
			double halfhreshfovmult = (((double)(hres-1))/2.0f)/halfhfov;
			double halfvresvfovmult = (((double)(vres-1))/2.0f)/halfvfov;
			double origindeltax = ((double)(hres-1))/2.0f;
			double origindeltay = ((double)(vres-1))/2.0f;
			Direction[] camdirs = projectedCameraDirections(vmat);
			Plane[] camdirrightupplanes = planeFromNormalAtPoint(vpos, camdirs);
			Plane[] camfwdplane = {camdirrightupplanes[0]};
			Plane[] camrightplane = {camdirrightupplanes[1]};
			Plane[] camupplane = {camdirrightupplanes[2]};
			double[][] fwdpointsdist = planePointDistance(vpoint, camfwdplane);
			double[][] rightpointsdist = planePointDistance(vpoint, camrightplane);
			double[][] uppointsdist = planePointDistance(vpoint, camupplane);
			for (int i=0;i<vpoint.length;i++) {
				Direction[] camfwddir = {new Direction(1.0f,0.0f,0.0f)};
				Direction[] vpointhdir = {new Direction(fwdpointsdist[i][0],rightpointsdist[i][0],0.0f)};
				Direction[] vpointvdir = {new Direction(fwdpointsdist[i][0],rightpointsdist[i][0],uppointsdist[i][0])};
				double[] hanglea = vectorAngle(vpointhdir, camfwddir);
				double[] vanglea = vectorAngle(vpointhdir, vpointvdir);
				if (!Double.isFinite(hanglea[0])) {hanglea[0]=0.0f;};
				if (!Double.isFinite(vanglea[0])) {vanglea[0]=90.0f;};
				double hangle = ((rightpointsdist[i][0]>=0.0)?1.0f:-1.0f)*hanglea[0];
				double vangle = ((uppointsdist[i][0]>=0.0)?1.0f:-1.0f)*vanglea[0];
				double hind = halfhreshfovmult*hangle+origindeltax;
				double vind = halfvresvfovmult*vangle+origindeltay;
				k[i] = new Coordinate(hind,vind);
			}
		}
		return k;
	}
	public static Rectangle[] spheremapTriangleIntersection(Position vpos, Triangle[] vtri, int hres, int vres, Matrix vmat, Plane nclipplane) {
		//TODO fix incorrect backside crossing triangle x-inverted two-part area and vertical maximum 0 to vres-1 height
		Rectangle[] k = null;
		if ((vpos!=null)&&(vtri!=null)&&(vmat!=null)) {
			k = new Rectangle[vtri.length];
			double halfhres = ((double)(hres-1))/2.0f;
			Direction[] camdirs = projectedCameraDirections(vmat);
			Plane[] camplanes = planeFromNormalAtPoint(vpos, camdirs);
			Plane[] camupplane = {camplanes[2]};
			Direction[] dirup = {camdirs[2]};
			Position[][] poleint = rayTriangleIntersection(vpos, dirup, vtri);
			Position[] vtripos1 = new Position[vtri.length];
			Position[] vtripos2 = new Position[vtri.length];
			Position[] vtripos3 = new Position[vtri.length];
			for (int i=0;i<vtri.length;i++) {
				vtripos1[i] = vtri[i].pos1;
				vtripos2[i] = vtri[i].pos2;
				vtripos3[i] = vtri[i].pos3;
			}
			Coordinate[] vtripos1pixel = spheremapPoint(vpos, vtripos1, hres, vres, vmat, nclipplane);
			Coordinate[] vtripos2pixel = spheremapPoint(vpos, vtripos2, hres, vres, vmat, nclipplane);
			Coordinate[] vtripos3pixel = spheremapPoint(vpos, vtripos3, hres, vres, vmat, nclipplane);
			for (int j=0;j<vtri.length;j++) {
				if ((vtripos1pixel[j]!=null)&&(vtripos2pixel[j]!=null)&&(vtripos3pixel[j]!=null)) {
					double minx = Double.POSITIVE_INFINITY;
					double maxx = Double.NEGATIVE_INFINITY;
					double miny = Double.POSITIVE_INFINITY;
					double maxy = Double.NEGATIVE_INFINITY;
					Coordinate[] vtripospixels = {vtripos1pixel[j], vtripos2pixel[j], vtripos3pixel[j]};
					for (int i=0;i<vtripospixels.length;i++) {
						if (vtripospixels[i].u<minx) {minx = vtripospixels[i].u;}
						if (vtripospixels[i].u>maxx) {maxx = vtripospixels[i].u;}
						if (vtripospixels[i].v<miny) {miny = vtripospixels[i].v;}
						if (vtripospixels[i].v>maxy) {maxy = vtripospixels[i].v;}
					}
					double vtripospixel12dif = Math.abs(vtripospixels[1].u-vtripospixels[0].u);
					double vtripospixel13dif = Math.abs(vtripospixels[2].u-vtripospixels[0].u);
					double vtripospixel23dif = Math.abs(vtripospixels[2].u-vtripospixels[1].u);
					boolean vtripospixel12crossingbehind = vtripospixel12dif>halfhres;
					boolean vtripospixel13crossingbehind = vtripospixel13dif>halfhres;
					boolean vtripospixel23crossingbehind = vtripospixel23dif>halfhres;
					if (vtripospixel12crossingbehind||vtripospixel13crossingbehind||vtripospixel23crossingbehind) {
						minx = 0;
						maxx = hres-1;
					}
					if (poleint[0][j]!=null) {
						minx = 0;
						maxx = hres-1;
						Position[] poleintpos = {poleint[0][j]};
						double[][] poleintdist = planePointDistance(poleintpos, camupplane);
						if (poleintdist[0][0]<0) {
							miny = 0;
						} else if (poleintdist[0][0]>0) {
							maxy = vres-1;
						}
					}
					int minxind = (int)Math.ceil(minx);
					int maxxind = (int)Math.floor(maxx);
					int minyind = (int)Math.ceil(miny);
					int maxyind = (int)Math.floor(maxy);
					int triwidth = maxxind-minxind+1;
					int triheight = maxyind-minyind+1;
					if ((triwidth!=0)&&(triheight>0)) {
						k[j] = new Rectangle(minxind,minyind,triwidth,triheight);
					}
				}
			}
		}
		return k;
	}
	public static Rectangle[] spheremapSphereIntersection(Position vpos, Sphere[] vsphere, int hres, int vres, Matrix vmat, Plane nclipplane) {
		//TODO fix incorrect pole and back side overlapping sphere rectangle area
		Rectangle[] k = null;
		if ((vpos!=null)&&(vsphere!=null)&&(vmat!=null)) {
			k = new Rectangle[vsphere.length];
			Position[] vpoint = sphereVertexList(vsphere);
			Direction[] lvec = vectorFromPoints(vpos, vsphere);
			double[] lvecl = vectorLength(lvec);
			double halfhfov = 360.0f/2.0f;
			double halfvfov = 180.0f/2.0f;
			double halfhreshfovmult = (((double)(hres-1))/2.0f)/halfhfov;
			double halfvresvfovmult = (((double)(vres-1))/2.0f)/halfvfov;
			double origindeltax = ((double)(hres-1))/2.0f;
			double origindeltay = ((double)(vres-1))/2.0f;
			Direction[] dirrightupvectors = projectedCameraDirections(vmat);
			Plane[] dirrightupplanes = planeFromNormalAtPoint(vpos, dirrightupvectors);
			double[][] fwdintpointsdist = planePointDistance(vpoint, dirrightupplanes);
			for (int i=0;i<vpoint.length;i++) {
				double prjspherehalfang = asind(vsphere[i].r/lvecl[i]);
				if (!Double.isFinite(prjspherehalfang)) {prjspherehalfang = 180.0f;}
				Direction camfwdvector = new Direction(1.0f, 0.0f, 0.0f);
				Direction[] posrightvector = {new Direction(fwdintpointsdist[i][0], fwdintpointsdist[i][1], 0.0f)};
				double[] posrightvectorangle = vectorAngle(camfwdvector, posrightvector);
				double[] posrightvectorlen = vectorLength(posrightvector);
				double hangle = ((fwdintpointsdist[i][1]<0.0f)?-1:1)*posrightvectorangle[0];
				double vangle = atand((fwdintpointsdist[i][2])/posrightvectorlen[0]);
				double hangle1 = hangle-prjspherehalfang;
				double hangle2 = hangle+prjspherehalfang;
				double vangle1 = vangle-prjspherehalfang;
				double vangle2 = vangle+prjspherehalfang;
				if (hangle1<-halfhfov) {hangle1=-halfhfov;}
				if (hangle2>halfhfov) {hangle2=halfhfov;}
				if (vangle1<-halfvfov) {vangle1=-halfvfov;}
				if (vangle2>halfvfov) {vangle2=halfvfov;}
				int hcenterind1 = (int)Math.ceil(halfhreshfovmult*hangle1+origindeltax);
				int hcenterind2 = (int)Math.floor(halfhreshfovmult*hangle2+origindeltax);
				int vcenterind1 = (int)Math.ceil(halfvresvfovmult*vangle1+origindeltay);
				int vcenterind2 = (int)Math.floor(halfvresvfovmult*vangle2+origindeltay);
				if (hcenterind1<0) {hcenterind1=0;}
				if (hcenterind2>=hres) {hcenterind2=hres-1;}
				if (vcenterind1<0) {vcenterind1=0;}
				if (vcenterind2>=vres) {vcenterind2=vres-1;}
				int spherewidth = hcenterind2-hcenterind1+1;
				int sphereheight = vcenterind2-vcenterind1+1;
				if ((spherewidth>0)&&(sphereheight>0)) {
					k[i] = new Rectangle(hcenterind1,vcenterind1,spherewidth,sphereheight);
				}
			}
		}
		return k;
	}
	
	public static AxisAlignedBoundingBox axisAlignedBoundingBox(Position[] vertexlist) {
		double xmin=Double.POSITIVE_INFINITY, ymin=Double.POSITIVE_INFINITY, zmin=Double.POSITIVE_INFINITY;
		double xmax=Double.NEGATIVE_INFINITY, ymax=Double.NEGATIVE_INFINITY, zmax=Double.NEGATIVE_INFINITY;
		for (int i=0;i<vertexlist.length;i++) {
			if (vertexlist[i].x<xmin) {xmin=vertexlist[i].x;}
			if (vertexlist[i].x>xmax) {xmax=vertexlist[i].x;}
			if (vertexlist[i].y<ymin) {ymin=vertexlist[i].y;}
			if (vertexlist[i].y>ymax) {ymax=vertexlist[i].y;}
			if (vertexlist[i].z<zmin) {zmin=vertexlist[i].z;}
			if (vertexlist[i].z>zmax) {zmax=vertexlist[i].z;}
		}
		return new AxisAlignedBoundingBox(new Position(xmin,ymin,zmin),new Position(xmax,ymax,zmax));
	}
	public static Sphere pointCloudCircumSphere(Position[] vertexlist) {
		AxisAlignedBoundingBox pointcloudlimits = axisAlignedBoundingBox(vertexlist);
		Position pointcloudcenter = new Position((pointcloudlimits.pos1.x+pointcloudlimits.pos2.x)/2.0f,(pointcloudlimits.pos1.y+pointcloudlimits.pos2.y)/2.0f,(pointcloudlimits.pos1.z+pointcloudlimits.pos2.z)/2.0f);
		Direction[] pointvectors = vectorFromPoints(pointcloudcenter, vertexlist);
		double[] pointdistances = vectorLength(pointvectors);
		double maxradius = -1;
		for (int i=0;i<pointdistances.length;i++) {
			if (pointdistances[i]>maxradius) {
				maxradius = pointdistances[i]; 
			}
		}
		return new Sphere(pointcloudcenter.x,pointcloudcenter.y,pointcloudcenter.z,maxradius);
	}
	public static Sphere[] triangleCircumSphere(Triangle[] trianglelist) {
		Sphere[] k = new Sphere[trianglelist.length];
		for (int i=0;i<trianglelist.length;i++) {
			Direction[] v12 = {new Direction(trianglelist[i].pos2.x-trianglelist[i].pos1.x,trianglelist[i].pos2.y-trianglelist[i].pos1.y,trianglelist[i].pos2.z-trianglelist[i].pos1.z)};
			Direction[] v13 = {new Direction(trianglelist[i].pos3.x-trianglelist[i].pos1.x,trianglelist[i].pos3.y-trianglelist[i].pos1.y,trianglelist[i].pos3.z-trianglelist[i].pos1.z)};
			double[] v12D = vectorDot(v12);
			double[] v13D = vectorDot(v13);
			Direction[] cv12v13 = vectorCross(v12,v13);
			double[] cv12v13D = vectorDot(cv12v13);
			Direction[] toparg = {new Direction(v12D[0]*v13[0].dx-v13D[0]*v12[0].dx,v12D[0]*v13[0].dy-v13D[0]*v12[0].dy,v12D[0]*v13[0].dz-v13D[0]*v12[0].dz)};
			Direction[] top = vectorCross(toparg,cv12v13);
			double bottom = 2.0f*cv12v13D[0];
			Position[] p1 = {trianglelist[i].pos1};
			if (bottom!=0) {
				Direction[] sphereradiusvector = {new Direction(top[0].dx/bottom, top[0].dy/bottom, top[0].dz/bottom)};
				Position[] spherecenter = translate(p1, sphereradiusvector[0], 1.0f);
				double[] sphereradius = vectorLength(sphereradiusvector);
				k[i] = new Sphere(spherecenter[0].x,spherecenter[0].y,spherecenter[0].z,sphereradius[0]);
			}
		}
		return k;
	}
	public static Sphere[] triangleInSphere(Triangle[] trianglelist) {
		Sphere[] k = new Sphere[trianglelist.length];
		for (int i=0;i<trianglelist.length;i++) {
			Position[] tripos1 = {trianglelist[i].pos1};
			Position[] tripos2 = {trianglelist[i].pos2};
			Position[] tripos3 = {trianglelist[i].pos3};
			Direction[] tridir12 = vectorFromPoints(tripos1, tripos2);
			Direction[] tridir13 = vectorFromPoints(tripos1, tripos3);
			Direction[] tridir23 = vectorFromPoints(tripos2, tripos3);
			double[] tridir12len = vectorLength(tridir12);
			double[] tridir13len = vectorLength(tridir13);
			double[] tridir23len = vectorLength(tridir23);
			double lensum = tridir12len[0]+tridir13len[0]+tridir23len[0];
			double xcoord = (tridir12len[0]*tripos3[0].x+tridir13len[0]*tripos2[0].x+tridir23len[0]*tripos1[0].x)/lensum;
			double ycoord = (tridir12len[0]*tripos3[0].y+tridir13len[0]*tripos2[0].y+tridir23len[0]*tripos1[0].y)/lensum;
			double zcoord = (tridir12len[0]*tripos3[0].z+tridir13len[0]*tripos2[0].z+tridir23len[0]*tripos1[0].z)/lensum;
			double semiperimeter = 0.5f*lensum;
			double trianglearea = Math.sqrt(semiperimeter*(semiperimeter-tridir12len[0])*(semiperimeter-tridir13len[0])*(semiperimeter-tridir23len[0]));
			double insphereradius = trianglearea/semiperimeter;
			k[i] = new Sphere(xcoord,ycoord,zcoord,insphereradius);
		}
		return k;
	}
	public static double[] triangleArea(Triangle[] trianglelist) {
		double[] k = new double[trianglelist.length];
		for (int i=0;i<trianglelist.length;i++) {
			Position[] tripos1 = {trianglelist[i].pos1};
			Position[] tripos2 = {trianglelist[i].pos2};
			Position[] tripos3 = {trianglelist[i].pos3};
			Direction[] tridir12 = vectorFromPoints(tripos1, tripos2);
			Direction[] tridir13 = vectorFromPoints(tripos1, tripos3);
			Direction[] tridir23 = vectorFromPoints(tripos2, tripos3);
			double[] tridir12len = vectorLength(tridir12);
			double[] tridir13len = vectorLength(tridir13);
			double[] tridir23len = vectorLength(tridir23);
			double lensum = tridir12len[0]+tridir13len[0]+tridir23len[0];
			double semiperimeter = 0.5f*lensum;
			k[i] = Math.sqrt(semiperimeter*(semiperimeter-tridir12len[0])*(semiperimeter-tridir13len[0])*(semiperimeter-tridir23len[0]));
		}
		return k;
	}
	
	public static double[][] linearAngleLengthInterpolation(Position vpos, Line[] vline, double[] vangle) {
		double[][] k = null;
		if ((vpos!=null)&&(vline!=null)&&(vangle!=null)) {
			k = new double[vline.length][vangle.length];
			for (int j=0;j<vline.length;j++) {
				Position[] startpos = {vline[j].pos1};
				Position[] endpos = {vline[j].pos2};
				Direction[] vposstartdir = vectorFromPoints(vpos, startpos);
				Direction[] startvposdir = {vposstartdir[0].invert()};
				Direction[] startenddir = vectorFromPoints(startpos, endpos);
				double[] vposstartdirlen = vectorLength(vposstartdir);
				double[] startenddirlen = vectorLength(startenddir);
				double[] startposangle = vectorAngle(startvposdir,startenddir);
				for (int i=0;i<vangle.length;i++) {
					double vposangle = vangle[i];
					double endposangle = 180.0f-startposangle[0]-vposangle;
					double startendangledirlen = vposstartdirlen[0]*(sind(vposangle)/sind(endposangle));
					double startendangledirlenfrac = startendangledirlen/startenddirlen[0];
					k[j][i] = startendangledirlenfrac;
				}
			}
		}
		return k;
	}
	public static Position[] pointOnPlane(Plane[] vplane) {
		Position[] k = new Position[vplane.length];
		for (int i=0;i<vplane.length;i++) {
			if (vplane[i].a!=0) {
				k[i] = new Position(-vplane[i].d/vplane[i].a,0.0f,0.0f);
			} else if (vplane[i].b!=0) {
				k[i] = new Position(0.0f,-vplane[i].d/vplane[i].b,0.0f);
			} else if (vplane[i].c!=0) {
				k[i] = new Position(0.0f,0.0f,-vplane[i].d/vplane[i].c);
			}
		}
		return k;
	}

	public static Position cameraPlanePosition(Position drawpos, int coordx, int coordy, int renderwidth, int renderheight, boolean snaplinemode, int gridstep, Matrix vmat) {
		Direction[] camdirs = projectedCameraDirections(vmat);
		int origindeltax = (int)Math.floor(((double)(renderwidth-1))/2.0f);
		int origindeltay = (int)Math.floor(((double)(renderheight-1))/2.0f);
		int mouserelativelocationx = coordx-origindeltax;
		int mouserelativelocationy = coordy-origindeltay;
		if (snaplinemode) {
    		mouserelativelocationx = snapToGrid(mouserelativelocationx, gridstep);
    		mouserelativelocationy = snapToGrid(mouserelativelocationy, gridstep);
		}
		Position[] drawposa = {drawpos};
		drawposa = translate(drawposa, camdirs[1], mouserelativelocationx);
		drawposa = translate(drawposa, camdirs[2], mouserelativelocationy);
		return drawposa[0];
	}
	
	public static int snapToGrid(int coordinate, int gridstep) {
		return gridstep*(int)Math.round(((double)coordinate)/((double)gridstep));
	}
	
	public static double calculateVfov(int renderwidth, int renderheight, double hfov) {
		return 2.0f*atand((((double)renderheight)/((double)renderwidth))*tand(hfov/2.0f));
	}
	
	public static Color sourceBlend(Color source, float alpha) {
		Color k = source;
		if (source!=null) {
			float[] sourcecomp = source.getRGBComponents(new float[4]);
			float[] sourcecomppa = new float[4];
			for (int i=0;i<4;i++) {
				sourcecomppa[i] = alpha*sourcecomp[i];
			}
			k = new Color(sourcecomppa[0],sourcecomppa[1],sourcecomppa[2],sourcecomppa[3]);
		}
		return k;
	}
	public static Color sourceOverBlend(Color dest, Color source, float alpha) {
		Color k = dest;
		if ((source!=null)&&(dest!=null)) {
			float[] sourcecomp = source.getRGBComponents(new float[4]);
			float[] destcomp = dest.getRGBComponents(new float[4]);
			float[] sourcecomppa = new float[4];
			float[] colorcomp = new float[4];
			for (int i=0;i<4;i++) {
				sourcecomppa[i] = alpha*sourcecomp[i];
				colorcomp[i] = sourcecomppa[i] + destcomp[i]*(1.0f-sourcecomppa[i]);
			}
			k = new Color(colorcomp[0],colorcomp[1],colorcomp[2],colorcomp[3]);
		}
		return k;
	}
	
	public static double refractionOutAngle(double anglein, float refraction1, float refraction2) {
		return asind((refraction1/refraction2)*sind(anglein));
	}
	
	public static double mod(double val, double modulo) {
		return val-Math.floor(val/modulo)*modulo;
	}
	public static Coordinate[] mod(Coordinate[] tex, double modulo) {
		Coordinate[] k = null;
		if (tex!=null) {
			k = new Coordinate[tex.length];
			for (int i=0;i<tex.length;i++) {
				k[i] = new Coordinate(mod(tex[i].u, modulo), mod(tex[i].v, modulo));
			}
		}
		return k;
	}
	
	public static Ray[][] surfaceMirrorRay(Ray[] vray, Plane[] vsurf) {
		Ray[][] k = null;
		if ((vsurf!=null)&&(vray!=null)) {
			k = new Ray[vray.length][vsurf.length];
			Direction[] vsurfnorm = planeNormal(vsurf);
			for(int j=0;j<vray.length;j++)  {
				Position[] raypos = {vray[j].pos};
				Direction[] raydir = {vray[j].dir};
				double[][] rayintdist = rayPlaneDistance(raypos[0], raydir, vsurf);
				for(int i=0;i<vsurf.length;i++)  {
					if ((Double.isFinite(rayintdist[0][i]))&&(rayintdist[0][i]>=1.0f)) {
						Position[] rayint = translate(raypos, raydir[0], rayintdist[0][i]);
						Matrix rayvsurfrot = rotationMatrixAroundAxis(vsurfnorm[i], 180.0f);
						Direction[] mirrorraydir = matrixMultiply(raydir, rayvsurfrot);
						Direction[] mirrorraydirn = normalizeVector(mirrorraydir);
						Direction mirrorraydirninv = mirrorraydirn[0].invert();
						k[j][i] = new Ray(rayint[0], mirrorraydirninv);
					}
				}
			}
		}
		return k;
	}
	public static Ray[][] surfaceRefractionRay(Ray[] vray, Plane[] vsurf, float refraction1, float refraction2) {
		Ray[][] k = null;
		if ((vsurf!=null)&&(vray!=null)) {
			k = new Ray[vray.length][vsurf.length];
			Direction[] vsurfnorm = planeNormal(vsurf);
			for(int j=0;j<vray.length;j++)  {
				Position[] raypos = {vray[j].pos};
				Direction[] raydir = {vray[j].dir};
				double[][] rayintdist = rayPlaneDistance(raypos[0], raydir, vsurf);
				for(int i=0;i<vsurf.length;i++)  {
					if ((Double.isFinite(rayintdist[0][i]))&&(rayintdist[0][i]>=1.0f)) {
						Position[] rayint = translate(raypos, raydir[0], rayintdist[0][i]);
						Direction[] refnormal = vectorCross(vsurfnorm[i], raydir);
						if (refnormal[0].isFinite()) {
							if (refnormal[0].isZero()) {
								k[j][i] = new Ray(rayint[0], raydir[0]);
							} else {
								double[] rayvsurfangle = vectorAngle(vsurfnorm[i], raydir);
								double rayvsurfangleout = refractionOutAngle(rayvsurfangle[0], refraction1, refraction2);
								if (Double.isFinite(rayvsurfangleout)) {
									double refrayrotangle = rayvsurfangle[0]-rayvsurfangleout;
									Matrix rayvsurfrefrot = rotationMatrixAroundAxis(refnormal[0], -refrayrotangle);
									Direction[] refractionraydir = matrixMultiply(raydir, rayvsurfrefrot);
									Direction[] refractionraydirn = normalizeVector(refractionraydir);
									k[j][i] = new Ray(rayint[0], refractionraydirn[0]);
								}
							}
						}
					}
				}
			}
		}
		return k;
	}
	public static PlaneRay[][] surfaceMirrorPlaneRay(PlaneRay[] vplaneray, Plane[] vsurf) {
		PlaneRay[][] k = null;
		if ((vplaneray!=null)&&(vsurf!=null)) {
			k = new PlaneRay[vplaneray.length][vsurf.length];
			Direction[] vsurfnorm = planeNormal(vsurf);
			for (int j=0;j<vplaneray.length;j++) {
				Position raypos = vplaneray[j].pos;
				Position[] rayposa = {raypos};
				Direction[] rayfwddir = {vplaneray[j].dir};
				Plane[] rayplane = {vplaneray[j].plane};
				double[] rayvfov = {vplaneray[j].vfov};
				Direction[] rayplanenorm = planeNormal(rayplane);
				double[][] rayfwdvsufrintdist = rayPlaneDistance(raypos, rayfwddir, vsurf);
				Position[][] rayfwdvsufrint = rayPlaneIntersection(raypos, rayfwddir, vsurf);
				Line[][] ppint = planePlaneIntersection(rayplane, vsurf);
				double[] rayrgtvsurfangles = planeAngle(rayplane[0], vsurf);
				for (int i=0;i<vsurf.length;i++) {
					if ((Double.isFinite(rayfwdvsufrintdist[0][i]))&&(rayfwdvsufrintdist[0][i]>=1.0f)&&(rayfwdvsufrint[0][i]!=null)&&(rayfwdvsufrint[0][i].isFinite())&&(ppint[0][i]!=null)&&(ppint[0][i].isFinite())) {
						Line[] ppintline = {ppint[0][i]};
						Direction[] ppintlinedir = vectorFromPoints(ppintline);
						double rayrgtvsurfangle = rayrgtvsurfangles[i];
						double anglemult = 1.0f;
						if (rayrgtvsurfangle>90.0f) {
							rayrgtvsurfangle = 180.0f-rayrgtvsurfangle;
							anglemult = -1.0f;
						}
						Position[] rayfwdvsurfpos = {rayfwdvsufrint[0][i]};
						double rotangle = anglemult*2.0f*rayrgtvsurfangle;
						Position[] rayposmirror = rotateAroundAxisPos(rayposa, rayfwdvsurfpos[0], ppintlinedir[0], rotangle);
						Matrix rayvsurfrot = rotationMatrixAroundAxis(vsurfnorm[i], 180.0f);
						Direction[] mirrorraydir = matrixMultiply(rayfwddir, rayvsurfrot);
						Direction[] mirrorraydirn = normalizeVector(mirrorraydir);
						Direction[] mirrorraydirninv = {mirrorraydirn[0].invert()};
						Direction[] mirrorplanenormdir = matrixMultiply(rayplanenorm, rayvsurfrot);
						Direction[] mirrorplanenormdirn = normalizeVector(mirrorplanenormdir);
						Plane[] mirrorplanenormplane = planeFromNormalAtPoint(rayposmirror[0], mirrorplanenormdirn);
						k[j][i] = new PlaneRay(rayposmirror[0],mirrorraydirninv[0],mirrorplanenormplane[0],rayvfov[0]);
					}
				}
			}
		}
		return k;
	}
	public static PlaneRay[][] surfaceRefractionPlaneRay(PlaneRay[] vplaneray, Plane[] vsurf, float refraction1, float refraction2) {
		PlaneRay[][] k = null;
		if ((vplaneray!=null)&&(vsurf!=null)) {
			k = new PlaneRay[vplaneray.length][vsurf.length];
		}
		return k;
	}
	public static RenderView[] surfaceMirrorProjectedCamera(Position campos, Plane[] vsurf, double hfov, double vfov, Matrix viewrot) {
		RenderView[] k = null;
		if ((vsurf!=null)&&(campos!=null)) {
			k = new RenderView[vsurf.length];
			Position[] camposa = {campos};
			Direction[] camdirs = projectedCameraDirections(viewrot);
			Plane[] camplanes = planeFromNormalAtPoint(campos, camdirs);
			Direction[] camfwddir = {camdirs[0]};
			Plane[] camrgtplane = {camplanes[1]};
			double[][] camfwdvsufrintdist = rayPlaneDistance(campos, camfwddir, vsurf);
			Position[][] camfwdvsufrint = rayPlaneIntersection(campos, camfwddir, vsurf);
			Line[][] ppint = planePlaneIntersection(camrgtplane, vsurf);
			double[] camrgtvsurfangles = planeAngle(camrgtplane[0], vsurf);
			for (int i=0;i<vsurf.length;i++) {
				if ((Double.isFinite(camfwdvsufrintdist[0][i]))&&(camfwdvsufrintdist[0][i]>=1.0f)&&(camfwdvsufrint[0][i]!=null)&&(camfwdvsufrint[0][i].isFinite())&&(ppint[0][i]!=null)&&(ppint[0][i].isFinite())) {
					Line[] ppintline = {ppint[0][i]};
					Direction[] ppintlinedir = vectorFromPoints(ppintline);
					double camrgtvsurfangle = camrgtvsurfangles[i];
					double anglemult = 1.0f;
					if (camrgtvsurfangle>90.0f) {
						camrgtvsurfangle = 180.0f-camrgtvsurfangle;
						anglemult = -1.0f;
					}
					Position[] camfwdvsurfpos = {camfwdvsufrint[0][i]};
					double rotangle = anglemult*2.0f*camrgtvsurfangle;
					Matrix mirrormat = rotationMatrixAroundAxis(ppintlinedir[0], rotangle);
					Matrix viewrotmirror = matrixMultiply(mirrormat, viewrot);
					Position[] camposmirror = rotateAroundAxisPos(camposa, camfwdvsurfpos[0], ppintlinedir[0], rotangle);
					k[i] = new RenderView();
					k[i].rot = viewrotmirror;
					k[i].pos = camposmirror[0];
					k[i].hfov = hfov;
					k[i].vfov = vfov;
				}
			}
		}
		return k;
	}
	public static RenderView[] surfaceRefractionProjectedCamera(Position campos, Plane[] vsurf, int renderwidth, double hfov, int renderheight, double vfov, Matrix viewrot, float refraction1, float refraction2) {
		RenderView[] k = null;
		if ((vsurf!=null)&&(campos!=null)) {
			k = new RenderView[vsurf.length];
			double halfvfov = vfov/2.0f;
			double halfhfov = hfov/2.0f;
			Position[] camposa = {campos};
			Direction[] camzerofwddir = {new Direction(0.0f,-1.0f,0.0f)};
			double[] cameravangles = {-halfvfov, halfvfov};
			double[] camerahangles = {-halfhfov, halfhfov};
			Direction[] camdirs = projectedCameraDirections(viewrot);
			Plane[] camplanes = planeFromNormalAtPoint(campos, camdirs);
			Direction[] camfwddir = {camdirs[0]};
			Direction[] camrgtdir = {camdirs[1]};
			Direction[] camupdir = {camdirs[2]};
			Plane[] camfwdplane = {camplanes[0]};
			Plane[] camrgtplane = {camplanes[1]};
			Plane[] camupplane = {camplanes[2]};
			Direction[] vsurfnorm = planeNormal(vsurf);
			double[][] camfwdvsufrintdist = rayPlaneDistance(campos, camfwddir, vsurf);
			Position[][] camfwdvsufrint = rayPlaneIntersection(campos, camfwddir, vsurf);
			Line[][] ppint = planePlaneIntersection(camrgtplane, vsurf);
			for (int i=0;i<vsurf.length;i++) {
				if ((Double.isFinite(camfwdvsufrintdist[0][i]))&&(camfwdvsufrintdist[0][i]>=1.0f)&&(camfwdvsufrint[0][i]!=null)&&(camfwdvsufrint[0][i].isFinite())&&(ppint[0][i]!=null)&&(ppint[0][i].isFinite())) {
					Line[] ppintline = {ppint[0][i]};
					Direction[] vsurfnormal = {vsurfnorm[i]};
					vsurfnormal = normalizeVector(vsurfnormal);
					double[] camfwddirposnormangle = vectorAngle(camfwddir, vsurfnormal);
					if (camfwddirposnormangle[0]<90.0f) {
						Direction[] newvsurfnormal = {vsurfnormal[0].invert()};
						vsurfnormal = newvsurfnormal;
					}
					Direction[] ppintlinedir = vectorFromPoints(ppintline);
					Direction[] horizlinedir = vectorCross(vsurfnormal, ppintlinedir);
					Position[] camfwdvsurfpos = {camfwdvsufrint[0][i]};
					Position[] camfwdvsurfpos2 = translate(camfwdvsurfpos,ppintlinedir[0],1.0f);
					Position[] camfwdhsurfpos2 = translate(camfwdvsurfpos,horizlinedir[0],1.0f);
					Line[] centerheightline = {new Line(camfwdvsurfpos[0], camfwdvsurfpos2[0])};
					Line[] centerwidthline = {new Line(camfwdvsurfpos[0], camfwdhsurfpos2[0])};
					double[][] linevlen = linearAngleLengthInterpolation(campos, centerheightline, cameravangles);
					double[][] linehlen = linearAngleLengthInterpolation(campos, centerwidthline, camerahangles);
					Position[] camfwdvsurfvendpos1 = translate(camfwdvsurfpos,ppintlinedir[0],linevlen[0][0]);
					Position[] camfwdvsurfvendpos2 = translate(camfwdvsurfpos,ppintlinedir[0],linevlen[0][1]);
					Position[] camfwdvsurfhendpos1 = translate(camfwdvsurfpos,horizlinedir[0],linehlen[0][0]);
					Position[] camfwdvsurfhendpos2 = translate(camfwdvsurfpos,horizlinedir[0],linehlen[0][1]);
					Direction[] camfwdvsurfvendposdir1 = vectorFromPoints(camposa, camfwdvsurfvendpos1);
					Direction[] camfwdvsurfvendposdir2 = vectorFromPoints(camposa, camfwdvsurfvendpos2);
					Direction[] camfwdvsurfhendposdir1 = vectorFromPoints(camposa, camfwdvsurfhendpos1);
					Direction[] camfwdvsurfhendposdir2 = vectorFromPoints(camposa, camfwdvsurfhendpos2);
					Direction[] camfwdvenddir12 = vectorFromPoints(camfwdvsurfvendpos1, camfwdvsurfvendpos2);
					Direction[] camfwdvenddir12inv = {camfwdvenddir12[0].invert()};
					Direction[] camfwdvenddir12invn = normalizeVector(camfwdvenddir12inv);
					Direction[] camfwdhenddir12 = vectorFromPoints(camfwdvsurfhendpos1, camfwdvsurfhendpos2);
					Direction[] camfwdhenddir12inv = {camfwdhenddir12[0].invert()};
					Direction[] camfwdhenddir12invn = normalizeVector(camfwdhenddir12inv);
					double[] camfwdvenddir12len = vectorLength(camfwdvenddir12);
					double[] camfwdhenddir12len = vectorLength(camfwdhenddir12);
					Plane[] camfwdvsurfvendposplane1 = planeFromNormalAtPoint(camfwdvsurfvendpos1, camfwdvenddir12);
					Plane[] camfwdvsurfvendposplane2 = planeFromNormalAtPoint(camfwdvsurfvendpos2, camfwdvenddir12inv);
					Plane[] camfwdvsurfhendposplane1 = planeFromNormalAtPoint(camfwdvsurfhendpos1, camfwdhenddir12);
					Plane[] camfwdvsurfhendposplane2 = planeFromNormalAtPoint(camfwdvsurfhendpos2, camfwdhenddir12inv);
					double[][] camvplane1dist = planePointDistance(camposa, camfwdvsurfvendposplane1);
					double[][] camvplane2dist = planePointDistance(camposa, camfwdvsurfvendposplane2);
					double[][] camhplane1dist = planePointDistance(camposa, camfwdvsurfhendposplane1);
					double[][] camhplane2dist = planePointDistance(camposa, camfwdvsurfhendposplane2);
					double[] camfwddirvposnormangle1 = vectorAngle(camfwdvsurfvendposdir1, vsurfnormal);
					double[] camfwddirvposnormangle2 = vectorAngle(camfwdvsurfvendposdir2, vsurfnormal);
					double[] camfwddirhposnormangle1 = vectorAngle(camfwdvsurfhendposdir1, vsurfnormal);
					double[] camfwddirhposnormangle2 = vectorAngle(camfwdvsurfhendposdir2, vsurfnormal);
					double fwdvendangle1 = camfwddirvposnormangle1[0]>90.0f?180.0f-camfwddirvposnormangle1[0]:camfwddirvposnormangle1[0];
					double fwdvendangle2 = camfwddirvposnormangle2[0]>90.0f?180.0f-camfwddirvposnormangle2[0]:camfwddirvposnormangle2[0];
					double fwdhendangle1 = camfwddirhposnormangle1[0]>90.0f?180.0f-camfwddirhposnormangle1[0]:camfwddirhposnormangle1[0];
					double fwdhendangle2 = camfwddirhposnormangle2[0]>90.0f?180.0f-camfwddirhposnormangle2[0]:camfwddirhposnormangle2[0];
					double fwdoutvangle1 = refractionOutAngle(fwdvendangle1, refraction1, refraction2);
					double fwdoutvangle2 = refractionOutAngle(fwdvendangle2, refraction1, refraction2);
					double fwdouthangle1 = refractionOutAngle(fwdhendangle1, refraction1, refraction2);
					double fwdouthangle2 = refractionOutAngle(fwdhendangle2, refraction1, refraction2);
						if ((Double.isFinite(fwdoutvangle1))&&(Double.isFinite(fwdoutvangle2))&&(Double.isFinite(fwdouthangle1))&&(Double.isFinite(fwdouthangle2))) {
						double reffwdvanglemult1 = camvplane1dist[0][0]>0.0f?1.0f:-1.0f;
						double reffwdvanglemult2 = camvplane2dist[0][0]>0.0f?1.0f:-1.0f;
						double reffwdhanglemult1 = camhplane1dist[0][0]>0.0f?1.0f:-1.0f;
						double reffwdhanglemult2 = camhplane2dist[0][0]>0.0f?1.0f:-1.0f;
						double reffwdvangle1 = 90.0f - fwdoutvangle1*reffwdvanglemult1;
						double reffwdvangle2 = 90.0f - fwdoutvangle2*reffwdvanglemult2;
						double reffwdhangle1 = 90.0f - fwdouthangle1*reffwdhanglemult1;
						double reffwdhangle2 = 90.0f - fwdouthangle2*reffwdhanglemult2;
						double refcamvfov = 180.0f - reffwdvangle1 - reffwdvangle2;
						double refcamhfov = 180.0f - reffwdhangle1 - reffwdhangle2;
						double refcamhalfvfov = refcamvfov/2.0f;
						double refcamhalfhfov = refcamhfov/2.0f;
						double refcamendhalfvangle = 180.0f - reffwdvangle2 - refcamhalfvfov;
						double refcamendhalfhangle = 180.0f - reffwdhangle2 - refcamhalfhfov;
						double reffwdvend2len = sind(reffwdvangle1)/sind(refcamvfov)*camfwdvenddir12len[0];
						double reffwdhend2len = sind(reffwdhangle1)/sind(refcamhfov)*camfwdhenddir12len[0];
						double reffwd12halfvanglelen = sind(refcamhalfvfov)/sind(refcamendhalfvangle)*reffwdvend2len;
						double reffwd12halfhanglelen = sind(refcamhalfhfov)/sind(refcamendhalfhangle)*reffwdhend2len;
						double refvdir12len = reffwdvend2len*cosd(reffwdvangle2);
						double refhdir12len = reffwdhend2len*cosd(reffwdhangle2);
						double refvnormlen = reffwdvend2len*sind(reffwdvangle2);
						double refhnormlen = reffwdhend2len*sind(reffwdhangle2);
						Position[] camposvrefraction = camfwdvsurfvendpos2;
						camposvrefraction = translate(camposvrefraction,camfwdvenddir12invn[0],refvdir12len);
						camposvrefraction = translate(camposvrefraction,vsurfnormal[0],refvnormlen);
						Position[] camposhrefraction = camfwdvsurfhendpos2;
						camposhrefraction = translate(camposhrefraction,camfwdhenddir12invn[0],refhdir12len);
						camposhrefraction = translate(camposhrefraction,vsurfnormal[0],refhnormlen);
						Direction[] camposvhrefractiondir = vectorFromPoints(camposvrefraction, camposhrefraction);
						Position[] camposrefraction = translate(camposvrefraction,camposvhrefractiondir[0],0.5f);
						Position[] refcamfwdhalfvendpos = translate(camfwdvsurfvendpos2,camfwdvenddir12invn[0],reffwd12halfvanglelen);
						Position[] refcamfwdhalfhendpos = translate(camfwdvsurfhendpos2,camfwdhenddir12invn[0],reffwd12halfhanglelen);
						Direction[] refcamfwdhalfhvendposdir = vectorFromPoints(refcamfwdhalfvendpos, refcamfwdhalfhendpos);
						Position[] refcamfwdhalfendpos = translate(refcamfwdhalfvendpos,refcamfwdhalfhvendposdir[0],0.5f);
						Direction[] refcamfwdhalfvendposdir = vectorFromPoints(camposrefraction, refcamfwdhalfendpos);
						Direction[] refcamfwdhalfvendposdirn = normalizeVector(refcamfwdhalfvendposdir);
						Position[] refcamfwddirendpos = translate(camposa,refcamfwdhalfvendposdirn[0],1.0f);
						double[][] refcamposcamfwdplanedist = planePointDistance(refcamfwddirendpos, camfwdplane);
						double[][] refcamposcamrgtplanedist = planePointDistance(refcamfwddirendpos, camrgtplane);
						double[][] refcamposcamupplanedist = planePointDistance(refcamfwddirendpos, camupplane);
						Direction[] refcamposcamfwdrgtpos = {new Direction(refcamposcamrgtplanedist[0][0],-refcamposcamfwdplanedist[0][0],0.0f)};
						Direction[] refcamposcamfwduppos = {new Direction(0.0f,-refcamposcamfwdplanedist[0][0],refcamposcamupplanedist[0][0])};
						double[] camrefcamvangle = vectorAngle(camzerofwddir, refcamposcamfwduppos);
						double[] camrefcamhangle = vectorAngle(camzerofwddir, refcamposcamfwdrgtpos);
						double refcamvangle = ((refcamposcamupplanedist[0][0]>0.0f)?-1.0f:1.0f)*camrefcamvangle[0];
						double refcamhangle = ((refcamposcamrgtplanedist[0][0]>0.0f)?1.0f:-1.0f)*camrefcamhangle[0];
						Matrix vrot = rotationMatrixAroundAxis(camrgtdir[0], refcamvangle);
						Matrix hrot = rotationMatrixAroundAxis(camupdir[0], refcamhangle);
						Matrix viewrotrefraction = viewrot;
						viewrotrefraction = matrixMultiply(hrot, viewrotrefraction);
						viewrotrefraction = matrixMultiply(vrot, viewrotrefraction);
						k[i] = new RenderView();
						k[i].rot = viewrotrefraction;
						k[i].pos = camposrefraction[0];
						k[i].hfov = refcamhfov;
						k[i].vfov = refcamvfov;
					}
				}
			}
		}
		return k;
	}
	
}
