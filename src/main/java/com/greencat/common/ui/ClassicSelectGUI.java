package com.greencat.common.ui;

import com.greencat.Antimony;
import com.greencat.common.config.ConfigLoader;
import com.greencat.common.config.getConfigByFunctionName;
import com.greencat.common.storage.SelectGUIStorage;
import com.greencat.type.SelectObject;
import com.greencat.type.SelectTable;
import net.minecraft.client.Minecraft;

public class ClassicSelectGUI {
    Minecraft mc = Minecraft.getMinecraft();
    public void draw(){



        int height = (Integer) getConfigByFunctionName.get("HUD","HUDHeight") + 10;
        int width = 0;
        mc.fontRendererObj.drawString("Antimony",0, (Integer) getConfigByFunctionName.get("HUD","HUDHeight"), Antimony.Color);
        for(SelectTable table : SelectGUIStorage.TableStorage){
            if(table.getID().equals(Antimony.PresentGUI)){
                for(SelectObject object : table.getList()){
                    if(object.getName().equals(Antimony.PresentFunction)) {
                        if(object.getType().equals("function")) {
                            String selectObjectName = "[功能]" + object.getName() + " <-";
                            mc.fontRendererObj.drawString(selectObjectName, width, height, Antimony.Color);
                        } else {
                            String selectObjectName = "[列表]" + object.getName() + " <-";
                            mc.fontRendererObj.drawString(selectObjectName, width, height, Antimony.Color);
                        }
                    } else {
                        if(object.getType().equals("function")) {
                            String selectObjectName = "[功能]" + object.getName();
                            mc.fontRendererObj.drawString(selectObjectName, width, height, Antimony.Color);
                        } else {
                            String selectObjectName = "[列表]" + object.getName();
                            mc.fontRendererObj.drawString(selectObjectName, width, height, Antimony.Color);
                        }
                    }
                    height = height + 10;
                }

            }
        }
    }
}
