package au.com.addstar.colormatch.patterns;

import au.com.addstar.colormatch.ColorMatch;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class PatternRegistry {
    private static final HashMap<String, PatternBase> allPatterns;

    static {
        allPatterns = new HashMap<String, PatternBase>();
        addPattern("Squares", new SquaresPattern(8, 8, 4));
        addPattern("Squares64", new SquaresPattern(16, 16, 4));
        addPattern("random", new SquaresPattern(32, 32, 1));
        addPattern("random64", new SquaresPattern(64, 64, 1));
    }

    public static void addPattern(String name, PatternBase pattern) {
        if (allPatterns.containsKey(name.toLowerCase()))
            throw new IllegalArgumentException("That name already exists");

        allPatterns.put(name.toLowerCase(), pattern);
    }

    public static PatternBase getPattern(String name) {
        return allPatterns.get(name.toLowerCase());
    }

    public static Collection<String> getPatterns() {
        return Collections.unmodifiableCollection(allPatterns.keySet());
    }

    public static void removePattern(String name) {
        PatternBase pattern = getPattern(name);
        if (pattern == null)
            return;

        if (!(pattern instanceof CustomPattern)) {
            throw new IllegalArgumentException("Only custom patterns can be removed");
        }

        allPatterns.remove(name.toLowerCase());

        // Delete the pattern file too
        File base = new File(ColorMatch.plugin.getDataFolder(), "patterns");
        File patternFile = new File(base, name.toLowerCase() + ".pattern");

        if (patternFile.exists())
            patternFile.delete();
    }

    public static boolean save(String name, CustomPattern pattern) {
        File base = new File(ColorMatch.plugin.getDataFolder(), "patterns");
        if (!base.exists()) {
            if (!base.mkdirs()) {
                return false;
            }
        }

        File patternFile = new File(base, name.toLowerCase() + ".pattern");
        return pattern.save(patternFile);
    }

    public static void loadSaved() {
        File base = new File(ColorMatch.plugin.getDataFolder(), "patterns");
        if (!base.exists()) {
            return;
        }

        for (File file : base.listFiles()) {
            CustomPattern pattern = CustomPattern.createFrom(file);
            if (pattern != null) {
                String name = file.getName();
                if (name.contains(".")) {
                    name = name.substring(0, name.lastIndexOf("."));
                }

                allPatterns.put(name.toLowerCase(), pattern);
            }
        }
    }
}
