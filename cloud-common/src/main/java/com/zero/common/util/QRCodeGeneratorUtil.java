package com.zero.common.util;


import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.sun.xml.internal.ws.util.UtilException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;


/**
 * @Author Zero
 * @Description
 * @Date 2021/6/25 0:36
 * @Since 1.8
 **/
public class QRCodeGeneratorUtil {
    private static Logger logger = LoggerFactory.getLogger(QRCodeGeneratorUtil.class);

    private static final int width = 300;//默认二维码宽度

    private static final int height = 300;//默认二维码高度

    private static final String format = "png";//默认二维码格式

    private static final Map<EncodeHintType, Object> hints = new HashMap<>();//二维码参数

    private static final int logo_width = 60;//默认logo宽度

    private static final int logo_height = 60;//默认logo高度

    private static List<String> SUPPORT_FILE_SUFFIX = new ArrayList<>();

    private static final boolean save = false;

    static{
        hints.put(EncodeHintType.CHARACTER_SET,"utf-8");//字符编码
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);//容错等级L/M/Q/H其中L为最低，H为最高
        hints.put(EncodeHintType.MARGIN,2);//二维码与图片边距
        SUPPORT_FILE_SUFFIX.add("jpg");
        SUPPORT_FILE_SUFFIX.add("png");
    }

    /**
     * 生成 默认尺寸、默认名字 无logo 二维码
     * @param srcUrl
     * @param imgPath
     * @return
     * @throws Exception
     */
    public static String createORCode(String srcUrl, String imgPath) throws Exception {
        String finalPath = createORCode(srcUrl, imgPath, width, height, null);
        return finalPath;
    }

    /**
     * 生成 默认尺寸、默认名字、默认输出到流中 无logo 二维码
     * @param srcUrl
     * @return
     * @throws Exception
     */
    public static byte[] createORCode(String srcUrl) throws Exception {
        byte[] finalPath = createORCode(srcUrl, width, height, null);
        return finalPath;
    }


    /**
     * 生成 指定大小 指定名称 无logo 二维码
     * @param srcUrl
     * @param imgPath
     * @param imgWidth
     * @param imgHeight
     * @param imgName
     * @return
     */
    public static String createORCode(String srcUrl, String imgPath, int imgWidth, int imgHeight, String imgName) throws Exception {
        String orCode = createORCode(srcUrl, imgPath, imgWidth, imgHeight, imgName, null, false);
        return orCode;
    }

    /**
     * 生成 指定大小 指定名称 无logo 二维码 输出到流
     * @param srcUrl
     * @param imgWidth
     * @param imgHeight
     * @param imgName
     * @return
     */
    public static byte[] createORCode(String srcUrl, int imgWidth, int imgHeight, String imgName) throws Exception {
        byte[] orCode = createORCode(srcUrl, imgWidth, imgHeight, imgName, null, false);
        return orCode;
    }


    /**
     * 生成 指定尺寸，默认名称，无logo 的二维码
     * @param srcUrl
     * @param imgPath
     * @param imgWidth
     * @param imgHeight
     * @return
     * @throws WriterException
     * @throws IOException
     */
    public static String createORCode(String srcUrl, String imgPath, int imgWidth, int imgHeight) throws Exception {
        String finalPath = createORCode(srcUrl, imgPath, imgWidth, imgHeight,null, null, false);
        return finalPath;
    }

    /**
     * 生成 指定尺寸，默认名称，无logo 的二维码 输出到流中
     * @param srcUrl
     * @param imgWidth
     * @param imgHeight
     * @return
     * @throws WriterException
     * @throws IOException
     */
    public static byte[] createORCode(String srcUrl, int imgWidth, int imgHeight) throws Exception {
        byte[] finalPath = createORCode(srcUrl, imgWidth, imgHeight,null, null, false);
        return finalPath;
    }

    /**
     * 生成 默认尺寸，默认名称，包含logo 二维码
     * @param srcUrl
     * @param imgPath
     * @param logoPath
     * @param needCompress
     * @return
     */
    public static String createORCodeLogo(String srcUrl, String imgPath, String logoPath, boolean needCompress) throws Exception {
        String orCodeLogo = createORCodeLogo(srcUrl, imgPath, width, height, logoPath, needCompress);
        return orCodeLogo;
    }

    /**
     * 生成 默认尺寸，默认名称，包含logo 二维码 输出到流中
     * @param srcUrl
     * @param logoPath
     * @param needCompress
     * @return
     */
    public static byte[] createORCodeLogo(String srcUrl, String logoPath, boolean needCompress) throws Exception {
        byte[] orCodeLogo = createORCodeLogo(srcUrl, width, height, logoPath, needCompress);
        return orCodeLogo;
    }

    /**
     * 生成 指定大小 默认名称 包含logo 二维码
     * @param srcUrl
     * @param imgPath
     * @param imgWidth
     * @param imgHeight
     * @param logoPath
     * @param needCompress
     * @return
     */
    public static String createORCodeLogo(String srcUrl, String imgPath, int imgWidth, int imgHeight, String logoPath, boolean needCompress) throws Exception {
        String orCodeLogo = createORCodeLogo(srcUrl, imgPath, imgWidth, imgHeight, null, logoPath, needCompress);
        return orCodeLogo;
    }

    /**
     * 生成 指定大小 默认名称 包含logo 二维码 输出到流中
     * @param srcUrl
     * @param imgWidth
     * @param imgHeight
     * @param logoPath
     * @param needCompress
     * @return
     */
    public static byte[] createORCodeLogo(String srcUrl, int imgWidth, int imgHeight, String logoPath, boolean needCompress) throws Exception {
        byte[] orCodeLogo = createORCodeLogo(srcUrl, imgWidth, imgHeight, null, logoPath, needCompress);
        return orCodeLogo;
    }

    /**
     * 生成 指定大小 指定名称 包含logo 二维码
     * @param srcUrl
     * @param imgPath
     * @param imgWidth
     * @param imgHeight
     * @param imgName
     * @param logoPath
     * @param needCompress
     * @return
     * @throws Exception
     */
    public static String createORCodeLogo(String srcUrl, String imgPath, int imgWidth, int imgHeight, String imgName, String logoPath, boolean needCompress) throws Exception {
        String orCode = createORCode(srcUrl, imgPath, imgWidth, imgHeight, imgName, logoPath, needCompress);
        return orCode;
    }

    /**
     * 生成 指定大小 指定名称 包含logo 二维码 输出到流中
     * @param srcUrl
     * @param imgWidth
     * @param imgHeight
     * @param imgName
     * @param logoPath
     * @param needCompress
     * @return
     * @throws Exception
     */
    public static byte[] createORCodeLogo(String srcUrl, int imgWidth, int imgHeight, String imgName, String logoPath, boolean needCompress) throws Exception {
        byte[] orCode = createORCode(srcUrl, imgWidth, imgHeight, imgName, logoPath, needCompress);
        return orCode;
    }
    /**
     * 生成 指定尺寸、指定名称、包含logo 的二维码
     * 如果logoPath为null，则默认生成不包含logo的二维码
     * 如果确定生成不包含logo的二维码，则可以调用另外指定方法
     * @param srcUrl    需要生成二维码地址
     * @param imgPath   需要保存二维码的目录地址
     * @param imgWidth  需要生成二维码的宽度
     * @param imgHeight 需要生成二维码的高度
     * @param logoPath  需要生成二维码的中间logo
     * @param needCompress  logo是否需要压缩
     * @return
     * @throws Exception
     */
    private static String createORCode(String srcUrl, String imgPath, int imgWidth, int imgHeight, String imgName, String logoPath, boolean needCompress) throws Exception{

        boolean isDirectory = checkDirectory(imgPath);
        if(!isDirectory){
            logger.info("请用户选择正确的保存二维码的路径");
            throw new UtilException("错误图片路径");
        }
        String finalPath = "";
        //如果logo图片是null，生成不包含logo的二维码，否则生成含有logo的二维码
        if(StringUtils.isNotBlank(logoPath)){
            boolean b = checkImg(logoPath);
            if(b) {
                finalPath = createORCodeForLogoToFile(srcUrl, imgPath, imgWidth, imgHeight, imgName, logoPath, needCompress);
            }
            else {
                logger.info("请用户选择正确的logo格式jpg or png");
                throw new UtilException("错误图片后缀");
            }
        }else {
            finalPath = createORCodeNotLogoToFile(srcUrl,imgPath,imgWidth,imgHeight,imgName);
        }
        return finalPath;
    }
    /**
     * 生成 指定尺寸、指定名称、包含logo 的二维码 输出到流中
     * 如果logoPath为null，则默认生成不包含logo的二维码
     * 如果确定生成不包含logo的二维码，则可以调用另外指定方法
     * @param srcUrl    需要生成二维码地址
     * @param imgWidth  需要生成二维码的宽度
     * @param imgHeight 需要生成二维码的高度
     * @param logoPath  需要生成二维码的中间logo
     * @param needCompress  logo是否需要压缩
     * @return
     * @throws Exception
     */
    private static byte[] createORCode(String srcUrl, int imgWidth, int imgHeight, String imgName, String logoPath, boolean needCompress) throws Exception{
        byte[] bytes = null;
        //如果logo图片是null，生成不包含logo的二维码，否则生成含有logo的二维码
        if(StringUtils.isNotBlank(logoPath)){
            boolean b = checkImg(logoPath);
            if(b) {
                bytes = createORCodeForLogoToStream(srcUrl,imgWidth, imgHeight,logoPath,needCompress);
            }
            else {
                logger.info("请用户选择正确的logo格式jpg or png");
                throw new UtilException("错误图片后缀");
            }
        }else {
            bytes = createORCodeNotLogoToStream(srcUrl,imgWidth,imgHeight,imgName);
        }
        return bytes;
    }

    /**
     * 创建一个 指定大小、指定名称、包含logo 二维码并输入到文件
     * @param srcUrl
     * @param imgPath
     * @param imgWidth
     * @param imgHeight
     * @param imgName
     * @param logoPath
     * @param needCompress
     * @throws WriterException
     * @throws IOException
     */
    private static String createORCodeForLogoToFile(String srcUrl, String imgPath, int imgWidth, int imgHeight, String imgName, String logoPath, boolean needCompress) throws WriterException, IOException {

        BufferedImage bufferedImage = drawQR(srcUrl,imgWidth,imgHeight);
        Image src = enterFinalLogoImg(logoPath, needCompress);
        insertLogo(bufferedImage,src);
        String finalPath = outImgToPath(bufferedImage, format, imgPath,imgName);
        return finalPath;
    }
    /**
     * 创建一个 指定大小、指定名称、包含logo 二维码并写入流中
     * @param srcUrl
     * @param imgWidth
     * @param imgHeight
     * @param logoPath
     * @param needCompress
     * @return
     * @throws WriterException
     * @throws IOException
     */
    private static byte[] createORCodeForLogoToStream(String srcUrl, int imgWidth, int imgHeight, String logoPath, boolean needCompress) throws WriterException, IOException {
        BufferedImage bufferedImage = drawQR(srcUrl,imgWidth,imgHeight);
        Image src = enterFinalLogoImg(logoPath, needCompress);
        insertLogo(bufferedImage,src);
        byte[] bytes = outImgToStream(bufferedImage, format);
        return bytes;
    }

    /**
     * 生成指定大小，指定名称，无logo的二维码并输出到文件
     * @param srcUrl
     * @param imgPath
     * @param imgWidth
     * @param imgHeight
     * @param imgName
     * @return
     * @throws WriterException
     * @throws IOException
     */
    private static String createORCodeNotLogoToFile(String srcUrl, String imgPath, int imgWidth, int imgHeight, String imgName) throws WriterException, IOException {
        BufferedImage bufferedImage = drawQR(srcUrl, imgWidth, imgHeight);
        String s = outImgToPath(bufferedImage, format, imgPath, imgName);
        return s;
    }

    /**
     * 生成指定大小、指定名称，无logo的二维码并写入流中
     * @param srcUrl
     * @param imgWidth
     * @param imgHeight
     * @param imgName
     * @return
     * @throws WriterException
     * @throws IOException
     */
    private static byte[] createORCodeNotLogoToStream(String srcUrl, int imgWidth, int imgHeight, String imgName) throws WriterException, IOException {
        BufferedImage bufferedImage = drawQR(srcUrl, imgWidth, imgHeight);
        byte[] bytes = outImgToStream(bufferedImage, format);
        return bytes;
    }

    /**
     * 将生成的QR写入流中
     * @param bufferedImage
     * @param format
     * @return
     * @throws IOException
     */
    private static byte[] outImgToStream(BufferedImage bufferedImage,String format) throws IOException {
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, format, pngOutputStream);
        return pngOutputStream.toByteArray();
    }


    /**
     * 绘制二维码
     * @param srcUrl
     * @param imgWidth
     * @param imgHeight
     * @return
     * @throws WriterException
     */
    private static BufferedImage drawQR(String srcUrl, int imgWidth, int imgHeight) throws WriterException {
        BitMatrix matrix = new MultiFormatWriter().encode(srcUrl, BarcodeFormat.QR_CODE, imgWidth, imgHeight, hints);
        int[] pixels = new int[width * height];
        //绘制成二维码（应该）
        for (int x = 0; x < imgWidth; x++) {
            for (int y = 0; y < imgHeight; y++) {
                // 二维码颜色（RGB）
                int num1 = (int) (50 - (50.0 - 13.0) / matrix.getHeight()
                        * (y + 1));
                int num2 = (int) (165 - (165.0 - 72.0) / matrix.getHeight()
                        * (y + 1));
                int num3 = (int) (162 - (162.0 - 107.0)
                        / matrix.getHeight() * (y + 1));
                Color color = new Color(num1, num2, num3);
                int colorInt = color.getRGB();
                // 此处可以修改二维码的颜色，可以分别制定二维码和背景的颜色；
                pixels[y * width + x] = matrix.get(x, y) ? colorInt : 16777215;
            }
        }
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.getRaster().setDataElements(0, 0, width, height, pixels);
        return image;
    }

    /**
     * 确定最终需要添加的logo大小（是否需要压缩）
     * @param logoPath
     * @param needCompress
     * @return
     * @throws IOException
     */
    private static Image enterFinalLogoImg(String logoPath, boolean needCompress) throws IOException {
        //根据图片的路径，获取图片偏流
        InputStream inputStream = null;
        boolean isUrl = isURL(logoPath);
        if(isUrl) {
            URL url = new URL(logoPath);
            inputStream = url.openStream();
        } else {
           inputStream = new FileInputStream(new File(logoPath));
        }
        BufferedImage logoImage = ImageIO.read(inputStream);
        int tempWidth = logoImage.getWidth(null);
        int tempHeight = logoImage.getHeight(null);
        //最终确定的logo图片
        Image src = logoImage;
        //需要压缩
        if(needCompress){
            if(tempWidth > logo_width){
                tempWidth = logo_width;
            }
            if(tempHeight > logo_height){
                tempHeight = logo_height;
            }
            Image scaledInstance = logoImage.getScaledInstance(tempWidth, tempHeight, Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics graphics = tag.getGraphics();
            graphics.drawImage(scaledInstance,0,0,null);
            graphics.dispose();
            src = scaledInstance;
        }
        return src;
    }

    /**
     * 将二维码中间插入logo
     * @param img
     * @param logo
     */
    private static void insertLogo(BufferedImage img, Image logo){
        Graphics2D graphics2D = img.createGraphics();
        int imgWidth = img.getWidth(null);
        int imgHeight = img.getHeight(null);
        //将logo定位到中间位置
        int logoWidth = logo.getWidth(null);
        int logoHeight = logo.getHeight(null);
        int x = (imgWidth - logoWidth) / 2;
        int y = (imgHeight - logoHeight) / 2;
        graphics2D.drawImage(logo, x, y, logoWidth, logoHeight, null);
        Shape shape = new RoundRectangle2D.Float(x, y, logoWidth, logoHeight, 6, 6);
        graphics2D.setStroke(new BasicStroke(3f));
        graphics2D.draw(shape);
        graphics2D.dispose();
    }

    /**
     * 将二维码保存到指定位置
     * @param image
     * @param format        生成二维码格式、暂时png格式
     * @param imgPath       生成二维码路径
     * @param imgName       生成二维码名称
     * @throws IOException
     * @return 返回生成二维码的图片地址
     */
    private static String outImgToPath(BufferedImage image, String format, String imgPath, String imgName) throws IOException {
        if(StringUtils.isBlank(imgName)){
            imgName = UUID.randomUUID().toString();
        }
        String finalImgPath = imgPath + imgName + "." + format;
        ImageIO.write(image, format, new File(finalImgPath));

        return image.toString();
    }
    /**
     * 检查该path是否是路径
     * @param path
     * @return
     */
    public static boolean checkDirectory(String path){
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
        return file.isDirectory();
    }

    /**
     * 检测该参数是图片后缀的
     * 暂时只支持jpg png
     * @param path
     * @return
     */
    public static boolean checkImg(String path) {
        File file = new File(path);
        String fileName = file.getName();
        int i = fileName.indexOf(".");
        String suff = "";
        if (i != -1) {
            suff = fileName.substring(i + 1);
        }
        boolean isImg = false;
        if (StringUtils.isNotBlank(suff)) {
            //SUPPORT_FILE_SUFFIX是一个list（"jpg","png"）
            boolean contains = SUPPORT_FILE_SUFFIX.contains(suff);
            if (contains) {
                isImg = true;
            }
        }
        return true;//这个地方为了网络图片的需要，暂时牺牲一下永远判定格式正确
    }

    /**
     * 判断输入的路径是不是网络地址
     * @param str
     * @return
     */
    public static boolean isURL(String str){
        str = str.toLowerCase();
        Pattern pattern = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
        boolean matches = pattern.matcher(str).matches();
        return matches;
    }



    /**
     * 解析二维码图片入口
     * @param url
     * @return
     * @throws IOException
     * @throws NotFoundException
     */
    public static String decoderQRCode(String url) throws Exception {
        InputStream inputStream = null;
        String result = null;
        if(isURL(url)){
            URL url1 = new URL(url);
            inputStream = url1.openStream();
            result = decoderQRCodeFromUrl(inputStream);
        } else {
            inputStream = new FileInputStream(new File(url));
            result = decoderQRCodeFromFile(inputStream);
        }
        return result;
    }

    /**
     * 解析二维码（QRCode）
     * @param imgPath 图片路径
     * @return
     */
    public static String decoderQRCodeFromFile(InputStream imgPath) {
        Result result = null;
        try {
            MultiFormatReader formatReader = new MultiFormatReader();
            BufferedImage image = ImageIO.read(imgPath);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));

            //定义二维码的参数
            Map hints = new HashMap();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            result = formatReader.decode(binaryBitmap, hints);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.getText();
    }

    /**
     * 解析二维码（QRCode）
     * @param input 输入流
     * @return
     */
    public static String decoderQRCodeFromUrl(InputStream input) throws Exception {
        Result result = null;
        MultiFormatReader formatReader = new MultiFormatReader();
        BufferedImage image = ImageIO.read(input);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
        //定义二维码的参数
        Map hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        try {
            result = formatReader.decode(binaryBitmap, hints);
        } catch (NotFoundException e) {
            throw new Exception("二维码中没有任何信息");
        }
        return result.getText();
    }
}
