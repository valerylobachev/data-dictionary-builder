// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

package finance.data.gl_account.model



/**
 * General ledger account
 */
data class GLAccountId(

    /**
     * Chart of accounts id
     */
    var chartOfAccountsId: String = "",

    /**
     * General ledger account id
     */
    var glAccountId: String = "",
) : Serializable 