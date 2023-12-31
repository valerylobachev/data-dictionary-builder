// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

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
     * Relation to persons
     */
    @OneToMany(mappedBy = "groupMember")
    var groupMember: Collection<GroupMemberEntity>? = null,
) 