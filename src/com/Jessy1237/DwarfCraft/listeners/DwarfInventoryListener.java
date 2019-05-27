/*
 * Copyright (c) 2018.
 *
 * DwarfCraft is an RPG plugin that allows players to improve their characters
 * skills and capabilities through training, not experience.
 *
 * Authors: Jessy1237 and Drekryan
 * Original Authors: smartaleq, LexManos and RCarretta
 */

package com.Jessy1237.DwarfCraft.listeners;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import co.kepler.fastcraft.craftgui.GUIFastCraft;
import co.kepler.fastcraft.recipes.CraftingInvWrapper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import com.Jessy1237.DwarfCraft.DwarfCraft;
import com.Jessy1237.DwarfCraft.events.DwarfEffectEvent;
import com.Jessy1237.DwarfCraft.guis.DwarfGUI;
import com.Jessy1237.DwarfCraft.models.DwarfEffect;
import com.Jessy1237.DwarfCraft.models.DwarfEffectType;
import com.Jessy1237.DwarfCraft.models.DwarfPlayer;
import com.Jessy1237.DwarfCraft.models.DwarfSkill;

public class DwarfInventoryListener implements Listener
{

    private DwarfCraft plugin;
    private HashMap<Location, BrewerInventory> stands = new HashMap<>();
    private HashMap<Player, DwarfGUI> dwarfGUIs = new HashMap<>();
    static HashMap<HumanEntity, Integer> amountEffectsFired = new HashMap<>();

    public DwarfInventoryListener( final DwarfCraft plugin )
    {
        this.plugin = plugin;
    }

