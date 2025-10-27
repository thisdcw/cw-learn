package com.cw.framework.mapstruct;

import com.cw.framework.mapstruct.domain.Tag;
import com.cw.framework.mapstruct.domain.User;
import com.cw.framework.mapstruct.vo.TagVO;
import com.cw.framework.mapstruct.vo.UserVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author thisdcw
 * @date 2025年10月27日 14:46
 */
@Mapper
public interface TagConvert {

    TagVO toVo(Tag tag);

    List<TagVO> toVoList(List<Tag> tag);

}
