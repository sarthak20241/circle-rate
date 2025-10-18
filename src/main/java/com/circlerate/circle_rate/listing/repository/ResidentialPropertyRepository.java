package com.circlerate.circle_rate.listing.repository;

import com.circlerate.circle_rate.listing.model.property.ResidentialProperty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResidentialPropertyRepository extends MongoRepository<ResidentialProperty, String> {
    List<ResidentialProperty> findByOwnerId(String ownerId);
}
