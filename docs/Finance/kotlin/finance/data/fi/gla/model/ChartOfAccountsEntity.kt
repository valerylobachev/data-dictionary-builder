// Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder)
// Date: 2024-09-13 11:22:44

package finance.data.fi.gla.model

import jakarta.persistence.*
import java.time.Instant


/**
 * Chart of accounts
 */
@Entity
@Table(name = "charts_of_accounts", schema = "gl_account")
data class ChartOfAccountsEntity(

    /**
     * Chart of accounts id
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