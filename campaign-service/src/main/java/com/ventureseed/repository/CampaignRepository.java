package com.ventureseed.repository;
import com.ventureseed.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CampaignRepository extends JpaRepository<Campaign, Long> {}
