// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

package finance.data.analytics.model

import jakarta.persistence.*
import java.time.Instant
import finance.data.ledger_entry.model.LedgerEntryEntity


/**
 * Business partner
 */
@Entity
@Table(name = "business_partners", schema = "analytics")
data class BusinessPartnerEntity(

    /**
     * Business partner id
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
     * Creditor id
     */
    @Column(name = "creditor_id", length = 10)
    var creditorId: String? = null,

    /**
     * Debtor id
     */
    @Column(name = "debtor_id", length = 10)
    var debtorId: String? = null,

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
     * Reference to Creditor
     */
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "creditor_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    var creditor: CreditorEntity? = null,

    /**
     * Reference to Debtor
     */
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "debtor_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    var debtor: DebtorEntity? = null,

    /**
     * Reference to BusinessPartner
     */
    @JsonIgnore
    @OneToMany(mappedBy = "businessPartner")
    var ledgerEntry: Collection<LedgerEntryEntity>? = null,
) 