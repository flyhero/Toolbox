package com.github.flyhero.toolbox.biz.interface2impl;

import java.util.HashSet;
import java.util.Objects;

import com.github.flyhero.toolbox.common.AbstractFieldParser;
import com.github.flyhero.toolbox.common.Notifier;
import com.google.common.collect.Sets;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.codeStyle.CodeStyleManager;

import org.jetbrains.annotations.NotNull;

public class Interface2ImplAction extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent e) {
		Project project = e.getProject();
		PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
		if (!(file instanceof PsiJavaFile)) {
			Notifier.error("当前非Java接口类", project);
			return;
		}
		PsiClass currentClass = AbstractFieldParser.getClassEntity(file);

		if (Objects.isNull(currentClass)) {
			Notifier.error("当前文件不存在类", project);
			return;
		}
		if (!currentClass.isInterface()) {
			Notifier.error("当前非接口类", project);
			return;
		}
		PsiDirectory parentDirectory = file.getContainingDirectory();

		String directoryName = "impl";  // 要创建的目录名称
		PsiDirectory subdirectory = parentDirectory.findSubdirectory(directoryName);
//		parentDirectory.createSubdirectory(directoryName);

		String packageName = ((PsiJavaFile) file).getPackageName();
		if (Objects.nonNull(subdirectory)) {
			packageName = packageName + "." + directoryName;
		}

		PackageChooserDialog selector = new PackageChooserDialog("选择创建路径", project);
		selector.selectPackage(packageName);
		selector.show();
		PsiPackage selectedPackage = selector.getSelectedPackage();
		if (selectedPackage == null) {
			return;
		}
		PsiDirectory[] directories = selectedPackage.getDirectories();
		for (PsiDirectory directory : directories) {
			PsiFileFactory fileFactory = PsiFileFactory.getInstance(directory.getProject());
			//文件中添加类
			PsiElementFactory factory = PsiElementFactory.getInstance(directory.getProject());
			WriteCommandAction.runWriteCommandAction(project, () -> {
				PsiJavaFile psiFile = (PsiJavaFile) fileFactory.createFileFromText(currentClass.getName() + "Impl.java", JavaFileType.INSTANCE, "");

				PsiClass aClass = factory.createClass(currentClass.getName() + "Impl");
				PsiJavaCodeReferenceElement superClassRef = factory.createClassReferenceElement(currentClass);
				aClass.getImplementsList().add(superClassRef);

				PsiImportList importList1 = ((PsiJavaFile) file).getImportList();
				PsiImportList importList = psiFile.getImportList();
				PsiImportStatement[] importStatements = importList1.getImportStatements();
				for (PsiImportStatement importStatement : importStatements) {
					importList.add(importStatement);
				}
				PsiImportStatement importStatement = factory.createImportStatementOnDemand("org.springframework.stereotype");
				importList.add(importStatement);

				aClass.getModifierList().addAnnotation("Service");

				PsiElement clazz = psiFile.add(aClass);

				PsiMethod[] allMethods = currentClass.getMethods();
				for (PsiMethod method : allMethods) {

					PsiType returnType = method.getReturnType();
					PsiMethod method1 = factory.createMethod(method.getName(), returnType, method);
					method1.getParameterList().replace(method.getParameterList());
					method1.getThrowsList().add(method.getThrowsList());

					method1.getModifierList().addAnnotation("Override");

					if (returnType instanceof PsiPrimitiveType) {
						if (isNumber(returnType)) {
							PsiCodeBlock methodBody = factory.createCodeBlockFromText("{return 0;}", aClass);
							method1.getBody().replace(methodBody);
						}
						if (returnType.getPresentableText().equals("char")) {
							PsiCodeBlock methodBody = factory.createCodeBlockFromText("{return ' ';}", aClass);
							method1.getBody().replace(methodBody);
						}
						if (returnType.getPresentableText().equals("boolean")) {
							PsiCodeBlock methodBody = factory.createCodeBlockFromText("{return false;}", aClass);
							method1.getBody().replace(methodBody);
						}
					} else {
						PsiCodeBlock methodBody = factory.createCodeBlockFromText("{return null;}", aClass);
						method1.getBody().replace(methodBody);
					}
					clazz.add(method1);
				}
				CodeStyleManager styleManager = CodeStyleManager.getInstance(directory.getProject());
				styleManager.reformat(psiFile);
				directory.add(psiFile);
			});
		}
	}

	private static HashSet<String> numbers = Sets.newHashSet("byte", "short", "int", "long", "double", "float");

	private boolean isNumber(PsiType psiType) {
		String presentableText = psiType.getPresentableText();
		return numbers.contains(presentableText);
	}

	public void update(@NotNull AnActionEvent e) {
		super.update(e);
		PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
		boolean ok = file instanceof PsiJavaFile;
		PsiClass currentClass = AbstractFieldParser.getClassEntity(file);

		if (Objects.isNull(currentClass)) {
			ok = false;
		} else {
			if (!currentClass.isInterface()) {
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
