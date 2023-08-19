package com.github.flyhero.toolbox.biz.comments;

import java.util.List;

import com.github.flyhero.toolbox.utils.PsiClassUtils;
import com.github.flyhero.toolbox.utils.PsiFieldUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.util.PsiTreeUtil;

import org.jetbrains.annotations.NotNull;

public class CommentTransformAction extends AnAction {
	@Override
	public void actionPerformed(@NotNull AnActionEvent event) {
		Project project = event.getProject();
		PsiFile psiFile = event.getData(LangDataKeys.PSI_FILE);

		if (project == null || psiFile == null) {
			return;
		}

		WriteCommandAction.runWriteCommandAction(project, () -> {
			PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
			PsiElementFactory elementFactory = PsiElementFactory.getInstance(project);

			// 遍历类中的所有方法
			PsiTreeUtil.processElements(psiFile, element -> {
				if (element instanceof PsiField) {
					PsiField field = (PsiField) element;
					PsiElement[] children = field.getChildren();
					for (PsiElement child : children) {
						if (child instanceof PsiComment && !(child instanceof PsiDocComment)) {
							// 获取方法的注释文本
							String commentText = child.getText();
							// 检查是否为单行注释
							if (commentText.contains("//")) {
								// 将单行注释转换为多行注释
								String multiLineCommentText = "/**\n" +
										commentText.replaceFirst("//", "* ")
//												.replaceAll("\\n", "\n * ")
												.trim() +
										"\n */";

								// 创建多行注释PsiElement
								PsiElement multiLineComment = elementFactory
										.createCommentFromText(multiLineCommentText, null);

								// 替换原来的注释
								child.replace(multiLineComment);
							}
						}
					}
				}
				return true;
			});

			// 保存修改
			documentManager.doPostponedOperationsAndUnblockDocument(documentManager.getDocument(psiFile));
			documentManager.commitDocument(documentManager.getDocument(psiFile));
		});
	}

	@Override
	public void update(@NotNull AnActionEvent event) {
		// 仅对Java 类文件启用此操作
		PsiFile psiFile = event.getData(LangDataKeys.PSI_FILE);
		List<PsiClass> psiClassList = PsiClassUtils.getClasses(psiFile);
		boolean hasField = false;
		boolean hasClass = false;
		boolean hasSingleComment = false;
		for (PsiClass psiClass : psiClassList) {
			PsiField[] fields = PsiFieldUtils.getFields(psiClass);
			if (fields.length != 0){
				hasField = true;
				queryComment:
				for (PsiField field : fields) {
					PsiElement[] children = field.getChildren();
					for (PsiElement child : children) {
						if (child instanceof PsiComment && !(child instanceof PsiDocComment)){
							hasSingleComment = true;
							break queryComment;
						}
					}
				}
			}
			if (!psiClass.isInterface()){
				hasClass = true;
			}
		}
		boolean isJava = psiFile instanceof PsiJavaFile;
		boolean enabled = isJava && hasClass && hasField && hasSingleComment;
		event.getPresentation().setEnabledAndVisible(enabled);
	}
}
