package com.github.flyhero.toolbox.biz.pojo2json.parser.type;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.RandomUtils;

public class DecimalType implements SpecifyType {


    @Override
    public Object def() {
        return BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY);
    }

    @Override
    public Object random() {
        return BigDecimal.valueOf(RandomUtils.nextDouble(0, 100)).setScale(2, RoundingMode.DOWN);
    }

}
