package com.github.flyhero.toolbox.utils;

import java.util.Collections;
import java.util.List;

import org.apache.commons.compress.utils.Lists;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocToken;

public class PsiDocCommentUtils {

    /**
     * 提取字段上的注释
     */
    public static List<String> extractCommentDescription(PsiField field) {
        PsiDocComment docComment = field.getDocComment();
        return extractCommentDescription(docComment);
    }

    /**
     * 提取类上的注释
     */
    public static List<String> extractCommentDescription(PsiClass psiClass) {
        PsiDocComment docComment = psiClass.getDocComment();
        return extractCommentDescription(docComment);
    }

    /**
     * 提取PsiDocComment的注释
     */
    public static List<String> extractCommentDescription(PsiDocComment docComment) {
        if (docComment == null) {
            return Collections.emptyList();
        }
        List<String> comments = Lists.newArrayList();
        PsiElement[] descriptionElements = docComment.getDescriptionElements();
        for (PsiElement descriptionElement : descriptionElements) {
            if (descriptionElement instanceof PsiDocToken) {
                comments.add(descriptionElement.getText().trim());
            }
        }
        return comments;
    }

    /**
     * 提取注释中的标签 @see Trade#id -> Trade#id
     */
    public static List<String> extractCommentTagForAtSee(PsiField field) {
        PsiDocComment docComment = field.getDocComment();
        return extractCommentTagForAtSee(docComment);
    }

    /**
     * 提取注释中的标签 @see Trade#id -> Trade#id
     */
    public static List<String> extractCommentTagForAtSee(PsiDocComment docComment) {
        if (docComment == null) {
            return Collections.emptyList();
        }
        List<String> result = Lists.newArrayList();
        for (PsiDocTag tag : docComment.getTags()) {
            if (tag.getNameElement().getText().equals("@see")) {
                if (tag.getValueElement() != null) {
                    result.add(tag.getValueElement().getText());
                }
            }
        }
        return result;
    }
}
