package com.example.demo.cart.controller

import com.example.demo.cart.dto.CartItemRequest
import com.example.demo.cart.dto.CartItemResponse
import com.example.demo.cart.dto.CartWeekRequest
import com.example.demo.cart.dto.CartWeekResponse
import com.example.demo.cart.service.ports.inp.CartService
import com.example.demo.common.security.resolver.CurrentMember
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cart")
class CartController(private val cartService: CartService) {

    @GetMapping
    fun getByMonth(
        @RequestParam year: Int,
        @RequestParam month: Int,
        @CurrentMember memberId: Long,
    ): List<CartWeekResponse> = cartService.findWeeksByMonth(memberId, year, month)

    @PostMapping("/weeks")
    @ResponseStatus(HttpStatus.CREATED)
    fun createWeek(
        @Valid @RequestBody request: CartWeekRequest,
        @CurrentMember memberId: Long,
    ): CartWeekResponse = cartService.findOrCreateWeek(memberId, request)

    @PostMapping("/weeks/{weekId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    fun createItem(
        @PathVariable weekId: Long,
        @Valid @RequestBody request: CartItemRequest,
        @CurrentMember memberId: Long,
    ): CartItemResponse = cartService.createItem(weekId, memberId, request)

    @PutMapping("/items/{id}")
    fun updateItem(
        @PathVariable id: Long,
        @Valid @RequestBody request: CartItemRequest,
        @CurrentMember memberId: Long,
    ): CartItemResponse = cartService.updateItem(id, memberId, request)

    @DeleteMapping("/items/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteItem(
        @PathVariable id: Long,
        @CurrentMember memberId: Long,
    ) = cartService.deleteItem(id, memberId)

    @PatchMapping("/items/{id}/check")
    fun toggleCheck(
        @PathVariable id: Long,
        @CurrentMember memberId: Long,
    ): CartItemResponse = cartService.toggleCheck(id, memberId)

    @DeleteMapping("/weeks/{weekId}/checked")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCheckedItems(
        @PathVariable weekId: Long,
        @CurrentMember memberId: Long,
    ) = cartService.deleteCheckedItems(weekId, memberId)
}
