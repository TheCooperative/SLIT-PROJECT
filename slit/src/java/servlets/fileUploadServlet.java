/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import db.DBConnectionManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.sql.SQLException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

/**
 *
 * @author Martin
 */
@WebServlet(name = "fileUploadServlet", urlPatterns = {"/fileUpload"})
@MultipartConfig(maxFileSize = 10485760)
public class fileUploadServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            
        InputStream inputStream = null;

        // obtain the upload file part
        Part filePart = request.getPart("fileBlob");
        if(filePart != null) {
            //obtains input stream of the upload file
            inputStream = filePart.getInputStream();
        }

        try {
        // Connect to the database
        Connection conn;
        conn = DBConnectionManager.getConnection();
        
        String fileName = request.getParameter("fileName");
        
        String sql = "INSERT INTO handIn(fileName, fileBlob, deliveryDate, u_id, m_id) values (?, ?, now(), ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        
        if(inputStream != null){
            HttpSession session = request.getSession(true);
            //fetches input stream of the upload file for the blob column
            ps.setString(1, fileName);
            ps.setBlob(2, inputStream);
            
            int u_id = (Integer) session.getAttribute("id");
            ps.setInt(3, u_id);
            //<--sett inn id mellom her-->
            //Den heter m_id oppe i sql queryen der.
            //ps.setInt(4, m_id);
            //<-------------------------->
            ps.executeUpdate();
        } 
        
        response.sendRedirect("fileUpload.jsp");
        //Legg til en melding til brukeren om opplastning fullført her!
        
        } catch(SQLException e){
            System.out.println("Error:"+e);
            response.sendRedirect("fileUpload.jsp");
            
            //Legg til en feilmelding til brukeren her!
        }
    }
}