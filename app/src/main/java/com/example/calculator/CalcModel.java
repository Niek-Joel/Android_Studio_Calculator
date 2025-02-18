package com.example.calculator;

import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Map;

public class CalcModel {
    private static final int ROUND_CONSTANT = 10;  // Round a little smaller here so phones can use app in portrait
    private static final MathContext ROUND_CONSTANT_MATH = new MathContext(10, RoundingMode.HALF_UP);
    public static final String TAG = "Model";
    private String display;  // Shows output buffer (Can be LHS, RHS, or result)
    private String result;  // Actual calculation answer
    private String leftOperand, rightOperand;
    private InputEnum operator;
    private CalculatorState state;
    protected PropertyChangeSupport propertyChangeSupport;
    private static final Map<String/*Button Tag*/, InputEnum/*EnumEquivalent*/> tagToEnum = Map.ofEntries(
            Map.entry("btnPlus", InputEnum.PLUS),
            Map.entry("btnMinus", InputEnum.MINUS),
            Map.entry("btnMultiply", InputEnum.MULTIPLY),
            Map.entry("btnDivide", InputEnum.DIVIDE),
            Map.entry("btnSqrt", InputEnum.SQRT),
            Map.entry("btnPercent", InputEnum.PERCENT),
            Map.entry("btnChangeSign", InputEnum.CHANGE_SIGN),
            Map.entry("btnClear", InputEnum.CLEAR),
            Map.entry("btnEquals", InputEnum.EQUALS),
            Map.entry("btnDecimal", InputEnum.DECIMAL));

