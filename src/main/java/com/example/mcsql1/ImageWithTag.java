package com.example.mcsql1;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

import java.io.File;
import java.net.MalformedURLException;

public class ImageWithTag {
    public String name;
    public ImagePattern imagePattern;

    public ImageWithTag(String name){
        try {
            this.name=name;
            Image image = new Image(new File("C:\\Users\\48511\\Desktop\\MCSQL1\\Texture"+name+".jpg").toURI().toURL().toExternalForm());
            imagePattern= new ImagePattern(image);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
