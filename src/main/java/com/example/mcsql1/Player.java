package com.example.mcsql1;

import javafx.scene.paint.Color;

public class Player {

    private final MySquare usedBlockIcon;
    private int Id;

    Player(Board board,int Id)
    {
        this.Id=Id;
        usedBlockIcon=new MySquare(23.5,0.5,board);
        usedBlockIcon.setTexture("ladder");
        usedBlockIcon.setStroke(Color.BLACK);
        usedBlockIcon.setStrokeWidth(2);
    }

    public void setNextBlockUsed(String[] blockList){
        String current = usedBlockIcon.getBlockName();
        for (int i=0;i < blockList.length-1 ;i++){
            if(blockList[i].matches(current)){
                usedBlockIcon.setTexture(blockList[i+1]);
                return;
            }
        }
        usedBlockIcon.setTexture("air");
    }

    public MySquare getUsedBLockIcon() {
        return usedBlockIcon;
    }

    public int getID(){
       return Id;
    }

}
