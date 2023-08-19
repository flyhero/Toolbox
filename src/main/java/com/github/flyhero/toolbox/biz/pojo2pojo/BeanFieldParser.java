package com.github.flyhero.toolbox.biz.pojo2pojo;

import java.util.Objects;

import com.github.flyhero.toolbox.common.AbstractFieldParser;
import com.github.flyhero.toolbox.utils.JavaLangUtils;
import com.github.flyhero.toolbox.utils.PsiFieldUtils;
import com.intellij.psi.PsiField;

import static com.github.flyhero.toolbox.constant.Constant.SERIAL_VERSION_UID;


public class BeanFieldParser extends AbstractFieldParser {
	@Override
	protected boolean isNeedAddConvert(PsiField psiField) {
		if (null == psiField) {
			return false;
		}
		// 忽略 serialVersionUID
		if (SERIAL_VERSION_UID.equals(psiField.getName())) {
			return false;
		}
		// 忽略静态变量
		if (JavaLangUtils.isStaticModifier(psiField)) {
			return false;
		}
		// 是否基础数据类型
		if (JavaLangUtils.isJavaBaseType(psiField.getType())) {
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
