package frc.robot.fbling;

import java.io.IOException;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.fbling.F.FMode;

/* TODO Add Shuffleboard support 
TODO add wrapping
TODO add HSV
 */
public class FBling extends SubsystemBase {
    public FShow show;

    public AddressableLED led;
    public AddressableLEDBuffer ledBuffer;
    private int ledLength;

    private int frame = 0;
    private FMode running = FMode.RUNNING;

    private FSegment currentSegment = null;

    //#region Constructors

    public FBling(AddressableLED led, AddressableLEDBuffer buffer) {
        this.led = led;
        this.ledBuffer = buffer;
        this.ledLength = buffer.getLength();
        this.led.setLength(this.ledLength);
        this.led.start();
    }

    public FBling(AddressableLED led, int length) {
        this.led = led;
        this.led.setLength(length);
        this.ledBuffer = new AddressableLEDBuffer(length);
        this.ledLength = length;
        this.led.start();
    }

    public FBling(int port, int length) {
        this.led = new AddressableLED(port);
        this.led.setLength(length);
        this.ledBuffer = new AddressableLEDBuffer(length);
        this.ledLength = length;
        this.led.start();
    }

    public FBling(AddressableLED led, AddressableLEDBuffer buffer, int length) {
        this.led = led;
        this.led.setLength(length);
        this.ledBuffer = buffer;
        this.ledLength = length;
        this.led.start();
    }

    public FBling(AddressableLED led, AddressableLEDBuffer buffer, String showName) {
        this.led = led;
        this.ledBuffer = buffer;
        this.ledLength = buffer.getLength();
        this.led.setLength(this.ledLength);
        this.setShow(showName);
        this.led.start();
    }

    public FBling(AddressableLED led, int length, String showName) {
        this.led = led;
        this.led.setLength(length);
        this.ledBuffer = new AddressableLEDBuffer(length);
        this.ledLength = length;
        this.setShow(showName);
        this.led.start();
    }

    public FBling(int port, int length, String showName) {
        this.led = new AddressableLED(port);
        this.led.setLength(length);
        this.ledBuffer = new AddressableLEDBuffer(length);
        this.ledLength = length;
        this.setShow(showName);
        this.led.start();
    }

    public FBling(AddressableLED led, AddressableLEDBuffer buffer, int length, String showName) {
        this.led = led;
        this.led.setLength(length);
        this.ledBuffer = buffer;
        this.ledLength = length;
        this.setShow(showName);
        this.led.start();
    }

    public FBling(AddressableLED led, AddressableLEDBuffer buffer, FShow show) {
        this.led = led;
        this.ledBuffer = buffer;
        this.ledLength = buffer.getLength();
        this.led.setLength(this.ledLength);
        this.setShow(show);
        this.led.start();
    }

    public FBling(AddressableLED led, int length, FShow show) {
        this.led = led;
        this.led.setLength(length);
        this.ledBuffer = new AddressableLEDBuffer(length);
        this.ledLength = length;
        this.setShow(show);
        this.led.start();
    }

    public FBling(int port, int length, FShow show) {
        this.led = new AddressableLED(port);
        this.led.setLength(length);
        this.ledBuffer = new AddressableLEDBuffer(length);
        this.ledLength = length;
        this.setShow(show);
        this.led.start();
    }

    public FBling(AddressableLED led, AddressableLEDBuffer buffer, int length, FShow show) {
        this.led = led;
        this.led.setLength(length);
        this.ledBuffer = buffer;
        this.ledLength = length;
        this.setShow(show);
        this.led.start();
    }

    //#endregion

    @Override
    public void periodic() {
        if (running == FMode.RUNNING) {
            step();
            currentSegment = getCurrSegment();
        } 
        if (running == FMode.RUNNING || running == FMode.PAUSED) {
            this.led.start();
            updateLed();
        }
        if (running == FMode.STOPPED) {
            blank();
            this.led.stop();
        }
    }

    public void updateLed() {
        if (currentSegment instanceof FFunctionSegment) {
            FFunctionSegment seg = ((FFunctionSegment) currentSegment);
            for (int i = 0; i < ledLength; i++) {
                Color c = ((FFunctionSegment) currentSegment).eval(i, ledLength, (frame-currentSegment.startFrame)/20d, frame);
                ledBuffer.setLED(i, c);
            }
            led.setData(ledBuffer);
        } else if (currentSegment instanceof FGotoSegment) {
            System.out.println(((FGotoSegment) (currentSegment)).gotof);
            setFrame(((FGotoSegment) currentSegment).gotof);
            updateLedCommand().execute();
        }
        System.out.println(frame/20);
    }

    public Command updateLedCommand() {
        return runOnce(() -> {
            updateLed();
        });
    }

    public void step() {
        frame++;
    }

    public Command stepCommand() {
        return runOnce(() -> {
            frame++;
        });
    }

    public void blank() {
        for (int i = 0; i < ledLength; i++) ledBuffer.setLED(i, Color.kBlack);
    }

    public Command blankCommand() {
        return runOnce(() -> {
            blank();
        });
    }

    //#region Show Related Methods
    
    public void setShow(FShow show) {
        this.show = show;
    }

    public void setShow(String showName) {
        try {
            this.show = F.loadShow(showName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FSegment getCurrSegment() {
        int time = frame;
        FSegment lastSeg = null;

        for (FSegment seg : show.segments) {
            if (time <= seg.startFrame) return lastSeg;
            if (seg == show.segments[show.segments.length-1]) {
                return seg;
            } 
            lastSeg = seg;
        }
        return null;
    }

    //#endregion

    //#region Timing and Such
    public void beginShow() {
        frame = 0;
        running = FMode.RUNNING;
    }
    public void stopShow() {
        frame = 0;
        running = FMode.PAUSED;
    }
    public void pauseShow() {
        running = FMode.PAUSED;
    }
    public void resumeShow() {
        running = FMode.RUNNING;
    }

    public FMode getMode() {
        return running;
    }
    public void setMode(FMode mode) {
        running = mode;
    }

    public void goToFrame(int f) {
        this.frame = f;
    }
    public void setFrame(int f) { goToFrame(f); }
    public int getFrame() { return frame; }

    //#endregion

    //#region Micelanoiusns  
    public Color getShowOutput() {
        return show.getColor();
    }

    //#endregion
}
