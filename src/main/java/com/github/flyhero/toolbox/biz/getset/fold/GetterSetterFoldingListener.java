package com.github.flyhero.toolbox.biz.getset.fold;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.github.flyhero.toolbox.utils.JavaLangUtils;
import com.github.flyhero.toolbox.utils.PsiClassUtils;
import com.github.flyhero.toolbox.utils.PsiFieldUtils;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.editor.FoldingModel;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;

import org.jetbrains.annotations.NotNull;

public class GetterSetterFoldingListener implements FileEditorManagerListener {
	private static final Logger LOG = Logger.getInstance(GetterSetterFoldingListener.class);
	private static final String PLACEHOLDER_TEXT = "Getter/Setter Methods";

	@Override
	public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
		Project project = source.getProject();
		PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
		if (!(psiFile instanceof PsiJavaFile)) {
			return;
		}
		Document document = FileDocumentManager.getInstance().getDocument(file);
		if (document == null) {
			return;
		}
		Editor editor = EditorFactory.getInstance().createEditor(document);
		if (editor == null) {
			return;
		}
		PsiJavaFile javaFile = (PsiJavaFile) psiFile;
		boolean writable = javaFile.isWritable();
		PsiImportList importList = javaFile.getImportList();
		if (Objects.nonNull(importList) && importList.isValid()) {
			String text = importList.getText();
			if (text.contains("lombok")) {
				return;
			}
		}
		try {
			javaFile.accept(new JavaElementVisitor() {
				public void visitElement(@NotNull PsiElement element) {
					if (!(element instanceof PsiJavaFile)) {
						return;
					}
					List<PsiClass> psiClassList = PsiClassUtils.getClasses(element);
					for (PsiClass psiClass : psiClassList) {
						PsiField[] fields = PsiFieldUtils.getFields(psiClass);
						List<String> fieldNames = getFieldNames(fields);
						if (fieldNames.isEmpty()) {
							continue;
						}
						List<PsiMethod> getterSetters = new ArrayList<>();
						String className = psiClass.getName();
						PsiMethod[] class1Methods = psiClass.getMethods();
						int part = 0;
						for (PsiMethod method : class1Methods) {
//							boolean constructor = method.isConstructor();
//							String methodName = method.getName();
//							if (constructor || (!methodName.startsWith("get") && !methodName.startsWith("set") && !methodName.startsWith("is"))) {
//								continue;
//							}
							if (isGetterSetter(method, fieldNames)) {
								getterSetters.add(method);
							} else if (!getterSetters.isEmpty()) {
								part++;
								addFoldingRegion(project, className, part, getterSetters);
								getterSetters.clear();
							}
						}

						if (!getterSetters.isEmpty()) {
							part++;
							addFoldingRegion(project, className, part, getterSetters);
						}
					}

				}
			});
		} catch (Exception e) {
			LOG.error("自动折叠 Getter and Setter 失败", e);
		}
	}

	private List<String> getFieldNames(PsiField[] psiFields) {
		List<String> fieldNames = new ArrayList<>();
		for (PsiField psiField : psiFields) {
			if (JavaLangUtils.isStaticModifier(psiField) || JavaLangUtils.isFinalModifier(psiField)) {
				continue;
			}
			fieldNames.add(psiField.getName());
		}
		return fieldNames;
	}

	private boolean isGetterSetter(PsiMethod method, List<String> fieldNames) {
		String methodName = method.getName();
		boolean isGet = methodName.startsWith("get");
		boolean is = methodName.startsWith("is");
		boolean isSet = methodName.startsWith("set");
		if (!isGet && !is && !isSet) {
			return false;
		}
		String name;
		if (isGet || isSet) {
			name = methodName.substring(3);
		} else {
			name = methodName.substring(2);
		}
		boolean anyMatch = fieldNames.stream().anyMatch(v -> v.equalsIgnoreCase(name));
		if (!anyMatch) {
			return false;
		}

		PsiParameterList parameterList = method.getParameterList();
		int parametersCount = parameterList.getParametersCount();
		if (isGet || is) {
			return parametersCount == 0;
		} else {
			return parametersCount == 1;
		}
	}

	private void addFoldingRegion(Project project, String className, int part, List<PsiMethod> getterSetters) {
		if (getterSetters.size() <= 1) {
			return;
		}
		String placeholderText;
		if (part == 1) {
			placeholderText = className + " " + PLACEHOLDER_TEXT;
		} else {
			placeholderText = className + " Part." + part + " " + PLACEHOLDER_TEXT;
		}
		Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
		int textLength = editor.getDocument().getTextLength();
		PsiMethod first = getterSetters.get(0);
		PsiMethod last = getterSetters.get(getterSetters.size() - 1);
		int startOffset = first.getTextRange().getStartOffset();
		int endOffset = last.getTextRange().getEndOffset();

		if (startOffset >= endOffset || endOffset >= textLength) {
			return;
		}
		if (!(editor instanceof EditorEx)) {
			return;
		}

		FoldingModel foldingModel = ((EditorEx) editor).getFoldingModel();
		FoldRegion[] allFoldRegions = foldingModel.getAllFoldRegions();
		foldingModel.runBatchFoldingOperation(() -> {
			boolean addFoldRegion = true;
			for (FoldRegion foldRegion : allFoldRegions) {
				boolean valid = foldRegion.isValid();
				if (!valid){
					foldingModel.removeFoldRegion(foldRegion);
					continue;
				}
				if (!placeholderText.equals(foldRegion.getPlaceholderText())) {
					continue;
				}
				if (foldRegion.getStartOffset() != startOffset || foldRegion.getEndOffset() != endOffset) {
					foldingModel.removeFoldRegion(foldRegion);
				} else {
					foldRegion.setExpanded(false);
					addFoldRegion = false;
				}
			}
			if (addFoldRegion) {
				FoldRegion foldRegion = foldingModel.addFoldRegion(startOffset, endOffset, placeholderText);
				if (Objects.nonNull(foldRegion)) {
					foldRegion.setExpanded(false);
				}
			}
		});
	}
}