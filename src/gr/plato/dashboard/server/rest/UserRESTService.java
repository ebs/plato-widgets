package gr.plato.dashboard.server.rest;

import gr.plato.dashboard.server.rest.exceptions.RestException;
import gr.plato.dashboard.service.model.User;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Stateless
@Path("/user")
public class UserRESTService extends RESTService {

	@GET
	@Path("/{id:[0-9][0-9]*}")
	public User get(@HeaderParam(TOKEN_HEADER) String authToken, @PathParam("id") Long id) throws RestException {
		getLoggedOn(authToken);
		try {
			User u = (User) em.createQuery(
				"from User u " +
					"where u.id=:id")
				.setParameter("id", id)
				.getSingleResult();
			return u;
		} catch (NoResultException e) {
			throw new RestException(Status.NOT_FOUND, "wrong.id");
		}

	}

	@GET
	@Path("/loggedon")
	public Response getLoggedOn(@Context HttpServletRequest request) {
		String authToken = request.getHeader(TOKEN_HEADER);
		if (authToken == null && request.getCookies() != null) {
			for (Cookie c : request.getCookies()) {
				if (c.getName().equals("_plato_a")) {
					authToken = c.getValue();
					break;
				}
			}
		}
		User u = super.getLoggedOn(authToken);
		return Response.status(200)
			.header(TOKEN_HEADER, u.getAuthToken())
			.cookie(new NewCookie("_plato_a", u.getAuthToken(), "/", null, null, NewCookie.DEFAULT_MAX_AGE, false))
			.entity(u)
			.build();
	}

	@PUT
	@Path("/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response login(@FormParam("username") String username, @FormParam("password") String password) {
		try {
			User u = (User) em.createQuery(
				"from User u " +
					"where u.username = :username")
				.setParameter("username", username)
				.getSingleResult();

			if (u.getPassword().equals(User.encodePassword(password, u.getPasswordSalt()))) {
				u.setAuthToken(u.generateAuthenticationToken());
				u = em.merge(u);
				return Response.status(200)
					.header(TOKEN_HEADER, u.getAuthToken())
					.cookie(new NewCookie("_plato_a", u.getAuthToken(), "/", null, null, NewCookie.DEFAULT_MAX_AGE, false))
					.entity(u)
					.build();
			} else {
				throw new RestException(Status.UNAUTHORIZED, "login.wrong.password");
			}
		} catch (NoResultException e) {
			throw new RestException(Status.UNAUTHORIZED, "login.wrong.username");
		}
	}

}
