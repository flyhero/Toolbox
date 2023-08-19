package com.github.flyhero.toolbox.utils;

import com.github.flyhero.toolbox.utils.StringUtils;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTreeUtil;

import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class PsiClassUtils {

    /**
     * 获取PsiClass
     */
    public static PsiClass getPsiClassByPsiType(Project project, PsiType returnType) {
        PsiClass psiClass = getPsiClassByFullName(project, returnType.getCanonicalText());
        assert psiClass != null;
        return psiClass;
    }

    /**
     * 通过全类名，获取PsiClass
     */
    @Nullable
    public static PsiClass getPsiClassByFullName(Project project, String fullName) {
        String simpleName = StringUtils.substringAfterLast(fullName, ".");
        PsiClass[] psiClasses = PsiShortNamesCache.getInstance(project)
                .getClassesByName(simpleName, GlobalSearchScope.allScope(project));
        for (PsiClass psiClass : psiClasses) {
            if (Objects.equals(psiClass.getQualifiedName(), fullName)) {
                return psiClass;
            }
        }
        return null;
    }

    /**
     * 获取文件的所有类
     *
     * @param element
     * @return
     */
    public static List<PsiClass> getClasses(PsiElement element) {
        List<PsiClass> elements = Lists.newArrayList();
        List<PsiClass> classElements = PsiTreeUtil.getChildrenOfTypeAsList(element, PsiClass.class);
        elements.addAll(classElements);
        for (PsiClass classElement : classElements) {
            // 这里用了递归的方式获取内部类
            elements.addAll(getClasses(classElement));
        }
        return elements;
    }

    /**
     * 获取当前类
     *
     * @param element
     * @return
     */
    public static PsiClass getClassEntity(PsiElement element) {
        return PsiTreeUtil.getChildOfType(element, PsiClass.class);
    }


}
