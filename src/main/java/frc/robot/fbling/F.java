package frc.robot.fbling;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
import edu.wpi.first.wpilibj.Filesystem;

public class F {
    public enum FMode {
        RUNNING,
        PAUSED,
        STOPPED,
    }

    public static final HashMap<String, String> conversionTable = new HashMap<>();

    public static final HashMap<String, String> conversionTableInt = new HashMap<>();

    public static ScriptEngineManager manager = new ScriptEngineManager();
    public static ScriptEngine js;
    
    // Creates the Conversion Tables & JS Engine
    static {
        manager.registerEngineName("Nashorn", new NashornScriptEngineFactory());
        js = manager.getEngineByName("Nashorn");

        conversionTable.put("abs", "jsmabs");
        conversionTable.put("acos", "math.acos");
        conversionTable.put("acosh", "math.acosh");
        conversionTable.put("asin", "math.asin");
        conversionTable.put("asinh", "math.asinh");
        conversionTable.put("atan", "math.atan");
        conversionTable.put("atan2", "math.atan2");
        conversionTable.put("atanh", "math.atanh");
        conversionTable.put("ceil", "math.ceil");
        conversionTable.put("cos", "math.cos");
        conversionTable.put("cosh", "math.cosh");
        conversionTable.put("exp", "math.exp");
        conversionTable.put("expm1", "math.xpm1");
        conversionTable.put("floor", "math.floor");
        conversionTable.put("hypot", "math.hypot");
        conversionTable.put("log", "math.log");
        conversionTable.put("log1p", "math.log1p");
        conversionTable.put("log2", "math.log2");
        conversionTable.put("log10", "math.log10");
        conversionTable.put("max", "jsmmax");
        conversionTable.put("min", "jsmmin");
        conversionTable.put("pow", "jsmpow");
        conversionTable.put("round", "jsmround");
        conversionTable.put("sin", "math.sin");
        conversionTable.put("sinh", "math.sinh");
        conversionTable.put("sqrt", "math.sqrt");
        conversionTable.put("tan", "math.tan");
        conversionTable.put("tanh", "math.tanh");
        conversionTable.put("trunc", "math.trunc");

        conversionTableInt.put("abs", "m001");
        conversionTableInt.put("acos", "m002");
        conversionTableInt.put("acosh", "m003");
        conversionTableInt.put("asin", "m004");
        conversionTableInt.put("asinh", "m005");
        conversionTableInt.put("atan", "m006");
        conversionTableInt.put("atan2", "m007");
        conversionTableInt.put("atanh", "m008");
        conversionTableInt.put("ceil", "m009");
        conversionTableInt.put("cos", "m010");
        conversionTableInt.put("cosh", "m011");
        conversionTableInt.put("exp", "m012");
        conversionTableInt.put("expm1", "m013");
        conversionTableInt.put("floor", "m014");
        conversionTableInt.put("hypot", "m015");
        conversionTableInt.put("log", "m016");
        conversionTableInt.put("log1p", "m017");
        conversionTableInt.put("log2", "m018");
        conversionTableInt.put("log10", "m019");
        conversionTableInt.put("max", "m020");
        conversionTableInt.put("min", "m021");
        conversionTableInt.put("pow", "m022");
        conversionTableInt.put("round", "m023");
        conversionTableInt.put("sin", "m024");
        conversionTableInt.put("sinh", "m025");
        conversionTableInt.put("sqrt", "m026");
        conversionTableInt.put("tan", "m027");
        conversionTableInt.put("tanh", "m028");
        conversionTableInt.put("trunc", "m029");
    }

    public static String convertToInternalMath(String string) {
        string = string.toLowerCase().replaceAll("pi", "3.14159");
        for (String key : conversionTableInt.keySet())
            string = string.replaceAll(key, conversionTableInt.get(key));
        return string;
    }

    public static String convertBackToReal(String string) {
        string = string.toLowerCase();
        for (String val : conversionTableInt.values()) {
            string = string.replaceAll(val, (String) conversionTableInt.keySet().toArray()[Arrays
                    .asList(conversionTableInt.values().toArray()).indexOf(val)]);
        }
        for (String key : conversionTable.keySet()) {
            string = string.replaceAll(key, conversionTable.get(key));
        }
        string = string.replaceAll("jsm", "Math.").replaceAll("math.", "Math.");
        return string;
    }

    private enum ConversionState {
        INIT,
        SEARCHING,
        RED,
        GREEN,
        BLUE
    }

