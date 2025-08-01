package com.cw.framework.finance;


import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author thisdcw
 * @date 2025年07月31日 22:45
 */
public class TreeConverter {

    /**
     * 路径数据节点
     */
    public static class DataNode {
        //父级用户id
        private Long parentId;
        //子级用户id
        private Long childId;
        //父级到子级的深度差
        private Integer depth;
        //下级节点
        private DataNode children;

        // 构造函数
        public DataNode() {
        }

        public DataNode(Long parentId, Long childId, Integer depth) {
            this.parentId = parentId;
            this.childId = childId;
            this.depth = depth;
        }

        // Getter和Setter
        public Long getParentId() {
            return parentId;
        }

        public void setParentId(Long parentId) {
            this.parentId = parentId;
        }

        public Long getChildId() {
            return childId;
        }

        public void setChildId(Long childId) {
            this.childId = childId;
        }

        public Integer getDepth() {
            return depth;
        }

        public void setDepth(Integer depth) {
            this.depth = depth;
        }

        public DataNode getChildren() {
            return children;
        }

        public void setChildren(DataNode children) {
            this.children = children;
        }

        @Override
        public String toString() {
            return "DataNode{parentId=" + parentId + ", childId=" + childId +
                    ", depth=" + depth + ", hasChildren=" + (children != null) + "}";
        }
    }

    /**
     * 将路径数据转换为链式结构
     *
     * @param pathData      路径数据列表，格式：parentId->childId, depth表示层级差
     * @param targetChildId 目标子级ID，构建到该子级的链路
     * @param maxDepth      最大深度限制，只保留depth小于maxDepth的路径
     * @return 构建的链式结构根节点，如果没有找到则返回null
     */
    public static DataNode buildChainToChild(List<DataNode> pathData, Long targetChildId, Integer maxDepth) {
        if (pathData == null || pathData.isEmpty() || targetChildId == null) {
            return null;
        }

        // 1. 筛选出指向目标子级的路径数据，并按深度限制过滤
        List<DataNode> targetPaths = pathData.stream()
                .filter(node -> targetChildId.equals(node.getChildId()))
                .filter(node -> maxDepth == null || node.getDepth() < maxDepth)
                .collect(Collectors.toList());

        if (targetPaths.isEmpty()) {
            return null;
        }

        // 2. 按深度降序排序，最远的祖先在前
        targetPaths.sort((a, b) -> Integer.compare(b.getDepth(), a.getDepth()));

        // 3. 构建链式结构
        DataNode rootNode = null;
        DataNode currentNode = null;

        for (DataNode pathNode : targetPaths) {
            // 创建新节点，表示从parent到child的连接
            DataNode newNode = new DataNode(pathNode.getParentId(), pathNode.getChildId(), pathNode.getDepth());

            if (rootNode == null) {
                // 第一个节点作为根节点
                rootNode = newNode;
                currentNode = newNode;
            } else {
                // 将新节点作为当前节点的子节点
                // 需要调整新节点，让它表示正确的父子关系
                DataNode chainNode = new DataNode(currentNode.getChildId(), pathNode.getParentId(), pathNode.getDepth() - 1);
                currentNode.setChildren(chainNode);
                currentNode = chainNode;
            }
        }

        return rootNode;
    }

    /**
     * 构建完整的链式结构（更准确的实现）
     *
     * @param pathData      路径数据列表
     * @param targetChildId 目标子级ID
     * @param maxDepth      最大深度限制
     * @return 链式结构根节点
     */
    public static DataNode buildChain(List<DataNode> pathData, Long targetChildId, Integer maxDepth) {
        if (pathData == null || pathData.isEmpty() || targetChildId == null) {
            return null;
        }

        // 1. 筛选目标子级的所有父级路径
        List<DataNode> targetPaths = pathData.stream()
                .filter(node -> targetChildId.equals(node.getChildId()))
                .filter(node -> maxDepth == null || node.getDepth() < maxDepth)
                .collect(Collectors.toList());

        if (targetPaths.isEmpty()) {
            return null;
        }

        // 2. 按深度降序排序，构建从最远祖先到目标子级的链路
        targetPaths.sort((a, b) -> Integer.compare(b.getDepth(), a.getDepth()));

        // 3. 构建节点ID的有序列表
        List<Long> chainIds = new ArrayList<>();
        for (DataNode pathNode : targetPaths) {
            if (!chainIds.contains(pathNode.getParentId())) {
                chainIds.add(pathNode.getParentId());
            }
        }
        chainIds.add(targetChildId);

        // 4. 构建链式结构
        DataNode rootNode = null;
        DataNode currentNode = null;

        for (int i = 0; i < chainIds.size() - 1; i++) {
            Long parentId = chainIds.get(i);
            Long childId = chainIds.get(i + 1);

            // 计算深度（从原始数据中查找）
            Integer depth = targetPaths.stream()
                    .filter(node -> parentId.equals(node.getParentId()))
                    .map(DataNode::getDepth)
                    .findFirst()
                    .orElse(i);

            DataNode newNode = new DataNode(parentId, childId, depth);

            if (rootNode == null) {
                rootNode = newNode;
                currentNode = newNode;
            } else {
                currentNode.setChildren(newNode);
                currentNode = newNode;
            }
        }

        return rootNode;
    }

