package com.sharesc.caliog.npclib;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import net.minecraft.server.v1_8_R1.Entity;
import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R1.PlayerInteractManager;
import net.minecraft.server.v1_8_R1.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.mojang.authlib.GameProfile;

public class NPCManager {

    private final HashMap<String, NPC> npcs = new HashMap<>();
    private final BServer server;
    private final Map<World, BWorld> bworlds = new HashMap<>();
    private NPCNetworkManager npcNetworkManager;
    public static JavaPlugin plugin;

    public NPCManager(JavaPlugin plugin) {
	server = BServer.getInstance();

	try {
	    npcNetworkManager = new NPCNetworkManager();
	} catch (final IOException e) {
	    e.printStackTrace();
	}

	NPCManager.plugin = plugin;

	Bukkit.getServer().getPluginManager().registerEvents(new SL(), plugin);
    }

    public BWorld getBWorld(World world) {
	BWorld bworld = bworlds.get(world);
	if (bworld != null) {
	    return bworld;
	}
	bworld = new BWorld(world);
	bworlds.put(world, bworld);
	return bworld;
    }

    private class SL implements Listener {
	@EventHandler
	public void onPluginDisable(PluginDisableEvent event) {
	    if (event.getPlugin() == plugin) {
		despawnAll();
	    }
	}
    }

    public boolean containsNPC(String name) {
	return npcs.containsKey(name);
    }

    public NPC spawnHumanNPC(String name, Location l) {
	int i = 0;
	String id = name;
	while (npcs.containsKey(id)) {
	    id = name + i;
	    i++;
	}
	return spawnHumanNPC(name, l, id);
    }

    public NPC spawnHumanNPC(String name, Location l, String id) {
	if (npcs.containsKey(id)) {
	    server.getLogger().log(Level.WARNING, "NPC with that id already exists, existing NPC returned");
	    return npcs.get(id);
	}
	if (name.length() > 16) { // Check and nag if name is too long, spawn
				  // NPC anyway with shortened name.
	    final String tmp = name.substring(0, 16);
	    server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
	    server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
	    name = tmp;
	}
	final BWorld world = getBWorld(l.getWorld());
	final NPCEntity npcEntity = new NPCEntity(this, world, new GameProfile(UUID.fromString(id), name), new PlayerInteractManager(
		world.getWorldServer()));
	npcEntity.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
	final HumanNPC npc = new HumanNPC(npcEntity);
	npc.setYaw(l.getYaw());
	world.getWorldServer().addEntity(npcEntity); // the right way
	world.getWorldServer().players.remove(npcEntity);
	NPCUtils.sendPacketNearby(l, new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, (EntityPlayer)npc.getEntity()));
	NPCUtils.sendPacketNearby(l, new PacketPlayOutNamedEntitySpawn((EntityPlayer)npc.getEntity()));
	npcs.put(id, npc);
	return npc;
    }

    public void despawnById(String id) {
	final NPC npc = npcs.get(id);
	if (npc != null) {
	    npcs.remove(id);
	    npc.removeFromWorld();
	}
    }

    public void despawnHumanByName(String npcName) {
	if (npcName.length() > 16) {
	    npcName = npcName.substring(0, 16); // Ensure you can still despawn
	}
	final HashSet<String> toRemove = new HashSet<>();
	for (final String n : npcs.keySet()) {
	    final NPC npc = npcs.get(n);
	    if (npc != null && npc instanceof HumanNPC) {
		if (((HumanNPC) npc).getName().equals(npcName)) {
		    toRemove.add(n);
		    npc.removeFromWorld();
		}
	    }
	}
	for (final String n : toRemove) {
	    npcs.remove(n);
	}
    }

    public void despawnAll() {
	for (final NPC npc : npcs.values()) {
	    if (npc != null) {
		npc.removeFromWorld();
	    }
	}
	npcs.clear();
    }

    public NPC getNPC(String id) {
	return npcs.get(id);
    }

    public boolean isNPC(org.bukkit.entity.Entity e) {
	return ((CraftEntity) e).getHandle() instanceof NPCEntity;
    }

    public List<NPC> getHumanNPCByName(String name) {
	final List<NPC> ret = new ArrayList<>();
	final Collection<NPC> i = npcs.values();
	for (final NPC e : i) {
	    if (e instanceof HumanNPC) {
		if (((HumanNPC) e).getName().equalsIgnoreCase(name)) {
		    ret.add(e);
		}
	    }
	}
	return ret;
    }

    public List<NPC> getNPCs() {
	return new ArrayList<>(npcs.values());
    }

    public String getNPCIdFromEntity(org.bukkit.entity.Entity e) {
	if (e instanceof HumanEntity) {
	    for (final String i : npcs.keySet()) {
		if (npcs.get(i).getBukkitEntity().getEntityId() == ((HumanEntity) e).getEntityId()) {
		    return i;
		}
	    }
	}
	return null;
    }

    public void rename(String id, String name) {
	if (name.length() > 16) { // Check and nag if name is too long, spawn
				  // NPC anyway with shortened name.
	    final String tmp = name.substring(0, 16);
	    server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
	    server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
	    name = tmp;
	}
	final HumanNPC npc = (HumanNPC) getNPC(id);
	npc.setName(name);
	final BWorld b = getBWorld(npc.getBukkitEntity().getLocation().getWorld());
	final WorldServer s = b.getWorldServer();
	try {
	    Method m = s.getClass().getDeclaredMethod("d", new Class[] { Entity.class });
	    m.setAccessible(true);
	    m.invoke(s, npc.getEntity());
	    m = s.getClass().getDeclaredMethod("c", new Class[] { Entity.class });
	    m.setAccessible(true);
	    m.invoke(s, npc.getEntity());
	} catch (final Exception ex) {
	    ex.printStackTrace();
	}
	s.everyoneSleeping();
    }

    public BServer getServer() {
	return server;
    }

    public NPCNetworkManager getNPCNetworkManager() {
	return npcNetworkManager;
    }

}
