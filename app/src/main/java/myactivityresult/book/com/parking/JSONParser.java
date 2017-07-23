package myactivityresult.book.com.parking;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser {
    private String dbStr;
    private String numbering;
    private int started_at;
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
            JSONObject json2;

            switch (select) {
                case 1:
                    json2 = json.getJSONObject("car");
                    numbering = json2.getString("numbering");
                    started_at = json2.getInt("started_at");

                    /*
                    JSONArray array = json.getJSONArray("car");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject result = array.getJSONObject(i);
                        numbering = result.getString("numbering");
                        started_at = result.getInt("started_at");
                    }
                    */
                    json2 = json.getJSONObject("place");
                    zone_name = json2.getString("zone_name");
                    zone_index = json2.getInt("zone_index");
                    floor = json2.getInt("floor");

                    break;
                case 2:
                    json2 = json.getJSONObject(""); // 서버 측에 API 제작 요청
                    empty_space = json2.getInt("");
                    break;
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public String getNumbering(){
        return numbering;
    }
    public int getStarted_at(){
        return started_at;
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
