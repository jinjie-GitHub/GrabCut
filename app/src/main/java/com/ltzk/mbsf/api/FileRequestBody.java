package com.ltzk.mbsf.api;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import java.io.IOException;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public final class FileRequestBody extends RequestBody {

    private Callback callback;
    private RequestBody requestBody;
    private BufferedSink bufferedSink;

    public FileRequestBody(RequestBody requestBody, Callback callback) {
        super();
        this.requestBody = requestBody;
        this.callback = callback;
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        bufferedSink = Okio.buffer(sink(sink));
        requestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            long bytesWritten = 0;
            long contentLength = 0;
            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    contentLength = contentLength();
                }
                bytesWritten += byteCount;
                if (callback != null) {
                    callback.onLoading(contentLength, bytesWritten);
                }
            }
        };
    }

    public interface Callback {
        void onLoading(long total, long progress);
    }
}