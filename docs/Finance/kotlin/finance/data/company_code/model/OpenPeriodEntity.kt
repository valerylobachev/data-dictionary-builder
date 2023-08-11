// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

package finance.data.company_code.model

import jakarta.persistence.*


/**
 * Open period
 */
@Entity
@Table(name = "open_periods", schema = "company_code")
@IdClass(OpenPeriodId::class)
data class OpenPeriodEntity(

    /**
     * Posting period variant id
     */
    @Id
    @Column(name = "posting_period_variant_id", nullable = false, length = 4)
    var postingPeriodVariantId: String,

    /**
     * Account type
     */
    @Id
    @Column(name = "account_type", nullable = false, length = 1)
    var accountType: String,

    /**
     * To account
     */
    @Id
    @Column(name = "to_account", nullable = false, length = 10)
    var toAccount: String,

    /**
     * From account
     */
    @Column(name = "from_account", nullable = false, length = 10)
    var fromAccount: String,

    /**
     * Group 1: From fiscal year
     */
    @Column(name = "from_fy1", nullable = false)
    var fromFy1: Int,

    /**
     * Group 1: From period
     */
    @Column(name = "from_period1", nullable = false)
    var fromPeriod1: Int,

    /**
     * Group 1: To fiscal year
     */
    @Column(name = "to_fy1", nullable = false)
    var toFy1: Int,

    /**
     * Group 1: To period
     */
    @Column(name = "to_period1", nullable = false)
    var toPeriod1: Int,

    /**
     * Group 2: From fiscal year
     */
    @Column(name = "from_fy2", nullable = false)
    var fromFy2: Int,

    /**
     * Group 2: From period
     */
    @Column(name = "from_period2", nullable = false)
    var fromPeriod2: Int,

    /**
     * Group 2: To fiscal year
     */
    @Column(name = "to_fy2", nullable = false)
    var toFy2: Int,

    /**
     * Group 2: To period
     */
    @Column(name = "to_period2", nullable = false)
    var toPeriod2: Int,

    /**
     * Group 3: From fiscal year
     */
    @Column(name = "from_fy3", nullable = false)
    var fromFy3: Int,

    /**
     * Group 3: From period
     */
    @Column(name = "from_period3", nullable = false)
    var fromPeriod3: Int,

    /**
     * Group 3: To fiscal year
     */
    @Column(name = "to_fy3", nullable = false)
    var toFy3: Int,

    /**
     * Group 3: To period
     */
    @Column(name = "to_period3", nullable = false)
    var toPeriod3: Int,

    /**
     * Reference to PostingPeriodVariant
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "posting_period_variant_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    var postingPeriodVariant: PostingPeriodVariantEntity? = null,
) 