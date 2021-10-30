package org.firstinspires.ftc.teamcode.TestHub;


import com.qualcomm.robotcore.hardware.HardwareMap;

public class ExtendClass {
    double extendMin = 2, extendMax = 1300, extendSet = extendMin;
    double extendDifference = 0, extendMultipliedP = 0, extendP = -.02, extendD = 0, extendMultipliedD = 0;

    double ExtendMotorPower = 0, lastError = 0;

    public double ExtendMethod(double Controller, double extendEncoder, boolean MagneticExtend){
        extendSet = extendSet + (15 * (Controller));//setting the setpoint using the controller input

        //reseting the encoder minimum at the magnetic sensor so we stop at the sensor and not overrun
        if(MagneticExtend == true){
            extendMin = extendEncoder;
        }

        //Setpoint limits
        if(extendSet < extendMin){
            extendSet = (extendMin + 2);
        }else  if( extendSet > extendMax){
            extendSet = (extendMax - 2);
        }

        //finding difference so we can use it for the proportional and derivative multipliers
        extendDifference = extendEncoder - extendSet;
        extendMultipliedP = extendDifference * extendP;//Proportional multiplying
        extendMultipliedD = (extendEncoder - lastError) * extendD;//derivative multiplying
        ExtendMotorPower = extendMultipliedP + extendMultipliedD;//adding them together to give 1 motor power

        lastError = extendDifference;// setting last error for use in derivative

        //returning the motor power for use
        return ExtendMotorPower;
    }

    public double ExtendAutoMethod(double desiredset, double speed, double extendEncoder, boolean MagneticExtend) {

        extendSet = desiredset;

        //reseting the encoder minimum at the magnetic sensor so we stop at the sensor and not overrun
        if (MagneticExtend == true) {
            extendMin = extendEncoder;
        }

        //Setpoint limits
        if (extendSet < extendMin) {
            extendSet = (extendMin + 2);
        } else if (extendSet > extendMax) {
            extendSet = (extendMax - 2);
        }

        //finding difference so we can use it for the proportional and derivative multipliers
        extendDifference = extendEncoder - extendSet;
        extendMultipliedP = extendDifference * extendP;//Proportional multiplying
        extendMultipliedD = (extendEncoder - lastError) * extendD;//derivative multiplying
        ExtendMotorPower = extendMultipliedP + extendMultipliedD;//adding them together to give 1 motor power

        lastError = extendDifference;// setting last error for use in derivative

        //setting limits for speed to go a desired setpoint
        if(ExtendMotorPower > speed && ExtendMotorPower > .05){
            ExtendMotorPower = speed;
        }else if (ExtendMotorPower < -speed && ExtendMotorPower < -.05){
            ExtendMotorPower = -speed;
        }
        //returning the motor power for use
        return ExtendMotorPower;

    }

    double homingnextset; boolean HasExtended = false; double homingMin = 0; boolean isHomed = false;
    public void ExtendHoming(boolean ManeticExtend, double Extendencoder){

        //looking to see if the slides are already fully retracted
        if(ManeticExtend == true){
            //dermeines if we started retracted or if we ran the homing sequence
                if(HasExtended == false){
                    //setting the setpoint for the extension of homing
                    homingnextset = Extendencoder + 5;
                    ExtendAutoMethod(homingnextset,.5, Extendencoder, ManeticExtend);
                }else{
                    //sets the minimum
                    homingMin = Extendencoder;
                    isHomed = true;
                }

        }else{
            //setting the setpoint for the retraction of homing
            homingnextset = Extendencoder - 5;
            ExtendAutoMethod(homingnextset,.5, Extendencoder, ManeticExtend);
            HasExtended = true;
        }
    }
    //returns the bvarible ishomed so we can know if we have homed in the main loop
    public boolean isHomedReturn(){return isHomed;}
}
