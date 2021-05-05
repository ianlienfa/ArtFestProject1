package com.example.artfestproject1.MyImage;

import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.*;
import org.opencv.core.Core;
import org.opencv.imgproc.Imgproc;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Math;
import java.math.BigDecimal;
import java.util.*;
import android.content.*;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.devs.sketchimage.SketchImage;

import static com.example.artfestproject1.MyImage.ImageGallery.testSaveImg;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class ImageGallery{

    /*   variable and dataTypes  */
    private Mat[][] imageGallery;
    private ImageStatus[][] trackingGallery;    // track status of each small img
    private int imageGallery_width;
    private int imageGallery_height;
    public static String DIRPATH = "C:\\Users\\Administrator\\AndroidStudioProjects\\ArtFestProject0\\app\\src\\main\\java\\com\\example\\artfestproject0\\MyImage\\";

    public static class ImageIndex {    // 這個struct用來表示一個照片的編號，如照片(3, 5)就是
        public int x;
        public int y;
        public ImageIndex(){}
        public ImageIndex(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
    }
    enum ImageStatus {
        USER_PRINTED,       // 1
        USER_AVAILABLE,     // 2
        ADMIN_PRINTED,      // 3
        ADMIN_AVAILABLE     // 4
    }


    /*  Methods  */
    public int get_ImageGallery_width() { return imageGallery_width; }
    public int get_ImageGallery_height() { return imageGallery_height; }

    public Mat get_baseImg(int w, int h)
    {
        return imageGallery[w][h];
    }

    public Mat get_baseImg(ImageIndex idx)
    {
        return imageGallery[idx.x][idx.y];
    }

    public ImageStatus get_Imgstatus(ImageIndex idx)
    {
        return trackingGallery[idx.x][idx.y];
    }

    public ImageStatus get_Imgstatus(int w, int h)
    {
        return trackingGallery[w][h];
    }

    public static Mat preparePrintImage(String filename){
        Mat image = imread(filename);
        if (image == null)
        {
            System.out.println("Photo not found.");
            System.exit(-2);
        }
        return image;
    }

    private void partition(Mat image, int small_img_width, int small_img_height)
    {
        // getReal width, height
        int width = image.arrayWidth();
        int height = image.arrayHeight();

        // // should be modified
        // int small_img_width = 108;
        // int small_img_height = 108;

        this.imageGallery_width = width/small_img_width;
        this.imageGallery_height = height/small_img_height;

        // space allocation -- index starts from 1
        imageGallery = new Mat[imageGallery_width][];
        trackingGallery = new ImageStatus[imageGallery_width][];
        for(int i = 0; i < imageGallery_width; i++)
        {
            imageGallery[i] = new Mat[imageGallery_height];
            trackingGallery[i] = new ImageStatus[imageGallery_height];
        }

        for(int i = 0; i < imageGallery_width; i++)
        {
            for(int j = 0; j < imageGallery_height; j++)
            {
                // System.out.print("i: "+String.valueOf(i)+" j: "+String.valueOf(j));
                int image_start_x = small_img_width * i, image_start_y = small_img_height * j;
                Rect rect = new Rect(image_start_x, image_start_y, small_img_width, small_img_height);
                Mat sub = new Mat(image, rect);
                imageGallery[i][j] = sub;

                // Initialize trackingGallery
                trackingGallery[i][j] = ImageStatus.USER_AVAILABLE;

                // Debug
                String destination = (DIRPATH+"["+String.valueOf(i)+"]"+"["+String.valueOf(j)+"]"+".jpg");
                imwrite(destination, sub);
            }
        }
    }
    public static Mat testLoadImg(String filename)
    {
//        File Images = new File("MyImage");
//        if(!Images.exists())
//        {
//            Images.mkdir();
//        }
        String temp = "/Users/linenyan/Coding/ArtFestProject1/app/src/main/java/com/example/artfestproject1/MyImage";
        String from = (temp+File.separator+filename);
        System.out.println("from: "+from);
//        Log.d("Destination", from);
        Mat image = imread(from);
        if(image == null)
        {
            System.out.println("stdLoadImg error: img is null.");
            System.exit(-1);
        }
        return image;
    }

    public static void testSaveImg(String filename, Mat mat)
    {
//        File Images = new File("MyImage");
//        if(!Images.exists())
//        {
//            Images.mkdir();
//        }
        String temp = "/Users/linenyan/Coding/ArtFestProject1/app/src/main/java/com/example/artfestproject1/MyImage";
        String to = (temp+File.separator+filename);
        System.out.println("to: "+to);
//        Log.d("Destination", from);
        imwrite(to, mat);
    }

    public static Mat stdLoadImg(String filename, Context context)
    {
        File Images = imageDirFile(context);
        if(!Images.exists())
        {
            Images.mkdir();
        }
        String from = (Images.getAbsolutePath()+File.separator+filename);
        Log.d("Destination", from);
        Mat image = imread(from);
        if(image == null)
        {
            System.out.println("stdLoadImg error: img is null.");
            System.exit(-1);
        }
        return image;
    }

    public static void stdSaveImg(Mat img, String filename, Context context)
    {
        File Images = imageDirFile(context);
        if(!Images.exists())
        {
            Images.mkdir();
        }
        String destination = (Images.getAbsolutePath()+File.separator+filename);
        Log.d("Destination", destination);
        if(img == null)
        {
            System.out.println("stdSaveImg error: img is null.");
            System.exit(-1);
        }
        imwrite(destination, img);
    }

    public void userSendprint()
    {
//        update()
//        sendPrint()
//        update()
        // update trackingGallery
    }

    public void adminSendPrint()
    {
        // update trackingGallery
    }

    public void recovery(ImageIndex idx)
    {
        //
    }


    public ImageIndex[] readImageIndex(String filename)
    {
        Vector<ImageIndex>
                vector = new Vector<ImageIndex>();

        try {
            File f = new File(filename);
            Scanner scan = new Scanner(f);

            while (scan.hasNextLine()) {

                ImageIndex idx = new ImageIndex();
                String line = scan.nextLine();
                Scanner stringParse = new Scanner(line);
                if(stringParse.hasNextInt())
                {
                    idx.x = stringParse.nextInt();
                    if(stringParse.hasNextInt())
                    {
                        idx.y = stringParse.nextInt();
                    }
                    else{System.out.println("Index file format error."); System.exit(-1);}

                    // insert into vector
                    vector.add(idx);
                }
                else{System.out.println("Index file format error."); System.exit(-1);}
                stringParse.close();
            }
            scan.close();

        } catch (FileNotFoundException e) {

            System.out.println("File not found.");
            e.printStackTrace();
        }

        ImageIndex[] idxes = vector.toArray(new ImageIndex[vector.size()]);
        for(int i = 0; i < idxes.length; i++)
        {
            if(idxes[i].x >= this.imageGallery_width || idxes[i].y >= imageGallery_height)
            {
                System.out.println(filename+": index outof bound.");
                System.exit(-1);
            }
            // debug
            // System.out.println("x: "+String.valueOf(idxes[i].x)+" y: "+String.valueOf(idxes[i].y));
        }
        return idxes;
    }


    // 我們可以用這個function保留自己列印的照片編號，傳入值是一個照片編號的array
    public void adminPartitionsUpdate(String filename)
    {
        filename = DIRPATH+filename;
        ImageIndex[] idxes = readImageIndex(filename);
        for(int i = 0; i < idxes.length; i++)
        {
            trackingGallery[idxes[i].x][idxes[i].y] = ImageStatus.ADMIN_AVAILABLE;
        }
        // update Disk
    }

    public enum degreeEnum
    {
        A(1),B(2),C(2);

        private int value;
        degreeEnum(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    public static Mat setRGB(Mat mat, int col, int row, int r, int g, int b) //效率差，不推薦使用
    {
        UByteIndexer indexer = mat.createIndexer();
        // imageBuffer.put(row, col, (b: 0, g:0, r:0), rgb量)
        indexer.put(row, col, 0, b);
        indexer.put(row, col, 1, g);
        indexer.put(row, col, 2, r);
        indexer.release();
        return mat;
    }

    private ImageIndex[] infectionRegion(Mat image_base, int w, int h, int affect_degree)
    {
        Vector<ImageIndex> imgidx = new Vector<ImageIndex>();

        // safe region
        int w0 = 0, w1 = image_base.arrayWidth()-1;
        int h0 = 0, h1 = image_base.arrayHeight()-1;

        // set up safe bound
        int w_up = (w+affect_degree > w1) ? w1 : w+affect_degree;
        int w_down = (w-affect_degree < w0)? w0 : w-affect_degree;
        int h_up = (h+affect_degree > h1) ? h1 : h+affect_degree;
        int h_down = (h-affect_degree < h0) ? h0 : h-affect_degree;

        for(int i = w_down; i <= w_up; i++)
        {
            if(i == w)continue;
            for(int j = h_down; j <= h_up; j++)
            {
                if(j == h)continue;
                ImageIndex idx = new ImageIndex(i, j);
                imgidx.add(idx);
            }
        }

        ImageIndex[] idxes = imgidx.toArray(new ImageIndex[imgidx.size()]);
        return idxes;
    }

    private int getDirection()
    {
        double rand = Math.random();
        if(rand < 0.3)
            return 0;
        else if(rand < 0.6)
            return -1;
        else return 1;
    }


    private static int convert(float red, float green, float blue, float alpha)
    {
        if (red < 0 || red > 1 || green < 0 || green > 1 || blue < 0 || blue > 1
                || alpha < 0 || alpha > 1)
            throw new IllegalArgumentException("Bad RGB values");
        int redval = Math.round(255 * red);
        int greenval = Math.round(255 * green);
        int blueval = Math.round(255 * blue);
        int alphaval = Math.round(255 * alpha);
        return (alphaval << 24) | (redval << 16) | (greenval << 8) | blueval;
    }

    public static int HSBtoRGB(float hue, float saturation, float brightness)
    {
        if (saturation == 0)
            return convert(brightness, brightness, brightness, 0);
        if (saturation < 0 || saturation > 1 || brightness < 0 || brightness > 1)
            throw new IllegalArgumentException();
        hue = hue - (float) Math.floor(hue);
        int i = (int) (6 * hue);
        float f = 6 * hue - i;
        float p = brightness * (1 - saturation);
        float q = brightness * (1 - saturation * f);
        float t = brightness * (1 - saturation * (1 - f));
        switch (i)
        {
            case 0:
                return convert(brightness, t, p, 0);
            case 1:
                return convert(q, brightness, p, 0);
            case 2:
                return convert(p, brightness, t, 0);
            case 3:
                return convert(p, q, brightness, 0);
            case 4:
                return convert(t, p, brightness, 0);
            case 5:
                return convert(brightness, p, q, 0);
            default:
                throw new InternalError("impossible");
        }
    }

    public static float[] RGBtoHSB(int red, int green, int blue, float array[])
    {
        if (array == null)
            array = new float[3];
        // Calculate brightness.
        int min;
        int max;
        if (red < green)
        {
            min = red;
            max = green;
        }
        else
        {
            min = green;
            max = red;
        }
        if (blue > max)
            max = blue;
        else if (blue < min)
            min = blue;
        array[2] = max / 255f;
        // Calculate saturation.
        if (max == 0)
            array[1] = 0;
        else
            array[1] = ((float) (max - min)) / ((float) max);
        // Calculate hue.
        if (array[1] == 0)
            array[0] = 0;
        else
        {
            float delta = (max - min) * 6;
            if (red == max)
                array[0] = (green - blue) / delta;
            else if (green == max)
                array[0] = 1f / 3 + (blue - red) / delta;
            else
                array[0] = 2f / 3 + (red - green) / delta;
            if (array[0] < 0)
                array[0]++;
        }
        return array;
    }

    public static int getR(int pixel) {
        return (pixel >> 16) & 0xff;
    }

    public static int getG(int pixel) {
        return (pixel >> 8) & 0xff;
    }

    public static int getB(int pixel) {
        return (pixel >> 0) & 0xff;
    }

    public Mat algorithm_BAI(Mat image_in, Mat image_base)
    {
//        Log.d("size", "in, cols: " + Integer.toString(image_in.cols()) + "rows: " + Integer.toString(image_in.rows()));
//        Log.d("size", "base, cols: " + Integer.toString(image_base.cols()) + "rows: " + Integer.toString(image_base.cols()));
        double lottery_brightness = 0.3;
        double win_prob = 0.1;
        int breed_radius = 10;
        boolean infection_effect_on = true;
        boolean breed_effect_on = true;

        // width, height check
        if(image_in.arrayWidth() != image_base.arrayWidth() || image_in.arrayHeight() != image_base.arrayHeight())
        {
            System.out.println("image_in -- w:" + image_in.arrayWidth() + " h: " + image_in.arrayHeight());
            System.out.println("image_base -- w:" + image_base.arrayWidth() + " h: " + image_base.arrayHeight());
            System.out.println("Image size error!");
            System.exit(-2);
        }

        // set the affected pixels

        // ==============================
        image_in = colorToGray(image_in);
//        image_in = anotherDoSomeMagic(image_in);
        image_in = doSomeMagic(image_in);
        // ==============================

//        System.out.println("image_in -- w:" + image_in.arrayWidth() + " h: " + image_in.arrayHeight());
//        System.out.println("image_base -- w:" + image_base.arrayWidth() + " h: " + image_base.arrayHeight());

        // change every pixel of the image_in
        for(int col = 0; col < image_in.arrayWidth(); col++)
        {
            for(int row = 0; row < image_in.arrayHeight(); row++)
            {
                // get the brightness of base_image
                UByteIndexer indexer_base = image_base.createIndexer();
                // get(row, col
//                System.out.println("col, row: (" + Integer.toString(col)+", " + Integer.toString(row) + ")"+ Integer.toString(indexer_base.get(row, col , 2))+ Integer.toString(indexer_base.get(row, col , 1)) + Integer.toString(indexer_base.get(row, col , 0)));
                float hsb_base[] = RGBtoHSB(indexer_base.get(row, col, 2),indexer_base.get(row, col, 1),indexer_base.get(row, col , 0), null);
                float brightness = hsb_base[2];

                // set this pixel to gray scale and tune its brightness to match the pixel_base
                UByteIndexer indexer_in = image_in.createIndexer();
                float hsb_in[] = RGBtoHSB(indexer_in.get(row, col, 2),indexer_in.get(row, col, 1),indexer_in.get(row, col, 0), null);
                hsb_in[2] = hsb_in[2] * (float)0.2;
                // 修改！！
                brightness = (float)((brightness * 0.8 + hsb_in[2] > 1.0 || brightness * 0.8 + hsb_in[2] < 0.3) ? (brightness) : (brightness * 0.8 + hsb_in[2]));
//                System.out.println(brightness);
                int rgb_val = HSBtoRGB(hsb_in[0], hsb_in[1], brightness);
                int r = getR(rgb_val); int g = getG(rgb_val); int b = getB(rgb_val);
                indexer_in.put(row, col, 0, b);
                indexer_in.put(row, col, 1, g);
                indexer_in.put(row, col, 2, r);

                //---- lottery breed ----//
                if(breed_effect_on && brightness < lottery_brightness)
                {
                    int breed_ct = 0, breed_w = 0, breed_h = 0;
                    int direction_w = getDirection();
                    int direction_h = getDirection();
                    while(Math.random() > win_prob && breed_ct < breed_radius)
                    {
                        breed_ct++;
                        breed_w = direction_w + col;
                        breed_h = direction_h + row;
                        int w0 = 0, w1 = image_base.arrayWidth()-1;
                        int h0 = 0, h1 = image_base.arrayHeight()-1;
                        if(breed_w > w1 || breed_w < w0) breed_w = col;
                        if(breed_h > h1 || breed_h < h0) breed_h = row;

                        // set this pixel to gray scale and tune its brightness to match the pixel_base
                        indexer_in.put(breed_h, breed_w, 0, b);
                        indexer_in.put(breed_h, breed_w, 1, g);
                        indexer_in.put(breed_h, breed_w, 2, r);
                    }
                }
                indexer_in.release();
                indexer_base.release();
            }
        }

//        System.out.println("pixel_count" + Integer.valueOf(pixel_ct).toString());

        if(infection_effect_on)
        {
            for(int col = 0; col < image_in.arrayWidth(); col++)
            {
                for(int row = 0; row < image_in.arrayHeight(); row++)
                {
                // get base pixel HSB
                UByteIndexer indexer_base = image_base.createIndexer();
                UByteIndexer indexer_in = image_in.createIndexer();
                float hsb_base[] = RGBtoHSB(indexer_base.get(row, col, 2),indexer_base.get(row, col, 1),indexer_base.get(row, col, 0), null);
                float brightness = hsb_base[2];

                // set affect_degree
                int affect_degree = 0;

                // affect_table: 0~20: C, 20~30: B, 30~70: A, 70~80: B, 80~100:C
                if(brightness <= 0.3)
                    affect_degree = degreeEnum.C.value;
//                else if(brightness > 0.1 && brightness < 0.5)
//                    affect_degree = degreeEnum.A.value;
                else
                    affect_degree = 0;

                // get infection_region
                ImageIndex[] infection_region = infectionRegion(image_base, col, row, affect_degree);

                // set the affected pixels
                for(int i = 0; i < infection_region.length; i++)
                {
                    int inf_w = infection_region[i].x, inf_h = infection_region[i].y;
                    float inf_hsb[] = RGBtoHSB(indexer_base.get(inf_w, inf_h, 2),indexer_base.get(inf_w, inf_h, 1),indexer_base.get(inf_w, inf_h, 0), null);
                    float hsb_in[] = RGBtoHSB(indexer_in.get(inf_w, inf_h, 2),indexer_in.get(inf_w, inf_h, 1),indexer_in.get(inf_w, inf_h, 0), null);
                    int inf_rgb = HSBtoRGB(hsb_in[0], hsb_in[1], (brightness+hsb_in[2])/2);
                    int r = getR(inf_rgb), g = getG(inf_rgb), b = getB(inf_rgb);
                    indexer_in.put(inf_h, inf_w, 0, b);
                    indexer_in.put(inf_h, inf_w, 1, g);
                    indexer_in.put(inf_h, inf_w, 2, r);
                }
                indexer_base.release();
                indexer_in.release();
            }
        }
        }

        for(int col = 0; col < image_in.arrayWidth(); col++)
        {
            for(int row = 0; row < image_in.arrayHeight(); row++)
            {
                UByteIndexer indexer_in = image_in.createIndexer();
                float hsb_in[] = RGBtoHSB(indexer_in.get(row, col, 2),indexer_in.get(row, col, 1),indexer_in.get(row, col, 0), null);
                int rgb_val = HSBtoRGB(0, 0, hsb_in[2]);
                int r = getR(rgb_val); int g = getG(rgb_val); int b = getB(rgb_val);
                indexer_in.put(row, col, 0, b);
                indexer_in.put(row, col, 1, g);
                indexer_in.put(row, col, 2, r);
                indexer_in.release();
            }
        }
        return image_in;
    }

    //
////    public BufferedImage colorToGray1(BufferedImage image_in)
////    {
////
////        BufferedImage image_out = image_in;
////        int width = image_out.getWidth();
////        int height = image_out.getHeight();
////
////        for (int y = 0; y < height; y++) {
////            for (int x = 0; x < width; x++) {
////                int pixel = image_out.getRGB(x, y);
////                int a = getA(pixel);
////                int r = getR(pixel);
////                int g = getG(pixel);
////                int b = getB(pixel);
////                int average = (int)(0.2989*r + 0.5870*g + 0.1140*b);
////                pixel = getPixel(a, average, average, average);
////                image_out.setRGB(x, y, pixel);
////            }
////        }
////
////        return image_out;
////
////    }
////
    public Mat algorithm_shiuan(Mat image_in, Mat image_base)
    {

        // ==============================
        image_in = colorToGray(image_in);
//        image_in = anotherDoSomeMagic(image_in);
        image_in = doSomeMagic(image_in);
        // ==============================

        Mat image_out, image_black;
        opencv_imgproc.cvtColor(image_in, image_in, opencv_imgproc.COLOR_BGR2GRAY);
        opencv_imgproc.cvtColor(image_base, image_base, opencv_imgproc.COLOR_BGR2GRAY);
        image_out=image_in;
        image_black=image_base;
        int width = image_in.cols();
        System.out.println(width);
        int height = image_in.rows();
        opencv_imgproc.cvtColor(image_in, image_in, opencv_imgproc.COLOR_BGR2BGRA);
        opencv_imgproc.cvtColor(image_black, image_black, opencv_imgproc.COLOR_BGR2BGRA);

        for (int j = 0; j < height; j++) {
            for (int i = 0; i <width; i++) {
                if ((i+j)%2==0) {
                    UByteIndexer indexer_in =image_in.createIndexer();
                    UByteIndexer indexer_black =image_black.createIndexer();
                    UByteIndexer indexer_out =image_out.createIndexer();
                    double alpha = (indexer_black.get(j, i,1))/255.0;
                    //double alpha = (indexer_black.get(j, i,1));
                    if (j==10 &&i<50)
                    {
                        System.out.println(indexer_black.get(j, i,3));
                    }
                    //int b=(int)((indexer_in.get(j, i,0))* alpha);
                    //int g=(int)((indexer_in.get(j, i,1))* alpha);
                    //int r=(int)((indexer_in.get(j, i,2))* alpha);
                    int b=(int)((indexer_in.get(j, i,0))* 0.3+indexer_black.get(j, i,1)*0.7);
                    int g=(int)((indexer_in.get(j, i,1))* 0.3+indexer_black.get(j, i,1)*0.7);
                    int r=(int)((indexer_in.get(j, i,2))* 0.3+indexer_black.get(j, i,1)*0.7);
                    indexer_out.put(j, i, 0, b);
                    indexer_out.put(j, i, 1, g);
                    indexer_out.put(j, i, 2, r);
                    indexer_out.release();
                    indexer_in.release();
                    indexer_black.release();

                }
                else
                {
                    UByteIndexer indexer_in =image_in.createIndexer();
                    UByteIndexer indexer_out =image_out.createIndexer();
                    int b=(int)(indexer_in.get(j, i,0));
                    int g=(int)(indexer_in.get(j, i,1));
                    int r=(int)(indexer_in.get(j, i,2));
                    indexer_out.put(j, i, 0, b);
                    indexer_out.put(j, i, 1, g);
                    indexer_out.put(j, i, 2, r);
                    indexer_out.release();
                    indexer_in.release();
                }

            }
        }


        return image_out;
    }

    // ==============================

    public Mat algorithm_Tim(Mat image_in, Mat image_base)
    {

        Mat image_out = image_base;
        int width = image_out.arrayWidth();
        int height = image_out.arrayHeight();
        // TODO: 值可以更改
        int gridWidth = 1;
        int gridHeight = 1;
        int numCol = width / gridWidth;
        int numRow = height / gridHeight;
        int[][] newR = new int[numRow][numCol];
        int[][] newG = new int[numRow][numCol];
        int[][] newB = new int[numRow][numCol];
        int[][] oldR = new int[height][width];
        int[][] oldG = new int[height][width];
        int[][] oldB = new int[height][width];

//        image_out = colorToGray(image_out);
        image_in = colorToGray(image_in);
//        image_in = anotherDoSomeMagic(image_in);
        image_in = doSomeMagic(image_in);

        // 先把 pixel 資訊存到一個 2D array

        UByteIndexer indexer = image_out.createIndexer();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                oldR[y][x] = indexer.get(y, x, 2);
                oldG[y][x] = indexer.get(y, x, 1);
                oldB[y][x] = indexer.get(y, x, 0);
            }
        }

        // 算每格各別的平均

        for (int y = 0; y < numRow; y++) {
            for (int x = 0; x < numCol; x++) {
                // 在每個小格子裡面
                int tempR = 0;
                int tempG = 0;
                int tempB = 0;
                for (int j = 0; j < gridHeight; j++) {
                    for (int i = 0; i < gridWidth; i++) {
                        int row = gridHeight * y + j;
                        int col = gridWidth * x + i;
                        tempR += oldR[row][col];
                        tempG += oldG[row][col];
                        tempB += oldB[row][col];
                    }
                }
                newR[y][x] = tempR / (gridHeight * gridWidth);
                newG[y][x] = tempG / (gridHeight * gridWidth);
                newB[y][x] = tempB / (gridHeight * gridWidth);
            }
        }


        // 把求出來的 set 回去

        for (int y = 0; y < numRow; y++) {
            for (int x = 0; x < numCol; x++) {
                // 在每個小格子裡面
                for (int j = 0; j < gridHeight; j++) {
                    for (int i = 0; i < gridWidth; i++) {
                        int row = gridHeight * y + j;
                        int col = gridWidth * x + i;
                        indexer.put(row, col, 0, newB[y][x]);
                        indexer.put(row, col, 1, newG[y][x]);
                        indexer.put(row, col, 2, newR[y][x]);
                    }
                }

            }
        }

        // 疊加使用者照片

        UByteIndexer indexer_in = image_in.createIndexer();

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                double alpha = indexer.get(j, i, 2) / 255.0;
                // TODO:
                alpha = alpha * 0.9 + 0.1;
                int r = (int)(indexer_in.get(j, i, 2) * alpha);
                int g = (int)(indexer_in.get(j, i, 1) * alpha);
                int b = (int)(indexer_in.get(j, i, 0) * alpha);
                indexer.put(j, i, 0, b);
                indexer.put(j, i, 1, g);
                indexer.put(j, i, 2, r);
            }
        }

        indexer.release();
        indexer_in.release();

        return image_out;

    }

    public static Mat doSomeMagic(Mat image_in)
    {
        Mat image_out = image_in;
        int width = image_out.arrayWidth();
        int height = image_out.arrayHeight();

        UByteIndexer indexer = image_out.createIndexer();

        int average_intensity = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int intensity = indexer.get(y, x, 2);
                average_intensity += intensity;
            }
        }
        average_intensity /= (height * width);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width-1; x++) {
                double left = indexer.get(y, x, 2);
//                int right = indexer.get(y, x+1, 2);
//                int threshold = 5;
                double threshold = average_intensity * 0.9;
                Log.d("left", String.valueOf(left));
//                Log.d("threshold", String.valueOf(threshold));
                if (left <= threshold) {
                    double magic = left / threshold * 130;
                    indexer.put(y, x, 0, (int)magic);
                    indexer.put(y, x, 1, (int)magic);
                    indexer.put(y, x, 2, (int)magic);
                } else {
//                    indexer.put(y, x, 0, 255);
//                    indexer.put(y, x, 1, 255);
//                    indexer.put(y, x, 2, 255);
                }
//                if (left-right > threshold || left-right < -threshold) {
//                    indexer.put(y, x, 0, 100);
//                    indexer.put(y, x, 1, 100);
//                    indexer.put(y, x, 2, 100);
//                } else {
////                    indexer.put(y, x, 0, 255);
////                    indexer.put(y, x, 1, 255);
////                    indexer.put(y, x, 2, 255);
//                }

            }
        }

        indexer.release();

        return image_out;

    }

    // https://www.youtube.com/watch?v=vS2ubdiAXvg
    // (failed.....)
    public static Mat anotherDoSomeMagic(Mat image_in)
    {
        Mat image_out = image_in;
        int width = image_out.arrayWidth();
        int height = image_out.arrayHeight();

        UByteIndexer indexer = image_out.createIndexer();

        // Invert the image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int intensity = indexer.get(y, x, 2);
                indexer.put(y, x, 0, 255 - intensity);
                indexer.put(y, x, 1, 255 - intensity);
                indexer.put(y, x, 2, 255 - intensity);
            }
        }

        // Blur the image by Gaussian function
        // https://stackoverflow.com/questions/20753130/opencvandroid-imgproc-gaussianblur-application-stopped
        opencv_imgproc.GaussianBlur(image_out, image_out, new Size(3, 3), 0);

        // Invert the blurred iamge
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int intensity = indexer.get(y, x, 2);
                indexer.put(y, x, 0, 255 - intensity);
                indexer.put(y, x, 1, 255 - intensity);
                indexer.put(y, x, 2, 255 - intensity);
            }
        }

        // Create the pencil sketch image
