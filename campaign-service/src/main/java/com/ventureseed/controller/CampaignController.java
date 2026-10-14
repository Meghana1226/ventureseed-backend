package com.ventureseed.controller;

import com.ventureseed.entity.Campaign;
import com.ventureseed.repository.CampaignRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/campaigns")
public class CampaignController {
    private final CampaignRepository repository;
    public CampaignController(CampaignRepository repository){ this.repository=repository; }

    @PostMapping
    public Campaign create(@RequestBody Campaign campaign){
        if(campaign.getCurrentAmount()==null) campaign.setCurrentAmount(0.0);
        if(campaign.getStatus()==null) campaign.setStatus("ACTIVE");
        return repository.save(campaign);
    }
    @GetMapping public List<Campaign> all(){ return repository.findAll(); }
    @GetMapping("/{id}") public Campaign one(@PathVariable Long id){
        return repository.findById(id).orElseThrow();
    }
    @PutMapping("/{id}") public Campaign update(@PathVariable Long id,@RequestBody Campaign c){
        Campaign x=repository.findById(id).orElseThrow();
        x.setTitle(c.getTitle()); x.setDescription(c.getDescription());
        x.setTargetAmount(c.getTargetAmount()); x.setCurrentAmount(c.getCurrentAmount());
        x.setStatus(c.getStatus()); return repository.save(x);
    }
    @DeleteMapping("/{id}") public String delete(@PathVariable Long id){
        repository.deleteById(id); return "Campaign deleted successfully";
    }
    @PutMapping("/{id}/contribute")
    public Campaign contribute(@PathVariable Long id,@RequestParam Double amount){
        Campaign x=repository.findById(id).orElseThrow();
        x.setCurrentAmount((x.getCurrentAmount()==null?0:x.getCurrentAmount())+amount);
        return repository.save(x);
    }
}
