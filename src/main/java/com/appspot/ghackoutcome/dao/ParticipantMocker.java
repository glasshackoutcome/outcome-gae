package com.appspot.ghackoutcome.dao;

import com.appspot.ghackoutcome.CardUtil;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rac on 21.06.14.
 */
public class ParticipantMocker {

    public static List<Participant> getMockList() {
        URL resource = CardUtil.class.getResource("/mockdata/base.csv");
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        List<Participant> ret = new ArrayList<Participant>();
        try {

            br = new BufferedReader(new FileReader(new File(resource.toURI())));
            br.readLine();
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] d = line.split(cvsSplitBy);
                Participant tmp = new Participant();
                tmp.setLastName(d[0])
                        .setFirstName(d[1])
                        .setPhotoURI(d[2])
                        .setProgramName(d[3])
                        .setValuedOutcome(d[4])
                        .setMethodOne(d[5])
                        .setMethodTwo(d[6])
                        .setMethodThree(d[7])
                        .setEmergDial(d[8])
                        .setEmergName(d[9])
                        .setEmergInfo(d[10])
                        .setMethodOneCompleted(false)
                        .setMethodTwoCompleted(false)
                        .setMethodThreeCompleted(false);
                ret.add(tmp);

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;

    }

}
