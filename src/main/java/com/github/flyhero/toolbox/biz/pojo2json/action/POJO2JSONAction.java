package com.github.flyhero.toolbox.biz.pojo2json.action;

import com.github.flyhero.toolbox.common.Clipboard;
import com.github.flyhero.toolbox.common.Notifier;
import com.github.flyhero.toolbox.biz.pojo2json.parser.KnownException;
import com.github.flyhero.toolbox.biz.pojo2json.parser.POJO2JSONParser;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UClass;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UVariable;
import org.jetbrains.uast.UastContextKt;
import org.jetbrains.uast.UastLanguagePlugin;
import org.jetbrains.uast.UastUtils;

public abstract class POJO2JSONAction extends AnAction {

	protected final POJO2JSONParser pojo2JSONParser;

	public POJO2JSONAction(POJO2JSONParser pojo2JSONParser) {
		this.pojo2JSONParser = pojo2JSONParser;
	}


	public void pojo2jsonAction(@NotNull final PsiFile psiFile) {
		pojo2jsonAction(psiFile, null, null);
	}

	public void pojo2jsonAction(@NotNull final PsiFile psiFile,
								@Nullable final Editor editor,
								@Nullable final PsiElement psiElement) {
		final Project project = psiFile.getProject();

		if (!uastSupported(psiFile)) {
			Notifier.warn("This file can't convert to json.", project);
			return;
		}

		UElement uElement = null;
		if (psiElement != null) {
			uElement = UastContextKt.toUElement(psiElement, UVariable.class);
		}

		if (uElement == null) {
			if (editor != null) {
				PsiElement elementAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
				uElement = UastUtils.findContaining(elementAt, UClass.class);
			}
		}

		if (uElement == null) {
			String fileText = psiFile.getText();
			int offset = fileText.contains("class") ? fileText.indexOf("class") : fileText.indexOf("record");
			if (offset < 0) {
				Notifier.warn("Can't find class scope.", project);
				return;
			}
			PsiElement elementAt = psiFile.findElementAt(offset);
			uElement = UastUtils.findContaining(elementAt, UClass.class);
		}

		try {
			String json = pojo2JSONParser.uElementToJSONString(uElement);

			Clipboard.copy(json);

			String fileName = psiFile.getName();
			Notifier.info("成功将 " + fileName.replace(".java", "") + " 转为JSON, 已拷贝到粘贴板.", project);

		} catch (KnownException ex) {
			Notifier.warn(ex.getMessage(), project);
		}
	}


	public boolean uastSupported(@NotNull final PsiFile psiFile) {
		return UastLanguagePlugin.Companion.getInstances()
				.stream()
				.anyMatch(l -> l.isFileSupported(psiFile.getName()));
	}
}
