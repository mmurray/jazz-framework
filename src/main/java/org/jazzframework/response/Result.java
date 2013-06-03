package org.jazzframework.response;

import java.util.HashMap;

import com.google.common.base.Function;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
public class Result extends HashMap<String, Object> {
	private String template;
	public void setTemplate(String template) {
		this.template = template;
	}
	public String getTemplate() {
		return template;
	}
}