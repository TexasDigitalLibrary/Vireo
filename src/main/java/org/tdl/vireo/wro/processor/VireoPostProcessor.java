package org.tdl.vireo.wro.processor;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.tdl.vireo.service.VireoThemeManager;

import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;

public class VireoPostProcessor implements ResourcePostProcessor {
	private VireoThemeManager themeManagerService;
	
	public VireoPostProcessor(VireoThemeManager themeManagerService) {
		super();
		this.themeManagerService = themeManagerService;
	}

	public void process(final Reader reader, final Writer writer) throws IOException {
		// read in the merged SCSS and add it after the custom SASS variables
		String resourceText = themeManagerService.getFormattedProperties();
		
		writer.append(resourceText);
		writer.append(IOUtils.toString(reader));
		// append any custom CSS last to maximize cascade-ability
		writer.append(themeManagerService.getCustomCss());
		reader.close();
		writer.close();
}
}
