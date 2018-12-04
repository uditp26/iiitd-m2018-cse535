package org.messmation.app.model;

import java.util.Date;

public class CouponDebitDetail {

    private String mealName;
    private String mealDate;
    private String couponDebitTime;

    public CouponDebitDetail(){

    }

    public CouponDebitDetail(String mealName, String mealDate, String couponDebitTime){
        this.mealName = mealName;
        this.mealDate = mealDate;
        this.couponDebitTime =couponDebitTime;

    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getMealDate() {
        return mealDate;
    }

    public void setMealDate(String mealDate) {
        this.mealDate = mealDate;
    }

    public String getCouponDebitTime() {
        return couponDebitTime;
    }

    public void setCouponDebitTime(String couponDebitTime) {
        this.couponDebitTime = couponDebitTime;
    }



}
