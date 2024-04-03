package com.github.flyhero.toolbox.biz.pojo2vue.action;

import java.util.List;

import com.github.flyhero.toolbox.biz.pojo2vue.service.VueModelFieldParser;
import com.github.flyhero.toolbox.common.AbstractFieldParser;
import com.github.flyhero.toolbox.common.Clipboard;
import com.github.flyhero.toolbox.model.Field;
import com.github.flyhero.toolbox.utils.PsiClassUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiType;

import org.apache.commons.lang3.StringUtils;

/**
 * 生成实体类的属性的vue model
 */
public class VueModelAction extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent e) {
		PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
		PsiClass currentClass = AbstractFieldParser.getClassEntity(file);

		generate(file, currentClass);
	}

	private void generate(PsiFile file, PsiClass currentClass) {
		VueModelFieldParser service = new VueModelFieldParser();
		WriteCommandAction.runWriteCommandAction(file.getProject(), () -> {
			List<Field> fieldList = service.getSimpleFieldList(currentClass, true);
			StringBuilder str = new StringBuilder("{\n");
			for (int i = 0; i < fieldList.size(); i++) {
				Field field = fieldList.get(i);
				String val = field.getName();
				str.append("\t").append(val).append(": ");
				String type = field.getType();
				String dou =",";
				if (i == fieldList.size() -1){
					dou = "";
				}
				if (StringUtils.equals(PsiType.BOOLEAN.getPresentableText(),type)){
					str.append("false").append(dou).append("\n");
				}else {
					str.append("null").append(dou).append("\n");
				}
			}
			str.append("}");
			Clipboard.copy(str.toString());
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
		VueModelFieldParser service = new VueModelFieldParser();
		List<Field> fieldList = service.getSimpleFieldList(psiClass, true);

		e.getPresentation().setEnabledAndVisible(fieldList != null && fieldList.size() > 1);
	}

}
