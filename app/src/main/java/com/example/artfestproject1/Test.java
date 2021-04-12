package com.example.artfestproject1;
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
        System.out.println(image.cols() + " " + image.rows());

        Mat dst = new Mat(image.rows(), image.cols()*2, image.type());
//        image.setTo(0);
        UByteIndexer indexer_dst = dst.createIndexer();
        UByteIndexer indexer_src = image.createIndexer();
        // imageBuffer.put(row, col, (b: 0, g:0, r:0), rgb量)
        for(int i = 0; i < image.rows(); i++)
        {
            for(int j = 0; j < image.cols(); j++)
            {
                int cols = image.cols();
                int r = indexer_src.get(i, j, 2);
                int g = indexer_src.get(i, j, 1);
                int b = indexer_src.get(i, j, 0);
                indexer_dst.put(i, j, 2, r);
                indexer_dst.put(i, j, 1, g);
                indexer_dst.put(i, j, 0, b);
                indexer_dst.put(i, j+cols, 2, r);
                indexer_dst.put(i, j+cols, 1, g);
                indexer_dst.put(i, j+cols, 0, b);
            }

        }
        indexer_src.release();
        indexer_dst.release();
        testSaveImg("rex_db.jpg", dst);


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
