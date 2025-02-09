package com.ridesharing.controllers;

import com.ridesharing.models.Passenger;
import com.ridesharing.repositories.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/passengers")
public class PassengerController {

    @Autowired
    private PassengerRepository passengerRepository;

    // Register a new passenger
    @PostMapping
    public ResponseEntity<Passenger> registerPassenger(@RequestBody Passenger passenger) {
        Passenger savedPassenger = passengerRepository.save(passenger);
        return ResponseEntity.ok(savedPassenger);
    }

    //  Get all passengers
    @GetMapping
    public ResponseEntity<List<Passenger>> getAllPassengers() {
        List<Passenger> passengers = passengerRepository.findAll();
        return ResponseEntity.ok(passengers);
    }

    //  Get a passenger by ID
    @GetMapping("/{id}")
    public ResponseEntity<Passenger> getPassengerById(@PathVariable Long id) {
        Optional<Passenger> passenger = passengerRepository.findById(id);
        return passenger.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    //  Update a passenger
    @PutMapping("/{id}")
    public ResponseEntity<Passenger> updatePassenger(@PathVariable Long id, @RequestBody Passenger updatedPassenger) {
        return passengerRepository.findById(id).map(passenger -> {
            passenger.setName(updatedPassenger.getName());
            passenger.setEmail(updatedPassenger.getEmail());
            passenger.setPhoneNumber(updatedPassenger.getPhoneNumber());
            passenger.setPassword(updatedPassenger.getPassword());

            Passenger savedPassenger = passengerRepository.save(passenger);
            return ResponseEntity.ok(savedPassenger);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete a passenger
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassenger(@PathVariable Long id) {
        if (passengerRepository.existsById(id)) {
            passengerRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
