package com.cw.framework.helper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author thisdcw
 * @date 2025年09月17日 10:12
 */
public class SaleManHelper {


    public static void main(String[] args) {

        LinkedList<SaleMan> chain = new LinkedList<>();
        SaleMan s1 = new SaleMan(1L, 2);
        SaleMan s2 = new SaleMan(2L, 3);
        SaleMan s3 = new SaleMan(3L, 4);
        SaleMan s4 = new SaleMan(4L, 5);
        SaleMan s5 = new SaleMan(5L, 5);
        SaleMan s6 = new SaleMan(6L, 6);
        chain.add(s1);
        chain.add(s2);
        chain.add(s3);
        chain.add(s4);
        chain.add(s5);
        chain.add(s6);

        long start = System.currentTimeMillis();
        //复制一份祖级,并且去掉子级和父级,从父级以上开始算
        LinkedList<SaleMan> parentSaleManChain = new LinkedList<>(chain.subList(1, chain.size()));
        //循环计算
        for (SaleMan parent : parentSaleManChain) {
            boolean b = canCalculateLevelDiff(chain, parent, s1);
            System.out.println(b);

            SaleMan suitableSaleMan = findSuitableSaleMan(chain, parent, s1);
            System.out.println(suitableSaleMan);
        }

        long end = System.currentTimeMillis();
        System.out.println("花费时间: " + (end - start) + " ms");

    }

    public static SaleMan findSuitableSaleMan(LinkedList<SaleMan> hierarchyChain, SaleMan takerSaleMan, SaleMan targetSaleMan) {
        if (hierarchyChain == null || hierarchyChain.isEmpty() || takerSaleMan == null || targetSaleMan == null) {
            return null;
        }

        if (takerSaleMan.getLevel() <= targetSaleMan.getLevel()) {
            return null;
        }

        // 构建层级映射，key为saleManId，value为在链表中的位置（深度）
        Map<Long, Integer> hierarchyDepthMap = new HashMap<>();
        for (int i = 0; i < hierarchyChain.size(); i++) {
            SaleMan saleMan = hierarchyChain.get(i);
            if (saleMan != null) {
                hierarchyDepthMap.put(saleMan.getId(), i);
            }
        }

        // 检查 takerSaleMan 和 targetSaleMan 是否都在层级链中
        Integer takerDepth = hierarchyDepthMap.get(takerSaleMan.getId());
        Integer targetDepth = hierarchyDepthMap.get(targetSaleMan.getId());

        // 确保 taker 在 target 的上级链路中（深度更大意味着层级更高/更远离子级）
        if (takerDepth <= targetDepth) {
            return null;
        }

        // 寻找合适的 SaleMan
        // 从 target 开始往上级方向查找，直到 taker 的位置
        SaleMan suitableSaleMan = targetSaleMan;
        int highestLevel = targetSaleMan.getLevel();

        // 遍历从 target 到 taker 之间的所有层级
        for (int i = targetDepth + 1; i < takerDepth; i++) {
            SaleMan currentSaleMan = hierarchyChain.get(i);
            if (currentSaleMan != null) {
                // 检查当前节点的职级是否不低于 taker 的职级
                if (currentSaleMan.getLevel() >= takerSaleMan.getLevel()) {
                    // 如果存在职级不低于 taker 的中间节点，则不符合计算条件
                    return null;
                }

                // 寻找职级最高的节点作为合适的计算对象
                if (currentSaleMan.getLevel() > highestLevel) {
                    highestLevel = currentSaleMan.getLevel();
                    suitableSaleMan = currentSaleMan;
                }
            }
        }

        return suitableSaleMan;
    }

    /**
     * 判断拿薪资的销售员是否能计算目标销售员的级差薪资
     *
     * @param saleManChain  销售员链条（从子级到祖级排序）
     * @param takerSaleMan  拿薪资的销售员
     * @param targetSaleMan 计算目标销售员
     * @return true表示可以计算级差薪资
     */
    public static boolean canCalculateLevelDiff(LinkedList<SaleMan> saleManChain,
                                                SaleMan takerSaleMan,
                                                SaleMan targetSaleMan) {
        // 参数校验
        if (saleManChain == null || saleManChain.isEmpty() ||
                takerSaleMan == null || targetSaleMan == null) {
            return false;
        }

        // 找到两个销售员在链条中的位置
        int takerIndex = findSaleManIndex(saleManChain, takerSaleMan);
        int targetIndex = findSaleManIndex(saleManChain, targetSaleMan);

        // 如果任一销售员不在链条中，返回false
        if (takerIndex == -1 || targetIndex == -1) {
            return false;
        }

        // 拿薪资的人必须在更高层级（索引更大）
        if (takerIndex <= targetIndex) {
            return false;
        }

        // 拿薪资的人职级必须大于目标人职级
        if (takerSaleMan.getLevel() <= targetSaleMan.getLevel()) {
            return false;
        }

        // 检查两者之间是否存在职级大于等于拿薪资的人的销售员
        for (int i = targetIndex + 1; i < takerIndex; i++) {
            SaleMan middleSaleMan = saleManChain.get(i);
            if (middleSaleMan.getLevel() >= takerSaleMan.getLevel()) {
                // 中间有更高或同等职级的人，不能越级拿薪资
                return false;
            }
        }

        return true;
    }

    /**
     * 找到销售员在链条中的索引位置
     *
     * @param saleManChain 销售员链条
     * @param saleMan      要查找的销售员
     * @return 索引位置，找不到返回-1
     */
    private static int findSaleManIndex(LinkedList<SaleMan> saleManChain, SaleMan saleMan) {
        for (int i = 0; i < saleManChain.size(); i++) {
            SaleMan current = saleManChain.get(i);
            if (current.getId().equals(saleMan.getId())) {
                return i;
            }
        }
        return -1;
    }
}