//        Mat output = new Mat();
//        Core.divide(image_in, image_out);
        UByteIndexer indexer0 = image_in.createIndexer();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
//                float threshold = 5;
                double intensity0 = (double)indexer0.get(y, x, 2)*1.0;
                double intensity = (double)indexer.get(y, x, 2)*1.0;
//                if (intensity0-intensity > threshold || intensity0-intensity < -threshold) {
//                    indexer.put(y, x, 0, 0);
//                    indexer.put(y, x, 1, 0);
//                    indexer.put(y, x, 2, 0);
//                } else {
//                    indexer.put(y, x, 0, 255);
//                    indexer.put(y, x, 1, 255);
//                    indexer.put(y, x, 2, 255);
//                }
//                indexer.put(y, x, 0, 200);
//                indexer.put(y, x, 1, 200);
//                indexer.put(y, x, 2, 200);
                double wow = intensity / intensity0 * 256.0;
                indexer.put(y, x, 0, (int)wow);
                indexer.put(y, x, 1, (int)wow);
                indexer.put(y, x, 2, (int)wow);
//                Log.d("image", String.valueOf(((int) (intensity0 * 256.0 / intensity))));
//                Log.d("image", String.valueOf(( (intensity0 * 256.0 / intensity))));
                Log.d("image", String.valueOf(intensity));
                Log.d("image0", String.valueOf(intensity0));
                Log.d("image1", String.valueOf(intensity/intensity0));
