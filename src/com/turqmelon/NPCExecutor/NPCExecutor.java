package com.turqmelon.NPCExecutor;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Creator: Devon
 * Project: NPCExecutor
 */
public class NPCExecutor extends JavaPlugin {

    private static NPCExecutor instance;

    @Override
    public void onEnable() {
        instance = this;
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(ExecutorTrait.class).withName("Executor"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equals("npce")){

            if (!sender.hasPermission("npce.manage")){
                sender.sendMessage(ChatColor.RED + "You don't have permission.");
                return true;
            }

            if ((sender instanceof Player)){
                NPC npc = CitizensAPI.getDefaultNPCSelector().getSelected(sender);
                if (npc != null){
                    if (npc.hasTrait(ExecutorTrait.class)){

                        if (args.length == 0){
                            sender.sendMessage(ChatColor.YELLOW + "Usage: /npce <Player|Console> <Command>");
                            sender.sendMessage(ChatColor.YELLOW + "Usage: /npce clear");
                        }
                        else{

                            if (args[0].equalsIgnoreCase("clear")){
                                for(Trait trait : npc.getTraits()){
                                    if ((trait instanceof ExecutorTrait)){
                                        ExecutorTrait exe = (ExecutorTrait)trait;
                                        exe.getCommands().clear();
                                        break;
                                    }
                                }
                                sender.sendMessage(ChatColor.GREEN + "Cleared the commands.");
                            }
                            else{

                                try {

                                    ExecutorTrait.SenderType type = ExecutorTrait.SenderType.valueOf(args[0].toUpperCase());

                                    String cmd = "";

                                    if (args.length >= 2){
                                        for(int i = 1; i < args.length; i++){
                                            cmd = cmd + " " + args[i];
                                        }
                                        cmd=cmd.substring(1);
                                    }

                                    if (cmd.length() > 0){

                                        for(Trait trait : npc.getTraits()){
                                            if ((trait instanceof ExecutorTrait)){
                                                ExecutorTrait exe = (ExecutorTrait)trait;
                                                exe.getCommands().put(cmd, type);
                                            }
                                        }
                                        sender.sendMessage(ChatColor.GREEN + type.name() + " command added: " + cmd);

                                    }
                                    else{
                                        sender.sendMessage(ChatColor.RED + "Command is empty.");
                                    }

                                }catch (Exception ex){
                                    sender.sendMessage(ChatColor.RED + "Unknown sender type.");
                                }

                            }

                        }

                    }
                    else{
                        sender.sendMessage(ChatColor.RED + npc.getName() + " is missing the \"executor\" trait.");
                    }
                }
                else{
                    sender.sendMessage(ChatColor.RED + "Select an NPC first.");
                }
            }
            else{
                sender.sendMessage(ChatColor.RED + "Must be player.");
            }

        }

        return true;
    }

    public static NPCExecutor getInstance() {
        return instance;
    }
}
