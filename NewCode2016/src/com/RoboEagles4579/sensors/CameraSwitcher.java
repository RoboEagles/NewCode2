
package com.RoboEagles4579.sensors;


import org.usfirst.frc4579.NewCode2016.Robot;
import com.ni.vision.NIVision;

import com.ni.vision.NIVision.Image;


import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.vision.AxisCamera;
import edu.wpi.first.wpilibj.vision.USBCamera;


public class CameraSwitcher {
	private static CameraServer server;
	private static AxisCamera Axis;
	private static USBCamera USB;
	private boolean toggleCam = false;
	
	
	
	private String USB_NAME = "cam0";
	
	public Image image;
	
	
	private static Joystick driveStick = Robot.oi.getDriveStick();
	
	private static JoystickButton toggleCamButton = new JoystickButton(driveStick, 10);
	
	public CameraSwitcher(){
		USB = new USBCamera(USB_NAME);
		image = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
		Axis = new AxisCamera("169.254.228.142");
		Axis.writeExposurePriority(0);
		
		server = CameraServer.getInstance();
        server.setQuality(50);
        
        init();

	}
	
	
	//This function runs during periodically during teleop 
	public void runTeleOP(){
		if(toggleCamButton.get()){
			toggleCam = !toggleCam; //Change the state of the toggle boolean
		}
		runCam();
		server.setImage(image);
		
	}
	
	private void runCam(){
		if(!toggleCam){
			USB.stopCapture();
			Axis.getImage(image);
		} 
		else {
			USB.startCapture();
			USB.getImage(image);
		}
	}
	
	public static void init(){
		USB.openCamera();
		USB.setFPS(15);
		USB.updateSettings();
		
		
		//Set default camera to automatically send default camera to dashboard
		USB.startCapture(); //start default camera (USB)*/
	}
	
	public static void end(){
		USB.closeCamera();
	}
}