package my.madet.function;

/**
 * I plan to make it more 
 * @author Mahadir
 *
 */
public class InAppPurchase {
	
	//purchase states
	public static final int PURCHASE_STATE_PURCHASED = 0;
	public static final int PURCHASE_STATE_CANCELED = 1;
	public static final int PURCHASE_STATE_REFUNDED = 2;
	
	//myvpn response codes
	public static final int MYVPN_RESPONSE_BILLING_OK = 200;
	public static final int MYVPN_RESPONSE_BILLING_USER_NOT_FOUND = 404;
	public static final int MYVPN_RESPONSE_BILLING_SIGNATURE_ERROR = 401;
	public static final int MYVPN_RESPONSE_BILLING_ENTRY_EXIST = 208;
	
	//myvpn link account response codes
	public static final int MYVPN_RESPONSE_LINK_OK = 404;
	public static final int MYVPN_RESPONSE_LINK_ACCOUNT_NOT_VALIDATED = 403;
	public static final int MYVPN_RESPONSE_LINK_UNAUTHORIZED = 401;
	public static final int MYVPN_RESPONSE_LINK_ORDER_ID_ERROR = 405;
	public static final int MYVPN_RESPONSE_LINK_ALREADY_LINKED = 402;
	
	// Billing response codes
    public static final int BILLING_RESPONSE_RESULT_OK = 0;
    public static final int BILLING_RESPONSE_RESULT_USER_CANCELED = 1;
    public static final int BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE = 3;
    public static final int BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE = 4;
    public static final int BILLING_RESPONSE_RESULT_DEVELOPER_ERROR = 5;
    public static final int BILLING_RESPONSE_RESULT_ERROR = 6;
    public static final int BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED = 7;
    public static final int BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED = 8;
    
    // Keys for the responses from InAppBillingService
    public static final String RESPONSE_CODE = "RESPONSE_CODE";
    public static final String RESPONSE_GET_SKU_DETAILS_LIST = "DETAILS_LIST";
    public static final String RESPONSE_BUY_INTENT = "BUY_INTENT";
    public static final String RESPONSE_INAPP_PURCHASE_DATA = "INAPP_PURCHASE_DATA";
    public static final String RESPONSE_INAPP_SIGNATURE = "INAPP_DATA_SIGNATURE";
    public static final String RESPONSE_INAPP_ITEM_LIST = "INAPP_PURCHASE_ITEM_LIST";
    public static final String RESPONSE_INAPP_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST";
    public static final String RESPONSE_INAPP_SIGNATURE_LIST = "INAPP_DATA_SIGNATURE_LIST";
    public static final String INAPP_CONTINUATION_TOKEN = "INAPP_CONTINUATION_TOKEN";
    
    // Item types
    public static final String ITEM_TYPE_INAPP = "inapp";
    public static final String ITEM_TYPE_SUBS = "subs";

    // some fields on the getSkuDetails response bundle
    public static final String GET_SKU_DETAILS_ITEM_LIST = "ITEM_ID_LIST";
    public static final String GET_SKU_DETAILS_ITEM_TYPE_LIST = "ITEM_TYPE_LIST";
    
    
    /**
     * constructor
     */
    public InAppPurchase(){
    	
    }
}
