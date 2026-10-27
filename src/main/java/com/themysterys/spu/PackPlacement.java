package com.themysterys.spu;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public final class PackPlacement {

    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("spu-positions.json");
    private static final Type MAP_TYPE = new TypeToken<Map<String, PackSpot>>() {}.getType();
    private static final Gson GSON = new Gson();

    private static final Map<String, String> HASHES = new ConcurrentHashMap<>();

    private static Map<String, PackSpot> saved;

    private PackPlacement() {
    }

    /**
     * Hashes a downloaded pack file with SHA-1 and keeps the result for its UUID.
     * The server's own hash is not used, since a server is not required to send one.
     */
    public static void hashFile(UUID id, Path file) {
        String key = id.toString();
        if (HASHES.containsKey(key)) {
            return;
        }
        try (InputStream in = Files.newInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] buffer = new byte[65536];
            int read;
            while ((read = in.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
            HASHES.put(key, HexFormat.of().formatHex(digest.digest()));
        } catch (IOException | NoSuchAlgorithmException e) {
            System.err.println("[SPU] Could not hash server pack: " + e);
        }
    }

    /**
     * Finds the saved spot for a pack: by UUID, then by file hash, then by name.
     * A hash or name only counts when exactly one saved spot has it.
     */
    public static synchronized PackSpot spotOf(Pack pack) {
        Map<String, PackSpot> spots = load();
        PackSpot spot = spots.get(keyOf(pack));
        if (spot != null) {
            return spot;
        }
        String sha = shaOf(pack);
        if (sha != null) {
            spot = onlyMatch(spots.values(), other -> sha.equals(other.sha));
            if (spot != null) {
                return spot;
            }
        }
        String name = nameOf(pack);
        return name.isEmpty() ? null : onlyMatch(spots.values(), other -> name.equals(other.name));
    }

    /**
     * Saves a pack's spot. Saved entries with the same hash or name under a UUID that is
     * no longer selected are the pack's old UUID: they are dropped and neighbour
     * references to them are moved to the new UUID.
     */
    public static synchronized void remember(Pack pack, PackSpot spot, List<String> selectedKeys) {
        String key = keyOf(pack);
        String name = nameOf(pack);
        spot.name = name.isEmpty() ? null : name;
        spot.sha = shaOf(pack);
        Map<String, PackSpot> spots = load();
        boolean changed = false;
        Set<String> stale = new HashSet<>();
        for (Map.Entry<String, PackSpot> entry : spots.entrySet()) {
            PackSpot other = entry.getValue();
            boolean same = spot.sha != null && spot.sha.equals(other.sha) || spot.name != null && spot.name.equals(other.name);
            if (same && !entry.getKey().equals(key) && !selectedKeys.contains(entry.getKey())) {
                stale.add(entry.getKey());
            }
        }
        for (String old : stale) {
            spots.remove(old);
            for (PackSpot other : spots.values()) {
                other.above.replaceAll(k -> k.equals(old) ? key : k);
                other.below.replaceAll(k -> k.equals(old) ? key : k);
            }
            changed = true;
        }
        if (!spot.equals(spots.get(key))) {
            spots.put(key, spot);
            changed = true;
        }
        if (!changed) {
            return;
        }
        try (Writer writer = Files.newBufferedWriter(FILE)) {
            GSON.toJson(spots, MAP_TYPE, writer);
        } catch (IOException e) {
            System.err.println("[SPU] Could not save pack positions: " + e);
        }
    }

    private static PackSpot onlyMatch(Collection<PackSpot> spots, Predicate<PackSpot> test) {
        PackSpot match = null;
        for (PackSpot spot : spots) {
            if (test.test(spot)) {
                if (match != null) {
                    return null;
                }
                match = spot;
            }
        }
        return match;
    }

    private static String shaOf(Pack pack) {
        return HASHES.get(keyOf(pack));
    }

    private static String nameOf(Pack pack) {
        return pack.getDescription().getString().trim();
    }

    public static boolean isFixedBottom(PackSelectionConfig config) {
        return config.fixedPosition() && config.defaultPosition() == Pack.Position.BOTTOM;
    }

    public static String keyOf(Pack pack) {
        String id = pack.getId();
        if (pack.getPackSource() != PackSource.SERVER) {
            return id;
        }
        String last = id.substring(id.lastIndexOf('/') + 1);
        try {
            return UUID.fromString(last).toString();
        } catch (IllegalArgumentException e) {
            return id;
        }
    }

    private static Map<String, PackSpot> load() {
        if (saved == null) {
            saved = new HashMap<>();
            if (Files.isRegularFile(FILE)) {
                try (Reader reader = Files.newBufferedReader(FILE)) {
                    Map<String, PackSpot> read = GSON.fromJson(reader, MAP_TYPE);
                    if (read != null) {
                        saved.putAll(read);
                    }
                } catch (Exception e) {
                    System.err.println("[SPU] Could not read pack positions, starting over: " + e);
                }
            }
        }
        return saved;
    }
}
