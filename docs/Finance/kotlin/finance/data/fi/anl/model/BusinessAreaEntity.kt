// Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder)
// Date: 2024-08-18 21:59:59

package finance.data.fi.anl.model

import jakarta.persistence.*
import java.time.Instant
import finance.data.fi.le.model.LedgerEntryEntity


/**
 * Business area
 */
@Entity
@Table(name = "business_areas", schema = "analytics")
data class BusinessAreaEntity(

    /**
     * Business area id
     */
    @Id
    @Column(name = "id", nullable = false, length = 4)
    var id: String,

    /**
     * Name
     */
    @Column(name = "name", nullable = false, length = 100)
    var name: String,

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

    /**
     * Reference to BusinessArea
     */
    @OneToMany(mappedBy = "businessArea")
    var ledgerEntry: Collection<LedgerEntryEntity>? = null,
) 