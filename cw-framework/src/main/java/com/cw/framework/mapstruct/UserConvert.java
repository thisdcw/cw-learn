package com.cw.framework.mapstruct;

import com.cw.framework.mapstruct.domain.Tag;
import com.cw.framework.mapstruct.domain.User;
import com.cw.framework.mapstruct.vo.UserVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author thisdcw
 * @date 2025年10月27日 14:46
 */
@Mapper(uses = {TagConvert.class})
public interface UserConvert extends BaseConvert<User, UserVO> {

    UserVO toVo(User user, Tag tags);

    UserVO toVo(User user, List<Tag> tagList);
}
