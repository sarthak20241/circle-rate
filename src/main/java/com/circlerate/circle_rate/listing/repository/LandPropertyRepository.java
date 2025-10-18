package com.circlerate.circle_rate.listing.repository;

import com.circlerate.circle_rate.listing.model.property.LandProperty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LandPropertyRepository extends MongoRepository<LandProperty, String> {
    List<LandProperty> findByOwnerId(String ownerId);
}
