package org.jazzframework;

import java.io.Writer;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.jazzframework.Annotations.Global;
import org.jazzframework.response.JsonMessageBodyWriter;
import org.jazzframework.response.Result;
import org.jazzframework.response.mustache.MustacheModule;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
//import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.RequestScoped;
import com.hazardousholdings.sprockets.Asset;
import com.hazardousholdings.sprockets.SprocketsModule;

public class JazzFrameworkModule extends AbstractModule {
	
	private final Environment env;
	
	public JazzFrameworkModule() {
		String prop = System.getProperty("env");
		if (Strings.isNullOrEmpty(prop)) {
			env = Environment.PRODUCTION;
		} else {
			switch(System.getProperty("env")) {
				case "dev":
					env = Environment.DEVELOPMENT;
					break;
				case "staging":
					env = Environment.STAGING;
					break;
				case "prod":
				default:
					env = Environment.PRODUCTION;
			}
		}
	}

	@Override
	protected void configure() {
		
		final boolean isDev = (env == Environment.DEVELOPMENT);
		
		// Sprockets module handles asset bundling & dependency resolution
		install(new SprocketsModule() {
			@Override
			protected void configureSprockets() {
//					setExternsPath("src/main/javascript/externs");

				if (isDev) {
					addPath("src/main/js");
					addPath("src/main/css");
					addPath("src/main/html");
					setOutputPath("src/main/webapp");
					setDebug(true);
				} else {
					addPath("webapp/js");
					addPath("webapp/css");
					addPath("webapp/html");
//					setUrlRoot("http://cdn.themusicbot.com");
					setLiveCompilation(false);
					setSkipCompile(true);
				}
			}
		});
		
		// MustacheModule extends jersey with a bunch of mustache.java support
		install(new MustacheModule() {
			@Override
			protected void configureMustache() {
				if (isDev) {
					setRootDirectory("src/main/html");
					setLiveCompilation(true);
				} else {
					setRootDirectory("webapp/html");
				}
			}
		});
	}
	
	@Provides
	@Singleton
	Environment provideEnvironment() {
		return env;
	}
	
	@Provides
	@Singleton
	JsonMessageBodyWriter provideJsonMessageBodyWriter() {
		return new JsonMessageBodyWriter();
	}
	
	@Provides
	@Singleton
	ExecutorService provideExecutorService() {
		return Executors.newCachedThreadPool();
	}
	
	@Provides
	@RequestScoped
	Map<String, Cookie> provideCookieMap(
			final HttpServletRequest request) {
		Map<String, Cookie> result = Maps.newHashMap();
		for (Cookie c : request.getCookies()) {
			result.put(c.getName(), c);
		}
		return result;
	}
	private static int abc = 1;
	
	@Provides
	@RequestScoped
    Result provideResult(
    		final @Named("mustache") Provider<Writer> writer,
			final Map<String, Asset> assets,
//			final Map<String, Cookie> cookies,
			final HttpServletRequest request,
			final @Global Result globalResult) {

		Result res = new Result();
		
		// Asset function
		res.put("asset", new Function<String, String>() {
			public String apply(@Nullable String key) {
				if (!assets.containsKey(key)) {
					throw new RuntimeException("Asset "+key+" does not exist.");
				}
				
//				if (request.getParameter("exp") != null &&
//						request.getParameter("exp").equals("rainbow") && 
//						key.equals("application.css")) {
//					key = "application_rainbow.css";
//				}else if (request.getParameter("exp") != null &&
//						request.getParameter("exp").equals("rainbow") && 
//						key.equals("search.css")) {
//					key = "search_rainbow.css";
//				}else {
//					System.out.println("NOT BMG!!!");
//					System.out.println(key);
//					System.out.println(request.getParameter("exp"));
//				}
				
				return assets.get(key).getTag();
			}
		});
		
		// Flush function
		res.put("flush", new Function<String, String>() {
			public String apply(@Nullable String input) {
				try{
					writer.get().flush();
				}catch(Exception e) {
					throw new RuntimeException(e);
				}
				return "";
			}
		});
		
		// Misc. globals
		res.put("copyYear", Calendar.getInstance().get(Calendar.YEAR));
		
		// App specific globals
		for (String key : globalResult.keySet()) {
			res.put(key, globalResult.get(key));
		}
		
		if (!Strings.isNullOrEmpty(globalResult.getTemplate())) {
			res.setTemplate(globalResult.getTemplate());
		}
		
		
		return res;
	}

	
}
