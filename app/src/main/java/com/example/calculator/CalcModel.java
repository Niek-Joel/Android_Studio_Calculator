package com.example.calculator;

import android.util.Log;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;

public class CalcModel {
    public static final String TAG = "Model";
    private String output;
    private String expression;
    private String leftOperand, rightOperand;
    private OperatorEnum operator;
    private CalculatorState state;
    protected PropertyChangeSupport propertyChangeSupport;

    public CalcModel() {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void initDefault() {
        output = "0";
        leftOperand = null;
        rightOperand = null;
        operator = null;
        state = CalculatorState.CLEAR;
    }


    public void addDigit(int digit){
        // TODO State machine
    }

    public void addDecimal(){
        // TODO State machine
    }










    // Setters
    public void setOutput(String newOutput) {
        String oldOutput = this.output;
        this.output = newOutput;
        Log.i(TAG, "Output changed: From " + oldOutput + " to " + newOutput);

        propertyChangeSupport.firePropertyChange(CalcPresenter.ELEMENT_OUTPUT_PROPERTY, oldOutput, newOutput);
    }
    public void setLeftOperand(String newLeftOperand) {
        String oldLeftOperand = this.leftOperand;
        this.leftOperand = newLeftOperand;
        Log.i(TAG, "Left Operand changed: From " + leftOperand + " to " + newLeftOperand);
    }
    public void setRightOperand(String newRightOperand) {
        String oldRightOperand = this.rightOperand;
        this.rightOperand = newRightOperand;
        Log.i(TAG, "Right Operand changed: From " + oldRightOperand + " to " + newRightOperand);
    }
    public void setOperator(OperatorEnum newOperator) {
        // You may have to drop operator
        OperatorEnum oldOperator = this.operator;
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
    public String getOutput() {return output;}
    public String getRightOperand() {return rightOperand;}
    public OperatorEnum getOperator() {return operator;}
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
                ", output='" + output + '\'' +
                '}';
    }
}
