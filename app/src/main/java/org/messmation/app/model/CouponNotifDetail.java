package org.messmation.app.model;

public class CouponNotifDetail {

    private String mealName;
    private String mealDate;
    private String notifGeneratedTime;
    private String notifAction;
    private String notifActionTime;

    public CouponNotifDetail(String mealName, String mealDate, String notifGeneratedTime, String notifAction) {
        this.mealName = mealName;
        this.mealDate = mealDate;
        this.notifGeneratedTime = notifGeneratedTime;
        this.notifAction = notifAction;
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

    public String getNotifGeneratedTime() {
        return notifGeneratedTime;
    }

    public void setNotifGeneratedTime(String notifGeneratedTime) {
        this.notifGeneratedTime = notifGeneratedTime;
    }

    public String getNotifAction() {
        return notifAction;
    }

    public void setNotifAction(String notifAction) {
        this.notifAction = notifAction;
    }

    public String getNotifActionTime() {
        return notifActionTime;
    }

    public void setNotifActionTime(String notifActionTime) {
        this.notifActionTime = notifActionTime;
    }

}
