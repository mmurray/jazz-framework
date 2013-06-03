package org.jazzframework.response.mustache;

import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.xml.ws.RequestWrapper;

import org.jazzframework.response.HtmlMessageBodyWriter;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.RequestScoped;
import com.sun.jersey.api.core.ExtendedUriInfo;

public class MustacheModule extends AbstractModule {
	
	private final List<String> clientsideDirs = Lists.newArrayList();
	private String rootDir;
	private boolean liveCompilation = false;
	private Function<String, String> clientsidePreProcessor;
	
	protected void configureMustache() {}
	
	protected void setRootDirectory(String dir) {
		rootDir = dir;
	}
	
	protected void addClientsideTemplateDirectory(String dir) {
		clientsideDirs.add(dir);
	}
	
	protected void setClientsideTemplatePreprocessor(Function<String, String> preprocessor) {
		clientsidePreProcessor = preprocessor;
	}
	
	protected void setLiveCompilation(boolean live) {
		liveCompilation = live;
	}

	@Override
	protected void configure() {
		System.out.println("######");
		System.out.println("### CONFIGURE MOUSTACHE!!!");
		System.out.println("######");
		System.out.println("######");
		System.out.println("######");
		configureMustache();

		if (rootDir == null) {
			throw new RuntimeException("Must configure a template directory.");
		}
	}
	
	@Provides
	@Singleton
	HtmlMessageBodyWriter provideMessageBodyWriter(
			MustacheViewProcessor viewProcessor,
			Provider<ExtendedUriInfo> uriInfoProvider) {
		return new HtmlMessageBodyWriter(viewProcessor, uriInfoProvider);
	}
	
	@Provides
	@Singleton
	MustacheViewProcessor provideViewProcessor(
			ExecutorServiceHolder executorService) {
		return new MustacheViewProcessor(rootDir, liveCompilation, executorService.value);
	}
	
	@Provides
	@RequestScoped
	@Named("mustache") Writer provideWriter(
			MustacheViewProcessor viewProcessor) throws Exception {
		Writer writer = viewProcessor.getWriter();
		if (writer == null) {
			throw new Exception("Attempted to inject the response writer before it was created.");
		}
		return viewProcessor.getWriter();
	}
	
	static class ExecutorServiceHolder {
		@Inject(optional=true) ExecutorService value = Executors.newCachedThreadPool();
	}
}