//                Log.d("image0", String.valueOf(intensity0));
//                Log.d("image0", String.valueOf(intensity-intensity0));
//                float a = intensity-intensity0;
//                float test = (float) a/intensity;
//                Log.d("image1", String.valueOf(test));
            }
        }

        indexer0.release();
        indexer.release();

        return image_out;
    }

    public static Mat colorToGray(Mat image_in)
    // 不上面那樣做是因為，要用constructor來使用比較安全
//    public Mat colorToGray(Mat image_in)
    {

        Mat image_out = image_in;
        int width = image_out.arrayWidth();
        int height = image_out.arrayHeight();
        int average_intensity = 0;

        UByteIndexer indexer = image_out.createIndexer();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = indexer.get(y, x, 2);
                int g = indexer.get(y, x, 1);
                int b = indexer.get(y, x, 0);
                int average = (r + g + b) / 3;
                indexer.put(y, x, 0, average);
                indexer.put(y, x, 1, average);
                indexer.put(y, x, 2, average);
                average_intensity += average;
            }
        }
        average_intensity /= (height * width);
        //將照片限制在min100 max200
        int min = 150;
        int max=255;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int intensity = indexer.get(y, x, 0);
                int new_intensity = (int)(((intensity-0)/255.0)*(max-min)+min);
