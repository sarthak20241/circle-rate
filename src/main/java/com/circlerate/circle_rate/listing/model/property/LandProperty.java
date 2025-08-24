package com.circlerate.circle_rate.listing.model.property;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class LandProperty extends Property {
    private List<Suitability> suitability;

    public enum Suitability{
        AGRICULTURE,
        RESIDENTIAL,
        INDUSTRIAL,
        COMMERCIAL,
        INSTITUTIONAL,
        OTHER
    }

}
