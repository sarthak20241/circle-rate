package com.circlerate.circle_rate.listing.payload;

import com.circlerate.circle_rate.listing.model.property.LandProperty;
import com.circlerate.circle_rate.listing.model.property.Property;
import com.circlerate.circle_rate.listing.model.propertyenums.LandAmenities;
import com.circlerate.circle_rate.listing.model.propertyenums.PropertyType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LandPropertyRequest extends PropertyRequest {
    private List<LandProperty.Suitability> suitability;
    private List<LandAmenities> amenities;

    @Override
    public Property toEntity() {
        LandProperty property = new LandProperty();
        copyCommonFields(property);
        property.setSuitability(this.suitability);
        property.setPropertyType(PropertyType.LAND);
        property.setAmenities(this.amenities);
        return property;
    }
}
