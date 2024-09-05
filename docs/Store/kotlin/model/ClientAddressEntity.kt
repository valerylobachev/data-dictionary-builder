// Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder)
// Date: 2024-08-19 17:06:09

package model

import jakarta.persistence.*
import java.time.Instant


/**
 * Client address
 */
@Entity
@Table(name = "client_client_addresses_table", schema = "client")
data class ClientAddressEntity(

    /**
     * Address Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Int,

    /**
     * Address name
     */
    @Column(name = "name", nullable = false, length = 100)
    var name: String,

    /**
     * Description
     */
    @Column(name = "description")
    var description: String? = null,

    /**
     * Client Id
     */
    @Column(name = "client_id", nullable = false)
    var clientId: Int,

    /**
     * First address line
     */
    @Column(name = "address_line1", nullable = false, length = 100)
    var addressLine1: String,

    /**
     * Second address line
     */
    @Column(name = "address_line2", length = 100)
    var addressLine2: String? = null,

    /**
     * City
     */
    @Column(name = "city", nullable = false, length = 50)
    var city: String,

    /**
     * State
     */
    @Column(name = "state", nullable = false, length = 50)
    var state: String,

    /**
     * Country
     */
    @Column(name = "country", nullable = false, length = 50)
    var country: String,

    /**
     * Post code
     */
    @Column(name = "postcode", nullable = false, length = 10)
    var postcode: String,

    /**
     * User created record
     */
    @Column(name = "created_by", nullable = false, length = 10)
    var createdBy: String,

    /**
     * Timestamp of record create
     */
    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,

    /**
     * User updated record
     */
    @Column(name = "updated_by", nullable = false, length = 10)
    var updatedBy: String,

    /**
     * Timestamp of record update
     */
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant,
) 