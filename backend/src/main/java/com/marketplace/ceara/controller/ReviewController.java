package com.marketplace.ceara.controller;

import com.marketplace.ceara.model.Review;
import com.marketplace.ceara.model.User;
import com.marketplace.ceara.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller para gestão de avaliações (US08).
 */
@RestController
@RequestMapping("/api/v1/services")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * US08 — Avaliar um serviço concluído.
     */
    @PostMapping("/{id}/reviews")
    public ResponseEntity<Review> createReview(
            @PathVariable("id") UUID serviceRequestId,
            @RequestBody ReviewRequest request,
            @AuthenticationPrincipal User client
    ) {
        Review review = reviewService.createReview(
                serviceRequestId,
                client.getId(),
                request.rating(),
                request.comment()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    public record ReviewRequest(Integer rating, String comment) {}
}
