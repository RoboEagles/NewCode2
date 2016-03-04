package org.usfirst.frc4579.NewCode2016.commands;

import org.usfirst.frc4579.NewCode2016.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class MoveLifterDown extends Command {
	
	

    public MoveLifterDown() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	
    	requires(Robot.lifter);
    	
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    
    	Robot.lifter.disable();
    
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	
    	Robot.lifter.controlMotors(-0.3);
    	
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	
    	Robot.lifter.enable();
    	
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	
    	end();
    	
    }
}
