package org.messmation.app.model;

public class CouponRemainingSummary {

    private int breakFastRemainingCoupons;
    private int lunchRemainingCoupons;
    private int eveningTeaRemainingCoupons;
    private int dinnerRemainingCoupons;


    public CouponRemainingSummary(int totalCreditedCouponCount){
        this.breakFastRemainingCoupons = totalCreditedCouponCount;
        this.lunchRemainingCoupons = totalCreditedCouponCount;
        this.eveningTeaRemainingCoupons = totalCreditedCouponCount;
        this.dinnerRemainingCoupons = totalCreditedCouponCount;
    }

    public CouponRemainingSummary(){

    }

    public int getBreakFastRemainingCoupons() {
        return breakFastRemainingCoupons;
    }

    public void setBreakFastRemainingCoupons(int breakFastRemainingCoupons) {
        this.breakFastRemainingCoupons = breakFastRemainingCoupons;
    }

    public int getLunchRemainingCoupons() {
        return lunchRemainingCoupons;
    }

    public void setLunchRemainingCoupons(int lunchRemainingCoupons) {
        this.lunchRemainingCoupons = lunchRemainingCoupons;
    }

    public int getEveningTeaRemainingCoupons() {
        return eveningTeaRemainingCoupons;
    }

    public void setEveningTeaRemainingCoupons(int eveningTeaRemainingCoupons) {
        this.eveningTeaRemainingCoupons = eveningTeaRemainingCoupons;
    }

    public int getDinnerRemainingCoupons() {
        return dinnerRemainingCoupons;
    }

    public void setDinnerRemainingCoupons(int dinnerRemainingCoupons) {
        this.dinnerRemainingCoupons = dinnerRemainingCoupons;
    }
}
