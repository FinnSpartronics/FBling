package frc.robot.fbling;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.util.Color;

public class FFunctionSegment extends FSegment {
    public final FFunction red;
    public final FFunction green;
    public final FFunction blue;

    public boolean wrap = false;
    public boolean useHSV = false;

    private static final float hsvr = 2.55f;

    public Color eval(int i, int length, double rt, int frame) {
        double r = red.calculate(i, length, rt, frame);
        double g = green.calculate(i, length, rt, frame);
        double b = blue.calculate(i, length, rt, frame);
        
        if (useHSV) {
            if (wrap) return Color.fromHSV(wc100(r), wc100(g), wc100(b));
            else return Color.fromHSV((int) (r/2), (int) (g*hsvr), (int) (b*hsvr));
        }

        if (wrap) return new Color(wc(r), wc(g), wc(b));
        return new Color(r/255, g/255, b/255);
    }

    // Wrap Clamp
    private int wc(double x) {
        if (x == 0f) return 0;
        if (x < 0)
            return (int) ((x - 0.1) % 255);
        return (int) ((x - 0.1) % 255);
    }

    // Wrap Clamp 360
    private int wc360(double x) {
        x *= .5;
        if (x == 0f) return 0;
        if (x < 0)
            return (int) ((x - 0.1) % 180);
        return (int) ((x - 0.1) % 180);
    }

    private int wc100(double x) {
        x *= hsvr;
        if (x == 0f) return 0;
        if (x < 0)
            return (int) ((x - 0.1) % 255);
        return (int) ((x - 0.1) % 255);
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
        System.out.println(hsv);
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

    public double clamp255(double n) {
        return MathUtil.clamp(n,0,255);
    }

    public double clamp1(double n) {
        return MathUtil.clamp(n,0,1);
    }

    // Make num real
    public double mnr(double c) {
        if (useHSV) {
            if (wrap) {
                if (c == 0) return 0d;
                if (c < 0)
                    return (c - 0.000001d) % 1d;
                return (c - 0.000001d) % 1d;
            }
            return clamp1(c/360d);
        } 
        if (wrap) {
            if (c == 0) return 0d;
            if (c < 0)
                return ((c - 0.1) % 255d);
            return ((c - 0.1) % 255d);
        }
        return c;
    }

}
