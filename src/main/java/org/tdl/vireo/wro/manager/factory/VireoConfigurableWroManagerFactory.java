package org.tdl.vireo.wro.manager.factory;

import java.util.Properties;

import org.tdl.vireo.utility.FileIOUtility;
import org.tdl.vireo.wro.model.resource.locator.SassClassPathUriLocator;

import edu.tamu.weaver.wro.manager.factory.CustomConfigurableWroManagerFactory;
import edu.tamu.weaver.wro.service.ThemeManagerService;
import ro.isdc.wro.model.resource.locator.ClasspathUriLocator;
import ro.isdc.wro.model.resource.locator.ServletContextUriLocator;
import ro.isdc.wro.model.resource.locator.UrlUriLocator;
import ro.isdc.wro.model.resource.locator.factory.SimpleUriLocatorFactory;
import ro.isdc.wro.model.resource.locator.factory.UriLocatorFactory;

public class VireoConfigurableWroManagerFactory extends CustomConfigurableWroManagerFactory {
    private FileIOUtility fileIOUtility;

	public VireoConfigurableWroManagerFactory(Properties props, ThemeManagerService themeManagerService,FileIOUtility fileIOUtility) {
		super(props, themeManagerService);
		this.fileIOUtility = fileIOUtility;
	}
	
	protected UriLocatorFactory newUriLocatorFactory() {
		return new SimpleUriLocatorFactory().addLocator(new SassClassPathUriLocator(fileIOUtility)).addLocator(new ServletContextUriLocator()).addLocator(new ClasspathUriLocator()).addLocator(new UrlUriLocator());
	}
	
}
