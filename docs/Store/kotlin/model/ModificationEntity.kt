// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

package model

import jakarta.persistence.*
import java.time.Instant


/**
 * Modification data structure
 */
@Entity
@Table(name = "modifications")
data class ModificationEntity(

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