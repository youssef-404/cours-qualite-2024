package fr.imt.coffee.machine.exception;

public class LackOfWaterInTankException extends Exception{
    public LackOfWaterInTankException(String message) {
        super(message);
    }
}
