package lm.charviewer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CharacterViewer {

    private static JsonObject charObj;

    public static void getCharacter(String name, String realm){
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getHealth(){
        if(charObj == null){
            return "";
        }
        return charObj.get("stats").getAsJsonObject().get("health").getAsString();
    }

    public static String getLevel(){
        if(charObj == null){
            return "";
        }
        return charObj.get("level").getAsString();
    }

    public static String getName(){
        if(charObj == null){
            return "";
        }
        return charObj.get("name").getAsString();
    }

    public static HashMap<String, String> getAttributes(){
        if(charObj == null){
            return null;
        }

        JsonObject object = charObj.get("stats").getAsJsonObject();
        LinkedHashMap<String, String> attributes = new LinkedHashMap<>();
        attributes.put("Strength", object.get("str").getAsString());
        attributes.put("Agility", object.get("agi").getAsString());
        attributes.put("Intellect", object.get("int").getAsString());
        attributes.put("Stamina", object.get("sta").getAsString());

        return attributes;
    }

    public static HashMap<String, String> getAttack(){
        if(charObj == null){
            return null;
        }

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


        attributes.put("Main Hand Damage", mainHandDmg);
        attributes.put("Main Hand DPS", object.get("agi").getAsString());
        attributes.put("Off Hand Damage", offHandDmg);
        attributes.put("Off Hand DPS", object.get("sta").getAsString());
        attributes.put("Ranged Damage", rangedDmg);
        attributes.put("Ranged DPS", object.get("sta").getAsString());

        return attributes;
    }

    public static HashMap<String, String> getDefense(){
        if(charObj == null){
            return null;
        }

        JsonObject object = charObj.get("stats").getAsJsonObject();
        LinkedHashMap<String, String> attributes = new LinkedHashMap<>();
        attributes.put("Armor", object.get("armor").getAsString());
        attributes.put("Dodge", String.format("%.2f", object.get("dodge").getAsDouble()) + "%");
        attributes.put("Parry", String.format("%.2f", object.get("parry").getAsDouble()) + "%");
        attributes.put("Block", String.format("%.2f", object.get("block").getAsDouble()) + "%");

        return attributes;
    }

    public static HashMap<String, String> getEnhancements(){
        if(charObj == null){
            return null;
        }

        JsonObject object = charObj.get("stats").getAsJsonObject();

        LinkedHashMap<String, String> attributes = new LinkedHashMap<>();
        attributes.put("Crit", String.format("%.2f", object.get("crit").getAsDouble()) + "%");
        attributes.put("Haste", String.format("%.2f", object.get("haste").getAsDouble()) + "%");
        attributes.put("Mastery", String.format("%.2f", object.get("mastery").getAsDouble()) + "%");
        attributes.put("Leech", String.format("%.2f", object.get("leech").getAsDouble()) + "%");
        attributes.put("Versatility", String.format("%.2f", object.get("versatility").getAsDouble()) + "%");

        return attributes;
    }

    public static HashMap<String, String> getItems(){
        if(charObj == null){
            return null;
        }

        JsonObject object = charObj.get("items").getAsJsonObject();
        LinkedHashMap<String, String> items = new LinkedHashMap<>();
        for (Map.Entry<String,JsonElement> entry : object.entrySet()) {
            try{
                String slot = entry.getKey();
                slot = Character.toUpperCase(slot.charAt(0)) + slot.substring(1);
                String name = entry.getValue().getAsJsonObject().get("name").getAsString();
                items.put(slot, name);
            } catch (IllegalStateException e){

            }
        }
        return items;
    }

}
