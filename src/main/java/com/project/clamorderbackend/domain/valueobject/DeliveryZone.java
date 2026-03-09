package com.project.clamorderbackend.domain.valueobject;

import lombok.Getter;
import java.util.Set;

/**
 * Value object representing delivery zones and their shipping rules.
 */
@Getter
public enum DeliveryZone {

    /**
     * Taichung South District - special rule: min 3 jin, free shipping from 3 jin
     */
    TAICHUNG_SOUTH("南區", 3, true),

    /**
     * Taichung free shipping zones - min 5 jin, free shipping from 5 jin
     */
    TAICHUNG_FREE_ZONE(
        "西區,西屯,北區,北屯,南屯,東區,中區,大里,烏日,太平",
        5,
        true
    ),

    /**
     * Other counties - shipping fee applies
     */
    OTHER_COUNTIES("其他", 5, false);

    private final String districts;
    private final Integer minWeight;
    private final boolean freeShipping;

    private static final Set<String> TAICHUNG_SOUTH_DISTRICTS = Set.of("南區");
    private static final Set<String> TAICHUNG_FREE_DISTRICTS = Set.of(
        "西區", "西屯", "北區", "北屯", "南屯", "東區", "中區", "大里", "烏日", "太平"
    );

    DeliveryZone(String districts, Integer minWeight, boolean freeShipping) {
        this.districts = districts;
        this.minWeight = minWeight;
        this.freeShipping = freeShipping;
    }

    /**
     * Determine the delivery zone based on district
     */
    public static DeliveryZone fromDistrict(String district) {
        if (district == null) {
            return OTHER_COUNTIES;
        }
        if (TAICHUNG_SOUTH_DISTRICTS.contains(district)) {
            return TAICHUNG_SOUTH;
        }
        if (TAICHUNG_FREE_DISTRICTS.contains(district)) {
            return TAICHUNG_FREE_ZONE;
        }
        return OTHER_COUNTIES;
    }

    /**
     * Get minimum weight requirement for this zone
     */
    public Integer getMinWeight() {
        return minWeight;
    }

    /**
     * Check if this zone has free shipping
     */
    public boolean hasFreeShipping() {
        return freeShipping;
    }
}
