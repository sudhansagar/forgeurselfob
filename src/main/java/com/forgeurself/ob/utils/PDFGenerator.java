package com.forgeurself.ob.utils;

import com.forgeurself.ob.entities.HomeLoan;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

@Component
public class PDFGenerator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PDFGenerator.class);

	public boolean generateAppln(HomeLoan loanDet, String filePath) {
		LOGGER.info("inside generateAppln...");
		boolean status = false;
		Document doc = new Document();
		try {
			PdfWriter.getInstance(doc, new FileOutputStream(filePath));
			doc.open();
			doc.add(generateTable(loanDet));
			doc.close();
			status = true;
		} catch (FileNotFoundException | DocumentException e) {
			LOGGER.error("Exception inside generateItinerary" + e);
		}
		return status;
	}

	private Element generateTable(HomeLoan loanDet) {
		PdfPTable table = new PdfPTable(2);
		PdfPCell cell;
		cell = new PdfPCell(new Phrase("Loan Application Details"));
		cell.setColspan(2);
		table.addCell(cell);
		
		table.addCell("Full Name ");
		table.addCell(loanDet.getFullName());

		table.addCell("Email");
		table.addCell(loanDet.getEmail());

		table.addCell("Phone Number");
		table.addCell(loanDet.getPhoneNumber());

		table.addCell("Address");
		table.addCell(loanDet.getAddress());

		table.addCell("Do you own or rent your home?");
		table.addCell(loanDet.getRentOrHome());

		table.addCell("Do you need to sell your home before buying a new one?");
		table.addCell(loanDet.getSellOrBuy());

		table.addCell("Are you pre-qualified for a loan?");
		table.addCell(loanDet.getPrequalified());


		table.addCell("Comments or questions :");
		table.addCell(loanDet.getComments());
		
		return table;
	}

}
