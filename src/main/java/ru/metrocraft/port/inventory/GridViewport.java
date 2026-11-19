package ru.metrocraft.port.inventory;

/** Pixel hit testing and scrolling only: the server still receives original 8x8 cell IDs. */
public final class GridViewport {
    public final int columns,rows,cellSize;
    public int column,row;
    public GridViewport(int columns,int rows,int size){this.columns=Math.max(1,Math.min(8,columns));this.rows=Math.max(1,Math.min(8,rows));cellSize=size;}
    public void scroll(int dx,int dy){column=Math.max(0,Math.min(8-columns,column+dx));row=Math.max(0,Math.min(8-rows,row+dy));}
    public int at(int x,int y){return x<0||y<0||x>=columns*cellSize||y>=rows*cellSize?-1:column+x/cellSize+(row+y/cellSize)*8;}
    public int x(int cell){return (cell%8-column)*cellSize;}
    public int y(int cell){return (cell/8-row)*cellSize;}
}
