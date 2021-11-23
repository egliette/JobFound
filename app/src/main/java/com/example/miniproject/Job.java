package com.example.miniproject;

public class Job {
    String title, province, requirements, description, type;
    Long phone, minSalary, maxSalary;
    Double lat, lng;

    public Job() {}

    public Job(String title, String province, String requirements,
               String description, String type, Long phone, Long minSalary,
               Long maxSalary, Double lat, Double lng) {
        this.title = title;
        this.type = type;
        this.province = province;
        this.requirements = requirements;
        this.description = description;
        this.phone = phone;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.lat = lat;
        this.lng = lng;
    }

    public String getTitle() {
        return title;
    }

    public String getProvince() {
        return province;
    }

    public String getRequirements() {
        return requirements;
    }

    public String getDescription() {
        return description;
    }

    public Long getPhone() {
        return phone;
    }

    public Long getMinSalary() {
        return minSalary;
    }

    public Long getMaxSalary() {
        return maxSalary;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }
}
