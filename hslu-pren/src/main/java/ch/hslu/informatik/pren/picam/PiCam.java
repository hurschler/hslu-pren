package ch.hslu.informatik.pren.picam;

import java.io.IOException;
import com.hopding.jrpicam.RPiCamera;
import com.hopding.jrpicam.exceptions.FailedToRunRaspistillException;

public class PiCam {

    public static void main(String[] args) throws FailedToRunRaspistillException, InterruptedException, IOException {

        RPiCamera piCamera = new RPiCamera("/home/pi/Pictures");
        int i = 0;
        while (true) {
            i++;
            piCamera.takeStill("An-Awesome-Pic" + i + ".jpg");
            Thread.sleep(5000);
        }

    }

}
