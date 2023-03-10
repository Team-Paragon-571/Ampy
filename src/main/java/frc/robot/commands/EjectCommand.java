package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.ConveyorSubsystem;
import frc.robot.subsystems.IntakeArmsSubsystem;
import frc.robot.subsystems.IntakeRollersSubsystem;
import frc.robot.subsystems.IntakeArmsSubsystem.ArmState;

/**
 * This command executes multiple commands in sequence in order to eject an
 * object. It first lowers the intake, then activates the conveyor
 * and intake rollers at the same time with both of them rotating in reverse in
 * order to eject an object. Afterwards, it raises the intake.
 */
public class EjectCommand extends SequentialCommandGroup {

        public EjectCommand(IntakeArmsSubsystem intakeArms, ConveyorSubsystem conveyor,
                        IntakeRollersSubsystem intakeRollers) {
                addCommands(
                                new MoveIntakeCommand(ArmState.LOWERED, intakeArms),
                                new ParallelDeadlineGroup(
                                                new MoveConveyorCommand(-0.3, conveyor),
                                                new MoveIntakeRollersCommand(0.3, intakeRollers)),
                                new MoveIntakeCommand(ArmState.RAISED, intakeArms));
        }

}