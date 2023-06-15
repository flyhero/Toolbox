package com.github.flyhero.toolbox.config;


import com.intellij.openapi.components.ServiceManager;

import javax.swing.*;


/**
 * @author breezes_y@163.com
 * @date 2021/2/6 19:35
 * @description
 */
public class SettingPanel {
	private JPanel mainPanel;
	private JPanel javaToolboxSettingPanel;
	private JPanel radioControlPanel;
	private JRadioButton pojo2PojoRadio;

	private JRadioButton entity2ConstantRadio;
	private JRadioButton interface2ImplRadio;
	private JRadioButton autowiredRadio;
	private JRadioButton pojo2JsonRadio;
	private JRadioButton json2PojoRadio;

	private MainSetting.SettingProperties properties;

	public SettingPanel() {
		MainSetting service = ServiceManager.getService(MainSetting.class);
		this.properties = service.myProperties;

		radioInit();
	}

	private void radioInit() {
		pojo2PojoRadio.setSelected(properties.getPojo2PojoRadio());
		entity2ConstantRadio.setSelected(properties.getEntity2ConstantRadio());
		autowiredRadio.setSelected(properties.getAutowiredRadio());
		interface2ImplRadio.setSelected(properties.getInterface2ImplRadio());
		pojo2JsonRadio.setSelected(properties.getPojo2JsonRadio());
		json2PojoRadio.setSelected(properties.getJson2PojoRadio());
	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public void setMainPanel(JPanel mainPanel) {
		this.mainPanel = mainPanel;
	}


	public JPanel getTranslationSettingPanel() {
		return javaToolboxSettingPanel;
	}

	public void setTranslationSettingPanel(JPanel translationSettingPanel) {
		this.javaToolboxSettingPanel = translationSettingPanel;
	}

	public JPanel getMapControlPanel() {
		return radioControlPanel;
	}

	public void setMapControlPanel(JPanel mapControlPanel) {
		this.radioControlPanel = mapControlPanel;
	}

	public JRadioButton getPojo2PojoRadio() {
		return pojo2PojoRadio;
	}

	public void setPojo2PojoRadio(JRadioButton pojo2PojoRadio) {
		this.pojo2PojoRadio = pojo2PojoRadio;
	}

	public JRadioButton getEntity2ConstantRadio() {
		return entity2ConstantRadio;
	}

	public void setEntity2ConstantRadio(JRadioButton entity2ConstantRadio) {
		this.entity2ConstantRadio = entity2ConstantRadio;
	}

	public JRadioButton getInterface2ImplRadio() {
		return interface2ImplRadio;
	}

	public void setInterface2ImplRadio(JRadioButton interface2ImplRadio) {
		this.interface2ImplRadio = interface2ImplRadio;
	}

	public JRadioButton getAutowiredRadio() {
		return autowiredRadio;
	}

	public void setAutowiredRadio(JRadioButton autowiredRadio) {
		this.autowiredRadio = autowiredRadio;
	}

	public JRadioButton getPojo2JsonRadio() {
		return pojo2JsonRadio;
	}

	public void setPojo2JsonRadio(JRadioButton pojo2JsonRadio) {
		this.pojo2JsonRadio = pojo2JsonRadio;
	}

	public MainSetting.SettingProperties getProperties() {
		return properties;
	}

	public void setProperties(MainSetting.SettingProperties properties) {
		this.properties = properties;
	}

	public JRadioButton getJson2PojoRadio() {
		return json2PojoRadio;
	}

	public void setJson2PojoRadio(JRadioButton json2PojoRadio) {
		this.json2PojoRadio = json2PojoRadio;
	}
}
