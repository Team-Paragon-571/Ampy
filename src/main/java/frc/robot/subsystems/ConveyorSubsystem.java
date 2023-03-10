package frc.robot.subsystems;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.hardware.WPI_CANSparkMax;

public class ConveyorSubsystem extends SubsystemBase {

    private WPI_CANSparkMax conveyorMotor;
    private static final int CONVEYOR_MOTOR_CAN_ID = 5; // CAN ID should be 5, it may be different as a result of testing
    private static final int CONVEYOR_SENSOR_CHANNEL = 0;
    private final DigitalInput conveyorFullSensor;

    public ConveyorSubsystem() {
        conveyorFullSensor = new DigitalInput(CONVEYOR_SENSOR_CHANNEL);
        conveyorMotor = new WPI_CANSparkMax(CONVEYOR_MOTOR_CAN_ID, MotorType.kBrushless);
        conveyorMotor.restoreFactoryDefaults();
        addChild("Conveyor motor", conveyorMotor);
        initializeSmartDashboard();
    }

    private void initializeSmartDashboard() {
        SmartDashboard.putBoolean("isConveyorEmpty", isEmpty());
    }

    @Override
    public void periodic() {
        SmartDashboard.putBoolean("isConveyorEmpty", isEmpty());
    }

    /**
     * Set the conveyor's speed
     * @param speed the speed in [-1.0, 1.0]
     */
    public void setSpeed(double speed) {
        conveyorMotor.set(speed);
    }

    public void stop() {
        conveyorMotor.stopMotor();
    }

    public boolean isEmpty() {
        return conveyorFullSensor.get();
    }
}
