package com.ventureseed.controller;

import com.ventureseed.entity.Contribution;
import com.ventureseed.repository.ContributionRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import java.util.List;

@RestController
@RequestMapping("/contributions")
public class ContributionController {
    private final ContributionRepository repository;
    private final RestClient restClient = RestClient.create();
    public ContributionController(ContributionRepository repository){this.repository=repository;}

    @PostMapping
    public Contribution create(@RequestBody Contribution c){
        c.setStatus("PENDING");
        // Update Campaign Service after recording the contribution.
        Contribution saved=repository.save(c);
        try {
            restClient.put()
                .uri("http://localhost:8081/campaigns/"+c.getCampaignId()+"/contribute?amount="+c.getAmount())
                .retrieve().toBodilessEntity();
            saved.setStatus("SUCCESS");
            return repository.save(saved);
        } catch(Exception e) {
            saved.setStatus("RECORDED_CAMPAIGN_UPDATE_FAILED");
            return repository.save(saved);
        }
    }
    @GetMapping public List<Contribution> all(){return repository.findAll();}
    @GetMapping("/{id}") public Contribution one(@PathVariable Long id){return repository.findById(id).orElseThrow();}
}
