package com.example.mcsql1;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class Board extends AnchorPane {

    String[] handledTextures
        = {"air","stone","dirt","bush","cactus","cloud","emerald","forestgrass","grass","gravel","ice","ladder",
            "mushroom","pumpkin","sand","sawanagrass","snow","tree","water","sandstone","snowlayer","bamboo","player"};

    ArrayList<ImageWithTag> imagePatterns = new ArrayList<>();
    private final int worldID;
    private final int worldSize=2499;
    private int min_X=0;
    private final MySquare[][] mySquares;

    private Player player;

    Board(int worldID, int playerId){
        Scene scene = new Scene(this,1625,975);
        this.worldID=worldID;
        this.mySquares = new MySquare[25][15];
        for (String s:handledTextures) {
            imagePatterns.add(new ImageWithTag(s));
        }
        initMySquares();
        initControls(scene);
        initScreen();
        initPlayer(playerId);
        initSave();
        fullDisplay();
    }

    private void initSave() {
        try {
            min_X = getPlayerPosition();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initPlayer(int playerId) {
        player = new Player(this,playerId);
        getChildren().add(player.getUsedBLockIcon());
    }

    private void initScreen() {
        try {
            for(int i=-3;i<32;i+=5){
                generateChunkIfNull((worldSize+i)%worldSize);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initControls(Scene scene) {
        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case A -> {
                    min_X--;
                    min_X=(min_X+worldSize)%worldSize;
                    System.out.println(min_X);
                    tryGenerateChunks();
                    fastDisplay("Right");
                }
                case D -> {
                    min_X++;
                    min_X=(min_X+worldSize)%worldSize;
                    System.out.println(min_X);
                    tryGenerateChunks();
                    fastDisplay("Left");
                }
                case SPACE -> player.setNextBlockUsed(handledTextures);
                case S -> {
                    try {
                        savePlayerPosition(min_X);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    private void initMySquares() {
        for(int i=0;i<25;i++){
            for(int j=0;j<15;j++){
                mySquares[i][j]=new MySquare(i, j, this);
                getChildren().add(mySquares[i][j]);
            }
        }
    }

    public void fullDisplay(){
        for(int i=1;i<26;i++)
            displayColumn(i);
    }

    public void fastDisplay(String direction)
    {
        if(direction.matches("Left"))
            moveDisplayLeft();
        if(direction.matches("Right"))
            moveDisplayRight();
        int i = direction.matches("Left") ? 25 : 1;
        displayColumn(i);
    }

    private void displayColumn(int i){
        try{
            for(int j=1;j<16;j++) {
                String name = Database.selectOneRow(
                    "SELECT GetBlockView("+worldID+","+
                        ((i+min_X)%worldSize)/125+","+
                        (((i+min_X)%worldSize)%125)+","+j+")");
                mySquares[i - 1][15 - j].setTexture(Objects.requireNonNullElse(name, ""));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void moveDisplayLeft() {

        for(int i=0;i<24;i++) {
            for (int j = 0; j < 15; j++) {
                mySquares[i][j].setTexture(mySquares[i+1][j].getBlockName());
            }
        }
    }

    private void moveDisplayRight() {

        for(int i=24;i > 0;i--) {
            for (int j = 0; j < 15; j++) {
                mySquares[i][j].setTexture(mySquares[i-1][j].getBlockName());
            }
        }
    }

    public void tryGenerateChunks()
    {
        try {
            if(min_X%5==2) {
                generateChunkIfNull((min_X-5+worldSize)%worldSize);
                generateChunkIfNull((min_X+30)%worldSize);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateChunkIfNull(int x) throws SQLException {
        String name = Database.selectOneRow("SELECT GetBlockView("+worldID+","+(x/125)+","+(x%125)+","+7+")");
        if(name==null) {
            generateChunk(x);
        }
    }

    private int getPlayerPosition() throws SQLException {
        String result = Database.selectOneRow("SELECT GetPlayerPos("+worldID+","+player.getID()+")");
        if (result == null) {
            Database.callStatement("Insert Into player_instance(world_id,player_id) Values ("+worldID+","+player.getID()+")");
            System.out.println("new pos");
            return 0;
        }
        System.out.println(result);
        return Integer.parseInt(result);
    }

    private void savePlayerPosition(int x) throws SQLException {
        Database.callStatement("CALL SetPlayerPos("+worldID+","
            + player.getID() +","
            +x+")");
    }

    public void updateBlock(int boardX,int boardY) throws SQLException {
        Database.callStatement("CALL UpdateBlock("+worldID+","
            + (min_X+boardX+1)/125 +","
            + (min_X+boardX+1)%125 +","
            + (15-boardY) +",'"
            +player.getUsedBLockIcon().getBlockName()+"')");
        displayColumn(boardX+1);
    }

    private void generateChunk(int x){
        Database.callStatement("CALL CreateChunk("+worldID+","
            + x +","
            +heightNoise(x -2)+","
            +heightNoise(x -1)+","
            +heightNoise(x)+","
            +heightNoise(x +1)+","
            +heightNoise(x +2)+","
            +tempNoise(x)+","
            +waterNoise(x)+")");
    }

    public ImagePattern findImageWithTag(String tag)
    {
        for (ImageWithTag imagePattern : imagePatterns) {
            if (tag.matches(imagePattern.name))
                return imagePattern.imagePattern;
        }
        return null;
    }

    int heightNoise(int x){
        return (int)(Math.sin((x+randomInt())/20f)*4+8);
    }

    int tempNoise(int x){
        return (int)(Math.sin(((x+randomInt()-180)/180f)*5f)*1.5f+2.5f);
    }

    int waterNoise(int x){
        return (int)(Math.sin((x+randomInt())/180f*5f)*1.5f+2.5f);
    }

    int randomInt(){
        return String.valueOf(worldID).hashCode();
    }
}
