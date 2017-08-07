package myactivityresult.book.com.parking;

import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONParser {
    private String dbStr;
    private int started_at;
    private String zone_name;
    private int zone_index;
    private int floor;
    private int empty_space;
    private int virtual_money;

    private ArrayList<Integer> entered_array;
    private ArrayList<Integer> exited_array;

    public JSONParser(String dbStr){
        this.dbStr = dbStr;
        empty_space = 0;
        entered_array = new ArrayList<>();
        exited_array = new ArrayList<>();
    }

    public void parser(int select){
        try {
            JSONObject json = new JSONObject(dbStr);
            JSONObject json2;

            switch (select) {
                case 1:
                    json2 = json.getJSONObject("car");
                    started_at = json2.getInt("entered_at");

                    json2 = json.getJSONObject("place");
                    zone_name = json2.getString("zone_name");
                    zone_index = json2.getInt("zone_index");
                    floor = json2.getInt("floor");

                    break;
                case 2:
                    empty_space = json.getInt("empty_places_count");
                    break;
                case 3:
                    json2 = json.getJSONObject("car");
                    virtual_money = json2.getInt("money");
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }
    public void parser_array(SharedPreferences pref){
        String carNumber = pref.getString("CarNumber",null);
        int last_index = pref.getInt(carNumber + "_last_index", 0);
        //Log.d("test", "last_index : " + last_index);
        //Log.d("test", "dbStr : " + dbStr);
        try{
            JSONObject json = new JSONObject(dbStr);
            JSONArray jsonArray = json.getJSONArray("entering_logs");
            for(int i = last_index ; i < jsonArray.length(); i++){
                JSONObject result = jsonArray.getJSONObject(i);
                entered_array.add(result.getInt("entered_at"));
                exited_array.add(result.getInt("exited_at"));
                last_index++;
            }
        } catch(JSONException e){}

        //Log.d("test", "last_index : " + last_index);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(carNumber + "_last_index",last_index);
        editor.commit();
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
    public ArrayList<Integer> getEntered_array(){ return entered_array; }
    public ArrayList<Integer> getExited_array(){ return exited_array; }
    public int getVirtual_money(){return virtual_money; }
}