    public static FShow convertfBlingShow(String[] lines) {
        // Strips the lines
        int i = 0;
        for (String ln : lines) {
            lines[i] = ln.replaceAll("\n", "").strip();
            if (lines[i].contains("//"))
                lines[i] = lines[i].substring(0, lines[i].indexOf("//")).strip();
            i += 1;
        }

        // Finds header info (Title, Description, Version)
        int titleIndex = 0;
        while (lines[titleIndex] == "")
            titleIndex += 1;

        int descIndex = titleIndex + 1;
        while (lines[descIndex] == "")
            descIndex += 1;

        int verIndex = descIndex + 1;
        while (lines[verIndex] == "")
            verIndex += 1;

        if (lines[titleIndex].startsWith("$") && lines[descIndex].startsWith("$") && lines[verIndex].startsWith("$"))
            System.err.println("Improper fBling file - Missing header.");

        String title = lines[titleIndex].substring(1).strip();
        String desc = lines[descIndex].substring(1).strip();
        int ver = Integer.parseInt((lines[verIndex].substring(1).strip()));

        // Segments

        ArrayList<FSegment> segments = new ArrayList<FSegment>();
        ConversionState partOn = ConversionState.INIT;
        HashMap<String, Object> currentSeg = new HashMap<String, Object>();
        boolean ugoto = false;
        for (String l : Arrays.copyOfRange(lines, verIndex+1, lines.length)) {
            if ((partOn == ConversionState.INIT || partOn == ConversionState.SEARCHING) && l.startsWith("-")) {
                if (partOn == ConversionState.SEARCHING) {
                    segments.add(new FFunctionSegment((int) currentSeg.get("startFrame"),
                                                      convertToInternalMath((String) currentSeg.get("red")),
                                                      convertToInternalMath((String) currentSeg.get("green")),
                                                      convertToInternalMath((String) currentSeg.get("blue")),
                                                      currentSeg.containsKey("wrap"),
                                                      currentSeg.containsKey("usehsv")));
                }
                currentSeg = new HashMap<String, Object>();
                currentSeg.put("startFrame", (int) Math.round(Math.floor(Float.parseFloat(l.substring(1).strip())*20f)));
                partOn = ConversionState.RED;
            }
            else if (partOn == ConversionState.RED) {
                if (l.contains("goto")) {
                    segments.add(new FGotoSegment((int) currentSeg.get("startFrame"), (int) Math.floor(Float.parseFloat((l.substring(4).strip()))/20)));
                    currentSeg = new HashMap<String, Object>();
                    ugoto = true;
                    partOn = ConversionState.INIT;
                } else if (l.contains("gofo")) {
                    segments.add(new FGotoSegment((int) currentSeg.get("startFrame"), (int) Math.floor(Float.parseFloat((l.substring(4).strip()))/20)));
                    currentSeg = new HashMap<String, Object>();
                    ugoto = true;
                    partOn = ConversionState.INIT;
                } else {
                    currentSeg.put("red", l);
                    partOn = ConversionState.GREEN;
                }
            } else if (partOn == ConversionState.GREEN) {
                currentSeg.put("green", l);
                partOn = ConversionState.BLUE;
            } else if (partOn == ConversionState.BLUE) {
                currentSeg.put("blue", l);
                partOn = ConversionState.SEARCHING;
            } else if (partOn == ConversionState.SEARCHING) {
                if (l.contains("wrap")) currentSeg.put("wrap", true);
                if (l.contains("usehsv")) currentSeg.put("usehsv", true);
            }
        }

        if (!ugoto)
            segments.add(new FFunctionSegment((int) currentSeg.get("startFrame"),
                                                      (String) currentSeg.get("red"),
                                                      (String) currentSeg.get("green"),
                                                      (String) currentSeg.get("blue"),
                                                      currentSeg.containsKey("wrap"),
                                                      currentSeg.containsKey("hsv")));

        return new FShow(title, desc, ver, segments);
    }

    public static FShow loadShow(String name) throws FileNotFoundException, IOException {
        String path = Filesystem.getDeployDirectory().getPath() + "\\" + name + ".fbling";
        System.out.println("Attempting to load " + path);
        BufferedReader reader = new BufferedReader(new FileReader(path));

        List<String> listOfStrings = new ArrayList<String>();

        String line = reader.readLine();

        // checking for end of file
        while (line != null) {
            listOfStrings.add(line);
            line = reader.readLine();
        }

        String[] strs = listOfStrings.toArray(new String[0]);
        reader.close();
        return convertfBlingShow(strs);
    }

    public static FShow nloadShow(String name) {
        try {
            return loadShow(name);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("^^^ its all your fault");
            return null;
        }
    }

}
