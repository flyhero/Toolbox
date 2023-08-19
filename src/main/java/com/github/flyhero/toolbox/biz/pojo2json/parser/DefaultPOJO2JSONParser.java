package com.github.flyhero.toolbox.biz.pojo2json.parser;

import com.github.flyhero.toolbox.biz.pojo2json.parser.type.SpecifyType;

public class DefaultPOJO2JSONParser extends POJO2JSONParser {

    @Override
    protected Object getFakeValue(SpecifyType specifyType) {
        return specifyType.def();
    }
}
