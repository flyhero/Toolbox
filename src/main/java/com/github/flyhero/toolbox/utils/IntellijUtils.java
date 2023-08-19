package com.github.flyhero.toolbox.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.stream.Collectors;

public class IntellijUtils {

    /**
     * 弹窗提示
     */
    public static void showMessage(Project project, String title, String message) {
        Messages.showMessageDialog(project, message, title, Messages.getInformationIcon());
    }

    /**
     * 复制到粘贴板
     */
    public static void copyToClipboard(String content) {
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        systemClipboard.setContents(new StringSelection(content), null);
    }

    /**
     * 另起一行，写入代码.
     */
    public static void writeJavaCodes(AnActionEvent event, List<String> javaCodes) {
        Project project = event.getProject();
        Editor editor = CommonDataKeys.EDITOR.getData(event.getDataContext());
        assert editor != null;
        Document document = editor.getDocument();

        int offset = editor.getCaretModel().getOffset();

        int lineStartOffset = document.getLineStartOffset(document.getLineNumber(offset) + 1);

        String blankSpace = "        ";
        WriteCommandAction.runWriteCommandAction(project, () -> {
            String content = javaCodes.stream().map(line -> blankSpace + line + "\n").collect(Collectors.joining());
            document.insertString(lineStartOffset, content);
        });
    }

}
