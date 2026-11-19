package ru.metrocraft.port.inventory;
import org.junit.Test;
import static org.junit.Assert.*;
public class InventoryCapacityTest {
    @Test public void backpacksNeverGateInventoryRows(){
        for(int tier:new int[]{-10,0,1,2,3,100}){
            assertEquals(3,InventoryCapacity.rows(true,tier));assertEquals(3,InventoryCapacity.rows(false,tier));
        }
    }
}
