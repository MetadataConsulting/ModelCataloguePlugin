package org.modelcatalogue.core.xml

/** Escapes special characters */
class EscapeSpecialWriter extends Writer {

    private final Writer delegate

    EscapeSpecialWriter(Writer delegate) {
        this.delegate = delegate
    }

    @Override
    void write(char[] cbuf, int off, int len) throws IOException {
        delegate.write escapeSpecial(new String(cbuf, off, len))
    }

    @Override
    void flush() throws IOException {
        delegate.flush();
    }

    @Override
    void close() throws IOException {
        delegate.close()
    }

    static String escapeSpecial(String input) {
        input.collect {
            if (it.bytes.size() > 1) {
                return "&#${it.codePointAt(0)};"
            } else {
                return it
            }
        }.join('')
    }
}
