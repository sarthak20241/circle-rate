package com.circlerate.circle_rate.listing.model.property;

import com.circlerate.circle_rate.listing.model.propertyenums.LandAmenities;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Component
@Document("land_properties")
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class LandProperty extends Property {
    private List<Suitability> suitability;
    private List<LandAmenities> amenities;

    public enum Suitability{
        AGRICULTURE,
        RESIDENTIAL,
        INDUSTRIAL,
        COMMERCIAL,
        INSTITUTIONAL,
        OTHER
    }

}