//                indexer.put(y, x, 0, 255);
//                indexer.put(y, x, 1, 255);
//                indexer.put(y, x, 2, 255);
                indexer.put(y, x, 0, new_intensity);
                indexer.put(y, x, 1, new_intensity);
                indexer.put(y, x, 2, new_intensity);
            }
        }
        /*
        // ==============================
        // TODO: 調整整體亮度的數值
        int target_intensity = 200;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int intensity = indexer.get(y, x, 0);
                int new_intensity = intensity * target_intensity / average_intensity + 30;
                new_intensity = new_intensity * 120 / 255 + 30;
                if (new_intensity > 255) {
                    new_intensity = 255;
                }
//                indexer.put(y, x, 0, 255);
//                indexer.put(y, x, 1, 255);
//                indexer.put(y, x, 2, 255);
                indexer.put(y, x, 0, new_intensity);
                indexer.put(y, x, 1, new_intensity);
                indexer.put(y, x, 2, new_intensity);
            }
        }
        // ==============================
*/
        indexer.release();

        return image_out;

    }

    public boolean androidWrite(Mat img)
    {
        return true;
    }

    public static Mat assetsRead(String filename, Context context)
    {
        Mat image = new Mat();
        File file = new File(context.getCacheDir() + "/" + filename);
        if (!file.exists())
            try {

                InputStream is = context.getAssets().open(filename);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();

                FileOutputStream fos = new FileOutputStream(file);

                fos.write(buffer);
                fos.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        if (file.exists()) {
            image = imread(file.getAbsolutePath());
        }
        return image;
    }

    public static String imageDirPath(Context context)
    {
        File Images = new File(context.getFilesDir(), "Images");
        return Images.getAbsolutePath();
    }

    public static File imageDirFile(Context context)
    {
        File Images = new File(context.getFilesDir(), "Images");
        return Images;
    }

    public static void internalImgWrite(String filename, Mat img, Context context)
    {
        File internalEntryPoint = context.getFilesDir();
        if(internalEntryPoint.canWrite())
        {
            // Get into Images
            File imgDir = new File(internalEntryPoint.getAbsolutePath(), "Images");
            if(!imgDir.exists())
            {
                imgDir.mkdir();
            }
            // Write Img
            File imgfile = new File(imgDir, filename);
            String imgfilePath = imgfile.getAbsolutePath();
            Log.d("ImgDir", "imgfilePath: "+imgfilePath);
            if(!imwrite(imgfilePath, img))
            {
                Log.d("Write", "ImageGallery.internalWrite: Unable to write into internal storage.");
            }
            else
            {
                Log.d("Write", "ImageGallery.internalWrite: Write Success.");
            }
        }
        else
        {
            Log.d("Write", "ImageGallery.internalWrite: Unable to write into internal storage.");
        }
    }

    public static Mat internalImgRead(String filename, Context context)
    {
        File internalEntryPoint = context.getFilesDir();
        Mat img = new Mat();
        if(internalEntryPoint.canRead())
        {
            // Get into Images
            File imgDir = new File(internalEntryPoint.getAbsolutePath(), "Images");
            if(!imgDir.exists())
            {
                Log.d("Write", "ImageGallery.internalWrite: Unable to write into internal storage.");
            }
            // Write Img
            File imgfile = new File(imgDir, filename);
            String imgfilePath = imgfile.getAbsolutePath();
            img = imread(imgfilePath);
            if(img.empty())
            {
                Log.d("Write", "ImageGallery.internalWrite: Unable to read into internal storage.");
            }
            else
            {
                Log.d("Write", "ImageGallery.internalWrite: Write Success.");
            }
        }
        else
        {
            Log.d("Write", "ImageGallery.internalWrite: Unable to write into internal storage.");
        }
        return img;
    }

    public static Bitmap internalBitMapRead(String filename, Context context) throws FileNotFoundException
    {
        File internalEntryPoint = context.getFilesDir();
        Bitmap img = null;
        if(internalEntryPoint.canRead())
        {
            // Get into Images
            File imgDir = new File(internalEntryPoint.getAbsolutePath(), "Images");
            if(!imgDir.exists())
            {
                Log.d("Read", "ImageGallery.internalBitMapRead: Unable to read internal storage.");
            }
            // Write Img
            File imgfile = new File(imgDir, filename);
            if(imgfile.exists())
            {
                Log.d("Read", "ImageGallery.internalBitMapRead: File exists");
            }
            else
            {
                Log.d("Read", "ImageGallery.internalBitMapRead: File not found");
                System.exit(1);
            }
            img = BitmapFactory.decodeStream(new FileInputStream(imgfile));
        }
        else
        {
            Log.d("Read", "ImageGallery.internalBitMapRead: Unable to read from internal storage.");
            System.exit(1);
        }
        return img;
    }

    public static void InternalBitMapWrite(Bitmap img, String filename, Context context) throws FileNotFoundException, IOException
    {
        File ImageFile = new File(context.getFilesDir(), "Images");
        File file = new File(ImageFile, filename);
        try (FileOutputStream out = new FileOutputStream(file)) {
            img.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File getImageDir(Context context)
    {
        File filesDir = context.getFilesDir();
        File imgDir = new File(filesDir, "Images");
        return imgDir;
    }

    public static void printImageDir(Context context)
    {
        String imageDirpath = imageDirPath(context);
        File imageDirFile = new File(imageDirpath);
        for(String f: imageDirFile.list())
        {
            System.out.println(f);
            Log.d("ImageDir", f);
        }
    }

    public static Bitmap bitmapCrop(Bitmap map, int startx, int starty, int width, int height)
    {
        return Bitmap.createBitmap(map, startx, starty, width, height);
    }

    public static Mat matCrop(Mat mat, int startx, int starty, int width, int height)
    {
        Rect myROI = new Rect(startx, starty, width, height);
        Mat cropImage = new Mat(mat, myROI);
        return cropImage;
    }

    public static Mat matDuplicate(Mat image)
    {
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
        return dst;
    }


//
//    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
//        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
//        Graphics2D graphics2D = resizedImage.createGraphics();
//        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
//        graphics2D.dispose();
//        return resizedImage;
//    }
//
//    private int getPixel(int a, int r, int g, int b) {
//        return (a << 24) | (r << 16) | (g << 8) | (b << 0);
//    }
//
public static Mat matDuplicateWithPadding(Mat image, int pad)
{
    Mat dst = new Mat(image.rows(), image.cols()*2 + pad, image.type());
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
            indexer_dst.put(i, j+cols+pad, 2, r);
            indexer_dst.put(i, j+cols+pad, 1, g);
            indexer_dst.put(i, j+cols+pad, 0, b);
        }
        for(int j = image.cols(); j <= image.cols()+pad; j++)
        {
            indexer_dst.put(i, j, 2, 1000);
            indexer_dst.put(i, j, 1, 1000);
            indexer_dst.put(i, j, 0, 1000);
        }
    }
    indexer_src.release();
    indexer_dst.release();
    return dst;
}




// ==============================

    public ImageGallery(Mat image, int small_img_width, int small_img_height)
    {
        // read Disk Status

        // do partition and save subImages into BufferedImage[][]
        partition(image, small_img_width, small_img_height);

        // user partitions update
//        adminPartitionsUpdate("1.txt");
    }
}