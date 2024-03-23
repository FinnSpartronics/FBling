package frc.robot.fbling;

public class FGotoSegment extends FSegment {
    public final int gotof;

    public FGotoSegment(int nstartframe, int ngotof) {
        super(nstartframe);
        this.gotof = ngotof;
    }
}
