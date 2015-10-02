package com.performancehorizon.mobiletracking;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by owainbrown on 02/03/15.
 */
public class RequestTools {

    public static List<NameValuePair> convertParameterMapToList(Map<String, Object> parametermap)
    {
        return RequestTools.nameValuePairsFromKeyAndValue(null, parametermap);
    }


    private static List<NameValuePair> nameValuePairsFromKeyAndValue(String key, Object value)
    {
        ArrayList<NameValuePair> returnparameterlist = new ArrayList<NameValuePair>();

        if (value instanceof Map<?, ?>)
        {
            @SuppressWarnings("unchecked") //Unfortunate, but having to delay checking.
                    Map<Object, Object> map = (Map<Object, Object>)value;

            for (Map.Entry<Object, Object> entry : map.entrySet())
            {
                Object nestedvalue = entry.getValue();
                Object nestedkey = entry.getKey();

                if (nestedvalue != null) //sanity check, could happen.
                {
                    if (key == null)
                    {
                        //unpacks dictionaries.
                        returnparameterlist.addAll(RequestTools.nameValuePairsFromKeyAndValue(key != null ? String.format("%s[%s]", key, nestedkey.toString()) :
                                nestedkey.toString(), nestedvalue));
                    }
                    else
                    {
                        returnparameterlist.addAll(RequestTools.nameValuePairsFromKeyAndValue(String.format("%s[%s]", key, nestedkey.toString()), nestedvalue));
                    }
                }
            }


        }
        else if(value instanceof List<?>)
        {
            @SuppressWarnings("unchecked") //Unfortunate, but having to delay checking.
                    List<Object> array = (List<Object>) value;
            for (int i = 0; i < array.size(); i++)
            {
                Object nestedvalue = array.get(i);

                returnparameterlist.addAll(RequestTools.nameValuePairsFromKeyAndValue(String.format("%s[%d]", key, i), nestedvalue));
            }
        }
        else if (value instanceof String)
        {
            returnparameterlist.add(new BasicNameValuePair(key, (String)value));
        }
        else
        {
            returnparameterlist.add(new BasicNameValuePair(key, value.toString()));
        }


        return returnparameterlist;
    }
}
