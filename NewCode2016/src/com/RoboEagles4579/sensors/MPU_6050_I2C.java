package com.RoboEagles4579.sensors;


import com.RoboEagles4579.math.Vector3d;
import com.RoboEagles4579.math.Vector3i;
import com.RoboEagles4579.math.Vector3s;

import edu.wpi.first.wpilibj.I2C;

public class MPU_6050_I2C {
	
	private byte deviceAddress;
	
	private int accelSensitivity = 0,
				gyroSensitivity = 0,
				sampleRateDivider = 18,
				digitalLPFConfig = 3;
	
	
	public Vector3s rawAccelerometer = new Vector3s(),
					 rawGyro = new Vector3s();
	
	private Vector3d accelValues = new Vector3d(),
					 gyroValues = new Vector3d();
	
	private double temp = 0.;
	
	private double accelLSB_Sensitivity = ACCEL_SENSITIVITY[accelSensitivity][1],
				   gyroLSB_Sensitivity = GYRO_SENSITIVITY[gyroSensitivity][1];
	
	private static final int REGISTER_PWRMGMT_1 = 0x6B,
							 REGISTER_PWRMGMT_2 = 0x6C,
							 REGISTER_ACCEL = 0x3B,
							 REGISTER_GYRO = 0x43,
							 REGISTER_CONFIG = 0x1A,
							 REGISTER_GYRO_CONFIG = 0x1B,
							 REGISTER_ACCEL_CONFIG = 0x1C,
							 REGISTER_TEMP = 0x41,
							 REGISTER_SAMPLE_RATE = 0x19,
							 REGISTER_INTERUPT_ENABLE = 0x38,
							 REGSITER_INTERUPT_STATUS = 0x3A;
	
	private static int DATA_READY_INT = 0;
	
	private static final double[][] ACCEL_SENSITIVITY = {
			{ 2. , 16384. },{ 4. , 8192. },{ 8. , 4096. },{ 16. , 2048. },
	};
	private static final double[][] GYRO_SENSITIVITY = {
			{ 250 , 131. },{ 500. , 65.5 },{ 1000. , 32.8 },{ 2000. , 16.4 },
	};

	
	public enum ACCEL_VALUES {
		
		k2g(0), k4g(1), k8g(2), k16g(3);
		
		private ACCEL_VALUES(int settingValue) {
			this.value = settingValue;
		}
		
		public int value;
		
	}
	
	public enum GYRO_VALUES {
		
		k250(0), k500(1), k1000(2), k2000(3);
		
		private GYRO_VALUES(int settingValue) {
			this.value = settingValue;
		}
		
		public int value;
		
	}
	
	private I2C MPU;
	
	private byte[] accelReads = new byte[6],
					gyroReads = new byte[6],
					interruptStatus = new byte[1],
					tempBuff = new byte[2];
	
	public MPU_6050_I2C(byte deviceAddress, 
						ACCEL_VALUES accelSensitivity, 
						GYRO_VALUES gyroSensitivity) {
	
		this.accelSensitivity = accelSensitivity.value;
		this.gyroSensitivity = gyroSensitivity.value;
		this.deviceAddress = deviceAddress;
		
		this.accelLSB_Sensitivity = ACCEL_SENSITIVITY[this.accelSensitivity][1];
		this.gyroLSB_Sensitivity = GYRO_SENSITIVITY[this.gyroSensitivity][1];
		
		MPU = new I2C(I2C.Port.kOnboard, (int) this.deviceAddress);
		
		init();
		
	}

	public MPU_6050_I2C(byte deviceAddress) {
		
		this(deviceAddress, ACCEL_VALUES.k4g, GYRO_VALUES.k500);
		
		
	}

	public MPU_6050_I2C() {
		
		this((byte) 0x68);
		
	}
	
	private void init() {
		
		byte[] registerConfig = new byte[1],
				registerAccelConfig = new byte[1],
				registerGyroConfig = new byte[1];
		
		MPU.read(REGISTER_CONFIG, registerConfig.length, registerConfig);
		MPU.read(REGISTER_ACCEL_CONFIG, registerAccelConfig.length, registerAccelConfig);
		MPU.read(REGISTER_GYRO_CONFIG, registerGyroConfig.length, registerGyroConfig);
		
		registerConfig[0] = (byte) ((registerConfig[0] & (byte) 248) | (byte) digitalLPFConfig);
		registerAccelConfig[0] = (byte) ((registerAccelConfig[0] & (byte) 99 | (byte) this.accelSensitivity << 3));
		registerGyroConfig[0] = (byte) ((registerGyroConfig[0] & (byte) 99 | (byte) this.gyroSensitivity << 3));
		
		MPU.write(REGISTER_SAMPLE_RATE, (byte) sampleRateDivider);
		MPU.write(REGISTER_CONFIG, registerConfig[0]);
		MPU.write(REGISTER_ACCEL_CONFIG, registerAccelConfig[0]);
		MPU.write(REGISTER_GYRO_CONFIG, registerGyroConfig[0]);
		MPU.write(REGISTER_PWRMGMT_1, (byte) 0x00);
		MPU.write(REGISTER_PWRMGMT_2, (byte) 0x00);
		MPU.write(REGISTER_INTERUPT_ENABLE, (byte) 0x01);
		
		
	}
	
	public MPU_6050_I2C read() {
				
		do {

			MPU.read(REGSITER_INTERUPT_STATUS, 1, interruptStatus);
			DATA_READY_INT = ((byte) interruptStatus[0]) & 0x01;
			
		} while (DATA_READY_INT == 0);
			
		
			MPU.read(REGISTER_ACCEL, accelReads.length, accelReads);
			MPU.read(REGISTER_GYRO, gyroReads.length, gyroReads);
			MPU.read(REGISTER_TEMP, tempBuff.length, tempBuff);
			
			rawAccelerometer.X = (short) ((accelReads[0] << 8) | accelReads[1]);
			rawAccelerometer.Y = (short) ((accelReads[2] << 8) | accelReads[3]);
			rawAccelerometer.Z = (short) ((accelReads[4] << 8) | accelReads[5]);
			
			rawGyro.X = (short) ((gyroReads[0] << 8) | gyroReads[1]);
			rawGyro.Y = (short) ((gyroReads[2] << 8) | gyroReads[3]);
			rawGyro.Z = (short) ((gyroReads[4] << 8) | gyroReads[5]);

			short tempTmp = (short) ((tempBuff[0] << 8) | tempBuff[1]);
			
			temp = (tempTmp / 340) + 36.53;
		
		return this;
		
	}

	public Vector3d getAccel() {
		return accelValues.set(rawAccelerometer).divide(this.accelLSB_Sensitivity);
	}
	
	public Vector3d getGyro() {
		return gyroValues.set(rawGyro).divide(this.gyroLSB_Sensitivity);
	}
	
	public double getAccelX() {
		return getAccel().X;
	}
	
	public double getAccelY() {
		return getAccel().Y;
	}
	
	public double getAccelZ() {
		return getAccel().Z;		
	}
	
	public double getGyroX() {
		return getGyro().X;
	}
	
	public double getGyroY() {
		return getGyro().Y;
	}
	
	public double getGyroZ() {
		return getGyro().Z;
	}
	
	
	public double getTemp() { // degrees Centigrade
		
		return temp;
		
	}

}
