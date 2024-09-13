// Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder)
// Date: 2024-09-13 11:22:44

package finance.data.ca.uom.model

import jakarta.persistence.*
import java.time.Instant


/**
 * Unit of measurement text
 */
@Entity
@Table(name = "uom_texts")
@IdClass(UnitOfMeasurementTextId::class)
data class UnitOfMeasurementTextEntity(

    /**
     * Language Id
     */
    @Id
    @Column(name = "language_id", nullable = false, length = 3)
    var languageId: String,

    /**
     * Unit of measurement Id
     */
    @Id
    @Column(name = "uom_id", nullable = false, length = 6)
    var uomId: String,

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
     * UoM commercial code
     */
    @Column(name = "commercial_code", nullable = false, length = 6)
    var commercialCode: String,

    /**
     * UoM technical code
     */
    @Column(name = "technical_code", nullable = false, length = 6)
    var technicalCode: String,

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