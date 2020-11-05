package wrteam.ekart.shop.model;

public class WalletTransaction {

    private String id, user_id, order_id, type, txn_id, amount, status, message, date_created, last_updated;

    public WalletTransaction() {
    }

    public WalletTransaction(String id, String user_id, String order_id, String type, String txn_id, String amount, String status, String message, String date_created, String last_updated) {
        this.id = id;
        this.user_id = user_id;
        this.order_id = order_id;
        this.type = type;
        this.txn_id = txn_id;
        this.amount = amount;
        this.status = status;
        this.message = message;
        this.date_created = date_created;
        this.last_updated = last_updated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTxn_id() {
        return txn_id;
    }

    public void setTxn_id(String txn_id) {
        this.txn_id = txn_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }
}
