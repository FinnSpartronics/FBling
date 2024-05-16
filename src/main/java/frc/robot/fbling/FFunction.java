package frc.robot.fbling;

import static frc.robot.fbling.F.*;

import java.util.List;

public class FFunction {
    public final List<String> function;
    
    public FFunction(String theFunction) {
        this.function = FEval.postfixBreakdown(FEval.toPostFix(theFunction));
    }

    public double calculate(int i, int length, double rt, int frame) {
        List<String> l = function;
        l = FEval.setVar(l, 'i', i);
        l = FEval.setVar(l, "len", length);
        l = FEval.setVar(l, "rt", rt);
        l = FEval.setVar(l, "t", frame/20d);
        l = FEval.setVar(l, "f", frame);
        return FEval.eval(l);
    }
}