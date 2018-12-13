package edu.temple.bytexchange;

class StockShort {

    private String symbol;
    private String name;
    private  String exchange;

    private double currentPrice = 0;
    private double openPrice = 0;

    public  StockShort (String newSymbol, String newName, String newExchange){
        this.symbol = newSymbol;
        this.name = newName;
        this.exchange = newExchange;
    }

    public  StockShort (String newSymbol, String newName, double newCurrentPrice, double newOpenPrice){
        this.symbol = newSymbol;
        this.name = newName;
        this.currentPrice = newCurrentPrice;
        this.openPrice = newOpenPrice;
    }

    public StockShort() {

    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }

    public double stockDifference(){
        return currentPrice - openPrice;
    }
}
