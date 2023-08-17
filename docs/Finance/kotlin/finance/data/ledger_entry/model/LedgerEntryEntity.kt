// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

package finance.data.ledger_entry.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import finance.data.logistic.model.PlantEntity
import finance.data.common.model.UnitOfMeasurementEntity
import finance.data.company_code.model.CompanyCodeEntity
import finance.data.analytics.model.SegmentEntity
import finance.data.gl_account.model.GLAccountEntity
import finance.data.analytics.model.FunctionalAreaEntity
import finance.data.logistic.model.LocationEntity
import finance.data.logistic.model.MaterialEntity
import finance.data.analytics.model.BusinessAreaEntity
import finance.data.analytics.model.DebtorEntity
import finance.data.ledger.model.LedgerEntity
import finance.data.analytics.model.CreditorEntity
import finance.data.analytics.model.BusinessPartnerEntity
import finance.data.common.model.CurrencyEntity
import finance.data.logistic.model.ValuationAreaEntity


/**
 * Ledger Entry
 */
@Entity
@Table(name = "ledger_entries", schema = "ledger_entry")
data class LedgerEntryEntity(

    /**
     * Entry no
     */
    @Id
    @Column(name = "entry_no", nullable = false)
    var entryNo: UUID,

    /**
     * Ledger Id
     */
    @Column(name = "ledger_id", nullable = false, length = 4)
    var ledgerId: String,

    /**
     * Company code
     */
    @Column(name = "company_code_id", nullable = false, length = 4)
    var companyCodeId: String,

    /**
     * Fiscal year
     */
    @Column(name = "fiscal_year", nullable = false)
    var fiscalYear: Int,

    /**
     * Document number
     */
    @Column(name = "document_number", nullable = false, length = 10)
    var documentNumber: String,

    /**
     * Line item no
     */
    @Column(name = "line_item_no", nullable = false, length = 6)
    var lineItemNo: String,

    /**
     * Posting date
     */
    @Column(name = "posting_date", nullable = false)
    var postingDate: LocalDate,

    /**
     * Document date
     */
    @Column(name = "document_date", nullable = false)
    var documentDate: LocalDate,

    /**
     * Valuation date
     */
    @Column(name = "valuation_date", nullable = false)
    var valuationDate: LocalDate,

    /**
     * Period
     */
    @Column(name = "period", nullable = false)
    var period: Int,

    /**
     * Fiscal year period
     */
    @Column(name = "fy_period", nullable = false)
    var fyPeriod: Int,

    /**
     * Chart of accounts id
     */
    @Column(name = "chart_of_accounts_id", nullable = false, length = 4)
    var chartOfAccountsId: String,

    /**
     * General ledger account id
     */
    @Column(name = "gl_account_id", nullable = false, length = 10)
    var glAccountId: String,

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
     * Business partner id
     */
    @Column(name = "business_partner_id", length = 10)
    var businessPartnerId: String? = null,

    /**
     * Business area id
     */
    @Column(name = "business_area_id", length = 4)
    var businessAreaId: String? = null,

    /**
     * Functional area id
     */
    @Column(name = "functional_area_id", length = 20)
    var functionalAreaId: String? = null,

    /**
     * Segment id
     */
    @Column(name = "segment_id", length = 10)
    var segmentId: String? = null,

    /**
     * Valuation area id
     */
    @Column(name = "valuation_area_id", length = 20)
    var valuationAreaId: String? = null,

    /**
     * Plant id
     */
    @Column(name = "plant_id", length = 4)
    var plantId: String? = null,

    /**
     * Location id
     */
    @Column(name = "location_id", length = 10)
    var locationId: String? = null,

    /**
     * Material id
     */
    @Column(name = "material_id", length = 40)
    var materialId: String? = null,

    /**
     * Debit/credit indicator
     */
    @Column(name = "debit_credit", nullable = false, length = 1)
    var debitCredit: String,

    /**
     * Document amount
     */
    @Column(name = "document_amount", nullable = false, precision = 25, scale = 2)
    var documentAmount: BigDecimal,

    /**
     * Document currency
     */
    @Column(name = "document_currency_id", nullable = false, length = 5)
    var documentCurrencyId: String,

    /**
     * Company code amount
     */
    @Column(name = "company_code_amount", nullable = false, precision = 25, scale = 2)
    var companyCodeAmount: BigDecimal,

    /**
     * Company code currency
     */
    @Column(name = "company_code_currency_id", nullable = false, length = 5)
    var companyCodeCurrencyId: String,

    /**
     * Quantity
     */
    @Column(name = "quantity", precision = 25, scale = 3)
    var quantity: BigDecimal? = null,

    /**
     * Unit of measure id
     */
    @Column(name = "uom_id", length = 6)
    var uomId: String? = null,

    /**
     * Basic unit of measure id
     */
    @Column(name = "basic_uom_id", length = 6)
    var basicUomId: String? = null,

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
     * Reference to Ledger
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "ledger_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    var ledger: LedgerEntity? = null,

    /**
     * Reference to CompanyCode
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "company_code_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    var companyCode: CompanyCodeEntity? = null,

    /**
     * Reference to GLAccount
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn([
      @JoinColumn(name = "chart_of_accounts_id", referencedColumnName = "chart_of_accounts_id", nullable = false, updatable = false, insertable = false),
      @JoinColumn(name = "gl_account_id", referencedColumnName = "gl_account_id", nullable = false, updatable = false, insertable = false),
    ])
    var glAccount: GLAccountEntity? = null,

    /**
     * Reference to Creditor
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "creditor_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    var creditor: CreditorEntity? = null,

    /**
     * Reference to Debtor
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "debtor_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    var debtor: DebtorEntity? = null,

    /**
     * Reference to BusinessPartner
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "business_partner_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    var businessPartner: BusinessPartnerEntity? = null,

    /**
     * Reference to BusinessArea
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "business_area_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    var businessArea: BusinessAreaEntity? = null,

    /**
     * Reference to FunctionalArea
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "functional_area_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    var functionalArea: FunctionalAreaEntity? = null,

    /**
     * Reference to Segment
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "segment_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    var segment: SegmentEntity? = null,

    /**
     * Reference to ValuationArea
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "valuation_area_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    var valuationArea: ValuationAreaEntity? = null,

    /**
     * Reference to Plant
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "plant_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    var plant: PlantEntity? = null,

    /**
     * Reference to Location
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn([
      @JoinColumn(name = "plant_id", referencedColumnName = "plant_id", nullable = false, updatable = false, insertable = false),
      @JoinColumn(name = "location_id", referencedColumnName = "location_id", nullable = false, updatable = false, insertable = false),
    ])
    var location: LocationEntity? = null,

    /**
     * Reference to Material
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "material_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    var material: MaterialEntity? = null,

    /**
     * Reference to Currency
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "document_currency_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    var currency: CurrencyEntity? = null,

    /**
     * Reference to Currency
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "company_code_currency_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    var currency1: CurrencyEntity? = null,

    /**
     * Reference to UnitOfMeasurement
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "uom_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    var uom: UnitOfMeasurementEntity? = null,

    /**
     * Reference to UnitOfMeasurement
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "basic_uom_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    var basicUom: UnitOfMeasurementEntity? = null,

    /**
     * Reference to LedgerEntry
     */
    @JsonIgnore
    @OneToMany(mappedBy = "ledgerEntryAttribute")
    var ledgerEntryAttribute: Collection<LedgerEntryAttributeEntity>? = null,
) 