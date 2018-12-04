package org.messmation.app.model;

import java.util.List;

public class CouponNotifHistory {

    private List<CouponNotifDetail> couponNotifList;

    public CouponNotifHistory(){

    }

    public List<CouponNotifDetail> getCouponNotifList() {
        return couponNotifList;
    }

    public void setCouponNotifList(List<CouponNotifDetail> couponNotifList) {
        this.couponNotifList = couponNotifList;
    }

}
