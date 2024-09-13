// Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder)
// Date: 2024-09-13 11:22:44

package finance.data.ca.cl.model

import jakarta.persistence.*
import java.time.Instant


/**
 * Country
 */
@Entity
@Table(name = "countries")
data class CountryEntity(

    /**
     * Country Id
     */
    @Id
    @Column(name = "id", nullable = false, length = 3)
    var id: String,

    /**
     * Name
     */
    @Column(name = "name", nullable = false, length = 100)
    var name: String,

    /**
     * Country Key
     */
    @Column(name = "key", length = 3)
    var key: String? = null,

    /**
     * Language Id
     */
    @Column(name = "language_id", nullable = false, length = 3)
    var languageId: String,

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