// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

package model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant


/**
 * Order line
 */
@Entity
@Table(name = "order_order_lines")
data class OrderLineEntity(

    /**
     * OrderLine Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Int,

    /**
     * Order Id
     */
    @Column(name = "order_id", nullable = false)
    var orderId: Int,

    /**
     * Item Id
     */
    @Column(name = "item_id", nullable = false)
    var itemId: Int,

    /**
     * Promotion Id
     */
    @Column(name = "promotion_id", length = 10)
    var promotionId: String? = null,

    /**
     * Segment Id
     */
    @Column(name = "segment_id", length = 10)
    var segmentId: String? = null,

    /**
     * Business Area Id
     */
    @Column(name = "business_area_id", length = 10)
    var businessAreaId: String? = null,

    /**
     * Amount
     */
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    var price: BigDecimal,

    /**
     * Quantity
     */
    @Column(name = "quantity", nullable = false, precision = 10, scale = 2)
    var quantity: BigDecimal,

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
     * Reference to order
     */
    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    var order: OrderEntity? = null,

    /**
     * Reference to item
     */
    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    var item: ItemEntity? = null,
) 