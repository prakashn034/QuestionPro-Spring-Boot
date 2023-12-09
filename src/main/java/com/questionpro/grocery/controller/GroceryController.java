package com.questionpro.grocery.controller;

import com.questionpro.grocery.entity.BuyGrocery;
import com.questionpro.grocery.entity.Grocery;
import com.questionpro.grocery.entity.GroceryOrder;
import com.questionpro.grocery.repository.GroceryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class GroceryController {

    @Autowired
    GroceryRepository groceryRepository;

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(summary = "Get all Grocery items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grocery Items fetched",
                    content = {@Content(mediaType = "application/json",schema = @Schema(implementation = Grocery.class))}),
            @ApiResponse(responseCode = "401",description = "Unauthorized user",content = @Content),
            @ApiResponse(responseCode = "403",description = "Forbidden Access",content = @Content),
            @ApiResponse(responseCode = "404",description = "Grocery not found",content = @Content),
            @ApiResponse(responseCode = "400",description = "Bad Request",content = @Content)})
    @GetMapping("/grocery")
    public ResponseEntity<List<Grocery>> getAllGrocery() {
        try {

            List<Grocery> grocery = groceryRepository.findAll();

            if (grocery.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(grocery, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(summary = "Get Grocery by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grocery Item fetched",
                    content = {@Content(mediaType = "application/json",schema = @Schema(implementation = Grocery.class))}),
            @ApiResponse(responseCode = "401",description = "Unauthorized user",content = @Content),
            @ApiResponse(responseCode = "403",description = "Forbidden Access",content = @Content),
            @ApiResponse(responseCode = "404",description = "Grocery not found",content = @Content),
            @ApiResponse(responseCode = "400",description = "Bad Request",content = @Content)})
    @GetMapping("/grocery/{id}")
    public ResponseEntity<Grocery> getGroceryById(@PathVariable("id") long id) {
        Optional<Grocery> groceryData = groceryRepository.findById(id);

        if (groceryData.isPresent()) {
            return new ResponseEntity<>(groceryData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Post Grocery Item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Grocery Item added",
                    content = {@Content(mediaType = "application/json",schema = @Schema(implementation = Grocery.class))}),
            @ApiResponse(responseCode = "401",description = "Unauthorized user",content = @Content),
            @ApiResponse(responseCode = "403",description = "Forbidden Access",content = @Content),
            @ApiResponse(responseCode = "400",description = "Bad Request",content = @Content)})
    @PostMapping("/grocery")
    public ResponseEntity<Grocery> createGrocery(@RequestBody Grocery grocery) {
        try {
            Grocery _grocery = groceryRepository
                    .save(new Grocery(grocery.getName(), grocery.getPrice()));
            return new ResponseEntity<>(_grocery, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Place Grocery order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Placed grocery order",
                    content = {@Content(mediaType = "application/json",schema = @Schema(implementation = Grocery.class))}),
            @ApiResponse(responseCode = "401",description = "Unauthorized user",content = @Content),
            @ApiResponse(responseCode = "403",description = "Forbidden Access",content = @Content),
            @ApiResponse(responseCode = "400",description = "Bad Request",content = @Content)})
    @PostMapping("/grocery/order")
    public ResponseEntity<String> orderGrocery(@RequestBody GroceryOrder groceryOrder) {
        try {
            var orders = groceryOrder.getOrder();
            for(var order: orders) {
                Optional<Grocery> groceryData = Optional
                        .ofNullable(groceryRepository.findByName(order.getName()));

                if (!groceryData.isPresent()) {
                    return new ResponseEntity<>(order.getName() + " is not available",
                            HttpStatus.NOT_FOUND);
                }
            }
            List<String> orderNames= groceryOrder.getOrder().stream().map(BuyGrocery::getName)
                    .toList();
            return new ResponseEntity<>("Order has been successfully placed for " + orderNames,
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Grocery item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Updated Grocery Item",
                    content = {@Content(mediaType = "application/json",schema = @Schema(implementation = Grocery.class))}),
            @ApiResponse(responseCode = "401",description = "Unauthorized user",content = @Content),
            @ApiResponse(responseCode = "403",description = "Forbidden Access",content = @Content),
            @ApiResponse(responseCode = "400",description = "Bad Request",content = @Content)})
    @PutMapping("/grocery/{id}")
    public ResponseEntity<Grocery> updateGrocery(@PathVariable("id") long id, @RequestBody Grocery grocery) {
        Optional<Grocery> groceryData = groceryRepository.findById(id);

        if (groceryData.isPresent()) {
            Grocery _grocery = groceryData.get();
            _grocery.setName(grocery.getName());
            _grocery.setPrice(grocery.getPrice());
            return new ResponseEntity<>(groceryRepository.save(_grocery), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete Grocery item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Grocery Item deleted",
                    content = {@Content(mediaType = "application/json",schema = @Schema(implementation = Grocery.class))}),
            @ApiResponse(responseCode = "401",description = "Unauthorized user",content = @Content),
            @ApiResponse(responseCode = "403",description = "Forbidden Access",content = @Content),
            @ApiResponse(responseCode = "400",description = "Bad Request",content = @Content)})
    @DeleteMapping("/grocery/{id}")
    public ResponseEntity<HttpStatus> deleteGrocery(@PathVariable("id") long id) {
        try {
            groceryRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete all Grocery items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Grocery Items deleted",
                    content = {@Content(mediaType = "application/json",schema = @Schema(implementation = Grocery.class))}),
            @ApiResponse(responseCode = "401",description = "Unauthorized user",content = @Content),
            @ApiResponse(responseCode = "403",description = "Forbidden Access",content = @Content),
            @ApiResponse(responseCode = "400",description = "Bad Request",content = @Content)})
    @DeleteMapping("/grocery")
    public ResponseEntity<HttpStatus> deleteAllGrocery() {
        try {
            groceryRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}