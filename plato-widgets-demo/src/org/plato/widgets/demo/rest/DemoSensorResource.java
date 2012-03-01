package org.plato.widgets.demo.rest;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.plato.widgets.demo.DemoSensorData;

@Path("sensor")
public class DemoSensorResource {

	@GET
	@Path("{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response get(@PathParam("id") Long id) {
		DemoSensorData data = new DemoSensorData();
		data.setId(id);
		ResponseBuilder builder = Response.ok(data);
		builder.expires(new Date());
		return builder.build();
	}

}
