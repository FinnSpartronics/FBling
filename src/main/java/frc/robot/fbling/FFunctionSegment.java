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
        if (useHSV) {
            int r = (int) (mnr(red.calculate(i, length, rt, frame)/2d));
            int g = (int) (mnr(green.calculate(i, length, rt, frame)*hsvr));
            int b = (int) (mnr(blue.calculate(i, length, rt, frame)*hsvr));
            if (wrap) return Color.fromHSV((int) wrapClampHSV(r), (int) wrapClampHSV(g), (int) wrapClampHSV(b));
            return Color.fromHSV(r,g,b);
        }
        int r = (int) Math.round(mnr(red.calculate(i, length, rt, frame))/255d);
        int g = (int) Math.round(mnr(green.calculate(i, length, rt, frame))/255d);
        int b = (int) Math.round(mnr(blue.calculate(i, length, rt, frame))/255d);
        if (wrap) {
            return new Color(wrapClamp(r), wrapClamp(g), wrapClamp(b));
        }
        return new Color(r,g,b);
    }

    private float wrapClamp(float x) {
        if (x == 0) return 0;
        if (x < 0) return (int) ((x - 0.1) % 255);
        return (int) ((x - 0.1) % 255);
    }

    private float wrapClampHSV(float x) {
        if (x == 0) return 0;
        if (x < 0) return (x - 0.000001f) % 1;
        return (x - 0.000001f) % 1;
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
