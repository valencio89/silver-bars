package com.valencio.silverbars.domain;

import com.valencio.silverbars.support.DSL;
import com.valencio.silverbars.support.Users;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.groupingBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class BidTest {

    @Test
    public void should_make_grouping_orders_easier() {

        List<Order> orders = asList(
                DSL.buy(DSL.kg(1.0), DSL.£(300), Users.UserOne),
                DSL.buy(DSL.kg(5.0), DSL.£(300), Users.UserOne)
        );

        Map<Bid, List<Order>> grouped = orders.stream().collect(groupingBy(Bid::forOrder));

        AssertionsForClassTypes.assertThat(grouped).isEqualTo(aMap(
            new Bid(DSL.£(300), Order.Type.Buy), orders
        ));
    }

    private static <K, V> Map<K, V> aMap(K key, V value) {
        return new HashMap<K, V>() {{
            put(key, value);
        }};
    }
}
