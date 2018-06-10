package irctc;

/**
 *
 * @author Manohar
 */
import com.sun.xml.ws.tx.coord.common.PendingRequestManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;

public class Complaint {

    public static void sendMessage(String mno, int tnum, String tname, String aplName, String toc, long mobno) throws ClassNotFoundException, SQLException {
        try {
            String retval = "";
            String Username = "becb-becoff";
            String Password = "becoff";
            String MobileNo = mno;
            String Message = "Compliace About : " + toc + "\nHappened in Train " + tnum + "  " + tname + "\n Applicant Details are : " + aplName + "\n Mobile Number :" + mobno;
            String SenderID = "BECOFF";
            String postData = "username=" + Username + "&password=" + Password + "&type=0&dlr=1&destination=" + MobileNo + "&source=" + SenderID + "&message=" + Message;
            URL url = new URL("http://103.16.101.52:8080/sendsms/bulksms?");
            HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
            urlconnection.setRequestMethod("POST");
            urlconnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlconnection.setDoOutput(true);

            OutputStreamWriter outs = new OutputStreamWriter(urlconnection.getOutputStream());
            outs.write(postData);
            outs.flush();
            outs.close();
            BufferedReader ins = new BufferedReader(new InputStreamReader(urlconnection.getInputStream()));
            String decodedString;

            while ((decodedString = ins.readLine()) != null) {
                retval += decodedString;

            }
            System.out.println(retval);
            ins.close();
            postData = "";
            retval = "";
        } catch (Exception e) {
        }
    }
}
