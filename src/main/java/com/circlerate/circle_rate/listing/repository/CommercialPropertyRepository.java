package com.circlerate.circle_rate.listing.repository;

import com.circlerate.circle_rate.listing.model.property.CommercialProperty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommercialPropertyRepository extends MongoRepository<CommercialProperty, String> {
    List<CommercialProperty> findByOwnerId(String ownerId);
}
