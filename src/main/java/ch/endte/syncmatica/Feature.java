package ch.endte.syncmatica;

public enum Feature {
    CORE, // every feature that's part of 0.1.0 - it doesn't make sense to divide those further since compatability with 0.0 of future versions
    // cannot be maintained and the version is very alpha.
    FEATURE, // the possibility of reporting on ones own features during version exchange
    MODIFY, // commands to modify the placement of a syncmatic placement on the server
    MESSAGE, // ability to send messages to display from server to client
    QUOTA,  // quota on client uploads to the server
    DEBUG,  // ability to configure debugging
    CORE_EX; // extended basic features - such as who owns a placement and subregion sharing

    public static Feature fromString(final String s) {
        for (final Feature f : Feature.values()) {
            if (f.toString().equals(s)) {
                return f;
            }
        }
        return null;
    }
}
