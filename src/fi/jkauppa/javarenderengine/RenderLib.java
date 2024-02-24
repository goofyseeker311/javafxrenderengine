package fi.jkauppa.javarenderengine;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.Transparency;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import fi.jkauppa.javarenderengine.ModelLib.Coordinate;
import fi.jkauppa.javarenderengine.ModelLib.Cubemap;
import fi.jkauppa.javarenderengine.ModelLib.Direction;
import fi.jkauppa.javarenderengine.ModelLib.Entity;
import fi.jkauppa.javarenderengine.ModelLib.Line;
import fi.jkauppa.javarenderengine.ModelLib.Material;
import fi.jkauppa.javarenderengine.ModelLib.Matrix;
import fi.jkauppa.javarenderengine.ModelLib.Plane;
import fi.jkauppa.javarenderengine.ModelLib.PlaneRay;
import fi.jkauppa.javarenderengine.ModelLib.Position;
import fi.jkauppa.javarenderengine.ModelLib.Ray;
import fi.jkauppa.javarenderengine.ModelLib.RenderView;
import fi.jkauppa.javarenderengine.ModelLib.Sphere;
import fi.jkauppa.javarenderengine.ModelLib.Triangle;
import fi.jkauppa.javarenderengine.ModelLib.SphereComparator;

public class RenderLib {
	private static GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	private static GraphicsDevice gd = ge.getDefaultScreenDevice();
	private static GraphicsConfiguration gc = gd.getDefaultConfiguration();

	public static RenderView renderProjectedLineViewHardware(Position campos, Line[] linelist, int renderwidth, double hfov, int renderheight, double vfov, boolean drawgrid, Matrix viewrot, int mouselocationx, int mouselocationy) {
		int originlinelength = 100;
		int vertexradius = 2;
		int axisstroke = 2;
		int vertexstroke = 2;
		int vertexfocus = 3;
		int sketchlinestroke = 2;
		double pointdist = 1.0f;
		int gridstep = 20;
		RenderView renderview = new RenderView();
		renderview.mouselocationx = mouselocationx;
		renderview.mouselocationy = mouselocationy;
		renderview.pos = campos.copy();
		renderview.rot = viewrot.copy();
		renderview.renderwidth = renderwidth;
		renderview.renderheight = renderheight;
		renderview.hfov = hfov;
		renderview.vfov = MathLib.calculateVfov(renderview.renderwidth, renderview.renderheight, renderview.hfov);
		renderview.mouselocationx = mouselocationx; 
		renderview.mouselocationy = mouselocationy; 
		renderview.dirs = MathLib.projectedCameraDirections(viewrot);
		renderview.renderimage = gc.createCompatibleImage(renderwidth, renderheight, Transparency.TRANSLUCENT);
		renderview.zbuffer = new double[renderheight][renderwidth];
		for (int i=0;i<renderview.zbuffer.length;i++) {Arrays.fill(renderview.zbuffer[i],Double.POSITIVE_INFINITY);}
		double editplanedistance = 1371.022f;
		int origindeltax = (int)Math.floor(((double)(renderwidth-1))/2.0f);
		int origindeltay = (int)Math.floor(((double)(renderheight-1))/2.0f);
		Graphics2D g2 = renderview.renderimage.createGraphics();
		g2.setComposite(AlphaComposite.Src);
		g2.setColor(new Color(0.0f,0.0f,0.0f,0.0f));
		g2.setPaint(null);
		g2.setClip(null);
		g2.fillRect(0, 0, renderwidth, renderheight);
		if (drawgrid) {
			g2.setComposite(AlphaComposite.SrcOver);
			BufferedImage bgpatternimage = gc.createCompatibleImage(gridstep, gridstep, Transparency.OPAQUE);
			TexturePaint bgpattern = null;
			Graphics2D pgfx = bgpatternimage.createGraphics();
			pgfx.setColor(Color.WHITE);
			pgfx.fillRect(0, 0, bgpatternimage.getWidth(), bgpatternimage.getHeight());
			pgfx.setColor(Color.BLACK);
			pgfx.drawLine(0, 0, 0, gridstep-1);
			pgfx.drawLine(0, 0, gridstep-1, 0);
			pgfx.dispose();
			bgpattern = new TexturePaint(bgpatternimage,new Rectangle(origindeltax, origindeltay, gridstep, gridstep));
			g2.setComposite(AlphaComposite.Src);
			g2.setColor(null);
			g2.setPaint(bgpattern);
			g2.fillRect(0, 0, renderwidth, renderheight);
			g2.setPaint(null);
		}
		g2.setComposite(AlphaComposite.SrcOver);
		ArrayList<Position> mouseoverhitvertex = new ArrayList<Position>(); 
		ArrayList<Line> mouseoverhitline = new ArrayList<Line>();
		Coordinate[][] linelistcoords = MathLib.projectedLine(renderview.pos, linelist, renderwidth, renderview.hfov, renderheight, renderview.vfov, viewrot, null);
		Position[] camposarray = {renderview.pos};
		Position[] editposarray = MathLib.translate(camposarray, renderview.dirs[0], editplanedistance);
		Plane[] editplanes = MathLib.planeFromNormalAtPoint(editposarray[0], renderview.dirs);
		Plane[] editplane = {editplanes[0]};
		for (int i=0;i<linelist.length;i++) {
			Position[] linepoints = {linelist[i].pos1, linelist[i].pos2};
			double[][] linepointdists = MathLib.planePointDistance(linepoints, editplane);
			if ((linepointdists[0][0]>=0)||(linepointdists[1][0]>=0)) {
				Coordinate coord1 = linelistcoords[i][0];
				Coordinate coord2 = linelistcoords[i][1];
				if ((coord1!=null)&&(coord2!=null)) {
					g2.setColor(Color.BLACK);
					if (Math.abs(linepointdists[0][0])<pointdist){g2.setStroke(new BasicStroke(vertexstroke+vertexfocus));}else{g2.setStroke(new BasicStroke(vertexstroke));}
					g2.drawOval((int)Math.round(coord1.u)-vertexradius, (int)Math.round(coord1.v)-vertexradius, vertexradius*2, vertexradius*2);
					if (Math.abs(linepointdists[1][0])<pointdist){g2.setStroke(new BasicStroke(vertexstroke+vertexfocus));}else{g2.setStroke(new BasicStroke(vertexstroke));}
					g2.drawOval((int)Math.round(coord2.u)-vertexradius, (int)Math.round(coord2.v)-vertexradius, vertexradius*2, vertexradius*2);
					g2.setStroke(new BasicStroke(sketchlinestroke));
					g2.setColor(Color.BLUE);
					g2.drawLine((int)Math.round(coord1.u), (int)Math.round(coord1.v), (int)Math.round(coord2.u), (int)Math.round(coord2.v));
					boolean mouseoverhit1 = g2.hit(new Rectangle(mouselocationx-vertexradius,mouselocationy-vertexradius,3,3), new Rectangle((int)Math.round(coord1.u)-vertexradius,(int)Math.round(coord1.v)-vertexradius,3,3), false);
					boolean mouseoverhit2 = g2.hit(new Rectangle(mouselocationx-vertexradius,mouselocationy-vertexradius,3,3), new Rectangle((int)Math.round(coord2.u)-vertexradius,(int)Math.round(coord2.v)-vertexradius,3,3), false);
					boolean mouseoverhitL = g2.hit(new Rectangle(mouselocationx-vertexradius,mouselocationy-vertexradius,3,3), new Line2D.Double((int)Math.round(coord1.u),(int)Math.round(coord1.v),(int)Math.round(coord2.u),(int)Math.round(coord2.v)), false);
					if (mouseoverhit1) {
						mouseoverhitvertex.add(linelist[i].pos1);
					}
					if (mouseoverhit2) {
						mouseoverhitvertex.add(linelist[i].pos2);
					}
					if (mouseoverhitL) {
						mouseoverhitline.add(linelist[i]);
					}
				}
			}
		}
		Position[] originpoints = {new Position(0.0f,0.0f,0.0f),new Position(originlinelength,0.0f,0.0f),new Position(0.0f,originlinelength,0.0f),new Position(0.0f,0.0f,originlinelength)}; 
		Coordinate[] originpointscoords = MathLib.projectedPoint(renderview.pos, originpoints, renderwidth, renderview.hfov, renderheight, renderview.vfov, viewrot, null);
		Coordinate coord1 = originpointscoords[0];
		Coordinate coord2 = originpointscoords[1];
		Coordinate coord3 = originpointscoords[2];
		Coordinate coord4 = originpointscoords[3];
		if ((coord1!=null)&&(coord2!=null)&&(coord3!=null)&&(coord4!=null)) {
			g2.setStroke(new BasicStroke(axisstroke));
			g2.setColor(Color.RED);
			g2.drawLine((int)Math.round(coord1.u), (int)Math.round(coord1.v), (int)Math.round(coord2.u), (int)Math.round(coord2.v));
			g2.setColor(Color.GREEN);
			g2.drawLine((int)Math.round(coord1.u), (int)Math.round(coord1.v), (int)Math.round(coord3.u), (int)Math.round(coord3.v));
			g2.setColor(Color.BLUE);
			g2.drawLine((int)Math.round(coord1.u), (int)Math.round(coord1.v), (int)Math.round(coord4.u), (int)Math.round(coord4.v));
			g2.setColor(Color.BLACK);
			g2.fillOval((int)Math.round(coord1.u)-vertexradius, (int)Math.round(coord1.v)-vertexradius, vertexradius*2, vertexradius*2);
		}
		renderview.mouseoververtex = mouseoverhitvertex.toArray(new Position[mouseoverhitvertex.size()]);
		renderview.mouseoverline = mouseoverhitline.toArray(new Line[mouseoverhitline.size()]);
		return renderview;
	}

