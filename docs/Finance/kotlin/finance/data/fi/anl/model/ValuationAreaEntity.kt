// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

package finance.data.fi.anl.model

import jakarta.persistence.*
import java.time.Instant
import finance.data.fi.cc.model.CompanyCodeEntity
import finance.data.fi.le.model.LedgerEntryEntity


/**
 * Valuation area
 */
@Entity
@Table(name = "valuation_areas", schema = "analytics")
data class ValuationAreaEntity(

    /**
     * Valuation area id
     */
    @Id
    @Column(name = "id", nullable = false, length = 20)
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
     * Company code
     */
    @Column(name = "company_code_id", nullable = false, length = 4)
    var companyCodeId: String,

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
     * Reference to CompanyCode
     */
    @ManyToOne
    @JoinColumn(name = "company_code_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    var companyCode: CompanyCodeEntity? = null,

    /**
     * Reference to ValuationArea
     */
    @OneToMany(mappedBy = "valuationArea")
    var ledgerEntry: Collection<LedgerEntryEntity>? = null,
) 