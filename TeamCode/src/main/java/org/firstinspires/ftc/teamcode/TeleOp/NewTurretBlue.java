package org.firstinspires.ftc.teamcode.TeleOp;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.GeneralRobotCode.FreightFrenzyHardwareMap;
import org.firstinspires.ftc.teamcode.GeneralRobotCode.Smoothing;
import org.firstinspires.ftc.teamcode.TurretClasses.TurretCombined;


@Config
@TeleOp
public class NewTurretBlue extends LinearOpMode{



    public double x, y, z;

    double teleOpExtendSet = 200, teleOpRotateSet = 0, teleOpVPivotSet = 1000;
    double intakeVPivotSet = 550, intakeRotateSet = 0, intakeExtendSet = 275;
    double teleOpExtendSpeedSet = 30, teleOpRotateSpeedSet = 2000, teleOpVPivotSpeedSet = 18;
    double lastDS = 5;


    FreightFrenzyHardwareMap robot = new FreightFrenzyHardwareMap();
    TurretCombined CombinedTurret = new TurretCombined();
    Smoothing Smoothing = new Smoothing();
    FtcDashboard dashboard = FtcDashboard.getInstance();
    Telemetry dashboardTelemetry = dashboard.getTelemetry();



    @Override
    public void runOpMode(){
        robot.init(hardwareMap);



        waitForStart();

        while (opModeIsActive()){






            x = Smoothing.SmoothDriveX(-Math.copySign(gamepad1.left_stick_x, gamepad1.left_stick_x * gamepad1.left_stick_x * gamepad1.left_stick_x));
            y = Smoothing.SmoothDriveY( -(Math.copySign(gamepad1.left_stick_y, gamepad1.left_stick_y * gamepad1.left_stick_y * gamepad1.left_stick_y)));
            z = Smoothing.SmoothDriveZ(  Math.copySign(gamepad1.right_stick_x, gamepad1.right_stick_x * gamepad1.right_stick_x * gamepad1.right_stick_x));




            //setting the possiblity of a slow speed on the drivetrain
            if(gamepad1.right_bumper){
                robot.LF_M.setPower(.4*((y)-x+(z)));//LF
                robot.LB_M.setPower(.4*((y)+x+(z)));//LB
                robot.RF_M.setPower(.4*(-((y)+x-(z))));//RF
                robot.RB_M.setPower(.4*(-((y)-x-(z))));//RB
            }else{
                robot.LF_M.setPower(((y)-x+(.8*z)));//LF
                robot.LB_M.setPower(((y)+x+(.8*z)));//LB
                robot.RF_M.setPower((-((y)+x-(.8*z))));//RF
                robot.RB_M.setPower((-((y)-x-(.8*z))));//RB
            }


            if(gamepad2.left_bumper){//Carousel trigger to let us use buttons more than once
                if(gamepad2.b){
                    robot.TC_M.setPower(0);
                }else if(gamepad2.a){
                    robot.TC_M.setPower(-.4);
                }else if(gamepad2.x){
                    robot.TC_M.setPower(-1);
                }else{
                    robot.TC_M.setPower(robot.TC_M.getPower() + (gamepad2.right_stick_y * .01));
                }
            }else{//regular game functions for the intake of the turret
                if(gamepad1.a || gamepad2.a){//intake
                    robot.RI_S.setPower(-.5);
                    robot.LI_S.setPower(.5);
                }else if(gamepad1.b || gamepad2.b){//outtake
                    robot.RI_S.setPower(.5);
                    robot.LI_S.setPower(-.5);
                }else{//servos off
                    robot.RI_S.setPower(-.05);
                    robot.LI_S.setPower(0.05);
                }
                if(lastDS > 1 && robot.I_DS.getDistance(DistanceUnit.INCH) < 1 && robot.RI_S.getPower() > 0){//shutting down the intake once we have a piece of freight in our intake
                    robot.RI_S.setPower(0);
                    robot.LI_S.setPower(0);
                }//TODO fix this so it works

                                //turret Presets
                if(gamepad2.y){//Resetting our intake position in case of any encoder drift
                    intakeVPivotSet = CombinedTurret.vPivotModifiedEncoder;
                    intakeExtendSet = CombinedTurret.extendModifiedEncoder;
                    intakeRotateSet = CombinedTurret.rotateModifiedEncoder;

                }else if(gamepad2.dpad_right) {//Alliance hub dropping preset
                    teleOpVPivotSet = 1600;
                    if (CombinedTurret.vPivotModifiedEncoder > 1000) {
                        teleOpRotateSet = 1300;
                        teleOpExtendSet = 800;
                    }
                }else if(gamepad2.dpad_down) {//Intake position
                    teleOpVPivotSet = 1000;

                    if (Math.abs(CombinedTurret.rotateModifiedEncoder) < 50 && CombinedTurret.extendModifiedEncoder > 220) {
                        teleOpVPivotSet = intakeVPivotSet;
                    }else  if (CombinedTurret.vPivotModifiedEncoder > 800) {
                        teleOpRotateSet = intakeRotateSet;
                        teleOpExtendSet = intakeExtendSet;
                    }
                }else if(gamepad2.dpad_left){//Shared shipping hub intake position
                    teleOpVPivotSet = 1000;
                    if (CombinedTurret.vPivotModifiedEncoder > 800) {
                        teleOpRotateSet = -1000;
                        teleOpExtendSet = 200;
                    }

                }else{//manual turret position setting
                    teleOpExtendSet = teleOpExtendSet - (gamepad2.right_stick_y * 50);
                    if(gamepad2.right_trigger > .05){
                        teleOpRotateSet = teleOpRotateSet + (gamepad2.right_trigger * 40);
                    }else if(gamepad2.left_trigger > .05){
                        teleOpRotateSet = teleOpRotateSet - (gamepad2.left_trigger * 40);
                    }

                    teleOpVPivotSet = teleOpVPivotSet + (gamepad2.left_stick_y * -30);

                }
            }
            //setpoint limits
            if(teleOpVPivotSet > 2000){
                teleOpVPivotSet = 2000;
            }else if(teleOpVPivotSet < 100){
                teleOpVPivotSet = 100;
            }

            if(teleOpExtendSet > 1200){
                teleOpExtendSet = 1200;
            }if(teleOpExtendSet < 0){
                teleOpExtendSet = 0;
            }

            if(teleOpRotateSet > 5000){
                teleOpRotateSet = 5000;
            }else if(teleOpRotateSet < -5000){
                teleOpRotateSet = -5000;
            }

            CombinedTurret.TurretCombinedMethod(teleOpExtendSet,teleOpExtendSpeedSet,teleOpRotateSet,teleOpRotateSpeedSet, teleOpVPivotSet,teleOpVPivotSpeedSet, robot.TE_M.getCurrentPosition(), robot.TE_G.getState(), robot.TR_M.getCurrentPosition(), robot.TR_G.getState(), robot.TP_M.getCurrentPosition(), robot.TP_G.getState());

            robot.TE_M.setPower(CombinedTurret.extendFinalMotorPower);
            robot.TR_M.setPower(CombinedTurret.rotateFinalMotorPower);
            robot.TP_M.setPower(CombinedTurret.vPivotFinalMotorPower);

            Telemetry();
            lastDS = robot.I_DS.getDistance(DistanceUnit.INCH);
        }

    }
    public void Telemetry(){

        dashboardTelemetry.addData("vPivotMotor Power", CombinedTurret.vPivotFinalMotorPower);
        dashboardTelemetry.addData("vPivot Modified Encoder", CombinedTurret.vPivotModifiedEncoder);

        dashboardTelemetry.addData("extend Motor Power", CombinedTurret.extendFinalMotorPower);
        dashboardTelemetry.addData("extend speed", CombinedTurret.extendSpeed);
        dashboardTelemetry.addData("extend Modified Encoder", CombinedTurret.extendModifiedEncoder);
        dashboardTelemetry.addData("extend Set", CombinedTurret.extendSet);
        dashboardTelemetry.addData("turret Homing trigger", CombinedTurret.turretHomingTrigger);

        telemetry.addData("intake Dist", robot.I_DS.getDistance(DistanceUnit.INCH));
        telemetry.addData("rotate set", CombinedTurret.rotateSet);
        telemetry.addData("rotate mod encoder", CombinedTurret.rotateModifiedEncoder);
        telemetry.addData("rotate RAW Encoder", robot.TR_M.getCurrentPosition());

        dashboardTelemetry.update();
        telemetry.update();
    }

}
