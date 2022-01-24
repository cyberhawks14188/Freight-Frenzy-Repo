package org.firstinspires.ftc.teamcode.TurretClasses;

import org.firstinspires.ftc.teamcode.TestHub.FreightFrenzyHardwareMap;

import java.util.Timer;

public class TurretCombined {
    FreightFrenzyHardwareMap robot = new FreightFrenzyHardwareMap();

    double extendSet, rotateSet, vPivotSet, extendEncoder, rotateEncoder, vPivotEncoder;
    double timeInSec = robot.TimerCustom(), lastTime = 0;
    double extendDeltaEncoder, extendModifiedEncoder, rotateDeltaEncoder, rotateModifiedEncoder, vPivotDeltaEncoder, vPivotModifiedEncoder;
    double extendLastEncoder, rotateLastEncoder, vPivotLastEncoder;
    double extendDirection, rotateDirection, vPivotDirection;
    double extendSpeedSet, rotateSpeedSet, vPivotSpeedSet;
    double extendSpeed, rotateSpeed, vPivotSpeed;
    double extendEncoderTickPerIn = 70,vPivotEncoder1Degree = 23 ;
    double vPivotPM, vPivotDM, vPivotUpPm = .015, vPivotDnPM = .004, vPivotUpDM = .009, vPivotDnDM = .012;
    double extendPM = .035, extendDM = 0.01;
    double rotatePM = .0005, rotateDM = 0.0003;
    double extendSpeedDifference, rotateSpeedDifference, vPivotSpeedDifference;
    double extendLastSpeedDifference = 0, rotateLastSpeedDifference = 0, vPivotLastSpeedDifference = 0;
    public double extendFinalMotorPower = 0, rotateFinalMotorPower = 0, vPivotFinalMotorPower = 0;

