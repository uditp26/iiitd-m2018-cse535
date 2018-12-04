package org.messmation.app.model;

import org.messmation.app.model.UserCouponHistory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class User {



    private String uuid;
    private String fullName;
    private String emailId;
    private String rollNo;
    //private String phone;
    private String city; //TODO change it to dropdown
    private boolean isActive;
    private String createdDate;
    private String updatedDate;

    private UserCouponHistory userCouponHistory;


    public User(){

    }

    public User(String fullName, String emailID, String rollNo, String city) {
        this.fullName = fullName;
        this.emailId = emailID;
        this.rollNo = rollNo;
        //this.phone = phone;
        this.city = city;
        this.isActive = true;
        this.userCouponHistory = new UserCouponHistory();
        SimpleDateFormat sdfWithTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
        this.createdDate = sdfWithTime.format(new Date());
        this.updatedDate = sdfWithTime.format(new Date());
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public void setUserCouponHistory(UserCouponHistory userCouponHistory) {
        this.userCouponHistory = userCouponHistory;
    }

    public UserCouponHistory getUserCouponHistory(String uuid) {
        if(this.uuid.equals(uuid))
            return userCouponHistory;
        else
            return null;
    }


    /*public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }*/
}
