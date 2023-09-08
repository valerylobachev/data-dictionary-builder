// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

package finance.data.ledger_entry.model

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