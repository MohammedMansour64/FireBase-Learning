package com.mohammedev.firebaselearning.data;

public class MyData {
    private String name , age , job , imageUrl , email;


    public MyData(String name, String age, String job , String imageUrl , String email) {
        this.name = name;
        this.age = age;
        this.job = job;
        this.imageUrl = imageUrl;
        this.email = email;
    }

    public MyData(String name, String age, String job , String imageUrl) {
        this.name = name;
        this.age = age;
        this.job = job;
        this.imageUrl = imageUrl;
    }

    public MyData() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}
