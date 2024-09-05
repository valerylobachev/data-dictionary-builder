// Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder)
// Date: 2024-08-19 17:06:09

package model

import jakarta.persistence.*
import java.time.Instant


/**
 * Person
 */
@Entity
@Table(name = "persons")
data class PersonEntity(

    /**
     * Person Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Int,

    /**
     * Person first name
     */
    @Column(name = "firstname", nullable = false, length = 40)
    var firstname: String,

    /**
     * Person last name
     */
    @Column(name = "lastname", nullable = false, length = 40)
    var lastname: String,

    /**
     * Search index
     */
    @Column(name = "search", nullable = false)
    var search: String,

    /**
     * Person attributes
     * Map string: string
     */
    @Column(name = "attributes", nullable = false)
    var attributes: Map<String, String>,

    /**
     * Person external Ids
     */
    @Column(name = "external_ids", nullable = false)
    var externalIds: Map<String, Int>,

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