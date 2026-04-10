package com.example.demo.reminder.domain

import com.example.demo.common.domain.BaseEntity
import com.example.demo.reminder.dto.UpdateReminderListRequest
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "reminder_lists")
class ReminderList(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val memberId: Long,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var color: String = "#007AFF",

    @Column(nullable = false)
    var displayOrder: Int = 0,

    @OneToMany(mappedBy = "reminderList", cascade = [CascadeType.ALL], orphanRemoval = true)
    val reminders: MutableList<Reminder> = mutableListOf()
) : BaseEntity() {

    fun update(request: UpdateReminderListRequest) {
        this.name = request.name
        this.color = request.color
        this.displayOrder = request.displayOrder
        this.modifiedAt = LocalDateTime.now()
    }
}
