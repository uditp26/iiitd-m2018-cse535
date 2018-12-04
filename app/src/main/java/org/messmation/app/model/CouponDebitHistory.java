package org.messmation.app.model;

import java.util.ArrayList;
import java.util.List;

public class CouponDebitHistory {

    public void setCouponDebitList(List<CouponDebitDetail> couponDebitList) {
        this.couponDebitList = couponDebitList;
    }

    private List<CouponDebitDetail> couponDebitList;

    public CouponDebitHistory(){
    }

    public List<CouponDebitDetail> getCouponDebitList() {
        return couponDebitList;
    }
}
