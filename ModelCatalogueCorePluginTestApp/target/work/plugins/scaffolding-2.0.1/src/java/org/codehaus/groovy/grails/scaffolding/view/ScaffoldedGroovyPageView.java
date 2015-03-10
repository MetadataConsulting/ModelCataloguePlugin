/*
 * Copyright 2004-2013 SpringSource.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.groovy.grails.scaffolding.view;

import groovy.lang.Writable;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.grails.web.pages.GroovyPageTemplate;
import org.codehaus.groovy.grails.web.pages.GroovyPagesTemplateEngine;
import org.codehaus.groovy.grails.web.servlet.view.GroovyPageView;
import org.codehaus.groovy.grails.web.sitemesh.GrailsLayoutDecoratorMapper;
import org.springframework.util.Assert;

/**
 * A special Spring View for scaffolding that renders an in-memory scaffolded view to the response.
 *
 * @author Graeme Rocher
 * @since 0.5
 */
public class ScaffoldedGroovyPageView extends GroovyPageView {

	private String contents;
	protected static final Log log = LogFactory.getLog(ScaffoldedGroovyPageView.class);

	public ScaffoldedGroovyPageView(String uri, String contents) {
		Assert.hasLength(contents, "Argument [contents] cannot be blank or null");
		Assert.hasLength(uri, "Argument [uri] cannot be blank or null");

		this.contents = contents;
		setUrl(uri);
	}

	/**
	 * Used for debug reporting.
	 *
	 * @return The URL of the view
	 */
	@Override
	public String getBeanName() {
		return getUrl();
	}

	/**
	 * Overrides the default implementation to render a GSP view using an in-memory representation
	 * held in the #contents property.
	 *
	 * @param templateEngine The GroovyPagesTemplateEngine instance
	 * @param model The model
	 * @param response The HttpServletResponse instance
	 *
	 * @throws IOException Thrown if there was an IO error rendering the view
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected void renderWithTemplateEngine(GroovyPagesTemplateEngine templateEngine, Map model,
			HttpServletResponse response, HttpServletRequest request) throws IOException {

		if (log.isDebugEnabled()) {
			log.debug("Rendering scaffolded view [" + getUrl() + "] with model [" + model + "]");
		}

		request.setAttribute(GrailsLayoutDecoratorMapper.RENDERING_VIEW, true);
		Writable w = template.make(model);
		Writer out = null;
		try {
			out = createResponseWriter(response);
			w.writeTo(out);
		}
		catch(Exception e) {
			handleException(e, templateEngine);
		}
		finally {
			if (out != null) {
				out.close();
			}
		}
	}

	@Override
	protected void initTemplate() throws IOException {
		template = templateEngine.createTemplate(contents, getUrl());
		if (template instanceof GroovyPageTemplate) {
			((GroovyPageTemplate)template).setAllowSettingContentType(true);
		}
	}
}
