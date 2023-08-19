package com.github.flyhero.toolbox.biz.serialVersion;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.flyhero.toolbox.utils.PsiClassUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.siyeh.ig.fixes.SerialVersionUIDBuilder;

public class GenerateSerialVersionUidAction extends AnAction {

	private static final Pattern SERIALIZABLE_PATTERN = Pattern.compile("implements.+Serializable.*");

	@Override
	public void update(AnActionEvent e) {
		DataContext dataContext = e.getDataContext();
		Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
		PsiFile psiFile = CommonDataKeys.PSI_FILE.getData(dataContext);
		boolean needSerialVersionUID = false;
		boolean hasSerializable = false;
		if (psiFile instanceof PsiJavaFile && editor != null) {
			String documentText = editor.getDocument().getText();
			int count = 0;
			Matcher matcher = SERIALIZABLE_PATTERN.matcher(documentText);
			while (matcher.find()) {
				hasSerializable = true;
				count++;
			}

			String[] uids = documentText.split("private static final long serialVersionUID");
			if (uids.length - 1 < count) {
				needSerialVersionUID = true;
			}
		}
		e.getPresentation().setEnabledAndVisible(hasSerializable && needSerialVersionUID);
	}

	@Override
	public void actionPerformed(AnActionEvent e) {
		Project project = e.getProject();
		PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
		DataContext dataContext = e.getDataContext();
		Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
		if (editor != null) {
			Document document = editor.getDocument();
			String documentText = document.getText();
			List<PsiClass> classes = PsiClassUtils.getClasses(file);
			boolean[] s = new boolean[classes.size()];
			for (int i = 0; i < classes.size(); i++) {
				PsiClass psiClass = classes.get(i);
				PsiField serialVersionUID = psiClass.findFieldByName("serialVersionUID", false);
				if (Objects.nonNull(serialVersionUID)){
					s[i] = true;
				}
			}

			Matcher matcher = SERIALIZABLE_PATTERN.matcher(documentText);
			int addLine = 1;
			int index = 0;
			while (matcher.find()) {
				if (!s[index]){
					int end = matcher.end();
					int lineNumber = editor.getDocument().getLineNumber(end);
					int nextLineStartOffset = document.getLineStartOffset(lineNumber + addLine);
					addLine = addLine + 3;
					long serialVersionUIDValue = SerialVersionUIDBuilder.computeDefaultSUID(classes.get(index));
					String tab = index == 0 ? "    ": "        ";
					final String text = "\n"+tab+"private static final long serialVersionUID = " + serialVersionUIDValue + "L;\n";
					WriteCommandAction.runWriteCommandAction(project, () ->
							document.insertString(nextLineStartOffset, text));
				}
				index++;
			}
		}
	}
}
