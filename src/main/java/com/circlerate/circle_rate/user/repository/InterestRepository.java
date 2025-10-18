package com.circlerate.circle_rate.user.repository;

import com.circlerate.circle_rate.user.model.Interest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterestRepository extends MongoRepository<Interest, String> {
    Page<Interest> findByUserId(String userId, Pageable pageable);
    boolean existsByUserIdAndPropertyId(String userId, String propertyId);
    void deleteByUserIdAndPropertyId(String userId, String propertyId);
    void deleteByUserId(String userId);
    Page<Interest> findByPropertyId(String propertyId, Pageable pageable);
    void deleteByPropertyId(String propertyId);
}
