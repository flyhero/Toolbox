package com.github.flyhero.toolbox.common;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

public class Clipboard {

    public static void copy(String content) {
        StringSelection selection = new StringSelection(content);
        java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }
}
