package org.jazzframework.response;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutorService;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;

import com.google.common.base.CaseFormat;
import com.google.gson.Gson;
import com.google.inject.Provider;
import com.sun.jersey.api.core.ExtendedUriInfo;
import com.sun.jersey.api.view.Viewable;

@javax.ws.rs.ext.Provider
@Produces("application/json")
public class JsonMessageBodyWriter implements MessageBodyWriter {

	public boolean isWriteable(Class type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return true;
	}

	public long getSize(Object t, Class type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	public void writeTo(Object t, Class type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap httpHeaders, OutputStream entityStream)
			throws IOException, WebApplicationException {
		String json = new Gson().toJson(t);
		entityStream.write(json.getBytes());
	}
	

}
