package com.github.flyhero.toolbox.bean2bean.ui;

import com.github.flyhero.toolbox.bean2bean.BeanFieldParser;
import com.github.flyhero.toolbox.model.Field;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class GeneratorPanel extends JFrame {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTextField pojoName;
	private JList fieldNames;
	private JLabel originClassName;
	private JLabel packageName;

	private Project project;

	private PsiClass psiClass;

	private PsiPackage selectedPackage;

	private DefaultListModel<Field> model;
	
	public GeneratorPanel(Project project, PsiClass psiClass, PsiPackage selectedPackage) {
		this.project = project;
		this.psiClass = psiClass;
		this.selectedPackage = selectedPackage;

		setContentPane(contentPane);
		getRootPane().setDefaultButton(buttonOK);

		packageName.setText(selectedPackage.getQualifiedName());
		originClassName.setText(psiClass.getQualifiedName());
		fillFieldNames(psiClass);

		setSize(500, 600);
		setTitle("Bean Convert Bean");
		// 屏幕居中
		setLocationRelativeTo(null);
		setVisible(true);

		buttonOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Window[] ownedWindows = getOwnedWindows();
				for (Window window : ownedWindows) {
					window.requestFocus();
				}
				onOK();
			}
		});

		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});

		// call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});

		// call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	private void createJavaFile(Project project, PsiPackage selectedPackage) {

		PsiDirectory[] directories = selectedPackage.getDirectories();
		for (PsiDirectory directory : directories) {
			WriteCommandAction.runWriteCommandAction(project, () -> {
				PsiElementFactory instance = PsiElementFactory.getInstance(directory.getProject());
				PsiClass aClass = instance.createClass(pojoName.getText());
				List<Field> list = fieldNames.getSelectedValuesList();
				for (Field o : list) {
					PsiElementFactory factory = PsiElementFactory.getInstance(aClass.getProject());
					PsiField field = factory.createField(o.getName(), PsiType.getTypeByName(o.getType(), project, GlobalSearchScope.allScope(project)));
//					PsiComment comment = factory.createCommentFromText("//"+o.getComment(), field);
//					aClass.add(comment);
					aClass.add(field);
				}
				directory.add(aClass);
			});
		}
	}

	private void fillFieldNames(PsiClass psiClass) {
		if (model == null) {
			model = new DefaultListModel();
		}
		model.clear();
		BeanFieldParser parser = new BeanFieldParser();
		List<Field> fieldList = parser.getSimpleFieldList(psiClass, true);
		for (Field field : fieldList) {
			model.addElement(field);
		}

		fieldNames.setModel(model);
		fieldNames.setCellRenderer(new ListCellRenderer<>() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				Field field = (Field) value;
				JLabel jLabel = new JLabel();
				jLabel.setIcon(AllIcons.Nodes.Field);
				jLabel.setText(field.name + " : " + field.getType());
				return jLabel;
			}
		});
		fieldNames.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		fieldNames.setSelectedIndex(0);
		fieldNames.setVisibleRowCount(10);
	}


	private void onOK() {
		createJavaFile(project, selectedPackage);
		dispose();
	}

	private void onCancel() {
		// add your code here if necessary
		dispose();
	}

//	public static void main(String[] args) {
//		PojoPanel dialog = new PojoPanel();
//		dialog.pack();
//		dialog.setVisible(true);
//		System.exit(0);
//	}
}
