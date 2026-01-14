package com.cw.demo.finance;


import com.cw.demo.finance.entity.UserLevel;
import com.cw.demo.finance.entity.UserNode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @author thisdcw
 * @date 2025年07月31日 21:19
 */
public class SalaryCalculator {

    /**
     * 计算整个树结构的薪资
     * @param root 根节点
     */
    public void calculateAllSalary(UserNode root) {
        // 递归计算每个节点的薪资
        calculateNodeSalary(root, root);
    }

    /**
     * 计算单个节点的薪资
     * @param node 当前节点
     * @param root 根节点
     */
    private void calculateNodeSalary(UserNode node, UserNode root) {
        if (node == null) {
            return;
        }

        BigDecimal totalBenefit = BigDecimal.ZERO;

        // 1. 个人薪资: 业绩performance * 职级对应的提成占比
        BigDecimal personalSalary = calculatePersonalSalary(node);
        totalBenefit = totalBenefit.add(personalSalary);

        // 2. 辅导薪资: 直属下级业绩performance * 0.02
        BigDecimal guidanceSalary = calculateGuidanceSalary(node);
        totalBenefit = totalBenefit.add(guidanceSalary);

        // 3. 级差薪资: 自己到该下级作为一个链路,取除自己外最高级的下级级对应的比例 * 该下级的业绩
        BigDecimal levelDiffSalary = calculateLevelDiffSalary(node);
        totalBenefit = totalBenefit.add(levelDiffSalary);

        // 4. 平级薪资: 下级发展超过自己,获得其薪资的10%
        BigDecimal peerSalary = calculatePeerSalary(node);
        totalBenefit = totalBenefit.add(peerSalary);

        // 设置总薪资
        node.setBenefit(totalBenefit.setScale(2, RoundingMode.HALF_UP));

        // 递归计算下级薪资
        if (node.getChildren() != null) {
            calculateNodeSalary(node.getChildren(), root);
        }
    }

    /**
     * 1. 计算个人薪资: 业绩performance * 职级对应的提成占比
     */
    private BigDecimal calculatePersonalSalary(UserNode node) {
        if (node.getPerformance() == null || node.getLevel() == null) {
            return BigDecimal.ZERO;
        }

        double radio = UserLevel.getRadioByLevel(node.getLevel());
        return node.getPerformance().multiply(BigDecimal.valueOf(radio));
    }

    /**
     * 2. 计算辅导薪资: 直属下级业绩performance * 0.02
     */
    private BigDecimal calculateGuidanceSalary(UserNode node) {
        if (node.getChildren() == null) {
            return BigDecimal.ZERO;
        }

        return node.getChildren().getPerformance()
                .multiply(BigDecimal.valueOf(0.02));
    }

    /**
     * 3. 计算级差薪资: 自己到该下级作为一个链路,取除自己外最高级的下级级对应的比例 * 该下级的业绩
     * 注意: 链路中如果有某个成员的level高于根节点成员的level,则忽略该成员往后的级差效益
     */
    private BigDecimal calculateLevelDiffSalary(UserNode node) {
        BigDecimal totalLevelDiff = BigDecimal.ZERO;

        // 获取到每个下级节点的所有路径
        List<UserNode> allSubordinates = getAllSubordinates(node);

        for (UserNode subordinate : allSubordinates) {
            // 获取从当前节点到该下级的路径
            List<UserNode> pathToSubordinate = getPathToNode(node, subordinate);

            if (pathToSubordinate.size() <= 1) continue; // 至少要有2个节点才能计算级差

            // 找到路径中除自己外最高级的下级
            int maxLevel = 0;
            boolean hasHigherLevel = false;

            // 检查路径中是否有level高于当前节点的成员
            for (int i = 1; i < pathToSubordinate.size(); i++) {
                UserNode pathNode = pathToSubordinate.get(i);

                if (pathNode.getLevel() > node.getLevel()) {
                    hasHigherLevel = true;
                    break;
                }

                // 更新路径中最高级别(排除当前节点自己)
                if (pathNode.getLevel() > maxLevel) {
                    maxLevel = pathNode.getLevel();
                }
            }

            // 如果路径中没有更高级别的成员,计算级差薪资
            if (!hasHigherLevel && maxLevel > 0) {
                double currentRadio = UserLevel.getRadioByLevel(node.getLevel());
                double maxRadio = UserLevel.getRadioByLevel(maxLevel);
                double diffRadio = currentRadio - maxRadio;

                if (diffRadio > 0) {
                    BigDecimal levelDiff = subordinate.getPerformance()
                            .multiply(BigDecimal.valueOf(diffRadio));
                    totalLevelDiff = totalLevelDiff.add(levelDiff);
                }
            }
        }

        return totalLevelDiff;
    }

