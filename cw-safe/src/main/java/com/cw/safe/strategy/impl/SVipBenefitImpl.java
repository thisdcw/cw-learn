package com.cw.safe.strategy.impl;


import com.cw.safe.strategy.BenefitStrategy;
import com.cw.safe.strategy.SupportUserType;
import com.cw.safe.strategy.UserType;
import org.springframework.stereotype.Service;

/**
 * SVIP 用户权益
 *
 * @author thisdcw
 * @date 2025年06月26日 21:03
 */
@Service
@SupportUserType(UserType.SVIP_USER)
public class SVipBenefitImpl implements BenefitStrategy {
    @Override
    public String getBenefit() {
        return "SVIP 用户权益";
    }
}
