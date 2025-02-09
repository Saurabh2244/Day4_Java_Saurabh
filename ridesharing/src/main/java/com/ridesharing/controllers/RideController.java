package com.ridesharing.controllers;

import com.ridesharing.models.*;
import com.ridesharing.repositories.DriverRepository;
import com.ridesharing.repositories.PassengerRepository;
import com.ridesharing.repositories.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rides")
public class RideController {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    // 1️⃣ Request a new ride
    @PostMapping
    public ResponseEntity<Ride> createRide(@RequestParam Long passengerId,
                                           @RequestParam String startLocation,
                                           @RequestParam String endLocation) {
        Optional<Passenger> passenger = passengerRepository.findById(passengerId);
        Optional<Driver> availableDriver = driverRepository.findAll()
                .stream()
                .filter(Driver::isAvailable)
                .findFirst();

        if (passenger.isPresent() && availableDriver.isPresent()) {
            Driver driver = availableDriver.get();
            driver.setAvailable(false); // Mark driver as busy
            driverRepository.save(driver);

            Ride ride = new Ride();
            ride.setPassenger(passenger.get());
            ride.setDriver(driver);
            ride.setStartLocation(startLocation);
            ride.setEndLocation(endLocation);
            ride.setStatus(RideStatus.REQUESTED);
            ride.setFare(calculateFare(startLocation, endLocation));

            Ride savedRide = rideRepository.save(ride);
            return ResponseEntity.ok(savedRide);
        }

        return ResponseEntity.badRequest().build();
    }

    // 2️⃣ Get all rides
    @GetMapping
    public ResponseEntity<List<Ride>> getAllRides() {
        return ResponseEntity.ok(rideRepository.findAll());
    }

    // 3️⃣ Get a specific ride by ID
    @GetMapping("/{id}")
    public ResponseEntity<Ride> getRideById(@PathVariable Long id) {
        return rideRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 4️⃣ Update Ride Status
    @PatchMapping("/{id}/status")
    public ResponseEntity<Ride> updateRideStatus(@PathVariable Long id, @RequestParam RideStatus status) {
        return rideRepository.findById(id).map(ride -> {
            ride.setStatus(status);

            // Mark driver available if ride is completed
            if (status == RideStatus.COMPLETED) {
                Driver driver = ride.getDriver();
                driver.setAvailable(true);
                driverRepository.save(driver);
            }

            Ride updatedRide = rideRepository.save(ride);
            return ResponseEntity.ok(updatedRide);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 5️⃣ Delete a ride
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRide(@PathVariable Long id) {
        if (rideRepository.existsById(id)) {
            rideRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // 📌 Fare Calculation (Basic Example)
    private double calculateFare(String startLocation, String endLocation) {
        return Math.random() * 50 + 10; // Generates a fare between 10 and 60
    }
}
