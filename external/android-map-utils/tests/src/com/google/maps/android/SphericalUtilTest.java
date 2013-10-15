package com.google.maps.android;

import com.google.android.gms.maps.model.LatLng;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SphericalUtilTest extends TestCase {
    static final double EARTH_RADIUS = SphericalUtil.EARTH_RADIUS;
    // The vertices of an octahedron, for testing
    private final LatLng up = new LatLng(90, 0);
    private final LatLng down = new LatLng(-90, 0);
    private final LatLng front = new LatLng(0, 0);
    private final LatLng right = new LatLng(0, 90);
    private final LatLng back = new LatLng(0, -180);
    private final LatLng left = new LatLng(0, -90);

    private static void expectEq(Object expected, Object actual) {
        Assert.assertEquals(expected, actual);
    }

    /**
     * Tests for approximate equality.
     */
    private static void expectLatLngApproxEquals(LatLng actual, LatLng expected) {
        expectNearNumber(actual.latitude, expected.latitude, 1e-6);
        // Account for the convergence of longitude lines at the poles
        double cosLat = Math.cos(Math.toRadians(actual.latitude));
        expectNearNumber(cosLat * actual.longitude, cosLat * expected.longitude, 1e-6);
    }

    private static void expectNearNumber(double actual, double expected, double epsilon) {
        Assert.assertTrue(String.format("Expected %f to be near %f", actual, expected),
                Math.abs(expected - actual) <= epsilon);
    }

    public void testAngles() {
        // Same vertex
        expectNearNumber(SphericalUtil.computeAngleBetween(up, up), 0, 1e-6);
        expectNearNumber(SphericalUtil.computeAngleBetween(down, down), 0, 1e-6);
        expectNearNumber(SphericalUtil.computeAngleBetween(left, left), 0, 1e-6);
        expectNearNumber(SphericalUtil.computeAngleBetween(right, right), 0, 1e-6);
        expectNearNumber(SphericalUtil.computeAngleBetween(front, front), 0, 1e-6);
        expectNearNumber(SphericalUtil.computeAngleBetween(back, back), 0, 1e-6);

        // Adjacent vertices
        expectNearNumber(SphericalUtil.computeAngleBetween(up, front), Math.PI / 2, 1e-6);
        expectNearNumber(SphericalUtil.computeAngleBetween(up, right), Math.PI / 2, 1e-6);
        expectNearNumber(SphericalUtil.computeAngleBetween(up, back), Math.PI / 2, 1e-6);
        expectNearNumber(SphericalUtil.computeAngleBetween(up, left), Math.PI / 2, 1e-6);

        expectNearNumber(SphericalUtil.computeAngleBetween(down, front), Math.PI / 2, 1e-6);
        expectNearNumber(SphericalUtil.computeAngleBetween(down, right), Math.PI / 2, 1e-6);
        expectNearNumber(SphericalUtil.computeAngleBetween(down, back), Math.PI / 2, 1e-6);
        expectNearNumber(SphericalUtil.computeAngleBetween(down, left), Math.PI / 2, 1e-6);

        expectNearNumber(SphericalUtil.computeAngleBetween(back, up), Math.PI / 2, 1e-6);
        expectNearNumber(SphericalUtil.computeAngleBetween(back, right), Math.PI / 2, 1e-6);
        expectNearNumber(SphericalUtil.computeAngleBetween(back, down), Math.PI / 2, 1e-6);
        expectNearNumber(SphericalUtil.computeAngleBetween(back, left), Math.PI / 2, 1e-6);

        // Opposite vertices
        expectNearNumber(SphericalUtil.computeAngleBetween(up, down), Math.PI, 1e-6);
        expectNearNumber(SphericalUtil.computeAngleBetween(front, back), Math.PI, 1e-6);
        expectNearNumber(SphericalUtil.computeAngleBetween(left, right), Math.PI, 1e-6);
    }

    public void testDistances() {
        expectNearNumber(SphericalUtil.computeDistanceBetween(up, down),
                Math.PI * EARTH_RADIUS, 1e-6);
    }

    public void testHeadings() {
        // Opposing vertices for which there is a result
        expectNearNumber(SphericalUtil.computeHeading(up, down), -180, 1e-6);
        expectNearNumber(SphericalUtil.computeHeading(down, up), 0, 1e-6);

        // Adjacent vertices for which there is a result
        expectNearNumber(SphericalUtil.computeHeading(front, up), 0, 1e-6);
        expectNearNumber(SphericalUtil.computeHeading(right, up), 0, 1e-6);
        expectNearNumber(SphericalUtil.computeHeading(back, up), 0, 1e-6);
        expectNearNumber(SphericalUtil.computeHeading(down, up), 0, 1e-6);

        expectNearNumber(SphericalUtil.computeHeading(front, down), -180, 1e-6);
        expectNearNumber(SphericalUtil.computeHeading(right, down), -180, 1e-6);
        expectNearNumber(SphericalUtil.computeHeading(back, down), -180, 1e-6);
        expectNearNumber(SphericalUtil.computeHeading(left, down), -180, 1e-6);

        expectNearNumber(SphericalUtil.computeHeading(right, front), -90, 1e-6);
        expectNearNumber(SphericalUtil.computeHeading(left, front), 90, 1e-6);

        expectNearNumber(SphericalUtil.computeHeading(front, right), 90, 1e-6);
        expectNearNumber(SphericalUtil.computeHeading(back, right), -90, 1e-6);
    }

    public void testComputeOffset() {
        // From front
        expectLatLngApproxEquals(
                front, SphericalUtil.computeOffset(front, 0, 0));
        expectLatLngApproxEquals(
                up, SphericalUtil.computeOffset(front, Math.PI * EARTH_RADIUS / 2, 0));
        expectLatLngApproxEquals(
                down, SphericalUtil.computeOffset(front, Math.PI * EARTH_RADIUS / 2, 180));
        expectLatLngApproxEquals(
                left, SphericalUtil.computeOffset(front, Math.PI * EARTH_RADIUS / 2, -90));
        expectLatLngApproxEquals(
                right, SphericalUtil.computeOffset(front, Math.PI * EARTH_RADIUS / 2, 90));
        expectLatLngApproxEquals(
                back, SphericalUtil.computeOffset(front, Math.PI * EARTH_RADIUS, 0));
        expectLatLngApproxEquals(
                back, SphericalUtil.computeOffset(front, Math.PI * EARTH_RADIUS, 90));

        // From left
        expectLatLngApproxEquals(
                left, SphericalUtil.computeOffset(left, 0, 0));
        expectLatLngApproxEquals(
                up, SphericalUtil.computeOffset(left, Math.PI * EARTH_RADIUS / 2, 0));
        expectLatLngApproxEquals(
                down, SphericalUtil.computeOffset(left, Math.PI * EARTH_RADIUS / 2, 180));
        expectLatLngApproxEquals(
                front, SphericalUtil.computeOffset(left, Math.PI * EARTH_RADIUS / 2, 90));
        expectLatLngApproxEquals(
                back, SphericalUtil.computeOffset(left, Math.PI * EARTH_RADIUS / 2, -90));
        expectLatLngApproxEquals(
                right, SphericalUtil.computeOffset(left, Math.PI * EARTH_RADIUS, 0));
        expectLatLngApproxEquals(
                right, SphericalUtil.computeOffset(left, Math.PI * EARTH_RADIUS, 90));

        // NOTE(appleton): Heading is undefined at the poles, so we do not test
        // from up/down.
    }

    public void testComputeOffsetOrigin() {
        expectLatLngApproxEquals(
                front, SphericalUtil.computeOffsetOrigin(front, 0, 0));

        expectLatLngApproxEquals(
                front, SphericalUtil.computeOffsetOrigin(new LatLng(0, 45),
                Math.PI * EARTH_RADIUS / 4, 90));
        expectLatLngApproxEquals(
                front, SphericalUtil.computeOffsetOrigin(new LatLng(0, -45),
                Math.PI * EARTH_RADIUS / 4, -90));
        expectLatLngApproxEquals(
                front, SphericalUtil.computeOffsetOrigin(new LatLng(45, 0),
                Math.PI * EARTH_RADIUS / 4, 0));
        expectLatLngApproxEquals(
                front, SphericalUtil.computeOffsetOrigin(new LatLng(-45, 0),
                Math.PI * EARTH_RADIUS / 4, 180));
        /*expectLatLngApproxEquals(
                front, SphericalUtil.computeOffsetOrigin(new LatLng(-45, 0),
                Math.PI / 4, 180, 1)); */
        // Situations with no solution, should return null.
        //
        // First 'over' the pole.
        expectEq(null, SphericalUtil.computeOffsetOrigin(new LatLng(80, 0),
                Math.PI * EARTH_RADIUS / 4, 180));
        // Second a distance that doesn't fit on the earth.
        expectEq(null, SphericalUtil.computeOffsetOrigin(new LatLng(80, 0),
                Math.PI * EARTH_RADIUS / 4, 90));
    }

    public void testComputeOffsetAndBackToOrigin() {
        LatLng start = new LatLng(40, 40);
        double distance = 1e5;
        double heading = 15;
        LatLng end;

        // Some semi-random values to demonstrate going forward and backward yields
        // the same location.
        end = SphericalUtil.computeOffset(start, distance, heading);
        expectLatLngApproxEquals(
                start, SphericalUtil.computeOffsetOrigin(end, distance, heading));

        heading = -37;
        end = SphericalUtil.computeOffset(start, distance, heading);
        expectLatLngApproxEquals(
                start, SphericalUtil.computeOffsetOrigin(end, distance, heading));

        distance = 3.8e+7;
        end = SphericalUtil.computeOffset(start, distance, heading);
        expectLatLngApproxEquals(
                start, SphericalUtil.computeOffsetOrigin(end, distance, heading));

        start = new LatLng(-21, -73);
        end = SphericalUtil.computeOffset(start, distance, heading);
        expectLatLngApproxEquals(
                start, SphericalUtil.computeOffsetOrigin(end, distance, heading));

        // computeOffsetOrigin with multiple solutions, all we care about is that
        // going from there yields the requested result.
        //
        // First, for this particular situation the latitude is completely arbitrary.
        start = SphericalUtil.computeOffsetOrigin(new LatLng(0, 90),
                Math.PI * EARTH_RADIUS / 2, 90);
        expectLatLngApproxEquals(
                new LatLng(0, 90),
                SphericalUtil.computeOffset(start, Math.PI * EARTH_RADIUS / 2, 90));

        // Second, for this particular situation the longitude is completely
        // arbitrary.
        start = SphericalUtil.computeOffsetOrigin(new LatLng(90, 0),
                Math.PI * EARTH_RADIUS / 4, 0);
        expectLatLngApproxEquals(
            new LatLng(90, 0),
            SphericalUtil.computeOffset(start, Math.PI * EARTH_RADIUS / 4, 0));
    }

    public void testInterpolate() {
        // Same point
        expectLatLngApproxEquals(
                up, SphericalUtil.interpolate(up, up, 1 / 2.0));
        expectLatLngApproxEquals(
                down, SphericalUtil.interpolate(down, down, 1 / 2.0));
        expectLatLngApproxEquals(
                left, SphericalUtil.interpolate(left, left, 1 / 2.0));

        // Between front and up
        expectLatLngApproxEquals(
                new LatLng(1, 0), SphericalUtil.interpolate(front, up, 1 / 90.0));
        expectLatLngApproxEquals(
                new LatLng(1, 0), SphericalUtil.interpolate(up, front, 89 / 90.0));
        expectLatLngApproxEquals(
                new LatLng(89, 0), SphericalUtil.interpolate(front, up, 89 / 90.0));
        expectLatLngApproxEquals(
                new LatLng(89, 0), SphericalUtil.interpolate(up, front, 1 / 90.0));

        // Between front and down
        expectLatLngApproxEquals(
                new LatLng(-1, 0), SphericalUtil.interpolate(front, down, 1 / 90.0));
        expectLatLngApproxEquals(
                new LatLng(-1, 0), SphericalUtil.interpolate(down, front, 89 / 90.0));
        expectLatLngApproxEquals(
                new LatLng(-89, 0), SphericalUtil.interpolate(front, down, 89 / 90.0));
        expectLatLngApproxEquals(
                new LatLng(-89, 0), SphericalUtil.interpolate(down, front, 1 / 90.0));

        // Between left and back
        expectLatLngApproxEquals(
                new LatLng(0, -91), SphericalUtil.interpolate(left, back, 1 / 90.0));
        expectLatLngApproxEquals(
                new LatLng(0, -91), SphericalUtil.interpolate(back, left, 89 / 90.0));
        expectLatLngApproxEquals(
                new LatLng(0, -179), SphericalUtil.interpolate(left, back, 89 / 90.0));
        expectLatLngApproxEquals(
                new LatLng(0, -179), SphericalUtil.interpolate(back, left, 1 / 90.0));

        // geodesic crosses pole
        expectLatLngApproxEquals(
                up, SphericalUtil.interpolate(new LatLng(45, 0), new LatLng(45, 180), 1 / 2.0));
        expectLatLngApproxEquals(
                down,
                SphericalUtil.interpolate(new LatLng(-45, 0), new LatLng(-45, 180), 1 / 2.0));
    }

    public void testComputeLength() {
        List<LatLng> latLngs;

        expectNearNumber(SphericalUtil.computeLength(Collections.<LatLng>emptyList()), 0, 1e-6);
        expectNearNumber(SphericalUtil.computeLength(Arrays.asList(new LatLng(0, 0))), 0, 1e-6);

        latLngs = Arrays.asList(new LatLng(0, 0), new LatLng(0.1, 0.1));
        expectNearNumber(SphericalUtil.computeLength(latLngs),
                Math.toRadians(0.1) * Math.sqrt(2) * EARTH_RADIUS, 1);

        latLngs = Arrays.asList(new LatLng(0, 0), new LatLng(90, 0), new LatLng(0, 90));
        expectNearNumber(SphericalUtil.computeLength(latLngs), Math.PI * EARTH_RADIUS, 1e-6);
    }

    public void testIsCCW() {
        // One face of the octahedron
        expectEq(1, SphericalUtil.isCCW(right, up, front));
        expectEq(1, SphericalUtil.isCCW(up, front, right));
        expectEq(1, SphericalUtil.isCCW(front, right, up));
        expectEq(-1, SphericalUtil.isCCW(front, up, right));
        expectEq(-1, SphericalUtil.isCCW(up, right, front));
        expectEq(-1, SphericalUtil.isCCW(right, front, up));
    }

    public void testComputeTriangleArea() {
        expectNearNumber(SphericalUtil.computeTriangleArea(right, up, front), Math.PI / 2, 1e-6);

        expectNearNumber(SphericalUtil.computeTriangleArea(front, up, right), Math.PI / 2, 1e-6);

        // computeArea returns area of zero on small polys
        double area = SphericalUtil.computeTriangleArea(
                new LatLng(0, 0),
                new LatLng(0, Math.toDegrees(1E-6)),
                new LatLng(Math.toDegrees(1E-6), 0));
        double expectedArea = 1E-12 / 2;

        Assert.assertTrue(Math.abs(expectedArea - area) < 1e-20);
    }

    public void testComputeSignedTriangleArea() {
        expectNearNumber(
                SphericalUtil.computeSignedTriangleArea(
                        new LatLng(0, 0), new LatLng(0, 0.1), new LatLng(0.1, 0.1)),
                Math.toRadians(0.1) * Math.toRadians(0.1) / 2, 1e-6);

        expectNearNumber(SphericalUtil.computeSignedTriangleArea(right, up, front),
                Math.PI / 2, 1e-6);

        expectNearNumber(SphericalUtil.computeSignedTriangleArea(front, up, right),
                -Math.PI / 2, 1e-6);
    }

    public void testComputeArea() {
        expectNearNumber(SphericalUtil.computeArea(Arrays.asList(right, up, front, down, right)),
                Math.PI * EARTH_RADIUS * EARTH_RADIUS, .4);

        expectNearNumber(SphericalUtil.computeArea(Arrays.asList(right, down, front, up, right)),
                Math.PI * EARTH_RADIUS * EARTH_RADIUS, .4);
    }

    public void testComputeSignedArea() {
        List<LatLng> path = Arrays.asList(right, up, front, down, right);
        List<LatLng> pathReversed = Arrays.asList(right, down, front, up, right);
        expectEq(-SphericalUtil.computeSignedArea(path), SphericalUtil.computeSignedArea(pathReversed));
    }
}

