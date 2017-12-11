package org.tdl.vireo.wro.manager.factory;

import java.util.Map;
import java.util.Properties;

import org.springframework.core.io.support.ResourcePatternResolver;
import org.tdl.vireo.service.VireoThemeManager;
import org.tdl.vireo.wro.processor.VireoPostProcessor;

import edu.tamu.weaver.wro.manager.factory.WeaverConfigurableWroManagerFactory;
import edu.tamu.weaver.wro.service.ThemeManager;
import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;

public class VireoConfigurableWroManagerFactory extends WeaverConfigurableWroManagerFactory {
	public VireoConfigurableWroManagerFactory(Properties props, ThemeManager themeManagerService, ResourcePatternResolver resourcePatternResolver) {
		super(props, themeManagerService,resourcePatternResolver);
	}

	@Override
	protected void contributePostProcessors(Map<String, ResourcePostProcessor> map) {
		map.put("vireoPostProcessor", new VireoPostProcessor((VireoThemeManager) getThemeManagerService()));
	}

	
}
