package org.plato.server.rest;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/measurement")
public class MeasurementService extends PlatoService {

	private static final Logger logger = Logger.getLogger(MeasurementService.class.getName());

	@GET
	@Path("{id}")
	@Produces("application/xml")
	public String getMeasurement(@PathParam("id") String id) {
		return super.getMeasurement(id);
	}

	@POST
	@Path("{id}/start")
	public void postMeasurementStart(@PathParam("id") String id) {
		startSampling(id);
	}

	@POST
	@Path("{id}/stop")
	public void postMeasurementStop(@PathParam("id") String id) {
		stopSampling(id);
	}
}
