package frc.robot.fbling;

public abstract class FSegment {
    public final int startFrame;

    public FSegment(int nstartframe) {
        this.startFrame = nstartframe;
    }

    public String toString() {
        return this.startFrame + "";
    }
}
