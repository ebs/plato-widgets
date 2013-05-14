package gr.plato.dashboard.server.rest;

import gr.plato.dashboard.server.rest.exceptions.RestException;
import gr.plato.dashboard.service.model.User;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;

import org.apache.commons.configuration.Configuration;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class RESTService {

	private static Logger log = Logger.getLogger(RESTService.class.getName());

	@Resource
	SessionContext sc;

	@PersistenceContext(unitName = "platodb")
	protected EntityManager em;

	@Context
	protected Providers providers;

	protected static Configuration conf;

	protected static final String TOKEN_HEADER = "X-Auth-Token";

	protected static final String ERROR_CODE_HEADER = "X-Error-Code";

	/******************************
	 * Login Functions ************
	 ******************************/

	protected User getLoggedOn(String authToken) throws RestException {
		if (authToken == null) {
			throw new RestException(Status.UNAUTHORIZED, "login.missing.token");
		}
		try {
			User user = (User) em.createQuery(
				"from User u " +
					"where u.authToken = :authToken")
				.setParameter("authToken", authToken)
				.getSingleResult();
			return user;
		} catch (NoResultException e) {
			throw new RestException(Status.UNAUTHORIZED, "login.invalid.token");
		}
	}

	/******************************
	 * JSON Utility Functions *****
	 ******************************/

	public String toJSON(Object object, Class<?> view) {
		try {
			ContextResolver<ObjectMapper> resolver = providers.getContextResolver(ObjectMapper.class, MediaType.APPLICATION_JSON_TYPE);
			ObjectMapper mapper = resolver.getContext(object.getClass());
			if (view == null) {
				return mapper.writeValueAsString(object);
			} else {
				return mapper.writerWithView(view).writeValueAsString(object);
			}
		} catch (JsonGenerationException e) {
			log.log(Level.SEVERE, "", e);
		} catch (JsonMappingException e) {
			log.log(Level.SEVERE, "", e);
		} catch (IOException e) {
			log.log(Level.SEVERE, "", e);
		} catch (Exception e) {
			log.log(Level.SEVERE, "", e);
		}
		return null;
	}

	public <T> T fromJSON(Class<T> clazz, String json) {
		try {
			ContextResolver<ObjectMapper> resolver = providers.getContextResolver(ObjectMapper.class, MediaType.APPLICATION_JSON_TYPE);
			ObjectMapper mapper = resolver.getContext(clazz);
			T result = mapper.readValue(json, clazz);
			return result;
		} catch (JsonGenerationException e) {
			log.log(Level.SEVERE, "", e);
		} catch (JsonMappingException e) {
			log.log(Level.SEVERE, "", e);
		} catch (IOException e) {
			log.log(Level.SEVERE, "", e);
		} catch (Exception e) {
			log.log(Level.SEVERE, "", e);
		}
		return null;
	}
}
