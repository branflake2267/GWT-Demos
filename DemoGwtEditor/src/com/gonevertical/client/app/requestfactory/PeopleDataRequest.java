package com.gonevertical.client.app.requestfactory;

import java.util.List;

import com.gonevertical.client.app.requestfactory.dto.PeopleDataProxy;
import com.gonevertical.client.app.requestfactory.dto.PeopleListFilterProxy;
import com.gonevertical.server.data.PeopleData;
import com.google.web.bindery.requestfactory.shared.ExtraTypes;
import com.google.web.bindery.requestfactory.shared.InstanceRequest;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(PeopleData.class)
public interface PeopleDataRequest extends RequestContext {

  Request<PeopleDataProxy> findPeopleData(String id);
  
  Request<List<PeopleDataProxy>> findPeopleData(PeopleListFilterProxy filter);
  
  Request<Long> findCount(PeopleListFilterProxy filter);
  
  InstanceRequest<PeopleDataProxy, PeopleDataProxy> persist();
  
  InstanceRequest<PeopleDataProxy, Boolean> remove();
  
}
