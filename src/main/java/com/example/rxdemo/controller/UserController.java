package com.example.rxdemo.controller;


import com.example.rxdemo.exceptions.EmailUniquenessException;
import com.example.rxdemo.model.User;
import com.example.rxdemo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public Mono<ResponseEntity<User>> createUser(@RequestBody User user) {
        return userRepository.findByEmail(user.email()).flatMap(existingUser -> Mono.<User>error(new EmailUniquenessException("Email already exists!"))).switchIfEmpty(Mono.defer(() -> userRepository.save(user))) // Use switchIfEmpty to save only if email not found
                .map(savedUser -> {
                    log.info("New user created: {}", savedUser); // Keep logging if desired, or move to service
                    return ResponseEntity.status(HttpStatus.CREATED).body(savedUser); // Return 201 CREATED
                });
        // No .onErrorResume here - let GlobalExceptionHandler handle it
    }

    @GetMapping
    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteUserById(@PathVariable Long id) {
        return userRepository.deleteById(id);
    }

    @GetMapping("/stream")
    public Flux<User> streamAllUsers() {
        long start = System.currentTimeMillis();
        return userRepository.findAll().onBackpressureBuffer() // Buffer strategy for backpressure
                .doOnNext(user -> log.debug("Processed User: {} in {} ms ", user.name(), System.currentTimeMillis() - start)).doOnError(error -> log.error("Error streaming users", error)).doOnComplete(() -> log.info("Finished streaming users for streamUsers in {} ms", System.currentTimeMillis() - start));
    }

}
