package com.example.demo.cart.service

import com.example.demo.cart.domain.CartItem
import com.example.demo.cart.domain.CartWeek
import com.example.demo.cart.dto.CartItemRequest
import com.example.demo.cart.dto.CartItemResponse
import com.example.demo.cart.dto.CartWeekRequest
import com.example.demo.cart.dto.CartWeekResponse
import com.example.demo.cart.repository.CartItemRepository
import com.example.demo.cart.repository.CartWeekRepository
import com.example.demo.cart.service.ports.inp.CartService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class DefaultCartService(
    private val cartWeekRepository: CartWeekRepository,
    private val cartItemRepository: CartItemRepository,
) : CartService {

    override fun findWeeksByMonth(memberId: Long, year: Int, month: Int): List<CartWeekResponse> =
        cartWeekRepository.findWeeksWithItems(memberId, year, month)
            .map { CartWeekResponse.from(it) }

    @Transactional
    override fun findOrCreateWeek(memberId: Long, request: CartWeekRequest): CartWeekResponse {
        val week = cartWeekRepository
            .findByMemberIdAndYearAndMonthAndWeekOfMonth(memberId, request.year, request.month, request.weekOfMonth)
            ?: cartWeekRepository.save(
                CartWeek(
                    memberId = memberId,
                    year = request.year,
                    month = request.month,
                    weekOfMonth = request.weekOfMonth,
                    label = "${request.month}월 ${request.weekOfMonth}주차",
                )
            )
        return CartWeekResponse.from(week)
    }

    @Transactional
    override fun createItem(weekId: Long, memberId: Long, request: CartItemRequest): CartItemResponse {
        val week = cartWeekRepository.findByIdAndMemberId(weekId, memberId)
            ?: throw NoSuchElementException("CartWeek not found: $weekId")
        val displayOrder = cartItemRepository.countByCartWeekId(weekId).toInt()
        val item = cartItemRepository.save(
            CartItem(
                cartWeek = week,
                name = request.name,
                quantity = request.quantity,
                unitPrice = request.unitPrice,
                displayOrder = displayOrder,
            )
        )
        return CartItemResponse.from(item)
    }

    @Transactional
    override fun updateItem(id: Long, memberId: Long, request: CartItemRequest): CartItemResponse {
        val item = cartItemRepository.findByIdAndCartWeek_MemberId(id, memberId)
            ?: throw NoSuchElementException("CartItem not found: $id")
        item.update(request.name, request.quantity, request.unitPrice)
        return CartItemResponse.from(item)
    }

    @Transactional
    override fun deleteItem(id: Long, memberId: Long) {
        val item = cartItemRepository.findByIdAndCartWeek_MemberId(id, memberId)
            ?: throw NoSuchElementException("CartItem not found: $id")
        cartItemRepository.delete(item)
    }

    @Transactional
    override fun toggleCheck(id: Long, memberId: Long): CartItemResponse {
        val item = cartItemRepository.findByIdAndCartWeek_MemberId(id, memberId)
            ?: throw NoSuchElementException("CartItem not found: $id")
        item.toggleCheck()
        return CartItemResponse.from(item)
    }

    @Transactional
    override fun deleteCheckedItems(weekId: Long, memberId: Long) {
        cartWeekRepository.findByIdAndMemberId(weekId, memberId)
            ?: throw NoSuchElementException("CartWeek not found: $weekId")
        cartItemRepository.deleteByCartWeekIdAndIsChecked(weekId, true)
    }
}
