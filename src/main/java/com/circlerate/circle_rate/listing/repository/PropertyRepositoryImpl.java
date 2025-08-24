package com.circlerate.circle_rate.listing.repository;

import com.circlerate.circle_rate.listing.model.property.Property;
import com.circlerate.circle_rate.listing.payload.PrimaryFilterRequest;
import com.circlerate.circle_rate.listing.payload.SecondaryFilterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PropertyRepositoryImpl implements PropertyRepositoryCustom {
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<Property> getPropertiesByFilters(PrimaryFilterRequest primaryFilters, SecondaryFilterRequest secondaryFilters, int limit, int offset){
        //todo: add code for offset
        Query query = new Query();
        addPrimaryFiltersToQuery(primaryFilters, query);
        addSecondaryFiltersToQuery(secondaryFilters, query);
        query.limit(limit);
        return mongoTemplate.find(query, Property.class);
    }

    private void addPrimaryFiltersToQuery(PrimaryFilterRequest primaryFilterRequest, Query query){
        if(primaryFilterRequest.getListingType()!=null){
            query.addCriteria(Criteria.where("listingType").is(primaryFilterRequest.getListingType()));
        }

        if(primaryFilterRequest.getPropertyType() != null){
            query.addCriteria(Criteria.where("propertyType").is(primaryFilterRequest.getPropertyType()));
        }

        if (primaryFilterRequest.getSubLocalityName() != null) {
            query.addCriteria(Criteria.where("subLocalityName").is(primaryFilterRequest.getSubLocalityName()));
        }
        else if(primaryFilterRequest.getLocalityName()!=null){
            query.addCriteria(Criteria.where("localityName").is(primaryFilterRequest.getLocalityName()));
        }
        else if(primaryFilterRequest.getCityName()!=null){
            query.addCriteria(Criteria.where("cityName").is(primaryFilterRequest.getCityName()));
        }
        else if(primaryFilterRequest.getStateName()!=null){
            query.addCriteria(Criteria.where("stateName").is(primaryFilterRequest.getStateName()));
        }

        if (primaryFilterRequest.getMinPrice() != null && primaryFilterRequest.getMaxPrice() != null) {
            query.addCriteria(Criteria.where("expectedPriceInRupees").gte(primaryFilterRequest.getMinPrice()).lte(primaryFilterRequest.getMaxPrice()));
        } else if (primaryFilterRequest.getMinPrice() != null) {
            query.addCriteria(Criteria.where("expectedPriceInRupees").gte(primaryFilterRequest.getMinPrice()));
        } else if (primaryFilterRequest.getMaxPrice() != null) {
            query.addCriteria(Criteria.where("expectedPriceInRupees").lte(primaryFilterRequest.getMaxPrice()));
        }

        if (primaryFilterRequest.getMinSqFtArea() != null && primaryFilterRequest.getMaxSqFtArea() != null) {
            query.addCriteria(Criteria.where("areaInSqFt").gte(primaryFilterRequest.getMinSqFtArea()).lte(primaryFilterRequest.getMaxSqFtArea()));
        } else if (primaryFilterRequest.getMinSqFtArea() != null) {
            query.addCriteria(Criteria.where("areaInSqFt").gte(primaryFilterRequest.getMinSqFtArea()));
        } else if (primaryFilterRequest.getMaxSqFtArea() != null) {
            query.addCriteria(Criteria.where("areaInSqFt").lte(primaryFilterRequest.getMaxSqFtArea()));
        }


        if(primaryFilterRequest.getNoOfRooms()!=null){
            query.addCriteria(Criteria.where("noOfRooms").in(primaryFilterRequest.getNoOfRooms()));
        }
    }

    private void addSecondaryFiltersToQuery(SecondaryFilterRequest secondaryFilterRequest, Query query){
        //owner type
        if (secondaryFilterRequest.getOwnerType() != null) {
            query.addCriteria(Criteria.where("ownerType").is(secondaryFilterRequest.getOwnerType()));
        }
        // No. of washrooms
        if (secondaryFilterRequest.getNoOfWashrooms() != null) {
            query.addCriteria(Criteria.where("noOfWashrooms").is(secondaryFilterRequest.getNoOfWashrooms()));
        }

        // No. of balconies
        if (secondaryFilterRequest.getNoOfBalconies() != null) {
            query.addCriteria(Criteria.where("noOfBalconies").is(secondaryFilterRequest.getNoOfBalconies()));
        }

        // Property floor range
        if (secondaryFilterRequest.getMinPropertyFloor() != null && secondaryFilterRequest.getMaxPropertyFloor() != null) {
            query.addCriteria(Criteria.where("propertyFloor")
                    .gte(secondaryFilterRequest.getMinPropertyFloor())
                    .lte(secondaryFilterRequest.getMaxPropertyFloor()));
        } else if (secondaryFilterRequest.getMinPropertyFloor() != null) {
            query.addCriteria(Criteria.where("propertyFloor").gte(secondaryFilterRequest.getMinPropertyFloor()));
        } else if (secondaryFilterRequest.getMaxPropertyFloor() != null) {
            query.addCriteria(Criteria.where("propertyFloor").lte(secondaryFilterRequest.getMaxPropertyFloor()));
        }

        // Total floors range
        if (secondaryFilterRequest.getMinTotalFloors() != null && secondaryFilterRequest.getMaxTotalFloors() != null) {
            query.addCriteria(Criteria.where("totalFloors")
                    .gte(secondaryFilterRequest.getMinTotalFloors())
                    .lte(secondaryFilterRequest.getMaxTotalFloors()));
        } else if (secondaryFilterRequest.getMinTotalFloors() != null) {
            query.addCriteria(Criteria.where("totalFloors").gte(secondaryFilterRequest.getMinTotalFloors()));
        } else if (secondaryFilterRequest.getMaxTotalFloors() != null) {
            query.addCriteria(Criteria.where("totalFloors").lte(secondaryFilterRequest.getMaxTotalFloors()));
        }

        // Age of property range
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

}
