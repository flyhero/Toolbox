package com.github.flyhero.toolbox.biz.pojo2json.parser.type;

public class BooleanType implements SpecifyType {

    @Override
    public Object def() {
        return false;
    }

    @Override
    public Object random() {
        return Boolean.TRUE;
    }
}
