package com.themysterys.spu;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public final class PackSpot {

    public static final int MAX_NEIGHBOURS = 6;
    public List<String> above = new ArrayList<>();
    public List<String> below = new ArrayList<>();
    public boolean bottom;
    public String name;
    public String sha;

    public static PackSpot around(List<String> keys, int row, int fixedBottom) {
        PackSpot spot = new PackSpot();
        spot.bottom = keys.size() - 1 - row <= fixedBottom;
        for (int i = row - 1; i >= 0 && spot.above.size() < MAX_NEIGHBOURS; i--) {
            spot.above.add(keys.get(i));
        }
        for (int i = row + 1; i < keys.size() && spot.below.size() < MAX_NEIGHBOURS; i++) {
            spot.below.add(keys.get(i));
        }
        return spot;
    }

    public int indexIn(List<String> keys, int fixedBottom) {
        if (above.isEmpty()) {
            return 0;
        }
        if (bottom) {
            return Math.max(0, keys.size() - fixedBottom);
        }
        for (String key : above) {
            int at = keys.indexOf(key);
            if (at >= 0) {
                return at + 1;
            }
        }
        for (String key : below) {
            int at = keys.indexOf(key);
            if (at >= 0) {
                return at;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof PackSpot spot && bottom == spot.bottom && Objects.equals(name, spot.name) && Objects.equals(sha, spot.sha) && above.equals(spot.above) && below.equals(spot.below);
    }

    @Override
    public int hashCode() {
        return Objects.hash(above, below, bottom, name, sha);
    }
}
