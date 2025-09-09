package com.circlerate.circle_rate.listing.model.property;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Component
@NoArgsConstructor
public class LandProperty extends Property {
    private List<Suitability> suitability;

    public LandProperty(LandProperty request) {
        super(request);
        this.suitability = request.getSuitability();
    }

    public enum Suitability{
        AGRICULTURE,
        RESIDENTIAL,
        INDUSTRIAL,
        COMMERCIAL,
        INSTITUTIONAL,
        OTHER
    }

}
