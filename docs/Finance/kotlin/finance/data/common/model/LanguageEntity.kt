// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

package finance.data.common.model

import jakarta.persistence.*
import java.time.Instant
import finance.data.company_code.model.CompanyCodeEntity


/**
 * Language
 */
@Entity
@Table(name = "languages", schema = "common")
data class LanguageEntity(

    /**
     * Language Id
     */
    @Id
    @Column(name = "id", nullable = false, length = 3)
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
     * Reference to language
     */
    @OneToMany(mappedBy = "country")
    var country: Collection<CountryEntity>? = null,

    /**
     * Reference to language
     */
    @OneToMany(mappedBy = "unitOfMeasurementText")
    var unitOfMeasurementText: Collection<UnitOfMeasurementTextEntity>? = null,

    /**
     * Reference to Language
     */
    @OneToMany(mappedBy = "companyCode")
    var companyCode: Collection<CompanyCodeEntity>? = null,
) 