package com.github.flyhero.toolbox.biz.entity2constant.action;

import java.util.List;
import java.util.stream.Collectors;

import com.github.flyhero.toolbox.biz.entity2constant.service.StaticConstantFieldParser;
import com.github.flyhero.toolbox.common.AbstractFieldParser;
import com.github.flyhero.toolbox.model.Field;
import com.github.flyhero.toolbox.utils.PsiClassUtils;
import com.github.flyhero.toolbox.utils.StringUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;

/**
 * 生成实体类的属性的静态变量
 */
public class StaticConstantAction extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent e) {
		PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
		PsiClass currentClass = AbstractFieldParser.getClassEntity(file);

		String[] items = {"属性名称(驼峰)", "表字段名称(下划线)"};

		BaseListPopupStep<String> step = new BaseListPopupStep<String>("选择静态属性「值」生成方式", items) {
			@Override
			public PopupStep onChosen(String selectedValue, boolean finalChoice) {
				boolean isUnderline = items[1].equals(selectedValue);
				generate(isUnderline, file, currentClass);
				return FINAL_CHOICE;
			}
		};

		ListPopup popup = JBPopupFactory.getInstance()
				.createListPopup(step);

		popup.showCenteredInCurrentWindow(file.getProject());
	}

	private void generate(boolean isUnderline, PsiFile file, PsiClass currentClass) {
		StaticConstantFieldParser service = new StaticConstantFieldParser();
		List<Field> staticConstantFieldList = service.getStaticConstantFieldList(currentClass, true);
		WriteCommandAction.runWriteCommandAction(file.getProject(), () -> {
			List<String> staticFieldNames = staticConstantFieldList.stream().map(Field::getName).collect(Collectors.toList());
			List<Field> fieldList = service.getSimpleFieldList(currentClass, true);
			for (Field field : fieldList) {
				String staticFieldName = StringUtils.lowerCamelToLowerUnderscore(field.getName()).toUpperCase();
				if (staticFieldNames.contains(staticFieldName)) {
					continue;
				}
				String val = field.getName();
				if (isUnderline) {
					val = StringUtils.lowerCamelToLowerUnderscore(val);
				}
				String builder = "public static final String " +
						staticFieldName + " = " +
						"\"" + val + "\";" + "\n";
				PsiField fieldFromText = PsiElementFactory.getInstance(file.getProject())
						.createFieldFromText(builder, currentClass);
				currentClass.addAfter(fieldFromText, currentClass.getLastChild().getPrevSibling());
			}
		});
	}

	@Override
	public void update(AnActionEvent e) {
		PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
		if (!(file instanceof PsiJavaFile)) {
			e.getPresentation().setEnabledAndVisible(false);
			return;
		}
		PsiClass psiClass = PsiClassUtils.getClassEntity(file);
		StaticConstantFieldParser service = new StaticConstantFieldParser();
		List<Field> fieldList = service.getSimpleFieldList(psiClass, true);

		e.getPresentation().setEnabledAndVisible(fieldList != null && fieldList.size() > 1);
	}

}
