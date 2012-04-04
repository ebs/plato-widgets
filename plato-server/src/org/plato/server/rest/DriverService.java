package org.plato.server.rest;

import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.plato.server.model.Driver;

@Path("/driver")
public class DriverService extends PlatoService {

	private static final Logger logger = Logger.getLogger(DriverService.class.getName());

	@GET
	@Produces("application/xml")
	public List<Driver> getDrivers() {
		return getMBeanDrivers();
	}

	@GET
	@Path("{id}")
	@Produces("application/xml")
	public Driver getDriver(@PathParam("id") String id) {
		return getMBeanDriver(id);
	}

	@POST
	@Consumes("application/xml")
	public String postDriver(String sensorML) {
		return addMBeanDriver(sensorML);
	}

	@DELETE
	@Path("{id}")
	public void deleteDriver(@PathParam("id") String id) {
		removeMBeanDriver(id);
	}

}
