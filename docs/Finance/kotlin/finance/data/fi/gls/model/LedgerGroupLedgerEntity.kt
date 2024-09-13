// Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder)
// Date: 2024-09-13 11:22:44

package finance.data.fi.gls.model

import jakarta.persistence.*
import java.time.Instant


/**
 * Ledger assignments to ledger group
 */
@Entity
@Table(name = "ledger_group_ledgers", schema = "ledger")
@IdClass(LedgerGroupLedgerId::class)
data class LedgerGroupLedgerEntity(

    /**
     * Ledger Group Id
     */
    @Id
    @Column(name = "ledger_group_id", nullable = false, length = 2)
    var ledgerGroupId: String,

    /**
     * Ledger Id
     */
    @Id
    @Column(name = "ledger_id", nullable = false, length = 4)
    var ledgerId: String,

    /**
     * Representative ledger indicator
     */
    @Column(name = "representative", nullable = false)
    var representative: Boolean,

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