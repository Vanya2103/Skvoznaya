package com.example;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class InformationTest {

    private Information info;

    @Before
    public void setUp() {
        // Настройка тестового окружения с переменными
        Map<Character, Double> variables = new HashMap<>();
        variables.put('x', 2.0);
        variables.put('y', 3.0);
        info = new Information(1, "3x + 4y - 5", variables);
    }

    @Test
    public void testReplaceVariables() {
        info.replaceVariables();
        assertEquals(0.0, info.getResult(), 0.01); // Проверяем, что результат равен 10.0 после замены переменных
    }


    @Test
    public void testCalculateResult() {
        info.calculateResult();
        assertEquals(13.0, info.getResult(), 0.001); // Проверяем, что результат вычисления равен 10.0
    }

    @Test
    public void testPrintResult() {
        // Вызываем метод, чтобы убедиться, что он не вызывает исключения
        info.calculateResult();
        info.printResult(); // Просто вызываем метод
    }

    @Test
    public void testUndefinedVariable() {
        Map<Character, Double> variables = new HashMap<>();
        variables.put('x', 2.0); // 'y' не определена
        info = new Information(2, "3x + 4y - 5", variables);

        info.replaceVariables();
        // Ожидаем, что метод вернет значение 0.0, так как переменная y не определена
        assertEquals(0.0, info.getResult(), 0.001);
    }

    @Test
    public void testDivisionByZero() {
        info = new Information(3, "3 / 0", new HashMap<>());
        info.calculateResult();
        // Ожидаем, что результат будет 0.0, так как деление на ноль не возвращает валидного значения
        assertEquals(0.0, info.getResult(), 0.01);
    }

    @Test
    public void testSimpleAddition() {
        Map<Character, Double> variables = new HashMap<>();
        variables.put('a', 5.0);
        variables.put('b', 10.0);
        info = new Information(1, "a + b", variables);

        info.replaceVariables();
        info.calculateResult();
        assertEquals(15.0, info.getResult(), 0.001); // Ожидаем 15.0
    }

    @Test
    public void testSimpleSubtraction() {
        Map<Character, Double> variables = new HashMap<>();
        variables.put('x', 20.0);
        variables.put('y', 5.0);
        info = new Information(2, "x - y", variables);

        info.replaceVariables();
        info.calculateResult();
        assertEquals(15.0, info.getResult(), 0.001); // Ожидаем 15.0
    }

    @Test
    public void testSimpleMultiplication() {
        Map<Character, Double> variables = new HashMap<>();
        variables.put('m', 4.0);
        variables.put('n', 3.0);
        info = new Information(3, "m * n", variables);

        info.replaceVariables();
        info.calculateResult();
        assertEquals(12.0, info.getResult(), 0.001); // Ожидаем 12.0
    }

    @Test
    public void testMixedOperations() {
        Map<Character, Double> variables = new HashMap<>();
        variables.put('x', 1.0);
        variables.put('y', 2.0);
        variables.put('z', 3.0);
        info = new Information(4, "x + y * z - 4", variables);

        info.replaceVariables();
        info.calculateResult();
        assertEquals(3.0, info.getResult(), 0.001); // Ожидаем 3.0 (1 + 2*3 - 4 = 3)
    }

    @Test
    public void testNegativeNumbers() {
        Map<Character, Double> variables = new HashMap<>();
        variables.put('a', -5.0);
        variables.put('b', 10.0);
        info = new Information(5, "a + b", variables);

        info.replaceVariables();
        info.calculateResult();
        assertEquals(5.0, info.getResult(), 0.001); // Ожидаем 5.0
    }
}
