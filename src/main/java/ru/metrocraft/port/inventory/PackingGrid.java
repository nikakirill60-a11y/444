package ru.metrocraft.port.inventory;

import java.util.Arrays;

/** Pure geometry. Each record is one real stack, never a copy in each covered cell. */
public final class PackingGrid {
    public static final int SIDE=8, CELLS=SIDE*SIDE, RECORDS=27;
    private final int[] widths,heights,positions=new int[RECORDS];
    public PackingGrid(int[] widths,int[] heights,int[] saved){
        this.widths=widths.clone();this.heights=heights.clone();Arrays.fill(positions,-1);
        // Reserve every still-valid saved rectangle before trying to place new/overflow items.
        for(int i=0;i<RECORDS;i++)if(widths[i]>0 && i<saved.length && canPlace(widths[i],heights[i],saved[i],i))positions[i]=saved[i];
        for(int i=0;i<RECORDS;i++)if(widths[i]>0 && positions[i]<0)positions[i]=firstFit(widths[i],heights[i],i);
    }
    public static boolean rotated(int position){return position>=CELLS;}
    public static int cell(int position){return position%CELLS;}
    public static int encode(int cell,boolean rotated){return cell+(rotated?CELLS:0);}
    public int position(int record){return positions[record];}
    public int[] positions(){return positions.clone();}
    public int width(int record){return rotated(positions[record])?heights[record]:widths[record];}
    public int height(int record){return rotated(positions[record])?widths[record]:heights[record];}
    private static boolean intersects(int ax,int ay,int aw,int ah,int bx,int by,int bw,int bh){
        return ax<bx+bw && ax+aw>bx && ay<by+bh && ay+ah>by;
    }
    public boolean canPlace(int width,int height,int encoded,int ignore){
        if(width<1||height<1||width>SIDE||height>SIDE||encoded<0||encoded>=CELLS*2)return false;
        int x=cell(encoded)%SIDE,y=cell(encoded)/SIDE;
        int w=rotated(encoded)?height:width,h=rotated(encoded)?width:height;
        if(x+w>SIDE||y+h>SIDE)return false;
        for(int i=0;i<RECORDS;i++)if(i!=ignore && positions[i]>=0){
            int p=cell(positions[i]);
            if(intersects(x,y,w,h,p%SIDE,p/SIDE,width(i),height(i)))return false;
        }
        return true;
    }
    public int firstFit(int width,int height,int ignore){
        for(int rotation=0;rotation<2;rotation++)for(int c=0;c<CELLS;c++){
            int p=encode(c,rotation==1);if(canPlace(width,height,p,ignore))return p;
        }
        return -1;
    }
    public boolean place(int record,int width,int height,int encoded){
        if(record<0||record>=RECORDS||!canPlace(width,height,encoded,record))return false;
        widths[record]=width;heights[record]=height;positions[record]=encoded;return true;
    }
    public int recordAt(int cell){
        if(cell<0||cell>=CELLS)return -1;
        int x=cell%SIDE,y=cell/SIDE;
        for(int i=0;i<RECORDS;i++)if(positions[i]>=0){
            int p=cell(positions[i]);
            if(x>=p%SIDE && x<p%SIDE+width(i) && y>=p/SIDE && y<p/SIDE+height(i))return i;
        }
        return -1;
    }
}
