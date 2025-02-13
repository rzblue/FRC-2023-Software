/* Copyright 2023 Pike RoboDevils, FRC Team 1018
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.md file or
 * at https://opensource.org/licenses/MIT. */

package org.pikerobodevils.frc2023;

import static edu.wpi.first.wpilibj.DataLogManager.log;

import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.WPILibVersion;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import io.github.oblarg.oblog.Logger;
import org.pikerobodevils.lib.Util;

public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  private RobotContainer m_robotContainer;

  @Override
  public void robotInit() {
    SmartDashboard.putData(CommandScheduler.getInstance());
    m_robotContainer = new RobotContainer();

    if (isReal()) {
      DataLogManager.start();
    } else {
      DataLogManager.start(
          Filesystem.getOperatingDirectory().toPath().resolve("sim_logs").toString());
    }

    if (isSimulation()) {
      DriverStation.silenceJoystickConnectionWarning(true);
    }

    log("Build debug info:");
    Util.getManifestAttributesForClass(this)
        .forEach(
            (key, value) -> {
              log(key + ": " + value);
            });
    log("Software Versions:");
    log("Java: " + System.getProperty("java.vendor") + " " + System.getProperty("java.version"));
    log("WPILib: " + WPILibVersion.Version);
    log("RevLib: " + CANSparkMax.kAPIVersion);
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
    Logger.updateEntries();
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void autonomousInit() {
    m_robotContainer.drivetrain.setIdleMode(CANSparkMax.IdleMode.kBrake);
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
    m_robotContainer.drivetrain.setIdleMode(CANSparkMax.IdleMode.kCoast);
  }

  @Override
  public void teleopPeriodic() {}

  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() {}

  @Override
  public void simulationInit() {}

  @Override
  public void simulationPeriodic() {
    m_robotContainer.simulationPeriodic();
  }
}
