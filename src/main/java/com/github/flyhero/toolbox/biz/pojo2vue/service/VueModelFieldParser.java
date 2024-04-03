package com.github.flyhero.toolbox.biz.pojo2vue.service;

import com.github.flyhero.toolbox.common.AbstractFieldParser;
import com.github.flyhero.toolbox.utils.JavaLangUtils;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;

import static com.github.flyhero.toolbox.constant.Constant.SERIAL_VERSION_UID;


public class VueModelFieldParser extends AbstractFieldParser {
	@Override
	protected boolean isNeedAddConvert(PsiField psiField) {
		if (null == psiField) {
			return false;
		}
		// 忽略 serialVersionUID
		if (SERIAL_VERSION_UID.equals(psiField.getName())) {
			return false;
		}
		// 忽略静态变量和非序列化的
		if (psiField.hasModifierProperty(PsiModifier.STATIC) || psiField.hasModifierProperty(PsiModifier.TRANSIENT)
				|| psiField.hasModifierProperty(PsiModifier.FINAL)) {
			return false;
		}
		// 额外可支持的类型
		return true;
	}
}
