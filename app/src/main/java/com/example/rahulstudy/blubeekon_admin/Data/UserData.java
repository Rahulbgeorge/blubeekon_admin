package com.example.rahulstudy.blubeekon_admin.Data;

/**
 * Created by Rahul study on 10-05-2018.
 */

public class UserData {
   private String email;
   private String password;

    public Boolean getProjectState() {
        return projectState;
    }

    public void setProjectState(Boolean projectState) {
        this.projectState = projectState;
    }

    private Boolean projectState;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProjectkey() {
        return projectkey;
    }

    public void setProjectkey(String projectkey) {
        this.projectkey = projectkey;
    }

    public String projectkey;

   public UserData(String email, String password)
   {
       projectState=false;
       this.email=email;
       this.password=password;
   }

   public UserData()
   {}
}