    @EventHandler( priority = EventPriority.NORMAL )
    public void onFurnaceExtractEvent( FurnaceExtractEvent event )
    {
        Player player = event.getPlayer();
        DwarfPlayer dCPlayer = plugin.getDataManager().find( player );
        ItemStack result = new ItemStack( event.getItemType(), event.getItemAmount() );

        if ( !plugin.getUtil().isWorldAllowed( player.getWorld() ) )
            return;

        for ( DwarfSkill s : dCPlayer.getSkills().values() )
        {
            for ( DwarfEffect e : s.getEffects() )
            {
                if ( e.getEffectType() == DwarfEffectType.SMELT && e.checkInitiator( result ) )
                {
                    final ItemStack output = e.getResult( dCPlayer );

                    DwarfEffectEvent ev = new DwarfEffectEvent( dCPlayer, e, new ItemStack[] { result }, new ItemStack[] { output }, null, null, null, null, null, null, null );
                    plugin.getServer().getPluginManager().callEvent( ev );

                    if ( ev.isCancelled() )
                        return;

                    for ( ItemStack item : ev.getAlteredItems() )
                    {
                        if ( item != null && item.getAmount() > 0 )
                        {
                            if ( item.getType() == result.getType() )
                            {
                                int extraAmount = ( item.getAmount() * event.getItemAmount() ) - event.getItemAmount();
                                int exp = (event.getExpToDrop() / event.getItemAmount() );

                                event.setExpToDrop( exp * ( event.getItemAmount() * extraAmount ) );

                                HashMap<Integer, ItemStack> overflow = player.getInventory().addItem( new ItemStack( item.getType(), extraAmount ) );

                                if ( !overflow.isEmpty() )
                                {
                                    for ( Map.Entry<Integer, ItemStack> overflowSet : overflow.entrySet() )
                                    {
                                        player.getWorld().dropItemNaturally( player.getLocation(), overflowSet.getValue() );
                                    }
                                }

                                player.updateInventory();
                            }
                            else
                            {
                                player.getWorld().dropItemNaturally( player.getLocation(), item );
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean hasItems( ItemStack stack )
    {
        return stack != null && stack.getAmount() > 0;
    }

    private void handleCrafting( InventoryClickEvent event )
    {
        HumanEntity player = event.getWhoClicked();
        ItemStack toCraft = event.getCurrentItem();
        ItemStack toStore = event.getCursor();

        //TODO: This code has bugs in it.. fix me
        //if ( plugin.getServer().getPluginManager().getPlugin( "FastCraft" ) != null )
            //handleFastCraft( event, player, toCraft );

        // Make sure we are actually crafting anything
        if ( player != null && hasItems( toCraft ) )
        {
            // Make sure they aren't duping when repairing tools
            if ( plugin.getUtil().isTool( toCraft.getType() ) )
            {
                CraftingInventory ci = ( CraftingInventory ) event.getClickedInventory();
                if ( ci.getRecipe() instanceof ShapelessRecipe )
                {
                    ShapelessRecipe r = ( ShapelessRecipe ) ci.getRecipe();
                    for ( ItemStack i : r.getIngredientList() )
                    {
                        if ( plugin.getUtil().isTool( i.getType() ) && toCraft.getType() == i.getType() )
                        {
                            return;
                        }
                    }
                }
            }

            if ( event.isShiftClick() )
            {
                DwarfPlayer dCPlayer = plugin.getDataManager().find( ( Player ) player );
                for ( DwarfSkill s : dCPlayer.getSkills().values() )
                {
                    for ( DwarfEffect e : s.getEffects() )
                    {
                        if ( e.getEffectType() == DwarfEffectType.CRAFT && e.checkInitiator( toCraft.getType() ) )
                        {
                            // Shift Click HotFix, checks inv for result item
                            // before and then compares to after to modify the
                            // amount of crafted items.
                            int held = 0;
                            for ( ItemStack i : player.getInventory().all( toCraft.getType() ).values() )
                            {
                                held += i.getAmount();
                            }

                            final ItemStack output = e.getResult( dCPlayer );

                            float modifier = ( float ) output.getAmount() / ( float ) toCraft.getAmount();

                            ItemStack check = null;

                            if ( toCraft.getType() != output.getType() )
                            {
                                check = toCraft;
                                modifier = ( float ) ( output.getAmount() + 1 ) / 1.0f;
                            }
                            DwarfEffectEvent ev = new DwarfEffectEvent( dCPlayer, e, new ItemStack[] { check != null ? new ItemStack( output.getType(), 0, output.getDurability() ) : toCraft }, new ItemStack[] { output }, null, null, null, null, null, null, null );
                            plugin.getServer().getPluginManager().callEvent( ev );

                            if ( ev.isCancelled() )
                                return;

                            player.setCanPickupItems( false );
                            for ( ItemStack item : ev.getAlteredItems() )
                            {
                                if ( item != null )
                                {
                                    if ( item.getAmount() > 0 )
                                    {
                                        int num = ( amountEffectsFired.get( player ) == null ? 0 : amountEffectsFired.get( player ) ) + 1;
                                        amountEffectsFired.put( player, num );
                                        plugin.getServer().getScheduler().runTaskLater( plugin, new ShiftClickTask( plugin, dCPlayer, item, check, held, modifier ), 5 );
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else
            {
                // The items are stored in the cursor. Make sure there's
                // enough
                // space.
                if ( isStackSumLegal( toCraft, toStore ) )
                {
                    DwarfPlayer dCPlayer = plugin.getDataManager().find( ( Player ) event.getWhoClicked() );

                    for ( DwarfSkill s : dCPlayer.getSkills().values() )
                    {
                        for ( DwarfEffect e : s.getEffects() )
                        {
                            if ( e.getEffectType() == DwarfEffectType.CRAFT && e.checkInitiator( toCraft.getType() ) )
                            {
                                final ItemStack output = e.getResult( dCPlayer );

                                DwarfEffectEvent ev = new DwarfEffectEvent( dCPlayer, e, new ItemStack[] { toCraft }, new ItemStack[] { output }, null, null, null, null, null, null, null );
                                plugin.getServer().getPluginManager().callEvent( ev );

                                if ( ev.isCancelled() )
                                    return;

                                player.setCanPickupItems( false );
                                for ( ItemStack item : ev.getAlteredItems() )
                                {
                                    if ( item != null )
                                    {
                                        if ( item.getAmount() > 0 )
                                        {
                                            if ( item.getType() == toCraft.getType() )
                                            {
                                                toCraft.setAmount( item.getAmount() );
                                            }
                                            else
                                            {
                                                player.getLocation().getWorld().dropItemNaturally( player.getLocation(), item );
                                            }
                                        }
                                    }
                                }
                                player.setCanPickupItems( true );
                            }
                        }
                    }
                }
                else
                {
                    event.setCancelled( true );
                }
            }
        }
    }

    private boolean isStackSumLegal( ItemStack a, ItemStack b )
    {
        // See if we can create a new item stack with the combined elements of
        // a
        // and b
        if ( a == null || b == null )
            return true; // Treat null as an empty stack
        else
            return a.getAmount() + b.getAmount() <= a.getType().getMaxStackSize();
    }

    @SuppressWarnings( "unlikely-arg-type" )
    @EventHandler( priority = EventPriority.NORMAL )
    public void onBrewEvent( BrewEvent event )
    {
        if ( !stands.containsKey( event.getBlock() ) )
        {
            stands.put( event.getBlock().getLocation(), event.getContents() );
        }
        else
        {
            stands.remove( event.getBlock().getLocation() );
            stands.put( event.getBlock().getLocation(), event.getContents() );
        }
    }

    @EventHandler( priority = EventPriority.NORMAL )
    public void onInventoryCloseEvent( InventoryCloseEvent event )
    {
        DwarfGUI dwarfGUI = dwarfGUIs.get( event.getPlayer() );

        if ( dwarfGUI == null )
            return;

        dwarfGUI.remove();
        dwarfGUIs.remove( event.getPlayer() );
    }

    public void addDwarfGUI( Player player, DwarfGUI gui )
    {
        gui.init();
        gui.openGUI();
        player.updateInventory();
        dwarfGUIs.put( player, gui );
    }

    @EventHandler( priority = EventPriority.NORMAL )
    public void onInventoryClickEvent( InventoryClickEvent event )
    {
        if ( !plugin.getUtil().isWorldAllowed( event.getWhoClicked().getWorld() ) )
            return;

        DwarfGUI dwarfGUI = dwarfGUIs.get( event.getWhoClicked() );
        if ( dwarfGUI != null )
        {
            if ( dwarfGUI.getInventory().equals( event.getClickedInventory() ) )
            {
                event.setCancelled( true );
                dwarfGUI.click( event );
            }
        }

        if ( event.getClickedInventory() != null && event.getSlotType() == SlotType.RESULT )
        {
            switch ( event.getClickedInventory().getType() )
            {
                case CRAFTING:
                    handleCrafting( event );
                    break;
                case WORKBENCH:
                    handleCrafting( event );
                    break;
                default:
                    break;
            }
        }

        if ( event.getSlotType() == SlotType.CRAFTING && ( event.isLeftClick() || event.isShiftClick() ) && event.getClickedInventory().getHolder() instanceof BrewingStand )
        {
            DwarfPlayer player = plugin.getDataManager().find( ( Player ) event.getWhoClicked() );
            HashMap<Integer, DwarfSkill> skills = player.getSkills();
            ItemStack item = event.getCurrentItem();
            final int amount = item.getAmount();
            BrewingStand block = ( BrewingStand ) event.getInventory().getHolder();
            BrewerInventory inv = check( block.getLocation() );

            // This means brewing has not taken place yet but a player has clicked the result slots of the stand
            if ( inv == null )
                return;

            ItemStack[] stack = inv.getContents();
            if ( stack != null )
            {
                if ( sameInv( stack, block.getInventory() ) )
                {
                    for ( DwarfSkill s : skills.values() )
                    {
                        for ( DwarfEffect effect : s.getEffects() )
                        {
                            if ( effect.getEffectType() == DwarfEffectType.BREW && effect.checkInitiator( item ) )
                            {
                                int newAmount = ( int ) ( amount * effect.getEffectAmount( player ) );

                                DwarfEffectEvent ev = new DwarfEffectEvent( player, effect, new ItemStack[] { item }, new ItemStack[] { new ItemStack( item.getType(), newAmount, item.getDurability() ) }, null, null, null, null, null, block.getBlock(), null );
                                plugin.getServer().getPluginManager().callEvent( ev );

                                if ( ev.isCancelled() )
                                    return;

                                Material ing = null, fuel = null;

                                if ( inv.getIngredient() != null )
                                    ing = inv.getIngredient().getType();
                                if ( inv.getFuel() != null )
                                    fuel = inv.getFuel().getType();

                                for ( ItemStack item1 : ev.getAlteredItems() )
                                {
                                    if ( item1 != null )
                                    {
                                        if ( item1.getAmount() > 0 )
                                        {
                                            for ( int n = 0; n != stack.length; n++ )
                                            {
                                                ItemStack it = stack[n];
                                                if ( it != null )
                                                {
                                                    int i = 1;
                                                    if ( it.getType() != ing && it.getType() != fuel )
                                                    {
                                                        while ( i != item1.getAmount() )
                                                        {
                                                            ItemStack itemstack;
                                                            if ( item1.getType() == it.getType() )
                                                            {
                                                                if ( ( newAmount - i ) < 64 )
                                                                {
                                                                    itemstack = new ItemStack( it.getType(), ( item1.getAmount() - i ), it.getDurability() );
                                                                    i = item1.getAmount();
                                                                }
                                                                else
                                                                {
                                                                    itemstack = new ItemStack( it.getType(), 64, it.getDurability() );
                                                                    i = i + 64;
                                                                }
                                                            }
                                                            else
                                                            {
                                                                itemstack = item1;
                                                            }
                                                            player.getPlayer().getWorld().dropItemNaturally( player.getPlayer().getLocation(), itemstack );
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                stands.remove( block.getLocation() );
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean sameInv( ItemStack[] orig, BrewerInventory new1 )
    {
        for ( int n = 0; n != orig.length; n++ )
        {
            ItemStack i = orig[n];
            if ( i != null )
            {
                if ( new1.contains( i ) && i.getType() == Material.POTION )
                {
                    if ( new1.getItem( n ).getDurability() == i.getDurability() )
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public BrewerInventory check( Location l )
    {
        for ( Location l1 : stands.keySet() )
        {
            if ( l1 != null )
            {
                if ( l1.getX() == l.getX() && l1.getY() == l.getY() && l1.getZ() == l.getZ() )
                {
                    return stands.get( l1 );
                }
            }
        }
        return null;
    }

    public void handleFastCraft( InventoryClickEvent event, HumanEntity player, ItemStack toCraft )
    {
        boolean hasFastCraft = event.getInventory() instanceof CraftingInvWrapper;
        if ( hasFastCraft )
        {
            CraftingInvWrapper inv = ( CraftingInvWrapper ) event.getInventory();
            if ( inv.getType().equals( InventoryType.WORKBENCH ) )
            {
                Player p = ( Player ) player;

                if ( !( p.getOpenInventory().getTopInventory().getHolder() instanceof GUIFastCraft ) )
                    return;

                DwarfPlayer dCPlayer = plugin.getDataManager().find( p );
                for ( DwarfSkill s : dCPlayer.getSkills().values() )
                {
                    for ( DwarfEffect e : s.getEffects() )
                    {
                        if ( e.getEffectType() == DwarfEffectType.CRAFT && e.checkInitiator( toCraft.getType() ) )
                        {
                            ItemStack output = e.getResult( dCPlayer );

                            DwarfEffectEvent ev = new DwarfEffectEvent( dCPlayer, e, new ItemStack[] { toCraft }, new ItemStack[] { output }, null, null, null, null, null, null, null );
                            plugin.getServer().getPluginManager().callEvent( ev );

                            if ( ev.isCancelled() )
                                return;

                            GUIFastCraft gui = ( GUIFastCraft ) p.getOpenInventory().getTopInventory().getHolder();
                            int multiplier = gui.getMultiplier();
                            output.setAmount( output.getAmount() * multiplier );

                            toCraft.setAmount( output.getAmount() ); // Update crafting count

                            Iterator<Recipe> recipes = plugin.getServer().recipeIterator();
                            while ( recipes.hasNext() )
                            {
                                Recipe recipe = recipes.next();

                                if ( recipe instanceof ShapedRecipe )
                                {
                                    ShapedRecipe shaped = ( ShapedRecipe ) recipe;
                                    if ( recipe.getResult().getType().equals( toCraft.getType() ) && recipe.getResult().getDurability() == toCraft.getDurability() )
                                    {
                                        for ( ItemStack stack : shaped.getIngredientMap().values() )
                                        {
                                            if ( stack != null )
                                            {
                                                stack.setAmount( stack.getAmount() * multiplier );
                                                if ( stack.getDurability() == 32767 )
                                                    takeItemByMaterial( p, stack );
                                                else
                                                    p.getInventory().removeItem( stack );
                                            }
                                        }
                                    }
                                    p.updateInventory();
                                }
                                else if ( recipe instanceof ShapelessRecipe )
                                {
                                    ShapelessRecipe shapeless = ( ShapelessRecipe ) recipe;
                                    if ( recipe.getResult().getType().equals( toCraft.getType() ) && recipe.getResult().getDurability() == toCraft.getDurability() )
                                    {
                                        for ( int i = 0; i < shapeless.getIngredientList().size(); i++ )
                                        {
                                            ItemStack stack = shapeless.getIngredientList().get( i );
                                            if ( stack != null )
                                            {
                                                stack.setAmount( stack.getAmount() * multiplier );
                                                p.getInventory().removeItem( stack );
                                            }
                                        }
                                        p.updateInventory();
                                    }
                                }
                            }

                            p.playSound( player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1 );
                            HashMap<Integer, ItemStack> remaining = p.getInventory().addItem( toCraft );
                            p.updateInventory();

                            for ( ItemStack stack : remaining.values() )
                            {
                                player.getWorld().dropItemNaturally( player.getLocation(), stack );
                            }

                            event.setCancelled( true );
                            return;
                        }
                    }
                }
            }
        }
    }

    public void takeItemByMaterial( Player p, ItemStack stack )
    {
        int numToRemove = stack.getAmount();
        ItemStack slot;
        for ( int i = 0; i < p.getInventory().getSize(); i++ )
        {
            if ( p.getInventory().getItem( i ) == null )
                break;
            slot = p.getInventory().getItem( i );
            if ( slot != null && slot.getType().equals( stack.getType() ) )
            {
                if ( slot.getAmount() <= numToRemove )
                {
                    numToRemove -= slot.getAmount();
                    p.getInventory().setItem( i, new ItemStack( Material.AIR ) );
                }
                else
                {
                    slot.setAmount( slot.getAmount() - numToRemove );
                    return;
                }
            }
        }
    }
}

class ShiftClickTask implements Runnable
{

    private DwarfPlayer p;
    private int init;
    private ItemStack item;
    private ItemStack check;
    private float modifier;
    private DwarfCraft plugin;

    public ShiftClickTask( DwarfCraft plugin, DwarfPlayer p, final ItemStack item, ItemStack check, int init, float modifier )
    {
        this.p = p;
        this.item = item;
        if ( check == null )
        {
            this.check = item;
        }
        else
        {
            this.check = check;
        }
        this.init = init;
        this.modifier = modifier;
        this.plugin = plugin;
    }

    @Override
    public void run()
    {
        int held = 0;

        Collection<? extends ItemStack> items = p.getPlayer().getInventory().all( check.getType() ).values();
        // Check inventory count of the item
        for ( ItemStack i : items )
        {
            held += i.getAmount();
        }

        // Checks if one of the effects has modified the amount of items in
        // the
        // players inventory. We want to apply the modifier effects on the
        // Vanilla drops. We dont want them to stack with previous effect
        // modifiers.

        final int difference = held - init;
        if ( modifier > 1 )
        {
            final int amount = plugin.getUtil().randomAmount( ( modifier * difference - difference ) );

            // Added the amount from this effect into the limbo ItemStack

            // Adds the leftover items to the player
            for ( int i = amount; i > 0; i -= item.getMaxStackSize() )
            {
                if ( i > item.getMaxStackSize() )
                {
                    p.getPlayer().getWorld().dropItemNaturally( p.getPlayer().getLocation(), new ItemStack( item.getType(), item.getMaxStackSize(), item.getDurability() ) );
                }
                else
                {
                    p.getPlayer().getWorld().dropItemNaturally( p.getPlayer().getLocation(), new ItemStack( item.getType(), i, item.getDurability() ) );
                }
            }
            // Does nothing when the modifier is 0. Happens when extra items
            // are
            // dropped from furnace events as its not the usual drop.
        }
        else if ( modifier == 0 )
        {

            // Takes away items from the inventory when the shift click crafts
            // more than it should do
        }
        else if ( modifier < 1 )
        {
            int amount = plugin.getUtil().randomAmount( ( difference - modifier * difference ) );
            p.getPlayer().getInventory().removeItem( new ItemStack( item.getType(), amount, item.getDurability() ) );
        }

        // Checks to see if this was the last crafting effect that was fired.
        int num = ( DwarfInventoryListener.amountEffectsFired.get( p.getPlayer() ) == null ? 1 : DwarfInventoryListener.amountEffectsFired.get( p.getPlayer() ) ) - 1;
        if ( num == 0 )
        {
            p.getPlayer().setCanPickupItems( true );
            DwarfInventoryListener.amountEffectsFired.remove( p.getPlayer() );
        }
        else
        {
            DwarfInventoryListener.amountEffectsFired.put( p.getPlayer(), num );
        }
    }
}
