package com.example.demo.cart.repository

import com.example.demo.cart.domain.CartWeek
import com.example.demo.cart.domain.QCartItem
import com.example.demo.cart.domain.QCartWeek
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class CartWeekRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : CartWeekRepositoryCustom {

    private val week = QCartWeek.cartWeek
    private val item = QCartItem.cartItem

    override fun findWeeksWithItems(memberId: Long, year: Int, month: Int): List<CartWeek> =
        queryFactory
            .selectFrom(week)
            .leftJoin(week.items, item).fetchJoin()
            .where(
                week.memberId.eq(memberId),
                week.year.eq(year),
                week.month.eq(month),
            )
            .orderBy(week.weekOfMonth.asc())
            .distinct()
            .fetch()
}
