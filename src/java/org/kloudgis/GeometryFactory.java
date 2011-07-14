/*
 * EchoSphere - Spatial Platform for Application and Communication Extensions.
 *
 * http://www.echospheretech.com
 *
 * Copyright (c) 2003 Echosphere Technologies, Inc.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Echosphere Technologies, Inc. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Echosphere Technologies, Inc.
 *
 */
package org.kloudgis;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import java.util.Arrays;

/**
 * Factory to perform the basic geometry transformation
 * @author jeanfelixg
 */
public class GeometryFactory {

    private static com.vividsolutions.jts.geom.GeometryFactory factory = new com.vividsolutions.jts.geom.GeometryFactory(new PrecisionModel(), 4326);

    public static void setPrecision(int iDecimal) {
        PrecisionModel model = new PrecisionModel(Math.pow(10, iDecimal));
        doSetPrecision(model);
    }

    public static void setPrecisionFull() {
        PrecisionModel model = new PrecisionModel();
        doSetPrecision(model);
    }

    public static void makePrecise(Coordinate c) {
        if(c != null){
            factory.getPrecisionModel().makePrecise(c);
        }
    }

    public static void makePrecise(Coordinate[] arrC) {
        if (arrC != null) {
            PrecisionModel model = factory.getPrecisionModel();
            for (int i = 0; i < arrC.length; i++) {
                model.makePrecise(arrC[i]);
            }
        }
    }
    
    public static Point generateLonLat(){
        return createPoint(new Coordinate(generateLon(), generateLat()));
    }
    
    public static double generateLon(){
         double dVal = Math.random() * 360;
         if(dVal > 180){
             dVal = (dVal - 180) * -1.0;
         }
         return dVal;
    }
    
    public static double generateLat(){
        double dVal = Math.random() * 180;
         if(dVal > 90){
             dVal = (dVal - 90) * -1.0;
         }
         return dVal;
    }

     public static double roundDouble(double d, int decimal) {
        double dShifter = Math.pow(10, decimal);
        double y = Math.abs(d) * dShifter;
        long rounded = Math.round(y);
        double dStrip = (double) rounded / dShifter;
        return d < 0.0 ? -1.0 * dStrip : dStrip;
    }


    private static void doSetPrecision(PrecisionModel model) {
        factory = new com.vividsolutions.jts.geom.GeometryFactory(model);
    }

    //************************************************************************//
    //  IO
    //************************************************************************//
    public static String toWKT(Geometry geo) {
        return new WKTWriter().write(geo);
    }

    public static Geometry readWKT(String wkt) throws ParseException {
        WKTReader wktReader = new WKTReader(factory);
        return wktReader.read(wkt);
    }

    public static Geometry readWKB(byte[] wkb) throws ParseException {
        WKBReader wkbReader = new WKBReader(factory);
        return wkbReader.read(wkb);
    }

    //************************************************************************//
    //  Geometry creation
    //************************************************************************//
    //POINT
    public static com.vividsolutions.jts.geom.Point createPoint(com.vividsolutions.jts.geom.Coordinate coordinate) {
        makePrecise(coordinate);
        return factory.createPoint(coordinate);
    }

    //MULTIPOINT
    public static com.vividsolutions.jts.geom.MultiPoint createMultiPoint(com.vividsolutions.jts.geom.Coordinate[] coordinates) {
        makePrecise(coordinates);
        return factory.createMultiPoint(coordinates);
    }

    public static com.vividsolutions.jts.geom.MultiPoint createMultiPoint(Point[] pts) {
        com.vividsolutions.jts.geom.Coordinate[] coordinates = null;
        if (pts != null) {
            coordinates = new com.vividsolutions.jts.geom.Coordinate[pts.length];
            for (int i = 0; i < pts.length; i++) {
                coordinates[i] = pts[i].getCoordinate();
            }
            makePrecise(coordinates);
        }
        return factory.createMultiPoint(coordinates);
    }

    //LINESTRING
    public static com.vividsolutions.jts.geom.LineString createLineString(com.vividsolutions.jts.geom.Coordinate[] coordinates) {
        makePrecise(coordinates);
        return factory.createLineString(coordinates);
    }

