package com.cw.framework.mapstruct;

import com.cw.framework.mapstruct.domain.Tag;
import com.cw.framework.mapstruct.domain.User;
import com.cw.framework.mapstruct.vo.UserVO;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

/**
 * @author thisdcw
 * @date 2025年10月27日 14:45
 */
public class Main {
    public static void main(String[] args) {

        UserConvert userConvert = Mappers.getMapper(UserConvert.class);

        User u = new User();
        u.setId(1L);
        u.setName("张三");
        u.setAge(18);

        Tag tag = new Tag();
        tag.setTagName("技术");
        tag.setSlug("tech");

        List<Tag> tagList = new ArrayList<>();
        tagList.add(tag);
        tagList.add(tag);


        long start = System.currentTimeMillis();
        UserVO vo = userConvert.toVo(u, tagList);
        long end = System.currentTimeMillis();
        System.out.println("mapstruct use time: " + (end - start) + "ms");
        System.out.println(vo);


    }
}
