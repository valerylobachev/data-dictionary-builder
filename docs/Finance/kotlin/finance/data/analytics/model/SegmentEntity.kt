// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

package finance.data.analytics.model

import jakarta.persistence.*
import java.time.Instant
import finance.data.ledger_entry.model.LedgerEntryEntity


/**
 * Segment
 */
@Entity
@Table(name = "segments", schema = "analytics")
data class SegmentEntity(

    /**
     * Segment id
     */
    @Id
    @Column(name = "id", nullable = false, length = 10)
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
     * Reference to Segment
     */
    @JsonIgnore
    @OneToMany(mappedBy = "segment")
    var ledgerEntry: Collection<LedgerEntryEntity>? = null,
) 