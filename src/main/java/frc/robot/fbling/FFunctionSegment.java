package frc.robot.fbling;

import edu.wpi.first.wpilibj.util.Color;

public class FFunctionSegment extends FSegment {
    public final FFunction red;
    public final FFunction green;
    public final FFunction blue;

    private boolean wrap = false;
    private boolean useHSV = false;
    
    public Color eval(int i, int length, double rt, int frame) {
        return new Color(red.calculate(i, length, rt, frame)/255d, green.calculate(i, length, rt, frame)/255d, blue.calculate(i, length, rt, frame)/255d);
    }

    public FFunctionSegment(int startFrame, String redf, String greenf, String bluef) {
        this(startFrame, new FFunction(redf), new FFunction(greenf), new FFunction(bluef));
    }

    public FFunctionSegment(int nstartFrame, FFunction redf, FFunction greenf, FFunction bluef) {
        this(nstartFrame, redf, greenf, bluef, false, false);
    }

    public FFunctionSegment(int startFrame, String redf, String greenf, String bluef, boolean uwrap, boolean hsv) {
        this(startFrame, new FFunction(redf), new FFunction(greenf), new FFunction(bluef), uwrap, hsv);
    }

    public FFunctionSegment(int nstartFrame, FFunction redf, FFunction greenf, FFunction bluef, boolean uwrap, boolean hsv) {
        super(nstartFrame);
        this.red = redf;
        this.blue = bluef;
        this.green = greenf;
        this.wrap = uwrap;
        this.useHSV = hsv;
    }

    public void setUseHSV(boolean set) {
        this.useHSV = set;
    }
    public void setWrap(boolean set) {
        this.wrap = set;
    }

    public String toString() {
        return "-" + Math.round((this.startFrame/20d)*100d)/100d + ", " + this.red.function + ", " + this.green.function + ", " + this.blue.function;
    }
}
