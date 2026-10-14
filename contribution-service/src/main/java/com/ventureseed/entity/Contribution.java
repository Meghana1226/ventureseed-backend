package com.ventureseed.entity;
import jakarta.persistence.*;
@Entity
public class Contribution {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long campaignId; private String userEmail; private Double amount; private String status;
    public Contribution(){}
    public Long getId(){return id;} public Long getCampaignId(){return campaignId;} public void setCampaignId(Long x){campaignId=x;}
    public String getUserEmail(){return userEmail;} public void setUserEmail(String x){userEmail=x;}
    public Double getAmount(){return amount;} public void setAmount(Double x){amount=x;}
    public String getStatus(){return status;} public void setStatus(String x){status=x;}
}
