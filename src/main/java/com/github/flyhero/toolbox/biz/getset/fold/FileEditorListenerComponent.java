package com.github.flyhero.toolbox.biz.getset.fold;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;

public class FileEditorListenerComponent implements ProjectComponent {

	public FileEditorListenerComponent(Project project) {
		GetterSetterFoldingListener array = new GetterSetterFoldingListener();
		project.getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, array);
	}
}