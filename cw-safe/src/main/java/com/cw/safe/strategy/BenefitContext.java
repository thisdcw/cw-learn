package com.cw.safe.strategy;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author thisdcw
 * @date 2025年06月26日 21:01
 */
@Service
@Slf4j
public class BenefitContext {

    private final Map<UserType, BenefitStrategy> handlerMap;

    @Autowired
    public BenefitContext(List<BenefitStrategy> benefitStrategies) {
        this.handlerMap = benefitStrategies.stream()
                .filter(s -> s.getClass().isAnnotationPresent(SupportUserType.class))
                .collect(Collectors.toMap(this::getBenefitStrategy, Function.identity()));
        log.info("加载策略: {}", handlerMap);
    }

    private UserType getBenefitStrategy(BenefitStrategy benefitStrategy) {
        SupportUserType supportUserType = benefitStrategy.getClass().getAnnotation(SupportUserType.class);
        return supportUserType.value();
    }

    public BenefitStrategy getBenefitStrategy(UserType userType) {
        return handlerMap.get(userType);
    }

}
