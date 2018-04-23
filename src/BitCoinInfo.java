public class BitCoinInfo {
    private Long coin_id;
    private String address;
    private String n_tx;
    private String balance;
    public Long getCoin_id() {
        return coin_id;
    }

    public void setCoin_id(Long coin_id) {
        this.coin_id = coin_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getN_tx() {
        return n_tx;
    }

    public void setN_tx(String n_tx) {
        this.n_tx = n_tx;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "BitCoinInfo{" +
                "coin_id=" + coin_id +
                ", address='" + address + '\'' +
                ", n_tx='" + n_tx + '\'' +
                ", balance='" + balance + '\'' +
                '}';
    }
}
