package com.example.miniproject;

public class User {

    private int image;
    private String title;
    private String province;
    private String requirements;
    private String description;
    private String type;
    private Long phone;
    private Long minSalary;
    private Long maxSalary;

//    public User(int image, String title, String province) {
//        this.image = image;
//        this.title = title;
//        this.province = province;
//    }

    public User(int image, String title, String province, String requirements, String description, String type, Long phone, Long minSalary, Long maxSalary) {
        this.image = image;
        this.title = title;
        this.province = province;
        this.requirements = requirements;
        this.description = description;
        this.type = type;
        this.phone = phone;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getPhone() {
        return phone;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }

    public Long getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(Long minSalary) {
        this.minSalary = minSalary;
    }

    public Long getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(Long maxSalary) {
        this.maxSalary = maxSalary;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
