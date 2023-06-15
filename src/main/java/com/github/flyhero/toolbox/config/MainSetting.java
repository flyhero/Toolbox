package com.github.flyhero.toolbox.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@State(
		name = "Toolbox.Settings",
		storages = {
				@Storage(value = "$APP_CONFIG$/toolbox.settings.xml")
		}
)
public class MainSetting implements PersistentStateComponent<MainSetting.SettingProperties> {

	public SettingProperties myProperties = new SettingProperties();

	public static MainSetting getInstance() {
		return ServiceManager.getService(MainSetting.class);
	}

	@Nullable
	@Override
	public MainSetting.SettingProperties getState() {
		return myProperties;
	}

	@Override
	public void loadState(@NotNull MainSetting.SettingProperties state) {
		myProperties = state;
	}

	public static class SettingProperties {
		private Boolean pojo2PojoRadio = true;
		private Boolean entity2ConstantRadio = true;
		private Boolean interface2ImplRadio = true;
		private Boolean autowiredRadio = true;
		private Boolean pojo2JsonRadio = true;
		private Boolean json2PojoRadio = true;

		public Boolean getPojo2PojoRadio() {
			return pojo2PojoRadio;
		}

		public void setPojo2PojoRadio(Boolean pojo2PojoRadio) {
			this.pojo2PojoRadio = pojo2PojoRadio;
		}

		public Boolean getEntity2ConstantRadio() {
			return entity2ConstantRadio;
		}

		public void setEntity2ConstantRadio(Boolean entity2ConstantRadio) {
			this.entity2ConstantRadio = entity2ConstantRadio;
		}

		public Boolean getInterface2ImplRadio() {
			return interface2ImplRadio;
		}

		public void setInterface2ImplRadio(Boolean interface2ImplRadio) {
			this.interface2ImplRadio = interface2ImplRadio;
		}

		public Boolean getAutowiredRadio() {
			return autowiredRadio;
		}

		public void setAutowiredRadio(Boolean autowiredRadio) {
			this.autowiredRadio = autowiredRadio;
		}

		public Boolean getPojo2JsonRadio() {
			return pojo2JsonRadio;
		}

		public void setPojo2JsonRadio(Boolean pojo2JsonRadio) {
			this.pojo2JsonRadio = pojo2JsonRadio;
		}

		public Boolean getJson2PojoRadio() {
			return json2PojoRadio;
		}

		public void setJson2PojoRadio(Boolean json2PojoRadio) {
			this.json2PojoRadio = json2PojoRadio;
		}
	}
}
