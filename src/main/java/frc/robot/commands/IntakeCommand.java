package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class IntakeCommand extends SequentialCommandGroup {

    public IntakeCommand() {
        addCommands(
                new LowerIntakeCommand(),

                new ParallelDeadlineGroup(
                        new ForwardConveyorCommand(),
                        new ForwardIntakeRollersCommand()),
                        
                new RaiseIntakeCommand());
    }

}