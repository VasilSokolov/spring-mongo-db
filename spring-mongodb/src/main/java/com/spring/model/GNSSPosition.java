package com.spring.model;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GNSSPosition {
	@JsonProperty("lat")
	private Double latitude = null;

	@JsonProperty("lng")
	private Double longitude = null;

	@JsonProperty("altitude")
	private Integer altitude = null;

	@JsonProperty("createTime")
	private Instant createTime = null;

	@JsonProperty("sendTime")
	private Instant sendTime = null;

	@JsonProperty("heading")
	private Double heading = null;

	@JsonProperty("speed")
	private Integer speed = null;

	@JsonProperty("horizontalAccuracy")
	private Integer horizontalAccuracy = null;

	public GNSSPosition() {
		super();
	}

	public GNSSPosition latitude(Double latitude) {
		this.latitude = latitude;
		return this;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Integer getAltitude() {
		return altitude;
	}

	public void setAltitude(Integer altitude) {
		this.altitude = altitude;
	}

	public Instant getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Instant createTime) {
		this.createTime = createTime;
	}

	public Instant getSendTime() {
		return sendTime;
	}

	public void setSendTime(Instant sendTime) {
		this.sendTime = sendTime;
	}

	public Double getHeading() {
		return heading;
	}

	public void setHeading(Double heading) {
		this.heading = heading;
	}

	public Integer getSpeed() {
		return speed;
	}

	public void setSpeed(Integer speed) {
		this.speed = speed;
	}

	public Integer getHorizontalAccuracy() {
		return horizontalAccuracy;
	}

	public void setHorizontalAccuracy(Integer horizontalAccuracy) {
		this.horizontalAccuracy = horizontalAccuracy;
	}

}
