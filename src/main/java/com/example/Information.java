package com.example;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Information {
    private int taskNumber; // Номер задачи
    private String equation; // Исходное уравнение
    private String equationProcessed; // Уравнение без переменных
    private Map<Character, Double> variables; // Переменные и их значения
    private double result; // Результат вычисления
    private boolean solvable; // Флаг решаемости

    // Конструктор
    public Information(int taskNumber, String equation, Map<Character, Double> variables) {
        this.taskNumber = taskNumber;
        this.equation = equation;
        this.variables = variables;
        this.result = 0.0;
        this.equationProcessed = equation;
        this.solvable = true;
    }

    // Замена переменных в уравнении
    public void replaceVariables() {
        // Регулярное выражение для поиска переменных
        Pattern variablePattern = Pattern.compile("(-?\\d*\\.?\\d*)([A-Za-z])");
        Matcher matcher = variablePattern.matcher(equationProcessed);

        while (matcher.find()) {
            String coefficient = matcher.group(1).isEmpty() ? "1" : matcher.group(1);
            char variable = matcher.group(2).charAt(0);

            if (!variables.containsKey(variable)) {
                solvable = false;
                equationProcessed = "Undefined variable: " + variable;
                return;
            }

            double value = Double.parseDouble(coefficient) * variables.get(variable);
            equationProcessed = matcher.replaceFirst(Double.toString(value));
            matcher = variablePattern.matcher(equationProcessed); // Обновляем matcher
        }
    }

    // Упрощение выражения (с обработкой скобок)
    public String simplifyExpression(String expression) {
        try {
            // Упрощение выражений в скобках
            Pattern bracketsPattern = Pattern.compile("\\(([^()]+)\\)");
            Matcher matcher = bracketsPattern.matcher(expression);

            while (matcher.find()) {
                String innerExpression = matcher.group(1);
                String simplified = evaluateOperations(innerExpression);
                expression = matcher.replaceFirst(simplified);
                matcher = bracketsPattern.matcher(expression);
            }

            // Финальная обработка оставшегося выражения
            return evaluateOperations(expression);
        } catch (ArithmeticException e) {
            solvable = false;
            return e.getMessage();
        }
    }

    // Вычисление операций (умножение, деление, сложение, вычитание, степени)
    private String evaluateOperations(String expression) {
        // Обработка степеней
        Pattern powerPattern = Pattern.compile("(-?\\d+\\.?\\d*)\\s*\\^\\s*(-?\\d+\\.?\\d*)");
        Matcher matcher = powerPattern.matcher(expression);

        while (matcher.find()) {
            double base = Double.parseDouble(matcher.group(1));
            double exponent = Double.parseDouble(matcher.group(2));
            double value = Math.pow(base, exponent);
            expression = matcher.replaceFirst(Double.toString(value));
            matcher = powerPattern.matcher(expression);
        }

        // Обработка умножения и деления
        Pattern mulDivPattern = Pattern.compile("(-?\\d+\\.?\\d*)\\s*([*/])\\s*(-?\\d+\\.?\\d*)");
        matcher = mulDivPattern.matcher(expression);

        while (matcher.find()) {
            double left = Double.parseDouble(matcher.group(1));
            double right = Double.parseDouble(matcher.group(3));
            if (matcher.group(2).equals("/") && right == 0) {
                throw new ArithmeticException("Division by zero");
            }
            double value = matcher.group(2).equals("*") ? left * right : left / right;
            expression = matcher.replaceFirst(Double.toString(value));
            matcher = mulDivPattern.matcher(expression);
        }

        // Обработка сложения и вычитания
        Pattern addSubPattern = Pattern.compile("(-?\\d+\\.?\\d*)\\s*([+-])\\s*(-?\\d+\\.?\\d*)");
        matcher = addSubPattern.matcher(expression);

        while (matcher.find()) {
            double left = Double.parseDouble(matcher.group(1));
            double right = Double.parseDouble(matcher.group(3));
            double value = matcher.group(2).equals("+") ? left + right : left - right;
            expression = matcher.replaceFirst(Double.toString(value));
            matcher = addSubPattern.matcher(expression);
        }

        return expression;
    }

    // Основная логика вычисления
    public void calculateResult() {
        this.equationProcessed = this.equation; // Копируем исходное уравнение
        replaceVariables(); // Заменяем переменные

        if (!solvable) {
            return; // Если переменные не найдены, прекращаем вычисление
        }

        String resultString = simplifyExpression(equationProcessed); // Упрощаем выражение

        try {
            this.result = Double.parseDouble(resultString); // Преобразуем результат в число
        } catch (NumberFormatException e) {
            solvable = false;
            equationProcessed = "Error: Invalid expression";
        }
    }

    // Печать результата
    public void printResult() {
        if (!solvable) {
            System.out.println("Task " + taskNumber + ": " + equation);
            System.out.println("Error: " + equationProcessed);
        } else {
            System.out.println("Task " + taskNumber + ": " + equation);
            System.out.println("Result: " + result);
        }
    }

    public double getResult() {
        return result;
    }

    public int getTaskNumber() {
        return taskNumber;
    }
}
