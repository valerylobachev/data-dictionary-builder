// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

package finance.data.common.model

import jakarta.persistence.*
import java.time.Instant
import finance.data.logistic.model.MaterialEntity
import finance.data.ledger_entry.model.LedgerEntryEntity


/**
 * Unit of measurement
 */
@Entity
@Table(name = "units_of_measurement", schema = "common")
data class UnitOfMeasurementEntity(

    /**
     * Unit of measurement Id
     */
    @Id
    @Column(name = "id", nullable = false, length = 6)
    var id: String,

    /**
     * Name
     */
    @Column(name = "name", nullable = false, length = 100)
    var name: String,

    /**
     * Short name
     */
    @Column(name = "short_name", nullable = false, length = 30)
    var shortName: String,

    /**
     * ISOCode
     */
    @Column(name = "iso_code", length = 3)
    var isoCode: String? = null,

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
     * Reference to unit of measurement
     */
    @JsonIgnore
    @OneToMany(mappedBy = "unitOfMeasurementText")
    var unitOfMeasurementText: Collection<UnitOfMeasurementTextEntity>? = null,

    /**
     * Reference to Unit Of Measurement
     */
    @JsonIgnore
    @OneToMany(mappedBy = "material")
    var material: Collection<MaterialEntity>? = null,

    /**
     * Reference to UnitOfMeasurement
     */
    @JsonIgnore
    @OneToMany(mappedBy = "uom")
    var ledgerEntry: Collection<LedgerEntryEntity>? = null,

    /**
     * Reference to UnitOfMeasurement
     */
    @JsonIgnore
    @OneToMany(mappedBy = "basicUom")
    var ledgerEntry1: Collection<LedgerEntryEntity>? = null,
) 