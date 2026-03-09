package com.project.clamorderbackend.domain;

import com.project.clamorderbackend.domain.valueobject.DeliveryZone;
import com.project.clamorderbackend.domain.valueobject.DiscountPolicy;
import com.project.clamorderbackend.domain.valueobject.PriceCalculation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for discount policy and pricing logic
 */
class DiscountPolicyTest {

    // ==================== Bulk Discount Tests ====================

    @Test
    void testBulkDiscount_NoDiscountForSmallOrder() {
        // 5斤以下無優惠
        BigDecimal discount = DiscountPolicy.calculateBulkDiscount(5);
        assertEquals(BigDecimal.ZERO, discount);
    }

    @Test
    void testBulkDiscount_Tier1_10Jin() {
        // 滿10斤，每斤-5元
        BigDecimal discount = DiscountPolicy.calculateBulkDiscount(10);
        assertEquals(BigDecimal.valueOf(-50), discount);
    }

    @Test
    void testBulkDiscount_Tier1_15Jin() {
        // 滿10斤未滿20斤，每斤-5元
        BigDecimal discount = DiscountPolicy.calculateBulkDiscount(15);
        assertEquals(BigDecimal.valueOf(-75), discount);
    }

    @Test
    void testBulkDiscount_Tier2_20Jin() {
        // 滿20斤，每斤-10元
        BigDecimal discount = DiscountPolicy.calculateBulkDiscount(20);
        assertEquals(BigDecimal.valueOf(-200), discount);
    }

    @Test
    void testBulkDiscount_Tier2_30Jin() {
        // 滿20斤，每斤-10元
        BigDecimal discount = DiscountPolicy.calculateBulkDiscount(30);
        assertEquals(BigDecimal.valueOf(-300), discount);
    }

    // ==================== Pickup Discount Tests ====================

    @Test
    void testPickupDiscount_NotPickup() {
        // 非自取無優惠
        BigDecimal discount = DiscountPolicy.calculatePickupDiscount(10, false);
        assertEquals(BigDecimal.ZERO, discount);
    }

    @Test
    void testPickupDiscount_Pickup() {
        // 自取每斤-10元
        BigDecimal discount = DiscountPolicy.calculatePickupDiscount(10, true);
        assertEquals(BigDecimal.valueOf(-100), discount);
    }

    // ==================== Shipping Fee Tests ====================

    @Test
    void testShippingFee_TaichungSouth_Under3Jin() {
        // 南區未滿3斤收費
        BigDecimal fee = DiscountPolicy.calculateShippingFee(DeliveryZone.TAICHUNG_SOUTH, 2);
        assertEquals(BigDecimal.valueOf(250), fee);
    }

    @Test
    void testShippingFee_TaichungSouth_3JinOrMore() {
        // 南區滿3斤免運
        BigDecimal fee = DiscountPolicy.calculateShippingFee(DeliveryZone.TAICHUNG_SOUTH, 3);
        assertEquals(BigDecimal.ZERO, fee);
    }

    @Test
    void testShippingFee_TaichungFreeZone_Under5Jin() {
        // 台中免運區未滿5斤收費
        BigDecimal fee = DiscountPolicy.calculateShippingFee(DeliveryZone.TAICHUNG_FREE_ZONE, 4);
        assertEquals(BigDecimal.valueOf(250), fee);
    }

    @Test
    void testShippingFee_TaichungFreeZone_5JinOrMore() {
        // 台中免運區滿5斤免運
        BigDecimal fee = DiscountPolicy.calculateShippingFee(DeliveryZone.TAICHUNG_FREE_ZONE, 5);
        assertEquals(BigDecimal.ZERO, fee);
    }

    @Test
    void testShippingFee_OtherCounties_Under5Jin() {
        // 外縣市未滿5斤
        BigDecimal fee = DiscountPolicy.calculateShippingFee(DeliveryZone.OTHER_COUNTIES, 4);
        assertEquals(BigDecimal.valueOf(250), fee);
    }

    @Test
    void testShippingFee_OtherCountries_5To14Jin() {
        // 外縣市5-14斤
        BigDecimal fee = DiscountPolicy.calculateShippingFee(DeliveryZone.OTHER_COUNTIES, 10);
        assertEquals(BigDecimal.valueOf(250), fee);
    }

    @Test
    void testShippingFee_OtherCountries_15JinOrMore() {
        // 外縣市滿15斤免運
        BigDecimal fee = DiscountPolicy.calculateShippingFee(DeliveryZone.OTHER_COUNTIES, 15);
        assertEquals(BigDecimal.ZERO, fee);
    }

    // ==================== Delivery Zone Tests ====================

