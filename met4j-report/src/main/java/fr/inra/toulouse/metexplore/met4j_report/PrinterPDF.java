/*******************************************************************************
 * Copyright INRA
 * 
 *  Contact: florence.maurier@toulouse.inra.fr
 * 
 * 
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *  In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *  The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 ******************************************************************************/
package fr.inra.toulouse.metexplore.met4j_report;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Vector;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

// TODO: Auto-generated Javadoc
/**
 * The Class PrinterPDF.
 */
public class PrinterPDF implements Printer {
	
	/** The normal font. */
	//private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.RED);
	private static Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
	
	/** The big title font. */
	private static Font bigTitleFont = new Font(Font.FontFamily.TIMES_ROMAN, 36, Font.BOLD);
	
	/** The chapter title font. */
	private static Font chapterTitleFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
	
	/** The section title font. */
	private static Font sectionTitleFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
	
	/** The subsection title font. */
	private static Font subsectionTitleFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	
	/** The bold. */
	private static Font bold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	
	/** The italic. */
	private static Font italic = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.ITALIC);
    
    /** The table title font. */
    //private static Font tableTitleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    private static Font tableTitleFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
    
    /** The Gray. */
    private static BaseColor Gray = new BaseColor(215,215,215);    
    
    /** The light gray. */
    private static BaseColor lightGray = new BaseColor(240,240,240);    
    
    /** The symbols. */
    private static Font symbols = new Font(Font.FontFamily.ZAPFDINGBATS, 12);    
    
    /** The full star. */
    private static Chunk fullStar = new Chunk(String.valueOf((char) 72), symbols);
    
    /** The empty star. */
    private static Chunk emptyStar = new Chunk(String.valueOf((char) 73), symbols);
    // paragraph.add(star);
    
    /** The pdf adr. */
    private String pdfAdr;
    
    /** The header title. */
    //private String title;
    private String headerTitle;
    
    /** The output stream. */
    private ByteArrayOutputStream outputStream;
    
    /** The writer. */
    private PdfWriter writer; 
    
    /** The document. */
    private Document document;
    
    /** The chapter number. */
    private Integer chapterNumber;
    
    /** The section number. */
    private Integer sectionNumber;
    
    /** The subsection number. */
    private Integer subsectionNumber;
    
    /** The last chapter. */
    private Chapter lastChapter;
    
    /** The last section. */
    private Section lastSection;
    
    /** The last subsection. */
    private Section lastSubsection;
    
    /** The figure number. */
    private Integer figureNumber;
		
	/**
	 * Instantiates a new printer pdf.
	 *
	 * @param pdfAdr the pdf adr
	 */
	public PrinterPDF(String pdfAdr) {
		this.pdfAdr = pdfAdr;
		this.chapterNumber = 0;
		this.sectionNumber = 0;
		this.subsectionNumber = 0;
		this.figureNumber = 0;
		try {			
			outputStream = new ByteArrayOutputStream();
			DataOutputStream output = new DataOutputStream(outputStream);
			document = new Document();
			writer = PdfWriter.getInstance(document, output);
			MyFooter event = new MyFooter();
	        writer.setPageEvent(event);
			document.open();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see parsebionet.utils.report.Printer#addMetaData(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void addMetaData(String title, String header, String subject, String keywords, String author, String creator) {
		//this.title = title;
		document.addTitle(title);
	    document.addSubject(subject);
	    document.addKeywords(keywords);
	    document.addAuthor(author);
	    document.addCreator(creator);
	    this.headerTitle = header;
	}

	/* (non-Javadoc)
	 * @see parsebionet.utils.report.Printer#close()
	 */
	public void close(){
		addLastChapter();
		document.close();
		try {
			outputStream.writeTo(new FileOutputStream(pdfAdr));
		} catch (FileNotFoundException e) {
			System.err.println("Error while writing outputstream to file "+pdfAdr+"\nNo file found");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error while writing outputstream to file "+pdfAdr+"\nIO exception");
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see parsebionet.utils.report.Printer#addTitlePage(java.lang.String, java.lang.String)
	 */
	public void addTitlePage(String reportTitle, String introduction){
	    Paragraph bigTitle = new Paragraph();
	    Paragraph title = new Paragraph("\n\n\n\n\n"+reportTitle, bigTitleFont);
	    title.setAlignment(Element.ALIGN_CENTER);
	    bigTitle.add(title);
	    try {
			document.add(bigTitle);
		} catch (DocumentException e) {
			e.printStackTrace();
			System.err.println("Error during the add of the preface. ");
		}
	    document.newPage();
	    Paragraph introTitle = new Paragraph("Introduction\n", chapterTitleFont);
	    Paragraph introductionParagraph = new Paragraph(introduction, bold);
	    introductionParagraph.setAlignment(Element.ALIGN_JUSTIFIED);
	    //introductionParagraph.setFirstLineIndent(25);

	    try {
			document.add(introTitle);	    
			document.add(introductionParagraph);;
		} catch (DocumentException e) {
			e.printStackTrace();
			System.err.println("Error during the add of the preface. ");
		}
	    document.newPage();
	}

	/* (non-Javadoc)
	 * @see parsebionet.utils.report.Printer#newChapter(java.lang.String)
	 */
	public void newChapter(String chapterTitle){
	    if (!(lastChapter==null)){
	    	lastChapter.setComplete(true);
		    if (!(lastSection==null)){
		    	lastSection.setComplete(true);
		    	if (!(lastSubsection==null)){
		    		lastSubsection.setComplete(true);
		    	}
		    }
	    }
		addLastChapter();
	    Paragraph paragraph = new Paragraph(chapterTitle, chapterTitleFont);
	    paragraph.setSpacingAfter(10);
	    chapterNumber += 1;
	    sectionNumber = 0;
	    subsectionNumber = 0;
	    lastChapter = new Chapter(new Paragraph(paragraph), chapterNumber);
	    lastChapter.setComplete(false);
	}
	
	/* (non-Javadoc)
	 * @see parsebionet.utils.report.Printer#newSection(java.lang.String)
	 */
	public void newSection (String sectionTitle){
	    if (!(lastSection==null)){
	    	lastSection.setComplete(true);
	    	if (!(lastSubsection==null)){
	    		lastSubsection.setComplete(true);
	    	}
	    }
		Paragraph paragraph = new Paragraph(sectionTitle, sectionTitleFont);
		paragraph.setSpacingAfter(10);
		sectionNumber += 1;
	    subsectionNumber = 0;	    
	    lastSection = lastChapter.addSection(paragraph);//, sectionNumber);
	    lastSection.setComplete(false);
	}
	
	/* (non-Javadoc)
	 * @see parsebionet.utils.report.Printer#newSubsection(java.lang.String)
	 */
	public void newSubsection (String subsectionTitle){
		if (!(lastSubsection==null)){
    		lastSubsection.setComplete(true);
    	}
		Paragraph paragraph = new Paragraph(subsectionTitle, subsectionTitleFont);
		paragraph.setSpacingAfter(10);
		subsectionNumber += 1;
		int numberDepth = 3;
	    lastSubsection = lastSection.addSection(paragraph, numberDepth);
	    lastSubsection.setComplete(false);
	}
	
	/**
	 * Adds the element to document.
	 *
	 * @param element the element
	 */
	private void addElementToDocument(Element element){
		if (subsectionNumber==0){
			if(sectionNumber==0){
				if(chapterNumber==0){
					try {
						document.add(element);
					} catch (DocumentException e) {
						System.err.println("Error while adding "+element.toString()+" to the document");
						e.printStackTrace();
					}
				}else{lastChapter.add(element);}
			}else{lastSection.add(element);}
		}else{lastSubsection.add(element);}
	}
	
	/**
	 * Adds the paragraph.
	 *
	 * @param newParagraph the new paragraph
	 */
	public void addParagraph (String newParagraph){
		Paragraph paragraph = new Paragraph("\t"+newParagraph+"\n", normalFont);
		paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
		//paragraph.setFirstLineIndent(25);
		paragraph.setSpacingAfter(5);
		addElementToDocument(paragraph);
	}

	/*public void addParagraphToLastSubsection(String newParagraph){
		Paragraph paragraph = new Paragraph("\t"+newParagraph+"\n", normalFont);
		paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
		//paragraph.setFirstLineIndent(25);
		paragraph.setSpacingAfter(6);
		lastSubsection.add(paragraph);
	}*/
		
	/**
	 * Adds the paragraph to last section.
	 *
	 * @param newParagraph the new paragraph
	 */
	public void addParagraphToLastSection(String newParagraph){
		Paragraph paragraph = new Paragraph("\t"+newParagraph+"\n", normalFont);
		paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
		//paragraph.setFirstLineIndent(25);
		paragraph.setSpacingAfter(5);
		lastSection.add(paragraph);
	}
	
	/**
	 * Adds the paragraph to last chapter.
	 *
	 * @param newParagraph the new paragraph
	 */
	public void addParagraphToLastChapter(String newParagraph){
		Paragraph paragraph = new Paragraph("\t"+newParagraph+"\n", normalFont);
		paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
		//paragraph.setFirstLineIndent(25);
		paragraph.setSpacingAfter(5);
		lastChapter.add(paragraph);
	}
	
	/*public void newParagraphTitle(String title){
		Paragraph paragraph = new Paragraph("\t"+title+"\n", bold);
		paragraph.setFirstLineIndent(20);
		paragraph.setAlignment(Element.ALIGN_LEFT);
	    if (sectionNumber == 0) {
	    	lastChapter.add(paragraph);
	    }
	    else {
	    	lastSection.add(paragraph);		    
	    }
	}*/
	
	/**
	 * Adds the last chapter.
	 */
	private void addLastChapter(){
		if (chapterNumber>0){
			try {
				document.add(lastChapter);
			    document.newPage();
			} catch (DocumentException e) {
				e.printStackTrace();
				System.err.println("Error during the add of the chapter "+chapterNumber);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see parsebionet.utils.report.Printer#addImage(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void addImage(String fileAdr, String legendString, String titleString){
		float scale = (float) 75;
		addImage(fileAdr, legendString, titleString, scale);
	}
	
	/**
	 * Adds the image.
	 *
	 * @param fileAdr the file adr
	 * @param legendString the legend string
	 * @param titleString the title string
	 * @param scale the scale
	 */
	public void addImage(String fileAdr, String legendString, String titleString, float scale){
		PdfPTable table = new PdfPTable(1);
		table.setWidthPercentage(100);
		table.setSpacingBefore(10);
		Image image = null;
		try {
			image = Image.getInstance(fileAdr);
		} catch (BadElementException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	    image.setCompressionLevel(2);//between 0 (best speed) and 9 (best compression)
	    image.scalePercent(scale);

		PdfPCell imageCell = new PdfPCell(image);
		imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		imageCell.setBorderWidth(0f);
		table.addCell(imageCell);
		if (legendString!=null || titleString!=null){
			Paragraph legend = new Paragraph();
			if (titleString!=null){
				figureNumber += 1;
				Chunk titleChunk = new Chunk("Fig "+figureNumber+". "+titleString+"\n", bold);				
				titleChunk.setUnderline(0.1f, -2f);
				legend.add(titleChunk);
			}
			if (legendString!=null){
				Chunk legendChunk = new Chunk(legendString, italic);
				legend.add(legendChunk);
			}
			PdfPCell legendCell = new PdfPCell(legend);
			legendCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			legendCell.setBorderWidth(0f);
			table.addCell(legendCell);
		}
		
		addElementToDocument(table);
	}
	
	/* (non-Javadoc)
	 * @see parsebionet.utils.report.Printer#addTable(java.lang.String, java.util.Vector, java.util.Vector)
	 */
	public void addTable(String title, Vector <String> heading, Vector <Vector<String>> data){
		addTable(title, heading, data, 100);
	}
	
	/**
	 * Adds the table.
	 *
	 * @param title the title
	 * @param heading the heading
	 * @param data the data
	 * @param widthPercent the width percent
	 */
	public void addTable(String title, Vector <String> heading, Vector <Vector<String>> data, int widthPercent){
		if (data.size()==0){
			Vector<String>line = new Vector<String>();
			for (String element:heading){
				line.add("-");
			}
			data.add(line);
		}
		int nb_col = data.elementAt(0).size();
		float[] columnWidths = new  float[nb_col];
		columnWidths[0]=2f;
		for (int i=1; i<nb_col; i++){
			columnWidths[i]=1f;
		}
		addTable(title,heading,data,widthPercent,columnWidths);
	}
	
	public void addTable(String title, Vector <String> heading, Vector <Vector<String>> data, int widthPercent, float[] columnWidthsRatio){
		int nb_col = data.elementAt(0).size();
		PdfPTable table = new PdfPTable(nb_col);
		table.setSpacingBefore(10f);
		table.setSpacingAfter(10f);
		table.setWidthPercentage(widthPercent);
		
        try {
			table.setWidths(columnWidthsRatio);
		} catch (DocumentException e) {
			System.err.println("Error with the widths of the table columns");
			e.printStackTrace();
		}		
		if (!title.equals("")){
			PdfPCell cell = new PdfPCell(new Phrase(title,tableTitleFont));
	    	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    	cell.setBackgroundColor(Gray);
	        cell.setColspan(nb_col);
	        table.addCell(cell);
		}
		if (!(heading==null)){
	        PdfPCell cellHeader;
		    for (int i=0;i<heading.size();i+=1){
		    	cellHeader = new PdfPCell(new Phrase(heading.get(i), bold));
		    	cellHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
		    	table.addCell(cellHeader);
		    }	    
		    table.setHeaderRows(1);	    
		}
		for (int i=0;i<data.size();i+=1){
	    	Vector <String> dataLine = data.get(i);
	    	for (int j=0;j<dataLine.size();j+=1){
	    		PdfPCell dataCell;
	    		String datacell = dataLine.get(j);
	    		if (datacell!=null && datacell!="-"){
	    			if (datacell.contains("\\*") || datacell.contains("\\°")){
	    				dataCell = new PdfPCell(getStars(datacell));
	    	    		dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    			}
	    			else {
	    				dataCell = new PdfPCell(new Phrase(datacell, normalFont));
	    	    		dataCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    			}
	    		}
	    		else{
	    			dataCell = new PdfPCell(new Phrase("-", normalFont));
		    		dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    		}
	    		if (i%2 == 0){
	    			dataCell.setBackgroundColor(lightGray);
	    		}
	    		if (j == 0){
	    			dataCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    		}
	    		table.addCell(dataCell);
	    	}
	    }
		addElementToDocument(table);
	}
	
	/* (non-Javadoc)
	 * @see parsebionet.utils.report.Printer#addList(java.util.Vector)
	 */
	public void addList(Vector<Object> items) {
		if (sectionNumber == 0) {
			addListRecursive(items,(Object)lastChapter, 1);
	    }
	    else {
	    	addListRecursive(items,(Object)lastSection, 1);		    
	    }
	}
	
	/**
	 * Adds the list recursive.
	 *
	 * @param items the items
	 * @param parentElement the parent element
	 * @param level the level
	 */
	private void addListRecursive(Vector <?> items, Object parentElement, int level) {
		Boolean numbered = false;
		Boolean lettered = false;
		float symbolIndent = 10;
	    List list = new List(numbered,lettered,symbolIndent);
		//list.setIndentationLeft(20*level);
		list.setIndentationLeft(20);
		for (int i=0;i<items.size();i+=1){
			Object item = items.get(i);
			if (item.getClass().getName().equals("java.lang.String")){
				String itemString = item.toString();
				list.add(new ListItem(itemString, normalFont));
			}
			else if (item.getClass().getName().equals("java.util.Vector")){		
				addListRecursive((Vector<?>)item, list, level+1);
			}
			else{
				System.err.println("Error in list with: "+item.getClass().getName());
			}
	    }
		if (parentElement.getClass().getName().equals("com.itextpdf.text.List")){
			List L = (List)parentElement;
			L.add(list);
		}
		else {
			addElementToDocument(list);
		}
	}
	
	/**
	 * Gets the stars.
	 *
	 * @param string the string
	 * @return the stars
	 */
	private Phrase getStars(String string){
		Phrase phrase = new Phrase("");
		Phrase stars = new Phrase("");
		if (string.contains("\\*\\*\\*")){
			stars.add(fullStar);
			stars.add(fullStar);
			stars.add(fullStar);
		}
		else if (string.contains("\\*\\*\\°")){
			stars.add(fullStar);
			stars.add(fullStar);
			stars.add(emptyStar);
		}
		else if (string.contains("\\*\\°\\°")){
			stars.add(fullStar);
			stars.add(emptyStar);
			stars.add(emptyStar);
		}
		else if (string.contains("\\°\\°\\°")){
			stars.add(emptyStar);
			stars.add(emptyStar);
			stars.add(emptyStar);
		}
		else{
			System.err.println("Error with string: "+string);
		}
		phrase.add(string.substring(0, string.length()-6) );
		phrase.add(stars);
		return phrase; 
	}
	
	/**
	 * The Class MyFooter.
	 */
	class MyFooter extends PdfPageEventHelper {
        
        /** The header font. */
        Font headerFont = new Font(Font.FontFamily.UNDEFINED, 10, Font.ITALIC, BaseColor.GRAY);
        
        /** The footer font. */
        Font footerFont = new Font(Font.FontFamily.UNDEFINED, 12, Font.ITALIC);
        
        /* (non-Javadoc)
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onEndPage(com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
         */
        public void onEndPage(PdfWriter writer, Document document) {
        	if (writer.getPageNumber() > 1){        	
	            PdfContentByte cb = writer.getDirectContent();
	            Phrase header = new Phrase(headerTitle, headerFont);
	            Phrase footer = new Phrase("- "+Integer.toString(writer.getPageNumber())+" -", footerFont);
	            float headerX = (document.right() - document.left()) + document.leftMargin();//(document.right() - document.left()) / 2 + document.leftMargin();
	            float headerY = document.top() + 10;
	            int rotation = 0;
	            float footerX = (document.right() - document.left()) / 2 + document.leftMargin();
	            float footerY = document.bottom() - 10;
	            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, header, headerX, headerY,rotation);
	            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer, footerX, footerY, rotation);
        	}
        }
    }	
}
