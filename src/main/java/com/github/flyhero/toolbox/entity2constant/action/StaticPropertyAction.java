package com.github.flyhero.toolbox.entity2constant.action;

import com.github.flyhero.toolbox.common.AbstractFieldParser;
import com.github.flyhero.toolbox.entity2constant.service.StaticConstantFieldParser;
import com.github.flyhero.toolbox.model.Field;
import com.github.flyhero.toolbox.utils.BaseUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 生成实体类的属性的静态变量
 */
public class StaticPropertyAction extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent e) {
		PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
		Editor editor = e.getData(CommonDataKeys.EDITOR);
		PsiClass currentClass = AbstractFieldParser.getClassEntity(file);
		StaticConstantFieldParser service = new StaticConstantFieldParser();

		WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> {
			PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
			List<Field> staticConstantFieldList = service.getStaticConstantFieldList(currentClass, true);
			List<String> staticFieldNames = staticConstantFieldList.stream().map(Field::getName).collect(Collectors.toList());
			List<Field> fieldList = service.getSimpleFieldList(currentClass, true);
			for (Field field : fieldList) {
				if (staticFieldNames.contains(field.getName().toUpperCase())) {
					continue;
				}
				String builder = "public static final String " +
						BaseUtil.humpToUnderline(field.getName()).toUpperCase() + " = " +
						"\"" + field.getName() + "\";" + "\n";
				PsiField fieldFromText = PsiElementFactory.getInstance(psiFile.getProject())
						.createFieldFromText(builder, currentClass);
				currentClass.addAfter(fieldFromText, currentClass.getLastChild().getPrevSibling());
			}
		});

	}
}