    /**
     * 将路径数据转换为链式结构（无深度限制）
     *
     * @param pathData      路径数据列表
     * @param targetChildId 目标子级ID
     * @return 链式结构根节点
     */
    public static DataNode buildChain(List<DataNode> pathData, Long targetChildId) {
        return buildChain(pathData, targetChildId, null);
    }

    /**
     * 打印链式结构
     *
     * @param rootNode 根节点
     */
    public static void printChain(DataNode rootNode) {
        DataNode current = rootNode;
        List<String> chain = new ArrayList<>();

        while (current != null) {
            chain.add("[ " + current.getParentId() + "->" + current.getChildId() + "(depth:" + current.getDepth() + ")" + " ]");
            current = current.getChildren();
        }

        System.out.println("Chain: " + String.join(" -> ", chain));
    }

    /**
     * 获取链路中的所有用户ID
     *
     * @param rootNode 根节点
     * @return 用户ID列表
     */
    public static List<Long> getChainUserIds(DataNode rootNode) {
        List<Long> userIds = new ArrayList<>();
        DataNode current = rootNode;

        if (current != null) {
            userIds.add(current.getParentId());
            while (current != null) {
                userIds.add(current.getChildId());
                current = current.getChildren();
            }
        }

        return userIds;
    }

    // 测试方法
    public static void main(String[] args) {
        // 创建测试数据: A->D深度3, B->D深度2, C->D深度1
        // 表示链路: A->B->C->D
        List<DataNode> pathData = Arrays.asList(
                // A->D, depth=3
                new DataNode(1L, 4L, 3),
                // B->D, depth=2
                new DataNode(2L, 4L, 2),
                // C->D, depth=1
                new DataNode(3L, 4L, 1)
        );

        System.out.println("原始路径数据:");
        pathData.forEach(System.out::println);

        System.out.println("\n构建到D(4)的完整链路:");
        DataNode fullChain = buildChain(pathData, 4L);
        if (fullChain != null) {
            printChain(fullChain);
            System.out.println("链路用户IDs: " + getChainUserIds(fullChain));
        }

        System.out.println("\n限制深度小于2的链路:");
        DataNode limitedChain = buildChain(pathData, 4L, 2);
        if (limitedChain != null) {
            printChain(limitedChain);
            System.out.println("链路用户IDs: " + getChainUserIds(limitedChain));
        }

        System.out.println("\n限制深度小于1的链路:");
        DataNode veryLimitedChain = buildChain(pathData, 4L, 1);
        if (veryLimitedChain != null) {
            printChain(veryLimitedChain);
            System.out.println("链路用户IDs: " + getChainUserIds(veryLimitedChain));
        } else {
            System.out.println("没有满足条件的链路");
        }

        // 测试另一个目标
        System.out.println("\n=== 测试复杂场景 ===");
        List<DataNode> complexData = Arrays.asList(
                // A->E, depth=4
                new DataNode(1L, 5L, 4),
                // B->E, depth=3
                new DataNode(2L, 5L, 3),
                // C->E, depth=2
                new DataNode(3L, 5L, 2),
                // D->E, depth=1
                new DataNode(4L, 5L, 1),
                // A->C, depth=2 (验证用)
                new DataNode(1L, 3L, 2),
                // B->C, depth=1 (验证用)
                new DataNode(2L, 3L, 1)
        );

        System.out.println("构建到E(5)的完整链路:");
        DataNode complexChain = buildChain(complexData, 5L);
        if (complexChain != null) {
            printChain(complexChain);
        }
    }
}
