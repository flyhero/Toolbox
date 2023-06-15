package com.github.flyhero.toolbox.common;

import com.github.flyhero.toolbox.model.Field;
import com.intellij.lang.jvm.types.JvmPrimitiveTypeKind;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.impl.source.javadoc.PsiDocTokenImpl;
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.flyhero.toolbox.common.Constant.*;

public abstract class AbstractFieldParser {
	public List<Field> getSimpleFieldList(PsiClass currentClass, boolean allField) {
		PsiField[] fields = getPsiFields(currentClass, allField);
		// 利用set去重
		HashSet<Field> fieldSet = new LinkedHashSet<>();
		for (PsiField field : fields) {
			if (isNeedAddConvert(field)) {
				fieldSet.add(getField(field));
			}
		}
		return new ArrayList<>(fieldSet);
	}

	public List<PsiField> getFieldList(PsiClass currentClass, boolean allField) {
		PsiField[] fields = getPsiFields(currentClass, allField);
		// 利用set去重
		HashSet<PsiField> fieldSet = new LinkedHashSet<>();
		for (PsiField field : fields) {
			if (isNeedAddConvert(field)) {
				fieldSet.add(field);
			}
		}
		return new ArrayList<>(fieldSet);
	}

	public List<Field> getStaticConstantFieldList(PsiClass currentClass, boolean allField) {
		PsiField[] fields = getPsiFields(currentClass, allField);
		return Arrays.stream(fields).filter(f -> f.getModifierList().hasExplicitModifier("static"))
				.map(f -> Field.newField(f.getName(), f.getType().getPresentableText(), getComment(f))).collect(Collectors.toList());
	}

	protected boolean isNeedAddConvert(PsiField psiField) {
		return true;
	}

	public PsiField[] getPsiFields(PsiClass currentClass, boolean allField) {
		if (allField) {
			return getAllFields(currentClass);
		}
		return getFields(currentClass);
	}

	/**
	 * 获取文件的所有类
	 *
	 * @param element
	 * @return
	 */
	public static List<PsiClass> getClasses(PsiElement element) {
		List<PsiClass> elements = Lists.newArrayList();
		List<PsiClass> classElements = PsiTreeUtil.getChildrenOfTypeAsList(element, PsiClass.class);
		elements.addAll(classElements);
		for (PsiClass classElement : classElements) {
			// 这里用了递归的方式获取内部类
			elements.addAll(getClasses(classElement));
		}
		return elements;
	}

	/**
	 * 获取当前类
	 *
	 * @param element
	 * @return
	 */
	public static PsiClass getClassEntity(PsiElement element) {
		return PsiTreeUtil.getChildOfType(element, PsiClass.class);
	}

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

	private Field getField(PsiField field) {
		return Field.newField(field.getName(), field.getType().getPresentableText(), getComment(field));
	}


	public static String getComment(PsiField field) {
		if (null != field.getDocComment()) {
			// 字段上没有携带注解，则从Doc注释上获取
			PsiDocComment docComment = field.getDocComment();
			if (null != docComment) {
				PsiElement[] elements = docComment.getDescriptionElements();
				return parseAllDoc(elements);
			}
		}
		return null;
	}

	/**
	 * 解析出文档所有有效文本
	 */
	public static String parseAllDoc(PsiElement[] elements) {
		if (elements == null || elements.length == 0) {
			return null;
		}
		List<String> docList = new ArrayList<>();
		for (PsiElement element : elements) {
			if (element instanceof PsiWhiteSpaceImpl) {
				continue;
			}
			if (element instanceof PsiDocTokenImpl) {
				String text = element.getText();
				if (StringUtils.isNotBlank(text)) {
					docList.add(text.trim());
				}
			}
		}
		return CollectionUtils.isEmpty(docList) ? StringUtils.EMPTY : String.join(" ", docList);
	}

	/**
	 * 是否基础类型
	 *
	 * @param field
	 * @return
	 */
	public static boolean isPrimitiveType(PsiField field) {
		if (null == field) {
			return false;
		}

		String canonicalText = field.getType().getCanonicalText();
		JvmPrimitiveTypeKind kindByName = JvmPrimitiveTypeKind.getKindByName(canonicalText);
		if (null != kindByName) {
			return true;
		}

		JvmPrimitiveTypeKind kindByFqn = JvmPrimitiveTypeKind.getKindByFqn(canonicalText);
		return null != kindByFqn;
	}

	protected boolean isAdditional(String canonicalText) {
		return (StringUtils.equals(STRING_PACKAGE, canonicalText)
				|| StringUtils.equals(DATE_PACKAGE, canonicalText)
				|| StringUtils.equals(DATE_SQL_PACKAGE, canonicalText)
				|| StringUtils.equals(BIG_DECIMAL_PACKAGE, canonicalText)
				|| StringUtils.equals(LOCAL_DATE, canonicalText)
				|| StringUtils.equals(LOCAL_TIME, canonicalText)
				|| StringUtils.equals(TIMESTAMP, canonicalText)
				|| StringUtils.equals(LOCAL_DATE_TIME, canonicalText));
	}
}
