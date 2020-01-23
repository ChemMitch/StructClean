/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thinkscience.demo.utilities;

import com.thinkscience.demo.structurecleaner.objects.ifc.FieldType;
import com.thinkscience.demo.structurecleaner.objects.ifc.IColumnMetadata;
import com.thinkscience.demo.structurecleaner.objects.ifc.IFieldDataList;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;

/**
 * This class provides a set of data-formatting functions used throughout the
 * application.
 *
 * @author Mitch
 */
public class HttpUtilities
{

	public void processError(String errorMessage, HttpServletRequest req,
					HttpServletResponse resp, ServletContext context)
	{
		System.out.println("HttpUtilities.processError called with errorMessage: "
						+ errorMessage);
		System.err.println("HttpUtilities.processError called with errorMessage: "
						+ errorMessage);
		req.setAttribute("ErrorMessage", errorMessage);

		RequestDispatcher dispatcher = context.getRequestDispatcher("/errorpage.jsp");

		try
		{
			dispatcher.forward(req, resp);
			//resp.flushBuffer();
		} catch (ServletException ex)
		{
			System.err.println("Error processing error: " + ex.getMessage());
		} catch (IOException iox)
		{
			System.err.println("Error processing error: " + iox.getMessage());
		}
	}

	/*
	 * Facile way to invoke a JSP
	 */
	public void forwardEasy(String nextPage, HttpServletRequest req,
					HttpServletResponse resp, ServletContext context)
	{
		RequestDispatcher dispatcher = context.getRequestDispatcher(nextPage);

		try
		{
			dispatcher.forward(req, resp);
			//resp.flushBuffer();
		} catch (ServletException ex)
		{
			System.err.println("Error processing error: " + ex.getMessage());
		} catch (IOException iox)
		{
			System.err.println("Error processing error: " + iox.getMessage());
		} catch (Exception ex)
		{
			System.err.println("Error processing error: " + ex.getMessage());
		}

	}

	/*
	 * Loop through the records in a ResultSet with fields ID,
	 */
	public String getHTMLTableAsStringFromRS(ResultSet RS, int startRecord,
					int maxRecord, boolean includeSelectionColumn,
					Double structureBoxWidth, Double structureBoxHeight,
					ArrayList<Integer> structureNumbersDisplayed)
	{
		StringBuilder sb = new StringBuilder();
		String dataCellStyleName = "dataCell";
		String chemNameCellStyleName = "chemNameCell";

		sb.append("<Table class=\"structTable\">");
		sb.append("<TR>");
		if (includeSelectionColumn)
		{
			sb.append("<TH>Select</TH>");
		}
		sb.append("<TH>ID</TH>");
		sb.append("<TH>Name</TH>");
		sb.append("<TH>Mol Wt.</TH>");
		sb.append("<TH>Mol Fmla.</TH>");
		sb.append("<TH>Original Structure</TH>");
		sb.append("<TH>Reoriented Structure</TH>");
		sb.append("</TR>");

		int currRec;
		//ArrayList<Integer> structureNumbersDisplayed = new ArrayList <Integer>();

		String structureDisplayClass="resultsStructureBox";
		try
		{
			if (!RS.absolute(startRecord))
			{
				sb = new StringBuilder();
				sb.append("<H3 style=\"color:#ff0000\">No records found for your query</H3>");
				sb.append("<br/>");
				return (sb.toString());
			}
			currRec = startRecord;
			while (RS.next() && currRec <= maxRecord)
			{
				String recordID = RS.getString("id");
				sb.append("<TR>");

				if (includeSelectionColumn)
				{
					sb.append("<TD>");
					String checkBoxName = "includeID_" + recordID;
					sb.append("<Input type='checkbox' name='");
					sb.append(checkBoxName);
					sb.append("' value='");
					sb.append(recordID);
					sb.append("' checked='true' />");
					sb.append("</TD>");
				}

				sb.append("<TD class=\"");
				sb.append(dataCellStyleName);
				sb.append("\">\n");
				sb.append(RS.getString("id"));
				sb.append("</TD>");
				sb.append("<TD class=\"");
				sb.append(chemNameCellStyleName);
				sb.append("\">");
				sb.append(RS.getString("molname"));
				sb.append("</TD>");
				sb.append("<TD class=\"");
				sb.append(dataCellStyleName);
				sb.append("\">");
				sb.append(RS.getDouble(3));
				sb.append("</TD>");
				sb.append("<TD class=\"");
				sb.append(dataCellStyleName);
				sb.append("\">");
				sb.append(RS.getString(4));
				sb.append("</TD>");
				sb.append("<TD class=\"");
				sb.append(dataCellStyleName);
				sb.append("\">");
				sb.append(buildSingleStructureDisplay(RS.getString(5),
								structureBoxWidth, structureBoxHeight, currRec, structureDisplayClass ));
				structureNumbersDisplayed.add(currRec);
				sb.append("</TD>\n");

				sb.append("<TD class=\"");
				sb.append(dataCellStyleName);
				sb.append("\">");
				int structNum =100*currRec+1;
				sb.append(buildSingleStructureDisplay(RS.getString(6),
								structureBoxWidth, structureBoxHeight, structNum,
								structureDisplayClass));
				structureNumbersDisplayed.add(structNum);
				sb.append("</TD>");

				sb.append("</TR>");
				currRec++;
			}
		} catch (SQLException ex)
		{
			Logger.getLogger("getHTMLTableAsStringFromRS").log(Level.SEVERE, null, ex);
		} catch (java.lang.NullPointerException npe)
		{
			Logger.getLogger("getHTMLTableAsStringFromRS").log(Level.SEVERE, null, npe);
			return "Error!";
		}
		sb.append("</Table>");
		return sb.toString();
	}

