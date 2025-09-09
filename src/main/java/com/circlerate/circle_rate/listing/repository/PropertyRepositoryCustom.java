package com.circlerate.circle_rate.listing.repository;

import com.circlerate.circle_rate.listing.model.property.Property;
import com.circlerate.circle_rate.listing.payload.PrimaryFilterRequest;
import com.circlerate.circle_rate.listing.payload.SecondaryFilterRequest;

import java.util.List;

public interface PropertyRepositoryCustom {
    List<Property> getPropertiesByFilters(PrimaryFilterRequest primaryFilterRequest, SecondaryFilterRequest secondaryFilterRequest, int limit, int offset);
}
