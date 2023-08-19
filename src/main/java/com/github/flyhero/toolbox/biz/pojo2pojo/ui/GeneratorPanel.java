package com.github.flyhero.toolbox.biz.pojo2pojo.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import com.github.flyhero.toolbox.biz.pojo2pojo.BeanFieldParser;
import com.github.flyhero.toolbox.model.Field;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PropertyUtilBase;

public class GeneratorPanel extends JFrame {
	private JPanel contentPane;
	private JTextField pojoName;
	private JList fieldNames;
	private JLabel originClassName;
	private JLabel packageName;
	private JButton OKButton;
	private JButton cancelButton;

	private final Project project;

	private final PsiPackage selectedPackage;

	private DefaultListModel<Field> model;
	
	public GeneratorPanel(Project project, PsiClass psiClass, PsiPackage selectedPackage) {
		this.project = project;
		this.selectedPackage = selectedPackage;

		setContentPane(contentPane);
		getRootPane().setDefaultButton(OKButton);

		packageName.setText(selectedPackage.getQualifiedName());
		originClassName.setText(psiClass.getQualifiedName());
		fillFieldNames(psiClass);

		setSize(500, 600);
		setTitle("POJO to other POJO");
		// 屏幕居中
		setLocationRelativeTo(null);
		setVisible(true);

		OKButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				Window[] ownedWindows = getOwnedWindows();
//				for (Window window : ownedWindows) {
//					window.requestFocus();
//				}
				onOK();
			}
		});

		cancelButton.addActionListener(new ActionListener() {
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
//					PsiMethod getterMethod = GenerationUtils.generateGetterPrototype(project, field);
//					PsiElement getter = containingClass.add(getterMethod);
//					PsiMethod getterPrototype = PropertyUtilBase.generateGetterPrototype(field);
//					PsiMethod getter = aClass.findMethodBySignature(getterPrototype, true);
					aClass.add(field);
				}
				PsiField[] fields = aClass.getFields();
				for (PsiField field : fields) {
					PsiMethod getterPrototype = PropertyUtilBase.generateGetterPrototype(field);
					aClass.add(getterPrototype);
					PsiMethod setterPrototype = PropertyUtilBase.generateSetterPrototype(field);
					aClass.add(setterPrototype);
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
//		fieldNames.setSize(400, 500);
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

	private void createUIComponents() {
		// TODO: place custom component creation code here
	}

//	public static void main(String[] args) {
//		PojoPanel dialog = new PojoPanel();
//		dialog.pack();
//		dialog.setVisible(true);
//		System.exit(0);
//	}
}
