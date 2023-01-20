package com.example.mcsql1;

import javafx.application.Application;
import javafx.stage.Stage;

public class ClientApplication extends Application {

public static int worldId, playerId;

@Override
public void start(Stage stage){

    Board root=new Board(worldId,playerId);
    stage.setTitle("fakecraft");
    stage.setScene(root.getScene());
    stage.show();
}

public static void main(int worldId, int playerId) {
    ClientApplication.worldId=worldId;
    ClientApplication.playerId=playerId;
    launch();
}
}