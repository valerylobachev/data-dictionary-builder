// Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder)
// Date: 2024-09-13 11:22:44

package finance.data.ca.cur.model

import jakarta.persistence.*
import java.time.Instant


/**
 * Currency
 */
@Entity
@Table(name = "currencies")
data class CurrencyEntity(

    /**
     * Currency Id
     */
    @Id
    @Column(name = "id", nullable = false, length = 5)
    var id: String,

    /**
     * Name
     */
    @Column(name = "name", nullable = false, length = 100)
    var name: String,

    /**
     * Short name
     */
    @Column(name = "short_name", nullable = false, length = 20)
    var shortName: String,

    /**
     * Currency ISO code
     */
    @Column(name = "iso_code", nullable = false, length = 3)
    var isoCode: String,

    /**
     * Currency key
     */
    @Column(name = "key", nullable = false, length = 3)
    var key: String,

    /**
     * Currency decimals
     */
    @Column(name = "decimals", nullable = false)
    var decimals: Int,

    /**
     * User created record
     */
    @Column(name = "created_by", nullable = false, length = 20)
    var createdBy: String,

    /**
     * Timestamp of record create
     */
    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,

    /**
     * User updated record
     */
    @Column(name = "updated_by", nullable = false, length = 20)
    var updatedBy: String,

    /**
     * Timestamp of record update
     */
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant,
) 