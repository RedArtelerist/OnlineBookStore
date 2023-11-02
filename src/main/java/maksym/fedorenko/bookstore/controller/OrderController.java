package maksym.fedorenko.bookstore.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import maksym.fedorenko.bookstore.dto.order.CreateOrderRequestDto;
import maksym.fedorenko.bookstore.dto.order.OrderDto;
import maksym.fedorenko.bookstore.dto.order.OrderItemDto;
import maksym.fedorenko.bookstore.dto.order.UpdateOrderRequestDto;
import maksym.fedorenko.bookstore.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@Validated
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<OrderDto> findAll(Authentication authentication) {
        return orderService.findAll(authentication);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto createOrder(
            Authentication authentication,
            @Valid @RequestBody CreateOrderRequestDto requestDto) {
        return orderService.createOrder(authentication, requestDto);
    }

    @GetMapping("/{id}/items")
    @PreAuthorize("hasRole('USER')")
    public OrderDto findById(Authentication authentication, @PathVariable @Positive Long id) {
        return orderService.getById(authentication, id);
    }

    @GetMapping("/{orderId}/items/{id}")
    @PreAuthorize("hasRole('USER')")
    public OrderItemDto findOrderItemById(
            Authentication authentication,
            @PathVariable @Positive Long orderId,
            @PathVariable @Positive Long id) {
        return orderService.getOrderItemById(authentication, id);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderDto update(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateOrderRequestDto requestDto) {
        return orderService.update(id, requestDto);
    }
}
