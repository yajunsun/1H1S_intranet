package zgan.ohos.Dals;

import org.ksoap2.serialization.SoapObject;

import java.util.List;

import zgan.ohos.Models.Event;

/**
 * Created by yajunsun on 2015/11/24.
 */
public class EventDal extends baseDal<Event> {
    public List<Event> getCurrentEvents(String datestr) throws Exception {
//        String SOAP_ACTION = "http://tempuri.org/IEventsContract/getCurrentEvents";
//        String MethodName = "getCurrentEvents";
//        SoapObject request = new SoapObject(NameSpace, MethodName);
//        request.addProperty("datestr", datestr);
//        return getnetobjectlist(new Event(), request, URL, SOAP_ACTION);
        return null;
    }

    public List<Event> getPreViewEvents(String datestr) throws Exception {
//        String SOAP_ACTION = "http://tempuri.org/IEventsContract/getPreViewEvents";
//        String MethodName = "getPreViewEvents";
//        SoapObject request = new SoapObject(NameSpace, MethodName);
//        request.addProperty("datestr", datestr);
//        return getnetobjectlist(new Event(), request, URL, SOAP_ACTION);
        return null;
    }

    public List<Event> getEvent(int Id) throws Exception {
//        String SOAP_ACTION = "http://tempuri.org/IEventsContract/getEvent";
//        String MethodName = "getEvent";
//        SoapObject request = new SoapObject(NameSpace, MethodName);
//        request.addProperty("Id", Id);
//        return getnetobjectlist(new Event(), request, URL, SOAP_ACTION);
        return null;
    }

    public Event getFrontEvent() throws Exception {
//        String SOAP_ACTION = "http://tempuri.org/IEventsContract/getFrontEvent";
//        String MethodName = "getFrontEvent";
//        SoapObject request = new SoapObject(NameSpace, MethodName);
//        return getnetobject(new Event(), request, URL, SOAP_ACTION);
        return null;
    }
}