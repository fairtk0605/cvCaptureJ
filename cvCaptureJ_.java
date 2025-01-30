import ij.*;
import ij.gui.*;
import ij.plugin.*;
import ij.process.*;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_videoio.VideoCapture;

public class cvCaptureJ_ implements PlugIn{
  int maxCam   = 10;
  int camCount = 0;
  boolean[] camExist = new boolean[10];
  VideoCapture   cap = new VideoCapture();
  ImagePlus     imp;
  ColorProcessor cp;

  int www,hhh;
  byte[] rbit,gbit,bbit;
  Mat img = new Mat();
  
  public void run( String arg ){
    countCamera();
    if(camCount<1){
      IJ.log("No Camera");
      return ;
    }
    
    String[] camIDs = new String[camCount];
    int n = 0 ;
    for(int i = 0 ; i < maxCam ; i ++){
      if( camExist[i] ) {
        camIDs[n] = ""+i;
        n ++ ;
      }
    }

    GenericDialog gd = new GenericDialog("Capture");
    gd.addChoice("CamID",camIDs,camIDs[0]);          
    gd.showDialog();
    if (gd.wasCanceled()) return;
    
    int id = Integer.parseInt(gd.getNextChoice());
    
    initCapture(id);
    imp.show();
    while( imp.getWindow() != null) setPixel();
    cap.release();

  }

  int countCamera(){
    IJ.showStatus("Now checking Camera");
    camCount = 0 ;    
    for(int i = 0 ; i < maxCam ; i ++){      
      if( cap.open(i) ){
        camExist[i] = true;
        camCount ++ ;
      }
      else{
        camExist[i] = false;
      }
      IJ.showStatus("Camera"+i+" Check Done");
    }
    return camCount;
  }
  
  void initCapture(int i){
    cap.open(i);
    www = (int)cap.get(3);
    hhh = (int)cap.get(4);    
    cp  = new ColorProcessor(www,hhh);
    imp = new ImagePlus("cap",cp);
    rbit = new byte[www*hhh];
    gbit = new byte[www*hhh];
    bbit = new byte[www*hhh];    
  }
  
  void setPixel(){
    cap.read(img);
    BytePointer bp = img.data();          
    for(int i = 0 ; i < www*hhh ; i ++){
      bbit[i] = bp.get(3*i+0);
      gbit[i] = bp.get(3*i+1);
      rbit[i] = bp.get(3*i+2);
    }
    cp.setRGB(rbit,gbit,bbit);
    imp.updateAndDraw();      
  }

  
}