    public void TurretCombinedMethod(double ExtendSet, double ExtendSpeedSet, double RotateSet, double RotateSpeedSet,
                                     double VPivotSet, double VPivotSpeedSet, double ExtendEncoder, boolean ExtendMag,
                                     double RotateEncoder, boolean RotateMag, double VPivotEncoder, boolean VPivotMag){
        //setting a time variable for use to calculate speed
        timeInSec = robot.TimerCustom();



        //setting our encoder parameters to variables to reverse some encoders
        extendEncoder = ExtendEncoder;
        rotateEncoder = RotateEncoder;
        vPivotEncoder = -VPivotEncoder;



        //setpoint limits
        if(ExtendSet > 1550){
            extendSet = 1550;
        }else{
            extendSet = ExtendSet;
        }

        if(RotateSet > 5000){
            rotateSet = 5000;
        }else if(RotateSet < -5000){
            rotateSet = -5000;
        }else{
            rotateSet = RotateSet;
        }

        if(VPivotSet > 3000){
            vPivotSet = 3000;
        }else if(VPivotSet < 200){
            vPivotSet = 200;
        }else{
            vPivotSet = VPivotSet;
        }



        //setting the current encoder to a variable to reset positions using sensors
        extendDeltaEncoder = extendEncoder - extendLastEncoder;
        extendModifiedEncoder = extendModifiedEncoder + extendDeltaEncoder;

        rotateDeltaEncoder = rotateEncoder - rotateLastEncoder;
        rotateModifiedEncoder = rotateModifiedEncoder + rotateDeltaEncoder;

        vPivotDeltaEncoder = vPivotEncoder - vPivotLastEncoder;
        vPivotModifiedEncoder = vPivotModifiedEncoder + vPivotDeltaEncoder;



        //setting which direction each axis has to move to get to it's setpoint
        if(extendSet < extendModifiedEncoder){
            extendDirection = -1;
        }else if(extendSet > extendModifiedEncoder){
            extendDirection = 1;
        }

        if(rotateSet < rotateModifiedEncoder){
            rotateDirection = -1;
        }else if(rotateSet > rotateModifiedEncoder){
            rotateDirection = 1;
        }

        if(vPivotSet < vPivotModifiedEncoder){
            vPivotDirection = -1;
        }else if(vPivotSet > vPivotModifiedEncoder){
            vPivotDirection = 1;
        }



        //setting our speed setpoint with the correct direction
        extendSpeedSet = Math.copySign(ExtendSpeedSet, extendDirection);
        rotateSpeedSet = Math.copySign(RotateSpeedSet, rotateDirection);
        vPivotSpeedSet = Math.copySign(VPivotSpeedSet, vPivotDirection);



        //Setting motion profiles and deadzones
        if(Math.abs(extendSet - extendModifiedEncoder) < Math.abs(extendSpeedSet * 13)){
            extendSpeedSet = extendSpeedSet * (Math.abs(extendSet - extendModifiedEncoder)/Math.abs(extendSpeedSet * 13));
        }

        if(Math.abs(rotateSet - rotateModifiedEncoder) < Math.abs(rotateSpeedSet/5)){
            rotateSpeedSet = rotateSpeedSet * Math.abs((rotateSet - rotateModifiedEncoder)/Math.abs(rotateSpeedSet/5));
        }else if( Math.abs(rotateSet - rotateModifiedEncoder) < 10){
            rotateSpeedSet = 0;
        }

        if(Math.abs(vPivotSet - vPivotModifiedEncoder) < 5){
            vPivotSpeedSet = 0;
        }else if(Math.abs(vPivotSet - vPivotModifiedEncoder) < 300){
            vPivotSpeedSet = vPivotSpeedSet * ((Math.abs(vPivotSet - vPivotModifiedEncoder))/300);
        }



        //calculating speed on all 3 axis
        extendSpeed = (extendDeltaEncoder/extendEncoderTickPerIn)/(timeInSec - lastTime);
        rotateSpeed = rotateDeltaEncoder/(timeInSec - lastTime);
        vPivotSpeed = ((2 * 16) * Math.PI)*(vPivotDeltaEncoder / 360) / (timeInSec - lastTime);



        //setting which PD multiplier for the vertical pivot
        if(vPivotSet < vPivotModifiedEncoder){
            vPivotPM = vPivotDnPM;
            vPivotDM = vPivotDnDM;
        }else if(vPivotSet > vPivotModifiedEncoder){
            vPivotPM = vPivotUpPm;
            vPivotDM = vPivotUpDM;
        }



        //calculating the speed difference from speed set to actual speed
        extendSpeedDifference = extendSpeedSet - extendSpeed;
        rotateSpeedDifference = rotateSpeedSet - rotateSpeed;
        vPivotSpeedDifference = vPivotSpeedSet - vPivotSpeed;



        //calculating the correction motor power for each axis
        extendFinalMotorPower = extendFinalMotorPower + (extendSpeedDifference * extendPM) + ((extendSpeedDifference - extendLastSpeedDifference) * extendDM);
        rotateFinalMotorPower = rotateFinalMotorPower + (rotateSpeedDifference * rotatePM) + ((rotateSpeedDifference - rotateLastSpeedDifference) * rotateDM);
        vPivotFinalMotorPower = vPivotFinalMotorPower + (vPivotSpeedDifference * vPivotPM) + ((vPivotSpeedDifference - vPivotLastSpeedDifference) * vPivotDM);



        //limiting our motor powers to prevent motor overload and our correction loops from having a correction delay
        if(extendFinalMotorPower > 1){
            extendFinalMotorPower = 1;
        }else if(extendFinalMotorPower < -1){
            extendFinalMotorPower = -1;
        }

        if(rotateFinalMotorPower > 1){
            rotateFinalMotorPower = 1;
        }else if(rotateFinalMotorPower < -1){
            rotateFinalMotorPower = -1;
        }

        if(vPivotFinalMotorPower > 1){
            vPivotFinalMotorPower = 1;
        }else if(vPivotFinalMotorPower < -1){
            vPivotFinalMotorPower = -1;
        }



        lastTime = timeInSec;
        extendLastEncoder = ExtendEncoder;
        rotateLastEncoder = RotateEncoder;
        vPivotLastEncoder = VPivotEncoder;
        extendLastSpeedDifference = extendSpeedDifference;
        rotateLastSpeedDifference = rotateSpeedDifference;
        vPivotLastSpeedDifference = vPivotSpeedDifference;

    }
}