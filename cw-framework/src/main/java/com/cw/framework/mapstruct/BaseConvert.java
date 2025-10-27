package com.cw.framework.mapstruct;

/**
 * @author thisdcw
 * @date 2025年10月27日 17:53
 */
public interface BaseConvert<E, V> {

    V toVo(E user);

}
