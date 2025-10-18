package com.circlerate.circle_rate.listing.model.property.dto;

import com.circlerate.circle_rate.listing.model.property.LandProperty;
import com.circlerate.circle_rate.listing.model.propertyenums.LandAmenities;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Component
@NoArgsConstructor
public class LandPropertyDto extends PropertyDto{
    private List<LandProperty.Suitability> suitability;
    private List<LandAmenities> amenities;

    public LandPropertyDto(LandProperty property) {
        super(property);

        this.suitability = property.getSuitability() != null
                ? new ArrayList<>(property.getSuitability()) // defensive copy
                : null;
        this.amenities = property.getAmenities();
    }


}
