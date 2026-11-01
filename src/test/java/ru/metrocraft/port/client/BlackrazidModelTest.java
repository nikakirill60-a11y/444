package ru.metrocraft.port.client;
import com.google.gson.Gson;
import net.minecraft.client.model.ModelRenderer;
import org.junit.Test;
import ru.metrocraft.port.blackrazid.BlackrazidContent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.Field;
import java.util.Map;
import static org.junit.Assert.*;
public class BlackrazidModelTest {
    @Test public void allEighteenModelsRebuildRealVanillaBoxesAndChildJoints()throws Exception{
        BlackrazidContent.load();
        for(BlackrazidContent.Entry e:BlackrazidContent.entries){
            BlackrazidModel.Data d;
            try(Reader r=new InputStreamReader(getClass().getResourceAsStream("/assets/metroport/geometry/blackrazid/"+e.model+".json"),StandardCharsets.UTF_8)){d=new Gson().fromJson(r,BlackrazidModel.Data.class);}
            BlackrazidModel m=new BlackrazidModel(d,e.mode);assertEquals(d.textureSize[0],m.textureWidth);
            Field field=BlackrazidModel.class.getDeclaredField("nodes");field.setAccessible(true);
            Map<String,ModelRenderer> nodes=(Map<String,ModelRenderer>)field.get(m);assertEquals(d.parts.length,nodes.size());
            for(BlackrazidModel.Part p:d.parts){assertEquals(p.boxes.length,nodes.get(p.name).cubeList.size());assertEquals(p.point[0],nodes.get(p.name).rotationPointX,0);}
            assertFalse(m.bipedBody.cubeList.isEmpty());assertTrue(m.patchSurface()>=.257);
        }
    }
}
