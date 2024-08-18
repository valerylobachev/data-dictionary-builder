// Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder)
// Date: cl.date

package finance.data.fi.bp.model

import jakarta.persistence.*
import java.time.Instant
import finance.data.fi.le.model.LedgerEntryEntity


/**
 * Debtor
 */
@Entity
@Table(name = "debtors", schema = "bp")
data class DebtorEntity(

    /**
     * Debtor id
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
     * Reference to Debtor
     */
    @OneToMany(mappedBy = "debtor")
    var ledgerEntry: Collection<LedgerEntryEntity>? = null,

    /**
     * Reference to Debtor
     */
    @OneToOne(mappedBy = "businessPartner")
    var businessPartner: BusinessPartnerEntity? = null,
) 