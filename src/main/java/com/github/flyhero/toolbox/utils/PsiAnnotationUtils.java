package com.github.flyhero.toolbox.utils;

import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.lang.jvm.annotation.JvmAnnotationConstantValue;
import com.intellij.psi.*;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PsiAnnotationUtils {

    /**
     * 注解@Nullable
     */
    public static boolean existsNullable(PsiField psiField) {
        PsiAnnotation[] annotations = psiField.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            if (Objects.equals(annotation.getText(), "@Nullable")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 注解@GeneratedValue
     */
    public static boolean existsGeneratedValue(PsiField psiField) {
        return psiField.getAnnotation("javax.persistence.GeneratedValue") != null;
    }

    /**
     * 注解@Type(type = "Json")
     */
    public static boolean existsJsonType(PsiField field) {
        PsiAnnotation tableAnnotation = field.getAnnotation("org.hibernate.annotations.Type");
        if (tableAnnotation == null) {
            return false;
        }
        PsiAnnotationMemberValue typeValue = tableAnnotation.findAttributeValue("type");
        if (typeValue == null) {
            return false;
        }
        Object value = ((PsiLiteralExpression) typeValue).getValue();
        if (value == null) {
            return false;
        }
        return value.toString().equals("Json");
    }

    /**
     * 注解@Query
     */
    public static boolean existsQuery(PsiMethod method) {
        return method.getAnnotation("org.springframework.data.jpa.repository.Query") != null;
    }

    /**
     * 提取类上的唯一约束字段
     * * @Table(uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "tid"}))
     *
     * @return ["userId", "tid"]
     */
    public static List<String> extractUniqueKey(PsiClass psiClass) {
        PsiAnnotation tableAnnotation = psiClass.getAnnotation("javax.persistence.Table");
        if (tableAnnotation == null) {
            return Collections.emptyList();
        }
        PsiAnnotationMemberValue uniqueConstraints = tableAnnotation.findAttributeValue("uniqueConstraints");
        if (uniqueConstraints instanceof PsiAnnotation) {
            PsiAnnotationMemberValue columnNames = ((PsiAnnotation) uniqueConstraints).findAttributeValue(
                    "columnNames");
            if (columnNames == null) {
                return Collections.emptyList();
            }
            PsiAnnotationMemberValue[] columnNamesValues = ((PsiArrayInitializerMemberValue) columnNames).getInitializers();
            return Arrays.stream(columnNamesValues).map(columnNamesValue -> {
                PsiLiteralExpression expression = (PsiLiteralExpression) columnNamesValue;
                return Objects.requireNonNull(expression.getValue()).toString();
            }).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @Nullable
    public static String extractQueryValue(PsiMethod method) {
        PsiAnnotation queryAnnotation = method.getAnnotation("org.springframework.data.jpa.repository.Query");
        if (queryAnnotation == null) {
            return null;
        }
        for (JvmAnnotationAttribute attribute : queryAnnotation.getAttributes()) {
            if (attribute.getAttributeName().equals("value")) {
                if (attribute.getAttributeValue() instanceof JvmAnnotationConstantValue) {
                    Object constantValue = ((JvmAnnotationConstantValue) attribute.getAttributeValue()).getConstantValue();
                    return constantValue != null ? constantValue.toString() : null;
                }
            }
        }
        return null;
    }
}
