package com.ventureseed.controller;
import com.ventureseed.entity.Payment;
import com.ventureseed.repository.PaymentRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentRepository repository;
    public PaymentController(PaymentRepository repository){this.repository=repository;}
    @PostMapping public Payment pay(@RequestBody Payment p){
        p.setStatus("SUCCESS");
        return repository.save(p);
    }
    @GetMapping public List<Payment> all(){return repository.findAll();}
    @GetMapping("/{id}") public Payment one(@PathVariable Long id){return repository.findById(id).orElseThrow();}
}
