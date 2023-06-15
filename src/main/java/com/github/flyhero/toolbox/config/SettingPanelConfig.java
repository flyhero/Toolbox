package com.github.flyhero.toolbox.config;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SettingPanelConfig implements SearchableConfigurable {

	private final SettingPanel settingPanel;

	private final MainSetting mainSetting;

	public SettingPanelConfig() {
		this.settingPanel = new SettingPanel();
		this.mainSetting = MainSetting.getInstance();
	}

	@NotNull
	@Override
	public String getId() {
		return "com.github.flyhero.toolbox.config.toolbox-plugin";
	}

	@Nls(capitalization = Nls.Capitalization.Title)
	@Override
	public String getDisplayName() {
		return "Toolbox";
	}

	@Nullable
	@Override
	public JComponent createComponent() {
		return settingPanel.getMainPanel();
	}

	@Override
	public boolean isModified() {
		MainSetting.SettingProperties myProperties = mainSetting.myProperties;
		if (myProperties.getPojo2PojoRadio() != settingPanel.getPojo2PojoRadio().isSelected()) {
			return true;
		}

		if (myProperties.getAutowiredRadio() != settingPanel.getAutowiredRadio().isSelected()) {
			return true;
		}
		if (myProperties.getEntity2ConstantRadio() != settingPanel.getEntity2ConstantRadio().isSelected()) {
			return true;
		}
		if (myProperties.getInterface2ImplRadio() != settingPanel.getInterface2ImplRadio().isSelected()) {
			return true;
		}
		if (myProperties.getJson2PojoRadio() != settingPanel.getJson2PojoRadio().isSelected()) {
			return true;
		}
		return myProperties.getPojo2JsonRadio() != settingPanel.getPojo2JsonRadio().isSelected();
	}

	@Override
	public void apply() throws ConfigurationException {
		MainSetting.SettingProperties myProperties = mainSetting.myProperties;
		myProperties.setPojo2PojoRadio(settingPanel.getPojo2PojoRadio().isSelected());
		myProperties.setAutowiredRadio(settingPanel.getAutowiredRadio().isSelected());
		myProperties.setEntity2ConstantRadio(settingPanel.getEntity2ConstantRadio().isSelected());
		myProperties.setInterface2ImplRadio(settingPanel.getInterface2ImplRadio().isSelected());
		myProperties.setPojo2JsonRadio(settingPanel.getPojo2JsonRadio().isSelected());
	}

	@Override
	public void reset() {
		MainSetting.SettingProperties myProperties = mainSetting.myProperties;
		settingPanel.getAutowiredRadio().setSelected((myProperties.getAutowiredRadio()));
		settingPanel.getInterface2ImplRadio().setSelected((myProperties.getInterface2ImplRadio()));
		settingPanel.getEntity2ConstantRadio().setSelected((myProperties.getEntity2ConstantRadio()));
		settingPanel.getPojo2JsonRadio().setSelected((myProperties.getPojo2JsonRadio()));
		settingPanel.getPojo2PojoRadio().setSelected((myProperties.getPojo2PojoRadio()));
	}
}
