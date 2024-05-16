package frc.robot.fbling;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import edu.wpi.first.wpilibj.Filesystem;

public class F {
    public enum FMode {
        RUNNING,
        PAUSED,
        STOPPED,
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
                                                      ((String) currentSeg.get("red")),
                                                      ((String) currentSeg.get("green")),
                                                      ((String) currentSeg.get("blue")),
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
        String path = Filesystem.getDeployDirectory().getPath() + "/" + name + ".fbling";
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
