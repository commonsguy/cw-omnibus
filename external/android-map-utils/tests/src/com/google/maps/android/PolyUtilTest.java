package com.google.maps.android;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.lang.String;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class PolyUtilTest extends TestCase {
    private static final String TEST_LINE = "_cqeFf~cjVf@p@fA}AtAoB`ArAx@hA`GbIvDiFv@gAh@t@X\\|@z@`@Z\\Xf@Vf@VpA\\tATJ@NBBkC";

    private static void expectNearNumber(double expected, double actual, double epsilon) {
        Assert.assertTrue(String.format("Expected %f to be near %f", actual, expected),
                Math.abs(expected - actual) <= epsilon);
    }

    private static List<LatLng> makeList(double... coords) {
        int size = coords.length / 2;
        ArrayList<LatLng> list = new ArrayList<LatLng>(size);
        for (int i = 0; i < size; ++i) {
            list.add(new LatLng(coords[i + i], coords[i + i + 1]));
        }
        return list;
    }

    private static void containsCase(List<LatLng> poly, List<LatLng> yes, List<LatLng> no) {
        for (LatLng point : yes) {
            Assert.assertTrue(PolyUtil.containsLocation(point, poly, true));
            Assert.assertTrue(PolyUtil.containsLocation(point, poly, false));
        }
        for (LatLng point : no) {
            Assert.assertFalse(PolyUtil.containsLocation(point, poly, true));
            Assert.assertFalse(PolyUtil.containsLocation(point, poly, false));
        }
    }

    private static void onEdgeCase(boolean geodesic,
                                   List<LatLng> poly, List<LatLng> yes, List<LatLng> no) {
        for (LatLng point : yes) {
            Assert.assertTrue(PolyUtil.isLocationOnEdge(point, poly, geodesic));
            Assert.assertTrue(PolyUtil.isLocationOnPath(point, poly, geodesic));
        }
        for (LatLng point : no) {
            Assert.assertFalse(PolyUtil.isLocationOnEdge(point, poly, geodesic));
            Assert.assertFalse(PolyUtil.isLocationOnPath(point, poly, geodesic));
        }
    }
    
    private static void onEdgeCase(List<LatLng> poly, List<LatLng> yes, List<LatLng> no) {
        onEdgeCase(true, poly, yes, no);
        onEdgeCase(false, poly, yes, no);
    }
    
    public void testOnEdge() {
        // Empty
        onEdgeCase(makeList(), makeList(), makeList(0, 0));

        final double small = 5e-7;  // About 5cm on equator, half the default tolerance.
        final double big   = 2e-6;  // About 10cm on equator, double the default tolerance.
        
        // Endpoints
        onEdgeCase(makeList(1, 2), makeList(1, 2), makeList(3, 5));
        onEdgeCase(makeList(1, 2, 3, 5), makeList(1, 2, 3, 5), makeList(0, 0));

        // On equator.
        onEdgeCase(makeList(0, 90, 0, 180),
                   makeList(0, 90-small, 0, 90+small, 0-small, 90, 0, 135, small, 135),
                   makeList(0, 90-big, 0, 0, 0, -90, big, 135));

        // Ends on same latitude.
        onEdgeCase(makeList(-45, -180, -45, -small),
                   makeList(-45, 180+small, -45, 180-small, -45-small, 180-small, -45, 0),
                   makeList(-45, big, -45, 180-big, -45+big, -90, -45, 90));

        // Meridian.
        onEdgeCase(makeList(-10, 30, 45, 30),
                   makeList(10, 30-small, 20, 30+small, -10-small, 30+small),
                   makeList(-10-big, 30, 10, -150, 0, 30-big));

        // Slanted close to meridian, close to North pole.
        onEdgeCase(makeList(0, 0, 90-small, 0+big),
                   makeList(1, 0+small, 2, 0-small, 90-small, -90, 90-small, 10),
                   makeList(-big, 0, 90-big, 180, 10, big));
        
        // Arc > 120 deg.
        onEdgeCase(makeList(0, 0, 0, 179.999),
                   makeList(0, 90, 0, small, 0, 179, small, 90),
                   makeList(0, -90, small, -100, 0, 180, 0, -big, 90, 0, -90, 180));

        onEdgeCase(makeList(10, 5, 30, 15),
                   makeList(10+2*big, 5+big, 10+big, 5+big/2, 30-2*big, 15-big),
                   makeList(20, 10, 10-big, 5-big/2, 30+2*big, 15+big, 10+2*big, 5, 10, 5+big));

        onEdgeCase(makeList(90-small, 0, 0, 180-small/2),
                   makeList(big, -180+small/2, big, 180-small/4, big, 180-small),
                   makeList(-big, -180+small/2, -big, 180, -big, 180-small));
        
        // Reaching close to North pole.
        onEdgeCase(true, makeList(80, 0, 80, 180-small),
                   makeList(90-small, -90, 90, -135, 80-small, 0, 80+small, 0),
                   makeList(80, 90, 79, big));

        onEdgeCase(false, makeList(80, 0, 80, 180-small),
                   makeList(80-small, 0, 80+small, 0, 80, 90),
                   makeList(79, big, 90-small, -90, 90, -135));
    }

    public void testContainsLocation() {
        // Empty.
        containsCase(makeList(),
                     makeList(),
                     makeList(0, 0));

        // One point.
        containsCase(makeList(1, 2),
                     makeList(1, 2),
                     makeList(0, 0));

        // Two points.
        containsCase(makeList(1, 2, 3, 5),
                     makeList(1, 2, 3, 5),
                     makeList(0, 0, 40, 4));

        // Some arbitrary triangle.
        containsCase(makeList(0., 0., 10., 12., 20., 5.),
                     makeList(10., 12., 10, 11, 19, 5),
                     makeList(0, 1, 11, 12, 30, 5, 0, -180, 0, 90));

        // Around North Pole.
        containsCase(makeList(89, 0, 89, 120, 89, -120),
                     makeList(90, 0, 90, 180, 90, -90),
                     makeList(-90, 0, 0, 0));

        // Around South Pole.
        containsCase(makeList(-89, 0, -89, 120, -89, -120),
                    makeList(90, 0, 90, 180, 90, -90, 0, 0),
                    makeList(-90, 0, -90, 90));

        // Over/under segment on meridian and equator.
        containsCase(makeList(5, 10, 10, 10, 0, 20, 0, -10),
                     makeList(2.5, 10, 1, 0),
                     makeList(15, 10, 0, -15, 0, 25, -1, 0));
    }
    
    public void testDecodePath() {
        List<LatLng> latLngs = PolyUtil.decode(TEST_LINE);

        int expectedLength = 21;
        Assert.assertEquals("Wrong length.", expectedLength, latLngs.size());

        LatLng lastPoint = latLngs.get(expectedLength - 1);
        expectNearNumber(37.76953, lastPoint.latitude, 1e-6);
        expectNearNumber(-122.41488, lastPoint.longitude, 1e-6);
    }

    public void testEncodePath() {
        List<LatLng> path = PolyUtil.decode(TEST_LINE);
        String encoded = PolyUtil.encode(path);
        Assert.assertEquals(TEST_LINE, encoded);
    }
}
