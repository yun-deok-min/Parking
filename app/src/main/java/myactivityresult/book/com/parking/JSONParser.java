package myactivityresult.book.com.parking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONParser {
    private String dbStr;
    private ArrayList<String> numbering;
    private ArrayList<Integer> start_at;
    private ArrayList<String> zone_name;
    private ArrayList<Integer> zone_index;
    private ArrayList<Integer> floor;
    private ArrayList<Integer> id;
    private int empty_space;

    public JSONParser(String dbStr){
        this.dbStr = dbStr;
        numbering = new ArrayList<>();
        start_at = new ArrayList<>();
        zone_name = new ArrayList<>();
        zone_index = new ArrayList<>();
        floor = new ArrayList<>();
        id = new ArrayList<>();
        end_at = new ArrayList<>();
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
                        numbering.add(result.getString("numbering"));
                        start_at.add(result.getInt("start_at"));
                    }
                    array = json.getJSONArray("place");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject result = array.getJSONObject(i);
                        zone_name.add(result.getString("zone_name"));
                        zone_index.add(result.getInt("zone_index"));
                        floor.add(result.getInt("floor"));
                    }
                    break;
                case 2:
                    array = json.getJSONArray("entering_logs");
                    for (int i = 0; i < array.length(); i++){
                        JSONObject result = array.getJSONObject(i);
                        id.add(result.getInt("id"));
                        start_at.add(result.getInt("start_at"));
                        end_at.add(result.getInt("end_at"));
                    }
                    break;
                case 3:
                    array = json.getJSONArray("places");
                    for (int i = 0; i < array.length(); i++){
                        JSONObject result = array.getJSONObject(i);
                        id.add(result.getInt("id"));
                        zone_name.add(result.getString("zone_name"));
                        zone_index.add(result.getInt("zone_index"));
                        floor.add(result.getInt("floor"));
                        numbering.add(result.getString("car_numbering"));
                    }
                    break;
            }

        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getNumbering(){
        return numbering;
    }
    public ArrayList<Integer> getStart_at(){
        return start_at;
    }
    public ArrayList<String> getZone_name(){
        return zone_name;
    }
    public ArrayList<Integer> getZone_index(){
        return zone_index;
    }
    public ArrayList<Integer> getFloor(){
        return floor;
    }
    public ArrayList<Integer> getId(){
        return id;
    }
    public ArrayList<Integer> getEnd_at(){
        return end_at;
    }
    public int getEmpty_space() { return empty_space; }

}
