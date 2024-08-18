// Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder)
// Date: cl.date

package finance.data.fi.le.model

import java.util.UUID


/**
 * Ledger entry attribute
 */
data class LedgerEntryAttributeId(

    /**
     * Entry no
     */
    var entryNo: UUID = UUID.randomUUID(),

    /**
     * Attribute id
     */
    var attributeId: String = "",

    /**
     * Attribute value id
     */
    var attributeValueId: String = "",
) : Serializable 