    /**
     * 4. 计算平级薪资: 下级发展超过自己,获得其薪资的10%
     */
    private BigDecimal calculatePeerSalary(UserNode node) {
        BigDecimal totalPeerSalary = BigDecimal.ZERO;

        // 获取所有下级节点
        List<UserNode> allSubordinates = getAllSubordinates(node);

        for (UserNode subordinate : allSubordinates) {
            // 检查该下级是否level高于当前节点
            if (subordinate.getLevel() > node.getLevel()) {
                // 获取从当前节点到该下级的路径
                List<UserNode> pathToSubordinate = getPathToNode(node, subordinate);

                // 检查路径中是否在该下级之前就有更高级别的节点
                boolean hasHigherLevelBefore = false;
                for (int i = 1; i < pathToSubordinate.size() - 1; i++) {
                    if (pathToSubordinate.get(i).getLevel() > node.getLevel()) {
                        hasHigherLevelBefore = true;
                        break;
                    }
                }

                // 如果在该下级之前没有更高级别的节点,则可以获得平级薪资
                if (!hasHigherLevelBefore) {
                    BigDecimal personalSalary = calculatePersonalSalary(subordinate);
                    BigDecimal peerSalary = personalSalary.multiply(BigDecimal.valueOf(0.1));
                    totalPeerSalary = totalPeerSalary.add(peerSalary);
                }
            }
        }

        return totalPeerSalary;
    }

    /**
     * 获取所有下级节点
     */
    private List<UserNode> getAllSubordinates(UserNode node) {
        List<UserNode> subordinates = new ArrayList<>();
        collectSubordinates(node, subordinates);
        return subordinates;
    }

    /**
     * 递归收集所有下级节点
     */
    private void collectSubordinates(UserNode node, List<UserNode> subordinates) {
        if (node.getChildren() != null) {
            subordinates.add(node.getChildren());
            collectSubordinates(node.getChildren(), subordinates);
        }
    }

    /**
     * 获取从起始节点到目标节点的路径
     */
    private List<UserNode> getPathToNode(UserNode start, UserNode target) {
        List<UserNode> path = new ArrayList<>();
        if (findPathToNode(start, target, path)) {
            return path;
        }
        return new ArrayList<>();
    }

    /**
     * 递归查找路径
     */
    private boolean findPathToNode(UserNode current, UserNode target, List<UserNode> path) {
        path.add(current);

        if (current.getUserId().equals(target.getUserId())) {
            return true;
        }

        if (current.getChildren() != null) {
            if (findPathToNode(current.getChildren(), target, path)) {
                return true;
            }
        }

        path.remove(path.size() - 1); // 回溯
        return false;
    }

    /**
     * 打印薪资明细(用于调试)
     */
    public void printSalaryDetails(UserNode node) {
        if (node == null) return;

        // 计算各项薪资明细
        BigDecimal personalSalary = calculatePersonalSalary(node);
        BigDecimal guidanceSalary = calculateGuidanceSalary(node);
        BigDecimal levelDiffSalary = calculateLevelDiffSalary(node);
        BigDecimal peerSalary = calculatePeerSalary(node);

        System.out.println("=== " + node.getUserName() + " 薪资明细 ===");
        System.out.println("等级: " + node.getLevel() + " 业绩: " + node.getPerformance());
        System.out.println("个人薪资: " + personalSalary);
        System.out.println("辅导薪资: " + guidanceSalary);
        System.out.println("级差薪资: " + levelDiffSalary);
        System.out.println("平级薪资: " + peerSalary);
        System.out.println("总薪资: " + node.getBenefit());
        System.out.println();

        if (node.getChildren() != null) {
            printSalaryDetails(node.getChildren());
        }
    }

}
