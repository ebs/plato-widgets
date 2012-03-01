package org.plato.widgets.demo.rest;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;

import org.plato.widgets.demo.DemoSensorData;

@Provider
public class JAXBContextResolver implements ContextResolver<JAXBContext> {

    private JAXBContext context;
    private Class<?>[] types = {DemoSensorData.class};

    public JAXBContextResolver() throws Exception {
        JSONConfiguration jsonConfiguration = JSONConfiguration.natural()
        		.build();
        this.context = new JSONJAXBContext(jsonConfiguration, types);
    }

    public JAXBContext getContext(Class<?> objectType) {
        for (Class<?> type : types) {
            if (type == objectType) {
                return context;
            }
        }
        return null;
    }
}