    public CalcModel() {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void initDefault() {
        setLeftOperand("0");
        setRightOperand("");
        setOperator(null);
        setResult("0");
        setState(CalculatorState.CLEAR);
        Log.i(TAG, "InitDefault(): Clear state with null values.");
    }

    public void stateMachine(String tag) {
        // Decipher input
        InputEnum input = null;
        Integer digitInput = null;
        if (tagToEnum.containsKey(tag)) {// Is non number key
            input = tagToEnum.get(tag);
        }
        else {// Is a number key
            try {
                digitInput = Integer.parseInt(tag.replaceAll("[^0-9]",""));
            }
            catch (NumberFormatException e){
                Log.i(TAG,"Error when parsing int out of button tag.");
            }
        }

        // Is core
        boolean isCoreOp = false;
        if (input == InputEnum.PLUS ||
            input == InputEnum.MINUS ||
            input == InputEnum.MULTIPLY ||
            input == InputEnum.DIVIDE) {
            isCoreOp = true;
        }

        // State machine transitions
        if (this.state == CalculatorState.CLEAR) {
            if (input != null) { // Input isn't a number
                if (isCoreOp) {
                    setState(CalculatorState.RHS);
                    setLeftOperand("0");
                    setOperator(input);
                }
                else if (input == InputEnum.CLEAR ||
                         input == InputEnum.EQUALS ||
                         input == InputEnum.CHANGE_SIGN ||
                         input == InputEnum.SQRT ||
                         input == InputEnum.PERCENT) {
                    initDefault(); // returns to CLEAR state
                }
                else if (input == InputEnum.DECIMAL) {
                    setState(CalculatorState.LHS);
                    tryAddDecimal();
                }
            }
            else { // Input is a number
                setState(CalculatorState.LHS);
                StringBuilder sb = new StringBuilder();
                sb.append(digitInput);
                setLeftOperand(sb.toString());
            }
        }
        else if (this.state == CalculatorState.LHS) {
            if (input != null) { // Input isn't a number
                if (input == InputEnum.CHANGE_SIGN) {
                    StringBuilder sb = new StringBuilder(leftOperand);
                    if (sb.charAt(0) == '-') {
                        sb.deleteCharAt(0);
                    }
                    else {
                        sb.insert(0, '-');
                    }
                    setLeftOperand(sb.toString());
                }
                else if (input == InputEnum.CLEAR || input == InputEnum.PERCENT) {
                    setState(CalculatorState.CLEAR);
                    initDefault(); // Clear state
                }
                else if (input == InputEnum.SQRT) {
                    boolean calculationFailed = calculateResult(input);
                    if  (!calculationFailed) {
                        setState(CalculatorState.RESULT);
                    }
                }
                else if (input == InputEnum.DECIMAL) {
                    tryAddDecimal();
                }
                else if (isCoreOp) {
                    setOperator(input);
                    setState(CalculatorState.OP_SCHEDULED);
                }
            }
            else { // Input is a number
                StringBuilder sb = new StringBuilder(leftOperand);
                sb.append(digitInput);
                setLeftOperand(sb.toString());
            }
        }
        else if (this.state == CalculatorState.OP_SCHEDULED) {
            if (input != null) { // Input isn't a number
                if (isCoreOp) {
                    setOperator(input);
                }
                else if (input == InputEnum.DECIMAL) {
                    setState(CalculatorState.RHS);
                    tryAddDecimal();
                }
                else if (input == InputEnum.PERCENT || input == InputEnum.SQRT) {
                    boolean failedCalculation = calculateResult(input);
                    if (!failedCalculation) {
                        setState(CalculatorState.RHS);
                    }
                }
                else if (input == InputEnum.CHANGE_SIGN) {
                    StringBuilder sb = new StringBuilder(leftOperand);
                    if (sb.charAt(0) == '-') {
                        sb.deleteCharAt(0);
                    }
                    else {
                        sb.insert(0, '-');
                    }
                    setRightOperand(sb.toString());
                    setState(CalculatorState.RHS);
                }
                else if (input == InputEnum.CLEAR) {
                    initDefault();
                }
                else if (input == InputEnum.EQUALS) { // Operator should still be old value. So it will keep doing last operation each time "=" is hit
                    setRightOperand(leftOperand);
                    boolean failedCalculation = calculateResult(input);
                    if (!failedCalculation) {  // This is really only necessary to check if the user tries to hit "/" + "=" when left operand is zero.
                        setState(CalculatorState.RESULT);
                    }
                }
            }
            else { // Input is a number
                setState(CalculatorState.RHS);
                StringBuilder sb = new StringBuilder(rightOperand);
                sb.append(digitInput);
                setRightOperand(sb.toString());
            }
        }
        else if (this.state == CalculatorState.RHS) {
            if (input != null) { // Input isn't a number
                if (isCoreOp || input == InputEnum.EQUALS) {
                    boolean calculationFailed = calculateResult(operator);
                    if (input != InputEnum.EQUALS) {
                        setOperator(input);
                    }
                    if (!calculationFailed) {
                        setState(CalculatorState.RESULT);
                    }
                }
                else if  (input == InputEnum.CLEAR) {
                    initDefault();
                }
                else if  (input == InputEnum.DECIMAL) {
                    tryAddDecimal();
                }
                else if  (input == InputEnum.PERCENT || input == InputEnum.SQRT) {
                    calculateResult(input);
                }
                else if (input == InputEnum.CHANGE_SIGN) {
                    StringBuilder sb = new StringBuilder(rightOperand);
                    if (sb.charAt(0) == '-') {
                        sb.deleteCharAt(0);
                    }
                    else {
                        sb.insert(0, '-');
                    }
                    setRightOperand(sb.toString());
                }
            }
            else { // Input is a number
                StringBuilder sb = new StringBuilder(rightOperand);
                sb.append(digitInput);
                setRightOperand(sb.toString());
            }
        }
        else if (this.state == CalculatorState.RESULT) {
            if (input != null) { // Input isn't a number
                if (isCoreOp) {
                    setOperator(input);
                    setLeftOperand(result);
                    setRightOperand("");
                    setState(CalculatorState.OP_SCHEDULED);
                }
                else if (input == InputEnum.EQUALS) {
                    // operator stays the same
                    setLeftOperand(result);
                    // Right operand stays the same
                    calculateResult(operator);
                    // State stays in Result
                }
                else if (input == InputEnum.PERCENT || input == InputEnum.SQRT) {
                    calculateResult(input);
                }
                else if (input == InputEnum.CLEAR) {
                    initDefault();
                }
            }
            else { // Input is a number
                StringBuilder sb = new StringBuilder();
                sb.append(digitInput);
                setRightOperand(sb.toString());
                setState(CalculatorState.RHS);
            }
        }
        else if (this.state == CalculatorState.ERROR) {
            if (input != null) { // Input isn't a number
                // Ignore everything but Clear and equals
                if (input == InputEnum.CLEAR || input == InputEnum.EQUALS) {
                    initDefault();
                }
            }
            else { // Input is a number
                StringBuilder sb = new StringBuilder();
                sb.append(digitInput);
                setLeftOperand(sb.toString());
                setState(CalculatorState.LHS);
            }
        }
    }

    // Doesn't set state
    private void tryAddDecimal() {
        if (state == CalculatorState.LHS && !leftOperand.contains(".")) {
            StringBuilder sb = new StringBuilder(leftOperand);
            sb.append(".");
            setLeftOperand(sb.toString());
        }
        else if (state == CalculatorState.RHS && !rightOperand.contains(".")) {
            StringBuilder sb = new StringBuilder(rightOperand);
            sb.append(".");
            setRightOperand(sb.toString());
        }
    }

    // Doesn't set state unless it's an erroneous calculation entry. (Ex. 8/0 or sqrt(-5))
    // Returns boolean true if calculateResult() succeeds. Returns false if there was an error during computation
    private boolean calculateResult(InputEnum scheduledOP) {
        boolean failedCalculation = false;
        try {
            BigDecimal left = null;
            BigDecimal right = null;
            BigDecimal result;
            if (!leftOperand.isBlank()) {
                left = new BigDecimal(leftOperand);
            }
            if (!rightOperand.isBlank()) {
                right = new BigDecimal(rightOperand);
            }
            if (scheduledOP == InputEnum.PLUS) {
                result = left.add(right);
                setLeftOperand(result.toString());
                setResult(result.toString());
            }
            else if (scheduledOP == InputEnum.MINUS) {
                result = left.subtract(right);
                setLeftOperand(result.toString());
                setResult(result.toString());
            }
            else if (scheduledOP == InputEnum.MULTIPLY) {
                result = left.multiply(right);
                setLeftOperand(result.toString());
                setResult(result.toString());
            }
            else if (scheduledOP == InputEnum.DIVIDE) {
                if (!right.equals(BigDecimal.ZERO)) {
                    result = left.divide(right, ROUND_CONSTANT_MATH);
                    setLeftOperand(result.toString());
                    setResult(result.toString());
                }
                else {// Special error case: divide by zero
                    setState(CalculatorState.ERROR);
                    setDisplay("Can't Divide by 0");
                    failedCalculation = true;
                }
            }
            else if (scheduledOP == InputEnum.SQRT) {
                Pair<BigDecimal, Boolean> pair = calculateSubResult(InputEnum.SQRT, left, right);
                BigDecimal subResult = pair.first;
                failedCalculation = pair.second;
                if (!failedCalculation) {
                    setResult(subResult.toString());
                }
            }
            else if (scheduledOP == InputEnum.PERCENT) {
                Pair<BigDecimal, Boolean> pair = calculateSubResult(InputEnum.PERCENT, left, right);
                BigDecimal subResult = pair.first;
                failedCalculation = pair.second;
                if (!failedCalculation) {
                    setResult(subResult.toString());
                }
            }
        }
        catch (Exception e) {
        Log.i(TAG, "Calculation Error");
        }
        return failedCalculation;
    }

    // Helper for calculateResult()
    // For results within one of the two operands. Only necessary through use of sqrt or percent operations
    private Pair<BigDecimal, Boolean> calculateSubResult(InputEnum input, BigDecimal left, BigDecimal right) {
        BigDecimal result = null;
        boolean failedCalculation = false;
        if (state == CalculatorState.LHS || state == CalculatorState.RESULT) { // Percent in LHS defaults to 0 so no need to compute
            if (left.compareTo(BigDecimal.ZERO) > 0) {
                double d = left.doubleValue(); // Can't use BigDecimal.sqrt() because this is on android API 22
                double r = Math.sqrt(d);
                result = BigDecimal.valueOf(r);
                setLeftOperand(result.toString());
            }
            else {// Special error: sqrt of a negative number
                setState(CalculatorState.ERROR);
                setDisplay("Invalid Square");
                failedCalculation = true;
            }
        }
        else if (state == CalculatorState.RHS || state == CalculatorState.OP_SCHEDULED) {
            // If user tries to schedule a sqrt or percent directly after an operator, it is assumed that the right operand is the same as the left.
            if (state == CalculatorState.OP_SCHEDULED) {
                right = left;
            }

            if (input == InputEnum.SQRT) {
                if (right.compareTo(BigDecimal.ZERO) > 0) {
                    double d = right.doubleValue(); // Can't use BigDecimal.sqrt() because this is on android API 22
                    double r = Math.sqrt(d);
                    result = BigDecimal.valueOf(r);
                    setRightOperand(result.toString());
                }
                else {// Special error: sqrt of a negative number
                    setState(CalculatorState.ERROR);
                    setDisplay("Invalid Square");
                    failedCalculation = true;
                }
            }
            else if (input == InputEnum.PERCENT) {
                BigDecimal percent = right.divide(BigDecimal.valueOf(100));
                result = percent.multiply(left).round(ROUND_CONSTANT_MATH);
                setRightOperand(result.toString());
            }
        }
        return new Pair<>(result, failedCalculation);
    }



    // Setters
    public void setDisplay(@NonNull String newDisplay) {
        String oldDisplay = this.display;
        this.display = newDisplay;
        Log.i(TAG, "Display changed: From " + oldDisplay + " to " + newDisplay);

        propertyChangeSupport.firePropertyChange(CalcPresenter.ELEMENT_OUTPUT_PROPERTY, oldDisplay, newDisplay);
    }
    public void setResult(String newResult) {
        String oldResult = this.result;
        this.result = newResult;
        Log.i(TAG, "Result changed: From " + oldResult + " to " + newResult);

        // Rounding
        try {
            if (newResult.length() > ROUND_CONSTANT) {
                BigDecimal num = new BigDecimal(newResult).round(ROUND_CONSTANT_MATH);
                newResult = num.toString();
            }
        }
        catch (Exception e) {
            Log.i(TAG, "Error during rounding process");
        }

        // Also setting the output to whatever newResult is
        setDisplay(newResult);
    }
    public void setLeftOperand(String newLeftOperand) {
        String oldLeftOperand = this.leftOperand;
        this.leftOperand = newLeftOperand;
        Log.i(TAG, "Left Operand changed: From " + oldLeftOperand + " to " + newLeftOperand);
        setDisplay(newLeftOperand);
    }
    public void setRightOperand(String newRightOperand) {
        String oldRightOperand = this.rightOperand;
        this.rightOperand = newRightOperand;
        Log.i(TAG, "Right Operand changed: From " + oldRightOperand + " to " + newRightOperand);
        setDisplay(newRightOperand);
    }
    public void setOperator(InputEnum newOperator) {
        // You may have to drop operator
        InputEnum oldOperator = this.operator;
        this.operator = newOperator;
        Log.i(TAG, "Operator changed: From " + oldOperator + " to " + newOperator);
    }
    public void setState(CalculatorState newState) {
        CalculatorState oldState = this.state;
        this.state = newState;
        Log.i(TAG, "State changed: From " + oldState + " to " + newState);
    }

    // Getters
    public String getLeftOperand() {return leftOperand;}
    public String getDisplay() {return display;}
    public String getRightOperand() {return rightOperand;}
    public InputEnum getOperator() {return operator;}
    public CalculatorState getState() {return state;}


    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public String toString() {
        return "Model{" +
                "state=" + state.toString() +
                ", operator='" + operator + '\'' +
                ", rightOperand=" + rightOperand.toString() +
                ", leftOperand=" + leftOperand.toString() +
                ", output='" + display + '\'' +
                '}';
    }
}