	public static RenderView renderProjectedPolygonViewHardware(Position campos, Entity[] entitylist, int renderwidth, double hfov, int renderheight, double vfov, Matrix viewrot, boolean unlit, int bounces, Plane nclipplane, Triangle nodrawtriangle, Rectangle drawrange, int mouselocationx, int mouselocationy) {
		RenderView renderview = new RenderView();
		renderview.pos = campos.copy();
		renderview.rot = viewrot.copy();
		renderview.renderwidth = renderwidth;
		renderview.renderheight = renderheight;
		renderview.hfov = hfov;
		renderview.vfov = MathLib.calculateVfov(renderview.renderwidth, renderview.renderheight, renderview.hfov);
		renderview.unlit = unlit;
		renderview.mouselocationx = mouselocationx;
		renderview.mouselocationy = mouselocationy;
		renderview.dirs = MathLib.projectedCameraDirections(renderview.rot);
		renderview.renderimage = gc.createCompatibleImage(renderwidth, renderheight, Transparency.TRANSLUCENT);
		int vertexradius = 2;
		Graphics2D g2 = renderview.renderimage.createGraphics();
		g2.setComposite(AlphaComposite.Src);
		g2.setColor(new Color(0.0f,0.0f,0.0f,0.0f));
		g2.setPaint(null);
		g2.setClip(null);
		g2.fillRect(0, 0, renderwidth, renderheight);
		g2.setComposite(AlphaComposite.SrcOver);
		ArrayList<Entity> mouseoverhitentity = new ArrayList<Entity>();
		ArrayList<Triangle> mouseoverhittriangle = new ArrayList<Triangle>();
		if ((entitylist!=null)&&(entitylist.length>0)) {
			Sphere[] entityspherelist = MathLib.entitySphereList(entitylist);
			SphereComparator distcomp = new SphereComparator(renderview.pos);
			Integer[] sortedentityspherelistind = UtilLib.objectIndexSort(entityspherelist, distcomp);
			Rectangle[] entityspherelistint = MathLib.projectedSphereIntersection(renderview.pos, entityspherelist, renderwidth, renderheight, renderview.hfov, renderview.vfov, viewrot, nclipplane);
			for (int k=sortedentityspherelistind.length-1;k>=0;k--) {
				int et = sortedentityspherelistind[k];
				if ((entityspherelistint[et]!=null)&&((drawrange==null)||(drawrange.intersects(entityspherelistint[et])))) {
					Triangle[] copytrianglelist = entitylist[et].trianglelist;
					if (copytrianglelist.length>0) {
						Direction[] copytrianglenormallist = MathLib.triangleNormal(copytrianglelist);
						Plane[] copytriangleplanelist = MathLib.trianglePlane(copytrianglelist);
						Sphere[] copytrianglespherelist = MathLib.triangleCircumSphere(copytrianglelist);
						Integer[] sortedtrianglespherelistind = UtilLib.objectIndexSort(copytrianglespherelist, distcomp);
						Direction[] copyviewtrianglespheredir = MathLib.vectorFromPoints(campos, copytrianglespherelist);
						Coordinate[][] copytrianglelistcoords = MathLib.projectedTriangle(renderview.pos, copytrianglelist, renderwidth, renderview.hfov, renderheight, renderview.vfov, viewrot, nclipplane);
						for (int i=sortedtrianglespherelistind.length-1;i>=0;i--) {
							int it = sortedtrianglespherelistind[i];
							Triangle[] copytriangle = {copytrianglelist[it]};
							if (!copytriangle[0].equals(nodrawtriangle)) {
								Direction[] copytrianglenormal = {copytrianglenormallist[it]};
								Plane[] copytriangleplane = {copytriangleplanelist[it]};
								Direction copytriangledir = copyviewtrianglespheredir[it];
								Coordinate coord1 = copytrianglelistcoords[it][0];
								Coordinate coord2 = copytrianglelistcoords[it][1];
								Coordinate coord3 = copytrianglelistcoords[it][2];
								Coordinate coord4 = copytrianglelistcoords[it][3];
								Coordinate coord5 = copytrianglelistcoords[it][4];
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
									boolean mouseoverhit = g2.hit(new Rectangle(mouselocationx-vertexradius,mouselocationy-vertexradius,3,3), trianglepolygon, false);
									if (mouseoverhit) {
										mouseoverhittriangle.add(copytrianglelist[it]);
										mouseoverhitentity.add(entitylist[et]);
									}
									Color trianglecolor = trianglePixelShader(copytriangle[0], copytrianglenormal[0], null, copytriangledir, unlit);
									if (trianglecolor!=null) {
										if ((bounces<=0)||(copytriangle[0].norm.isZero())) {
											g2.setComposite(AlphaComposite.SrcOver);
										} else {
											g2.setComposite(AlphaComposite.Src);
										}
										g2.setColor(trianglecolor);
										g2.fill(trianglepolygon);
										if (bounces>0) {
											double[] camfwddirposnormangle = MathLib.vectorAngle(copytriangledir, copytrianglenormal);
											float refrindex1 = 1.0f;
											float refrindex2 = copytriangle[0].mat.refraction;
											Plane[] vsurf = copytriangleplane;
											if (camfwddirposnormangle[0]<90.0f) {
												Plane[] newvsurf = {vsurf[0].invert()};
												vsurf = newvsurf;
												refrindex1 = copytriangle[0].mat.refraction;
												refrindex2 = 1.0f;
											}
											if ((copytriangle[0].mat.transparency<1.0f)&&(!copytriangle[0].norm.isZero())) {
												RenderView[] refractioncamera = MathLib.surfaceRefractionProjectedCamera(campos, vsurf, renderwidth, hfov, renderheight, vfov, viewrot, refrindex1, refrindex2);
												if ((refractioncamera[0]!=null)&&(refractioncamera[0].pos.isFinite())) {
													Rectangle refractiondrawrange = trianglepolygon.getBounds();
													RenderView refractionview = renderProjectedPolygonViewHardware(refractioncamera[0].pos, entitylist, renderwidth, refractioncamera[0].hfov, renderheight, refractioncamera[0].vfov, refractioncamera[0].rot, unlit, bounces-1, vsurf[0].invert(), copytriangle[0], refractiondrawrange, mouselocationx, mouselocationy);
													BufferedImage refractionimage = refractionview.renderimage;
													g2.setClip(null);
													g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, copytriangle[0].mat.transparency));
													g2.clip(trianglepolygon);
													g2.drawImage(refractionimage, 0, 0, null);
													g2.setClip(null);
												}
											}
											if (copytriangle[0].mat.metallic>0.0f) {
												RenderView[] mirrorcamera = MathLib.surfaceMirrorProjectedCamera(campos, vsurf, hfov, vfov, viewrot);
												if ((mirrorcamera[0]!=null)&&(mirrorcamera[0].pos.isFinite())) {
													Rectangle mirrordrawrange = trianglepolygon.getBounds();
													mirrordrawrange.setLocation((renderwidth-1)-mirrordrawrange.x-mirrordrawrange.width, mirrordrawrange.y);
													RenderView mirrorview = renderProjectedPolygonViewHardware(mirrorcamera[0].pos, entitylist, renderwidth, mirrorcamera[0].hfov, renderheight, mirrorcamera[0].vfov, mirrorcamera[0].rot, unlit, bounces-1, vsurf[0], copytriangle[0], mirrordrawrange, mouselocationx, mouselocationy);
													BufferedImage mirrorimage = UtilLib.flipImage(mirrorview.renderimage, true, false);
													g2.setClip(null);
													g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, copytriangle[0].mat.metallic));
													g2.clip(trianglepolygon);
													g2.drawImage(mirrorimage, 0, 0, null);
													g2.setClip(null);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		renderview.mouseoverentity = mouseoverhitentity.toArray(new Entity[mouseoverhitentity.size()]);
		renderview.mouseovertriangle = mouseoverhittriangle.toArray(new Triangle[mouseoverhittriangle.size()]);
		return renderview;
	}
	
	public static RenderView renderPlaneViewSoftware(Position campos, Entity[] entitylist, int renderwidth, double hfov, int renderheight, double vfov, Matrix viewrot, boolean spherical, boolean unlit, int bounces, Plane nclipplane, Triangle nodrawtriangle, Rectangle drawrange, int mouselocationx, int mouselocationy) {
		RenderView renderview = new RenderView();
		renderview.pos = campos.copy();
		renderview.rot = viewrot.copy();
		renderview.renderwidth = renderwidth;
		renderview.renderheight = renderheight;
		renderview.unlit = unlit;
		renderview.mouselocationx = mouselocationx;
		renderview.mouselocationy = mouselocationy;
		if (spherical) {
			renderview.hfov = 360.0f;
			renderview.vfov = 180.0f;
			renderview.planerays = MathLib.spheremapPlaneRays(renderview.pos, renderwidth, renderheight, renderview.rot);
		} else {
			renderview.hfov = hfov;
			renderview.vfov = MathLib.calculateVfov(renderview.renderwidth, renderview.renderheight, renderview.hfov);
			renderview.planerays = MathLib.projectedPlaneRays(renderview.pos, renderwidth, renderheight, renderview.hfov, renderview.vfov, renderview.rot);
		}
		renderview.dirs = MathLib.projectedCameraDirections(renderview.rot);
		renderview.renderimage = gc.createCompatibleImage(renderwidth, renderheight, Transparency.TRANSLUCENT);
		renderview.zbuffer = new double[renderheight][renderwidth];
		renderview.tbuffer = new Triangle[renderheight][renderwidth];
		renderview.cbuffer = new Coordinate[renderheight][renderwidth];
		for (int i=0;i<renderview.zbuffer.length;i++) {Arrays.fill(renderview.zbuffer[i],Double.POSITIVE_INFINITY);}
		Graphics2D g2 = renderview.renderimage.createGraphics();
		g2.setComposite(AlphaComposite.Src);
		g2.setColor(new Color(0.0f,0.0f,0.0f,0.0f));
		g2.setPaint(null);
		g2.setClip(null);
		g2.fillRect(0, 0, renderwidth, renderheight);
		g2.setComposite(AlphaComposite.SrcOver);
		ArrayList<Entity> mouseoverhitentity = new ArrayList<Entity>();
		ArrayList<Triangle> mouseoverhittriangle = new ArrayList<Triangle>();
		if ((entitylist!=null)&&(entitylist.length>0)) {
			double origindeltay = ((double)(renderheight-1))/2.0f;
			double halfvres = ((double)(renderheight-1))/2.0f;
			Sphere[] entityspherelist = MathLib.entitySphereList(entitylist);
			SphereComparator distcomp = new SphereComparator(renderview.pos);
			Integer[] sortedentityspherelistind = UtilLib.objectIndexSort(entityspherelist, distcomp);
			Rectangle[] entityspherelistint = null;
			if (spherical)  {
				entityspherelistint = MathLib.spheremapSphereIntersection(renderview.pos, entityspherelist, renderwidth, renderheight, viewrot, nclipplane);
			} else {
				entityspherelistint = MathLib.projectedSphereIntersection(renderview.pos, entityspherelist, renderwidth, renderheight, renderview.hfov, renderview.vfov, viewrot, nclipplane);
			}
			for (int k=sortedentityspherelistind.length-1;k>=0;k--) {
				int et = sortedentityspherelistind[k];
				if ((entityspherelistint[et]!=null)&&((drawrange==null)||(drawrange.intersects(entityspherelistint[et])))) {
					Triangle[] copytrianglelist = entitylist[et].trianglelist;
					if (copytrianglelist.length>0) {
						Direction[] copytrianglenormallist = MathLib.triangleNormal(copytrianglelist);
						Sphere[] copytrianglespherelist = MathLib.triangleCircumSphere(copytrianglelist);
						Plane[] copytriangleplanelist = MathLib.trianglePlane(copytrianglelist);
						Direction[] copyviewtrianglespheredir = MathLib.vectorFromPoints(campos, copytrianglespherelist);
						Integer[] sortedtrianglespherelistind = UtilLib.objectIndexSort(copytrianglespherelist, distcomp);
						Rectangle[] copytrianglelistint = null;
						if (spherical) {
							copytrianglelistint = MathLib.spheremapTriangleIntersection(renderview.pos, copytrianglelist, renderwidth, renderheight, viewrot, nclipplane);
						} else {
							copytrianglelistint = MathLib.projectedTriangleIntersection(renderview.pos, copytrianglelist, renderwidth, renderheight, renderview.hfov, renderview.vfov, viewrot, nclipplane);
						}
						for (int i=sortedtrianglespherelistind.length-1;i>=0;i--) {
							int it = sortedtrianglespherelistind[i];
							if (copytrianglelistint[it]!=null) {
								Entity[] copyentity = {entitylist[et]};
								Triangle[] copytriangle = {copytrianglelist[it]};
								Direction[] copytrianglenormal = {copytrianglenormallist[it]};
								Plane[] copytriangleplane = {copytriangleplanelist[it]};
								Direction copytriangledir = copyviewtrianglespheredir[it];
								int jstart = copytrianglelistint[it].x;
								int jend = copytrianglelistint[it].x+copytrianglelistint[it].width-1;
								for (int j=jstart;((jstart<=jend)&&(j<=jend))||((jstart>jend)&&((j>=jstart)||(j<=jend)));j++) {
									if (j>=renderwidth) {j=0;}
									PlaneRay[] camplaneray = {renderview.planerays[j]};
									Position[] camposa = {camplaneray[0].pos};
									Direction[] camfwddir = {camplaneray[0].dir};
									Plane[] camplane = {camplaneray[0].plane};
									double[] camvfov = {camplaneray[0].vfov};
									Direction[] camrgtdir = MathLib.planeNormal(camplane);
									Direction[] camupdir = MathLib.vectorCross(camfwddir, camrgtdir);
									Plane[] camfwdplane = MathLib.planeFromNormalAtPoint(camposa[0], camfwddir);
									Plane[] camupplane = MathLib.planeFromNormalAtPoint(camposa[0], camupdir);
									Plane[] rendercutplane = MathLib.translate(camfwdplane, camfwddir[0], 1.1d);
									double[] verticalangles = null;
									double halfvfovmult = 0.0f;
									if (spherical) {
										verticalangles = MathLib.spheremapAngles(renderheight, camvfov[0]);
										halfvfovmult = (1.0f/(camvfov[0]/2.0f));
									} else {
										verticalangles = MathLib.projectedAngles(renderheight, camvfov[0]);
										halfvfovmult = (1.0f/MathLib.tand(camvfov[0]/2.0f));
									}
									Line[][] vertplanetriangleint = MathLib.planeTriangleIntersection(camplane, copytriangle);
									Line drawline = vertplanetriangleint[0][0];
									if (drawline!=null) {
										Position[] drawlinepoints = {drawline.pos1, drawline.pos2};
										double[][] fwdintpointsdist = MathLib.planePointDistance(drawlinepoints, camfwdplane);
										double[][] upintpointsdist = MathLib.planePointDistance(drawlinepoints, camupplane);
										if ((fwdintpointsdist[0][0]>=1.0f)||(fwdintpointsdist[1][0]>=1.0f)) {
											if (!((fwdintpointsdist[0][0]>=1.0f)&&(fwdintpointsdist[1][0]>=1.0f))) {
												Position[] drawlinepos1 = {drawline.pos1};
												Position[] drawlinepos2 = {drawline.pos2};
												Direction[] drawlinedir12 = MathLib.vectorFromPoints(drawlinepos1, drawlinepos2);
												double[][] drawlinedir12dist = MathLib.rayPlaneDistance(drawlinepos1[0], drawlinedir12, rendercutplane);
												Position[] drawlinepos3 = MathLib.translate(drawlinepos1, drawlinedir12[0], drawlinedir12dist[0][0]);
												Coordinate tex1 = drawlinepos1[0].tex; 
												Coordinate tex2 = drawlinepos2[0].tex; 
												if ((tex1!=null)&&(tex2!=null)) {
													Position[] drawlinepostex1 = {new Position(tex1.u,tex1.v,0.0f)};
													Position[] drawlinepostex2 = {new Position(tex2.u,tex2.v,0.0f)};
													Direction[] drawlinetexdir12 = MathLib.vectorFromPoints(drawlinepostex1, drawlinepostex2);
													Position[] drawlinepostex3 = MathLib.translate(drawlinepostex1, drawlinetexdir12[0], drawlinedir12dist[0][0]);
													drawlinepos3[0].tex = new Coordinate(drawlinepostex3[0].x, drawlinepostex3[0].y);
												}
												if (fwdintpointsdist[0][0]>=1.0f) {
													Position[] newdrawlinepoints = {drawlinepos1[0], drawlinepos3[0]};
													drawlinepoints = newdrawlinepoints;
												} else {
													Position[] newdrawlinepoints = {drawlinepos2[0], drawlinepos3[0]};
													drawlinepoints = newdrawlinepoints;
												}
												fwdintpointsdist = MathLib.planePointDistance(drawlinepoints, camfwdplane);
												upintpointsdist = MathLib.planePointDistance(drawlinepoints, camupplane);
											}
											double vpixelyang1 = MathLib.atand(upintpointsdist[0][0]/fwdintpointsdist[0][0]);
											double vpixelyang2 = MathLib.atand(upintpointsdist[1][0]/fwdintpointsdist[1][0]);
											double vpixely1 = 0.0f;
											double vpixely2 = 0.0f;
											if (spherical) {
												vpixely1 = halfvres*halfvfovmult*vpixelyang1+origindeltay;
												vpixely2 = halfvres*halfvfovmult*vpixelyang2+origindeltay;
											} else {
												vpixely1 = halfvfovmult*halfvres*(upintpointsdist[0][0]/fwdintpointsdist[0][0])+origindeltay;
												vpixely2 = halfvfovmult*halfvres*(upintpointsdist[1][0]/fwdintpointsdist[1][0])+origindeltay;
											}
											double[] vpixelys = {vpixely1, vpixely2};
											double[] vpixelyangs = {vpixelyang1, vpixelyang2};
											int[] vpixelyinds = UtilLib.indexSort(vpixelys);
											double[] vpixelysort = UtilLib.indexValues(vpixelys, vpixelyinds);
											Position[] vpixelpoints = {drawlinepoints[vpixelyinds[0]], drawlinepoints[vpixelyinds[1]]};
											Position[] vpixelpoint1 = {vpixelpoints[0]};
											Position[] vpixelpoint2 = {vpixelpoints[1]};
											Position[] vcamposd = {new Position(0.0f,0.0f,0.0f)};
											Position[] vpixelpoint1d = {new Position(fwdintpointsdist[vpixelyinds[0]][0],upintpointsdist[vpixelyinds[0]][0],0.0f)};
											Position[] vpixelpoint2d = {new Position(fwdintpointsdist[vpixelyinds[1]][0],upintpointsdist[vpixelyinds[1]][0],0.0f)};
											Line[] vpixelline = {new Line(vpixelpoint1d[0], vpixelpoint2d[0])};
											double vpixelyangsort1 = vpixelyangs[vpixelyinds[0]]; 
											int vpixelyind1 = (int)Math.ceil(vpixelysort[0]); 
											int vpixelyind2 = (int)Math.floor(vpixelysort[1]);
											int vpixelystart = vpixelyind1;
											int vpixelyend = vpixelyind2;
											Direction[] vpixelpointdir12 = MathLib.vectorFromPoints(vpixelpoint1, vpixelpoint2);
											if ((vpixelyend>=0)&&(vpixelystart<=renderheight)) {
												if (vpixelystart<0) {vpixelystart=0;}
												if (vpixelyend>=renderheight) {vpixelyend=renderheight-1;}
												for (int n=vpixelystart;n<=vpixelyend;n++) {
													double[] vpixelcampointangle = {verticalangles[n]-vpixelyangsort1};
													double[][] vpixelpointlenfraca = MathLib.linearAngleLengthInterpolation(vcamposd[0], vpixelline, vpixelcampointangle);
													double vpixelpointlenfrac = vpixelpointlenfraca[0][0];
													Position[] linepoint = MathLib.translate(vpixelpoint1, vpixelpointdir12[0], vpixelpointlenfrac);
													Direction[] linepointdir = MathLib.vectorFromPoints(renderview.pos, linepoint);
													double[] linepointdirlen = MathLib.vectorLength(linepointdir);
													Direction[] camray = linepointdir;
													double drawdistance = linepointdirlen[0];
													double[][] linepointdist = MathLib.planePointDistance(linepoint, camfwdplane);
													if ((linepointdist[0][0]>=1.0f)&&(drawdistance<renderview.zbuffer[n][j])) {
														renderview.zbuffer[n][j] = drawdistance;
														renderview.tbuffer[n][j] = copytriangle[0];
														Coordinate tex1 = vpixelpoints[0].tex;
														Coordinate tex2 = vpixelpoints[1].tex;
														Coordinate lineuv = null;
														if ((tex1!=null)&&(tex2!=null)) {
															Position[] lineuvpoint1 = {new Position(tex1.u,1.0f-tex1.v,0.0f)};
															Position[] lineuvpoint2 = {new Position(tex2.u,1.0f-tex2.v,0.0f)};
															Direction[] vpixelpointdir12uv = MathLib.vectorFromPoints(lineuvpoint1, lineuvpoint2);
															Position[] lineuvpos = MathLib.translate(lineuvpoint1, vpixelpointdir12uv[0], vpixelpointlenfrac);
															lineuv = new Coordinate(lineuvpos[0].x, lineuvpos[0].y);
															renderview.cbuffer[n][j] = lineuv;
														}
														if ((mouselocationx==j)&&(mouselocationy==n)) {
															mouseoverhittriangle.add(copytriangle[0]);
															mouseoverhitentity.add(copyentity[0]);
														}
														Color trianglecolor = trianglePixelShader(copytriangle[0], copytrianglenormal[0], lineuv, camray[0], unlit);
														if (trianglecolor!=null) {
															if ((bounces<=0)||(copytriangle[0].norm.isZero())) {
																g2.setComposite(AlphaComposite.SrcOver);
															} else {
																g2.setComposite(AlphaComposite.Src);
															}
															g2.setColor(trianglecolor);
															g2.drawLine(j, n, j, n);
														}
													}
												}
												if (bounces>0) {
													double[] camfwddirposnormangle = MathLib.vectorAngle(copytriangledir, copytrianglenormal);
													float refrindex1 = 1.0f;
													float refrindex2 = copytriangle[0].mat.refraction;
													Plane[] vsurf = copytriangleplane;
													if (camfwddirposnormangle[0]<90.0f) {
														Plane[] newvsurf = {vsurf[0].invert()};
														vsurf = newvsurf;
														refrindex1 = copytriangle[0].mat.refraction;
														refrindex2 = 1.0f;
													}
													if ((copytriangle[0].mat.transparency<1.0f)&&(!copytriangle[0].norm.isZero())) {
														PlaneRay[][] refractionplaneraya = MathLib.surfaceRefractionPlaneRay(camplaneray, vsurf, refrindex1, refrindex2);
														if (refractionplaneraya!=null) {
															PlaneRay[] refractionplaneray = {refractionplaneraya[0][0]};
															if ((refractionplaneray!=null)&&(refractionplaneray[0]!=null)) {
																BufferedImage[] refractionrendercolumn = renderPlaneRay(refractionplaneray, entitylist, renderheight, spherical, unlit, bounces-1, vsurf[0].invert(), copytriangle[0]);
																if ((refractionrendercolumn!=null)&&(refractionrendercolumn[0]!=null)) {
																	g2.setClip(null);
																	g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, copytriangle[0].mat.transparency));
																	g2.clip(new Rectangle(j,vpixelystart,1,vpixelyend-vpixelystart+1));
																	g2.drawImage(refractionrendercolumn[0], j, 0, null);
																	g2.setClip(null);
																}
															}
														}
													}
													if (copytriangle[0].mat.metallic>0.0f) {
														PlaneRay[][] mirrorplaneraya = MathLib.surfaceMirrorPlaneRay(camplaneray, vsurf);
														if (mirrorplaneraya!=null) {
															PlaneRay[] mirrorplaneray = {mirrorplaneraya[0][0]};
															if ((mirrorplaneray!=null)&&(mirrorplaneray[0]!=null)) {
																BufferedImage[] mirrorrendercolumn = renderPlaneRay(mirrorplaneray, entitylist, renderheight, spherical, unlit, bounces-1, vsurf[0], copytriangle[0]);
																if ((mirrorrendercolumn!=null)&&(mirrorrendercolumn[0]!=null)) {
																	g2.setClip(null);
																	g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, copytriangle[0].mat.metallic));
																	g2.clip(new Rectangle(j,vpixelystart,1,vpixelyend-vpixelystart+1));
																	g2.drawImage(mirrorrendercolumn[0], j, 0, null);
																	g2.setClip(null);
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		renderview.mouseoverentity = mouseoverhitentity.toArray(new Entity[mouseoverhitentity.size()]);
		renderview.mouseovertriangle = mouseoverhittriangle.toArray(new Triangle[mouseoverhittriangle.size()]);
		return renderview;
	}

	public static RenderView renderRayViewSoftware(Position campos, Entity[] entitylist, int renderwidth, double hfov, int renderheight, double vfov, Matrix viewrot, boolean spherical, boolean unlit, int bounces, Plane nclipplane, Triangle nodrawtriangle, Rectangle drawrange, int mouselocationx, int mouselocationy) {
		RenderView renderview = new RenderView();
		renderview.pos = campos.copy();
		renderview.rot = viewrot.copy();
		renderview.renderwidth = renderwidth;
		renderview.renderheight = renderheight;
		renderview.hfov = hfov;
		renderview.vfov = MathLib.calculateVfov(renderview.renderwidth, renderview.renderheight, renderview.hfov);
		renderview.unlit = unlit;
		renderview.mouselocationx = mouselocationx; 
		renderview.mouselocationy = mouselocationy; 
		renderview.dirs = MathLib.projectedCameraDirections(renderview.rot);
		if (spherical) {
			renderview.rays = MathLib.spheremapRays(renderwidth, renderheight, renderview.rot);
			renderview.fwddirs = MathLib.spheremapVectors(renderwidth, renderview.rot);
		} else {
			renderview.rays = MathLib.projectedRays(renderwidth, renderheight, renderview.hfov, renderview.vfov, renderview.rot, true);
			Direction[] fwddir = {renderview.dirs[0]};
			renderview.fwddirs = fwddir;
		}
		renderview.renderimage = gc.createCompatibleImage(renderwidth, renderheight, Transparency.TRANSLUCENT);
		renderview.zbuffer = new double[renderheight][renderwidth];
		renderview.tbuffer = new Triangle[renderheight][renderwidth];
		renderview.cbuffer = new Coordinate[renderheight][renderwidth];
		for (int i=0;i<renderview.zbuffer.length;i++) {Arrays.fill(renderview.zbuffer[i],Double.POSITIVE_INFINITY);}
		Graphics2D g2 = renderview.renderimage.createGraphics();
		g2.setComposite(AlphaComposite.Src);
		g2.setColor(new Color(0.0f,0.0f,0.0f,0.0f));
		g2.setPaint(null);
		g2.setClip(null);
		g2.fillRect(0, 0, renderwidth, renderheight);
		g2.setComposite(AlphaComposite.SrcOver);
		ArrayList<Entity> mouseoverhitentity = new ArrayList<Entity>();
		ArrayList<Triangle> mouseoverhittriangle = new ArrayList<Triangle>();
		if ((entitylist!=null)&&(entitylist.length>0)) {
			Plane[] camfwdplanes = MathLib.planeFromNormalAtPoint(renderview.pos, renderview.fwddirs);
			Sphere[] entityspherelist = MathLib.entitySphereList(entitylist);
			SphereComparator distcomp = new SphereComparator(renderview.pos);
			Integer[] sortedentityspherelistind = UtilLib.objectIndexSort(entityspherelist, distcomp);
			Rectangle[] entityspherelistint = null;
			if (spherical) {
				entityspherelistint = MathLib.spheremapSphereIntersection(renderview.pos, entityspherelist, renderwidth, renderheight, viewrot, nclipplane);
			} else {
				entityspherelistint = MathLib.projectedSphereIntersection(renderview.pos, entityspherelist, renderwidth, renderheight, renderview.hfov, renderview.vfov, viewrot, nclipplane);
			}
			for (int k=sortedentityspherelistind.length-1;k>=0;k--) {
				int et = sortedentityspherelistind[k];
					if ((entityspherelistint[et]!=null)&&((drawrange==null)||(drawrange.intersects(entityspherelistint[et])))) {
					Triangle[] copytrianglelist = entitylist[et].trianglelist;
					if (copytrianglelist.length>0) {
						Direction[] copytrianglenormallist = MathLib.triangleNormal(copytrianglelist);
						Sphere[] copytrianglespherelist = MathLib.triangleCircumSphere(copytrianglelist);
						Plane[] copytriangleplanelist = MathLib.trianglePlane(copytrianglelist);
						Integer[] sortedtrianglespherelistind = UtilLib.objectIndexSort(copytrianglespherelist, distcomp);
						Rectangle[] copytrianglelistint = null;
						if (spherical) {
							copytrianglelistint = MathLib.spheremapTriangleIntersection(renderview.pos, copytrianglelist, renderwidth, renderheight, viewrot, nclipplane);
						} else {
							copytrianglelistint = MathLib.projectedTriangleIntersection(renderview.pos, copytrianglelist, renderwidth, renderheight, renderview.hfov, renderview.vfov, viewrot, nclipplane);
						}
						for (int n=sortedtrianglespherelistind.length-1;n>=0;n--) {
							int it = sortedtrianglespherelistind[n];
							if (copytrianglelistint[it]!=null) {
								Entity[] copyentity = {entitylist[et]};
								Triangle[] copytriangle = {copytrianglelist[it]};
								if (!copytriangle[0].equals(nodrawtriangle)) {
									Direction[] copytrianglenormal = {copytrianglenormallist[it]};
									Plane[] copytriangleplane = {copytriangleplanelist[it]};
									int jstart = 0;
									int jend = 0;
									if (spherical) {
										jstart = 0;
										jend = renderheight-1;
									} else {
										jstart = copytrianglelistint[it].y;
										jend = copytrianglelistint[it].y+copytrianglelistint[it].height-1;
									}
									int istart = copytrianglelistint[it].x;
									int iend = copytrianglelistint[it].x+copytrianglelistint[it].width-1;
									for (int j=jstart;j<=jend;j++) {
										for (int i=istart;((istart<=iend)&&(i<=iend))||((istart>iend)&&((i>=istart)||(i<=iend)));i++) {
											if (i>=renderwidth) {i=0;}
											Direction[] camray = {renderview.rays[j][i]};
											Ray[] ray = {new Ray(campos, camray[0])};
											Plane[] camfwdplane = null;
											if (spherical) {
												Plane[] newcamfwdplane = {camfwdplanes[i]};
												camfwdplane = newcamfwdplane;
											} else {
												camfwdplane = camfwdplanes;
											}
											Position[][] camrayint = MathLib.rayTriangleIntersection(renderview.pos, camray, copytriangle);
											Position[] camrayintpos = {camrayint[0][0]};
											if (camrayintpos[0]!=null) {
												Direction[] linepointdir = MathLib.vectorFromPoints(renderview.pos, camrayintpos);
												double[] linepointdirlen = MathLib.vectorLength(linepointdir);
												double drawdistance = linepointdirlen[0];
												double[][] camrayintposdist = MathLib.planePointDistance(camrayintpos, camfwdplane);
												if ((camrayintposdist[0][0]>=1.0f)&&(drawdistance<renderview.zbuffer[j][i])) {
													Coordinate tex = camrayint[0][0].tex;
													Coordinate pointuv = null;
													if (tex!=null) {
														pointuv = new Coordinate(tex.u,1.0f-tex.v);
														renderview.cbuffer[j][i] = pointuv;
													}
													renderview.zbuffer[j][i] = drawdistance;
													renderview.tbuffer[j][i] = copytriangle[0];
													if ((mouselocationx==i)&&(mouselocationy==j)) {
														mouseoverhittriangle.add(copytriangle[0]);
														mouseoverhitentity.add(copyentity[0]);
													}
													Color trianglecolor = trianglePixelShader(copytriangle[0], copytrianglenormal[0], pointuv, camray[0], unlit);
													if (trianglecolor!=null) {
														if ((bounces<=0)||(copytriangle[0].norm.isZero())) {
															g2.setComposite(AlphaComposite.SrcOver);
														} else {
															g2.setComposite(AlphaComposite.Src);
														}
														g2.setColor(trianglecolor);
														g2.drawLine(i, j, i, j);
														if (bounces>0) {
															double[] camfwddirposnormangle = MathLib.vectorAngle(camray[0], copytrianglenormal);
															float refrindex1 = 1.0f;
															float refrindex2 = copytriangle[0].mat.refraction;
															Plane[] vsurf = copytriangleplane;
															if (camfwddirposnormangle[0]<90.0f) {
																Plane[] newvsurf = {vsurf[0].invert()};
																vsurf = newvsurf;
																refrindex1 = copytriangle[0].mat.refraction;
																refrindex2 = 1.0f;
															}
															if ((copytriangle[0].mat.transparency<1.0f)&&(!copytriangle[0].norm.isZero())) {
																Ray[][] refractionraya = MathLib.surfaceRefractionRay(ray, vsurf, refrindex1, refrindex2);
																if (refractionraya!=null) {
																	Ray[] refractionray = {refractionraya[0][0]};
																	if ((refractionray!=null)&&(refractionray[0]!=null)) {
																		Color[] refractionraycolor = renderRay(refractionray, entitylist, unlit, bounces-1, copytriangle[0]);
																		if ((refractionraycolor!=null)&&(refractionraycolor[0]!=null)) {
																			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, copytriangle[0].mat.transparency));
																			g2.setColor(refractionraycolor[0]);
																			g2.drawLine(i, j, i, j);
																		}
																	}
																}
															}
															if (copytriangle[0].mat.metallic>0.0f) {
																Ray[][] mirrorraya = MathLib.surfaceMirrorRay(ray, vsurf);
																if (mirrorraya!=null) {
																	Ray[] mirrorray = {mirrorraya[0][0]};
																	if ((mirrorray!=null)&&(mirrorray[0]!=null)) {
																		Color[] mirrorraycolor = renderRay(mirrorray, entitylist, unlit, bounces-1, copytriangle[0]);
																		if ((mirrorraycolor!=null)&&(mirrorraycolor[0]!=null)) {
																			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, copytriangle[0].mat.metallic));
																			g2.setColor(mirrorraycolor[0]);
																			g2.drawLine(i, j, i, j);
																		}
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		renderview.mouseoverentity = mouseoverhitentity.toArray(new Entity[mouseoverhitentity.size()]);
		renderview.mouseovertriangle = mouseoverhittriangle.toArray(new Triangle[mouseoverhittriangle.size()]);
		return renderview;
	}

	public static RenderView renderProjectedView(Position campos, Entity[] entitylist, int renderwidth, double hfov, int renderheight, double vfov, Matrix viewrot, boolean unlit, int mode, int bounces, Plane nclipplane, Triangle nodrawtriangle, Rectangle drawrange, int mouselocationx, int mouselocationy) {
		RenderView projectedview = null;
		if (mode==2) {
			projectedview = renderPlaneViewSoftware(campos, entitylist, renderwidth, hfov, renderheight, vfov, viewrot, false, unlit, bounces, nclipplane, nodrawtriangle, drawrange, mouselocationx, mouselocationy);
		} else if (mode==3) {
			projectedview = renderRayViewSoftware(campos, entitylist, renderwidth, hfov, renderheight, vfov, viewrot, false, unlit, bounces, nclipplane, nodrawtriangle, drawrange, mouselocationx, mouselocationy);
		} else {
			projectedview = renderProjectedPolygonViewHardware(campos, entitylist, renderwidth, hfov, renderheight, vfov, viewrot, unlit, bounces, nclipplane, nodrawtriangle, drawrange, mouselocationx, mouselocationy);
		}
		return projectedview;
	}
	public static RenderView renderSpheremapView(Position campos, Entity[] entitylist, int renderwidth, int renderheight, Matrix viewrot, boolean unlit, int mode, int bounces, Plane nclipplane, Triangle nodrawtriangle, Rectangle drawrange, int mouselocationx, int mouselocationy) {
		RenderView spheremapview = null;
		if (mode==2) {
			spheremapview = renderRayViewSoftware(campos, entitylist, renderwidth, 360.0f, renderheight, 180.0f, viewrot, true, unlit, bounces, nclipplane, nodrawtriangle, drawrange, mouselocationx, mouselocationy);
		} else {
			spheremapview = renderPlaneViewSoftware(campos, entitylist, renderwidth, 360.0f, renderheight, 180.0f, viewrot, true, unlit, bounces, nclipplane, nodrawtriangle, drawrange, mouselocationx, mouselocationy);
		}
		return spheremapview;
	}
	public static RenderView renderCubemapView(Position campos, Entity[] entitylist, int renderwidth, int renderheight, int rendersize, Matrix viewrot, boolean unlit, int mode, int bounces, Plane nclipplane, Triangle nodrawtriangle, Rectangle drawrange, int mouselocationx, int mouselocationy) {
		RenderView renderview = new RenderView();
		renderview.pos = campos.copy();
		renderview.rot = viewrot.copy();
		renderview.renderwidth = renderwidth;
		renderview.renderheight = renderheight;
		renderview.rendersize = rendersize;
		renderview.hfov = 90.0f;
		renderview.vfov = 90.0f;
		renderview.unlit = unlit;
		renderview.mouselocationx = mouselocationx;
		renderview.mouselocationy = mouselocationy;
		int rendermode = (mode>0)?mode:1;
		int renderposx1start = 0;
		int renderposx1end = rendersize-1;
		int renderposx2start = rendersize;
		int renderposx2end = 2*rendersize-1;
		int renderposx3start = 2*rendersize;
		int renderposx3end = 3*rendersize-1;
		int renderposy1start = 0;
		int renderposy1end = rendersize-1;
		int renderposy2start = rendersize;
		int renderposy2end = 2*rendersize-1;
		Matrix topmatrix = MathLib.rotationMatrix(-180.0f, 0.0f, 0.0f);
		Matrix bottommatrix = MathLib.rotationMatrix(0.0f, 0.0f, 0.0f);
		Matrix forwardmatrix = MathLib.rotationMatrix(-90.0f, 0.0f, 0.0f);
		Matrix rightmatrix = MathLib.matrixMultiply(MathLib.rotationMatrix(0.0f, 0.0f, 90.0f), forwardmatrix);
		Matrix backwardmatrix = MathLib.matrixMultiply(MathLib.rotationMatrix(0.0f, 0.0f, 180.0f), forwardmatrix);
		Matrix leftmatrix = MathLib.matrixMultiply(MathLib.rotationMatrix(0.0f, 0.0f, 270.0f), forwardmatrix);
		topmatrix = MathLib.matrixMultiply(renderview.rot, topmatrix);
		bottommatrix = MathLib.matrixMultiply(renderview.rot, bottommatrix);
		forwardmatrix = MathLib.matrixMultiply(renderview.rot, forwardmatrix);
		rightmatrix = MathLib.matrixMultiply(renderview.rot, rightmatrix);
		backwardmatrix = MathLib.matrixMultiply(renderview.rot, backwardmatrix);
		leftmatrix = MathLib.matrixMultiply(renderview.rot, leftmatrix);
		renderview.cubemap = new Cubemap();
		renderview.cubemap.topview = renderProjectedView(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, topmatrix, renderview.unlit, rendermode, bounces, nclipplane, nodrawtriangle, drawrange, mouselocationx, mouselocationy);
		renderview.cubemap.bottomview = renderProjectedView(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, bottommatrix, renderview.unlit, rendermode, bounces, nclipplane, nodrawtriangle, drawrange, mouselocationx, mouselocationy); 
		renderview.cubemap.forwardview = renderProjectedView(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, forwardmatrix, renderview.unlit, rendermode, bounces, nclipplane, nodrawtriangle, drawrange, mouselocationx, mouselocationy);
		renderview.cubemap.rightview = renderProjectedView(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, rightmatrix, renderview.unlit, rendermode, bounces, nclipplane, nodrawtriangle, drawrange, mouselocationx, mouselocationy);
		renderview.cubemap.backwardview = renderProjectedView(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, backwardmatrix, renderview.unlit, rendermode, bounces, nclipplane, nodrawtriangle, drawrange, mouselocationx, mouselocationy);
		renderview.cubemap.leftview = renderProjectedView(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, leftmatrix, renderview.unlit, rendermode, bounces, nclipplane, nodrawtriangle, drawrange, mouselocationx, mouselocationy);
		renderview.renderimage = gc.createCompatibleImage(renderwidth, renderheight, Transparency.TRANSLUCENT);
		Graphics2D rigfx = renderview.renderimage.createGraphics();
		rigfx.setComposite(AlphaComposite.Src);
		rigfx.setColor(new Color(0.0f,0.0f,0.0f,0.0f));
		rigfx.setPaint(null);
		rigfx.setClip(null);
		rigfx.fillRect(0, 0, renderwidth, renderheight);
		rigfx.drawImage(renderview.cubemap.forwardview.renderimage, renderposx1start, renderposy1start, renderposx1end, renderposy1end, 0, 0, renderview.rendersize-1, renderview.rendersize-1, null);
		rigfx.drawImage(renderview.cubemap.backwardview.renderimage, renderposx2start, renderposy1start, renderposx2end, renderposy1end, 0, 0, renderview.rendersize-1, renderview.rendersize-1, null);
		rigfx.drawImage(renderview.cubemap.topview.renderimage, renderposx3start, renderposy1start, renderposx3end, renderposy1end, 0, 0, renderview.rendersize-1, renderview.rendersize-1, null);
		rigfx.drawImage(renderview.cubemap.leftview.renderimage, renderposx1start, renderposy2start, renderposx1end, renderposy2end, 0, 0, renderview.rendersize-1, renderview.rendersize-1, null);
		rigfx.drawImage(renderview.cubemap.bottomview.renderimage, renderposx2start, renderposy2start, renderposx2end, renderposy2end, 0, 0, renderview.rendersize-1, renderview.rendersize-1, null);
		rigfx.drawImage(renderview.cubemap.rightview.renderimage, renderposx3start, renderposy2start, renderposx3end, renderposy2end, 0, 0, renderview.rendersize-1, renderview.rendersize-1, null);
		rigfx.dispose();
		return renderview;
	}

	public static BufferedImage[] renderPlaneRay(PlaneRay[] vplaneray, Entity[] entitylist, int renderheight, boolean spherical, boolean unlit, int bounces, Plane nclipplane, Triangle nodrawtriangle) {
		BufferedImage[] rendercolumn = null;
		if ((vplaneray!=null)&&(entitylist!=null)&&(entitylist.length>0)) {
			rendercolumn = new BufferedImage[vplaneray.length];
			double origindeltay = ((double)(renderheight-1))/2.0f;
			double halfvres = ((double)(renderheight-1))/2.0f;
			Plane[] nclipplanea = {nclipplane};
			Sphere[] entityspherelist = MathLib.entitySphereList(entitylist);
			Position[] entityspherepos = MathLib.sphereVertexList(entityspherelist);
			for (int k=0;k<vplaneray.length;k++) {
				double[] zbuffer = new double[renderheight];
				Arrays.fill(zbuffer, Double.POSITIVE_INFINITY);
				rendercolumn[k] = gc.createCompatibleImage(1, renderheight, Transparency.TRANSLUCENT);
				Graphics2D g2 = rendercolumn[k].createGraphics();
				g2.setComposite(AlphaComposite.Src);
				g2.setColor(new Color(0.0f,0.0f,0.0f,0.0f));
				g2.setPaint(null);
				g2.setClip(null);
				g2.fillRect(0, 0, 1, renderheight);
				g2.setComposite(AlphaComposite.SrcOver);
				PlaneRay[] ray = {vplaneray[k]};
				Position[] raypos = {ray[0].pos};
				Plane[] rayplane = {ray[0].plane};
				Direction[] rayfwddir = {ray[0].dir};
				double[] rayvfov = {ray[0].vfov};
				double[] verticalangles = null;
				double halfvfovmult = 0.0f;
				if (spherical) {
					verticalangles = MathLib.spheremapAngles(renderheight, rayvfov[0]);
					halfvfovmult = (1.0f/(rayvfov[0]/2.0f));
				} else {
					verticalangles = MathLib.projectedAngles(renderheight, rayvfov[0]);
					halfvfovmult = (1.0f/MathLib.tand(rayvfov[0]/2.0f));
				}
				Direction[] camrgtdir = MathLib.planeNormal(rayplane);
				Direction[] camupdir = MathLib.vectorCross(rayfwddir, camrgtdir);
				Plane[] camfwdplane = MathLib.planeFromNormalAtPoint(raypos[0], rayfwddir);
				Plane[] camupplane = MathLib.planeFromNormalAtPoint(raypos[0], camupdir);
				Plane[] rendercutplane = MathLib.translate(camfwdplane,rayfwddir[0],1.1d);
				double[][] rayplaneentityspheredist = MathLib.planePointDistance(entityspherepos, rayplane);
				double[][] nearclipplaneentityspheredist = null;
				if (nclipplanea[0]!=null) {
					nearclipplaneentityspheredist = MathLib.planePointDistance(entityspherepos, nclipplanea);
				}
				for (int j=0;j<entitylist.length;j++) {
					if ((nearclipplaneentityspheredist==null)||((nearclipplaneentityspheredist[j][0]-entityspherelist[j].r)>=1.0f)) {
						if (Math.abs(rayplaneentityspheredist[j][0])<=entityspherelist[j].r) {
							Triangle[] copytrianglelist = entitylist[j].trianglelist;
							if (copytrianglelist.length>0) {
								Direction[] copytrianglenormallist = MathLib.triangleNormal(copytrianglelist);
								Sphere[] copytrianglespherelist = MathLib.triangleCircumSphere(copytrianglelist);
								Plane[] copytriangleplanelist = MathLib.trianglePlane(copytrianglelist);
								Direction[] copyviewtrianglespheredir = MathLib.vectorFromPoints(raypos[0], copytrianglespherelist);
								Position[] copytrianglespherepos = MathLib.sphereVertexList(copytrianglespherelist);
								double[][] rayplanetrianglespheredist = MathLib.planePointDistance(copytrianglespherepos, rayplane);
								double[][] nearclipplanetrianglespheredist = null;
								if (nclipplanea[0]!=null) {
									nearclipplanetrianglespheredist = MathLib.planePointDistance(copytrianglespherepos, nclipplanea);
								}
								for (int i=0;i<copytrianglelist.length;i++) {
									if ((nearclipplanetrianglespheredist==null)||((nearclipplanetrianglespheredist[i][0]-copytrianglespherelist[i].r)>=1.0f)) {
										if (Math.abs(rayplanetrianglespheredist[i][0])<=copytrianglespherelist[i].r) {
											Triangle[] copytriangle = {copytrianglelist[i]};
											if (!copytriangle[0].equals(nodrawtriangle)) {
												Direction[] copytrianglenormal = {copytrianglenormallist[i]};
												Plane[] copytriangleplane = {copytriangleplanelist[i]};
												Direction copytriangledir = copyviewtrianglespheredir[i];
												Line[][] vertplanetriangleint = MathLib.planeTriangleIntersection(rayplane, copytriangle);
												Line drawline = vertplanetriangleint[0][0];
												if (drawline!=null) {
													Position[] drawlinepoints = {drawline.pos1, drawline.pos2};
													double[][] fwdintpointsdist = MathLib.planePointDistance(drawlinepoints, camfwdplane);
													double[][] upintpointsdist = MathLib.planePointDistance(drawlinepoints, camupplane);
													if ((fwdintpointsdist[0][0]>=1.0f)||(fwdintpointsdist[1][0]>=1.0f)) {
														if (!((fwdintpointsdist[0][0]>=1.0f)&&(fwdintpointsdist[1][0]>=1.0f))) {
															Position[] drawlinepos1 = {drawline.pos1};
															Position[] drawlinepos2 = {drawline.pos2};
															Direction[] drawlinedir12 = MathLib.vectorFromPoints(drawlinepos1, drawlinepos2);
															double[][] drawlinedir12dist = MathLib.rayPlaneDistance(drawlinepos1[0], drawlinedir12, rendercutplane);
															Position[] drawlinepos3 = MathLib.translate(drawlinepos1, drawlinedir12[0], drawlinedir12dist[0][0]);
															Coordinate tex1 = drawlinepos1[0].tex; 
															Coordinate tex2 = drawlinepos2[0].tex; 
															if ((tex1!=null)&&(tex2!=null)) {
																Position[] drawlinepostex1 = {new Position(tex1.u,tex1.v,0.0f)};
																Position[] drawlinepostex2 = {new Position(tex2.u,tex2.v,0.0f)};
																Direction[] drawlinetexdir12 = MathLib.vectorFromPoints(drawlinepostex1, drawlinepostex2);
																Position[] drawlinepostex3 = MathLib.translate(drawlinepostex1, drawlinetexdir12[0], drawlinedir12dist[0][0]);
																drawlinepos3[0].tex = new Coordinate(drawlinepostex3[0].x, drawlinepostex3[0].y);
															}
															if (fwdintpointsdist[0][0]>=1.0f) {
																Position[] newdrawlinepoints = {drawlinepos1[0], drawlinepos3[0]};
																drawlinepoints = newdrawlinepoints;
															} else {
																Position[] newdrawlinepoints = {drawlinepos2[0], drawlinepos3[0]};
																drawlinepoints = newdrawlinepoints;
															}
															fwdintpointsdist = MathLib.planePointDistance(drawlinepoints, camfwdplane);
															upintpointsdist = MathLib.planePointDistance(drawlinepoints, camupplane);
														}
														double vpixelyang1 = MathLib.atand(upintpointsdist[0][0]/fwdintpointsdist[0][0]);
														double vpixelyang2 = MathLib.atand(upintpointsdist[1][0]/fwdintpointsdist[1][0]);
														double vpixely1 = 0.0f;
														double vpixely2 = 0.0f;
														if (spherical) {
															vpixely1 = halfvres*halfvfovmult*vpixelyang1+origindeltay;
															vpixely2 = halfvres*halfvfovmult*vpixelyang2+origindeltay;
														} else {
															vpixely1 = halfvfovmult*halfvres*(upintpointsdist[0][0]/fwdintpointsdist[0][0])+origindeltay;
															vpixely2 = halfvfovmult*halfvres*(upintpointsdist[1][0]/fwdintpointsdist[1][0])+origindeltay;
														}
														double[] vpixelys = {vpixely1, vpixely2};
														double[] vpixelyangs = {vpixelyang1, vpixelyang2};
														int[] vpixelyinds = UtilLib.indexSort(vpixelys);
														double[] vpixelysort = UtilLib.indexValues(vpixelys, vpixelyinds);
														Position[] vpixelpoints = {drawlinepoints[vpixelyinds[0]], drawlinepoints[vpixelyinds[1]]};
														Position[] vpixelpoint1 = {vpixelpoints[0]};
														Position[] vpixelpoint2 = {vpixelpoints[1]};
														Position[] vcamposd = {new Position(0.0f,0.0f,0.0f)};
														Position[] vpixelpoint1d = {new Position(fwdintpointsdist[vpixelyinds[0]][0],upintpointsdist[vpixelyinds[0]][0],0.0f)};
														Position[] vpixelpoint2d = {new Position(fwdintpointsdist[vpixelyinds[1]][0],upintpointsdist[vpixelyinds[1]][0],0.0f)};
														Line[] vpixelline = {new Line(vpixelpoint1d[0], vpixelpoint2d[0])};
														double vpixelyangsort1 = vpixelyangs[vpixelyinds[0]]; 
														int vpixelyind1 = (int)Math.ceil(vpixelysort[0]); 
														int vpixelyind2 = (int)Math.floor(vpixelysort[1]);
														int vpixelystart = vpixelyind1;
														int vpixelyend = vpixelyind2;
														Direction[] vpixelpointdir12 = MathLib.vectorFromPoints(vpixelpoint1, vpixelpoint2);
														if ((vpixelyend>=0)&&(vpixelystart<=renderheight)) {
															if (vpixelystart<0) {vpixelystart=0;}
															if (vpixelyend>=renderheight) {vpixelyend=renderheight-1;}
															for (int n=vpixelystart;n<=vpixelyend;n++) {
																double[] vpixelcampointangle = {verticalangles[n]-vpixelyangsort1};
																double[][] vpixelpointlenfraca = MathLib.linearAngleLengthInterpolation(vcamposd[0], vpixelline, vpixelcampointangle);
																double vpixelpointlenfrac = vpixelpointlenfraca[0][0];
																Position[] linepoint = MathLib.translate(vpixelpoint1, vpixelpointdir12[0], vpixelpointlenfrac);
																Direction[] linepointdir = MathLib.vectorFromPoints(raypos, linepoint);
																double[] linepointdirlen = MathLib.vectorLength(linepointdir);
																Direction[] camray = linepointdir;
																double drawdistance = linepointdirlen[0];
																double[][] linepointdist = MathLib.planePointDistance(linepoint, camfwdplane);
																if ((linepointdist[0][0]>=1.0f)&&(drawdistance<zbuffer[n])) {
																	zbuffer[n] = drawdistance;
																	Coordinate tex1 = vpixelpoints[0].tex;
																	Coordinate tex2 = vpixelpoints[1].tex;
																	Coordinate lineuv = null;
																	if ((tex1!=null)&&(tex2!=null)) {
																		Position[] lineuvpoint1 = {new Position(tex1.u,1.0f-tex1.v,0.0f)};
																		Position[] lineuvpoint2 = {new Position(tex2.u,1.0f-tex2.v,0.0f)};
																		Direction[] vpixelpointdir12uv = MathLib.vectorFromPoints(lineuvpoint1, lineuvpoint2);
																		Position[] lineuvpos = MathLib.translate(lineuvpoint1, vpixelpointdir12uv[0], vpixelpointlenfrac);
																		lineuv = new Coordinate(lineuvpos[0].x, lineuvpos[0].y);
																	}
																	Color trianglecolor = trianglePixelShader(copytriangle[0], copytrianglenormal[0], lineuv, camray[0], unlit);
																	if (trianglecolor!=null) {
																		if ((bounces<=0)||(copytriangle[0].norm.isZero())) {
																			g2.setComposite(AlphaComposite.SrcOver);
																		} else {
																			g2.setComposite(AlphaComposite.Src);
																		}
																		g2.setColor(trianglecolor);
																		g2.drawLine(0, n, 0, n);
																	}
																}
															}
															if (bounces>0) {
																double[] camfwddirposnormangle = MathLib.vectorAngle(copytriangledir, copytrianglenormal);
																float refrindex1 = 1.0f;
																float refrindex2 = copytriangle[0].mat.refraction;
																Plane[] vsurf = copytriangleplane;
																if (camfwddirposnormangle[0]<90.0f) {
																	Plane[] newvsurf = {vsurf[0].invert()};
																	vsurf = newvsurf;
																	refrindex1 = copytriangle[0].mat.refraction;
																	refrindex2 = 1.0f;
																}
																if ((copytriangle[0].mat.transparency<1.0f)&&(!copytriangle[0].norm.isZero())) {
																	PlaneRay[][] refractionraya = MathLib.surfaceRefractionPlaneRay(ray, vsurf, refrindex1, refrindex2);
																	if (refractionraya!=null) {
																		PlaneRay[] refractionray = {refractionraya[0][0]};
																		if ((refractionray!=null)&&(refractionray[0]!=null)) {
																			BufferedImage[] refractionrendercolumn = renderPlaneRay(refractionray, entitylist, renderheight, spherical, unlit, bounces-1, vsurf[0].invert(), copytriangle[0]);
																			if ((refractionrendercolumn!=null)&&(refractionrendercolumn[0]!=null)) {
																				g2.setClip(null);
																				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, copytriangle[0].mat.transparency));
																				g2.clip(new Rectangle(0,vpixelystart,1,vpixelyend-vpixelystart+1));
																				g2.drawImage(refractionrendercolumn[0], 0, 0, null);
																				g2.setClip(null);
																			}
																		}
																	}
																}
																if (copytriangle[0].mat.metallic>0.0f) {
																	PlaneRay[][] mirrorraya = MathLib.surfaceMirrorPlaneRay(ray, vsurf);
																	if (mirrorraya!=null) {
																		PlaneRay[] mirrorray = {mirrorraya[0][0]};
																		if ((mirrorray!=null)&&(mirrorray[0]!=null)) {
																			BufferedImage[] mirrorrendercolumn = renderPlaneRay(mirrorray, entitylist, renderheight, spherical, unlit, bounces-1, vsurf[0], copytriangle[0]);
																			if ((mirrorrendercolumn!=null)&&(mirrorrendercolumn[0]!=null)) {
																				g2.setClip(null);
																				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, copytriangle[0].mat.metallic));
																				g2.clip(new Rectangle(0,vpixelystart,1,vpixelyend-vpixelystart+1));
																				g2.drawImage(mirrorrendercolumn[0], 0, 0, null);
																				g2.setClip(null);
																			}
																		}
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return rendercolumn;
	}
	
	public static Color[] renderRay(Ray[] vray, Entity[] entitylist, boolean unlit, int bounces, Triangle nodrawtriangle) {
		Color[] rendercolor = null;
		if ((vray!=null)&&(entitylist!=null)&&(entitylist.length>0)) {
			rendercolor = new Color[vray.length];
			Sphere[] entityspherelist = MathLib.entitySphereList(entitylist);
			Position[] entityspherepos = MathLib.sphereVertexList(entityspherelist);
			for (int k=0;k<vray.length;k++) {
				double zbuffer = Double.POSITIVE_INFINITY;
				Ray[] ray = {vray[k]};
				Position[] raypos = {ray[0].pos};
				Direction[] raydir = {ray[0].dir};
				rendercolor[k] = new Color(0.0f, 0.0f, 0.0f, 0.0f);
				double[][] rayentityspheredist = MathLib.rayPointDistance(raypos[0], raydir, entityspherepos);
				for (int j=0;j<entitylist.length;j++) {
					if (rayentityspheredist[0][j]<=entityspherelist[j].r) {
						Triangle[] copytrianglelist = entitylist[j].trianglelist;
						if (copytrianglelist.length>0) {
							Direction[] copytrianglenormallist = MathLib.triangleNormal(copytrianglelist);
							Sphere[] copytrianglespherelist = MathLib.triangleCircumSphere(copytrianglelist);
							Plane[] copytriangleplanelist = MathLib.trianglePlane(copytrianglelist);
							Position[] copytrianglespherepos = MathLib.sphereVertexList(copytrianglespherelist);
							double[][] raytrianglespheredist = MathLib.rayPointDistance(raypos[0], raydir, copytrianglespherepos);
							double[][] raytriangleplanedist = MathLib.rayPlaneDistance(raypos[0], raydir, copytriangleplanelist);
							for (int i=0;i<copytrianglelist.length;i++) {
								if (raytrianglespheredist[0][i]<=copytrianglespherelist[i].r) {
									Triangle[] copytriangle = {copytrianglelist[i]};
									if (!copytriangle[0].equals(nodrawtriangle)) {
										Direction[] copytrianglenormal = {copytrianglenormallist[i]};
										Plane[] copytriangleplane = {copytriangleplanelist[i]};
										double drawdistance = raytriangleplanedist[0][i];
										Position[][] copyraytriangleint = MathLib.rayTriangleIntersection(raypos[0], raydir, copytriangle);
										Position[] camrayintpos = {copyraytriangleint[0][0]};
										if (camrayintpos[0]!=null) {
											if ((drawdistance>=1.0f)&&(drawdistance<zbuffer)) {
												zbuffer = drawdistance;
												Coordinate tex = camrayintpos[0].tex;
												Coordinate pointuv = null;
												if (tex!=null) {
													pointuv = new Coordinate(tex.u,1.0f-tex.v);
												}
												Color trianglecolor = trianglePixelShader(copytriangle[0], copytrianglenormal[0], pointuv, raydir[0], unlit);
												if (trianglecolor!=null) {
													if ((bounces<=0)||(copytriangle[0].norm.isZero())) {
														rendercolor[k] = MathLib.sourceOverBlend(rendercolor[k], trianglecolor, 1.0f);
													} else {
														rendercolor[k] = trianglecolor;
													}
													if (bounces>0) {
														double[] camfwddirposnormangle = MathLib.vectorAngle(raydir[0], copytrianglenormal);
														float refrindex1 = 1.0f;
														float refrindex2 = copytriangle[0].mat.refraction;
														Plane[] vsurf = copytriangleplane;
														if (camfwddirposnormangle[0]<90.0f) {
															Plane[] newvsurf = {vsurf[0].invert()};
															vsurf = newvsurf;
															refrindex1 = copytriangle[0].mat.refraction;
															refrindex2 = 1.0f;
														}
														if ((copytriangle[0].mat.transparency<1.0f)&&(!copytriangle[0].norm.isZero())) {
															Ray[][] refractionraya = MathLib.surfaceRefractionRay(ray, vsurf, refrindex1, refrindex2);
															if (refractionraya!=null) {
																Ray[] refractionray = {refractionraya[0][0]};
																if ((refractionray!=null)&&(refractionray[0]!=null)) {
																	Color[] refractionraycolor = renderRay(refractionray, entitylist, unlit, bounces-1, copytriangle[0]);
																	if ((refractionraycolor!=null)&&(refractionraycolor[0]!=null)) {
																		rendercolor[k] = MathLib.sourceOverBlend(rendercolor[k], refractionraycolor[0], copytriangle[0].mat.transparency);
																	}
																}
															}
														}
														if (copytriangle[0].mat.metallic>0.0f) {
															Ray[][] mirrorraya = MathLib.surfaceMirrorRay(ray, vsurf);
															if (mirrorraya!=null) {
																Ray[] mirrorray = {mirrorraya[0][0]};
																if ((mirrorray!=null)&&(mirrorray[0]!=null)) {
																	Color[] mirrorraycolor = renderRay(mirrorray, entitylist, unlit, bounces-1, copytriangle[0]);
																	if ((mirrorraycolor!=null)&&(mirrorraycolor[0]!=null)) {
																		rendercolor[k] = MathLib.sourceOverBlend(rendercolor[k], mirrorraycolor[0], copytriangle[0].mat.metallic);
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return rendercolor;
	}
	
	public static Color trianglePixelShader(Triangle triangle, Direction trianglenormal, Coordinate texuv, Direction camray, boolean unlit) {
		Triangle[] copytriangle = {triangle};
		Material copymaterial = copytriangle[0].mat;
		float roughnessmult = 1.0f-copymaterial.roughness;
		Color trianglecolor = copymaterial.facecolor;
		float alphacolor = copymaterial.transparency;
		float[] trianglecolorcomp = null;
		if (trianglecolor!=null) {
			trianglecolorcomp = trianglecolor.getRGBComponents(new float[4]);
			trianglecolor = new Color(trianglecolorcomp[0], trianglecolorcomp[1], trianglecolorcomp[2], alphacolor);
		}
		BufferedImage triangletexture = copymaterial.fileimage;
		Color emissivecolor = copymaterial.emissivecolor;
		float[] emissivecolorcomp = null;
		if (emissivecolor!=null) {
			emissivecolorcomp = emissivecolor.getRGBComponents(new float[4]);
		}
		BufferedImage emissivetexture = copymaterial.emissivefileimage;
		Color lightmapcolor = copymaterial.ambientcolor;
		float[] lightmapcolorcomp =  null;
		if (lightmapcolor!=null) {
			lightmapcolorcomp = lightmapcolor.getRGBComponents(new float[4]);
		}
		BufferedImage lightmaptexture = copymaterial.ambientfileimage;
		float shadingmultiplier = 1.0f;
		boolean frontsidevisible = true;
		if ((trianglenormal!=null)&&(camray!=null)) {
			Direction[] copytrianglenormal = {trianglenormal};
			double[] triangleviewangle = MathLib.vectorAngle(camray, copytrianglenormal);
			if ((copytriangle[0].norm.isZero())&&(triangleviewangle[0]<90.0f)) {
				triangleviewangle[0] = 180.0f - triangleviewangle[0];
			}
			triangleviewangle[0] -= 90.0f;
			if (triangleviewangle[0]<0.0f) {triangleviewangle[0]=0.0f;frontsidevisible=false;}
			shadingmultiplier = ((float)triangleviewangle[0])/90.0f;
		}
		if (texuv!=null) {
			Coordinate[] texuva = {texuv};
			Coordinate[] texuvzero = MathLib.mod(texuva, 1.0f);
			Coordinate texuvz = texuvzero[0];
			if (lightmaptexture!=null) {
				int lineuvx = (int)Math.round(texuvz.u*(lightmaptexture.getWidth()-1));
				int lineuvy = (int)Math.round(texuvz.v*(lightmaptexture.getHeight()-1));
				lightmapcolor = new Color(lightmaptexture.getRGB(lineuvx, lineuvy));
				lightmapcolorcomp = lightmapcolor.getRGBComponents(new float[4]);
			}
			if (emissivetexture!=null) {
				int lineuvx = (int)Math.round(texuvz.u*(emissivetexture.getWidth()-1));
				int lineuvy = (int)Math.round(texuvz.v*(emissivetexture.getHeight()-1));
				emissivecolor = new Color(emissivetexture.getRGB(lineuvx, lineuvy));
				emissivecolorcomp = emissivecolor.getRGBComponents(new float[4]);
			}
			if (triangletexture!=null) {
				int lineuvx = (int)Math.round(texuvz.u*(triangletexture.getWidth()-1));
				int lineuvy = (int)Math.round(texuvz.v*(triangletexture.getHeight()-1));
				Color triangletexturecolor = new Color(triangletexture.getRGB(lineuvx, lineuvy));
				trianglecolorcomp = triangletexturecolor.getRGBComponents(new float[4]);
				trianglecolor = new Color(trianglecolorcomp[0], trianglecolorcomp[1], trianglecolorcomp[2], alphacolor);
			}
		}
		if (trianglecolor!=null) {
			float texr = trianglecolorcomp[0]*shadingmultiplier;
			float texg = trianglecolorcomp[1]*shadingmultiplier;
			float texb = trianglecolorcomp[2]*shadingmultiplier;
			float multiplier = 10.0f;
			if (unlit) {
				if ((frontsidevisible)&&(lightmapcolor!=null)) {
					texr *= lightmapcolorcomp[0]*multiplier*roughnessmult;
					texg *= lightmapcolorcomp[1]*multiplier*roughnessmult;
					texb *= lightmapcolorcomp[2]*multiplier*roughnessmult;
				} else {
					texr = 0.0f;
					texg = 0.0f;
					texb = 0.0f;
				}
			}
			if ((frontsidevisible)&&(emissivecolor!=null)) {
				texr += emissivecolorcomp[0]*multiplier*roughnessmult;
				texg += emissivecolorcomp[1]*multiplier*roughnessmult;
				texb += emissivecolorcomp[2]*multiplier*roughnessmult;
			}
			if (texr>1.0f) {texr=1.0f;}
			if (texg>1.0f) {texg=1.0f;}
			if (texb>1.0f) {texb=1.0f;}
			trianglecolor = new Color(texr, texg, texb, alphacolor);
		}
		return trianglecolor;
	}
	
	public static void renderSurfaceFaceLightmapCubemapView(Entity[] entitylist, int rendersize, int bounces, int mode) {
		float multiplier = 1000.0f;
		Direction[][] cubemaprays = MathLib.projectedRays(rendersize, rendersize, 90.0f, 90.0f, MathLib.rotationMatrix(0.0f, 0.0f, 0.0f), false);
		double[][] cubemapraylen = new double[rendersize][rendersize];
		for (int i=0;i<cubemaprays.length;i++) {
			cubemapraylen[i] = MathLib.vectorLength(cubemaprays[i]);
		}
		int rendermode = (mode>0)?mode:1;
		int lightbounces = (bounces>0)?bounces:1;
		for (int l=0;l<lightbounces;l++) {
			if (entitylist!=null) {
				for (int j=0;j<entitylist.length;j++) {
					if (entitylist[j]!=null) {
						if (entitylist[j].trianglelist!=null) {
							Sphere[] trianglespherelist = MathLib.triangleInSphere(entitylist[j].trianglelist);
							for (int i=0;i<entitylist[j].trianglelist.length;i++) {
								if (entitylist[j].trianglelist[i]!=null) {
									Sphere[] trianglesphere = {trianglespherelist[i]};
									Position[] trianglespherepoint = MathLib.sphereVertexList(trianglesphere);
									RenderView p4pixelview = renderCubemapView(trianglespherepoint[0], entitylist, rendersize*3, rendersize*2, rendersize, MathLib.rotationMatrix(0, 0, 0), true, rendermode, bounces, null, entitylist[j].trianglelist[i], null, 0, 0);
									RenderView[] cubemapviews = new RenderView[6];
									cubemapviews[0] = p4pixelview.cubemap.backwardview;
									cubemapviews[1] = p4pixelview.cubemap.bottomview;
									cubemapviews[2] = p4pixelview.cubemap.forwardview;
									cubemapviews[3] = p4pixelview.cubemap.leftview;
									cubemapviews[4] = p4pixelview.cubemap.rightview;
									cubemapviews[5] = p4pixelview.cubemap.topview;
									float p4pixelr = 0.0f;
									float p4pixelg = 0.0f;
									float p4pixelb = 0.0f;
									float pixelcount = 6*rendersize*rendersize;
									if (entitylist[j].trianglelist[i].mat.emissivecolor!=null) {
										float[] triangleemissivecolorcomp = entitylist[j].trianglelist[i].mat.emissivecolor.getRGBComponents(new float[4]);
										p4pixelr = triangleemissivecolorcomp[0]*pixelcount;
										p4pixelg = triangleemissivecolorcomp[1]*pixelcount;
										p4pixelb = triangleemissivecolorcomp[2]*pixelcount;
									}
									for (int k=0;k<cubemapviews.length;k++) {
										for (int ky=0;ky<cubemapviews[k].renderimage.getHeight();ky++) {
											for (int kx=0;kx<cubemapviews[k].renderimage.getWidth();kx++) {
												Color p4pixelcolor = new Color(cubemapviews[k].renderimage.getRGB(kx, ky));
												float[] p4pixelcolorcomp = p4pixelcolor.getRGBComponents(new float[4]);
												p4pixelr += p4pixelcolorcomp[0]/cubemapraylen[ky][kx];
												p4pixelg += p4pixelcolorcomp[1]/cubemapraylen[ky][kx];
												p4pixelb += p4pixelcolorcomp[2]/cubemapraylen[ky][kx];
											}
										}
									}
									float p4pixelrt = multiplier*p4pixelr/(float)Math.pow(pixelcount,l+1);
									float p4pixelgt = multiplier*p4pixelg/(float)Math.pow(pixelcount,l+1);
									float p4pixelbt = multiplier*p4pixelb/(float)Math.pow(pixelcount,l+1);
									if (p4pixelrt>1.0f) {p4pixelrt=1.0f;}
									if (p4pixelgt>1.0f) {p4pixelgt=1.0f;}
									if (p4pixelbt>1.0f) {p4pixelbt=1.0f;}
									Color p4pixelcolor = new Color(p4pixelrt, p4pixelgt, p4pixelbt, 1.0f);
									System.out.println("RenderLib: renderSurfaceFaceLightmapCubemapView: mode["+mode+"] bounce["+(l+1)+"] entitylist["+(j+1)+"/"+entitylist.length+"]["+(i+1)+"/"+entitylist[j].trianglelist.length+"]="+trianglespherepoint[0].x+","+trianglespherepoint[0].y+","+trianglespherepoint[0].z);
									if ((entitylist[j].trianglelist[i].lmatl==null)||(entitylist[j].trianglelist[i].lmatl.length!=lightbounces)) {
										entitylist[j].trianglelist[i].lmatl = new Material[lightbounces];
									}
									entitylist[j].trianglelist[i].lmatl[l] = new Material(p4pixelcolor, 1.0f, null);
								}
							}
						}
					}
				}
			}
			if (entitylist!=null) {
				for (int j=0;j<entitylist.length;j++) {
					if (entitylist[j]!=null) {
						if (entitylist[j].trianglelist!=null) {
							for (int i=0;i<entitylist[j].trianglelist.length;i++) {
								if (entitylist[j].trianglelist[i]!=null) {
									if ((entitylist[j].trianglelist[i].mat!=null)&&(entitylist[j].trianglelist[i].lmatl!=null)) {
										entitylist[j].trianglelist[i].mat = entitylist[j].trianglelist[i].mat.copy();
										if (entitylist[j].trianglelist[i].mat.ambientcolor==null) {
											entitylist[j].trianglelist[i].mat.ambientcolor = Color.BLACK;
										}
										float[] ambcolorcomp = entitylist[j].trianglelist[i].mat.ambientcolor.getRGBComponents(new float[4]);
										float[] lightcolorcomp = entitylist[j].trianglelist[i].lmatl[l].facecolor.getRGBComponents(new float[4]);
										float newr = ambcolorcomp[0]+lightcolorcomp[0];
										float newg = ambcolorcomp[1]+lightcolorcomp[1];
										float newb = ambcolorcomp[2]+lightcolorcomp[2];
										if (newr>1.0f) {newr=1.0f;}
										if (newg>1.0f) {newg=1.0f;}
										if (newb>1.0f) {newb=1.0f;}
										entitylist[j].trianglelist[i].mat.ambientcolor = new Color(newr,newg,newb,1.0f);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static void renderSurfaceTextureLightmapCubemapView(Entity[] entitylist, int rendersize, int texturesize, int bounces, int mode) {
		//TODO pixel lightmap texture output
		float multiplier = 1000.0f;
		Direction[][] cubemaprays = MathLib.projectedRays(rendersize, rendersize, 90.0f, 90.0f, MathLib.rotationMatrix(0.0f, 0.0f, 0.0f), false);
		double[][] cubemapraylen = new double[rendersize][rendersize];
		for (int i=0;i<cubemaprays.length;i++) {
			cubemapraylen[i] = MathLib.vectorLength(cubemaprays[i]);
		}
		int rendermode = (mode>0)?mode:1;
		int lightbounces = (bounces>0)?bounces:1;
		for (int l=0;l<lightbounces;l++) {
			if (entitylist!=null) {
				for (int j=0;j<entitylist.length;j++) {
					if (entitylist[j]!=null) {
						if (entitylist[j].trianglelist!=null) {
							Sphere[] trianglespherelist = MathLib.triangleInSphere(entitylist[j].trianglelist);
							for (int i=0;i<entitylist[j].trianglelist.length;i++) {
								if (entitylist[j].trianglelist[i]!=null) {
									Sphere[] trianglesphere = {trianglespherelist[i]};
									Position[] trianglespherepoint = MathLib.sphereVertexList(trianglesphere);
									RenderView p4pixelview = renderCubemapView(trianglespherepoint[0], entitylist, rendersize*3, rendersize*2, rendersize, MathLib.rotationMatrix(0, 0, 0), true, rendermode, bounces, null, entitylist[j].trianglelist[i], null, 0, 0);
									RenderView[] cubemapviews = new RenderView[6];
									cubemapviews[0] = p4pixelview.cubemap.backwardview;
									cubemapviews[1] = p4pixelview.cubemap.bottomview;
									cubemapviews[2] = p4pixelview.cubemap.forwardview;
									cubemapviews[3] = p4pixelview.cubemap.leftview;
									cubemapviews[4] = p4pixelview.cubemap.rightview;
									cubemapviews[5] = p4pixelview.cubemap.topview;
									float p4pixelr = 0.0f;
									float p4pixelg = 0.0f;
									float p4pixelb = 0.0f;
									float pixelcount = 6*rendersize*rendersize;
									if (entitylist[j].trianglelist[i].mat.emissivecolor!=null) {
										float[] triangleemissivecolorcomp = entitylist[j].trianglelist[i].mat.emissivecolor.getRGBComponents(new float[4]);
										p4pixelr = triangleemissivecolorcomp[0]*pixelcount;
										p4pixelg = triangleemissivecolorcomp[1]*pixelcount;
										p4pixelb = triangleemissivecolorcomp[2]*pixelcount;
									}
									for (int k=0;k<cubemapviews.length;k++) {
										for (int ky=0;ky<cubemapviews[k].renderimage.getHeight();ky++) {
											for (int kx=0;kx<cubemapviews[k].renderimage.getWidth();kx++) {
												Color p4pixelcolor = new Color(cubemapviews[k].renderimage.getRGB(kx, ky));
												float[] p4pixelcolorcomp = p4pixelcolor.getRGBComponents(new float[4]);
												p4pixelr += p4pixelcolorcomp[0]/cubemapraylen[ky][kx];
												p4pixelg += p4pixelcolorcomp[1]/cubemapraylen[ky][kx];
												p4pixelb += p4pixelcolorcomp[2]/cubemapraylen[ky][kx];
											}
										}
									}
									float p4pixelrt = multiplier*p4pixelr/(float)Math.pow(pixelcount,l+1);
									float p4pixelgt = multiplier*p4pixelg/(float)Math.pow(pixelcount,l+1);
									float p4pixelbt = multiplier*p4pixelb/(float)Math.pow(pixelcount,l+1);
									if (p4pixelrt>1.0f) {p4pixelrt=1.0f;}
									if (p4pixelgt>1.0f) {p4pixelgt=1.0f;}
									if (p4pixelbt>1.0f) {p4pixelbt=1.0f;}
									Color p4pixelcolor = new Color(p4pixelrt, p4pixelgt, p4pixelbt, 1.0f);
									System.out.println("RenderLib: renderSurfaceTextureLightmapCubemapView: mode["+mode+"] bounce["+(l+1)+"] entitylist["+(j+1)+"/"+entitylist.length+"]["+(i+1)+"/"+entitylist[j].trianglelist.length+"]="+trianglespherepoint[0].x+","+trianglespherepoint[0].y+","+trianglespherepoint[0].z);
									if ((entitylist[j].trianglelist[i].lmatl==null)||(entitylist[j].trianglelist[i].lmatl.length!=lightbounces)) {
										entitylist[j].trianglelist[i].lmatl = new Material[lightbounces];
									}
									BufferedImage lightmaptexture = gc.createCompatibleImage(texturesize, texturesize, Transparency.TRANSLUCENT);
									Graphics2D lmgfx = lightmaptexture.createGraphics();
									lmgfx.setComposite(AlphaComposite.Src);
									lmgfx.setColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
									lmgfx.fillRect(0, 0, texturesize, texturesize);
									lmgfx.setComposite(AlphaComposite.SrcOver);
									lmgfx.setColor(p4pixelcolor);
									lmgfx.fillRect(0, 0, texturesize, texturesize);
									lmgfx.dispose();
									Material newlmatl = new Material(Color.WHITE, 1.0f, lightmaptexture);
									newlmatl.fileimage = lightmaptexture; 
									entitylist[j].trianglelist[i].lmatl[l] = newlmatl;
								}
							}
						}
					}
				}
			}
			if (entitylist!=null) {
				for (int j=0;j<entitylist.length;j++) {
					if (entitylist[j]!=null) {
						if (entitylist[j].trianglelist!=null) {
							for (int i=0;i<entitylist[j].trianglelist.length;i++) {
								if (entitylist[j].trianglelist[i]!=null) {
									if ((entitylist[j].trianglelist[i].mat!=null)&&(entitylist[j].trianglelist[i].lmatl!=null)) {
										entitylist[j].trianglelist[i].mat = entitylist[j].trianglelist[i].mat.copy();
										entitylist[j].trianglelist[i].mat.ambientfileimage = entitylist[j].trianglelist[i].lmatl[l].fileimage;
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
}
