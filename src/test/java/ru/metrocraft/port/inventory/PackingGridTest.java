package ru.metrocraft.port.inventory;

import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class PackingGridTest {
    private int[] sizes(int count,int value){int[] n=new int[27];Arrays.fill(n,0,count,value);return n;}
    private void noOverlaps(PackingGrid grid,int[] widths){
        int occupied=0;Set<Integer> cells=new HashSet<>();
        for(int i=0;i<27;i++)if(grid.position(i)>=0){
            assertTrue(widths[i]>0);int c=PackingGrid.cell(grid.position(i));
            for(int y=0;y<grid.height(i);y++)for(int x=0;x<grid.width(i);x++){
                int cell=c+x+y*8;assertTrue(cell>=0&&cell<64);assertTrue(c%8+x<8);
                assertTrue(cells.add(cell));assertEquals(i,grid.recordAt(cell));occupied++;
            }
        }
        assertTrue(occupied<=64);
    }
    @Test public void fourFourByFourItemsFillTheGridAndFifthIsOverflow(){
        int[] w=sizes(5,4),h=sizes(5,4);PackingGrid grid=new PackingGrid(w,h,new int[0]);
        for(int i=0;i<4;i++)assertTrue(grid.position(i)>=0);assertEquals(-1,grid.position(4));
        assertEquals(-1,grid.firstFit(1,1,-1));noOverlaps(grid,w);
    }
    @Test public void rotationMakesThreeByTwoFitAlongRightEdge(){
        PackingGrid grid=new PackingGrid(new int[27],new int[27],new int[0]);
        assertFalse(grid.place(0,3,2,PackingGrid.encode(6,false)));
        assertTrue(grid.place(0,3,2,PackingGrid.encode(6,true)));
        assertEquals(2,grid.width(0));assertEquals(3,grid.height(0));assertEquals(0,grid.recordAt(7+2*8));
    }
    @Test public void overlapAndBottomBoundaryRejectWithoutChangingState(){
        PackingGrid grid=new PackingGrid(new int[27],new int[27],new int[0]);assertTrue(grid.place(0,4,4,0));
        int[] before=grid.positions();assertFalse(grid.place(1,3,2,1));assertArrayEquals(before,grid.positions());
        assertFalse(grid.place(1,3,2,7*8));assertFalse(grid.place(-1,1,1,0));assertFalse(grid.place(27,1,1,0));
        assertFalse(grid.canPlace(0,1,0,0));assertFalse(grid.canPlace(9,1,0,0));assertFalse(grid.canPlace(1,1,128,0));
    }
    @Test public void savedPositionsAndRotationSurviveReload(){
        int[] w=sizes(2,3),h=sizes(2,2);PackingGrid grid=new PackingGrid(w,h,new int[0]);
        assertTrue(grid.place(1,3,2,PackingGrid.encode(5*8+6,true)));
        PackingGrid next=new PackingGrid(w,h,grid.positions());assertArrayEquals(grid.positions(),next.positions());
    }
    @Test public void badOldPositionsAreRepairedAndEveryStackStillHasARecord(){
        int[] w=sizes(27,2),h=sizes(27,2),bad=new int[27];Arrays.fill(bad,999);
        PackingGrid grid=new PackingGrid(w,h,bad);int placed=0,overflow=0;
        for(int i=0;i<27;i++)if(grid.position(i)>=0)placed++;else overflow++;
        assertEquals(16,placed);assertEquals(11,overflow);noOverlaps(grid,w);
    }
    @Test public void newlyFreedSpaceRecoversOverflowWithoutMovingOtherRecords(){
        int[] w=sizes(5,4),h=sizes(5,4);PackingGrid old=new PackingGrid(w,h,new int[0]);
        int freed=old.position(1);w[1]=h[1]=0;PackingGrid next=new PackingGrid(w,h,old.positions());
        assertEquals(freed,next.position(4));assertEquals(-1,next.position(1));assertEquals(old.position(2),next.position(2));
    }
    @Test public void corruptedOverlappingSavedRectanglesNeverOverlapAfterRepair(){
        int[] w=sizes(27,3),h=sizes(27,2),bad=new int[27];
        PackingGrid grid=new PackingGrid(w,h,bad);noOverlaps(grid,w);
    }
    @Test public void randomLayoutsStayBoundedDisjointAndIdempotent(){
        Random random=new Random(35);
        for(int trial=0;trial<1000;trial++){
            int[] w=new int[27],h=new int[27],saved=new int[27];
            for(int i=0;i<27;i++){w[i]=random.nextInt(5);h[i]=w[i]==0?0:1+random.nextInt(4);saved[i]=random.nextInt(150)-10;}
            PackingGrid grid=new PackingGrid(w,h,saved);noOverlaps(grid,w);
            assertArrayEquals(grid.positions(),new PackingGrid(w,h,grid.positions()).positions());
        }
    }
}
