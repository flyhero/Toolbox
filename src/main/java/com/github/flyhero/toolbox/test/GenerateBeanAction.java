/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.flyhero.toolbox.test;

import com.github.flyhero.toolbox.common.BaseUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Action group which contains Generate... actions
 * Available in the Java code editor context only
 *
 * @author Alexey Kudravtsev
 */
public class GenerateBeanAction extends AnAction {


	@Override
	public void actionPerformed(@NotNull AnActionEvent e) {
		Project project = e.getData(PlatformDataKeys.PROJECT);
		PsiFile mFile = e.getData(CommonDataKeys.PSI_FILE);
		PsiClass psiClass = BaseUtil.getClassEntity(mFile);
		GenerateBeanHandler handler = new GenerateBeanHandler();
		PsiField[] selectedFields = handler.getSelectedField(psiClass, project);
		WriteCommandAction.runWriteCommandAction(project, () -> {
			for (PsiField field : selectedFields) {
				psiClass.add(field);
			}
		});
	}
}