    public static com.vividsolutions.jts.geom.LineString createLineString(Point[] pts) {
        com.vividsolutions.jts.geom.Coordinate[] coordinates = null;
        if (pts != null) {
            coordinates = new com.vividsolutions.jts.geom.Coordinate[pts.length];
            for (int i = 0; i < pts.length; i++) {
                coordinates[i] = pts[i].getCoordinate();
            }
            makePrecise(coordinates);
        }
        return factory.createLineString(coordinates);
    }

    public static com.vividsolutions.jts.geom.LineString createLineString(double[] inXs, double[] inYs) {
        com.vividsolutions.jts.geom.Coordinate[] coordinates = null;
        if (inXs != null) {
            coordinates = new com.vividsolutions.jts.geom.Coordinate[inXs.length];
            for (int i = 0; i < inXs.length; i++) {
                coordinates[i] = new com.vividsolutions.jts.geom.Coordinate(inXs[i], inYs[i]);
            }
            makePrecise(coordinates);
        }
        return factory.createLineString(coordinates);
    }

    //LINEARRING
    public static com.vividsolutions.jts.geom.LinearRing createLinearRing(com.vividsolutions.jts.geom.Coordinate[] pts) {
        com.vividsolutions.jts.geom.Coordinate[] coordinates = null;
        if (pts != null) {
            if (pts[0].equals(pts[pts.length - 1])) {
                coordinates = pts;
            } else {
                coordinates = Arrays.copyOf(pts, pts.length + 1);
                coordinates[pts.length] = pts[0];//close it
            }
            makePrecise(coordinates);
        }
        return factory.createLinearRing(coordinates);
    }

    public static com.vividsolutions.jts.geom.LinearRing createLinearRing(Point[] pts) {
        com.vividsolutions.jts.geom.Coordinate[] coordinates = null;
        if (pts != null) {
            if (pts[0].equals(pts[pts.length - 1])) {
                coordinates = new com.vividsolutions.jts.geom.Coordinate[pts.length];
            } else {
                coordinates = new com.vividsolutions.jts.geom.Coordinate[pts.length + 1];
                coordinates[pts.length] = pts[0].getCoordinate();//close it
            }
            for (int i = 0; i < pts.length; i++) {
                coordinates[i] = pts[i].getCoordinate();
            }
            makePrecise(coordinates);
        }
        return factory.createLinearRing(coordinates);
    }

    public static com.vividsolutions.jts.geom.LinearRing createLinearRing(double[] inXs, double[] inYs) {
        com.vividsolutions.jts.geom.Coordinate[] coordinates = null;
        if (inXs != null) {
            coordinates = new com.vividsolutions.jts.geom.Coordinate[inXs.length];
            for (int i = 0; i < inXs.length; i++) {
                coordinates[i] = new com.vividsolutions.jts.geom.Coordinate(inXs[i], inYs[i]);
            }
            if (!coordinates[0].equals(coordinates[coordinates.length - 1])) {
                Coordinate[] coordClosed = Arrays.copyOf(coordinates, coordinates.length + 1);
                coordClosed[coordinates.length] = coordinates[0];//close it
                coordinates = coordClosed;
            }
            makePrecise(coordinates);
        }
        return factory.createLinearRing(coordinates);
    }

    //MULTILINESTRING
    public static com.vividsolutions.jts.geom.MultiLineString createMultiLineString(com.vividsolutions.jts.geom.LineString[] linestrings) {
        return factory.createMultiLineString(linestrings);
    }

    //POLYGON
    public static com.vividsolutions.jts.geom.Polygon createPolygon(com.vividsolutions.jts.geom.LinearRing ring, com.vividsolutions.jts.geom.LinearRing[] holes) {
        return factory.createPolygon(ring, holes);
    }

    //MULTIPOLYGON
    public static com.vividsolutions.jts.geom.MultiPolygon createMultiPolygon(com.vividsolutions.jts.geom.Polygon[] polygons) {
        return factory.createMultiPolygon(polygons);
    }

    //GEOMETRY COLLECTION
    public static com.vividsolutions.jts.geom.GeometryCollection createGeometryCollection(com.vividsolutions.jts.geom.Geometry[] geos) {
        return factory.createGeometryCollection(geos);
    }

    //ENVELOPE
    public static com.vividsolutions.jts.geom.Envelope createEnvelope(Coordinate c1, Coordinate c2) {
        makePrecise(c1);
        makePrecise(c2);
        return new com.vividsolutions.jts.geom.Envelope(c1, c2);
    }  

}
