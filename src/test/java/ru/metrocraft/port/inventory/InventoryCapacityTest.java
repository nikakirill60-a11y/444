package ru.metrocraft.port.inventory;
import org.junit.Test;
import static org.junit.Assert.*;
public class InventoryCapacityTest {
    @Test public void creativeCatalogueHasFullStorageButSurvivalAlwaysUsesMetroBackpackCapacity(){
        for(int rows=0;rows<=3;rows++){
            assertEquals(3,InventoryCapacity.rows(true,rows));assertEquals(rows,InventoryCapacity.rows(false,rows));
        }
        assertEquals(0,InventoryCapacity.rows(false,-10));assertEquals(3,InventoryCapacity.rows(false,100));
    }
}
