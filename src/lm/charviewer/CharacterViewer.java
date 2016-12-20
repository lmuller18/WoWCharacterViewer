package lm.charviewer;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class for accessing Blizzard API for WoW character stats
 */
public class CharacterViewer {

    // the currently searched for character as a jsonObject
    public JsonObject charObj;

    // database variables
    private static Connection con;
    private static String url = "jdbc:mysql://localhost:3306/wowcharacter";
    private static String user = "root";
    private static String password = "test";

    /**
     * Retrieves the JsonObject for the given character
     * @param name - the name of the character to search for
     * @param realm - the realm of the character to search for
     */
    public boolean getCharacter(String name, String realm){
        String charInfo = "";
        try {
            // access the api
            URL url = new URL("https://us.api.battle.net/wow/character/"+realm+"/"+name+"?fields=stats,items&locale=en_US&apikey=9ebxvqhu6a5ym2u8cgc7eg3u3tsd8cae");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                return false;
            }

            // retrieve the json data
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            while ((output = br.readLine()) != null) {
                charInfo += output;
            }
            br.close();
            conn.disconnect();

            // cast to json
            JsonParser jsonParser = new JsonParser();
            JsonElement element = jsonParser.parse(charInfo);

            JsonObject temp = element.getAsJsonObject();
            if(temp == null || (temp.get("status") != null && temp.get("status").getAsString().equals("nok"))){
                return false;
            }
            // set charObj
            charObj = element.getAsJsonObject();

            // add basic char info to the database
            addToDatabase();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Add to Character database
     */
    private void addToDatabase(){
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection(url,user,password);
            String name = getName();
            int hp = getHealth();
            int level = getLevel();
            String query = "insert ignore into `character`(CHARNAME,HP,`LEVEL`) values(?, ?, ?)";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, hp);
            preparedStatement.setInt(3, level);
            preparedStatement.executeUpdate();

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException e){

        }
    }

    /**
     * Returns hp for current character
     * @return character's hp
     */
    public int getHealth(){
        return charObj.get("stats").getAsJsonObject().get("health").getAsInt();
    }

    public static int getCompareHealth(String name){
        try{
            String query = "SELECT * from `CHARACTER` WHERE CHARNAME = ?";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, name);
            ResultSet rs = preparedStatement.executeQuery();

            rs.next();
            return rs.getInt("HP");
        } catch (SQLException e){
            return -99;
        }
    }

    /**
     * Returns level for current character
     * @return character's level
     */
    public int getLevel(){
        return charObj.get("level").getAsInt();
    }

    public static int getCompareLevel(String name){
        try{
            String query = "SELECT * from `CHARACTER` WHERE CHARNAME = ?";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, name);
            ResultSet rs = preparedStatement.executeQuery();

            rs.next();
            return rs.getInt("LEVEL");
        } catch (SQLException e){
            return -99;
        }
    }

    /**
     * Returns name for current character
     * @return character's name
     */
    public String getName(){return charObj.get("name").getAsString();}

    public static String getCompareAttributes(String name){
        try{
            String query = "SELECT * from ATTRIBUTES WHERE Attribute_ID = ?";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, name);
            ResultSet rs = preparedStatement.executeQuery();

            LinkedHashMap<String, Integer> attributes = new LinkedHashMap<>();
            while(rs.next()){
                attributes.put("Strength", rs.getInt("STRENGTH"));
                attributes.put("Agility", rs.getInt("AGILITY"));
                attributes.put("Intellect", rs.getInt("INTELLECT"));
                attributes.put("Stamina", rs.getInt("STAMINA"));
            }
            String json = new Gson().toJson(attributes);
            return json;
        } catch (SQLException e){
            return null;
        }
    }

    /**
     * Retrieve Attack stats from database
     * @param name - name of character to search for
     * @return - attack stats
     */
    public static String getCompareAttack(String name){
        try{
            String query = "SELECT * from ATTACK WHERE Attack_ID = ?";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, name);
            ResultSet rs = preparedStatement.executeQuery();

            LinkedHashMap<String, String> attributes = new LinkedHashMap<>();
            while(rs.next()){
                attributes.put("Main Hand Damage", rs.getString("MainHandDmg"));
                attributes.put("Main Hand DPS", Integer.toString(rs.getInt("MainHandDPS")));
                attributes.put("Off Hand Damage", rs.getString("OffHandDmg"));
                attributes.put("Off Hand DPS", Integer.toString(rs.getInt("OffHandDPS")));
                attributes.put("Ranged Damage", rs.getString("RangedDmg"));
                attributes.put("Ranged DPS", Integer.toString(rs.getInt("RangedDPS")));
            }
            String json = new Gson().toJson(attributes);
            return json;
        } catch (SQLException e){
            return null;
        }
    }

    /**
     * Retrieve Defense stats from database
     * @param name - name of character to search for
     * @return defense stats
     */
    public static String getCompareDefense(String name){
        try{
            String query = "SELECT * from DEFENSE WHERE Defense_ID = ?";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, name);
            ResultSet rs = preparedStatement.executeQuery();

            LinkedHashMap<String, String> attributes = new LinkedHashMap<>();
            while(rs.next()){
                attributes.put("Armor", Integer.toString(rs.getInt("ARMOR")));
                attributes.put("Dodge", rs.getString("DODGE"));
                attributes.put("Parry", rs.getString("PARRY"));
                attributes.put("Block", rs.getString("BLOCK"));
            }
            String json = new Gson().toJson(attributes);
            return json;
        } catch (SQLException e){
            return null;
        }
    }

    /**
     * Retrieve Enhancements from the database
     * @param name - name of character to search for
     * @return Enhancements
     */
    public static String getCompareEnhancements(String name){
        try{
            String query = "SELECT * from ENHANCEMENTS WHERE Enhancement_ID = ?";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, name);
            ResultSet rs = preparedStatement.executeQuery();

            LinkedHashMap<String, String> attributes = new LinkedHashMap<>();
            while(rs.next()){
                attributes.put("Crit", rs.getString("CRIT"));
                attributes.put("Haste", rs.getString("HASTE"));
                attributes.put("Mastery", rs.getString("MASTERY"));
                attributes.put("Leech", rs.getString("LEECH"));
                attributes.put("Versatility", rs.getString("VERSATILITY"));
            }
            String json = new Gson().toJson(attributes);
            return json;
        } catch (SQLException e){
            return null;
        }
    }


    public static String getCompareCharacter(String name){
        try{
            String query = "SELECT * from `CHARACTER` WHERE CHARNAME = ?";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, name);
            ResultSet rs = preparedStatement.executeQuery();

            LinkedHashMap<String, Integer> attributes = new LinkedHashMap<>();
            while(rs.next()){
                attributes.put("lv", rs.getInt("LEVEL"));
                attributes.put("hp", rs.getInt("HP"));
            }
            String json = new Gson().toJson(attributes);
            return json;
        } catch (SQLException e){
            return null;
        }
    }


    /**
     * Returns attributes for the current character
     * Adds attributes to db
     * @return character's attributes
     */
    public String getAttributes(){
        JsonObject object = charObj.get("stats").getAsJsonObject();
        LinkedHashMap<String, Integer> attributes = new LinkedHashMap<>();
        int strength = object.get("str").getAsInt();
        int agility = object.get("agi").getAsInt();
        int intellect = object.get("int").getAsInt();
        int stamina = object.get("sta").getAsInt();

        attributes.put("Strength", strength);
        attributes.put("Agility", agility);
        attributes.put("Intellect", intellect);
        attributes.put("Stamina", stamina);

        String json = new Gson().toJson(attributes);

        try{
            String name = getName();
            String query = "insert ignore into attributes(ATTRIBUTE_ID,STRENGTH,AGILITY,INTELLECT,STAMINA) values(?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, strength);
            preparedStatement.setInt(3, agility);
            preparedStatement.setInt(4, intellect);
            preparedStatement.setInt(5, stamina);
            preparedStatement.executeUpdate();

        } catch (SQLException e){
            return null;
        }
        return json;
    }

    /**
     * Returns attack attributes for character
     * adds attack to db
     * @return character's attack
     */
    public String getAttack(){
        JsonObject object = charObj.get("stats").getAsJsonObject();
        LinkedHashMap<String, String> attributes = new LinkedHashMap<>();

        String mainHandDmg;
        int mainHandDmgMin = object.get("mainHandDmgMin").getAsInt();
        int mainHandDmgMax = object.get("mainHandDmgMax").getAsInt();
        if(mainHandDmgMin == mainHandDmgMax){
            mainHandDmg = Integer.toString(mainHandDmgMin);
        } else {
            mainHandDmg = mainHandDmgMin + "-" + mainHandDmgMax;
        }

        String offHandDmg;
        int offHandDmgMin = object.get("offHandDmgMin").getAsInt();
        int offHandDmgMax = object.get("offHandDmgMax").getAsInt();
        if(offHandDmgMin == offHandDmgMax){
            offHandDmg = Integer.toString(offHandDmgMin);
        } else {
            offHandDmg = offHandDmgMin + "-" + offHandDmgMax;
        }

        String rangedDmg;
        int rangedDmgMin = object.get("rangedDmgMin").getAsInt();
        int rangedDmgMax = object.get("rangedDmgMax").getAsInt();
        if(rangedDmgMin == rangedDmgMax){
            rangedDmg = Integer.toString(rangedDmgMin);
        } else {
            rangedDmg = rangedDmgMin + "-" + rangedDmgMax;
        }

        int mainHandDPS, offHandDPS, rangedDPS;
        mainHandDPS = object.get("mainHandDps").getAsInt();
        offHandDPS = object.get("offHandDps").getAsInt();
        rangedDPS = object.get("rangedDps").getAsInt();

        attributes.put("Main Hand Damage", mainHandDmg);
        attributes.put("Main Hand DPS", Integer.toString(mainHandDPS));
        attributes.put("Off Hand Damage", offHandDmg);
        attributes.put("Off Hand DPS", Integer.toString(offHandDPS));
        attributes.put("Ranged Damage", rangedDmg);
        attributes.put("Ranged DPS", Integer.toString(rangedDPS));

        String json = new Gson().toJson(attributes);

        try{
            String name = getName();
            String query = "insert ignore into attack(Attack_ID,MainHandDmg,MainHandDPS,OffHandDmg,OffHandDPS,RangedDmg,RangedDPS) values(?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, mainHandDmg);
            preparedStatement.setInt(3, mainHandDPS);
            preparedStatement.setString(4, offHandDmg);
            preparedStatement.setInt(5, offHandDPS);
            preparedStatement.setString(6, rangedDmg);
            preparedStatement.setInt(7, rangedDPS);
            preparedStatement.executeUpdate();

        } catch (SQLException e){
            return null;
        }

        return json;
    }

    /**
     * Returns defense attributes for character
     * adds defense to db
     * @return character's defense
     */
    public String getDefense(){
        if(charObj == null){
            return null;
        }

        JsonObject object = charObj.get("stats").getAsJsonObject();
        LinkedHashMap<String, String> attributes = new LinkedHashMap<>();

        String name = getName();
        int armor = object.get("armor").getAsInt();
        String dodge = String.format("%.2f", object.get("dodge").getAsDouble()) + "%";
        String parry = String.format("%.2f", object.get("parry").getAsDouble()) + "%";
        String block = String.format("%.2f", object.get("block").getAsDouble()) + "%";

        attributes.put("Armor", Integer.toString(armor));
        attributes.put("Dodge", dodge);
        attributes.put("Parry", parry);
        attributes.put("Block", block);

        String json = new Gson().toJson(attributes);

        try{
            String query = "insert ignore into defense(Defense_ID,ARMOR,DODGE,PARRY,BLOCK) values(?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, armor);
            preparedStatement.setString(3, dodge);
            preparedStatement.setString(4, parry);
            preparedStatement.setString(5, block);
            preparedStatement.executeUpdate();

        } catch (SQLException e){
            return null;
        }

        return json;
    }

    /**
     * Returns enhancements of current character
     * adds enhancements to db
     * @return character's enhancements
     */
    public String getEnhancements(){
        if(charObj == null){
            return null;
        }

        JsonObject object = charObj.get("stats").getAsJsonObject();

        LinkedHashMap<String, String> attributes = new LinkedHashMap<>();
        String name = getName();
        String crit = String.format("%.2f", object.get("crit").getAsDouble()) + "%";
        String haste = String.format("%.2f", object.get("haste").getAsDouble()) + "%";
        String mastery = String.format("%.2f", object.get("mastery").getAsDouble()) + "%";
        String leech = String.format("%.2f", object.get("leech").getAsDouble()) + "%";
        String versatility = String.format("%.2f", object.get("versatility").getAsDouble()) + "%";

        attributes.put("Crit", crit);
        attributes.put("Haste", haste);
        attributes.put("Mastery", mastery);
        attributes.put("Leech", leech);
        attributes.put("Versatility", versatility);

        String json = new Gson().toJson(attributes);

        try{
            String query = "insert ignore into ENHANCEMENTS(Enhancement_ID,CRIT,HASTE,MASTERY,LEECH,VERSATILITY) values(?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, crit);
            preparedStatement.setString(3, haste);
            preparedStatement.setString(4, mastery);
            preparedStatement.setString(5, leech);
            preparedStatement.setString(6, versatility);
            preparedStatement.executeUpdate();

        } catch (SQLException e){
            return null;
        }

        return json;
    }

    /**
     * Returns currently equipped items
     * gets cost
     * adds items to db
     * @return currently equipped items
     */
    public ArrayList<String> getItems(){
        if(charObj == null){
            return null;
        }

        JsonObject object = charObj.get("items").getAsJsonObject();
        String name = getName();
        ArrayList<String> items = new ArrayList<>();
        int buy, sell;
        JsonObject itemDetails;
        for (Map.Entry<String,JsonElement> entry : object.entrySet()) {
            try{
                String slot = entry.getKey();
                slot = Character.toUpperCase(slot.charAt(0)) + slot.substring(1);
                String item = entry.getValue().getAsJsonObject().get("name").getAsString();
                String id = entry.getValue().getAsJsonObject().get("id").getAsString();
                itemDetails = getItemDetails(id);
                buy = itemDetails.get("buyPrice").getAsInt();
                sell = itemDetails.get("sellPrice").getAsInt();

                JsonObject json = new JsonObject();
                json.addProperty("slot", slot);
                json.addProperty("item", item);
                json.addProperty("buy", buy);
                json.addProperty("sell", sell);

                try{
                    String query = "insert into items(Owner, SLOT, ITEM, ITEM_ID, BUYCOST, SELLCOSE) values(?, ?, ?, ?, ?, ?)";
                    PreparedStatement preparedStatement = con.prepareStatement(query);
                    preparedStatement.setString(1, name);
                    preparedStatement.setString(2, slot);
                    preparedStatement.setString(3, item);
                    preparedStatement.setString(4, id);
                    preparedStatement.setInt(5, buy);
                    preparedStatement.setInt(6, sell);

                    preparedStatement.executeUpdate();
                } catch (SQLException e){

                }

                String itemGson = new Gson().toJson(json);

                items.add(itemGson);
            } catch (IllegalStateException e){

            }
        }
        return items;
    }

