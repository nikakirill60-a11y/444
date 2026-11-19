package ru.metrocraft.port.inventory;

/** Fixed pre-dev39 geometry. More text must never enlarge a cell or add scrolling. */
public final class CompactInventoryLayout {
    public static final int WIDTH=312,HEIGHT=230,GRID_X=170,GRID_Y=40,CELL=16,GRID_SIZE=128;
    public static int cellAt(int x,int y){return x<0||y<0||x>=GRID_SIZE||y>=GRID_SIZE?-1:x/CELL+y/CELL*8;}
    public static final class Card {
        public final boolean name,id,stats;
        public final int header,footer,iconSize;
        public Card(int width,int height){
            name=width>=32&&height>=32;
            id=name&&width>=64&&height>=48;stats=id;
            header=name?(id?13:8):2;footer=name?(stats?16:8):2;
            iconSize=Math.max(4,Math.min(38,Math.min(width-4,height-header-footer)));
        }
    }
    private CompactInventoryLayout(){}
}
