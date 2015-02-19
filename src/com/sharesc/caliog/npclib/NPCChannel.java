package com.sharesc.caliog.npclib;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.EventLoop;

import java.net.SocketAddress;

public class NPCChannel extends AbstractChannel {
    private final ChannelConfig config = new DefaultChannelConfig(this);

    protected NPCChannel(Channel parent) {
	super(parent);
    }

    public ChannelConfig config() {
	this.config.setAutoRead(true);
	return this.config;
    }

    protected void doBeginRead() throws Exception {
    }

    protected void doBind(SocketAddress arg0) throws Exception {
    }

    protected void doClose() throws Exception {
    }

    protected void doDisconnect() throws Exception {
    }

    protected void doWrite(ChannelOutboundBuffer arg0) throws Exception {
    }

    public boolean isActive() {
	return false;
    }

    protected boolean isCompatible(EventLoop arg0) {
	return true;
    }

    public boolean isOpen() {
	return false;
    }

    protected SocketAddress localAddress0() {
	return null;
    }

    public ChannelMetadata metadata() {
	return null;
    }

    protected AbstractChannel.AbstractUnsafe newUnsafe() {
	return null;
    }

    protected SocketAddress remoteAddress0() {
	return null;
    }
}
