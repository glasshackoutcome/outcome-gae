package com.appspot.ghackoutcome;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.io.IOException;
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
    public String getCardTemplate(String fileName, Map values) throws IOException {
        String content = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("cards/" + fileName));
        return replaceVariables(content, values);
    }

    private String replaceVariables(String content, Map values) {
        StrSubstitutor sub = new StrSubstitutor(values);
        return sub.replace(content);
    }
}
