package com.github.flyhero.toolbox.model;


import java.util.Objects;

import com.google.common.base.CaseFormat;

public class Field {

    public String name;

    private String type;
    //单行注释
    private String comment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field field = (Field) o;
        return Objects.equals(name, field.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }


    public static Field newField(String name, String type, String comment) {
        return new Field(name, type, comment);
    }
    public static Field newField(String name, String type) {
        return new Field(name, type);
    }

    public String getTableColumn() {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this.name);
    }

    public Field() {
    }

    public Field(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public Field(String name, String type, String comment) {
        this.name = name;
        this.type = type;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return name + "\t\t\t\t"  + type;
    }
}
