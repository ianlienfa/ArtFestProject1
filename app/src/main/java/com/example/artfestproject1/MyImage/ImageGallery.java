package com.example.artfestproject1.MyImage;

import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.*;
import org.opencv.imgproc.Imgproc;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;
import android.content.*;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.core.content.ContextCompat;

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
        for(int i = 0; i < imageGallery_height; i++)
        {
            imageGallery[i] = new Mat[imageGallery_height];
            trackingGallery[i] = new ImageStatus[imageGallery_height];
        }

        for(int i = 0; i < imageGallery_width; i++)
        {
            for(int j = 0; j < imageGallery_height; j++)
            {
                // System.out.print("i: "+String.valueOf(i)+" j: "+String.valueOf(j));
                int image_start_x = small_img_width * i, image_start_y = small_img_width * j;
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
        double lottery_brightness = 0.5;
        double win_prob = 0.2;
        int breed_radius = 10;
        boolean infection_effect_on = true;
        boolean breed_effect_on = false;

        // width, height check
        if(image_in.arrayWidth() != image_base.arrayWidth() || image_in.arrayHeight() != image_base.arrayHeight())
        {
            System.out.println("image_in -- w:" + image_in.arrayWidth() + " h: " + image_in.arrayHeight());
            System.out.println("image_base -- w:" + image_base.arrayWidth() + " h: " + image_base.arrayHeight());
            System.out.println("Image size error!");
            System.exit(-2);
        }

        // set the affected pixels


        // change every pixel of the image_in
        for(int w = 0; w < image_in.arrayWidth(); w++)
        {
            for(int h = 0; h < image_in.arrayHeight(); h++)
            {
                // get the brightness of base_image
                UByteIndexer indexer_base = image_base.createIndexer();
                float hsb_base[] = RGBtoHSB(indexer_base.get(w, h, 2),indexer_base.get(w, h, 1),indexer_base.get(w, h, 0), null);
                float brightness = hsb_base[2];

                // Debug
                // System.out.print(String.valueOf(brightness)+ " ");;

                // set this pixel to gray scale and tune its brightness to match the pixel_base
                UByteIndexer indexer_in = image_in.createIndexer();
                float hsb_in[] = RGBtoHSB(indexer_in.get(w, h, 2),indexer_in.get(w, h, 1),indexer_in.get(w, h, 0), null);
                // 修改！！
                brightness = (float)(brightness * 0.2 + hsb_in[2]*0.8);
                int rgb_val = HSBtoRGB(hsb_in[0], hsb_in[1], brightness);
                int r = getR(rgb_val); int g = getG(rgb_val); int b = getB(rgb_val);
                indexer_in.put(w, h, 0, b);
                indexer_in.put(w, h, 1, g);
                indexer_in.put(w, h, 2, r);

                //---- lottery breed ----//
                if(breed_effect_on && brightness < lottery_brightness)
                {
                    int breed_ct = 0, breed_w = 0, breed_h = 0;
                    int direction_w = getDirection();
                    int direction_h = getDirection();
                    while(Math.random() > win_prob && breed_ct < breed_radius)
                    {
                        breed_ct++;
                        breed_w = direction_w + w;
                        breed_h = direction_h + h;
                        int w0 = 0, w1 = image_base.arrayWidth()-1;
                        int h0 = 0, h1 = image_base.arrayHeight()-1;
                        if(breed_w > w1 || breed_w < w0) breed_w = w;
                        if(breed_h > h1 || breed_h < h0) breed_h = h;

                        // set this pixel to gray scale and tune its brightness to match the pixel_base
                        indexer_in.put(breed_w, breed_h, 0, b);
                        indexer_in.put(breed_w, breed_h, 1, g);
                        indexer_in.put(breed_w, breed_h, 2, r);
                    }
                }
                indexer_in.release();
                indexer_base.release();
            }
        }

        if(infection_effect_on)
        { for(int w = 0; w < image_in.arrayWidth(); w++)
        {
            for(int h = 0; h < image_in.arrayHeight(); h++)
            {
                // get base pixel HSB
                UByteIndexer indexer_base = image_base.createIndexer();
                UByteIndexer indexer_in = image_in.createIndexer();
                float hsb_base[] = RGBtoHSB(indexer_base.get(w, h, 2),indexer_base.get(w, h, 1),indexer_base.get(w, h, 0), null);
                float brightness = hsb_base[2];

                // set affect_degree
                int affect_degree = 0;

                // affect_table: 0~20: C, 20~30: B, 30~70: A, 70~80: B, 80~100:C
                if(brightness <= 0.3)
                    affect_degree = degreeEnum.C.value;
                else if(brightness > 0.3 && brightness < 0.5)
                    affect_degree = degreeEnum.A.value;
                else
                    affect_degree = 0;

                // get infection_region
                ImageIndex[] infection_region = infectionRegion(image_base, w, h, affect_degree);

                // set the affected pixels
                for(int i = 0; i < infection_region.length; i++)
                {
                    int inf_w = infection_region[i].x, inf_h = infection_region[i].y;
                    float inf_hsb[] = RGBtoHSB(indexer_base.get(inf_w, inf_h, 2),indexer_base.get(inf_w, inf_h, 1),indexer_base.get(inf_w, inf_h, 0), null);
                    float hsb_in[] = RGBtoHSB(indexer_in.get(inf_w, inf_h, 2),indexer_in.get(inf_w, inf_h, 1),indexer_in.get(inf_w, inf_h, 0), null);
                    int inf_rgb = HSBtoRGB(hsb_in[0], hsb_in[1], (brightness+hsb_in[2])/2);
                    int r = getR(inf_rgb), g = getG(inf_rgb), b = getB(inf_rgb);
                    indexer_in.put(inf_w, inf_h, 0, b);
                    indexer_in.put(inf_w, inf_h, 1, g);
                    indexer_in.put(inf_w, inf_h, 2, r);
                }
                indexer_base.release();
                indexer_in.release();
            }
        }
        }

        for(int w = 0; w < image_in.arrayWidth(); w++) {
            for (int h = 0; h < image_in.arrayHeight(); h++) {
                UByteIndexer indexer_in = image_in.createIndexer();
                float hsb_in[] = RGBtoHSB(indexer_in.get(w, h, 2),indexer_in.get(w, h, 1),indexer_in.get(w, h, 0), null);
                int rgb_val = HSBtoRGB(0, 0, hsb_in[2]);
                int r = getR(rgb_val); int g = getG(rgb_val); int b = getB(rgb_val);
                indexer_in.put(w, h, 0, b);
                indexer_in.put(w, h, 1, g);
                indexer_in.put(w, h, 2, r);
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
                    if (j==10 &&i<50)
                    {
                        System.out.println(indexer_black.get(j, i,3));
                    }
                    int b=(int)((indexer_in.get(j, i,0))* alpha);
                    int g=(int)((indexer_in.get(j, i,1))* alpha);
                    int r=(int)((indexer_in.get(j, i,2))* alpha);
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
        int gridWidth = 30;
        int gridHeight = 30;
        int numCol = width / gridWidth;
        int numRow = height / gridHeight;
        int[][] newR = new int[numRow][numCol];
        int[][] newG = new int[numRow][numCol];
        int[][] newB = new int[numRow][numCol];
        int[][] oldR = new int[height][width];
        int[][] oldG = new int[height][width];
        int[][] oldB = new int[height][width];

        image_out = colorToGray(image_out);

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
                alpha = alpha * 0.6 + 0.4;
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

    public static Mat colorToGray(Mat image_in)
    // 不上面那樣做是因為，要用constructor來使用比較安全
//    public Mat colorToGray(Mat image_in)
    {

        Mat image_out = image_in;
        int width = image_out.arrayWidth();
        int height = image_out.arrayHeight();

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
            }
        }
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