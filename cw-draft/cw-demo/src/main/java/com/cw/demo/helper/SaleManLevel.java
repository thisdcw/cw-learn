package com.cw.demo.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

/**
 * @author thisdcw
 * @date 2025年07月30日 11:14
 */
@AllArgsConstructor
@Getter
public enum SaleManLevel {

    JUNIOR_SPECIALIST(1, "初级专员", 0.06, (train) -> true, (store) -> true, (kpi) -> true, (kpi) -> true),
    SENIOR_SPECIALIST(2, "高级专员", 0.11, (train) -> true, (store) -> store >= 10 && store < 100, (kpi) -> kpi.compareTo(BigDecimal.valueOf(3000)) >= 0, (kpi) -> kpi.compareTo(BigDecimal.valueOf(10000)) >= 0),
    JUNIOR_MANAGER(3, "初级经理", 0.13, (train) -> train >= 2, (store) -> store >= 100 && store < 1000, (kpi) -> true, (kpi) -> kpi.compareTo(BigDecimal.valueOf(80000)) >= 0),
    SENIOR_MANAGER(4, "高级经理", 0.15, (train) -> train >= 2, (store) -> store >= 1000 && store < 10000, (kpi) -> true, (kpi) -> kpi.compareTo(BigDecimal.valueOf(500000)) >= 0),
    BUSINESS_DIRECTOR(5, "商务总监", 0.17, (train) -> train >= 2, (store) -> store >= 10000 && store < 50000, (kpi) -> true, (kpi) -> kpi.compareTo(BigDecimal.valueOf(2000000)) >= 0),
    DIRECTOR(6, "董事", 0.20, (train) -> train >= 3, (store) -> store >= 50000, (kpi) -> true, (kpi) -> kpi.compareTo(BigDecimal.valueOf(10000000)) >= 0);

    //等级
    private final int level;

    //商务名
    private final String name;

    //薪资比例
    private final double ratio;
    //培养标准
    private final IntPredicate train;

    //发展商家数量标准
    private final IntPredicate scope;

    //个人业绩标准le
    private final Predicate<BigDecimal> personalKpi;

    //团队业绩标准
    private final Predicate<BigDecimal> teamKpi;

    /**
     * 是否达标
     *
     * @param train       培养数量
     * @param scope       商家数量
     * @param personalKpi 个人业绩
     * @param teamKpi     团队业绩
     * @return 是否达标
     */
    public boolean isAlready(int train, int scope, BigDecimal personalKpi, BigDecimal teamKpi) {
        return this.train.test(train) &&
                this.scope.test(scope) &&
                this.personalKpi.test(personalKpi) &&
                this.teamKpi.test(teamKpi);
    }

    //获取下一级商务
    public SaleManLevel next() {
        SaleManLevel[] vals = values();
        int idx = this.ordinal() + 1;
        return idx < vals.length ? vals[idx] : null;
    }


    public static SaleManLevel getByLevel(int level) {
        for (SaleManLevel sl : values()) {
            if (sl.level == level) {
                return sl;
            }
        }
        return JUNIOR_SPECIALIST;
    }

    // 是否为经理级及以上（可获得平级薪资）
    public boolean isManagerOrAbove() {
        return this.level >= JUNIOR_MANAGER.level;
    }

}
