package com.github.flyhero.toolbox.json2pojo.process;

import com.github.flyhero.toolbox.json2pojo.entity.ClassEntity;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;

/**
 * Created by dim on 16/11/7.
 */
class FastJsonProcessor extends Processor {

    @Override
    public void onStarProcess(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls, IProcessor visitor) {
        super.onStarProcess(classEntity, factory, cls, visitor);
    }
}
