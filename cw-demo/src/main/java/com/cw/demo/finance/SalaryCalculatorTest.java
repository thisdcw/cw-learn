package com.cw.demo.finance;


import com.cw.demo.finance.entity.UserNode;

import java.math.BigDecimal;

/**
 * @author thisdcw
 * @date 2025年07月31日 21:19
 */
public class SalaryCalculatorTest {

    public static void main(String[] args) {
        // 创建测试数据: A->B->C->D
        UserNode nodeA = new UserNode();
        nodeA.setUserId(1L);
        nodeA.setUserName("A");
        nodeA.setLevel(4); // 最高级
        nodeA.setDepth(0);
        nodeA.setPerformance(new BigDecimal("10000"));

        UserNode nodeB = new UserNode();
        nodeB.setUserId(2L);
        nodeB.setUserName("B");
        nodeB.setLevel(3);
        nodeB.setDepth(1);
        nodeB.setPerformance(new BigDecimal("8000"));

        UserNode nodeC = new UserNode();
        nodeC.setUserId(3L);
        nodeC.setUserName("C");
        nodeC.setLevel(2);
        nodeC.setDepth(2);
        nodeC.setPerformance(new BigDecimal("6000"));

        UserNode nodeD = new UserNode();
        nodeD.setUserId(4L);
        nodeD.setUserName("D");
        nodeD.setLevel(1);
        nodeD.setDepth(3);
        nodeD.setPerformance(new BigDecimal("5000"));

        // 构建树结构
        nodeA.setChildren(nodeB);
        nodeB.setChildren(nodeC);
        nodeC.setChildren(nodeD);

        // 计算薪资
        SalaryCalculator calculator = new SalaryCalculator();
        calculator.calculateAllSalary(nodeA);

        // 打印结果
        calculator.printSalaryDetails(nodeA);
    }

}
