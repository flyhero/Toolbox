package com.github.flyhero.toolbox.entity2constant.service;

import com.github.flyhero.toolbox.common.AbstractFieldParser;
import com.github.flyhero.toolbox.common.BaseUtil;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;

import static com.github.flyhero.toolbox.entity2constant.constant.Constant.SERIAL_VERSION_UID;

public class StaticConstantFieldParser extends AbstractFieldParser {
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
		if (psiField.hasModifierProperty(PsiModifier.STATIC) || psiField.hasModifierProperty(PsiModifier.TRANSIENT)) {
			return false;
		}
		// 是否基础数据类型
		if (BaseUtil.isPrimitiveType(psiField)) {
			return true;
		}
		// 额外可支持的类型
		String canonicalText = psiField.getType().getCanonicalText();
		if (isAdditional(canonicalText)) {
			return true;
		}
		return false;
	}
}
