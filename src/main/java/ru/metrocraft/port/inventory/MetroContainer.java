package ru.metrocraft.port.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;

public final class MetroContainer extends Container {
    public final InventoryBasic bag;
    public final InventoryBasic nvg,watch,lamp,nativeBag;
    private final EntityPlayer owner;
    private boolean gridTransaction;
    public int gridRevision;
    public final int[] gridPositions=new int[PackingGrid.RECORDS];
    private final ItemStack[] observed=new ItemStack[PackingGrid.RECORDS+1];
    public MetroContainer(EntityPlayer player) {
        owner=player;java.util.Arrays.fill(gridPositions,-1);java.util.Arrays.fill(observed,ItemStack.EMPTY);
        ExtraEquipment.migrateLegacyBackpack(player);
        bag=Backpacks.inventory(player);nvg=NvgEquipment.inventory(player);watch=WatchEquipment.inventory(player);
        lamp=ExtraEquipment.inventory(player,ExtraEquipment.Kind.LAMP);nativeBag=ExtraEquipment.inventory(player,ExtraEquipment.Kind.BLACKRAZID_BACKPACK);
        addSlotToContainer(new Slot(bag,0,24,18) {
            @Override public boolean isItemValid(ItemStack stack) { return false; } // Take-only recovery slot for worlds with two equipped bags.
            @Override public int getSlotStackLimit() { return 1; }
        });
        EntityEquipmentSlot[] armor={EntityEquipmentSlot.HEAD,EntityEquipmentSlot.CHEST,EntityEquipmentSlot.LEGS,EntityEquipmentSlot.FEET};
        for(int i=0;i<4;i++) {
            final EntityEquipmentSlot type=armor[i];
            addSlotToContainer(new Slot(player.inventory,39-i,44,7+i*18) {
                @Override public int getSlotStackLimit(){return 1;}
                @Override public boolean isItemValid(ItemStack stack){return EquipmentRules.allowed(player,type,stack)&&stack.getItem().isValidArmor(stack,type,player);}
                @Override public boolean canTakeStack(EntityPlayer actor){return actor.capabilities.isCreativeMode || !net.minecraft.enchantment.EnchantmentHelper.hasBindingCurse(getStack());}
            });
        }
        addSlotToContainer(new Slot(player.inventory,40,120,61));
        for(int row=0;row<3;row++) for(int col=0;col<9;col++) {
            // Hidden backing records, NOT one network slot per covered grid cell.
            addSlotToContainer(new Slot(player.inventory,9+row*9+col,-10000,-10000) {
                @Override public boolean isItemValid(ItemStack stack){return gridTransaction;}
                @Override public boolean canTakeStack(EntityPlayer player){return gridTransaction;}
                @Override public boolean isEnabled(){return false;}
            });
        }
        for(int i=0;i<9;i++) {
            final int index=i;
            addSlotToContainer(new Slot(player.inventory,i,8+i*18,142){
                @Override public boolean isItemValid(ItemStack stack){return LoadoutRules.accepts(index,stack);}
                @Override public int getSlotStackLimit(){return index<3?1:64;}
            });
        }
        // Append, preserving every existing backpack/armor/inventory slot number.
        addSlotToContainer(new Slot(nvg,0,140,18) {
            @Override public boolean isItemValid(ItemStack stack) {return NvgEquipment.accepts(stack);}
            @Override public int getSlotStackLimit() {return 1;}
            @Override public boolean canTakeStack(EntityPlayer actor) {return actor.capabilities.isCreativeMode || !net.minecraft.enchantment.EnchantmentHelper.hasBindingCurse(getStack());}
        });
        addSlotToContainer(new Slot(watch,0,140,54) {
            @Override public boolean isItemValid(ItemStack stack){return WatchEquipment.accepts(stack);}
            @Override public int getSlotStackLimit(){return 1;}
            @Override public boolean canTakeStack(EntityPlayer actor){return actor.capabilities.isCreativeMode || !net.minecraft.enchantment.EnchantmentHelper.hasBindingCurse(getStack());}
        });
        addSlotToContainer(extraSlot(lamp,ExtraEquipment.Kind.LAMP,184,18));
        addSlotToContainer(extraSlot(nativeBag,ExtraEquipment.Kind.BLACKRAZID_BACKPACK,184,54));
        // Visual layout changes only: retain all 46 network slot IDs and real vanilla equipment.
        position(0,104,104);
        position(1,8,42);position(2,8,73);position(3,8,104);position(4,38,104);position(5,134,104);

        position(33,14,141);position(34,14,178);position(35,134,42);
        for(int i=3;i<9;i++)position(33+i,170+(i-3)*22,184);
        position(42,104,42);position(43,104,73);position(44,134,73);position(45,38,42);
    }
    private void position(int index,int x,int y){
        Slot slot=inventorySlots.get(index);slot.xPos=x;slot.yPos=y;
    }
    private Slot extraSlot(InventoryBasic inventory,ExtraEquipment.Kind kind,int x,int y){
        return new Slot(inventory,0,x,y){
            @Override public boolean isItemValid(ItemStack stack){return ExtraEquipment.accepts(kind,stack);}
            @Override public int getSlotStackLimit(){return 1;}
            @Override public boolean canTakeStack(EntityPlayer actor){return actor.capabilities.isCreativeMode || !net.minecraft.enchantment.EnchantmentHelper.hasBindingCurse(getStack());}
        };
    }
    @Override public boolean canInteractWith(EntityPlayer player){return player==owner && player.isEntityAlive();}
    @Override public ItemStack transferStackInSlot(EntityPlayer player,int index){return ItemStack.EMPTY;}
    @Override public boolean canMergeSlot(ItemStack stack,Slot slot){return slot.inventory!=bag && slot.inventory!=nvg && slot.inventory!=watch && slot.inventory!=lamp && slot.inventory!=nativeBag && slot.isItemValid(stack);}
    @Override public ItemStack slotClick(int index,int drag,ClickType type,EntityPlayer player){
        if(index>=6 && index<=32 && !gridTransaction)return ItemStack.EMPTY;
        if(type==ClickType.SWAP && drag>=0 && drag<9 && index>=0 && index<inventorySlots.size()
                && !LoadoutRules.accepts(drag,inventorySlots.get(index).getStack()))return ItemStack.EMPTY;
        return super.slotClick(index,drag,type,player);}
    public PackingGrid grid(){
        if(!owner.world.isRemote)return GridInventory.snapshot(owner);
        int[] w=new int[27],h=new int[27];
        for(int i=0;i<27;i++){
            ItemStack stack=owner.inventory.getStackInSlot(i+9);
            if(!stack.isEmpty()){w[i]=ItemMetrics.width(stack);h[i]=ItemMetrics.height(stack);}
        }
        return new PackingGrid(w,h,gridPositions);
    }
    @Override public void updateProgressBar(int id,int value){
        if(id>=100 && id<127)gridPositions[id-100]=value;
        else if(id==127)gridRevision=value;
        else super.updateProgressBar(id,value);
    }
    private void sendGrid(IContainerListener listener){
        for(int i=0;i<27;i++)listener.sendWindowProperty(this,100+i,gridPositions[i]);
        listener.sendWindowProperty(this,127,gridRevision);
    }
    @Override public void addListener(IContainerListener listener){
        super.addListener(listener);
        if(!owner.world.isRemote){detectAndSendChanges();sendGrid(listener);}
    }
    @Override public void detectAndSendChanges(){
        super.detectAndSendChanges();if(owner.world.isRemote)return;
        int[] positions=GridInventory.snapshot(owner).positions();
        boolean changed=gridRevision==0 || !java.util.Arrays.equals(positions,gridPositions);
        for(int i=0;i<28;i++){
            ItemStack stack=i==27?owner.inventory.getItemStack():owner.inventory.getStackInSlot(i+9);
            if(!ItemStack.areItemStacksEqual(stack,observed[i])){changed=true;observed[i]=stack.copy();}
        }
        if(changed){
            System.arraycopy(positions,0,gridPositions,0,27);gridRevision=gridRevision%32767+1;
            for(IContainerListener listener:listeners)sendGrid(listener);
        }
    }
    private void resync(net.minecraft.entity.player.EntityPlayerMP player){
        detectAndSendChanges();player.sendContainerToPlayer(this);sendGrid(player);
        player.connection.sendPacket(new net.minecraft.network.play.server.SPacketSetSlot(-1,-1,player.inventory.getItemStack()));
    }
    /** Action 0=grid click, 1=take the first overflow stack. Everything is checked on the server. */
    public void gridClick(net.minecraft.entity.player.EntityPlayerMP player,int window,int revision,int cell,int button,boolean rotated,int action){
        if(player!=owner || player.openContainer!=this || window!=windowId || !canInteractWith(player) || player.isSpectator())return;
        detectAndSendChanges();
        if(revision!=gridRevision || button<0 || button>1 || action<0 || action>1){resync(player);return;}
        if(!gridAction(cell,button,rotated,action))
            player.sendStatusMessage(new net.minecraft.util.text.TextComponentTranslation("metroport.inventory.grid_no_room"),true);
        resync(player);
    }
    // Package-private for transaction tests with a real InventoryPlayer/Container.slotClick implementation.
    boolean gridAction(int cell,int button,boolean rotated,int action){
        if(button<0||button>1||action<0||action>1)return false;
        PackingGrid grid=GridInventory.snapshot(owner);ItemStack cursor=owner.inventory.getItemStack();
        int record=-1,position=-1;
        if(action==1){
            if(!cursor.isEmpty())return false;
            for(int i=0;i<27;i++)if(!owner.inventory.getStackInSlot(i+9).isEmpty() && grid.position(i)<0){record=i;break;}
        }else{
            if(cell<0||cell>=PackingGrid.CELLS)return false;
            record=grid.recordAt(cell);
            if(record<0 && cursor.isEmpty())return true;
            if(record>=0)position=grid.position(record);
            if(!cursor.isEmpty()){
                if(record<0){
                    for(int i=0;i<27;i++)if(owner.inventory.getStackInSlot(i+9).isEmpty()){record=i;break;}
                    position=PackingGrid.encode(cell,rotated);
                }
                if(record<0)return false;
                // Left/right merge into the same stack keeps the current rectangle/orientation.
                ItemStack old=owner.inventory.getStackInSlot(record+9);
                boolean merging=!old.isEmpty() && ItemStack.areItemsEqual(old,cursor) && ItemStack.areItemStackTagsEqual(old,cursor);
                if(!merging){
                    position=PackingGrid.encode(PackingGrid.cell(position),rotated);
                    if(!grid.canPlace(ItemMetrics.width(cursor),ItemMetrics.height(cursor),position,record))return false;
                }
            }
        }
        if(record<0)return false;
        gridTransaction=true;
        try{slotClick(6+record,button,ClickType.PICKUP,owner);}finally{gridTransaction=false;}
        ItemStack placed=owner.inventory.getStackInSlot(record+9);
        if(!placed.isEmpty() && position>=0 && grid.place(record,ItemMetrics.width(placed),ItemMetrics.height(placed),position))GridInventory.save(owner,grid);
        GridInventory.snapshot(owner);owner.inventory.markDirty();return true;
    }
    @Override public void onContainerClosed(EntityPlayer player){super.onContainerClosed(player);Backpacks.enforceCapacity(player);}
}
