package com.github.flyhero.toolbox.biz.pojo2json.action;

import java.util.List;

import com.github.flyhero.toolbox.biz.entity2constant.service.StaticConstantFieldParser;
import com.github.flyhero.toolbox.biz.pojo2json.parser.POJO2JSONParser;
import com.github.flyhero.toolbox.common.AbstractFieldParser;
import com.github.flyhero.toolbox.model.Field;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import org.jetbrains.annotations.NotNull;

public abstract class EditorPopupMenuAction extends POJO2JSONAction {

	public EditorPopupMenuAction(POJO2JSONParser pojo2JSONParser) {
		super(pojo2JSONParser);
	}

	@Override
	public void update(@NotNull AnActionEvent e) {
		final Project project = e.getProject();
		final Editor editor = e.getData(CommonDataKeys.EDITOR);
		final PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

		boolean menuAllowed = false;
		if (psiFile != null && editor != null && project != null) {
			menuAllowed = uastSupported(psiFile);
		}
		StaticConstantFieldParser service = new StaticConstantFieldParser();
		PsiClass currentClass = AbstractFieldParser.getClassEntity(psiFile);
		List<Field> fieldList = service.getSimpleFieldList(currentClass, true);

		e.getPresentation().setEnabledAndVisible(menuAllowed && fieldList != null && fieldList.size() > 1);
	}

	@Override
	public void actionPerformed(AnActionEvent e) {
		final Editor editor = e.getData(CommonDataKeys.EDITOR);
		final PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
		final PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);

		pojo2jsonAction(psiFile, editor, psiElement);
	}
}


