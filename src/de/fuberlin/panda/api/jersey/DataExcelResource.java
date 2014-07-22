package de.fuberlin.panda.api.jersey;

import java.io.File;
import java.util.GregorianCalendar;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.fuberlin.panda.api.APIHelper;
import de.fuberlin.panda.api.data.ExchangeXML;

/**
 * This is the data resource class for processing HTTP requests to excel files.
 * Example URL: http://localhost:8080/PANDA/rest/Data/xls/test/0/8/1 to 
 * request value of cell with sheet number 1, row number 9 and column B of test.xls
 * 
 * @author Christoph Schröder
 */
@Path( "/Data/{FileType:(xls|xlsx)}")
public class DataExcelResource {
    
    /**
     * 
     * @param FileType - xls or xlsx
     * @param FileName - filename of excel file
     * @param SheetNum - index of addressed sheet 
     * @param RowNum - index of row
     * @param ColNum - index of column
     * @return ExchangeXML - JAXB class for XML response
     * @throws WebApplicationException in case resource not found (HTTP 404) or semantic error while processing resource (HTTP 422)
     * @author Christoph Schröder
     */
	@GET
	@Path( "{FileName}/{SheetNum}/{RowNum}/{ColNum}")
	@Produces( MediaType.APPLICATION_XML )
	public ExchangeXML getSingleValue (
	        @PathParam("FileType") String FileType,
	        @PathParam("FileName") String FileName,
	        @PathParam("SheetNum") int SheetNum,
	        @PathParam("RowNum") int RowNum,
	        @PathParam("ColNum") int ColNum)
	                throws WebApplicationException
	{
		ExchangeXML xmlDoc = new ExchangeXML();
		Workbook wb;
		Sheet sheet;
		Row row;
		Cell cell;
		if (FileType.equals("xls")){
	        try {
	            NPOIFSFileSystem fs = new NPOIFSFileSystem(new File(APIHelper.getWebContentDirPath()+"testData/"+FileName+".xls"));
	            wb = new HSSFWorkbook(fs.getRoot(),false);
                sheet = wb.getSheetAt(SheetNum);
                row = sheet.getRow(RowNum);
                cell = row.getCell(ColNum);
                DataExcelResource.addCellValXML(cell,xmlDoc);
	            fs.close();
	        } catch (Exception e) {
	            throw new WebApplicationException(404);
	        }
		}
		else if (FileType.equals("xlsx")){
            try {
                OPCPackage pkg = OPCPackage.open(new File(APIHelper.getWebContentDirPath()+"testData/"+FileName+".xlsx"));
                wb = new XSSFWorkbook(pkg);
                sheet = wb.getSheetAt(SheetNum);
                row = sheet.getRow(RowNum);
                cell = row.getCell(ColNum);
                DataExcelResource.addCellValXML(cell,xmlDoc);
                pkg.close();
            } catch (Exception e) {
                throw new WebApplicationException(404);
            }
		}
		else{
		    throw new WebApplicationException(404);		    
		}
		xmlDoc.setBaseURI("/"+FileType+"/"+FileName+"/"+SheetNum+"/");
		return xmlDoc;
    }
	
	
	/**
	 * This method processes the value of a cell if existent according to different value types in excel 
	 * and adds it to the JAXB class object for XML response.
	 * 
	 * @param Cell - reference to requested cell
	 * @param ExchangeXML - reference to JAXB class object used for Response
	 * @throws WebApplicationException in case resource not found (HTTP 404) or semantic error while processing resource (HTTP 422)
	 * @author Christoph Schröder
	**/
	public static void addCellValXML(Cell cell,ExchangeXML xmlDoc) throws WebApplicationException {
	    ExchangeXML.Value cellVal = new ExchangeXML.Value();
	    Integer rowIndex = cell.getRowIndex();
	    Integer colIndex = cell.getColumnIndex();
	    cellVal.setSubURI(rowIndex.toString()+"/"+colIndex.toString());
	    
        switch (cell.getCellType()) {
        case Cell.CELL_TYPE_STRING:
            cellVal.setType("xs:string");
            cellVal.setValue(cell.getRichStringCellValue().getString());
            break;
        case Cell.CELL_TYPE_NUMERIC:
            if (DateUtil.isCellDateFormatted(cell)) {
                cellVal.setType("xs:date");
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTime(cell.getDateCellValue());
                try {
                    cellVal.setValue(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal).toString());
                } catch (DatatypeConfigurationException e) {
                    throw new WebApplicationException(422);
                }
            } 
            else {
                cellVal.setType("xs:double");
                cellVal.setValue(String.valueOf(cell.getNumericCellValue()));
            }
            break;
        case Cell.CELL_TYPE_BOOLEAN:
            cellVal.setType("xs:Boolean");
            cellVal.setValue(String.valueOf(cell.getBooleanCellValue()));
            break;
        case Cell.CELL_TYPE_FORMULA:
            cellVal.setType("xs:string");
            cellVal.setValue(cell.getCellFormula());
            break;
        default:
            throw new WebApplicationException(404);
        }
	    xmlDoc.getValue().add(cellVal);
	}

}
