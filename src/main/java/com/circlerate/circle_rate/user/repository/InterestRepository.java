package com.circlerate.circle_rate.user.repository;

import com.circlerate.circle_rate.user.model.Interest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterestRepository extends MongoRepository<Interest, String> {

    List<Interest> findByUserId(String userId);
    boolean existsByUserIdAndPropertyId(String userId, String propertyId);
    void deleteByUserIdAndPropertyId(String userId, String propertyId);
}
