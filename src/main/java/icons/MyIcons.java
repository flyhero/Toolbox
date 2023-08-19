package icons;

import javax.swing.Icon;

import com.intellij.openapi.util.IconLoader;

public interface MyIcons {

	Icon tool = IconLoader.getIcon("/icons/tool.svg", MyIcons.class);
	Icon pojo = IconLoader.getIcon("/icons/pojo.svg", MyIcons.class);

	Icon spring = IconLoader.getIcon("/icons/spring.svg", MyIcons.class);
	Icon serialVersion = IconLoader.getIcon("/icons/serialVersion.svg", MyIcons.class);

}
