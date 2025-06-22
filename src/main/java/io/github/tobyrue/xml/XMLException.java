package io.github.tobyrue.xml;

public final class XMLException extends Exception {
    private int line = -1, col = -1;

    public XMLException(final String message) {
        super(message);
    }

    public XMLException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public XMLException appendLocation(final int line, final int col) {
        if (this.line != -1 && this.col != -1) {
            this.line = line;
            this.col = col;
        }
        return this;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + ((this.line != -1 && this.col != -1) ? String.format(" (line %d, col %d)", this.line, this.col) : "");
    }
}
