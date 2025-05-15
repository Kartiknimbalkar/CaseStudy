package com.example.demo.service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import lombok.extern.slf4j.Slf4j;

import com.example.demo.model.Sales;
import com.example.demo.repo.SalesRepository;

@Service
@Slf4j
public class SalesReportService {
	
	// generate PDF

    @Autowired
    private SalesRepository salesRepo;

    public byte[] generateSalesReport() throws IOException {
    	log.info("Fetching all the sales for generating report");
        List<Sales> salesList = salesRepo.findAll(); 
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        document.add(new Paragraph("Sales Report").setBold().setFontSize(18));

        // Table Headers
        float[] columnWidths = {150f, 60f, 100f, 60f, 100f, 150f};
        Table table = new Table(columnWidths);
        table.addCell("Doctor Name");
        table.addCell("Order ID");
        table.addCell("Batch ID");
        table.addCell("Quantity");
        table.addCell("Total Price");
        table.addCell("Sale Date");

        // Add Sales Data to Table
        for (Sales sale : salesList) {
        	table.addCell(String.valueOf(sale.getDoctorName()));
            table.addCell(String.valueOf(sale.getOrderId()));
            table.addCell(sale.getBatchId());
            table.addCell(String.valueOf(sale.getQuantity()));
            table.addCell(String.valueOf(sale.getTotalPrice()));
            table.addCell(String.valueOf(sale.getSaleDate()));
        }

        document.add(table);
        document.close();
        
        log.info("PDF generated");

        return byteArrayOutputStream.toByteArray();
    }
}
