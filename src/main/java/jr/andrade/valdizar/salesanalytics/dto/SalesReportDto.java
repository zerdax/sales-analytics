package jr.andrade.valdizar.salesanalytics.dto;

public class SalesReportDto {

    private Integer customersAmount;
    private Integer salesmenAmount;
    private String identifierMostValuableSale;
    private String worstSalemanName;

    public Integer getCustomersAmount() {
        return customersAmount;
    }

    public void setCustomersAmount(Integer customersAmount) {
        this.customersAmount = customersAmount;
    }

    public Integer getSalesmenAmount() {
        return salesmenAmount;
    }

    public void setSalesmenAmount(Integer salesmenAmount) {
        this.salesmenAmount = salesmenAmount;
    }

    public String getIdentifierMostValuableSale() {
        return identifierMostValuableSale;
    }

    public void setIdentifierMostValuableSale(String identifierMostValuableSale) {
        this.identifierMostValuableSale = identifierMostValuableSale;
    }

    public String getWorstSalemanName() {
        return worstSalemanName;
    }

    public void setWorstSalemanName(String worstSalemanName) {
        this.worstSalemanName = worstSalemanName;
    }
}
