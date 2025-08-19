package gov.epa.ghg.service.view.list.viewmaker;

import java.util.Map;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public interface ListViewMaker {
	
  JSONArray buildColumnHeaders();
  JSONArray buildRows();
  String buildTableHeader();
  JSONObject buildListData();
  void unrollArguments(Map<String, Object> args);
  JSONObject createView(Map<String, Object> args );



}
