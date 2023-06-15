package com.github.flyhero.toolbox.json2pojo;

import com.github.flyhero.toolbox.common.BaseUtil;
import com.github.flyhero.toolbox.json2pojo.ui.JsonDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;

/**
 * User: dim
 * Date: 14-7-4
 * Time: 下午1:44
 * <pre>
 * 插件主入口方法
 * </pre>
 */
//public class MainAction extends BaseGenerateAction {
public class MainAction extends AnAction {

	@SuppressWarnings("unused")
/*	public MainAction() {
		super(null);
	}

	@SuppressWarnings("unused")
	public MainAction(CodeInsightActionHandler handler) {
		super(handler);
	}

	@Override
	protected boolean isValidForClass(final PsiClass targetClass) {
		return super.isValidForClass(targetClass);
	}

	@Override
	public boolean isValidForFile(Project project, Editor editor, PsiFile file) {
		return super.isValidForFile(project, editor, file);
	}*/

	@Override
	public void actionPerformed(AnActionEvent event) {
		Navigatable data = event.getData(CommonDataKeys.NAVIGATABLE);
		Project project = event.getData(PlatformDataKeys.PROJECT);
		// 选择的是路径
		if (data instanceof PsiDirectory) {
			System.out.println(((PsiDirectory) data).getName());
			JsonDialog jsonD = new JsonDialog((PsiDirectory) data, null, null, project);
			jsonD.setProject(project);
			return;
		}

		Editor editor = event.getData(PlatformDataKeys.EDITOR);
		PsiFile mFile = event.getData(CommonDataKeys.PSI_FILE);
//		PsiFile mFile = PsiUtilBase.getPsiFileInEditor(editor, project);
		PsiClass psiClass = BaseUtil.getClassEntity(mFile);
//		PsiClass psiClass = getTargetClass(editor, mFile);
		//初始化Json弹框
		JsonDialog jsonD = new JsonDialog(null, psiClass, mFile, project);
		jsonD.setClass(psiClass);
		jsonD.setFile(mFile);
		jsonD.setProject(project);

	}

}
