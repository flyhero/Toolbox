package com.github.flyhero.toolbox.spring.autowired.action;

import com.github.flyhero.toolbox.common.AbstractFieldParser;
import com.github.flyhero.toolbox.common.Notifier;
import com.intellij.ide.util.ClassFilter;
import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public class BeanAutowiredAction extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent e) {
		Project project = e.getProject();
		PsiJavaFile file = ((PsiJavaFile) e.getData(CommonDataKeys.PSI_FILE));
		PsiClass currentClass = AbstractFieldParser.getClassEntity(file);
		if (Objects.isNull(currentClass)) {
			Notifier.notifyError("当前文件不存在类", project);
			return;
		}
		TreeClassChooserFactory instance = TreeClassChooserFactory.getInstance(project);
		TreeClassChooser selector = instance.createInheritanceClassChooser("Select a Spring Bean", GlobalSearchScope.allScope(project), null, null, new SpringClassFilter());
//		TreeClassChooser selector = instance.createAllProjectScopeChooser("Select a Spring Bean");

		selector.showDialog();
		PsiClass selected = selector.getSelected();
		if (selected != null) {
			autowired(project, file, currentClass, selected);
		}
	}

	private void autowired(Project project, PsiJavaFile file, PsiClass currentClass, PsiClass selected) {
		WriteCommandAction.runWriteCommandAction(project, () -> {
			PsiElementFactory factory = PsiElementFactory.getInstance(project);
			PsiImportStatement importStatement = factory.createImportStatement(selected);
			PsiImportList importList = file.getImportList();
			PsiImportStatement[] importStatements = importList.getImportStatements();
			boolean match = Arrays.stream(importStatements).anyMatch(s -> s.getText().equals(importStatement.getText()));
			if (!match) {
				importList.add(importStatement);
			}
			String name = selected.getName();
			String fieldName;
			if (name.length() > 1) {
				fieldName = name.substring(0, 1).toLowerCase() + name.substring(1);
			} else {
				fieldName = name.toLowerCase();
			}
			PsiField field = factory.createField(fieldName, PsiType.getTypeByName(selected.getName(), project, GlobalSearchScope.allScope(project)));
			field.getModifierList().setModifierProperty(PsiModifier.PRIVATE, true);

			PsiField[] fields = currentClass.getFields();
			boolean anyMatch = Arrays.stream(fields).anyMatch(psiField -> psiField.getType().getPresentableText().equals(field.getType().getCanonicalText()));
			if (!anyMatch) {
				currentClass.addAfter(field, currentClass.getLBrace().getNextSibling());
				boolean existAutowired = Arrays.stream(importStatements).anyMatch(s -> s.getText().equals("import org.springframework.beans.factory.annotation.Autowired;") || s.getText().equals("import org.springframework.beans.factory.annotation.*;"));
				if (!existAutowired) {
					PsiImportStatement onDemand = factory.createImportStatementOnDemand("org.springframework.beans.factory.annotation");
					importList.add(onDemand);
				}
				PsiAnnotation annotation = factory.createAnnotationFromText("@Autowired", field);
				currentClass.addAfter(annotation, currentClass.getLBrace().getNextSibling());
			}
		});
	}


	private static class SpringClassFilter implements ClassFilter {
		@Override
		public boolean isAccepted(PsiClass aClass) {
			return aClass.isInterface() || aClass.hasAnnotation("Service") || aClass.hasAnnotation("Component");
		}
	}

	public void update(@NotNull AnActionEvent e) {
		super.update(e);
		PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
		boolean ok = file instanceof PsiJavaFile;
		PsiClass currentClass = AbstractFieldParser.getClassEntity(file);

		if (Objects.isNull(currentClass)) {
			ok = false;
		} else {
			if (currentClass.isInterface() || currentClass.isEnum() || currentClass.isAnnotationType() || currentClass.isRecord()) {
				ok = false;
			}
		}

		// 设置当前 action 菜单的可见性
		e.getPresentation().setVisible(ok);

		// 设置当前 action 菜单的可用性，
		// 如果不可用，则 actionPreformed() 方法收不到点击事件
		e.getPresentation().setEnabled(ok);

		// 同时设置当前 action 菜单的可见性和可用性
		e.getPresentation().setEnabledAndVisible(ok);

	}
}
