package com.example.smartattendance;

public class student {
    public String name,rollno,password,gender,semester,batch;

    public student(){

    }
    public student(String name, String rollno,String password, String gender, String semester,String batch) {
        this.name = name;
        this.rollno = rollno;
        this.password=password;
        this.gender = gender;
        this.semester = semester;
        this.batch=batch;
    }

    public void setRollno(String rollno) {
        this.rollno = rollno;
    }

    public String getRollno() {
        return rollno;
    }
}
