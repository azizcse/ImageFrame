package org.example;

public class Main {
    public static void main(String[] args) {
        // 500 is frame thickness double side
        ImageProcessor obj = new ImageProcessor(500);
        obj.loadImages();
        obj.addImageFrame();
    }
}