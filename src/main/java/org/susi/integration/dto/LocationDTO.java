package org.susi.integration.dto;


import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LocationDTO {
    long latitude;
    long longitude;

    public LocationDTO(long latitude, long longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LocationDTO() {
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "LocationDTO{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
