package com.example.mcsql1;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.sql.SQLException;

public class MySquare extends Rectangle {
    private String blockName;
    private final Board board;
    private final int x;
    private final int y;

    void onClick(){
        System.out.println("clicked on: "+x+" "+y);
        try {
            board.updateBlock(x,y);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setTexture(String textureName) {
            blockName = textureName;
            setFill(board.findImageWithTag(textureName));
    }

    public String getBlockName()
    {
        if(blockName!=null)
        return blockName;
        else return "";
    }

    /** Constructor for buttons that change type of shape to draw*/
    MySquare(double x, double y,Board board){
        super(65*x,65*y,65,65);
        this.board = board;
        this.x=(int)x;
        this.y=(int)y;
        setFill(Color.BLUEVIOLET);
        addEventHandler(MouseEvent.MOUSE_PRESSED, event -> onClick());
    }
}
