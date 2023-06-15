package com.github.flyhero.toolbox.json2pojo.common;

import com.github.flyhero.toolbox.json2pojo.entity.ClassEntity;
import com.github.flyhero.toolbox.json2pojo.entity.FieldEntity;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;

/**
 * @author wangzejun
 * @description javaDoc注释工具
 * @date 2020年07月03日 19:10:14
 */
public class JavaDocUtils {

	/**
	 * 增加类上注释
	 *
	 * @param psiClass
	 * @param classEntity
	 * @param factory
	 */
	public static void addClassComment(PsiClass psiClass, ClassEntity classEntity, PsiElementFactory factory) {
		StringBuffer javadoc = new StringBuffer();
		javadoc.append("/**\n");
		if (StringUtils.isNotBlank(classEntity.getClassDesc())) {
			javadoc.append("* ").append(classEntity.getClassDesc());
		} else {
			javadoc.append("* ").append(classEntity.getClassName());
		}
		javadoc.append("\n");
		javadoc.append("*/");
		PsiComment comment = factory.createCommentFromText(javadoc.toString(), null);
		psiClass.addBefore(comment, psiClass.getFirstChild());
	}

	/**
	 * 增加字段注释
	 *
	 * @param field
	 * @param fieldEntity
	 * @param factory
	 */
	public static void addFieldComment(PsiField field, FieldEntity fieldEntity, PsiElementFactory factory) {
		StringBuffer javadocField = new StringBuffer();
		javadocField.append("/**\n");
		if (StringUtils.isNotBlank(fieldEntity.getFieldComment())) {
			javadocField.append("* ").append(fieldEntity.getFieldComment());
		} else {
			javadocField.append("* ").append(fieldEntity.getFieldName());
		}
		javadocField.append("\n");
		javadocField.append("*/");
		PsiComment comment = factory.createCommentFromText(javadocField.toString(), null);
		field.addBefore(comment, field.getFirstChild());
	}
}
