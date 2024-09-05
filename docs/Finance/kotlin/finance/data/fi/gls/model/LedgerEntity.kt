// Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder)
// Date: 2024-08-19 17:06:07

package finance.data.fi.gls.model

import jakarta.persistence.*
import java.time.Instant


/**
 * Ledger
 */
@Entity
@Table(name = "ledgers", schema = "ledger")
data class LedgerEntity(

    /**
     * Ledger Id
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
     * Leading ledger indicator
     */
    @Column(name = "leading", nullable = false)
    var leading: Boolean,

    /**
     * Ledger Type
     */
    @Column(name = "ledger_type", nullable = false, length = 2)
    var ledgerType: String,

    /**
     * Underlying ledger
     */
    @Column(name = "underlying_ledger", length = 4)
    var underlyingLedger: String? = null,

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