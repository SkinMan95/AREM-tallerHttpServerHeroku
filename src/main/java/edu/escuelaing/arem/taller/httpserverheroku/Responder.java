package edu.escuelaing.arem.taller.httpserverheroku;

import java.io.OutputStream;

/**
 *
 * @author Alejandro Anzola email: alejandro.anzola@mail.escuelaing.edu.co
 */
public interface Responder {

    public void respond(OutputStream out);
}
