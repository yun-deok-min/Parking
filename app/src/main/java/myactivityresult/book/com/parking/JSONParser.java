package myactivityresult.book.com.parking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser {
    private String dbStr;
    private String numbering;
    private int start_at;
    private String zone_name;
    private int zone_index;
    private int floor;
    private int empty_space;

    public JSONParser(String dbStr){
        this.dbStr = dbStr;
        empty_space = 0;
    }

    public void parser(int select){
        try {
            JSONObject json = new JSONObject(dbStr);

            switch (select) {
                case 1:
                    JSONArray array = json.getJSONArray("car");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject result = array.getJSONObject(i);
                        numbering = result.getString("numbering");
                        start_at = result.getInt("start_at");
                    }
                    array = json.getJSONArray("place");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject result = array.getJSONObject(i);
                        zone_name = result.getString("zone_name");
                        zone_index = result.getInt("zone_index");
                        floor = result.getInt("floor");
                    }
                    break;
                case 2:
                    array = json.getJSONArray(""); // 서버 측에 API 제작 요청
                    for (int i = 0; i < array.length(); i++){
                        JSONObject result = array.getJSONObject(i);
                        empty_space = result.getInt(""); // 서버 측에 API 제작 요청
                    }
                    break;
            }

        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public String getNumbering(){
        return numbering;
    }
    public int getStart_at(){
        return start_at;
    }
    public String getZone_name(){
        return zone_name;
    }
    public int getZone_index(){
        return zone_index;
    }
    public int getFloor(){
        return floor;
    }
    public int getEmpty_space() { return empty_space; }

}
