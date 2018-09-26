package ch.hslu.informatik.pren;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.hopding.jrpicam.RPiCamera;
import com.hopding.jrpicam.enums.Exposure;
import com.hopding.jrpicam.exceptions.FailedToRunRaspistillException;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import rpi.sensehat.api.SenseHat;

public class Pren {

    private ITesseract tesseractInstance = null;
    private SenseHat senseHat = null;

    public String getImgText(int counter, RPiCamera piCamera) throws FailedToRunRaspistillException, IOException, InterruptedException {
        ITesseract instance = getTesseractInstance();
        BufferedImage in = piCamera.takeBufferedStill();

        try {
            BufferedImage out = createBlackAndWhite(in);
            // writeImageToFileSystem(out);
            String imgText = instance.doOCR(out);
            return imgText;
        } catch (TesseractException e) {
            e.getMessage();
            return "Error while reading image";
        }
    }

    private void writeImageToFileSystem(BufferedImage out) throws IOException {
        File outputfile = new File("image.jpg");
        ImageIO.write(out, "jpg", outputfile);
    }

    private void showOnLed(String s) {
        if ((s != null) && (s.length() > 0)) {
            getSenseHat().ledMatrix.showLetter(s);
        }
    }

    private void showMessageOnLed(String s) {
        if ((s != null) && (s.length() > 0)) {
            getSenseHat().ledMatrix.showMessage(s.substring(0, 2));
        }
    }

    private SenseHat getSenseHat() {
        if (senseHat == null) {
            SenseHat senseHat = new SenseHat();
            this.senseHat = senseHat;
        }
        return this.senseHat;
    }

    private void tesseractConfig(ITesseract instance) {
        instance.setTessVariable("tessedit_char_whitelist", "0123456789");
        instance.setTessVariable("load_system_dawg", "0");
        instance.setTessVariable("load_freq_dawg", "0");
        instance.setTessVariable("load_punc_dawg", "0");
        instance.setTessVariable("load_number_dawg", "0");
        instance.setTessVariable("load_unambig_dawg", "0");
        instance.setTessVariable("load_bigram_dawg", "0");
        instance.setTessVariable("load_fixed_length_dawgs", "0");
        instance.setTessVariable("classify_enable_learning", "0");
        instance.setTessVariable("classify_enable_adaptive_matcher", "0");
        instance.setTessVariable("segment_penalty_garbage", "0");
        instance.setTessVariable("segment_penalty_dict_nonword", "0");
        instance.setTessVariable("segment_penalty_dict_frequent_word", "0");
        instance.setTessVariable("segment_penalty_dict_case_ok", "0");
        instance.setTessVariable("segment_penalty_dict_case_bad", "0");
    }

    private ITesseract getTesseractInstance() {
        if (this.tesseractInstance == null) {
            ITesseract tesseract = new Tesseract();
            tesseractConfig(tesseract);
            this.tesseractInstance = tesseract;
        }
        return tesseractInstance;
    }

    private static BufferedImage createBlackAndWhite(final BufferedImage src) {
        final BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        result.createGraphics().drawImage(src, 0, 0, null);
        return result;
    }

    public static void main(String[] args) throws FailedToRunRaspistillException, IOException, InterruptedException {
        Pren app = new Pren();
        app.init();
        app.loop();
    }

    private void init() {
        SenseHat senseHat = getSenseHat();
        senseHat.ledMatrix.showMessage("Hello HSLU");
        senseHat.ledMatrix.showMessage("Hello HSLU");
        senseHat.ledMatrix.showMessage("Hello HSLU");
    }

    private void loop() throws FailedToRunRaspistillException, IOException, InterruptedException {
        RPiCamera piCamera = getCamera();
        int i = 0;
        while (true) {
            i++;
            String s = getImgText(i, piCamera);
            s = cleanString(s);
            System.out.println("erkannte Zeichen: " + s);
            // s = getOneChar(s);
            showMessageOnLed(s);
            Thread.sleep(1000);
        }

    }

    private String getOneChar(String s) {
        if (s.length() > 0) {
            s = s.substring(0, 1);
        }
        return s;
    }

    private String cleanString(String s) {
        return s.replaceAll("\\s+", "");
    }

    private RPiCamera getCamera() throws FailedToRunRaspistillException {

        RPiCamera piCamera = new RPiCamera();

        piCamera.setWidth(500).setHeight(500) // Set Camera to produce 500x500
                                              // // images.
                .setBrightness(75) // Adjust Camera's brightness setting.
                .setExposure(Exposure.AUTO);

        return piCamera;
    }
}