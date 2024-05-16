package frc.robot.fbling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;
import static java.lang.Math.*;

public class FEval {
    public static void main(String[] args) {
        List<String> list = postfixBreakdown(toPostFix("x+y*z"));
        list = setVar(list, 'x', 5);
        list = setVar(list, 'y', 2);
        list = setVar(list, 'z', 3);
        System.out.println(eval(list));
       
        System.out.println(eval("10%4"));
    }

    public static final List<String> functions = List.of("sin,asin,cos,acos,tan,atan,dtr,rtd,exp,log10,sq,cb,sqrt,cbrt,ceil,floor,round,abs".split(","));

    private static int precedence(String operator) {
        switch(operator) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
            case "%":
                return 2;
            case "^": return 3;
            default: return 4;
        }
    }

    public static String toPostFix(String infix) {
        infix = infix.replaceAll("\\(-", "(0-");

        ArrayList<String> arr = new ArrayList<>();
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; i < infix.length(); i++) {
            if (Pattern.compile("[+\\-*/()^% ]").matcher(String.valueOf(infix.charAt(i))).matches()) {
                if (!tmp.toString().trim().equals("")) arr.add(tmp.toString());
                if (infix.charAt(i) != ' ') arr.add(String.valueOf(infix.charAt(i)));
                tmp = new StringBuilder();
            } else tmp.append(infix.charAt(i));
        }
        if (!tmp.toString().equals("")) arr.add(tmp.toString());

        Stack<String> stack = new Stack<>();
        StringBuilder postfix = new StringBuilder();

        for (int i = 0; i <= arr.size()-1; i++) {
            if (Pattern.compile("[+\\-*/^%]").matcher(arr.get(i)).matches() || functions.contains(arr.get(i))) {
                if (stack.size() == 0 || stack.contains("(")) stack.push(arr.get(i));
                else if (precedence(stack.get(stack.size()-1)) < precedence(arr.get(i))) stack.push(arr.get(i));
                else if (precedence(stack.get(stack.size()-1)) == precedence(arr.get(i))) {
                    postfix.append(stack.pop()).append(' ');
                    stack.push(arr.get(i));
                }
                else {
                    int p = precedence(arr.get(i));
                    while (true) {
                        if (stack.size() > 0)
                            if (precedence(stack.get(stack.size()-1)) >= p) postfix.append(stack.pop()).append(' ');
                            else break;
                        else break;
                    }
                    stack.push(arr.get(i));
                }
            }
            else if (arr.get(i).equals("(")) stack.push("(");
            else if (arr.get(i).equals(")")) {
                while (true) {
                    if (!stack.get(stack.size()-1).equals("(")) postfix.append(stack.pop()).append(' ');
                    else {
                        stack.pop();
                        break;
                    }
                }
            }
            else postfix.append(arr.get(i)).append(' ');
        }

        while(stack.size() > 0) postfix.append(stack.pop()).append(' ');

        return postfix.toString().trim();
    }

    public static List<String> postfixBreakdown(String str) {
        List<String> arr = (List<String>) Arrays.asList(str.split(" "));
        return arr;
    }
   
    public static List<String> setVar(List<String> list, String var, String val) {
        while (list.indexOf(var) != -1) list.set(list.indexOf(var), val);
        return list;
    }
    public static List<String> setVar(List<String> list, char var, String val) {
        while (list.indexOf(""+var) != -1) list.set(list.indexOf(""+var), val);
        return list;
    }
    public static List<String> setVar(List<String> list, String var, double val) {
        while (list.indexOf(var) != -1) list.set(list.indexOf(var), ""+val);
        return list;
    }
    public static List<String> setVar(List<String> list, char var, double val) {
        while (list.indexOf(""+var) != -1) list.set(list.indexOf(""+var), ""+val);
        return list;
    }

    public static double eval(List<String> postfixArr) {
        Stack<Object> stack = new Stack();
        for(int i = 0; i <= postfixArr.size()-1; i++) {
            try {
                stack.push(Double.parseDouble(postfixArr.get(i)));
            } catch (Exception ignored) {
                char tmp = ((String) (postfixArr.get(i))).charAt(0);
                if ((42 <= tmp && 47 >= tmp && tmp != 46) || tmp == 94 || tmp == 37) {
                    double a = (Double) stack.get(stack.size() - 2);
                    double b = (Double) stack.get(stack.size() - 1);
                    stack.pop(); stack.pop();
                    switch (tmp) {
                        case '+': stack.push(a+b); break;
                        case '-': stack.push(a-b); break;
                        case '*': stack.push(a*b); break;
                        case '/': stack.push(a/b); break;
                        case '^': stack.push(pow(a,b)); break;
                        case '%': stack.push(a%b); break;
                    }
                } else {
                    if (functions.contains(postfixArr.get(i))) {
                        double a = (double) stack.get(stack.size()-1);
                        stack.pop();
                        stack.push(evaluateFunction(postfixArr.get(i), a));
                    }
                    else {
                        throw new EvaluationException("Unexpected item " + postfixArr.get(i));
                    }
                }
            }
            //console.log(stack)
        }
        return (double) stack.get(0);
    }

    public static double eval(String infix) {
        return eval(postfixBreakdown(toPostFix(infix)));
    }

    public static double eval_(String postfix) {
        return eval(postfixBreakdown(postfix));
    }

    public static double evaluateFunction(String function, double x) {
        switch(function) {
            // Trigonometry Functions
            case "sin": return sin(x);
            case "asin": return asin(x);
            case "sinh": return sinh(x);
            case "cos": return cos(x);
            case "acos": return acos(x);
            case "cosh": return cosh(x);
            case "tan": return tan(x);
            case "atan": return atan(x);
            case "tanh": return tanh(x);
            case "dtr": return toRadians(x);
            case "rtd": return toDegrees(x);

            // Exponents and such
            case "exp": return exp(x);
            case "log": return log(x);
            case "log10": return log10(x);
            case "log1p": return log1p(x);

            case "sq": return pow(x,2);
            case "cb": return pow(x,3);
            case "sqrt": return sqrt(x);
            case "cbrt": return cbrt(x);
           
            case "pow4": return pow(x,4);
            case "pow5": return pow(x,5);
            case "pow6": return pow(x,6);

            // Rounding functions
            case "ceil": return ceil(x);
            case "floor": return floor(x);
            case "round": return round(x);
            case "round0": return round(x);
            case "round1": return round(x*10)/10;
            case "round2": return round(x*100)/100;
            case "round3": return round(x*1000)/1000;

            // Other Functions
            case "abs": return abs(x);
            case "sign": return signum(x);
            case "rand":
            case "random": return random();
        }
        throw new EvaluationException("Unknown function " + function + x);
    }
}

class EvaluationException extends RuntimeException {
    public EvaluationException() {
        super("An unexpected error has occurred during evaluation");
    }
    public EvaluationException(String message) {
        super("An unexpected error has occurred during evaluation: " + message);
    }
}