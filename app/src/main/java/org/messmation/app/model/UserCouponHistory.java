package org.messmation.app.model;

import java.util.ArrayList;
import java.util.List;

public class UserCouponHistory {

    private List<CouponCreditDetail> couponCreditDetailList = new ArrayList<CouponCreditDetail>();

    public UserCouponHistory(){

    }

    public List<CouponCreditDetail> getCouponCreditDetailList() {
        return couponCreditDetailList;
    }

}
