package com.cw.safe.spel;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author thisdcw
 * @date 2025年05月21日 9:49
 */
public class Main {

    @Data
    @AllArgsConstructor
    public static class Person {
        private String name;
    }

    public static void main(String[] args) {
        // 2. 创建 SpelExpressionParser 实例
        ExpressionParser parser = new SpelExpressionParser();

        // 3. 解析表达式
        Expression exp = parser.parseExpression("name.toUpperCase()");

        // 4. 创建你的根对象实例
        Person person = new Person("alice");

        // 5. 创建 EvaluationContext 并设置根对象
        StandardEvaluationContext context = new StandardEvaluationContext(person);

        // 6. 评估表达式
        // 指定期望的返回类型 (可选但推荐)
        String result = exp.getValue(context, String.class);

        // 打印结果
        System.out.println("原始名称: " + person.getName());
        System.out.println("表达式评估结果: " + result); // 输出: ALICE

        // --- 另一个不同名称的例子 ---
        person.setName("bob");
        String anotherResult = exp.getValue(context, String.class);
        System.out.println("新名称: " + person.getName());
        System.out.println("表达式评估结果: " + anotherResult); // 输出: BOB

        // --- 没有根对象的例子 (如果你想评估字面量或简单表达式) ---
        Expression literalExp = parser.parseExpression("'Hello' + ' SpEL'");
        String literalResult = literalExp.getValue(String.class);
        // 输出: Hello SpEL
        System.out.println("字面量表达式结果: " + literalResult);

    }
}
