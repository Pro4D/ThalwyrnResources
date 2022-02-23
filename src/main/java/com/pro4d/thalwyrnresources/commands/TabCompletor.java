package com.pro4d.thalwyrnresources.commands;

import com.pro4d.thalwyrnresources.ThalwyrnResources;
import com.pro4d.thalwyrnresources.enums.JobTypes;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TabCompletor implements TabCompleter {

    public TabCompletor(ThalwyrnResources resources) {
        resources.getCommand("resource").setTabCompleter(this);
    }


    // length = 5
    //           0   1      2          3        4
    //resource place 3 woodcutting gran_tree end_tree
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> tabList = new ArrayList<>();
        switch (strings.length) {

            case 1:
                tabList.add("reload");
                tabList.add("delete");
                tabList.add("edit");
                tabList.add("place");
                tabList.add("copy");
                break;
            case 2:
                if(strings[0].equals("delete") || strings[0].equals("edit") || strings[0].equals("copy")) {
                    tabList.add("<id>");
                }
                if(strings[0].equals("place")) {
                    tabList.add("<level>");
                }
                break;
            case 3:
                if(strings[0].equals("place")) {
                    for(JobTypes jobTypes : JobTypes.values()) {
                       tabList.add((jobTypes.getJobName().toLowerCase()));
                    }
                }
                if(strings[0].equals("edit")) {
                    tabList.add("xp");
                    tabList.add("level");
                    tabList.add("job");
                    tabList.add("left-click");
                    tabList.add("right-click");
                }
                break;
            case 4:
                switch (strings[0]) {
                    case "edit":
                        switch (strings[2]) {
                            case "xp":
                            case "level":
                                tabList.add("<value>");
                                break;
                            case "job":
                                for(JobTypes jobTypes : JobTypes.values()) {
                                    tabList.add((jobTypes.getJobName().toLowerCase()));
                                }
                                break;
                        }
                    case "place":
                        switch(strings[2]) {
                            case "woodcutting":
                                tabList.add("schematic-1");
                                break;
                        }

                }
                break;
                //FIX SCHEMATIC AUTOCOMPLETE
                //FIX CAPS FOR JOBS
                //CHANGE TOP LINE OF HOLOGRAM
            case 5:
                switch (strings[0]) {
                    case "place":
                        if(strings[2].equals("woodcutting")) {
                            tabList.add("schematic-2");
                        }
                        break;
                }
                break;
        }

        return tabList;
    }
}
