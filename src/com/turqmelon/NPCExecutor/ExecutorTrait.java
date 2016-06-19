package com.turqmelon.NPCExecutor;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.api.util.MemoryDataKey;
import org.bukkit.event.EventHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Map;

/**
 * Creator: Devon
 * Project: NPCExecutor
 */
@SuppressWarnings("unchecked")
public class ExecutorTrait extends Trait {

    private Map<String, SenderType> commands = new HashMap<>();

    public ExecutorTrait() {
        super("Executor");
    }

    @Override
    public void load(DataKey key) {
        JSONParser parser = new JSONParser();
        String data = key.getString("commands", null);

        if (data == null)return;

        try {
            Object o = parser.parse(data);
            JSONObject cmds = (JSONObject)o;
            JSONArray array = (JSONArray) cmds.get("cmds");
            for(Object co : array){
                JSONObject cmd = (JSONObject)co;
                String command = (String) cmd.get("cmd");
                SenderType type = SenderType.valueOf((String) cmd.get("sender"));
                commands.put(command, type);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(DataKey key) {
        JSONObject data = new JSONObject();
        JSONArray cmds = new JSONArray();
        for(String command : commands.keySet()){
            SenderType type = commands.get(command);
            JSONObject cmd = new JSONObject();
            cmd.put("cmd", command);
            cmd.put("sender", type.name());
            cmds.add(cmd);
        }
        data.put("cmds", cmds);
        key.setString("commands", data.toJSONString());
    }

    @EventHandler
    public void onClick(NPCRightClickEvent event){
        for(String command : commands.keySet()){
            SenderType sender = commands.get(command);
            String c = command.replace("{name}", event.getClicker().getName());
            if (sender == SenderType.CONSOLE){
                if (c.startsWith("/")){
                    c = c.substring(1);
                }
                NPCExecutor.getInstance().getServer().dispatchCommand(
                        NPCExecutor.getInstance().getServer().getConsoleSender(),
                        c
                );
            }
            else if (sender == SenderType.PLAYER){
                if (!c.startsWith("/")){
                    c = "/" + c;
                }
                event.getClicker().chat(c);
            }
        }
    }

    @Override
    public void onAttach() {
        load(new MemoryDataKey());
    }

    @Override
    public void onDespawn() {
        super.onDespawn();
    }

    @Override
    public void onSpawn() {
        super.onSpawn();
    }

    @Override
    public void onRemove() {
        super.onRemove();
    }

    public Map<String, SenderType> getCommands() {
        return commands;
    }

    public static enum SenderType {
        CONSOLE, PLAYER
    }
}
