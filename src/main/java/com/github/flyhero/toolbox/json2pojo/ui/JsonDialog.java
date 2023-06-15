package com.github.flyhero.toolbox.json2pojo.ui;

import com.github.flyhero.toolbox.json2pojo.ConvertBridge;
import com.github.flyhero.toolbox.json2pojo.common.JsonUtils;
import com.github.flyhero.toolbox.json2pojo.common.PsiClassUtil;
import com.github.flyhero.toolbox.json2pojo.common.StringUtils;
import com.github.flyhero.toolbox.json2pojo.common.SystemUtils;
import com.github.flyhero.toolbox.json2pojo.config.Config;
import com.github.flyhero.toolbox.json2pojo.tools.json.JSONArray;
import com.github.flyhero.toolbox.json2pojo.tools.json.JSONException;
import com.github.flyhero.toolbox.json2pojo.tools.json.JSONObject;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import org.apache.http.util.TextUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Objects;

public class JsonDialog extends JFrame implements ConvertBridge.Operator {

	private CardLayout cardLayout;

	private JPanel contentPane2;
	private JButton okButton;
	private JButton cancelButton;
	private JLabel errorLB;
	private JTextArea editTP;
	private JButton settingButton;

	private JLabel generateClassLB;
	private JTextField generateClassTF;
	private JPanel generateClassP;
	private JButton formatBtn;
	private JTextArea commentAR;
	private JButton copyJsonButton;
	private JButton copyCommentButton;

	private PsiClass cls;
	private PsiFile file;
	private Project project;
	private String errorInfo = null;
	private String currentClass = null;

	private PsiDirectory psiDirectory;

	public JsonDialog(PsiDirectory psiDirectory, PsiClass cls, PsiFile file, Project project) throws HeadlessException {
		this.psiDirectory = psiDirectory;
		this.cls = cls;
		this.file = file;
		this.project = project;
		//设置内容面板
		setContentPane(contentPane2);
		setTitle("GsonFormatPlus");
		getRootPane().setDefaultButton(okButton);
		this.setAlwaysOnTop(true);
		//初始化面板
		boolean b = initGeneratePanel(file);
		//初始化监听器
		initListener();

		if (b) {
			this.setSize(1000, 700);
			this.setLocationRelativeTo(null);
			this.setVisible(true);
		}
	}

	private boolean initGeneratePanel(PsiFile file) {

		cardLayout = (CardLayout) generateClassP.getLayout();
		generateClassTF.setBackground(errorLB.getBackground());
		//获取当前Class
		if (Objects.nonNull(psiDirectory)) {
			String canonicalPath = psiDirectory.getVirtualFile().getCanonicalPath();
			if (!canonicalPath.contains("src")) {
				NotificationCenter.sendNotificationForProject("所选路径不合适", NotificationType.ERROR, project);
				return false;
			}
			String src = canonicalPath.split("src")[1];
			currentClass = src.replace(File.separatorChar, '.') + ".Root";
		} else {
			currentClass = ((PsiJavaFileImpl) file).getPackageName() + "." + file.getName().split("\\.")[0];
		}
		generateClassLB.setText(currentClass);
		generateClassTF.setText(currentClass);
		//生成类输入框焦点监听器
		generateClassTF.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent focusEvent) {
			}

			@Override
			public void focusLost(FocusEvent focusEvent) {
				cardLayout.next(generateClassP);
				if (TextUtils.isEmpty(generateClassTF.getText())) {
					generateClassLB.setText(currentClass);
					generateClassTF.setText(currentClass);
				} else {
					generateClassLB.setText(generateClassTF.getText());
				}
			}
		});
		//生成类标签鼠标监听器
		generateClassLB.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				super.mouseClicked(mouseEvent);
				cardLayout.next(generateClassP);
				if (generateClassLB.getText().equals(currentClass)
						&& !TextUtils.isEmpty(Config.getInstant().getEntityPackName())
						&& !Config.getInstant().getEntityPackName().equals("null")) {
					generateClassLB.setText(Config.getInstant().getEntityPackName());
					generateClassTF.setText(Config.getInstant().getEntityPackName());
				}
				generateClassTF.requestFocus(true);
			}

		});
		return true;
	}

	private void initListener() {

		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (generateClassTF.isFocusOwner()) {
					editTP.requestFocus(true);
				} else {
					onOK();
				}
			}
		});
		//格式化按钮监听器
		formatBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String json = editTP.getText();
				json = json.trim();
				try {
					if (json.startsWith("{")) {
						JSONObject jsonObject = new JSONObject(json);
						String formatJson = jsonObject.toString(4);
						editTP.setText(formatJson);
					} else if (json.startsWith("[")) {
						JSONArray jsonArray = new JSONArray(json);
						String formatJson = jsonArray.toString(4);
						editTP.setText(formatJson);
					}
				} catch (JSONException jsonException) {
					try {
						String goodJson = JsonUtils.removeComment(json);
						String formatJson = JsonUtils.formatJson(goodJson);
						editTP.setText(formatJson);
						commentAR.setText(JsonUtils.getJsonComment(json, formatJson));
					} catch (Exception exception) {
						exception.printStackTrace();
						NotificationCenter.sendNotificationForProject("json格式不正确，格式需要标准的json或者json5", NotificationType.ERROR, project);
						return;
					}
				}

			}
		});
