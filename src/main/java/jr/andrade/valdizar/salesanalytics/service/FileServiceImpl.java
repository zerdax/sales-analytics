package jr.andrade.valdizar.salesanalytics.service;

import jr.andrade.valdizar.salesanalytics.dto.SalesReportDto;
import jr.andrade.valdizar.salesanalytics.model.Customer;
import jr.andrade.valdizar.salesanalytics.model.Sale;
import jr.andrade.valdizar.salesanalytics.model.SaleItem;
import jr.andrade.valdizar.salesanalytics.model.Salesman;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static jr.andrade.valdizar.salesanalytics.utils.PathUtils.PATH_DIR_OUT;

public class FileServiceImpl implements FileService {

    private static final String FILE_COLUMN_SEPARATOR = "ç";
    private static final String SALE_ITEM_SEPARATOR = ",";
    private static final String SALE_ITEM_COLUMN_SEPARATOR = "-";

    private static final Map<String, Customer> customers = new ConcurrentHashMap<>();
    private static final Map<String, Salesman> salesmen = new ConcurrentHashMap<>();
    private static final Map<String, Sale> sales = new ConcurrentHashMap<>();

    private static Long customerId = 0L;
    private static Long salesmanId = 0L;
    private static Long saleId = 0L;

    private String fileNameOut;

    @Override
    public void inputFileProcess(Path path) {
        try {
            fileNameOut = String.format("%s.out", path.getFileName());
            Files.lines(path).forEach(this::process);
            generateReportData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void process(String line) {
        String dataTypeId = line.split(FILE_COLUMN_SEPARATOR)[0];
        DataTypeEnum dataType = Optional.of(Arrays.stream(DataTypeEnum.values())
                .filter(dataTypeEnum -> dataTypeEnum.getDataTypeId().equals(dataTypeId))
                .findFirst().orElseThrow(() -> new RuntimeException(String
                        .format("Invalid data type: %s.", dataTypeId)))).get();
        String[] values = line.split(FILE_COLUMN_SEPARATOR);
        switch(dataType) {
            case SALESMAN:
                addSalesman(values);
                break;
            case SALE:
                addSale(values);
                break;
            case CUSTOMER:
                addCustomer(values);
                break;
        }
    }

    private synchronized Long generateCustomerId() {
        return ++customerId;
    }

    private synchronized Long generateSalesmanId() {
        return ++salesmanId;
    }

    private synchronized Long generateSaleId() {
        return ++saleId;
    }

    private void addCustomer(String[] values) {
        Customer customer = new Customer();
        try {
            customer.setCustomerId(generateCustomerId());
            customer.setCnpj(values[1]);
            customer.setName(values[2]);
            customer.setBusinessArea(values[3]);
            customers.put(String.valueOf(customer.getCustomerId()), customer);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    private void addSalesman(String[] values) {
        Salesman salesman = new Salesman();
        try {
            salesman.setSalesmanId(generateSalesmanId());
            salesman.setCpf(values[1]);
            salesman.setName(values[2]);
            salesman.setSalary(Double.valueOf(values[3]));
            salesmen.put(salesman.getCpf(), salesman);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    private void addSale(String[] values) {
        Sale sale = new Sale();
        try {
            List<SaleItem> saleItems = new ArrayList<>();
            Arrays.stream(values[2].replace("[", "").replace("]", "")
                    .split(SALE_ITEM_SEPARATOR)).forEach(s -> {
                        SaleItem item;
                        if(nonNull(item = createSaleItem(s.split(SALE_ITEM_COLUMN_SEPARATOR)))) {
                            saleItems.add(item);
                        }

            });
            sale.setSaleId(generateSaleId());
            sale.setSaleIdentifier(values[1]);
            sale.setSaleItems(saleItems);
            sale.setSalesmanName(values[3]);
            sales.put(sale.getSaleIdentifier(), sale);
        }  catch (NumberFormatException | IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    private SaleItem createSaleItem(String[] values) {
        SaleItem item = new SaleItem();
        try {
            item.setItemId(values[0]);
            item.setItemQuantity(Integer.valueOf(values[1]));
            item.setItemPrice(Double.valueOf(values[2]));
            return item;
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void generateReportData() {
        SalesReportDto report = new SalesReportDto();
        Map<String, Double> salesTotalValue = new LinkedHashMap<>();
        report.setCustomersAmount(customers.keySet().size());
        report.setSalesmenAmount(salesmen.keySet().size());
        sales.forEach((key, value) -> {
            List<SaleItem> items = value.getSaleItems();
            Double saleValue = items.stream().mapToDouble(
                    saleItem -> saleItem.getItemPrice() * saleItem.getItemQuantity()).sum();
            salesTotalValue.put(key, saleValue);
        });
        salesTotalValue.entrySet().stream().max(Map.Entry.comparingByValue())
                .ifPresent(stringDoubleEntry -> report.setIdentifierMostValuableSale(stringDoubleEntry.getKey()));
        List<SalesmanSales> salesmanSalesList = new ArrayList<>();
        sales.forEach((s, sale) -> {
            SalesmanSales salesmanSales = new SalesmanSales();
            salesmanSales.setSalesmanName(sale.getSalesmanName());
            salesmanSales.setSaleTotalValue(sale.getSaleItems().stream().mapToDouble(
                    saleItem -> saleItem.getItemPrice() * saleItem.getItemQuantity()).sum());
            salesmanSalesList.add(salesmanSales);
        });
        salesmanSalesList.stream().collect(Collectors.groupingBy(SalesmanSales::getSalesmanName,
                Collectors.summingDouble(SalesmanSales::getSaleTotalValue)))
                .entrySet().stream().min(Map.Entry.comparingByValue())
                .ifPresent(stringDoubleEntry -> report.setWorstSalemanName(stringDoubleEntry.getKey()));
        exportReport(report);
        clearMaps();
    }

    private void exportReport(SalesReportDto report) {
        Path pathFileOut = Paths.get(String.format("%s%s%s", PATH_DIR_OUT, File.separator, fileNameOut));
        try(BufferedWriter bw = Files.newBufferedWriter(pathFileOut)) {
            bw.write(String.format("%s%n%s;%s;%s;%s%n",
                    "QTD. CLIENTES;QTD. VENDEDORES;ID MAIOR VENDA;PIOR VENDEDOR",
                    report.getCustomersAmount(),
                    report.getSalesmenAmount(),
                    report.getIdentifierMostValuableSale(),
                    report.getWorstSalemanName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearMaps() {
        customers.clear();
        salesmen.clear();
        sales.clear();
    }
}

class SalesmanSales {

    private String salesmanName;
    private Double saleTotalValue;

    public String getSalesmanName() {
        return salesmanName;
    }

    public void setSalesmanName(String salesmanName) {
        this.salesmanName = salesmanName;
    }

    public Double getSaleTotalValue() {
        return saleTotalValue;
    }

    public void setSaleTotalValue(Double saleTotalValue) {
        this.saleTotalValue = saleTotalValue;
    }
}