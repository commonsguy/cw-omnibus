/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.maps.android;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import static java.lang.Math.*;

public class SphericalUtil {

    private SphericalUtil() {}

    /**
     * The earth's radius, in meters.
     * Mean radius as defined by IUGG.
     */
    static final double EARTH_RADIUS = 6371009;

    /**
     * Returns the heading from one LatLng to another LatLng. Headings are
     * expressed in degrees clockwise from North within the range [-180,180).
     * @return The heading in degrees clockwise from north.
     */
    public static double computeHeading(LatLng from, LatLng to) {
        // http://williams.best.vwh.net/avform.htm#Crs
        double fromLat = toRadians(from.latitude);
        double fromLng = toRadians(from.longitude);
        double toLat = toRadians(to.latitude);
        double toLng = toRadians(to.longitude);
        double dLng = toLng - fromLng;
        double heading = atan2(
                sin(dLng) * cos(toLat),
                cos(fromLat) * sin(toLat) - sin(fromLat) * cos(toLat) * cos(dLng));
        return wrap(toDegrees(heading), -180, 180);
    }

    /**
     * Returns the LatLng resulting from moving a distance from an origin
     * in the specified heading (expressed in degrees clockwise from north).
     * @param from     The LatLng from which to start.
     * @param distance The distance to travel.
     * @param heading  The heading in degrees clockwise from north.
     */
    public static LatLng computeOffset(LatLng from, double distance, double heading) {
        distance /= EARTH_RADIUS;
        heading = toRadians(heading);
        // http://williams.best.vwh.net/avform.htm#LL
        double fromLat = toRadians(from.latitude);
        double fromLng = toRadians(from.longitude);
        double cosDistance = cos(distance);
        double sinDistance = sin(distance);
        double sinFromLat = sin(fromLat);
        double cosFromLat = cos(fromLat);
        double sinLat = cosDistance * sinFromLat + sinDistance * cosFromLat * cos(heading);
        double dLng = atan2(
                sinDistance * cosFromLat * sin(heading),
                cosDistance - sinFromLat * sinLat);
        return new LatLng(toDegrees(asin(sinLat)), toDegrees(fromLng + dLng));
    }

    /**
     * Returns the location of origin when provided with a LatLng destination,
     * meters travelled and original heading. Headings are expressed in degrees
     * clockwise from North. This function returns null when no solution is
     * available.
     * @param to       The destination LatLng.
     * @param distance The distance travelled, in meters.
     * @param heading  The heading in degrees clockwise from north.
     */
    public static LatLng computeOffsetOrigin(LatLng to, double distance, double heading) {
        heading = toRadians(heading);
        distance /= EARTH_RADIUS;
        // http://lists.maptools.org/pipermail/proj/2008-October/003939.html
        double n1 = cos(distance);
        double n2 = sin(distance) * cos(heading);
        double n3 = sin(distance) * sin(heading);
        double n4 = sin(toRadians(to.latitude));
        // There are two solutions for b. b = n2 * n4 +/- sqrt(), one solution results
        // in the latitude outside the [-90, 90] range. We first try one solution and
        // back off to the other if we are outside that range.
        double n12 = n1 * n1;
        double discriminant = n2 * n2 * n12 + n12 * n12 - n12 * n4 * n4;
        if (discriminant < 0) {
            // No real solution which would make sense in LatLng-space.
            return null;
        }
        double b = n2 * n4 + sqrt(discriminant);
        b /= n1 * n1 + n2 * n2;
        double a = (n4 - n2 * b) / n1;
        double fromLatRadians = atan2(a, b);
        if (fromLatRadians < -PI / 2 || fromLatRadians > PI / 2) {
            b = n2 * n4 - sqrt(discriminant);
            b /= n1 * n1 + n2 * n2;
            fromLatRadians = atan2(a, b);
        }
        if (fromLatRadians < -PI / 2 || fromLatRadians > PI / 2) {
            // No solution which would make sense in LatLng-space.
            return null;
        }
        double fromLngRadians = toRadians(to.longitude) -
                atan2(n3, n1 * cos(fromLatRadians) - n2 * sin(fromLatRadians));
        return new LatLng(toDegrees(fromLatRadians), toDegrees(fromLngRadians));
    }

    /**
     * Returns the LatLng which lies the given fraction of the way between the
     * origin LatLng and the destination LatLng.
     * @param from     The LatLng from which to start.
     * @param to       The LatLng toward which to travel.
     * @param fraction A fraction of the distance to travel.
     * @return The interpolated LatLng.
     */
    public static LatLng interpolate(LatLng from, LatLng to, double fraction) {
        // http://en.wikipedia.org/wiki/Slerp
        double fromLat = toRadians(from.latitude);
        double fromLng = toRadians(from.longitude);
        double toLat = toRadians(to.latitude);
        double toLng = toRadians(to.longitude);
        double cosFromLat = cos(fromLat);
        double cosToLat = cos(toLat);

        // Computes Spherical interpolation coefficients.
        double angle = computeAngleBetween(from, to);
        double sinAngle = sin(angle);
        if (sinAngle < 1E-6) {
            return from;
        }
        double a = sin((1 - fraction) * angle) / sinAngle;
        double b = sin(fraction * angle) / sinAngle;

        // Converts from polar to vector and interpolate.
        double x = a * cosFromLat * cos(fromLng) + b * cosToLat * cos(toLng);
        double y = a * cosFromLat * sin(fromLng) + b * cosToLat * sin(toLng);
        double z = a * sin(fromLat) + b * sin(toLat);

        // Converts interpolated vector back to polar.
        double lat = atan2(z, sqrt(x * x + y * y));
        double lng = atan2(y, x);
        return new LatLng(toDegrees(lat), toDegrees(lng));
    }

