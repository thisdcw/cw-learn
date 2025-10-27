package com.cw.framework.mapstruct.vo;

import lombok.Data;

import java.util.List;

/**
 * @author thisdcw
 * @date 2025年10月27日 14:44
 */
@Data
public class UserVO {

    private Integer id;

    private String name;

    private Integer age;

//    private TagVO tags;

    private List<TagVO> tagList;

}
