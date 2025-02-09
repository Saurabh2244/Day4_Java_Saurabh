package com.ridesharing.controllers;

import com.ridesharing.models.Driver;
import com.ridesharing.repositories.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/drivers")
public class DriverController {

    @Autowired
    private DriverRepository driverRepository;

    // Register a new driver
    @PostMapping
    public ResponseEntity<Driver> registerDriver(@RequestBody Driver driver) {
        Driver savedDriver = driverRepository.save(driver);
        return ResponseEntity.ok(savedDriver);
    }

    // Get all drivers
    @GetMapping
    public ResponseEntity<List<Driver>> getAllDrivers() {
        List<Driver> drivers = driverRepository.findAll();
        return ResponseEntity.ok(drivers);
    }

    // Get a driver by ID
    @GetMapping("/{id}")
    public ResponseEntity<Driver> getDriverById(@PathVariable Long id) {
        Optional<Driver> driver = driverRepository.findById(id);
        return driver.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update a driver
    @PutMapping("/{id}")
    public ResponseEntity<Driver> updateDriver(@PathVariable Long id, @RequestBody Driver updatedDriver) {
        return driverRepository.findById(id).map(driver -> {
            driver.setName(updatedDriver.getName());
            driver.setEmail(updatedDriver.getEmail());
            driver.setPhoneNumber(updatedDriver.getPhoneNumber());
            driver.setVehicleDetails(updatedDriver.getVehicleDetails());
            driver.setAvailable(updatedDriver.isAvailable());

            Driver savedDriver = driverRepository.save(driver);
            return ResponseEntity.ok(savedDriver);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete a driver
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        if (driverRepository.existsById(id)) {
            driverRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Update Driver Availability
    @PatchMapping("/{id}/availability")
    public ResponseEntity<Driver> updateDriverAvailability(@PathVariable Long id, @RequestParam boolean available) {
        return driverRepository.findById(id).map(driver -> {
            driver.setAvailable(available);
            Driver updatedDriver = driverRepository.save(driver);
            return ResponseEntity.ok(updatedDriver);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
