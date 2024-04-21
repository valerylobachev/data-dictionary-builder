// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

package model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant


/**
 * Item
 */
@Entity
@Table(name = "order_items")
data class ItemEntity(

    /**
     * Item Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Int,

    /**
     * Item Id
     */
    @Column(name = "item_id", nullable = false)
    var itemId: Int,

    /**
     * Amount
     */
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    var price: BigDecimal,

    /**
     * User created record
     */
    @Column(name = "created_by", nullable = false, length = 10)
    var createdBy: String,

    /**
     * Timestamp of record create
     */
    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,

    /**
     * User updated record
     */
    @Column(name = "updated_by", nullable = false, length = 10)
    var updatedBy: String,

    /**
     * Timestamp of record update
     */
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant,

    /**
     * Reference to item
     */
    @OneToMany(mappedBy = "orderLine")
    var orderLine: Collection<OrderLineEntity>? = null,
) 