package com.Jessy1237.DwarfCraft.model;

/**
 * Original Authors: smartaleq, LexManos and RCarretta
 */

public class DwarfGreeterMessage
{
    private final String leftClick;
    private final String rightClick;

    public DwarfGreeterMessage( String newLeftClick, String newRightClick )
    {
        this.leftClick = newLeftClick;
        this.rightClick = newRightClick;
    }

    protected String getLeftClickMessage()
    {
        return leftClick;
    }

    protected String getRightClickMessage()
    {
        return rightClick;
    }
}