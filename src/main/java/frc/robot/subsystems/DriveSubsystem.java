package frc.robot.subsystems;

import java.lang.Math;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatorCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.TalonFXInvertType;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.util.datalog.DoubleLogEntry;

/**
 * Subsystem to model the robot's drivetrain
 */
public class DriveSubsystem extends SubsystemBase {
    // Drive motors
    private WPI_TalonFX leftMain;
    private WPI_TalonFX leftFollower;
    private WPI_TalonFX rightMain;
    private WPI_TalonFX rightFollower;

    // DifferentialDrive object for drive calculations
    private DifferentialDrive drive;

    // Current neutral mode
    private NeutralMode neutralMode = NeutralMode.Brake;

    // Current limiting enabled?
    private Boolean currentLimitEnabled = true;

    DataLog log;
    DoubleLogEntry rightMainCurrentLog;
    DoubleLogEntry leftMainCurrentLog;
    DoubleLogEntry rightFollowerCurrentLog;
    DoubleLogEntry leftFollowerCurrentLog;

    DoubleLogEntry leftMainSpeedLog;
    DoubleLogEntry leftFollowerSpeedLog;
    DoubleLogEntry rightMainSpeedLog;
    DoubleLogEntry rightFollowerSpeedLog;


    public DriveSubsystem() {
        // Motor initialization
        // Left motors turn clockwise
        leftMain = initMotor(1);
        leftMain.setInverted(TalonFXInvertType.Clockwise);
        leftFollower = initMotor(2);
        leftFollower.follow(leftMain);
        leftFollower.setInverted(TalonFXInvertType.FollowMaster);

        // Right motors turn counterclockwise
        rightMain = initMotor(3);
        rightMain.setInverted(TalonFXInvertType.CounterClockwise);
        rightFollower = initMotor(4);
        rightFollower.follow(rightMain);
        rightFollower.setInverted(TalonFXInvertType.FollowMaster);

        setCurrentLimitEnabled(true);

        // Drivetrain initialization
        drive = new DifferentialDrive(leftMain, rightMain);

        DataLogManager.start();
        rightMainCurrentLog = new DoubleLogEntry(DataLogManager.getLog(), "Right Main Current");
        leftMainCurrentLog = new DoubleLogEntry(DataLogManager.getLog(), "Left Main Current");
        rightFollowerCurrentLog = new DoubleLogEntry(DataLogManager.getLog(), "Right Follower Current");
        leftFollowerCurrentLog = new DoubleLogEntry(DataLogManager.getLog(), "Left Follower Current");

        leftMainSpeedLog = new DoubleLogEntry(DataLogManager.getLog(), "Left Main Speed");
        leftFollowerSpeedLog = new DoubleLogEntry(DataLogManager.getLog(), "Left Follower Speed");
        rightMainSpeedLog = new DoubleLogEntry(DataLogManager.getLog(), "Right Main Speed");
        rightFollowerSpeedLog = new DoubleLogEntry(DataLogManager.getLog(), "Right Follower Speed");
    }

    /**
     * Helper method to initialize a WPI_TalonFX.
     * 
     * @param canId The motor's CAN ID
     * @return newly initialized WPI_TalonFX
     */
    private WPI_TalonFX initMotor(int canId) {
        WPI_TalonFX motor = new WPI_TalonFX(canId);
        motor.configFactoryDefault();
        motor.setNeutralMode(neutralMode);
        return motor;
    }

