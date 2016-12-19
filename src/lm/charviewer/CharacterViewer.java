package lm.charviewer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class CharacterViewer {

    public JsonObject charObj;

    private static Connection con;
    private static String url = "jdbc:mysql://localhost:3306/wowcharacter";
    private static String user = "root";
    private static String password = "test";

    public void getCharacter(String name, String realm){
        String charInfo = "";
        try {
            URL url = new URL("https://us.api.battle.net/wow/character/"+realm+"/"+name+"?fields=stats,items&locale=en_US&apikey=9ebxvqhu6a5ym2u8cgc7eg3u3tsd8cae");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            while ((output = br.readLine()) != null) {
                charInfo += output;
            }
            br.close();
            conn.disconnect();

            JsonParser jsonParser = new JsonParser();
            JsonElement element = jsonParser.parse(charInfo);
            charObj = element.getAsJsonObject();
            addToDatabase();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public int getHealth(){
        return charObj.get("stats").getAsJsonObject().get("health").getAsInt();
    }

    public int getLevel(){
        return charObj.get("level").getAsInt();
    }

    public String getName(){
        return charObj.get("name").getAsString();
    }

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

        }

        return json;
    }

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

        }

        return json;
    }

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

        }

        return json;
    }

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

        }

        return json;
    }

    public String getItems(){
        if(charObj == null){
            return null;
        }

        JsonObject object = charObj.get("items").getAsJsonObject();
        LinkedHashMap<String, String> items = new LinkedHashMap<>();
        String name = getName();
        for (Map.Entry<String,JsonElement> entry : object.entrySet()) {
            try{
                String slot = entry.getKey();
                slot = Character.toUpperCase(slot.charAt(0)) + slot.substring(1);
                String item = entry.getValue().getAsJsonObject().get("name").getAsString();
                String id = entry.getValue().getAsJsonObject().get("id").getAsString();
                items.put(slot, item);

                try{
                    String query = "insert into items(Owner, SLOT, ITEM, ITEM_ID) values(?, ?, ?, ?)";
                    PreparedStatement preparedStatement = con.prepareStatement(query);
                    preparedStatement.setString(1, name);
                    preparedStatement.setString(2, slot);
                    preparedStatement.setString(3, item);
                    preparedStatement.setString(4, id);

                    preparedStatement.executeUpdate();
                } catch (SQLException e){

                }
            } catch (IllegalStateException e){

            }
        }

        String json = new Gson().toJson(items);

        return json;
    }

}
