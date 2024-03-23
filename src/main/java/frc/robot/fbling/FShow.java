package frc.robot.fbling;

import java.util.ArrayList;
import edu.wpi.first.wpilibj.util.Color;

public class FShow {
    public String name;
    public String description;
    public int version;
    public FSegment[] segments;

    public Color getColor() {
        return new Color(0,0,0);
    }

    public FShow(String n, String d, int v, FSegment[] ss) {
        this.name = n;
        this.description = d;
        this.version = v;
        this.segments = ss;
    }

    public FShow(String n, String d, int v, ArrayList<FSegment> ss) {
        this.name = n;
        this.description = d;
        this.version = v;
        this.segments = ss.toArray(new FSegment[0]);
    }
}
