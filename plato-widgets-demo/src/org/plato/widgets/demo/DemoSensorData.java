package org.plato.widgets.demo;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "sensorData")
public class DemoSensorData {
	
	Long id;
	Double temperature;
	Double pressure;
	Double humidity;
	
	public Double getTemperature() {
		return temperature;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public Double getPressure() {
		return pressure;
	}

	public void setPressure(Double pressure) {
		this.pressure = pressure;
	}

	public Double getHumidity() {
		return humidity;
	}

	public void setHumidity(Double humidity) {
		this.humidity = humidity;
	}
	
	public DemoSensorData() {
		temperature = Math.random()*100;
		pressure = Math.random();
		humidity = Math.random();
	}

}
