package jr.andrade.valdizar.salesanalytics.service;

public enum DataTypeEnum {

    SALESMAN("001", "Vendedor"),
    CUSTOMER("002", "Cliente"),
    SALE("003", "Venda");

    private String dataTypeId;
    private String dataDescription;

    DataTypeEnum(String dataTypeId, String dataDescription) {
        this.dataTypeId = dataTypeId;
        this.dataDescription = dataDescription;
    }

    public String getDataTypeId() {
        return dataTypeId;
    }

    public String getDataDescription() {
        return dataDescription;
    }
}
