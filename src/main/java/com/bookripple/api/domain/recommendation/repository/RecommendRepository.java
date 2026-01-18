package com.bookripple.api.domain.recommendation.repository;

import com.bookripple.api.domain.recommendation.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendRepository extends JpaRepository<Recommendation, Long> {

}
