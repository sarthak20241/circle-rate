package com.circlerate.circle_rate.listing.repository;

import com.circlerate.circle_rate.listing.model.property.Property;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRepository extends MongoRepository<Property,String>, PropertyRepositoryCustom {
}
