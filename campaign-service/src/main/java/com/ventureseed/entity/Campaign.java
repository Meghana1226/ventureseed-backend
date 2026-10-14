package com.ventureseed.entity;

import jakarta.persistence.*;

@Entity
public class Campaign {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private Double targetAmount;
    private Double currentAmount = 0.0;
    private String status;

    public Campaign() {}
    public Long getId(){ return id; }
    public void setId(Long id){ this.id=id; }
    public String getTitle(){ return title; }
    public void setTitle(String title){ this.title=title; }
    public String getDescription(){ return description; }
    public void setDescription(String description){ this.description=description; }
    public Double getTargetAmount(){ return targetAmount; }
    public void setTargetAmount(Double targetAmount){ this.targetAmount=targetAmount; }
    public Double getCurrentAmount(){ return currentAmount; }
    public void setCurrentAmount(Double currentAmount){ this.currentAmount=currentAmount; }
    public String getStatus(){ return status; }
    public void setStatus(String status){ this.status=status; }
}
