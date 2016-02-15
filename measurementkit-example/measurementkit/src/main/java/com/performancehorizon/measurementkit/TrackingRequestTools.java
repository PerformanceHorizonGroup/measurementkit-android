//package com.performancehorizon.measurementkit;
//
//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by owainbrown on 02/03/15.
// */
///*public class TrackingRequestTools {
//
//    static protected class NameValuePairFactory {
//
//        protected NameValuePair pair(String name, String value) {
//            return new BasicNameValuePair(name, value);
//        }
//    }
//
//    public static List<NameValuePair> convertParameterMapToList(Map<String, Object> parametermap)
//    {
//        return TrackingRequestTools.convertParameterMapToList(parametermap, new NameValuePairFactory());
//    }
//
//    protected static List<NameValuePair> convertParameterMapToList(Map<String, Object> parametermap, NameValuePairFactory factory)
//    {
//        return TrackingRequestTools.nameValuePairsFromKeyAndValue(null, parametermap, factory);
//    }
//
//    private static List<NameValuePair> nameValuePairsFromKeyAndValue(String key, Object value, NameValuePairFactory factory)
//    {
//        ArrayList<NameValuePair> returnparameterlist = new ArrayList<NameValuePair>();
//
//        if (value instanceof Map<?, ?>) {
//            @SuppressWarnings("unchecked") //Unfortunate, but having to delay checking.
//                    Map<Object, Object> map = (Map<Object, Object>)value;
//
//            for (Map.Entry<Object, Object> entry : map.entrySet()) {
//                Object nestedvalue = entry.getValue();
//                Object nestedkey = entry.getKey();
//
//                if (nestedvalue != null) { //sanity check, could happen.
//
//                    if (key == null) {
//                        //unpacks dictionaries.
//                        returnparameterlist.addAll(TrackingRequestTools.nameValuePairsFromKeyAndValue(key != null ? String.format("%s[%s]", key, nestedkey.toString()) :
//                                nestedkey.toString(), nestedvalue, factory));
//                    }
//                    else {
//                        returnparameterlist.addAll(TrackingRequestTools.nameValuePairsFromKeyAndValue(String.format("%s[%s]", key, nestedkey.toString()), nestedvalue, factory));
//                    }
//                }
//            }
//
//
//        }
//        else if(value instanceof List<?>) {
//            @SuppressWarnings("unchecked") //Unfortunate, but having to delay checking.
//            List<Object> array = (List<Object>) value;
//            for (int i = 0; i < array.size(); i++) {
//                Object nestedvalue = array.get(i);
//
//                returnparameterlist.addAll(TrackingRequestTools.nameValuePairsFromKeyAndValue(String.format("%s[%d]", key, i), nestedvalue, factory));
//            }
//        }
//        else if (value instanceof String) {
//            returnparameterlist.add(factory.pair(key, (String)value));
//        }
//        else {
//            returnparameterlist.add(factory.pair(key, value.toString()));
//        }
//
//        return returnparameterlist;
//    }
//}
