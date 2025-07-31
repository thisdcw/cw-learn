package com.cw.safe.strategy.impl;


import com.cw.safe.strategy.BenefitStrategy;
import com.cw.safe.strategy.SupportUserType;
import com.cw.safe.strategy.UserType;
import org.springframework.stereotype.Service;

/**
 * 默认权益
 *
 * @author thisdcw
 * @date 2025年06月26日 21:03
 */
@Service
@SupportUserType(UserType.DEFAULT)
public class DefaultBenefitImpl implements BenefitStrategy {
    @Override
    public String getBenefit() {
        return "默认权益";
    }
}
