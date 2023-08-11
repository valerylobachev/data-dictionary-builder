// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

package model

import jakarta.persistence.*


/**
 * Analytics data structure
 */
@Entity
@Table(name = "analyticses")
data class AnalyticsEntity(

    /**
     * Promotion Id
     */
    @Column(name = "promotion_id", length = 10)
    var promotionId: String? = null,

    /**
     * Segment Id
     */
    @Column(name = "segment_id", length = 10)
    var segmentId: String? = null,

    /**
     * Business Area Id
     */
    @Column(name = "business_area_id", length = 10)
    var businessAreaId: String? = null,

    /**
     * Reference to Promotion
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "promotion_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    var promotion: PromotionEntity? = null,

    /**
     * Reference to Segment
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "segment_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    var segment: SegmentEntity? = null,

    /**
     * Reference to BusinessArea
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "business_area_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    var businessArea: BusinessAreaEntity? = null,
) 