    @Override
    public void periodic() {

        SmartDashboard.putNumber("Left Main Sensor Position (m)", -getMeters(leftMain.getSelectedSensorPosition()));
        SmartDashboard.putNumber("Left Main Sensor Velocity (m/s)", Math.abs(getMetersPerSecond(leftMain.getSelectedSensorVelocity())));
        SmartDashboard.putNumber("Right Main Sensor position (m)", -getMeters(rightMain.getSelectedSensorPosition()));
        SmartDashboard.putNumber("Right Main Sensor velocity (m/s)", Math.abs(getMetersPerSecond(rightMain.getSelectedSensorVelocity())));
        // Motor temps
        SmartDashboard.putNumber("MotorTemperature/Left Main (C)", Math.round(leftMain.getTemperature()));
        SmartDashboard.putNumber("MotorTemperature/Left Follower (C)", Math.round(leftFollower.getTemperature()));
        SmartDashboard.putNumber("MotorTemperature/Right Main (C)", Math.round(rightMain.getTemperature()));
        SmartDashboard.putNumber("MotorTemperature/Right Follower (C)", Math.round(rightFollower.getTemperature()));
        // Brake Mode
        SmartDashboard.putBoolean("Brake Mode", getNeutralMode() == NeutralMode.Brake);
        // Current Limiting
        SmartDashboard.putBoolean("Current limiting", isCurrentLimitEnabled());
        // Motor current
        SmartDashboard.putNumber("MotorCurrent/Left Main", leftMain.getStatorCurrent());
        SmartDashboard.putNumber("MotorCurrent/Left Follower", leftFollower.getStatorCurrent());
        SmartDashboard.putNumber("MotorCurrent/Right Main", rightMain.getStatorCurrent());
        SmartDashboard.putNumber("MotorCurrent/Right Follower", rightFollower.getStatorCurrent());

        rightMainCurrentLog.append(rightMain.getStatorCurrent());
        leftMainCurrentLog.append(leftMain.getStatorCurrent());
        rightFollowerCurrentLog.append(rightFollower.getStatorCurrent());
        leftFollowerCurrentLog.append(leftFollower.getStatorCurrent());

        leftMainSpeedLog.append(getMetersPerSecond(leftMain.getSelectedSensorVelocity()));
        leftFollowerSpeedLog.append(getMetersPerSecond(leftFollower.getSelectedSensorVelocity()));
        rightMainSpeedLog.append(getMetersPerSecond(rightMain.getSelectedSensorVelocity()));
        rightFollowerSpeedLog.append(getMetersPerSecond(rightFollower.getSelectedSensorVelocity()));
    }

    /**
     * Drives the robot
     * 
     * @param speed Motor speed as a value in [-1.0, 1.0]
     * @param turn  The robot's curvature as a value in [-1.0, 1.0]. Also controls
     *              turn rate for turn-in-place maneuvers
     */
    public void drive(double speed, double turn) {
        drive.curvatureDrive(speed, turn, true);
    }

    /**
     * Set the drive motor's neutral mode
     * A motor's neutral mode determines whether it resists motion - brake mode - or
     * rotates freely - coast mode - when no power is applied to it.
     * 
     * @param neutralMode The motor's neutral mode
     */
    public void setNeutralMode(NeutralMode neutralMode) {
        this.neutralMode = neutralMode;
        leftMain.setNeutralMode(neutralMode);
        leftFollower.setNeutralMode(neutralMode);
        rightMain.setNeutralMode(neutralMode);
        rightFollower.setNeutralMode(neutralMode);
    }

    /**
     * Get the drivetrain's current neutral mode
     * 
     * @return the current neutral mode
     */
    public NeutralMode getNeutralMode() {
        return neutralMode;
    }

    /**
     * Returns whether current limiting is currently enabled
     * @return whether current limiting is applied to drive motors
     */
    public boolean isCurrentLimitEnabled() {
        return currentLimitEnabled;
    }

    /**
     * Set whether current limiting is enabled
     * @param enabled whether to enable current limiting
     */
    public void setCurrentLimitEnabled(boolean enabled) {
        currentLimitEnabled = enabled;
        StatorCurrentLimitConfiguration limiter = new StatorCurrentLimitConfiguration(currentLimitEnabled, 100, 115, 2);
        leftMain.configStatorCurrentLimit(limiter);
        leftFollower.configStatorCurrentLimit(limiter);
        rightMain.configStatorCurrentLimit(limiter);
        rightFollower.configStatorCurrentLimit(limiter);

    }

    /**
     * Stop the subsystem
     */
    public void stop() {
        leftMain.set(0);
        rightMain.set(0);
    }

    private static double getMeters(double sensorReading) {
        final double gearRatio = 8.45; // 8.45:1 gear ratio
        final double encoderCount = 2048; // 2048 encoder counts per revolution
        final double wheelDiameter = 0.1524; // 6-inch wheel diameter in meters
        final double wheelCircumference = (Math.PI * wheelDiameter);
        final double pulsesPerRevolution = (gearRatio * encoderCount);

        return sensorReading / pulsesPerRevolution * wheelCircumference;
    }

    private static double getMetersPerSecond(double sensorReading) {
        return getMeters(sensorReading) * 10;
    }

}
