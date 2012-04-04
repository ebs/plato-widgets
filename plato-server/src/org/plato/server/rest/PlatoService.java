package org.plato.server.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.TabularDataSupport;

import org.plato.server.model.Driver;

public class PlatoService {

	private static final Logger logger = Logger.getLogger(PlatoService.class.getName());

	private static ObjectName platoMBeanName;

	static {
		try {
			platoMBeanName = new ObjectName("service.server.platon:service=SES");
		} catch (Exception e) {
			logger.log(Level.SEVERE, "", e);
		}
	}

	public List<Driver> getMBeanDrivers() {
		MBeanServer server = java.lang.management.ManagementFactory.getPlatformMBeanServer();

		List<Driver> drivers = new ArrayList<Driver>();
		try {
			TabularDataSupport data = (TabularDataSupport) server.getAttribute(platoMBeanName, "DriversInfo");
			for (Object value : data.values()) {
				CompositeDataSupport compositeData = (CompositeDataSupport) value;
				String id = (String) compositeData.get("key");
				String info = (String) compositeData.get("value");
				Driver driver = new Driver();
				driver.setUniqueID(id);
				driver.setInfo(info);
				drivers.add(driver);
			}
			return drivers;
		} catch (AttributeNotFoundException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "", e);
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "", e);
		} catch (MBeanException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "", e);
		} catch (ReflectionException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "", e);
		}
		return null;
	}

	public Driver getMBeanDriver(String uniqueId) {
		MBeanServer server = java.lang.management.ManagementFactory.getPlatformMBeanServer();

		try {
			Object[] input = {uniqueId};
			String[] signatures = {uniqueId.getClass().getName()};
			String info = (String) server.invoke(platoMBeanName, "getDriverInfo", input, signatures);
			Driver driver = new Driver();
			driver.setUniqueID(uniqueId);
			driver.setInfo(info);
			return driver;
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "", e);
		} catch (MBeanException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "", e);
		} catch (ReflectionException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "", e);
		}
		return null;
	}

	public String addMBeanDriver(String sensorML) {
		MBeanServer server = java.lang.management.ManagementFactory.getPlatformMBeanServer();

		try {
			Object[] input = {sensorML};
			String[] signatures = {sensorML.getClass().getName()};
			String uniqueId = (String) server.invoke(platoMBeanName, "addNewDriver", input, signatures);
			return uniqueId;
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "", e);
		} catch (MBeanException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "", e);
		} catch (ReflectionException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "", e);
		}
		return null;
	}

	public void removeMBeanDriver(String uniqueId) {
		MBeanServer server = java.lang.management.ManagementFactory.getPlatformMBeanServer();
		try {
			Object[] input = {uniqueId};
			String[] signatures = {uniqueId.getClass().getName()};
			server.invoke(platoMBeanName, "removeDriver", input, signatures);
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "", e);
		} catch (MBeanException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "", e);
		} catch (ReflectionException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "", e);
		}
	}

	public void stopSampling(String uniqueId) {
		MBeanServer server = java.lang.management.ManagementFactory.getPlatformMBeanServer();
		try {
			Object[] input = {uniqueId};
			String[] signatures = {uniqueId.getClass().getName()};
			server.invoke(platoMBeanName, "stopSampling", input, signatures);
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "", e);
		} catch (MBeanException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "", e);
		} catch (ReflectionException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "", e);
		}
	}

	public void startSampling(String uniqueId) {
		MBeanServer server = java.lang.management.ManagementFactory.getPlatformMBeanServer();
		try {
			Object[] input = {uniqueId};
			String[] signatures = {uniqueId.getClass().getName()};
			server.invoke(platoMBeanName, "startSampling", input, signatures);
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "", e);
		} catch (MBeanException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "", e);
		} catch (ReflectionException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "", e);
		}
	}

	public String getMeasurement(String uniqueId) {
		MBeanServer server = java.lang.management.ManagementFactory.getPlatformMBeanServer();
		try {
			Object[] input = {uniqueId};
			String[] signatures = {uniqueId.getClass().getName()};
			String result = (String) server.invoke(platoMBeanName, "getLastReading", input, signatures);
			return result;
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "", e);
		} catch (MBeanException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "", e);
		} catch (ReflectionException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "", e);
		}

		return null;
	}
}
