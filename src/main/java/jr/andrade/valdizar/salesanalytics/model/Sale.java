package jr.andrade.valdizar.salesanalytics.model;

import java.io.Serializable;
import java.util.List;

public class Sale implements Serializable {
    private Long saleId;
    private String saleIdentifier;
    private String salesmanName;
    private String dataFileHash;
    private List<SaleItem> saleItems;

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    public String getSaleIdentifier() {
        return saleIdentifier;
    }

    public void setSaleIdentifier(String saleIdentifier) {
        this.saleIdentifier = saleIdentifier;
    }

    public String getSalesmanName() {
        return salesmanName;
    }

    public void setSalesmanName(String salesmanName) {
        this.salesmanName = salesmanName;
    }

    public String getDataFileHash() {
        return dataFileHash;
    }

    public void setDataFileHash(String dataFileHash) {
        this.dataFileHash = dataFileHash;
    }

    public List<SaleItem> getSaleItems() {
        return saleItems;
    }

    public void setSaleItems(List<SaleItem> saleItems) {
        this.saleItems = saleItems;
    }

    public Double getTotalSaleValue() {
        return saleItems.stream()
                .mapToDouble(saleItem -> saleItem.getItemPrice() * saleItem.getItemQuantity()).sum();
    }
}
