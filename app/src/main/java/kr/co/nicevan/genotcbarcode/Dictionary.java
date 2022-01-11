package kr.co.nicevan.genotcbarcode;


public class Dictionary {

    private String Date;
    private String Name;
    private String Amount;
    private String Balance;

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        this.Date = date;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getBalance() {
        return Balance;
    }

    public void setBalance(String balance) {
        Balance = balance;
    }

    public Dictionary(String date, String name, String amount, String balance) {
        this.Date = date;
        Name = name;
        Amount = amount;
        Balance = balance;
    }
}