//    public ArrayList<String> getItemList(String name){
//        try{
//            String query = "SELECT ITEM_ID FROM ITEMS WHERE Owner = ?";
//            PreparedStatement preparedStatement = con.prepareStatement(query);
//            preparedStatement.setString(1, name);
//            ResultSet rs = preparedStatement.executeQuery();
//
//            String id;
//            int buy;
//            int sell;
//            JsonObject itemDetails;
//            ArrayList<String> items = new ArrayList<>();
//            while(rs.next()){
//                id = rs.getString("ITEM_ID");
//                itemDetails = getItemDetails(id);
//                buy = itemDetails.get("buyPrice").getAsInt();
//                sell = itemDetails.get("sellPrice").getAsInt();
//                String update = "UPDATE ITEMS SET BUYCOST=?, SELLCOST=? WHERE ITEM_ID=?";
//                preparedStatement = con.prepareStatement(update);
//                preparedStatement.setInt(1, buy);
//                preparedStatement.setInt(2, sell);
//                preparedStatement.setString(3, id);
//
//                preparedStatement.executeUpdate();
//
//                JsonObject json = new JsonObject();
//                json.addProperty("id", id);
//                json.addProperty("sell", sell);
//                json.addProperty("buy", buy);
//
//                String itemGson = new Gson().toJson(json);
//
//                items.add(itemGson);
//            }
//            return items;
//        } catch (SQLException e){}
//        return null;
//    }

    /**
     * Retrieves full item details from api call
     * @param id - item id
     * @return - item jsonobject
     */
    public JsonObject getItemDetails(String id){
        try{
            String itemInfo = "";
            URL url = new URL("https://us.api.battle.net/wow/item/"+id+"?locale=en_US&apikey=9ebxvqhu6a5ym2u8cgc7eg3u3tsd8cae");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            // retrieve the json data
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            while ((output = br.readLine()) != null) {
                itemInfo += output;
            }
            br.close();
            conn.disconnect();

            // cast to json
            JsonParser jsonParser = new JsonParser();
            JsonElement element = jsonParser.parse(itemInfo);

            // set charObj
            JsonObject itemDetails = element.getAsJsonObject();
            return itemDetails;
        } catch (IOException e){}
        return null;
    }
}
