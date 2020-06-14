package com.forgeurself.ob.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author madhusudhan.gr
 */
@Entity
@Table(name = "`CUSTOMER_HOME_LOAN_APPLN`")
public class HomeLoan extends AbstractEntity {

    @Column(name = "FULL_NAME")
    private String fullName;

    @Column(name="APPLN_NAME")
    private String applnName;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "OWN_RENT")
    private String rentOrHome;

    @Column(name = "SELL_HOME")
    private String sellOrBuy;

    @Column(name = "PRE_QUALIFIED")
    private String prequalified;

    @Column(name = "COMMENTS")
    private String comments;

    @Column(name = "MAIL_STATUS")
    private String mailStatus;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRentOrHome() {
        return rentOrHome;
    }

    public void setRentOrHome(String rentOrHome) {
        this.rentOrHome = rentOrHome;
    }

    public String getSellOrBuy() {
        return sellOrBuy;
    }

    public void setSellOrBuy(String sellOrBuy) {
        this.sellOrBuy = sellOrBuy;
    }

    public String getPrequalified() {
        return prequalified;
    }

    public void setPrequalified(String prequalified) {
        this.prequalified = prequalified;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getMailStatus() {
        return mailStatus;
    }

    public void setMailStatus(String mailStatus) {
        this.mailStatus = mailStatus;
    }

    public String getApplnName() {
        return applnName;
    }

    public void setApplnName(String applnName) {
        this.applnName = applnName;
    }

    @Override
    public String toString() {
        return "HomeLoan{" +
                "fullName='" + fullName + '\'' +
                ", applnName='" + applnName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", rentOrHome='" + rentOrHome + '\'' +
                ", sellOrBuy='" + sellOrBuy + '\'' +
                ", prequalified='" + prequalified + '\'' +
                ", comments='" + comments + '\'' +
                ", mailStatus='" + mailStatus + '\'' +
                '}';
    }
}
