package com.github.flyhero.toolbox.bean2bean.action;

import com.github.flyhero.toolbox.bean2bean.ui.GeneratorPanel;
import com.github.flyhero.toolbox.common.AbstractFieldParser;
import com.github.flyhero.toolbox.common.Notifier;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiPackage;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.Enumeration;
import java.util.Objects;

public class Bean2BeanAction extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent e) {
		Project project = e.getProject();
		PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
		PsiClass currentClass = AbstractFieldParser.getClassEntity(file);
		if (Objects.isNull(currentClass)) {
			Notifier.notifyError("当前文件不存在类", project);
			return;
		}
		String packageName = ((PsiJavaFile) file).getPackageName();

		PackageChooserDialog selector = new PackageChooserDialog("选择创建路径", project);
		selector.selectPackage(packageName);
//		PackageChooserDialog selector = new PackageChooserDialog("选择创建路径", project);
//		JTree jTree = (JTree) selector.getPreferredFocusedComponent();
//		TreeNode root = (TreeNode) jTree.getModel().getRoot();
//		expandAll(jTree, new TreePath(root), true);
		selector.show();
		PsiPackage selectedPackage = selector.getSelectedPackage();
		if (selectedPackage == null) {
			return;
		}

//		GenerateBeanHandler handler = new GenerateBeanHandler();
//		PsiField[] selectedFields = handler.getSelectedField(currentClass, project);

/*		PsiDirectory[] directories = selectedPackage.getDirectories();
		for (PsiDirectory directory : directories) {
			WriteCommandAction.runWriteCommandAction(project, () -> {
				PsiElementFactory instance = PsiElementFactory.getInstance(directory.getProject());
				PsiClass aClass = instance.createClass(pojoName.getText());
				for (PsiField field : selectedFields) {
					aClass.add(field);
				}
				directory.add(aClass);
			});
		}*/
		new GeneratorPanel(project, currentClass, selectedPackage);
	}

	/**
	 * 展开tree
	 *
	 * @param tree
	 * @param parent
	 * @param expand
	 */
	private static void expandAll(JTree tree, TreePath parent, boolean expand) {
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements(); ) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path, expand);
			}
		}
		if (expand) {
			tree.expandPath(parent);
		} else {
			tree.collapsePath(parent);
		}
	}
}
