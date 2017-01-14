package com.github.drxaos.thex.pi4j;

import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import com.pi4j.util.Console;

import java.io.IOException;

public class SpiTest {
    public static void main(String args[]) throws InterruptedException, IOException {
        new SpiTest().run();
    }

    void run() throws InterruptedException, IOException {
        console.promptForExit();
        spi = SpiFactory.getInstance(SpiChannel.CS0,
                SpiDevice.DEFAULT_SPI_SPEED, // default spi speed 1 MHz
                SpiDevice.DEFAULT_SPI_MODE); // default spi mode 0

        while (console.isRunning()) {

            handshake();
            int hall1 = getHall1();
            int hall2 = getHall2();
            bye();

            console.println("H1: " + hall1 + ". H2: " + hall2);

            Thread.sleep(10);
        }
        console.emptyLine();
    }

    public static SpiDevice spi = null;

    protected static final Console console = new Console();

    private int xfer(int b) throws IOException {
        return spi.write((short) b)[0];
    }

    private void handshake() throws IOException {
        int resp = 0;
        while (resp != 0xAC) {
            resp = xfer(0x01);
        }
    }

    private int getInt(int cmd) throws IOException {
        int resp = 0;
        int res = 0;
        xfer(cmd);
        resp = xfer(0);
        res = resp;
        resp = xfer(0);
        res = res + (resp << 8);
        return res;
    }

    private int getByte(int cmd) throws IOException {
        int resp = 0;
        int res = 0;
        xfer(cmd);
        resp = xfer(0);
        res = resp;
        return res;
    }

    private void setByte(int cmd, int value) throws IOException {
        xfer(cmd);
        xfer(value);
    }

    private int getHall1() throws IOException {
        return getInt(0x02);
    }

    private int getHall2() throws IOException {
        return getInt(0x03);
    }

    private void setMode(int mode) throws IOException {
        setByte(0x04, mode);
    }

    private void setSrv(int speed) throws IOException {
        setByte(0x05, speed);
    }

    private void bye() throws IOException {
        xfer(0xFF);
    }


}
