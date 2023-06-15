package com.github.flyhero.toolbox.json2pojo.entity;

import com.github.flyhero.toolbox.json2pojo.common.CheckUtil;
import com.github.flyhero.toolbox.json2pojo.common.StringUtils;
import com.github.flyhero.toolbox.json2pojo.tools.json.JSONObject;
import com.github.flyhero.toolbox.json2pojo.tools.ux.CellProvider;
import com.github.flyhero.toolbox.json2pojo.tools.ux.Selector;
import com.intellij.psi.PsiClass;
import org.apache.http.util.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dim on 2015/7/15.
 */
public class ClassEntity implements Selector, CellProvider {

    private PsiClass psiClass;

    /**
     * 字段类型后缀
     */
    private String fieldTypeSuffix;

    /**
     * 类名描述
     */
    private String classDesc;

    /**
     * 类名
     */
    private String className;
    /**
     * 字段
     */
    private List<FieldEntity> fields = new ArrayList<>();
    /**
     * 内部类
     */
    private List<ClassEntity> innerClasses = new ArrayList<>();
    /**
     * 包名
     */
    private String packName;
    /**
     * 存储 comment
     */
    private String extra;

    private boolean generate = true;

    private boolean lock = false;

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public boolean isGenerate() {
        return generate;
    }

    public void setGenerate(boolean generate) {
        this.generate = generate;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public void addAllFields(List fields) {
        this.fields.addAll(fields);
    }

    public void addField(FieldEntity fieldEntity) {
        this.fields.add(fieldEntity);
    }

    public void addInnerClass(ClassEntity classEntity) {
        this.innerClasses.add(classEntity);
    }

    public List<ClassEntity> getInnerClasses() {
        return innerClasses;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public String getFieldTypeSuffix() {
        return fieldTypeSuffix;
    }

    public void setFieldTypeSuffix(String fieldTypeSuffix) {
        this.fieldTypeSuffix = fieldTypeSuffix;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = CheckUtil.getInstant().handleArg(className);
    }

    public List<? extends FieldEntity> getFields() {
        return fields;
    }

    public PsiClass getPsiClass() {
        return psiClass;
    }

    public void setPsiClass(PsiClass psiClass) {
        this.psiClass = psiClass;
    }

    public String getQualifiedName() {
        String fullClassName;
        if (!TextUtils.isEmpty(packName)) {
            fullClassName = packName + "." + className;
        } else {
            fullClassName = className;
        }

        return fullClassName;
    }

    @Override
    public void setSelect(boolean select) {
        setGenerate(select);
    }

    public boolean isSame(JSONObject o) {
        if (o == null) {
            return false;
        }
        boolean same = true;
        for (String key : o.keySet()) {
            same = false;
            for (FieldEntity field : fields) {
                if (field.getKey().equals(key)) {
                    if (field.isSameType(o.get(key))) {
                        same = true;
                    }
                    break;
                }
            }
            if (!same) {
                break;
            }
        }
        return same;
    }


    @Override
    public String getCellTitle(int index) {
        String result = "";
        switch (index) {
            case 0:
                result = getClassName();
                break;
            case 3:
                result = getClassName();
                break;
            case 4:
                result = getClassDesc();
                break;
            default:
                break;
        }
        return result;
    }

    @Override
    public void setValueAt(int column, String text) {
        switch (column) {
            case 2:
                break;
            case 3:
                String result;
                if (!TextUtils.isEmpty(fieldTypeSuffix)) {
                    result = fieldTypeSuffix + "." + text;
                } else {
                    result = text;
                }
                if (CheckUtil.getInstant().containsDeclareClassName(result)) {
                    return;
                }
                CheckUtil.getInstant().removeDeclareClassName(getQualifiedName());
                setClassName(text);
                break;
            case 4:
                if (StringUtils.isNotBlank(text)) {
                    setClassDesc(text);
                }
            default:
                break;
        }
    }

    public String getClassDesc() {
        return classDesc;
    }

    public void setClassDesc(String classDesc) {
        this.classDesc = classDesc;
    }
}
