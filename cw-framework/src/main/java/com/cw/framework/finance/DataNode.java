package com.cw.framework.finance;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author thisdcw
 * @date 2025年07月31日 22:39
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class DataNode {

    //用户id
    private Long parentId;

    private Long childId;

    //节点深度,parent到child的深度
    private Integer depth;

    private DataNode children;
}
