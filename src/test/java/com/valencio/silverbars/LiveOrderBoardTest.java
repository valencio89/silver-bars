package com.valencio.silverbars;

import com.valencio.silverbars.domain.Order;
import com.valencio.silverbars.domain.OrderSummary;
import com.valencio.silverbars.domain.PricePerKg;
import com.valencio.silverbars.support.DSL;
import com.valencio.silverbars.support.Users;
import org.junit.Before;
import org.junit.Test;

import javax.measure.Quantity;
import javax.measure.quantity.Mass;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class LiveOrderBoardTest {

    private LiveOrderBoard board;

    @Before
    public void setUp() throws Exception {
        board = new LiveOrderBoard();
    }

    @Test
    public void should_start_with_no_orders_displayed() {

        assertThat(board.summary()).isEmpty();
    }

    @Test
    public void should_display_a_registered_order() {

        board.register(DSL.buy(DSL.kg(3.5), DSL.£(306), Users.UserOne));

        assertThat(board.summary()).containsExactly(
                buyOrderSummary(DSL.kg(3.5), DSL.£(306))
        );
    }

    @Test
    public void should_display_a_summary_of_those_registered_orders_which_type_and_price_match() {

        board.register(DSL.sell(DSL.kg(3.5), DSL.£(306), Users.UserOne));
        board.register(DSL.sell(DSL.kg(2.0), DSL.£(306), Users.UserFour));

        assertThat(board.summary()).containsExactly(
                sellOrderSummary(DSL.kg(5.5), DSL.£(306))
        );
    }


    @Test
    public void should_allow_to_cancel_a_registered_order_when_requested_so_by_the_user_who_placed_it() {

        board.register(DSL.buy(DSL.kg(3.5), DSL.£(306), Users.UserOne));
        board.cancel(DSL.buy(DSL.kg(3.5), DSL.£(306), Users.UserOne));

        assertThat(board.summary()).isEmpty();
    }

    @Test
    public void should_not_cancel_a_registered_order_when_requested_so_by_a_user_who_did_not_place_it() {

        board.register(DSL.buy(DSL.kg(3.5), DSL.£(306), Users.UserOne));
        board.cancel(DSL.buy(DSL.kg(3.5), DSL.£(306), Users.UserFour));

        assertThat(board.summary()).containsExactly(
                buyOrderSummary(DSL.kg(3.5), DSL.£(306))
        );
    }

    @Test
    public void should_allow_to_cancel_one_of_several_registered_orders() {

        board.register(DSL.buy(DSL.kg(3.5), DSL.£(306), Users.UserOne));
        board.register(DSL.buy(DSL.kg(2.5), DSL.£(306), Users.UserOne));
        board.register(DSL.buy(DSL.kg(1.5), DSL.£(306), Users.UserOne));

        board.cancel(DSL.buy(DSL.kg(3.5), DSL.£(306), Users.UserOne));

        assertThat(board.summary()).containsExactly(
                buyOrderSummary(DSL.kg(4.0), DSL.£(306))
        );
    }

    @Test
    public void should_produce_distinct_summaries_for_orders_with_different_price() {

        board.register(DSL.buy(DSL.kg(3.5), DSL.£(306), Users.UserOne));
        board.register(DSL.buy(DSL.kg(7.0), DSL.£(250), Users.UserOne));

        assertThat(board.summary()).containsExactly(
                buyOrderSummary(DSL.kg(3.5), DSL.£(306)),
                buyOrderSummary(DSL.kg(7.0), DSL.£(250))
        );
    }

    @Test
    public void should_sort_Sell_orders_in_ascending_order() {

        board.register(DSL.sell(DSL.kg(3.5), DSL.£(306), Users.UserOne));
        board.register(DSL.sell(DSL.kg(7.0), DSL.£(250), Users.UserOne));

        assertThat(board.summary()).containsExactly(
                sellOrderSummary(DSL.kg(7.0), DSL.£(250)),
                sellOrderSummary(DSL.kg(3.5), DSL.£(306))
        );
    }

    @Test
    public void should_sort_Buy_orders_in_descending_order() {

        board.register(DSL.buy(DSL.kg(7.0), DSL.£(250), Users.UserOne));
        board.register(DSL.buy(DSL.kg(3.5), DSL.£(306), Users.UserOne));

        assertThat(board.summary()).containsExactly(
                buyOrderSummary(DSL.kg(3.5), DSL.£(306)),
                buyOrderSummary(DSL.kg(7.0), DSL.£(250))
        );
    }

    @Test
    public void should_display_Buy_orders_before_Sell_orders() {

        board.register(DSL.buy(DSL.kg(7.0), DSL.£(250), Users.UserOne));
        board.register(DSL.sell(DSL.kg(7.0), DSL.£(430), Users.UserOne));

        assertThat(board.summary()).containsExactly(
                buyOrderSummary(DSL.kg(7.0), DSL.£(250)),
                sellOrderSummary(DSL.kg(7.0), DSL.£(430))
        );
    }

    // a tiny DSL to improve the readability of the test scenarios

    private static OrderSummary buyOrderSummary(Quantity<Mass> quantity, PricePerKg pricePerKg) {
        return new OrderSummary(quantity, pricePerKg, Order.Type.Buy);
    }

    private static OrderSummary sellOrderSummary(Quantity<Mass> quantity, PricePerKg pricePerKg) {
        return new OrderSummary(quantity, pricePerKg, Order.Type.Sell);
    }
}
