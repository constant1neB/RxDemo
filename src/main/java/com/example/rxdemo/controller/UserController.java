package com.example.rxdemo.controller;


import com.example.rxdemo.exceptions.EmailUniquenessException;
import com.example.rxdemo.model.User;
import com.example.rxdemo.repository.UserRepository;
import com.example.rxdemo.utils.GlobalLogger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public Mono<ResponseEntity<User>> createUser(@RequestBody User user) {
        return userRepository.findByEmail(user.email())
                .flatMap(existingUser -> Mono.error(new EmailUniquenessException("Email already exists!")))
                .then(userRepository.save(user)) // Save the new user if the email doesn't exist
                .map(ResponseEntity::ok) // Map the saved user to a ResponseEntity
                .doOnNext(savedUser -> GlobalLogger.info("New user created: " + savedUser)) // Logging
                .onErrorResume(e -> { // Handling errors, such as email uniqueness violation
                    GlobalLogger.error("An exception has occurred: " + e.getMessage(), e);
                    if (e instanceof EmailUniquenessException) {
                        return Mono.just(ResponseEntity
                                .status(HttpStatus.CONFLICT).build());
                    } else {
                        return Mono.just(ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .build());
                    }
                });
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
        return userRepository.findAll()
                .onBackpressureBuffer() // Buffer strategy for backpressure
                .doOnNext(user -> GlobalLogger.debug("Processed User: {} in {} ms ", user.name(), System.currentTimeMillis() - start))
                .doOnError(error -> GlobalLogger.error("Error streaming users", error))
                .doOnComplete(() -> GlobalLogger.info("Finished streaming users for streamUsers in {} ms", System.currentTimeMillis() - start));
    }

}
