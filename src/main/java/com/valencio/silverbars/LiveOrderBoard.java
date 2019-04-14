package com.valencio.silverbars;

import com.valencio.silverbars.domain.Bid;
import com.valencio.silverbars.domain.Order;
import com.valencio.silverbars.domain.OrderSummary;

import javax.measure.Quantity;
import javax.measure.quantity.Mass;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

public class LiveOrderBoard {
    private final List<Order> registeredOrders            = new ArrayList<>();
    private final Comparator<OrderSummary> byTypeAndPrice = new OrderComparator();

    public void register(Order order) {
        registeredOrders.add(order);
    }

    public void cancel(Order order) {
        registeredOrders.remove(order);
    }

    public List<OrderSummary> summary() {
        return registeredOrders.stream().
                collect(groupingBy(Bid::forOrder, mapping(Order::quantity, toList()))).
                entrySet().stream().
                map(toOrderSummary()).
                sorted(byTypeAndPrice).
                collect(toList());
    }

    private Function<Map.Entry<Bid, List<Quantity<Mass>>>, OrderSummary> toOrderSummary() {
        return entry -> new OrderSummary(entry.getKey(), entry.getValue());
    }
}