	/*
	 * Loop through the records in a ResultSet with fields ID,
	 */
	public String getHTMLTableAsStringFromRS(ResultSet RS, int startRecord,
					int maxRecord, boolean includeSelectionColumn,
					ArrayList<IColumnMetadata> metadata)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("<Table class=\"structTable\">");
		sb.append("<TR>");
		if (includeSelectionColumn)
		{
			sb.append("<TH>Select</TH>");
		}
		String structureDisplayClass = "resultsStructureBox";
		String idFieldName = null;
		//loop over all fields, creating header items and looking for an ID
		for (IColumnMetadata mdItem : metadata)
		{
			if (mdItem.isDisplayColumn())
			{
				sb.append("<TH>");
				sb.append(mdItem.getColumnLabel());
				sb.append("</TH>");

				if (mdItem.isIsID())
				{
					idFieldName = mdItem.getColumnName();
				}
			}
		}
		sb.append("</TR>");


		int currRec;
		try
		{
			if (!RS.absolute(startRecord))
			{
				sb = new StringBuilder();
				sb.append("<H3 style=\"color:#ff0000\">No records found for your query</H3>");
				sb.append("<br/>");
				return (sb.toString());
			}
			currRec = startRecord;
			while (RS.next() && currRec <= maxRecord)
			{
				String recordID = RS.getString(idFieldName);
				sb.append("<TR>");

				if (includeSelectionColumn)
				{
					sb.append("<TD>");
					String checkBoxName = "includeID_" + recordID;
					sb.append("<Input type='checkbox' name='");
					sb.append(checkBoxName);
					sb.append("' value='");
					sb.append(recordID);
					sb.append("' checked='true' />");
					sb.append("</TD>");
				}

				//loop through the fields
				for (IColumnMetadata mdItem : metadata)
				{
					if (mdItem.isDisplayColumn())
					{
						//The table cell tag, possibly including a CSS class
						sb.append("<TD");
						if (mdItem.getColumnCSSClass() != null
										&& mdItem.getColumnCSSClass().length() > 0)
						{
							sb.append(" class=\"");
							sb.append(mdItem.getColumnCSSClass());
							sb.append("\"");
						}
						sb.append(">\n");

						String columnValue = null;
						if (mdItem.getColumnType() == FieldType.CLOB
										|| mdItem.getColumnType() == FieldType.STRING)
						{
							columnValue = RS.getString(mdItem.getColumnName());
						} else if (mdItem.getColumnType() == FieldType.DOUBLE)
						{
							double tempValue = RS.getDouble(mdItem.getColumnName());
							columnValue = String.format(mdItem.getColumnFormat(), tempValue);
						} else if (mdItem.getColumnType() == FieldType.FLOAT)
						{
							float tempValue = RS.getFloat(mdItem.getColumnName());
							columnValue = String.format(mdItem.getColumnFormat(), tempValue);
						} else if (mdItem.getColumnType() == FieldType.INT)
						{
							int tempValue = RS.getInt(mdItem.getColumnName());
							columnValue = String.format(mdItem.getColumnFormat(), tempValue);
						} else if (mdItem.getColumnType() == FieldType.STRUCTURE_CHEM)
						{
							columnValue = buildSingleStructureDisplay(RS.getString(mdItem.getColumnName()), 
									mdItem.getStructureBoxWidth(), mdItem.getStructureBoxHeight(), currRec,
											structureDisplayClass);
						}
						sb.append(columnValue);
						sb.append("</TD>");
					}
				}

				sb.append("</TR>");
				currRec++;
			}
		} catch (SQLException ex)
		{
			Logger.getLogger("getHTMLTableAsStringFromRS").log(Level.SEVERE, null, ex);
		} catch (java.lang.NullPointerException npe)
		{
			Logger.getLogger("getHTMLTableAsStringFromRS").log(Level.SEVERE, null, npe);
			return "Error!";
		}
		sb.append("</Table>");
		return sb.toString();
	}


	/*
	 * Loop through the records in a ResultSet with fields ID,
	 */
	/**
	 *
	 * @param data - cache of data from the DB
	 * @param startRecord 1-based record on which to start
	 * @param maxRecord last record number to display
	 * @param includeSelectionColumn true means create a checkbox for IDs
	 * @param metadata column descriptions
	 * @param selectedIDs List of IDs the user has already selected
	 * @param requireQuotes - an indication of whether field is text and values
	 * need to be quoted
	 * @param structureItemIds
	 * @return
	 */
	public String getHTMLTableAsStringFromArrayList(ArrayList<IFieldDataList> data,
					int startRecord, int maxRecord, boolean includeSelectionColumn,
					ArrayList<IColumnMetadata> metadata,
					ArrayList<String> selectedIDs,
					Boolean requireQuotes,
					ArrayList<Integer> structureItemIds)
	{
		int recNum = 0;
		int structureNumber =0;
		String structureDisplayClass ="structureResultsBox";
		StringBuilder sb = new StringBuilder();

		sb.append("<Table class=\"structTable\">");
		sb.append("<TR>");
		if (includeSelectionColumn)
		{
			sb.append("<TH>Select</TH>");
		}

		if (data.isEmpty())
		{
			sb = new StringBuilder();
			sb.append("<H3 style=\"color:#ff0000\">Sorry, No records found for your query. </H3>");
			sb.append("<br/>");
			return (sb.toString());
		}

		String idFieldName = null;
		//loop over all fields, creating header items and looking for an ID
		for (IColumnMetadata mdItem : metadata)
		{
			if (mdItem.isDisplayColumn())
			{
				sb.append("<TH>");
				sb.append(mdItem.getColumnLabel());
				sb.append("</TH>");

				if (mdItem.isIsID())
				{
					idFieldName = mdItem.getColumnName();
				}
			}
		}
		sb.append("</TR>");


		int currRec = 0;
		try
		{
			if (data.size() < startRecord)
			{
				sb = new StringBuilder();
				sb.append("<H3 style=\"color:#ff0000\">Record ");
				sb.append(startRecord);
				sb.append(" was not found for your query</H3>");
				sb.append("<br/>");
				return (sb.toString());
			}
			//currRec = startRecord;
			for (int rec = startRecord; rec <= maxRecord; rec++)
			{
				recNum++;
				IFieldDataList currRowData = data.get(rec);
				String recordID = null;
				Object idValue = data.get(rec).getDataForField(idFieldName);
				if (idValue != null)
				{
					recordID = idValue.toString();
				}

				sb.append("<TR>");

				if (includeSelectionColumn)
				{
					sb.append("<TD>");
					String checkBoxName = "includeID_" + recordID;
					sb.append("<Input type='checkbox' name='");
					sb.append(checkBoxName);
					sb.append("' value='");
					sb.append(recordID);

					sb.append("' ");
					if (selectedIDs == null || !selectedIDs.contains(prepareID(recordID, requireQuotes)))
					{
						//sb.append("checked='false' />");
					} else
					{
						sb.append("checked='checked' />");
					}

					sb.append("</TD>");
				}

				//loop through the fields
				for (IColumnMetadata mdItem : metadata)
				{
					if (mdItem.isDisplayColumn())
					{
						//The table cell tag, possibly including a CSS class
						sb.append("<TD");
						if (mdItem.getColumnCSSClass() != null
										&& mdItem.getColumnCSSClass().length() > 0)
						{
							sb.append(" class=\"");
							sb.append(mdItem.getColumnCSSClass());
							sb.append("\"");
						}
						sb.append(">\n");

						String columnValue = null;
						if (mdItem.getColumnType() == FieldType.CLOB
										|| mdItem.getColumnType() == FieldType.STRING)
						{
							columnValue = currRowData.getStringDataForField(mdItem.getColumnName());
						} else if (mdItem.getColumnType() == FieldType.DOUBLE)
						{
							double tempValue = currRowData.getDoubleDataForField(mdItem.getColumnName());
							columnValue = String.format(mdItem.getColumnFormat(), tempValue);
						} else if (mdItem.getColumnType() == FieldType.FLOAT)
						{
							float tempValue = currRowData.getFloatDataForField(mdItem.getColumnName());
							columnValue = String.format(mdItem.getColumnFormat(), tempValue);
						} else if (mdItem.getColumnType() == FieldType.INT)
						{
							int tempValue = currRowData.getIntegerDataForField(mdItem.getColumnName());
							columnValue = String.format(mdItem.getColumnFormat(), tempValue);
						} else if (mdItem.getColumnType() == FieldType.STRUCTURE_CHEM)
						{
							currRec++;
							columnValue = buildSingleStructureDisplay(
											currRowData.getStringDataForField(mdItem.getColumnName()),
											new Double(mdItem.getStructureBoxWidth()),
											new Double(mdItem.getStructureBoxHeight()),++structureNumber,
											structureDisplayClass);
							structureItemIds.add(structureNumber);
						}
						sb.append(columnValue);
						sb.append("</TD>");
					}
				}

				sb.append("</TR>");
				//currRec++;
			}
		} catch (Exception ex)
		{
			Logger.getLogger("getHTMLTableAsStringFromArrayList").log(Level.SEVERE, null, ex);
			return "Error!";
		}
		sb.append("</Table>");
		return sb.toString();
	}

	//Create the DOM elements that Marvin JS will use
	// to construct and display a depiction of the structure
	// the width and height parameters correspond to the border 
	// around the structure image, NOT the size of the image itself
	public String buildSingleStructureDisplay(String molString,
					Double appletWidth, Double appletHeight, int uniqueNumber,
					String className)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("<div id='molContainer");
		sb.append(uniqueNumber);
		sb.append("' style='width: ");
		sb.append(appletWidth);
		sb.append("px; height: ");
		sb.append(appletHeight);
		sb.append("px;border:1px solid gray' ");
		sb.append(" class='");
		sb.append(className);
		sb.append("' >\n<img id='mol");
		sb.append( uniqueNumber);
		sb.append("' class='bordered' />\n");
		sb.append("<input type='hidden' id='molContents");
		sb.append( uniqueNumber);
		sb.append("' value='");
		sb.append(molString);
		sb.append("' />");
		sb.append("</div>");

		return sb.toString();
	}

	public static String buildMarvinJsForItems( ArrayList<Integer> itemNumbers, 
					int StructureViewWidth, int StructureViewHeight)
	{
		StringBuilder preRet = new StringBuilder();
		preRet.append("<script type='text/javascript'>\n");
		preRet.append("	//Javascript function that loops through all IDs to create the display\n");
		preRet.append("	function displayAllViaMarvin()\n");
		preRet.append("	{\n");
		preRet.append("		var structureDisplaySettings = {\n");
		preRet.append("			'carbonLabelVisible' : false,\n");
		preRet.append("						'cpkColoring' : true,\n");
		preRet.append("						'implicitHydrogen' : 'TERMINAL_AND_HETERO',\n");
		preRet.append("						'width' : ");
		preRet.append(StructureViewWidth);
		preRet.append(",\n");
		preRet.append("						'height' : ");
		preRet.append( StructureViewHeight);
		preRet.append("\n");
		preRet.append("					};\n");

		preRet.append("		var idString = '");
		preRet.append( StringUtils.join(itemNumbers,","));
		preRet.append("';\n");
		preRet.append("		var ids=idString.split(',');\n");
		preRet.append("		$.each(ids,		function (idIndex, id){\n");
		preRet.append("				var molDataItemId = 'molContents' + id;\n");
		preRet.append("				var structureItemId = '#mol' + id;\n");
		preRet.append("				structureContainerId = '#molContainer' + id;\n");
		preRet.append("				var structureData = document.getElementById(molDataItemId).value;// $(molDataItemId).value;\n");
		preRet.append("				var dataUrl;\n");
		preRet.append("				try\n");
		preRet.append("				{\n");
		preRet.append("						globalMarvin.exportMolToImageDataUri(structureData,\"image/png\",structureDisplaySettings).then(function(s){\n");
		preRet.append("						console.log(s);\n");
		preRet.append("						console.log('structureItemId: ' + structureItemId);\n");
		preRet.append("						$(structureItemId).attr(\"src\", s);\n");
		preRet.append("					});");
		preRet.append("				}\n");
		preRet.append("				catch(ex)\n");
		preRet.append("				{\n");
		preRet.append("							dataUrl= globalMarvin.exportMolToImageDataUri(caffeineSource,\"image/png\");\n");
		preRet.append("				}\n");
		preRet.append("				//var structureBox = $(structureItemId);\n");
		preRet.append("				//structureBox.value =dataUrl;\n");
		preRet.append("				$(structureItemId).attr(\"src\", dataUrl);\n");
		preRet.append("				$(structureContainerId).css(\"display\", \"inherit\");\n");
		preRet.append("			});\n");
		preRet.append("		\n");
		preRet.append("	};\n");
		preRet.append("</script>"); 
		return preRet.toString();
		
	}
	
		public static String buildMarvinJsForItems_prev( ArrayList<Integer> itemNumbers, 
					int StructureViewWidth, int StructureViewHeight)
	{
		StringBuilder preRet = new StringBuilder();
		preRet.append("<script type='text/javascript'>\n");
		preRet.append("	//Javascript function that loops through all IDs to create the display\n");
		preRet.append("	function displayAllViaMarvin()\n");
		preRet.append("	{\n");
		preRet.append("		var structureDisplaySettings = {\n");
		preRet.append("			'carbonLabelVisible' : false,\n");
		preRet.append("						'cpkColoring' : true,\n");
		preRet.append("						'implicitHydrogen' : 'TERMINAL_AND_HETERO',\n");
		preRet.append("						'width' : ");
		preRet.append(StructureViewWidth);
		preRet.append(",\n");
		preRet.append("						'height' : ");
		preRet.append( StructureViewHeight);
		preRet.append("\n");
		preRet.append("					};\n");

		preRet.append("		var idString = '");
		preRet.append( StringUtils.join(itemNumbers,","));
		preRet.append("';\n");
		preRet.append("		var ids=idString.split(',');\n");
		preRet.append("		$.each(ids,		function (idIndex, id){\n");
		preRet.append("				var molDataItemId = 'molContents' + id;\n");
		preRet.append("				var structureItemId = '#mol' + id;\n");
		preRet.append("				structureContainerId = '#molContainer' + id;\n");
		preRet.append("				var structureData = document.getElementById(molDataItemId).value;// $(molDataItemId).value;\n");
		preRet.append("				var dataUrl;\n");
		preRet.append("				try\n");
		preRet.append("				{\n");
		preRet.append("							dataUrl= globalMarvin.ImageExporter.molToDataUrl(structureData,\"image/png\",structureDisplaySettings);\n");
		preRet.append("				}\n");
		preRet.append("				catch(ex)\n");
		preRet.append("				{\n");
		preRet.append("							dataUrl= globalMarvin.ImageExporter.molToDataUrl(caffeineSource,\"image/png\");\n");
		preRet.append("				}\n");
		preRet.append("				//var structureBox = $(structureItemId);\n");
		preRet.append("				//structureBox.value =dataUrl;\n");
		preRet.append("				$(structureItemId).attr(\"src\", dataUrl);\n");
		preRet.append("				$(structureContainerId).css(\"display\", \"inherit\");\n");
		preRet.append("			});\n");
		preRet.append("		\n");
		preRet.append("	};\n");
		preRet.append("</script>"); 
		return preRet.toString();
		
	}

	/**
	 * Find all ID values in the current request that have the supplied match
	 * pattern in their name.
	 *
	 *
	 * @param req - Request with data from a user
	 * @param paramStart - beginning data
	 * @param wrapValuesInQuotes - indication that a field is text and values
	 * require quotes
	 * @param deselectedIDs - IDs to remove from selection
	 */
	public ArrayList<String> getSelectedIDsFromRequest(HttpServletRequest req,
					String paramStart,
					boolean wrapValuesInQuotes,
					ArrayList<String> deselectedIDs)
	{
		ArrayList<String> ret = new ArrayList<String>();

		Enumeration paramNames = req.getParameterNames();
		while (paramNames.hasMoreElements())
		{
			String paramName = (String) paramNames.nextElement();
			if (paramName.startsWith(paramStart))
			{
				String[] paramValues = req.getParameterValues(paramName);
				if (paramValues.length == 1)
				{
					String paramValue = paramValues[0];
					if (paramValue.length() > 0)
					{
						String tempID = paramValue;
						if (wrapValuesInQuotes)
						{
							tempID = "'" + tempID + "'";
						}
						ret.add(tempID);
					}
				} else
				{
					String excludedID = paramName.substring(paramStart.length());
					deselectedIDs.add(excludedID);
					System.out.println("Perceived deslected ID " + excludedID);
				}
			}
		}
		return ret;
	}

	public static String[] getSQLOperator(String queryOperatorInput, String queryValue)
	{
		//figure out the correct SQL operator
		String ret[] = new String[2];
		if (queryOperatorInput.equalsIgnoreCase("="))
		{
			ret[0] = queryOperatorInput;
			ret[1] = queryValue;
		} else if (queryOperatorInput.equalsIgnoreCase("starts"))
		{
			ret[0] = "like";
			ret[1] = queryValue + "%";
		} else if (queryOperatorInput.equalsIgnoreCase("contains"))
		{
			ret[0] = "like";
			ret[1] = "%" + queryValue + "%";
		}
		return ret;
	}

	/*
	 * Delete the session attributes that get set while processing a list so
	 * that next search/processing starts fresh
	 */
	public static void clearSessionAttributes(HttpSession currSession,
					boolean full)
	{
		currSession.removeAttribute("IDsToProcess");
		currSession.removeAttribute("CheshData");
		currSession.removeAttribute("TableMetadata");

		if (full)
		{
			currSession.removeAttribute("OrientationStructure");
			currSession.removeAttribute("FirstRecord");
			currSession.removeAttribute("LastMolWtMax");
			currSession.removeAttribute("LastLocatorValue");
			currSession.removeAttribute("LastCitationCount");
			currSession.removeAttribute("CleanBeforeOrienting");
		}
	}

	public static String formatMolfile( String inMol)
	{
		//make sure we have real data to work with
		if( inMol == null || inMol.length() == 0)
		{
			return "return null;";
		}
		//Get the individual lines of the molfile
		String[] molLines= inMol.split("\n");
		StringBuilder preRet = new StringBuilder();
		preRet.append("	var jsPreRet = new Array();");
		preRet.append("\n");
		
		//for each line, create a statement that adds the line to an array
		for( String line : molLines)
		{
			System.out.println("processing line " + line);
			preRet.append(" jsPreRet.push('");
			if( line != null && line.length() >0) 
			{
				preRet.append(line.substring(0, line.length()-1));
			}
			else
			{
				preRet.append(line);
			}
			preRet.append(" '); \n");
		}
		//make the Javascript join the array
		preRet.append(" return jsPreRet.join('\\n');");
		
		return preRet.toString();
	}
	private String prepareID(String initID, Boolean idRequiresQuotes)
	{
		String ret = initID;
		if (idRequiresQuotes)
		{
			ret = "'" + ret + "'";
		}
		return ret;
	}
}
