/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package irctc;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Manohar
 */
public class SubmitForm extends HttpServlet {

    //static Connection  c;
    PreparedStatement ps;
    //  Class.forName("org.apache.derby.jdbc.ClientDriver");
    Connection c;
    String pnr;
    long pnrnum;

    public SubmitForm() throws SQLException {
        this.c = DriverManager.getConnection("jdbc:derby://localhost:1527/ApplicantData", "cse", "cse");
    }
 String name ="",toc="";
  long mobno;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        long pnrno = 0;
        int tnum = 0;
        String tname = null, fs = null, bs = null, ts = null, ds = null;
        Date mydate = null;
        name = request.getParameter("nm");
       
       
        String mob = request.getParameter("mob");
         mobno = Long.parseLong(mob);
       toc = request.getParameter("toc");
        if (request.getParameter("res").equals("true")) {
             pnr = request.getParameter("pnr");
             pnrnum = Long.parseLong(pnr);
            try {
                c = DriverManager.getConnection("jdbc:derby://localhost:1527/ApplicantData", "cse", "cse");
                ps = c.prepareStatement("select * from pnrtable where pnrno=" + pnrnum);
                ResultSet r = ps.executeQuery();
                while (r.next()) {
                    pnrno = r.getLong("pnrno");
                    tnum = r.getInt("tnum");
                    tname = r.getString("tname");
                    mydate = new java.util.Date(r.getDate("doj").getTime());
                    fs = r.getString("fs");
                    ts = r.getString("ts");
                    bs = r.getString("bs");
                    ds = r.getString("ds");
                    
                }
                System.out.println("Exception at Reserved");
            } catch (SQLException ex) {
                Logger.getLogger(SubmitForm.class.getName()).log(Level.SEVERE, null, ex);
            }
            String d = new SimpleDateFormat("yyyyMMdd").format(mydate);
            System.out.println("Date is : "+d);
            try {
                getNextTrains(request,response,tnum,tname,d);
                
                /*  String in = "insert into complaints values(?,?,?,?,?,?,?,?) ";
                try {
                ps = c.prepareStatement(in);
                ps.setLong(1, pnrnum);
                ps.setString(2, name);
                ps.setInt(3, tnum);
                ps.setString(4, tname);
                ps.setString(5, d);
                ps.setString(6, bs);
                ps.setString(7, ds);
                ps.setLong(8, mobno);
                ps.executeUpdate();
                } catch (SQLException ex) {
                Logger.getLogger(SubmitForm.class.getName()).log(Level.SEVERE, null, ex);
                }*/
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(SubmitForm.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } 
    else if (request.getParameter("res").equals("false"))
        {
            String trainInfo = request.getParameter("tn");
            int trainNo = Integer.parseInt((trainInfo.substring(0,trainInfo.indexOf("-"))).trim());
            String doj = request.getParameter("doj");
            System.out.print(trainNo);
            String totalDoj = "";
            for(int len=0;len<doj.split("-").length;len++){
                totalDoj+=doj.split("-")[len];
            }
            System.out.print(totalDoj);
           
            try {
                ps = c.prepareStatement("select * from TrainDetails where tnum=?");
                ps.setInt(1, trainNo);
                ResultSet rs = ps.executeQuery();
                while(rs.next())
                    tname = rs.getString("tname");
            } catch (SQLException ex) {
                
                System.out.println("Exception at Genereal");
            }
            try {
                getNextTrains(request,response,trainNo,tname,totalDoj);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(SubmitForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public void errorPage(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {
        RequestDispatcher rd = req.getRequestDispatcher("/Failed");
        rd.forward(req, res);
    }
    public int getTno() throws SQLException {
        int tno = 0;
        ps = c.prepareStatement("select tnum from pnrtable where pnrno =" + pnrnum);
        ResultSet r = ps.executeQuery();
        while (r.next()) {
            tno = r.getInt("tnum");
        }
        return tno;
    }
    public void getNextTrains(HttpServletRequest request,HttpServletResponse response, int tnum,String tname,String doj) throws ServletException, IOException, ClassNotFoundException
    {
        
         NextStations n = new NextStations();
            String gc = null;
            gc = n.getCodes(tnum,doj);
            HttpSession ses = request.getSession();
            if (gc.equals("1")) {
                System.out.println("Not Started");
                ses.setAttribute("Error","Train "+tnum+"-"+tname+" is Not Started");
                errorPage(request,response);
                return;
            } else if (gc.equals("2")) {
                System.out.println("Destination Reached");
                ses.setAttribute("Error","Train "+tnum+"-"+tname+" has Reached the Destination");
                errorPage(request,response);
                return;
            } else if (gc.equals("3")) {
                System.out.println("Train Cancelled/Train on this Day/Date is not Available");
                ses.setAttribute("Error","Train "+tnum+"-"+tname+" is Cancelled / Not avaialable for Given Date/Day");
                errorPage(request,response);
                return;
            } else if (gc.equals("Exception")) {
                System.out.println("Internal error Occured");
                ses.setAttribute("Error","An Internal Error has been Occured");
                errorPage(request,response);
                return;
            } else if (gc.contains("-")) {
                String[] codes = gc.split("-");
                for (int i = 1; i < codes.length; i++) {
                    System.out.println(codes[i]);
                    try {
                        ps = c.prepareStatement("select mno from stations where scode=?");
                        ps.setString(1, codes[i]);
                        ResultSet r = ps.executeQuery();
                        while (r.next()) {
                            String m = r.getString("mno");
                            String[] mnum = m.split(",");
                            for (int lop = 0; lop < mnum.length; lop++) {
                              Complaint.sendMessage(mnum[lop], tnum, tname, name, toc, mobno);
                              System.out.println("Submission Success"+mnum[lop]);
                            }
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(SubmitForm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
            //ServletContext sc = getServletContext();
            RequestDispatcher rd =request.getRequestDispatcher("/Success.html");
            rd.forward(request, response);
            return;
    }

}
