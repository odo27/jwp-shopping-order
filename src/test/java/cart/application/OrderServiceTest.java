package cart.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.List;

import cart.domain.CartItems;
import cart.domain.Order;
import cart.domain.OrderPoint;
import cart.domain.Point;
import cart.dto.OrderRequest;
import cart.dto.OrderResponse;
import cart.exception.PriceNotMatchException;
import cart.fixture.CartItemFixture;
import cart.fixture.MemberFixture;
import cart.fixture.OrderFixture;
import cart.repository.CartItemRepository;
import cart.repository.OrderRepository;
import cart.repository.PointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private PointRepository pointRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private OrderRepository orderRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(pointRepository, cartItemRepository, orderRepository);
    }

    @Test
    void createOrder() {
        given(cartItemRepository.findByIds(any(), any())).willReturn(CartItems.of(List.of(CartItemFixture.CHICKEN, CartItemFixture.PIZZA)));
        given(pointRepository.updatePoint(any(), any(), any(), any())).willReturn(new OrderPoint(1L, Point.valueOf(100), Point.valueOf(50)));
        given(orderRepository.createOrder(any(), any())).willReturn(new Order(10L, OrderFixture.ORDER));
        final OrderResponse result = orderService.createOrder(MemberFixture.MEMBER, new OrderRequest(List.of(1L, 2L), 100, 25_000));
        assertAll(
                () -> assertThat(result.getOrderId()).isEqualTo(10L),
                () -> assertThat(result.getCreateAt()).isEqualTo("2023-05-31 10:00:00.0"),
                () -> assertThat(result.getOrderItems().get(0).getProductId()).isEqualTo(1L),
                () -> assertThat(result.getOrderItems().get(0).getProductName()).isEqualTo("치킨"),
                () -> assertThat(result.getOrderItems().get(0).getQuantity()).isEqualTo(1),
                () -> assertThat(result.getOrderItems().get(0).getPrice()).isEqualTo(10_000),
                () -> assertThat(result.getOrderItems().get(0).getImageUrl()).isEqualTo("http://example.com/chicken.jpg"),
                () -> assertThat(result.getOrderItems().get(1).getProductId()).isEqualTo(2L),
                () -> assertThat(result.getOrderItems().get(1).getProductName()).isEqualTo("피자"),
                () -> assertThat(result.getOrderItems().get(1).getQuantity()).isEqualTo(1),
                () -> assertThat(result.getOrderItems().get(1).getPrice()).isEqualTo(15_000),
                () -> assertThat(result.getOrderItems().get(1).getImageUrl()).isEqualTo("http://example.com/pizza.jpg"),
                () -> assertThat(result.getTotalPrice()).isEqualTo(25_000),
                () -> assertThat(result.getUsedPoint()).isEqualTo(100),
                () -> assertThat(result.getEarnedPoint()).isEqualTo(50)
        );
    }

    @Test
    void validateTotalPriceFail() {
        given(cartItemRepository.findByIds(any(), any())).willReturn(CartItems.of(List.of(CartItemFixture.CHICKEN, CartItemFixture.PIZZA)));
        final OrderRequest invalidPriceRequest = new OrderRequest(null, 0, 25_001);
        assertThatThrownBy(() -> orderService.createOrder(MemberFixture.MEMBER, invalidPriceRequest))
                .isInstanceOf(PriceNotMatchException.class)
                .hasMessage(new PriceNotMatchException(25_001, 25_000).getMessage());
    }
}