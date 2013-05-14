package gr.plato.dashboard.server.jaxrs.util;

import java.text.SimpleDateFormat;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

import com.fasterxml.jackson.module.hibernate.HibernateModule;

// Customized {@code ContextResolver} implementation to pass ObjectMapper to use
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JacksonContextResolver implements ContextResolver<ObjectMapper> {

	private ObjectMapper objectMapper;

	public JacksonContextResolver() throws Exception {
		// Use Jackson annotations as primary; use JAXB annotation as fallback.
		// See http://wiki.fasterxml.com/JacksonJAXBAnnotations
		AnnotationIntrospector primaryIntrospector = new JacksonAnnotationIntrospector();
		AnnotationIntrospector secondaryIntropsector = new JaxbAnnotationIntrospector();
		AnnotationIntrospector pair = new AnnotationIntrospector.Pair(primaryIntrospector, secondaryIntropsector);

		this.objectMapper = new ObjectMapper()
			.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false)
			.configure(SerializationConfig.Feature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false)
			.setAnnotationIntrospector(pair)
			.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
		this.objectMapper.setDateFormat(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"));
		this.objectMapper.registerModule(new HibernateModule());
	}

	public ObjectMapper getContext(Class<?> objectType) {
		return objectMapper;
	}
}
