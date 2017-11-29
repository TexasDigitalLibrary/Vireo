package org.tdl.vireo.wro.manager.factory;

import java.util.Map;
import java.util.Properties;

import org.tdl.vireo.service.VireoThemeManagerService;
import org.tdl.vireo.utility.FileIOUtility;
import org.tdl.vireo.wro.model.resource.locator.SassClassPathUriLocator;
import org.tdl.vireo.wro.processor.VireoPostProcessor;

import edu.tamu.weaver.wro.manager.factory.CustomConfigurableWroManagerFactory;
import ro.isdc.wro.model.resource.locator.ClasspathUriLocator;
import ro.isdc.wro.model.resource.locator.ServletContextUriLocator;
import ro.isdc.wro.model.resource.locator.UrlUriLocator;
import ro.isdc.wro.model.resource.locator.factory.SimpleUriLocatorFactory;
import ro.isdc.wro.model.resource.locator.factory.UriLocatorFactory;
import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;

public class VireoConfigurableWroManagerFactory extends CustomConfigurableWroManagerFactory {
    private FileIOUtility fileIOUtility;
    private VireoThemeManagerService themeManagerService;

	public VireoConfigurableWroManagerFactory(Properties props, VireoThemeManagerService themeManagerService,FileIOUtility fileIOUtility) {
		super(props, themeManagerService);
		this.fileIOUtility = fileIOUtility;
		this.themeManagerService = themeManagerService;
	}
	
	@Override
	protected void contributePostProcessors(Map<String, ResourcePostProcessor> map) {
		map.put("vireoPostProcessor", new VireoPostProcessor(themeManagerService));
}
	
	protected UriLocatorFactory newUriLocatorFactory() {
		return new SimpleUriLocatorFactory().addLocator(new SassClassPathUriLocator(fileIOUtility)).addLocator(new ServletContextUriLocator()).addLocator(new ClasspathUriLocator()).addLocator(new UrlUriLocator());
	}
	
}
