package irctc;

/**
 *
 * @author Manohar
 */

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.Scanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class NextStations {

    public static String trainInfo[][];
    public static int rows;
    public static String[] msgStations;

    public String getCodes(int tnum,String doj) {

        msgStations = new String[3];
        try {
            //int tnum = 17406;
            String stationCodes = "";
            Document doc = null;
            String checker = null;
            doc = Jsoup.connect("https://runningstatus.in/status/" + tnum + "-on-"+doj).get();
            
            Element fontTag = doc.select("font").first();
            checker = fontTag.text();

            System.out.print("Train is " + checker + "\n");
           
            if (checker.equals("Running") || checker.equals("Not Started")) {
                Elements trs = doc.getElementsByTag("tr");
                trainInfo = new String[trs.size() - 4][2];
                int index = 0;
                int i = 0;
                int outerloop = 0;
                
                for (Element tr : trs) {
                    if (outerloop == 0 || outerloop >= trs.size() - 3) {
                    } else {
                        if (tr.children().size() == 7) {
                            trainInfo[i][0] = tr.child(index).text();
                            trainInfo[i][1] = tr.child(index + 6).text();
                            i++;
                        } else if (tr.children().size() == 5) {
                            trainInfo[i][0] = tr.child(index).text();
                            trainInfo[i][1] = "--";
                            i++;
                        }
                    }
                    outerloop++;
                }
                
                for (int rows = trainInfo.length - 1; rows >= 0; rows--) {
                    if (trainInfo[rows][1].contains("Departed")) {
                        // System.out.println("Previous Station is :" + trainInfo[rows - 1][0]);
                        if (trainInfo.length - rows != 0) {
                            //  System.out.println("Total Stations :" + trainInfo.length + "\n " + rows);
                            //  System.out.println("Remaining Stations :" + (trainInfo.length - rows));
                            int pres = 0;
                            System.out.print("Atleast Reached this Statement");
                            //  System.out.print("Stations to be Informed are :");
                            if (trainInfo.length - rows == 1) {
                                msgStations = new String[1];
                                msgStations[0] = trainInfo[rows + 1][0];
                                // System.out.println(trainInfo[rows + 1][0]);
                            } else if (trainInfo.length - rows == 2) {
                                msgStations = new String[2];
                                msgStations[0] = trainInfo[rows + 1][0];
                                msgStations[1] = trainInfo[rows + 2][0];
                                //System.out.println(trainInfo[rows + 1][0] + trainInfo[rows + 2][0]);
                            } else if (trainInfo.length - rows >= 3) {
                                msgStations = new String[3];
                                msgStations[0] = trainInfo[rows + 1][0];
                                msgStations[1] = trainInfo[rows + 2][0];
                                msgStations[2] = trainInfo[rows + 3][0];
                                // System.out.println(trainInfo[rows + 1][0] + trainInfo[rows + 2][0] + trainInfo[rows + 3][0]);
                            }
                        }
                        break;
                    }
                }
                for (int rem = 0; rem < msgStations.length; rem++) {
                    stationCodes += "-" + msgStations[rem].substring(msgStations[rem].indexOf('(') + 1, msgStations[rem].indexOf(')'));

                }
                String[] mpo = stationCodes.split("-");
               /* for (int lmo = 1; lmo < mpo.length; lmo++) {
                    System.out.println("Codes are :" + mpo[lmo]);
                }*/
                return stationCodes;
            } else if (checker.equals("Not Started")) {
                System.out.println("Not Started");
                return "1";
            } else if (checker.equals("Destination Reached")) {
                System.out.println("Destination Reached");
                return "2";
            } else {
                System.out.println("Train Cancelled/Train on this Day/Date is not Available");
                return "3";
            }

        } catch (Exception exccc) {
            System.out.println("Exception Raised Here " + exccc.getMessage());
            return "Exception";
        }
    }
}
