package src.java.GUI;

import java.util.List;

public class PolylineEncoder {

    // Encodes a sequence of LatLng coordinates into an encoded polyline string
    public static String encode(List<Place> path) {
        long lastLat = 0;
        long lastLng = 0;

        StringBuilder result = new StringBuilder();

        for (Place place : path) {
            long lat = Math.round(place.getLatitude() * 1e5);
            long lng = Math.round(place.getLongitude() * 1e5);

            long dLat = lat - lastLat;
            long dLng = lng - lastLng;

            encodeValue(dLat, result);
            encodeValue(dLng, result);

            lastLat = lat;
            lastLng = lng;
        }
        return result.toString();
    }

    // Encodes a single coordinate value into an encoded polyline
    private static void encodeValue(long value, StringBuilder result) {
        // Shift the value left by 1 and if it's negative, invert it
        value = value << 1;
        if (value < 0) {
            value = ~value;
        }

        // While value is greater than or equal to 0x20, take the lowest 5 bits of it and add 0x20 to them
        while (value >= 0x20) {
            int nextValue = (int) (0x20 | (value & 0x1f)) + 63;
            result.append((char) nextValue);
            value >>= 5;
        }

        // The lowest 5 bits of the final value are stored directly
        result.append((char) (value + 63));
    }

}

