// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

package finance.data.gl_account.model

import jakarta.persistence.*
import java.time.Instant
import finance.data.ledger_entry.model.LedgerEntryEntity


/**
 * General ledger account
 */
@Entity
@Table(name = "gl_accounts", schema = "gl_account")
@IdClass(GLAccountId::class)
data class GLAccountEntity(

    /**
     * Chart of accounts id
     */
    @Id
    @Column(name = "chart_of_accounts_id", nullable = false, length = 4)
    var chartOfAccountsId: String,

    /**
     * General ledger account id
     */
    @Id
    @Column(name = "gl_account_id", nullable = false, length = 10)
    var glAccountId: String,

    /**
     * Short name
     */
    @Column(name = "short_name", nullable = false, length = 20)
    var shortName: String,

    /**
     * Name
     */
    @Column(name = "name", nullable = false, length = 100)
    var name: String,

    /**
     * General ledger account type
     */
    @Column(name = "gl_account_type", nullable = false, length = 1)
    var glAccountType: String,

    /**
     * General ledger account group id
     */
    @Column(name = "gl_account_group_id", nullable = false, length = 10)
    var glAccountGroupId: String,

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
     * Reference to Chart Of Accounts
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "chart_of_accounts_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    var chartOfAccounts: ChartOfAccountsEntity? = null,

    /**
     * Reference to GLAccountGroup
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn([
      @JoinColumn(name = "chart_of_accounts_id", referencedColumnName = "chart_of_accounts_id", nullable = false, updatable = false, insertable = false),
      @JoinColumn(name = "gl_account_group_id", referencedColumnName = "gl_account_group_id", nullable = false, updatable = false, insertable = false),
    ])
    var gLAccountGroup: GLAccountGroupEntity? = null,

    /**
     * Reference to GLAccount
     */
    @JsonIgnore
    @OneToMany(mappedBy = "glAccount")
    var ledgerEntry: Collection<LedgerEntryEntity>? = null,
) 