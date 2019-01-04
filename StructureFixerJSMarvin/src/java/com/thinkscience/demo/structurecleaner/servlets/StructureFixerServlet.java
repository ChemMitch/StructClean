/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thinkscience.demo.structurecleaner.servlets;

import com.thinkscience.demo.structurecleaner.objects.ifc.*;
import com.thinkscience.demo.structurecleaner.objects.impl.*;

import com.thinkscience.demo.utilities.DatabaseUtilities;
import com.thinkscience.demo.utilities.HttpUtilities;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.RequestDispatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javax.servlet.ServletContext;

/**
 *
 * @author Mitch
 */
public class StructureFixerServlet extends HttpServlet
{

	private Integer SIZE_STRUCTURE_IN_TABLE = 150;
	private Integer SIZE_STRUCTURE_QUERY = 100;

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		try
		{
			HttpUtilities httpUtils = new HttpUtilities();
			IStructureProcessor structureProcessor = new StructureProcessor();

			String cleanFirstParam = request.getParameter("CleanBeforeOrient");
			System.out.println("Value of cleanFirstParam: " + cleanFirstParam);
			getServletContext().log("Value of cleanFirstParam: " + cleanFirstParam);

			String username = getServletContext().getInitParameter("dbusername");
			String password = getServletContext().getInitParameter("dbpassword");
			String servername = getServletContext().getInitParameter("servername");
			String dbid = getServletContext().getInitParameter("databaseSID");
			String dbPort = getServletContext().getInitParameter("dbport");
			HttpSession currSession = request.getSession();
			DatabaseUtilities dbUtilities = new DatabaseUtilities();
			Connection conn = (Connection) currSession.getAttribute("OracleConnection");
			try
			{
				if (conn == null || !conn.isClosed())
				{
					conn = dbUtilities.getOracleConnection(servername, dbid, dbPort, username,
							password);
				}
			} catch (SQLException ex)
			{
				String msg = "Error in StructureFixerServlet while checking for existing database connection";
				httpUtils.processError(msg, request, response, getServletContext());
			}

			String cheshireInitString = getServletContext().getInitParameter("CheshireInit");
			String cheshireLoadMolString = getServletContext().getInitParameter("CheshireLoadMol");
			String chemicalTableName = getServletContext().getInitParameter("ChemTable");
			String chemicalTableID = getServletContext().getInitParameter("StructureIdField");
			String superlistTable = getServletContext().getInitParameter("SuperListTableName");
			String superlistTableID = getServletContext().getInitParameter("SuperlistTableID");

			String cleanBeforeOrienting = "FALSE";
			if (cleanFirstParam != null && cleanFirstParam.toUpperCase().equals("ON"))
			{
				cleanBeforeOrienting = "TRUE";
				System.out.println("cleanBeforeOrienting is TRUE");
				getServletContext().log("cleanBeforeOrienting is TRUE");
			} else
			{
				System.out.println("cleanBeforeOrienting is FALSE");
				getServletContext().log("cleanBeforeOrienting is FALSE");
			}
			String structureAsMolfilestring = request.getParameter("OrientationStructureAsMolfileString");
			if (structureAsMolfilestring == null || structureAsMolfilestring.length() == 0)
			{
				String msg = "Error: a template structure is required";
				httpUtils.processError(msg, request, response, getServletContext());
				return;
			}

			structureAsMolfilestring = structureAsMolfilestring.replace("\r", "");
			
			String molfileName = java.util.UUID.randomUUID() + ".mol";
			//Use dbUtilities to save structure on the database server.
			String tempMolfilePath = dbUtilities.saveMolfile(structureAsMolfilestring, molfileName, conn);

			String cheshireLoadMolSQL = String.format(cheshireLoadMolString, tempMolfilePath);//structureAsMolfilestring

			String sqlToLoadCheshireFunctions = getServletContext().getInitParameter("ScriptLoadingSQL");

			Boolean structureIDRequiresQuotes = Boolean.parseBoolean(getServletContext().getInitParameter("StructureIDRequiresQuotes"));

			String maxRecordsPerPageString = getServletContext().getInitParameter("MaxRecordsPerPage");
			int maxRecordsPerPage = Integer.parseInt(maxRecordsPerPageString);
			//connect
			String tempHeight = getServletContext().getInitParameter("StructureViewHeight");
			String tempWidth = getServletContext().getInitParameter("StructureViewWidth");

			currSession.setAttribute("StructureViewHeight", tempHeight);
			currSession.setAttribute("StructureViewWidth", tempWidth);
			int structureHeight = Integer.parseInt(tempHeight);
			int structureWidth = Integer.parseInt(tempWidth);

			int arbitraryLargeNumber = 2013822;
			String formattedQueryStructure = httpUtils.buildSingleStructureDisplay(structureAsMolfilestring,
					new Double(structureWidth), new Double(structureHeight), arbitraryLargeNumber,
					"structureQueryBox");
			request.getSession().setAttribute("FormattedQueryStructure", formattedQueryStructure);

			ArrayList<String> selectedColumnNames = new ArrayList<>();
			ArrayList<String> queryTables = new ArrayList<>();
			ArrayList<IQueryJoinItem> queryJoinItems = new ArrayList<>();
			ArrayList<IQueryClause> queryClauses = new ArrayList<>();
			ArrayList<String> nonstandardQueryItems = new ArrayList<>();

			System.out.println("structure written to " + tempMolfilePath);
			getServletContext().log("structure written to " + tempMolfilePath);

			//build up the query
			setUpBasicQuery(queryTables, selectedColumnNames, nonstandardQueryItems,
					tempMolfilePath, getServletContext());

			//debug:
			System.out.println("After setupBasicQuery, we have a total of "
					+ queryTables.size() + " table and " + selectedColumnNames.size()
					+ " fields for the query.");
			getServletContext().log("After setupBasicQuery, we have a total of "
					+ queryTables.size() + " table and " + selectedColumnNames.size()
					+ " fields for the query.");
			//See if user entered a mol wt cutoff
			String molWtMaxString = request.getParameter("MolWtMax");
			Double molWtMax = 0.0d;
			try
			{
				molWtMax = Double.parseDouble(molWtMaxString);

				if (molWtMax > 0)
				{
					//User has entered a molecular weight cutoff
					queryClauses.add(new QueryClause(chemicalTableName,
							getServletContext().getInitParameter("molweightfieldname"),
							"<=", molWtMaxString, QueryClause.DEFAULT_CONJUNCTION));

				}
			} catch (NumberFormatException ex)
			{
				System.err.println("Invalid number for mol wt.");
				getServletContext().log("Invalid number for mol wt.");
				//No need to take further action
			}
			if (molWtMax > 0)
			{
				currSession.setAttribute("LastMolWtMax", molWtMaxString);
			} else
			{
				currSession.setAttribute("LastMolWtMax", "");
			}

			//See if user entered a Citation Count cutoff
			String citationCountString = request.getParameter("CitationCountMin");
			Integer citationCount = 0;
			try
			{
				citationCount = Integer.parseInt(citationCountString);
				if (citationCount > 0)
				{
					selectedColumnNames.add("[tbl_superlist].CITATIONCOUNT");
					//User has entered a citation count cutoff -- require a new clause in the query
					if (!queryTables.contains(superlistTable))
					{
						queryTables.add(superlistTable);
						queryJoinItems.add(new QueryJoinItem(chemicalTableName, chemicalTableID,
								superlistTable, superlistTableID));
					} else
					{
						getServletContext().log("query tables already contained superlist table.");
						System.out.println("query tables already contained superlist table.");
					}
					String citationCountFieldName = getServletContext().getInitParameter("CitationCountFieldName");

					//hard-code the operator for now
					queryClauses.add(new QueryClause(superlistTable,
							citationCountFieldName, ">=", citationCountString,
							QueryClause.DEFAULT_CONJUNCTION));
					getServletContext().log("Added citation count to the query.");
					System.out.println("Added citation count to the query.");
					getServletContext().log("Added citation count to the query.");
				}
			} catch (NumberFormatException ex)
			{
				getServletContext().log("Invalid number for citation count", ex);
				System.out.println("Invalid number for citation count");
			}
			currSession.setAttribute("LastCitationCount", citationCountString);

			//locators
			String locatorValue = request.getParameter("LocatorCodeValue");
			if (locatorValue != null && locatorValue.length() > 0)
			{
				String locatorOperator = request.getParameter("LocatorCodeOperator");
				if (locatorOperator == null || locatorOperator.length() == 0)
				{
					String errMsg = "Error in StructureFixerServlet.  Locator specified but not operator.";
					getServletContext().log(errMsg);
					httpUtils.processError(errMsg, request, response, getServletContext());
				}
				String locatorTableName = getServletContext().getInitParameter("LocatorTableName");
				String locatorTableID = getServletContext().getInitParameter("LocatorTableID");

				String locatorColumn = getServletContext().getInitParameter("LocatorQueryColumn");
				//translate operator
				String[] locatorOperatorForQuery = HttpUtilities.getSQLOperator(locatorOperator,
						locatorValue.toUpperCase());
				queryClauses.add(new QueryClause(locatorTableName, locatorColumn,
						locatorOperatorForQuery[0], "'" + locatorOperatorForQuery[1] + "'",
						QueryClause.DEFAULT_CONJUNCTION));
				if (!queryTables.contains(locatorTableName))
				{
					queryTables.add(locatorTableName);
					queryJoinItems.add(new QueryJoinItem(chemicalTableName, chemicalTableID,
							locatorTableName, locatorTableID));
				}
			}

			currSession.setAttribute("LastLocatorValue", locatorValue);
			//build the overall query

			String cheshireViewSQL = "";
			try
			{
				cheshireViewSQL = DatabaseUtilities.createQueryFromComponents(selectedColumnNames,
						queryTables,
						queryClauses, queryJoinItems, nonstandardQueryItems);
			} catch (IllegalArgumentException ex)
			{
				String msg = "Error during query generation: "
						+ ex.getMessage();
				System.err.println(msg);
				httpUtils.processError(msg, request, response, getServletContext());
				return;
			}

			System.out.println("Going to run SQL: " + cheshireViewSQL);
			getServletContext().log("Going to run SQL: " + cheshireViewSQL);
			try
			{
				if (conn == null || conn.isClosed())
				{
					request.setAttribute("ErrorMessage", "Error establishing Oracle connection");

					RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/errorpage.jsp");
					dispatcher.forward(request, response);
					return;
				}

				StringBuilder errorMessage = null;
				//initialize the Cheshire environment
				if (!structureProcessor.initCheshire(cheshireInitString, conn, errorMessage))
				{
					httpUtils.processError(errorMessage.toString(), request, response,
							getServletContext());
				}

				//Must load templateMol variable before defining script functions
				Statement statement2 = conn.createStatement();
				ResultSet loadingMolResult = statement2.executeQuery(cheshireLoadMolSQL);
				if (loadingMolResult == null || !loadingMolResult.next())
				{
					//Error!
					getServletContext().log("Error initializing loading target structure into Cheshire; command did not produce a result!");
					System.err.println("Error initializing loading target structure into Cheshire; command did not produce a result!");
					return;
				} else
				{
					System.out.println("Cheshire loaded mol OK. SQL: "
							+ cheshireLoadMolSQL);
					getServletContext().log("Cheshire loaded mol OK. SQL: "
							+ cheshireLoadMolSQL);
				}
				loadingMolResult.close();
				statement2.close();

				//Set up our Cheshire function
				Statement statementCheshire1 = conn.createStatement();
				getServletContext().log("SQL to define Cheshire functions: "
						+ sqlToLoadCheshireFunctions);

				ResultSet definingScriptResult;// = statementCheshire1.executeQuery(sqlToLoadCheshireFunctions0);

				definingScriptResult = statementCheshire1.executeQuery(sqlToLoadCheshireFunctions);
				if (definingScriptResult == null || !definingScriptResult.next())
				{
					//Error!
					System.out.println("Error loading Cheshire scripts; command did not produce a result!");
				 	getServletContext().log("Cheshire loaded mol OK. SQL: "
							+ cheshireLoadMolSQL);
					return;
				}
				int cheshInt = definingScriptResult.getInt(1);
				System.out.println("Result of Cheshire function init: " + cheshInt);
				getServletContext().log("Result of Cheshire function init: " + cheshInt);

				definingScriptResult.close();
				statementCheshire1.close();

				ArrayList<IColumnMetadata> tableMetadata = new ArrayList<IColumnMetadata>();
				String idFieldName = getServletContext().getInitParameter("StructureIdField");

				tableMetadata.add(
						new ColumnMetadata(idFieldName, "ID", "dataCell", FieldType.STRING,
								SIZE_STRUCTURE_IN_TABLE, SIZE_STRUCTURE_IN_TABLE, true, true, "%s"));
				tableMetadata.add(
						new ColumnMetadata("molname", "Name", "chemNameCell", FieldType.STRING,
								SIZE_STRUCTURE_IN_TABLE, SIZE_STRUCTURE_IN_TABLE, false, true, "%s"));
				tableMetadata.add(
						new ColumnMetadata("molwt", "Mol Wt.", "dataCell", FieldType.DOUBLE,
								SIZE_STRUCTURE_IN_TABLE, SIZE_STRUCTURE_IN_TABLE, false, true, "%1$.3f"));
				tableMetadata.add(
						new ColumnMetadata("molfmla", "Mol Fmla.", "dataCell", FieldType.STRING,
								SIZE_STRUCTURE_IN_TABLE, SIZE_STRUCTURE_IN_TABLE, false, true, "%s"));
				tableMetadata.add(
						new ColumnMetadata("TimesUpdated", "# Cheshire Updates", "dataCell",
								FieldType.INT, SIZE_STRUCTURE_IN_TABLE, SIZE_STRUCTURE_IN_TABLE,
								false, true, "%s"));
//				if (citationCount > 0)
//				{
//					tableMetadata.add(
//							new ColumnMetadata("CITATIONCOUNT", "Citations", "dataCell",
//							FieldType.INT, SIZE_STRUCTURE_IN_TABLE, SIZE_STRUCTURE_IN_TABLE,
//							false, true, "%s"));
//				}
				tableMetadata.add(
						new ColumnMetadata("initstr", "Initial Structure", "dataCell", FieldType.STRUCTURE_CHEM,
								SIZE_STRUCTURE_IN_TABLE + 40, SIZE_STRUCTURE_IN_TABLE + 40, false, true, "%s"));

				String structureStyleClass = "dataCell";
				String structureStyleClassOrientOnly = "dataCell";
				if (cleanBeforeOrienting.equals("TRUE"))
				{
					structureStyleClass = "dataCellHighlighted";
				} else
				{
					structureStyleClassOrientOnly = "dataCellHighlighted";
				}
				tableMetadata.add(
						new ColumnMetadata("cleanedandoriented", "Cleaned and Oriented",
								structureStyleClass, FieldType.STRUCTURE_CHEM,
								SIZE_STRUCTURE_IN_TABLE + 40, SIZE_STRUCTURE_IN_TABLE + 40, false, true, "%s"));

				tableMetadata.add(
						new ColumnMetadata("onlyOriented", "Oriented Only",
								structureStyleClassOrientOnly, FieldType.STRUCTURE_CHEM,
								SIZE_STRUCTURE_IN_TABLE + 40, SIZE_STRUCTURE_IN_TABLE + 40, false, true, "%s"));

				Statement statement3 = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);

				System.out.println("SQL for SSS/Cheshire orientation: "
						+ cheshireViewSQL);
				getServletContext().log("SQL for SSS/Cheshire orientation: "
						+ cheshireViewSQL);
				long beforeSQL = System.currentTimeMillis();
				ResultSet cheshireResults = statement3.executeQuery(cheshireViewSQL);
				long afterSQL = System.currentTimeMillis() - beforeSQL;
				double elapsed = afterSQL / 1000.0d;
				DecimalFormat df = new DecimalFormat("#.##");
				getServletContext().log("SQL processing: " + df.format(elapsed));

				int totalRecs = 0;
				if (cheshireResults != null)
				{
					long beforeDataCollection = System.currentTimeMillis();
					ArrayList<IFieldDataList> cheshireData = dbUtilities.getDataFromResultSet(cheshireResults);
					long netDataCollection = System.currentTimeMillis() - beforeDataCollection;
					elapsed = netDataCollection / 1000.0d;
					getServletContext().log("Collecting data from ResultSet: " + df.format(elapsed));

					cheshireResults.last();
					totalRecs = cheshireResults.getRow();
					cheshireResults.close();
					statement3.close();

					ArrayList<String> selectedIDs = new ArrayList<>();
					ArrayList<Integer> structureNumbersDisplayed = new ArrayList<>();

					String resultsTable = httpUtils.getHTMLTableAsStringFromArrayList(cheshireData,
							0, Math.min(totalRecs - 1, (maxRecordsPerPage - 1)), true, tableMetadata, selectedIDs,
							structureIDRequiresQuotes, structureNumbersDisplayed);
					structureNumbersDisplayed.add(arbitraryLargeNumber);
					String javascriptFunctionToCall
							= HttpUtilities.buildMarvinJsForItems(structureNumbersDisplayed,
									structureWidth, structureHeight);
					getServletContext().log("called HttpUtilities.buildMarvinJsForItems");

					currSession.setAttribute("StructureDataTable", resultsTable);
					currSession.setAttribute("OracleConnection", conn);
					currSession.setAttribute("OrientationStructure",
							structureAsMolfilestring);
					currSession.setAttribute("MolWtMax", molWtMaxString);
					currSession.setAttribute("CleanBeforeOrienting",
							cleanBeforeOrienting);

					currSession.setAttribute("RecordCount", totalRecs);
					currSession.setAttribute("lastRowShown", maxRecordsPerPage);
					currSession.setAttribute("MaxRecordsPerPage", maxRecordsPerPage);
					currSession.setAttribute("CheshData", cheshireData);
					currSession.setAttribute("TableMetadata", tableMetadata);
					currSession.setAttribute("FirstRecord", 1);
					currSession.setAttribute("displayFunction", javascriptFunctionToCall);

					response.sendRedirect("structuretable2.jsp");
				}

			} catch (SQLException ex)
			{
				System.err.println("Error executing statement: " + ex.getMessage());
				getServletContext().log("Error executing statement: " + ex.getMessage(), ex);
				ex.printStackTrace();
				request.setAttribute("ErrorMessage",
						"Error running Oracle query on structure table: " + ex.getMessage());

				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/errorpage.jsp");
				dispatcher.forward(request, response);
			}

		} finally
		{
			out.close();
		}
	}

	//Set up those parts of the query that we use in all cases
	public void setUpBasicQuery(ArrayList<String> tableNameList,
			ArrayList<String> columnList, ArrayList<String> nonstandardClauses,
			String queryStructure, ServletContext context)
	{
		//make sure we have input
		if (tableNameList == null || columnList == null)
		{
			throw new IllegalArgumentException("Error in StructureFixerServlet.setUpBasicQuery: null input.");
		}
		String chemTableName = context.getInitParameter("ChemTable");
		tableNameList.add(chemTableName);

		//retrieve the list of known fields for all queries
		String allColumnNamesString = context.getInitParameter("ColumnListMain");
		String[] allColumnNames = allColumnNamesString.split(", ");
		columnList.addAll(Arrays.asList(allColumnNames));
//		for (String columnName : allColumnNames)
//		{
//			columnList.add(columnName);
//		}

		//Some clauses come from the structure search and Cheshire Script
		//Assumption: there is only one CTAB field among the tables we're using!
		String structureSearch = " sss(CTAB, '%s', 3) = 1 ";
		structureSearch = String.format(structureSearch, queryStructure);
		nonstandardClauses.add(structureSearch);

		String cheshireScriptCommand = "runScript((%s), molfile(CTAB), cheshVarsList('outMol1'), 2) = 1 ";
		String scriptSubquery = context.getInitParameter("ScriptRunningSQL");
		cheshireScriptCommand = String.format(cheshireScriptCommand, scriptSubquery);
		nonstandardClauses.add(cheshireScriptCommand);
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo()
	{
		return "Runs a SQL query that includes a call to a script that orients a series of structures according to a template structure.";
	}// </editor-fold>
}
