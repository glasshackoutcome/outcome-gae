package com.appspot.ghackoutcome;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

/**
 * Created by rac on 21.06.14.
 */
public class CardUtil {
    /**
     * Replace all variables from name/value pairs in values map in the template which match the corresponding ${var}
     *
     * @param fileName
     * @param values
     * @return
     * @throws IOException
     */
    public static String getCardTemplate(String fileName, Map values) throws IOException {
        URL resource = CardUtil.class.getResource("/cards/" + fileName);
        File templateFile = new File("./src/main/resources/cards/" + fileName);
        try {
            templateFile = new File(resource.toURI());
            //LOG.info("Able to find oauth properties from file.");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            //LOG.info(e.toString());
            //LOG.info("Using default source path.");
        }
        FileInputStream is = new FileInputStream(templateFile);
        String content = IOUtils.toString(is);
        return replaceVariables(content, values);
    }

    private static String replaceVariables(String content, Map values) {
        StrSubstitutor sub = new StrSubstitutor(values);
        return sub.replace(content);
    }
}
