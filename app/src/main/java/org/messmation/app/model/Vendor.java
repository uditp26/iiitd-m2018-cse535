package org.messmation.app.model;

public class Vendor {
    private String emailId;
    public Vendor()
    {

    }
    public Vendor(String emailId)
    {
        this.emailId=emailId;
    }
    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
}
