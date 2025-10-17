package com.circlerate.circle_rate.listing.repository;

import com.circlerate.circle_rate.listing.model.property.CommercialProperty;
import com.circlerate.circle_rate.listing.model.property.LandProperty;
import com.circlerate.circle_rate.listing.model.property.Property;
import com.circlerate.circle_rate.listing.model.property.ResidentialProperty;
import com.circlerate.circle_rate.listing.payload.PrimaryFilterRequest;
import com.circlerate.circle_rate.listing.payload.SecondaryFilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PropertyQueryRepository {
    private final MongoTemplate mongoTemplate;

    public PropertyQueryRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Page<ResidentialProperty> findResidentialPropertiesByFilters(
            PrimaryFilterRequest primaryFilter,
            SecondaryFilterRequest secondaryFilter,
            Pageable pageable) {
        
        Query query = new Query();
        addPrimaryFiltersToQuery(primaryFilter, query);
        addSecondaryFiltersToQuery(secondaryFilter, query);
        addResidentialSpecificFilters(primaryFilter, secondaryFilter, query);
        
        long total = mongoTemplate.count(query, ResidentialProperty.class);
        query.with(pageable);
        
        List<ResidentialProperty> properties = mongoTemplate.find(query, ResidentialProperty.class);
        return new PageImpl<>(properties, pageable, total);
    }
    
    public Page<CommercialProperty> findCommercialPropertiesByFilters(
            PrimaryFilterRequest primaryFilter, 
            SecondaryFilterRequest secondaryFilter, 
            Pageable pageable) {
        
        Query query = new Query();
        addPrimaryFiltersToQuery(primaryFilter, query);
        addSecondaryFiltersToQuery(secondaryFilter, query);
        addCommercialSpecificFilters(primaryFilter, secondaryFilter, query);
        
        long total = mongoTemplate.count(query, CommercialProperty.class);
        query.with(pageable);
        
        List<CommercialProperty> properties = mongoTemplate.find(query, CommercialProperty.class);
        return new PageImpl<>(properties, pageable, total);
    }
    
    public Page<LandProperty> findLandPropertiesByFilters(
            PrimaryFilterRequest primaryFilter, 
            SecondaryFilterRequest secondaryFilter, 
            Pageable pageable) {
        
        Query query = new Query();
        addPrimaryFiltersToQuery(primaryFilter, query);
        addSecondaryFiltersToQuery(secondaryFilter, query);
        addLandSpecificFilters(primaryFilter, secondaryFilter, query);
        
        long total = mongoTemplate.count(query, LandProperty.class);
        query.with(pageable);
        
        List<LandProperty> properties = mongoTemplate.find(query, LandProperty.class);
        return new PageImpl<>(properties, pageable, total);
    }
    
    private void addPrimaryFiltersToQuery(PrimaryFilterRequest primaryFilterRequest, Query query) {
        if (primaryFilterRequest.getListingType() != null) {
            query.addCriteria(Criteria.where("listingType").is(primaryFilterRequest.getListingType()));
        }

        // Location filters (hierarchical)
        if (primaryFilterRequest.getSubLocalityName() != null) {
            query.addCriteria(Criteria.where("subLocalityName").is(primaryFilterRequest.getSubLocalityName()));
        } else if (primaryFilterRequest.getLocalityName() != null) {
            query.addCriteria(Criteria.where("localityName").is(primaryFilterRequest.getLocalityName()));
        } else if (primaryFilterRequest.getCityName() != null) {
            query.addCriteria(Criteria.where("cityName").is(primaryFilterRequest.getCityName()));
        } else if (primaryFilterRequest.getStateName() != null) {
            query.addCriteria(Criteria.where("stateName").is(primaryFilterRequest.getStateName()));
        }

        // Price range filters
        if (primaryFilterRequest.getMinPrice() != null && primaryFilterRequest.getMaxPrice() != null) {
            query.addCriteria(Criteria.where("expectedPriceInRupees").gte(primaryFilterRequest.getMinPrice()).lte(primaryFilterRequest.getMaxPrice()));
        } else if (primaryFilterRequest.getMinPrice() != null) {
            query.addCriteria(Criteria.where("expectedPriceInRupees").gte(primaryFilterRequest.getMinPrice()));
        } else if (primaryFilterRequest.getMaxPrice() != null) {
            query.addCriteria(Criteria.where("expectedPriceInRupees").lte(primaryFilterRequest.getMaxPrice()));
        }

        // Area range filters
        if (primaryFilterRequest.getMinSqFtArea() != null && primaryFilterRequest.getMaxSqFtArea() != null) {
            query.addCriteria(Criteria.where("areaInSqFt").gte(primaryFilterRequest.getMinSqFtArea()).lte(primaryFilterRequest.getMaxSqFtArea()));
        } else if (primaryFilterRequest.getMinSqFtArea() != null) {
            query.addCriteria(Criteria.where("areaInSqFt").gte(primaryFilterRequest.getMinSqFtArea()));
        } else if (primaryFilterRequest.getMaxSqFtArea() != null) {
            query.addCriteria(Criteria.where("areaInSqFt").lte(primaryFilterRequest.getMaxSqFtArea()));
        }
    }
    
    private void addSecondaryFiltersToQuery(SecondaryFilterRequest secondaryFilterRequest, Query query) {
        // Owner type filter
        if (secondaryFilterRequest.getOwnerType() != null) {
            query.addCriteria(Criteria.where("ownerType").is(secondaryFilterRequest.getOwnerType()));
        }
    }
    
    private void addResidentialSpecificFilters(PrimaryFilterRequest primaryFilterRequest, SecondaryFilterRequest secondaryFilterRequest, Query query) {
        if (primaryFilterRequest.getNoOfRooms() != null && !primaryFilterRequest.getNoOfRooms().isEmpty()) {
            query.addCriteria(Criteria.where("noOfRooms").in(primaryFilterRequest.getNoOfRooms()));
        }

        if (secondaryFilterRequest.getNoOfWashrooms() != null) {
            query.addCriteria(Criteria.where("noOfWashrooms").is(secondaryFilterRequest.getNoOfWashrooms()));
        }

        if (secondaryFilterRequest.getNoOfBalconies() != null) {
            query.addCriteria(Criteria.where("noOfBalconies").is(secondaryFilterRequest.getNoOfBalconies()));
        }

        if (secondaryFilterRequest.getMinPropertyFloor() != null && secondaryFilterRequest.getMaxPropertyFloor() != null) {
            query.addCriteria(Criteria.where("propertyFloor")
                    .gte(secondaryFilterRequest.getMinPropertyFloor())
                    .lte(secondaryFilterRequest.getMaxPropertyFloor()));
        } else if (secondaryFilterRequest.getMinPropertyFloor() != null) {
            query.addCriteria(Criteria.where("propertyFloor").gte(secondaryFilterRequest.getMinPropertyFloor()));
        } else if (secondaryFilterRequest.getMaxPropertyFloor() != null) {
            query.addCriteria(Criteria.where("propertyFloor").lte(secondaryFilterRequest.getMaxPropertyFloor()));
        }

        if (secondaryFilterRequest.getMinTotalFloors() != null && secondaryFilterRequest.getMaxTotalFloors() != null) {
            query.addCriteria(Criteria.where("totalFloors")
                    .gte(secondaryFilterRequest.getMinTotalFloors())
                    .lte(secondaryFilterRequest.getMaxTotalFloors()));
        } else if (secondaryFilterRequest.getMinTotalFloors() != null) {
            query.addCriteria(Criteria.where("totalFloors").gte(secondaryFilterRequest.getMinTotalFloors()));
        } else if (secondaryFilterRequest.getMaxTotalFloors() != null) {
            query.addCriteria(Criteria.where("totalFloors").lte(secondaryFilterRequest.getMaxTotalFloors()));
        }

        if (secondaryFilterRequest.getMinAgeOfProperty() != null && secondaryFilterRequest.getMaxAgeOfProperty() != null) {
            query.addCriteria(Criteria.where("ageOfProperty")
                    .gte(secondaryFilterRequest.getMinAgeOfProperty())
                    .lte(secondaryFilterRequest.getMaxAgeOfProperty()));
        } else if (secondaryFilterRequest.getMinAgeOfProperty() != null) {
            query.addCriteria(Criteria.where("ageOfProperty").gte(secondaryFilterRequest.getMinAgeOfProperty()));
        } else if (secondaryFilterRequest.getMaxAgeOfProperty() != null) {
            query.addCriteria(Criteria.where("ageOfProperty").lte(secondaryFilterRequest.getMaxAgeOfProperty()));
        }
    }
    
    private void addCommercialSpecificFilters(PrimaryFilterRequest primaryFilterRequest, SecondaryFilterRequest secondaryFilterRequest, Query query) {
    }
    
    private void addLandSpecificFilters(PrimaryFilterRequest primaryFilterRequest, SecondaryFilterRequest secondaryFilterRequest, Query query) {
    }
    
    public boolean propertyExistsByIdAndType(String propertyId, String propertyType) {
        switch (propertyType.toUpperCase()) {
            case "RESIDENTIAL" -> {
                return mongoTemplate.exists(Query.query(Criteria.where("id").is(propertyId)), ResidentialProperty.class);
            }
            case "COMMERCIAL" -> {
                return mongoTemplate.exists(Query.query(Criteria.where("id").is(propertyId)), CommercialProperty.class);
            }
            case "LAND" -> {
                return mongoTemplate.exists(Query.query(Criteria.where("id").is(propertyId)), LandProperty.class);
            }
            default -> {
                return false;
            }
        }
    }
    
    public Property findPropertyByIdAndType(String propertyId, String propertyType) {
        switch (propertyType.toUpperCase()) {
            case "RESIDENTIAL" -> {
                return mongoTemplate.findById(propertyId, ResidentialProperty.class);
            }
            case "COMMERCIAL" -> {
                return mongoTemplate.findById(propertyId, CommercialProperty.class);
            }
            case "LAND" -> {
                return mongoTemplate.findById(propertyId, LandProperty.class);
            }
            default -> {
                return null;
            }
        }
    }
}
