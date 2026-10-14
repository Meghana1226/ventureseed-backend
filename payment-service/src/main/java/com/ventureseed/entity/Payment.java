package com.ventureseed.entity;
import jakarta.persistence.*;
@Entity
public class Payment {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long contributionId; private Double amount; private String method; private String status;
    public Payment(){}
    public Long getId(){return id;} public Long getContributionId(){return contributionId;} public void setContributionId(Long x){contributionId=x;}
    public Double getAmount(){return amount;} public void setAmount(Double x){amount=x;}
    public String getMethod(){return method;} public void setMethod(String x){method=x;}
    public String getStatus(){return status;} public void setStatus(String x){status=x;}
}
