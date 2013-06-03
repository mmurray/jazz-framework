package org.jazzframework.response;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutorService;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;

import org.jazzframework.response.mustache.MustacheViewProcessor;

import com.google.common.base.CaseFormat;
import com.google.inject.Provider;
import com.sun.jersey.api.core.ExtendedUriInfo;
import com.sun.jersey.api.view.Viewable;

@javax.ws.rs.ext.Provider
@Produces("text/html")
public class HtmlMessageBodyWriter implements MessageBodyWriter {

	private MustacheViewProcessor viewProcessor;
	private Provider<ExtendedUriInfo> uriInfoProvider;

	public HtmlMessageBodyWriter(
			MustacheViewProcessor viewProcessor,
			Provider<ExtendedUriInfo> uriInfoProvider) {
		this.viewProcessor = viewProcessor;
		this.uriInfoProvider = uriInfoProvider;
	}

    public long getSize(Object obj, Class type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    public boolean isWriteable(Class type, Type genericType,
            Annotation annotations[], MediaType mediaType) {
        return true;
    }

    public void writeTo(Object target, Class type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap httpHeaders, OutputStream outputStream)
            throws IOException {

    	ExtendedUriInfo uriInfo = uriInfoProvider.get();

    	String action = uriInfo.getMatchedMethod().getMethod().getName();
    	String controller = uriInfo.getMatchedMethod().getResource().getResourceClass()
    			.getSimpleName().replace("Controller", "");
    
    	String template = String.format("/%s/%s.html.mustache",
    			CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, controller),
    			action);
    	
    	if (type.equals(Result.class)) {
    		String resultTemplate = ((Result)target).getTemplate();
    		if (resultTemplate != null) {
    			template = resultTemplate;
    		}
    	}

    	Viewable viewable = (type.equals(Viewable.class)) ?
    		(Viewable)target : new Viewable(template, target);
    	viewProcessor.writeTo(viewProcessor.resolve(viewable.getTemplateName()), viewable, outputStream);
    }

}
