package com.github.flyhero.toolbox.utils;

import com.intellij.lang.jvm.types.JvmPrimitiveTypeKind;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiLiteralExpression;

public class PsiFieldUtils {

	private static final String QUOT = "&quot;";


	/**
	 * 获取当前类的所有字段(包括父类)
	 *
	 * @param psiClass
	 * @return
	 */
	public static PsiField[] getAllFields(PsiClass psiClass) {
		if (null == psiClass) {
			return new PsiField[0];
		}
		return psiClass.getAllFields();
	}

	/**
	 * 获取当前类的所有字段
	 *
	 * @param psiClass
	 * @return
	 */
	public static PsiField[] getFields(PsiClass psiClass) {
		if (null == psiClass) {
			return new PsiField[0];
		}
		return psiClass.getFields();
	}

	/**
	 * 是否基础类型, 实验性API，慎用
	 *
	 * @param field
	 * @return
	 */
//	public static boolean isPrimitiveType(PsiField field) {
//		if (null == field) {
//			return false;
//		}
//
//		String canonicalText = field.getType().getCanonicalText();
//		JvmPrimitiveTypeKind kindByName = JvmPrimitiveTypeKind.getKindByName(canonicalText);
//		if (null != kindByName) {
//			return true;
//		}
//
//		JvmPrimitiveTypeKind kindByFqn = JvmPrimitiveTypeKind.getKindByFqn(canonicalText);
//		return null != kindByFqn;
//	}

	/**
	 * 去除字符首尾 "" 标记，转换成常规字符串
	 * <pre>
	 *     复制于PsiLiteralUtil.getStringLiteralContent
	 *     为了消除高版本因为API变动，IDEA版本校验的警告
	 * </pre>
	 *
	 * @param expression
	 * @return
	 */
	public static String getStringLiteralContent(PsiLiteralExpression expression) {
		String text = expression.getText();
		int textLength = text.length();
		if (textLength > 1 && text.charAt(0) == '\"' && text.charAt(textLength - 1) == '\"') {
			return text.substring(1, textLength - 1);
		}
		if (textLength > QUOT.length() && text.startsWith(QUOT) && text.endsWith(QUOT)) {
			return text.substring(QUOT.length(), textLength - QUOT.length());
		}
		return null;
	}
}
