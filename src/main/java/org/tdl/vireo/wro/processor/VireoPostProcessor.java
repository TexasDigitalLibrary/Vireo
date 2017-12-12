package org.tdl.vireo.wro.processor;

import org.tdl.vireo.service.VireoThemeManager;

import edu.tamu.weaver.wro.processor.RepoPostProcessor;

public class VireoPostProcessor extends RepoPostProcessor {
	
	public VireoPostProcessor(VireoThemeManager themeManagerService) {
		super(themeManagerService);
	}

	@Override
	protected String getDynamicThemeContent() {
		String baseContent = super.getDynamicThemeContent();
		baseContent += "/* The Vireo ThemeManagerService provided the following custom CSS: */\n\n" + ((VireoThemeManager) getThemeManagerService()).getCustomCss() + "\n\n /* End custom CSS */";
		return baseContent;
	}
}
