/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thinkscience.demo.structurecleaner.servlets;

import com.thinkscience.demo.structurecleaner.objects.ifc.IColumnMetadata;
import com.thinkscience.demo.structurecleaner.objects.ifc.IFieldDataList;
import com.thinkscience.demo.structurecleaner.objects.impl.FieldDataList;
import com.thinkscience.demo.utilities.ArrayListUtilities;
import com.thinkscience.demo.utilities.HttpUtilities;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Mitch
 */
public class StructureFixerPager extends HttpServlet
{

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
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
			HttpSession currSession = request.getSession();

			Connection conn = (Connection) currSession.getAttribute("OracleConnection");
			HttpUtilities httpUtils = new HttpUtilities();
			try
			{
				if (conn == null || conn.isClosed())
				{
					httpUtils.processError("Applicaiton error. No database connection found. Please restart",
							request, response, getServletContext());
				}
			} catch (SQLException ex)
			{
				String msg = "Error in StructureFixerPager while checking for existing database connection";
				httpUtils.processError(msg, request, response, getServletContext());
			}

			int totalRecs = (Integer) currSession.getAttribute("RecordCount");
			int recordsPerPage = Integer.parseInt(getServletContext().getInitParameter("MaxRecordsPerPage"));
			int firstRecord = Integer.parseInt(request.getParameter("startRec"));

			currSession.setAttribute("FirstRecord", firstRecord);

			int lastRecord = firstRecord + recordsPerPage - 2;
			//Make sure we don't try to move past the last record
			if (lastRecord > totalRecs-1)
			{
				lastRecord = totalRecs-1;
			}

			ArrayList<IFieldDataList> cheshData
					= (ArrayList<IFieldDataList>) currSession.getAttribute("CheshData");
			ArrayList<IColumnMetadata> tableMetadata =
					(ArrayList<IColumnMetadata>) currSession.getAttribute("TableMetadata");
			Boolean structureIDRequiresQuotes = Boolean.parseBoolean(getServletContext().getInitParameter("StructureIDRequiresQuotes"));

			ArrayList<String> IDsToIgnore = new ArrayList<String>();
			ArrayList<String> IDsToProcess = httpUtils.getSelectedIDsFromRequest(request,
					"includeID_", structureIDRequiresQuotes, IDsToIgnore);
			ArrayListUtilities.DumpArrayList(IDsToIgnore, "IDs to ignore:");
			ArrayListUtilities.DumpArrayList(IDsToProcess, "IDs from Request object:");

			ArrayList<String> knownIDsToProcess =
					(ArrayList<String>) currSession.getAttribute("IDsToProcess");
			ArrayListUtilities.DumpArrayList(knownIDsToProcess, "IDs from Session object:");

			ArrayListUtilities.ConsolidateArrayLists( knownIDsToProcess, IDsToProcess,
					IDsToIgnore);

			currSession.setAttribute("IDsToProcess", IDsToProcess);

			System.out.print("In StructureFixerPager, consolidated list of IDs to process. ");
			System.out.println("(count = " + IDsToProcess.size());
			System.out.println("	");
			for(String id : IDsToProcess)
			{
				System.out.print(id + " ");
			}
			System.out.println(" ");

			//special case: end  of list
			int firstRecordForDisplay;
			firstRecordForDisplay = firstRecord -1;
			if( firstRecordForDisplay <=0 ) firstRecordForDisplay = 1;
			ArrayList<Integer> structureNumbersDisplayed = new ArrayList<Integer>();
			//make the query appear
			int arbitraryLargeNumber = 2013822;
			structureNumbersDisplayed.add(arbitraryLargeNumber);
			String resultsTable = httpUtils.getHTMLTableAsStringFromArrayList(cheshData,
					firstRecordForDisplay, lastRecord, true, tableMetadata, IDsToProcess,
					structureIDRequiresQuotes, structureNumbersDisplayed);
			int structureHeight = Integer.parseInt((String) currSession.getAttribute("StructureViewHeight"));
			int structureWidth = Integer.parseInt((String) currSession.getAttribute("StructureViewWidth"));

			String javascriptFunctionToCall = HttpUtilities.buildMarvinJsForItems(structureNumbersDisplayed, 
							structureWidth, structureHeight);
			currSession.setAttribute("displayFunction", javascriptFunctionToCall);

			System.out.println(String.format("called with firstRecord: %s; calculated lastRecord: %s",
					firstRecord, lastRecord));
			currSession.setAttribute("StructureDataTable", resultsTable);
			try
			{
				response.sendRedirect("structuretable2.jsp");
			} catch (IllegalStateException ise)
			{
				System.err.println("Error trying to forward:  " + ise.getMessage());
			}

		} catch (Exception ex)
		{
			System.err.println("Error in StructureFixerPager servlet: "
					+ ex.getMessage());
		} finally
		{
			out.close();
		}
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
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
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo()
	{
		return "Short description";
	}// </editor-fold>
}
