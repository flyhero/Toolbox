// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.github.flyhero.toolbox.test;

import com.intellij.codeInsight.generation.ClassMember;
import com.intellij.codeInsight.generation.PsiElementClassMember;
import com.intellij.codeInsight.generation.PsiFieldMember;
import com.intellij.ide.util.MemberChooser;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GenerateBeanHandler {

	public PsiField[] getSelectedField(PsiClass aClass, Project project) {
		ClassMember[] members = chooseOriginalMembers(aClass, project);
		List<PsiField> fieldsVector = new ArrayList<>();
		for (ClassMember member1 : members) {
			PsiElement member = ((PsiElementClassMember<?>) member1).getElement();
			if (member instanceof PsiField) {
				fieldsVector.add((PsiField) member);
			}
		}
		PsiField[] fields = fieldsVector.toArray(PsiField.EMPTY_ARRAY);
		return fields;
	}

	protected ClassMember @Nullable [] chooseOriginalMembers(PsiClass aClass, Project project) {
		ClassMember[] allMembers = getAllOriginalMembers(aClass);
		return chooseMembers(allMembers, false, false, project, null);
	}

	protected ClassMember @Nullable [] chooseMembers(ClassMember[] members,
													 boolean allowEmptySelection,
													 boolean copyJavadocCheckbox,
													 Project project,
													 @Nullable Editor editor) {
		MemberChooser<ClassMember> chooser = createMembersChooser(members, allowEmptySelection, copyJavadocCheckbox, project);
		if (editor != null) {
			final int offset = editor.getCaretModel().getOffset();

			ClassMember preselection = null;
			for (ClassMember member : members) {
				if (member instanceof PsiElementClassMember) {
					final PsiDocCommentOwner owner = ((PsiElementClassMember<?>) member).getElement();
					final TextRange textRange = owner.getTextRange();
					if (textRange != null && textRange.contains(offset)) {
						preselection = member;
						break;
					}
				}
			}
			if (preselection != null) {
				chooser.selectElements(new ClassMember[]{preselection});
			}
		}

		chooser.show();
//		myToCopyJavaDoc = chooser.isCopyJavadoc();
		final List<ClassMember> list = chooser.getSelectedElements();
		return list == null ? null : list.toArray(ClassMember.EMPTY_ARRAY);
	}

	protected MemberChooser<ClassMember> createMembersChooser(ClassMember[] members,
															  boolean allowEmptySelection,
															  boolean copyJavadocCheckbox,
															  Project project) {
		MemberChooser<ClassMember> chooser =
				new MemberChooser<>(members, allowEmptySelection, true, project, null, null) {
					@Nullable
					@Override
					protected String getHelpId() {
						return "GenerateBeanAction";
					}
				};
		chooser.setTitle("选择属性");
		chooser.setCopyJavadocVisible(copyJavadocCheckbox);
		return chooser;
	}

	protected ClassMember[] getAllOriginalMembers(PsiClass aClass) {
		PsiField[] fields = aClass.getFields();
		ArrayList<ClassMember> array = new ArrayList<>();
		for (PsiField field : fields) {
			if (field.hasModifierProperty(PsiModifier.STATIC)) continue;
			if (field.hasModifierProperty(PsiModifier.FINAL) && field.getInitializer() != null) continue;

			array.add(new PsiFieldMember(field));
		}
		return array.toArray(ClassMember.EMPTY_ARRAY);
	}

}