    @Test
    void testDeliveryZone_TaichungSouth() {
        assertEquals(DeliveryZone.TAICHUNG_SOUTH, DeliveryZone.fromDistrict("南區"));
    }

    @Test
    void testDeliveryZone_TaichungFreeZones() {
        assertEquals(DeliveryZone.TAICHUNG_FREE_ZONE, DeliveryZone.fromDistrict("西區"));
        assertEquals(DeliveryZone.TAICHUNG_FREE_ZONE, DeliveryZone.fromDistrict("西屯"));
        assertEquals(DeliveryZone.TAICHUNG_FREE_ZONE, DeliveryZone.fromDistrict("北區"));
    }

    @Test
    void testDeliveryZone_OtherCounties() {
        assertEquals(DeliveryZone.OTHER_COUNTIES, DeliveryZone.fromDistrict("台北市"));
        assertEquals(DeliveryZone.OTHER_COUNTIES, DeliveryZone.fromDistrict("高雄市"));
        assertEquals(DeliveryZone.OTHER_COUNTIES, DeliveryZone.fromDistrict(null));
    }

    // ==================== Price Calculation Integration Tests ====================

    @Test
    void testFullCalculation_TaichungSouth_5Jin() {
        // 台中南區5斤一般商品
        // 小計: 170 * 5 = 850
        // 滿5斤無量販優惠
        // 非自取無自取優惠
        // 南區滿3斤免運
        // 總計: 850 + 0 + 0 + 0 = 850
        
        BigDecimal subtotal = BigDecimal.valueOf(170 * 5);
        BigDecimal bulkDiscount = DiscountPolicy.calculateBulkDiscount(5);
        BigDecimal pickupDiscount = DiscountPolicy.calculatePickupDiscount(5, false);
        BigDecimal shippingFee = DiscountPolicy.calculateShippingFee(DeliveryZone.TAICHUNG_SOUTH, 5);
        
        BigDecimal total = subtotal.add(bulkDiscount).add(pickupDiscount).add(shippingFee);
        
        assertEquals(BigDecimal.valueOf(850), total);
    }

    @Test
    void testFullCalculation_TaichungSouth_20Jin() {
        // 台中南區20斤
        // 小計: 170 * 20 = 3400
        // 滿20斤每斤-10: -200
        // 非自取: 0
        // 南區免運: 0
        // 總計: 3400 - 200 + 0 + 0 = 3200
        
        BigDecimal subtotal = BigDecimal.valueOf(170 * 20);
        BigDecimal bulkDiscount = DiscountPolicy.calculateBulkDiscount(20);
        BigDecimal pickupDiscount = DiscountPolicy.calculatePickupDiscount(20, false);
        BigDecimal shippingFee = DiscountPolicy.calculateShippingFee(DeliveryZone.TAICHUNG_SOUTH, 20);
        
        BigDecimal total = subtotal.add(bulkDiscount).add(pickupDiscount).add(shippingFee);
        
        assertEquals(BigDecimal.valueOf(3200), total);
    }

    @Test
    void testFullCalculation_Pickup_10Jin() {
        // 自取10斤
        // 小計: 170 * 10 = 1700
        // 滿10斤每斤-5: -50
        // 自取每斤-10: -100
        // 自取無運費: 0
        // 總計: 1700 - 50 - 100 + 0 = 1550
        
        BigDecimal subtotal = BigDecimal.valueOf(170 * 10);
        BigDecimal bulkDiscount = DiscountPolicy.calculateBulkDiscount(10);
        BigDecimal pickupDiscount = DiscountPolicy.calculatePickupDiscount(10, true);
        BigDecimal shippingFee = DiscountPolicy.calculateShippingFee(null, 10);
        
        BigDecimal total = subtotal.add(bulkDiscount).add(pickupDiscount).add(shippingFee);
        
        assertEquals(BigDecimal.valueOf(1550), total);
    }

    @Test
    void testFullCalculation_OtherCounty_10Jin() {
        // 外縣市10斤
        // 小計: 170 * 10 = 1700
        // 滿10斤每斤-5: -50
        // 非自取: 0
        // 5-14斤運費: 250
        // 總計: 1700 - 50 + 0 + 250 = 1900
        
        BigDecimal subtotal = BigDecimal.valueOf(170 * 10);
        BigDecimal bulkDiscount = DiscountPolicy.calculateBulkDiscount(10);
        BigDecimal pickupDiscount = DiscountPolicy.calculatePickupDiscount(10, false);
        BigDecimal shippingFee = DiscountPolicy.calculateShippingFee(DeliveryZone.OTHER_COUNTIES, 10);
        
        BigDecimal total = subtotal.add(bulkDiscount).add(pickupDiscount).add(shippingFee);
        
        assertEquals(BigDecimal.valueOf(1900), total);
    }
}
