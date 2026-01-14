package com.cw.demo.finance.entity;


import lombok.Data;

import java.math.BigDecimal;

/**
 * 假设:
 * A邀请B,B邀请C,C邀请D,则组成上下级链路A->B->C->D,上下级不代表职级
 * <p>
 * 薪资计算:
 * 1: 个人薪资: 业绩performance * 职级对应的提成占比
 * 2: 辅导薪资: 直属下级业绩performance * 0.02
 * 3: 级差薪资: 自己到该下级作为一个链路,取除自己外最高级的下级级对应的比例 * 该下级的业绩,例如A到下级E的链路:A->B->C->D->E,取B的比例与A的比例之差 * E的业绩
 * 4: 平级薪资: 下级发展超过自己,获得其薪资的10%
 * 注意: 链路中如果有某个成员的level高于根节点成员的level,则忽略该成员往后的级差效益,只能获取该成员薪资的10%
 *
 * @author thisdcw
 * @date 2025年07月31日 20:14
 */
@Data
public class UserNode {


    //用户id
    private Long userId;

    //用户名
    private String userName;

    //用户等级
    private Integer level;

    //节点深度,0为根节点
    private Integer depth;

    //业绩
    private BigDecimal performance;

    //薪资
    private BigDecimal benefit;

    //下级
    private UserNode children;
}
