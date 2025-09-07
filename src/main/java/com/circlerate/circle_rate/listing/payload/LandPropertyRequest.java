package com.circlerate.circle_rate.listing.payload;

import com.circlerate.circle_rate.listing.model.property.LandProperty;
import com.circlerate.circle_rate.listing.model.property.Property;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class LandPropertyRequest extends PropertyRequest {
    private List<LandProperty.Suitability> suitability;

    @Override
    public Property toEntity() {
        LandProperty property = new LandProperty();
        copyCommonFields(property);
        property.setSuitability(this.suitability);
        return property;
    }
}
