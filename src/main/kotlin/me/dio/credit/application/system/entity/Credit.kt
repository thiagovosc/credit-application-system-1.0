package me.dio.credit.application.system.entity

import jakarta.persistence.*
import me.dio.credit.application.system.enummeration.Status
import org.hibernate.proxy.HibernateProxy
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "Credit")
data class Credit(
  @Column(nullable = false, unique = true) val creditCode: UUID = UUID.randomUUID(),
  @Column(nullable = false) val creditValue: BigDecimal = BigDecimal.ZERO,
  @Column(nullable = false) val dayFirstInstallment: LocalDate,
  @Column(nullable = false) val numberOfInstallments: Int = 0,
  @Enumerated val status: Status = Status.IN_PROGRESS,
  @ManyToOne var customer: Customer? = null,
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null
) {
  final override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null) return false
    val oEffectiveClass =
      if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
    val thisEffectiveClass =
      if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
    if (thisEffectiveClass != oEffectiveClass) return false
    other as Credit

    return id != null && id == other.id
  }

  final override fun hashCode(): Int =
    if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()
}
