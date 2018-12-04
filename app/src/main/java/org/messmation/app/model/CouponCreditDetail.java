
package org.messmation.app.model;

import java.util.ArrayList;
import java.util.List;

public class CouponCreditDetail {

    private String forMonth;
    private String validaityUpto;
    private int totalCreditedCouponCount;

    private String defaultingPriority;

    private double couponSetAmount;
    private String transactionID;
    private String transactionDate;

    private CouponDebitHistory couponDebitHistory;
    private CouponRemainingSummary couponRemainingSummary;
    private CouponNotifHistory couponNotifHistory;

    public CouponCreditDetail(){

    }

    public CouponCreditDetail(String forMonth, String validaityUpto, int totalCreditedCouponCount, String transactionID, double couponSetAmount, String transactionDate){
        this.forMonth = forMonth;
        this.validaityUpto = validaityUpto;
        this.totalCreditedCouponCount = totalCreditedCouponCount;
        this.couponDebitHistory = new CouponDebitHistory();
        this.couponRemainingSummary = new CouponRemainingSummary(totalCreditedCouponCount);
        this.couponNotifHistory = new CouponNotifHistory();
        this.defaultingPriority = "-1";

        this.couponSetAmount = couponSetAmount;
        this.transactionID = transactionID;
        this.transactionDate = transactionDate;
    }

    public String getForMonth() {
        return forMonth;
    }

    public void setForMonth(String forMonth) {
        this.forMonth = forMonth;
    }

    public String getValidaityUpto() {
        return validaityUpto;
    }

    public void setValidaityUpto(String validaityUpto) {
        this.validaityUpto = validaityUpto;
    }

    public int getTotalCreditedCouponCount() {
        return totalCreditedCouponCount;
    }

    public void setTotalCreditedCouponCount(int totalCreditedCouponCount) {
        this.totalCreditedCouponCount = totalCreditedCouponCount;
    }

    public CouponDebitHistory getCouponDebitHistory() {
        return couponDebitHistory;
    }

    public void setCouponDebitHistory(CouponDebitHistory couponDebitHistory) {
        this.couponDebitHistory = couponDebitHistory;
    }

    public CouponRemainingSummary getCouponRemainingSummary() {
        return couponRemainingSummary;
    }

    public void setCouponRemainingSummary(CouponRemainingSummary couponRemainingSummary) {
        this.couponRemainingSummary = couponRemainingSummary;
    }

    public CouponNotifHistory getCouponNotifHistory() {
        return couponNotifHistory;
    }

    public void setCouponNotifHistory(CouponNotifHistory couponNotifHistory) {
        this.couponNotifHistory = couponNotifHistory;
    }

    public String getDefaultingPriority() {
        return defaultingPriority;
    }

    public void setDefaultingPriority(String defaultingPriority) {
        this.defaultingPriority = defaultingPriority;
    }

    public void setCouponSetAmount(double couponSetAmount) {
        this.couponSetAmount = couponSetAmount;
    }

    public double getCouponSetAmount() {
        return couponSetAmount;
    }


    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }
}
