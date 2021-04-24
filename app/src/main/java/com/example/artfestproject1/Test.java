package com.example.artfestproject1;
import android.provider.ContactsContract;

import com.example.artfestproject1.MyImage.*;

import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.opencv.opencv_core.*;

import static com.example.artfestproject1.MyImage.ImageGallery.testSaveImg;

public class Test {
    public static void main(String[] args) {

//        ImageGallery.DIRPATH ="/Users/linenyan/Coding/ArtFestProject0/app/src/main/java/com/example/artfestproject0/MyImage/";
//        ImageGallery.DIRPATH = "/Users/linyanting/Desktop/ArtFestProject0/app/src/main/java/com/example/artfestproject0/MyImage/output.jpg";
//        ImageGallery.DIRPATH="/Users/Administrator/AndroidStudioProjects/ArtFestProject0/app/src/main/java/com/example/artfestproject0/MyImage/output.jpg";
//          System.out.println("hello!");
        Mat image = ImageGallery.testLoadImg("rex.jpg");
//        System.out.println(image.cols() + " " + image.rows());
        ImageGallery imageGallery = new ImageGallery(image, 10, 10);
        Mat img_user = ImageGallery.testLoadImg("img_user.jpg");
        Mat img_base = ImageGallery.testLoadImg("img_base.jpg");
        Mat img_new = imageGallery.algorithm_BAI(img_user, img_base);
        imageGallery.testSaveImg("out.jpg", img_new);
//        Mat image = ImageGallery.testLoadImg("bw.jpg");
//        ImageGallery imageGallery = new ImageGallery(image, 1, 1);
//        System.out.println(Integer.toString(image.cols()) + " " + Integer.toString(image.rows()));
//        UByteIndexer indexer_base = image.createIndexer();
//        int w = 0, h = 0;

        // get(row, col)
//        System.out.println("w, h: (" + Integer.toString(w)+", " + Integer.toString(h) + ")"+ Integer.toString(indexer_base.get(w, h, 2))+ " " + Integer.toString(indexer_base.get(w, h, 1)) + " " +Integer.toString(indexer_base.get(w, h, 0)));
//        w = 0; h = 1;
//        System.out.println("w, h: (" + Integer.toString(w)+", " + Integer.toString(h) + ")"+ Integer.toString(indexer_base.get(w, h, 2))+ " " + Integer.toString(indexer_base.get(w, h, 1)) + " " +Integer.toString(indexer_base.get(w, h, 0)));

//        ImageGallery imageGallery = new ImageGallery(image, 108, 108);
//
////        Mat img_user = ImageGallery.stdLoadImg("1.jpg");
////        Mat img_base = ImageGallery.stdLoadImg("[9][1].jpg");
////        img_new = imageGallery.algorithm_Tim(img_user, img_new);
////        img_new = imageGallery.algorithm_shiuan(img_user, img_new);
//        Mat img_user = ImageGallery.stdLoadImg("[4][7].jpg");
//        Mat img_base = ImageGallery.stdLoadImg("[1][7].jpg");
//        Mat img_new = imageGallery.algorithm_BAI(img_user, img_base);
//        ImageGallery.stdSaveImg(img_new, "output.jpg");

    }
}
