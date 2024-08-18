// Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder)
// Date: cl.date

package finance.data.lo.model

import jakarta.persistence.*
import java.time.Instant
import finance.data.fi.le.model.LedgerEntryEntity


/**
 * Plant
 */
@Entity
@Table(name = "plants", schema = "logistic")
data class PlantEntity(

    /**
     * Plant id
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
     * Short name
     */
    @Column(name = "short_name", nullable = false, length = 20)
    var shortName: String,

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
     * Reference to Plant
     */
    @OneToMany(mappedBy = "plant")
    var ledgerEntry: Collection<LedgerEntryEntity>? = null,

    /**
     * Reference to Plant
     */
    @OneToMany(mappedBy = "location")
    var location: Collection<LocationEntity>? = null,
) 