//        editTP.addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyReleased(KeyEvent keyEvent) {
//                super.keyReleased(keyEvent);
//                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
//                    onOK();
//                }
//            }
//        });
		generateClassP.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent keyEvent) {
				super.keyReleased(keyEvent);
				if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
					editTP.requestFocus(true);
				}
			}
		});
		errorLB.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				super.mouseClicked(mouseEvent);
				if (errorInfo != null) {
					NotificationCenter.sendNotification(errorInfo, NotificationType.ERROR);
				}
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		settingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openSettingDialog();
			}
		});
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});
		contentPane2.registerKeyboardAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		copyJsonButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String json = editTP.getText().trim();
				if (!StringUtils.isNotBlank(json)) {
					return;
				}
				String formatJson = "";
				try {
					if (json.startsWith("{")) {
						JSONObject jsonObject = new JSONObject(json);
						formatJson = jsonObject.toString(4);
						editTP.setText(formatJson);
					} else if (json.startsWith("[")) {
						JSONArray jsonArray = new JSONArray(json);
						formatJson = jsonArray.toString(4);
						editTP.setText(formatJson);
					}
					SystemUtils.copyToClipboard(formatJson);
					NotificationCenter.sendNotificationForProject(" Copy json success !", NotificationType.INFORMATION, project);
				} catch (JSONException jsonException) {
					try {
						String goodJson = JsonUtils.removeComment(json);
						formatJson = JsonUtils.formatJson(goodJson);
						editTP.setText(formatJson);
						commentAR.setText(JsonUtils.getJsonComment(json, formatJson));
						SystemUtils.copyToClipboard(formatJson);
						NotificationCenter.sendNotificationForProject(" Copy json success !", NotificationType.INFORMATION, project);
					} catch (Exception exception) {
						formatJson = "";
						exception.printStackTrace();
						NotificationCenter.sendNotificationForProject("json格式不正确，格式需要标准的json或者json5", NotificationType.ERROR, project);
						return;
					}
				}
			}
		});

		copyCommentButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String jsonComment = commentAR.getText().trim();
				if (!StringUtils.isNotBlank(jsonComment)) {
					return;
				}
				SystemUtils.copyToClipboard(jsonComment);
				NotificationCenter.sendNotificationForProject("Copy jsonComment success", NotificationType.INFORMATION, project);
			}
		});

	}

	private void onOK() {


		this.setAlwaysOnTop(false);
		String jsonSTR = editTP.getText().trim();
		String jsonComment = commentAR.getText().trim();
		if (TextUtils.isEmpty(jsonSTR)) {
			return;
		}
		//生成类名
		String generateClassName = generateClassTF.getText().replaceAll(" ", "").replaceAll(".java$", "");
		if (TextUtils.isEmpty(generateClassName) || generateClassName.endsWith(".")) {
			Toast.make(project, generateClassP, MessageType.ERROR, "the path is not allowed");
			return;
		}
		PsiClass generateClass = null;
		if (Objects.isNull(file)) {
			WriteCommandAction.runWriteCommandAction(project, () -> {
				PsiElementFactory instance = PsiElementFactory.getInstance(project);
				PsiClass aClass = instance.createClass(generateClassName);
				psiDirectory.add(aClass);
			});
		} else {
			if (!currentClass.equals(generateClassName)) {
				generateClass = PsiClassUtil.exist(file, generateClassTF.getText());
			} else {
				generateClass = cls;
			}
		}
		file = psiDirectory.findFile(generateClassName + ".java");
		System.out.println("扎到吗？" + file.getText());
		//执行转换
		new ConvertBridge(this, jsonSTR, jsonComment, file, project, generateClass,
				cls, generateClassName).run();
	}

	private void onCancel() {
		dispose();
	}

	public PsiClass getClss() {
		return cls;
	}

	public void setClass(PsiClass mClass) {
		this.cls = mClass;
	}

	public void setProject(Project mProject) {
		this.project = mProject;
	}

	public void setFile(PsiFile mFile) {
		this.file = mFile;
	}

	private void createUIComponents() {

	}

	/**
	 * 显示设置弹框
	 */
	public void openSettingDialog() {

		SettingDialog settingDialog = new SettingDialog(project);
		settingDialog.setSize(900, 720);
		settingDialog.setLocationRelativeTo(null);
//        settingDialog.setResizable(false);
		settingDialog.setVisible(true);
	}

	@Override
	public void cleanErrorInfo() {
		errorInfo = null;
	}

	@Override
	public void setErrorInfo(String error) {
		errorInfo = error;
	}

	@Override
	public void showError(ConvertBridge.Error err) {
		switch (err) {
			case DATA_ERROR:
				errorLB.setText("data err !!");
				if (Config.getInstant().isToastError()) {
					Toast.make(project, errorLB, MessageType.ERROR, "click to see details");
				}
				break;
			case PARSE_ERROR:
				errorLB.setText("parse err !!");
				if (Config.getInstant().isToastError()) {
					Toast.make(project, errorLB, MessageType.ERROR, "click to see details");
				}
				break;
			case PATH_ERROR:
				Toast.make(project, generateClassP, MessageType.ERROR, "the path is not allowed");
				break;
			default:
				break;
		}
	}

}