    /**
     * Returns the angle between two LatLngs, in radians.
     */
    static double computeAngleBetween(LatLng from, LatLng to) {
        // Haversine's formula
        double fromLat = toRadians(from.latitude);
        double fromLng = toRadians(from.longitude);
        double toLat = toRadians(to.latitude);
        double toLng = toRadians(to.longitude);
        double dLat = fromLat - toLat;
        double dLng = fromLng - toLng;
        return 2 * asin(sqrt(pow(sin(dLat / 2), 2) +
                cos(fromLat) * cos(toLat) * pow(sin(dLng / 2), 2)));
    }

    /**
     * Returns the distance between two LatLngs, in meters.
     */
    public static double computeDistanceBetween(LatLng from, LatLng to) {
        return computeAngleBetween(from, to) * EARTH_RADIUS;
    }

    /**
     * Returns the length of the given path.
     */
    public static double computeLength(List<LatLng> path) {
        double length = 0;
        for (int i = 0, I = path.size() - 1; i < I; ++i) {
            length += computeDistanceBetween(path.get(i), path.get(i + 1));
        }
        return length;
    }

    /**
     * Returns the area of a closed path. The computed area uses the same units as
     * the radius.
     * @param path A closed path.
     * @return The loop's area in square meters.
     */
    public static double computeArea(List<LatLng> path) {
        return abs(computeSignedArea(path));
    }

    /**
     * Returns the signed area of a closed path. The signed area may be used to
     * determine the orientation of the path. The computed area uses the same
     * units as the radius.
     * @param loop A closed path.
     * @return The loop's area in square meters.
     */
    public static double computeSignedArea(List<LatLng> loop) {
        // For each edge, accumulate the signed area of the triangle formed with
        // the first point. We can skip the first and last edge as they form
        // triangles of zero area.
        LatLng origin = loop.get(0);
        double total = 0;
        for (int i = 1, I = loop.size() - 1; i < I; ++i) {
            total += computeSignedTriangleArea(origin, loop.get(i), loop.get(i + 1));
        }
        return total * EARTH_RADIUS * EARTH_RADIUS;
    }

    /**
     * Compute the signed area of the triangle [a, b, c] on the unit sphere.
     */
    static double computeSignedTriangleArea(LatLng a, LatLng b, LatLng c) {
        return computeTriangleArea(a, b, c) * isCCW(a, b, c);
    }

    /**
     * Compute the area of the triangle [a, b, c] on the unit sphere.
     * We use l'Huilier's theorem, which is the Spherical analogue of Heron's
     * theorem for the area of a triangle in R2.
     * @return The area.
     */
    static double computeTriangleArea(LatLng a, LatLng b, LatLng c) {
        LatLng[] points = new LatLng[]{a, b, c, a};  // Simplify cyclic indexing

        // Compute the length of each edge, and s which is half the perimeter.
        double[] angles = new double[3];
        double s = 0;
        for (int i = 0; i < 3; ++i) {
            angles[i] = computeAngleBetween(points[i], points[i + 1]);
            s += angles[i];
        }
        s /= 2;

        // Apply l'Huilier's theorem
        double product = tan(s / 2);
        for (int i = 0; i < 3; ++i) {
            product *= tan((s - angles[i]) / 2);
        }
        return 4 * atan(sqrt(abs(product)));
    }

    /**
     * Compute the ordering of 3 points in a triangle:
     * Counter ClockWise (CCW) vs ClockWise (CW).
     * Results are indeterminate for triangles of area 0.
     * @return +1 if CCW, -1 if CW.
     */
    static int isCCW(LatLng a, LatLng b, LatLng c) {
        // Convert the 3 points to 3 unit vectors on the sphere.
        LatLng[] points = new LatLng[]{a, b, c};
        double[][] pointsR3 = new double[3][];
        for (int i = 0; i < 3; ++i) {
            LatLng latLng = points[i];
            double lat = toRadians(latLng.latitude);
            double lng = toRadians(latLng.longitude);
            double[] r3 = new double[3];
            r3[0] = cos(lat) * cos(lng);
            r3[1] = cos(lat) * sin(lng);
            r3[2] = sin(lat);
            pointsR3[i] = r3;
        }

        // Compute the determinant of the matrix formed by the 3 unit vectors.
        double det = pointsR3[0][0] * pointsR3[1][1] * pointsR3[2][2] +
                pointsR3[1][0] * pointsR3[2][1] * pointsR3[0][2] +
                pointsR3[2][0] * pointsR3[0][1] * pointsR3[1][2] -
                pointsR3[0][0] * pointsR3[2][1] * pointsR3[1][2] -
                pointsR3[1][0] * pointsR3[0][1] * pointsR3[2][2] -
                pointsR3[2][0] * pointsR3[1][1] * pointsR3[0][2];

        // Threshold to sign
        return det > 0 ? 1 : -1;
    }

    /**
     * Wraps the given value into the inclusive-exclusive interval between min and max.
     * @param n   The value to wrap.
     * @param min The minimum.
     * @param max The maximum.
     */
    static double wrap(double n, double min, double max) {
        return (n >= min && n < max) ? n : (mod(n - min, max - min) + min);
    }

    /**
     * Returns the non-negative remainder of x / m.
     * @param x The operand.
     * @param m The modulus.
     */
    static double mod(double x, double m) {
        return ((x % m) + m) % m;
    }

}
