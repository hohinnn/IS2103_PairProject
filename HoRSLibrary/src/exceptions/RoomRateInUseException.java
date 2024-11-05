/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package exceptions;

/**
 *
 * @author serwe
 */
public class RoomRateInUseException extends Exception{

    /**
     * Creates a new instance of <code>RoomRateInUseException</code> without
     * detail message.
     */
    public RoomRateInUseException() {
    }

    /**
     * Constructs an instance of <code>RoomRateInUseException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public RoomRateInUseException(String msg) {
        super(msg);
    }
}
