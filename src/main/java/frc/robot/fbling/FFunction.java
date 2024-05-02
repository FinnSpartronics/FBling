package frc.robot.fbling;

import static frc.robot.fbling.F.*;

public class FFunction {
    public final String function;
    
    public FFunction(String theFunction) {
        this.function = theFunction;
    }

    public double calculate(int i, int length, double rt, int frame) {
        String tmp = function;
        tmp = (tmp.replaceAll("i", Integer.toString(i)).replaceAll("f", Integer.toString(frame)).replaceAll("len", Integer.toString(length)).replaceAll("rt", Double.toString(rt)).replaceAll("t", Float.toString(frame/20)));
        tmp = F.convertBackToReal(tmp);
        try {
            var val = Double.parseDouble(F.js.eval(tmp).toString());
            return val;
        } catch (Exception e) {
            System.err.println("Parsing Error - Original: " + function + " - " + tmp);
        }
        return 0;
    }
}